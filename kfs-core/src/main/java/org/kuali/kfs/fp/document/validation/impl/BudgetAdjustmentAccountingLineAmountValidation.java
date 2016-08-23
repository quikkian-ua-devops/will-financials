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

import org.kuali.kfs.fp.businessobject.BudgetAdjustmentAccountingLine;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Validation that checks the amounts on budget adjustment document accounting lines.
 */
public class BudgetAdjustmentAccountingLineAmountValidation extends GenericValidation {
    private BudgetAdjustmentAccountingLine accountingLineForValidation;
    private AccountingDocument accountingDocumentForValidation;
    private DebitDeterminerService debitDeterminerService;

    /**
     * Validates the amounts on a budget adjustment accounting line, making sure that either the current adjustment amount or the base adjustment amount are not zero,
     * and that all given amounts are positive.
     *
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean amountValid = true;

        // check amounts both current and base amounts are not zero
        if (getAccountingLineForValidation().getCurrentBudgetAdjustmentAmount().isZero() && getAccountingLineForValidation().getBaseBudgetAdjustmentAmount().isZero()) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, KFSKeyConstants.ERROR_BA_AMOUNT_ZERO);
            amountValid = false;
        }

        // if not an error correction, all amounts must be positive
        if (!debitDeterminerService.isErrorCorrection(getAccountingDocumentForValidation())) {
            amountValid &= checkAmountSign(getAccountingLineForValidation().getCurrentBudgetAdjustmentAmount(), KFSPropertyConstants.CURRENT_BUDGET_ADJUSTMENT_AMOUNT, "Current");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getBaseBudgetAdjustmentAmount().kualiDecimalValue(), KFSPropertyConstants.BASE_BUDGET_ADJUSTMENT_AMOUNT, "Base");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth1LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_1_LINE_AMOUNT, "Month 1");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth2LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_2_LINE_AMOUNT, "Month 2");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth3LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_3_LINE_AMOUNT, "Month 3");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth4LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_4_LINE_AMOUNT, "Month 4");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth5LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_5_LINE_AMOUNT, "Month 5");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth6LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_6_LINE_AMOUNT, "Month 6");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth7LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_7_LINE_AMOUNT, "Month 7");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth8LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_8_LINE_AMOUNT, "Month 8");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth8LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_9_LINE_AMOUNT, "Month 9");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth10LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_10_LINE_AMOUNT, "Month 10");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth10LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_11_LINE_AMOUNT, "Month 11");
            amountValid &= checkAmountSign(getAccountingLineForValidation().getFinancialDocumentMonth12LineAmount(), KFSPropertyConstants.FINANCIAL_DOCUMENT_MONTH_12_LINE_AMOUNT, "Month 12");
        }

        return amountValid;
    }

    /**
     * Helper method to check if an amount is negative and add an error if not.
     *
     * @param amount       to check
     * @param propertyName to add error under
     * @param label        for error
     * @return boolean indicating if the value has the requested sign
     */
    protected boolean checkAmountSign(KualiDecimal amount, String propertyName, String label) {
        boolean correctSign = true;

        if (amount.isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyName, KFSKeyConstants.ERROR_BA_AMOUNT_NEGATIVE, label);
            correctSign = false;
        }

        return correctSign;
    }


    /**
     * Gets the accountingLineForValidation attribute.
     *
     * @return Returns the accountingLineForValidation.
     */
    public BudgetAdjustmentAccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     *
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(BudgetAdjustmentAccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     *
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     *
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the debitDeterminerService attribute.
     *
     * @return Returns the debitDeterminerService.
     */
    public DebitDeterminerService getDebitDeterminerService() {
        return debitDeterminerService;
    }

    /**
     * Sets the debitDeterminerService attribute value.
     *
     * @param debitDeterminerService The debitDeterminerService to set.
     */
    public void setDebitDeterminerService(DebitDeterminerService debitDeterminerService) {
        this.debitDeterminerService = debitDeterminerService;
    }
}
