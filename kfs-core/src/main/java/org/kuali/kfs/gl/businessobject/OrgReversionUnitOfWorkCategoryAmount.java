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
package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.LinkedHashMap;

/**
 * This class represents a organization reversion unit of work category amount
 */
public class OrgReversionUnitOfWorkCategoryAmount extends PersistableBusinessObjectBase {
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String categoryCode;
    private KualiDecimal actual = KualiDecimal.ZERO;
    private KualiDecimal budget = KualiDecimal.ZERO;
    private KualiDecimal encumbrance = KualiDecimal.ZERO;
    private KualiDecimal carryForward = KualiDecimal.ZERO;
    private KualiDecimal available = KualiDecimal.ZERO;

    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private OrganizationReversionCategory organizationReversionCategory;
    private OrgReversionUnitOfWork organizationReversionUnitOfWork;

    public OrgReversionUnitOfWorkCategoryAmount(String cat) {
        this.categoryCode = cat;
    }

    public OrgReversionUnitOfWorkCategoryAmount(String chartOfAccountsCode, String accountNbr, String subAccountNbr, String cat) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNbr;
        this.subAccountNumber = subAccountNbr;
        categoryCode = cat;
    }

    public void addActual(KualiDecimal amount) {
        actual = actual.add(amount);
    }

    public void addBudget(KualiDecimal amount) {
        budget = budget.add(amount);
    }

    public void addEncumbrance(KualiDecimal amount) {
        encumbrance = encumbrance.add(amount);
    }

    public void addCarryForward(KualiDecimal amount) {
        carryForward = carryForward.add(amount);
    }

    public void addAvailable(KualiDecimal amount) {
        available = available.add(amount);
    }

    public KualiDecimal getAvailable() {
        return available;
    }

    public void setAvailable(KualiDecimal available) {
        this.available = available;
    }

    public KualiDecimal getActual() {
        return actual;
    }

    public void setActual(KualiDecimal actual) {
        this.actual = actual;
    }

    public KualiDecimal getBudget() {
        return budget;
    }

    public void setBudget(KualiDecimal budget) {
        this.budget = budget;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public KualiDecimal getEncumbrance() {
        return encumbrance;
    }

    public void setEncumbrance(KualiDecimal encumbrance) {
        this.encumbrance = encumbrance;
    }

    public KualiDecimal getCarryForward() {
        return carryForward;
    }

    public void setCarryForward(KualiDecimal carryForward) {
        this.carryForward = carryForward;
    }

    /**
     * Gets the accountNbr attribute.
     *
     * @return Returns the accountNbr.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNbr attribute value.
     *
     * @param accountNbr The accountNbr to set.
     */
    public void setAccountNumber(String accountNbr) {
        this.accountNumber = accountNbr;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the subAccountNbr attribute.
     *
     * @return Returns the subAccountNbr.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNbr attribute value.
     *
     * @param subAccountNbr The subAccountNbr to set.
     */
    public void setSubAccountNumber(String subAccountNbr) {
        this.subAccountNumber = subAccountNbr;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     *
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organizationReversionCategory attribute.
     *
     * @return Returns the organizationReversionCategory.
     */
    public OrganizationReversionCategory getOrganizationReversionCategory() {
        return organizationReversionCategory;
    }

    /**
     * Sets the organizationReversionCategory attribute value.
     *
     * @param organizationReversionCategory The organizationReversionCategory to set.
     */
    public void setOrganizationReversionCategory(OrganizationReversionCategory organizationReversionCategory) {
        this.organizationReversionCategory = organizationReversionCategory;
    }

    /**
     * Gets the organizationReversionUnitOfWork attribute.
     *
     * @return Returns the organizationReversionUnitOfWork.
     */
    public OrgReversionUnitOfWork getOrganizationReversionUnitOfWork() {
        return organizationReversionUnitOfWork;
    }

    /**
     * Sets the organizationReversionUnitOfWork attribute value.
     *
     * @param organizationReversionUnitOfWork The organizationReversionUnitOfWork to set.
     */
    public void setOrganizationReversionUnitOfWork(OrgReversionUnitOfWork organizationReversionUnitOfWork) {
        this.organizationReversionUnitOfWork = organizationReversionUnitOfWork;
    }

    /**
     * Gets the subAccount attribute.
     *
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     *
     * @param subAccount The subAccount to set.
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pkMap = new LinkedHashMap();
        pkMap.put("chartOfAccountsCode", this.chartOfAccountsCode);
        pkMap.put("accountNbr", this.accountNumber);
        pkMap.put("subAccountNbr", this.subAccountNumber);
        pkMap.put("categoryCode", this.categoryCode);
        return pkMap;
    }

}
