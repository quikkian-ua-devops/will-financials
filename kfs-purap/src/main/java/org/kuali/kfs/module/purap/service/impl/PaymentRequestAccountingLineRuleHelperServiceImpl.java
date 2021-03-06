/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
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
package org.kuali.kfs.module.purap.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

public class PaymentRequestAccountingLineRuleHelperServiceImpl extends PurapAccountingLineRuleHelperServiceImpl {

    /**
     * @see org.kuali.kfs.module.purap.service.impl.AccountsPayableAccountingLineRuleHelperServiceImpl#hasRequiredOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     * For payment request we must throw an error after AP approval for C&G accounts that are expired more than 90 days.
     */
    @Override
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
        boolean hasOverrides = true;

        Account account = SpringContext.getBean(AccountService.class).getByPrimaryId(line.getChartOfAccountsCode(), line.getAccountNumber());
        String docStatus = getDocument().getApplicationDocumentStatus();

        //if account exists
        if (ObjectUtils.isNotNull(account)) {
            //after AP approval
            if (PaymentRequestStatuses.APPDOC_AWAITING_SUB_ACCT_MGR_REVIEW.equals(docStatus) ||
                PaymentRequestStatuses.APPDOC_AWAITING_FISCAL_REVIEW.equals(docStatus) ||
                PaymentRequestStatuses.APPDOC_AWAITING_ORG_REVIEW.equals(docStatus) ||
                PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW.equals(docStatus) ||
                PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED.equals(docStatus) ||
                PaymentRequestStatuses.APPDOC_AUTO_APPROVED.equals(docStatus)) {

                String expirationExtensionDays = SpringContext.getBean(ParameterService.class).getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
                int expirationExtensionDaysInt = 3 * 30; // default to 90 days (approximately 3 months)

                if (ObjectUtils.isNotNull(expirationExtensionDays) && expirationExtensionDays.trim().length() > 0) {

                    expirationExtensionDaysInt = new Integer(expirationExtensionDays).intValue();
                }

                //if account is expired, c&g and past 90 days, add error
                if (account.isExpired() && account.isForContractsAndGrants() && (SpringContext.getBean(DateTimeService.class).dateDiff(account.getAccountExpirationDate(), SpringContext.getBean(DateTimeService.class).getCurrentDate(), true) > expirationExtensionDaysInt)) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED_REPLACE, account.getAccountNumber());
                    hasOverrides = false;
                }
            }
        } else {
            //account not valid, shouldn't happen but just in case
            hasOverrides = false;
        }
        return hasOverrides;
    }


    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        Integer universityFiscalYear = ((PaymentRequestDocument) getDocument()).getPostingYearPriorOrCurrent();
        ObjectCode objectCodeForValidation = (SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(universityFiscalYear, objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode()));

        // check active status
        if (!objectCodeForValidation.isFinancialObjectActiveCode()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }


    public boolean isValidSubObjectCode(SubObjectCode subObjectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getSubObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        Integer universityFiscalYear = ((PaymentRequestDocument) getDocument()).getPostingYearPriorOrCurrent();
        SubObjectCode subObjectCodeForValidation = (SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, subObjectCode.getChartOfAccountsCode(), subObjectCode.getAccountNumber(), subObjectCode.getFinancialObjectCode(), subObjectCode.getFinancialSubObjectCode()));

        // check active flag
        if (!subObjectCodeForValidation.isActive()) {
            if (((PaymentRequestDocument) getDocument()).getApplicationDocumentStatus().equals(PaymentRequestStatuses.APPDOC_AWAITING_FISCAL_REVIEW)) {
                GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
                return false;
            }

        }

        return true;
    }


    /**
     * @see org.kuali.kfs.sys.document.service.impl.AccountingLineRuleHelperServiceImpl#isValidSubAccount(org.kuali.kfs.coa.businessobject.SubAccount, org.kuali.rice.krad.datadictionary.DataDictionary, java.lang.String)
     */
    @Override
    public boolean isValidSubAccount(SubAccount subAccount, DataDictionary dataDictionary, String errorPropertyName, String errorPropertyIdentifyingName) {
        String label = getSubAccountLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subAccount)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        if (!subAccount.isActive()) {
            if (((PaymentRequestDocument) getDocument()).getApplicationDocumentStatus().equals(PaymentRequestStatuses.APPDOC_AWAITING_FISCAL_REVIEW)) {
                GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
                return false;
            }

        }
        return true;
    }

}
