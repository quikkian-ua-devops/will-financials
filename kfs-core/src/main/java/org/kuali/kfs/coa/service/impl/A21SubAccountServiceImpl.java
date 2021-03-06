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
package org.kuali.kfs.coa.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.dataaccess.A21SubAccountDao;
import org.kuali.kfs.coa.service.A21SubAccountService;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is the default implementation of the A21SubAccountService
 */

@NonTransactional
public class A21SubAccountServiceImpl implements A21SubAccountService {

    private A21SubAccountDao a21SubAccountDao;
    private AccountService accountService;

    /**
     * @see org.kuali.kfs.coa.service.A21SubAccountService#getByPrimaryKey(java.lang.String, java.lang.String, java.lang.String)
     */
    public A21SubAccount getByPrimaryKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        keys.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
        return (A21SubAccount) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(A21SubAccount.class, keys);
    }

    /**
     * @see org.kuali.kfs.coa.service.A21SubAccountService#buildCgIcrAccount(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String)
     */
    public A21SubAccount buildCgIcrAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String subAccountTypeCode) {
        if (StringUtils.isEmpty(chartOfAccountsCode) || StringUtils.isEmpty(accountNumber) || StringUtils.equals(subAccountTypeCode, KFSConstants.SubAccountType.COST_SHARE)) {
            return null;
        }

        A21SubAccount a21SubAccount = new A21SubAccount();
        a21SubAccount.setSubAccountNumber(subAccountNumber);
        a21SubAccount.setSubAccountTypeCode(subAccountTypeCode);

        this.populateCgIcrAccount(a21SubAccount, chartOfAccountsCode, accountNumber);

        return a21SubAccount;
    }

    /**
     * @see org.kuali.kfs.coa.service.A21SubAccountService#populateCgIcrAccount(org.kuali.kfs.coa.businessobject.A21SubAccount,
     * java.lang.String, java.lang.String)
     */
    public void populateCgIcrAccount(A21SubAccount a21SubAccount, String chartOfAccountsCode, String accountNumber) {
        Account account = accountService.getByPrimaryIdWithCaching(chartOfAccountsCode, accountNumber);

        if (ObjectUtils.isNotNull(account) && ObjectUtils.isNotNull(a21SubAccount) && !StringUtils.equals(a21SubAccount.getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
            a21SubAccount.setChartOfAccountsCode(account.getChartOfAccountsCode());
            a21SubAccount.setAccountNumber(account.getAccountNumber());
            a21SubAccount.setFinancialIcrSeriesIdentifier(StringUtils.defaultString(account.getFinancialIcrSeriesIdentifier()));

            //deactivate old ICR lists
            for (A21IndirectCostRecoveryAccount a21Icr : a21SubAccount.getA21IndirectCostRecoveryAccounts()) {
                a21Icr.setActive(false);
            }
            //add new lists from account
            for (IndirectCostRecoveryAccount icrAccount : account.getActiveIndirectCostRecoveryAccounts()) {
                a21SubAccount.getA21IndirectCostRecoveryAccounts().add(A21IndirectCostRecoveryAccount.copyICRAccount(icrAccount));
            }
            a21SubAccount.setIndirectCostRecoveryTypeCode(account.getAcctIndirectCostRcvyTypeCd());
            a21SubAccount.setOffCampusCode(account.isAccountOffCampusIndicator());
        }
    }

    /**
     * @param subAccountDao The a21SubAccountDao to set.
     */
    public void setA21SubAccountDao(A21SubAccountDao subAccountDao) {
        this.a21SubAccountDao = subAccountDao;
    }

    /**
     * Sets the accountService attribute value.
     *
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
