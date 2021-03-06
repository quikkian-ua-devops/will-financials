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
package org.kuali.kfs.sys.fixture;

import org.kuali.kfs.coa.businessobject.A21IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.SubAccount;

import java.math.BigDecimal;

public enum SubAccountFixture {
    ACTIVE_SUB_ACCOUNT(null, null, null, null, true, null, null, null), INACTIVE_SUB_ACCOUNT(null, null, null, null, false, null, null, null), VALID_SUB_ACCOUNT("BA", "6044900", "ARREC"), INVALID_SUB_ACCOUNT("ZZ", "0000000", "A"), SUB_ACCOUNT_WITH_REPORTS_TO_ORGANIZATION("EA", "2367527", "SOCHR", null, true, "EA", "SACT", null), // another
    // sub
    // acccount
    // value
    // is
    // SOCON
    SUB_ACCOUNT_WITHOUT_REPORTS_TO_ORGANIZATION("BL", "1031400", "BLDG"), SUB_ACCOUNT_WITH_BAD_CG_FUND_GROUP("BL", "2220090", "12345", "A New SubAccount", true, null, null, null), A21_SUB_ACCOUNT_WITH_BAD_CG_ACCOUNT_TYPE("BL", "4831497", "12345", "A New SubAccount", true, null, null, null, "ZZ", null, null, null, null, false, null, null, null),;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String subAccountName;
    private boolean active;
    private String financialReportChartCode;
    private String finReportOrganizationCode;
    private String financialReportingCode;

    private A21SubAccount a21;

    /**
     * Constructs a SubAccountFixture.java.
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     */
    private SubAccountFixture(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Constructs a SubAccountFixture.java in order to create a new Sub Account that includes an A21 Sub Account
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param subAccountName
     * @param active
     * @param finReportChartCode
     * @param finReportOrgCode
     * @param finReportingCode
     */
    private SubAccountFixture(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String subAccountName, boolean active, String finReportChartCode, String finReportOrgCode, String finReportingCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
        this.subAccountName = subAccountName;
        this.active = active;
        this.financialReportChartCode = finReportChartCode;
        this.finReportOrganizationCode = finReportOrgCode;
        this.financialReportingCode = finReportingCode;
    }

    /**
     * Constructs a SubAccountFixture.java in order to create a new Sub Account that includes an A21 Sub Account
     *
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param subAccountName
     * @param active
     * @param finReportChartCode
     * @param finReportOrgCode
     * @param finReportingCode
     * @param subAccountTypeCode
     * @param icrTypeCode
     * @param finSeriesId
     * @param icrChartCode
     * @param icrAccountNumber
     * @param offCampusCode
     * @param costShareChartCode
     * @param costShareSourceAccountNumber
     * @param costShareSubAccountNumber
     */
    private SubAccountFixture(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String subAccountName, boolean active, String finReportChartCode, String finReportOrgCode, String finReportingCode, String subAccountTypeCode, String icrTypeCode, String finSeriesId, String icrChartCode, String icrAccountNumber, boolean offCampusCode, String costShareChartCode, String costShareSourceAccountNumber, String costShareSubAccountNumber) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
        this.subAccountName = subAccountName;
        this.active = active;
        this.financialReportChartCode = finReportChartCode;
        this.finReportOrganizationCode = finReportOrgCode;
        this.financialReportingCode = finReportingCode;

        a21 = new A21SubAccount();
        a21.setChartOfAccountsCode(chartOfAccountsCode);
        a21.setAccountNumber(accountNumber);
        a21.setSubAccountTypeCode(subAccountTypeCode);
        a21.setIndirectCostRecoveryTypeCode(icrTypeCode);
        a21.setFinancialIcrSeriesIdentifier(finSeriesId);

        A21IndirectCostRecoveryAccount icr = new A21IndirectCostRecoveryAccount();
        icr.setIndirectCostRecoveryAccountNumber(icrAccountNumber);
        icr.setIndirectCostRecoveryFinCoaCode(icrChartCode);
        icr.setAccountLinePercent(new BigDecimal(100));
        a21.getA21IndirectCostRecoveryAccounts().add(icr);

        a21.setOffCampusCode(offCampusCode);
        a21.setCostShareChartOfAccountCode(costShareChartCode);
        a21.setCostShareSourceAccountNumber(costShareSourceAccountNumber);
        a21.setCostShareSourceSubAccountNumber(costShareSubAccountNumber);

    }

    public SubAccount createSubAccount() {
        SubAccount subAccount = new SubAccount();
        subAccount.setChartOfAccountsCode(chartOfAccountsCode);
        subAccount.setAccountNumber(accountNumber);
        subAccount.setSubAccountNumber(subAccountNumber);
        subAccount.setSubAccountName(subAccountName);
        subAccount.setActive(active);
        subAccount.setFinancialReportChartCode(financialReportChartCode);
        subAccount.setFinReportOrganizationCode(finReportOrganizationCode);
        subAccount.setFinancialReportingCode(financialReportingCode);
        subAccount.setA21SubAccount(a21);
        return subAccount;
    }

}
