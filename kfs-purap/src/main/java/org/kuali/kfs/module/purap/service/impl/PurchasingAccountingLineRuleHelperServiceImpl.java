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
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;

public class PurchasingAccountingLineRuleHelperServiceImpl extends PurapAccountingLineRuleHelperServiceImpl {

    /**
     * @see org.kuali.kfs.module.purap.service.impl.PurapAccountingLineRuleHelperServiceImpl#hasRequiredOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     * override the default implementation and throw our own error message for accounts that are expired.
     */
    @Override
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
        boolean retVal = true;
        Account account = line.getAccount();
        if (!ObjectUtils.isNull(account) && account.isExpired()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, account.getAccountNumber());
            retVal = false;
        }
        return retVal;
    }

    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        Integer universityFiscalYear = getDocument().getPostingYearNextOrCurrent();
        ObjectCode objectCodeForValidation = (SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(universityFiscalYear, objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode()));

        // check active status
        if (objectCodeForValidation == null || !objectCodeForValidation.isFinancialObjectActiveCode()) {
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

        Integer universityFiscalYear = getDocument().getPostingYearNextOrCurrent();
        SubObjectCode subObjectCodeForValidation = (SpringContext.getBean(SubObjectCodeService.class).getByPrimaryId(universityFiscalYear, subObjectCode.getChartOfAccountsCode(), subObjectCode.getAccountNumber(), subObjectCode.getFinancialObjectCode(), subObjectCode.getFinancialSubObjectCode()));


        // check active flag
        if (!subObjectCodeForValidation.isActive()) {
            GlobalVariables.getMessageMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }
        return true;
    }

}
