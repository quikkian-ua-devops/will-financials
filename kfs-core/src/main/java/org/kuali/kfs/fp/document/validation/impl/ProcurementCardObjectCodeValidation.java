/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;

import java.util.Iterator;
import java.util.List;

/**
 * Validates that an accounting line does not have a capital object object code
 */
public class ProcurementCardObjectCodeValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;

    /**
     * Validates that an accounting line does not have a capital object object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     *
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        ProcurementCardDocument pcDocument = (ProcurementCardDocument) event.getDocument();
        AccountingLine accountingLine = getAccountingLineForValidation();
        if (!accountingLine.isTargetAccountingLine()) return true;

        MessageMap errors = GlobalVariables.getMessageMap();
        String errorKey = KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
        boolean objectCodeAllowed = true;

        /* object code exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* make sure object code is active */
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.putError(errorKey, KFSKeyConstants.ERROR_INACTIVE, "object code");
            objectCodeAllowed = false;
        }

        /* get merchant category code (mcc) restriction from transaction */
        String mccRestriction = "";
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) accountingLine;
        List pcTransactions = pcDocument.getTransactionEntries();
        for (Iterator iter = pcTransactions.iterator(); iter.hasNext(); ) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                mccRestriction = transactionEntry.getProcurementCardVendor().getTransactionMerchantCategoryCode();
            }
        }

        if (StringUtils.isBlank(mccRestriction)) {
            return objectCodeAllowed;
        }

        /* check object code is in permitted list for merchant category code (mcc) */
        if (objectCodeAllowed) {
            ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ProcurementCardDocument.class, ProcurementCardDocumentRuleConstants.VALID_OBJECTS_BY_MCC_CODE_PARM_NM, ProcurementCardDocumentRuleConstants.INVALID_OBJECTS_BY_MCC_CODE_PARM_NM, mccRestriction, accountingLine.getFinancialObjectCode());
            objectCodeAllowed = evaluator.evaluateAndAddError(SourceAccountingLine.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }

        /* check object sub type is in permitted list for merchant category code (mcc) */
        if (objectCodeAllowed) {
            ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ProcurementCardDocument.class, ProcurementCardDocumentRuleConstants.VALID_OBJ_SUB_TYPE_BY_MCC_CODE_PARM_NM, ProcurementCardDocumentRuleConstants.INVALID_OBJ_SUB_TYPE_BY_MCC_CODE_PARM_NM, mccRestriction, accountingLine.getObjectCode().getFinancialObjectSubTypeCode());
            objectCodeAllowed = evaluator.evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectSubTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }
        return objectCodeAllowed;
    }


    /**
     * Gets the accountingLineForValidation attribute.
     *
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     *
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
