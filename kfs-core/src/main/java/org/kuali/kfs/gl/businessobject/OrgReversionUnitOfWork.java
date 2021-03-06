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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a unit of work for the organization reversion
 */
public class OrgReversionUnitOfWork extends PersistableBusinessObjectBase {
    public String chartOfAccountsCode = "";
    public String accountNumber = "";
    public String subAccountNumber = "";
    public Map<String, OrgReversionUnitOfWorkCategoryAmount> amounts;
    private KualiDecimal totalReversion;
    private KualiDecimal totalCarryForward;
    private KualiDecimal totalAvailable;
    private KualiDecimal totalCash;

    public OrgReversionUnitOfWork() {
        amounts = new HashMap<String, OrgReversionUnitOfWorkCategoryAmount>();
    }

    public OrgReversionUnitOfWork(String chart, String acct, String subAcct) {
        this();
        chartOfAccountsCode = chart;
        accountNumber = acct;
        subAccountNumber = subAcct;
    }

    /**
     * Returns true if COA code and account number are not blank.
     *
     * @return true if COA code and account number are not blank.
     */
    public boolean isInitialized() {
        return !StringUtils.isBlank(chartOfAccountsCode) && !StringUtils.isBlank(accountNumber);
    }

    public void setFields(String chart, String acct, String subAcct) {
        chartOfAccountsCode = chart;
        accountNumber = acct;
        subAccountNumber = subAcct;
        cascadeCategoryAmountKeys();
        clearAmounts();
    }

    /**
     * Set category amounts
     *
     * @param cats list of organization reversion categories
     */
    public void setCategories(List<OrganizationReversionCategory> cats) {
        for (OrganizationReversionCategory element : cats) {
            OrgReversionUnitOfWorkCategoryAmount ca = new OrgReversionUnitOfWorkCategoryAmount(element.getOrganizationReversionCategoryCode());
            amounts.put(element.getOrganizationReversionCategoryCode(), ca);
        }
    }

    /**
     * This method adds to the actual amount for a specific category code
     *
     * @param categoryCode category code
     * @param amount       amount
     */
    public void addActualAmount(String categoryCode, KualiDecimal amount) {
        OrgReversionUnitOfWorkCategoryAmount ca = amounts.get(categoryCode);
        ca.setActual(ca.getActual().add(amount));
    }

    /**
     * This method adds to the budget amount for a specific category code
     *
     * @param categoryCode category code
     * @param amount       amount
     */
    public void addBudgetAmount(String categoryCode, KualiDecimal amount) {
        OrgReversionUnitOfWorkCategoryAmount ca = amounts.get(categoryCode);
        ca.setBudget(ca.getBudget().add(amount));
    }

    /**
     * This method adds to the encumbrance amount for a specific category code
     *
     * @param categoryCode category code
     * @param amount       amount
     */
    public void addEncumbranceAmount(String categoryCode, KualiDecimal amount) {
        OrgReversionUnitOfWorkCategoryAmount ca = amounts.get(categoryCode);
        ca.setEncumbrance(ca.getEncumbrance().add(amount));
    }

    /**
     * This method adds to the carry forward amount for a specific category code
     *
     * @param categoryCode category code
     * @param amount       amount
     */
    public void addCarryForwardAmount(String categoryCode, KualiDecimal amount) {
        OrgReversionUnitOfWorkCategoryAmount ca = amounts.get(categoryCode);
        ca.setCarryForward(ca.getCarryForward().add(amount));
    }

    /**
     * This method clears all amounts for this unit of work
     */
    public void clearAmounts() {
        totalAvailable = KualiDecimal.ZERO;
        totalCarryForward = KualiDecimal.ZERO;
        totalCash = KualiDecimal.ZERO;
        totalReversion = KualiDecimal.ZERO;

        for (Iterator<OrgReversionUnitOfWorkCategoryAmount> iter = amounts.values().iterator(); iter.hasNext(); ) {
            OrgReversionUnitOfWorkCategoryAmount element = iter.next();
            element.setActual(KualiDecimal.ZERO);
            element.setBudget(KualiDecimal.ZERO);
            element.setEncumbrance(KualiDecimal.ZERO);
        }
    }

    /**
     * This method updates the category amount keys for the current unit of work
     */
    public void cascadeCategoryAmountKeys() {
        for (String category : amounts.keySet()) {
            OrgReversionUnitOfWorkCategoryAmount catAmt = amounts.get(category);
            catAmt.setChartOfAccountsCode(this.chartOfAccountsCode);
            catAmt.setAccountNumber(this.accountNumber);
            catAmt.setSubAccountNumber(this.subAccountNumber);
        }
    }

    /**
     * This method returns true if this unit of work's chart of accounts code, account number, and sub account number match the passed in parameter values
     *
     * @param chart
     * @param acct
     * @param subAcct
     * @return true if this unit of work's chart of accounts code, account number, and sub account number match the passed in parameter values
     */
    public boolean isSame(String chart, String acct, String subAcct) {
        return (chartOfAccountsCode.equals(chart) && accountNumber.equals(acct) && subAccountNumber.equals(subAcct));
    }

    /**
     * Return true of this unit of work has the same chart of accounts code, account number, and sub account number as the passed in balance
     *
     * @param balance
     * @return
     */
    public boolean wouldHold(Balance balance) {
        return StringUtils.equals(chartOfAccountsCode, balance.getChartOfAccountsCode()) && StringUtils.equals(accountNumber, balance.getAccountNumber()) && StringUtils.equals(subAccountNumber, balance.getSubAccountNumber());
    }

    public KualiDecimal getTotalAccountAvailable() {
        KualiDecimal amount = KualiDecimal.ZERO;
        for (Iterator<OrgReversionUnitOfWorkCategoryAmount> iter = amounts.values().iterator(); iter.hasNext(); ) {
            OrgReversionUnitOfWorkCategoryAmount element = iter.next();
            amount = amount.add(element.getAvailable());
        }
        return amount;
    }

    public KualiDecimal getTotalCarryForward() {
        return totalCarryForward;
    }

    public void setTotalCarryForward(KualiDecimal totalCarryForward) {
        this.totalCarryForward = totalCarryForward;
    }

    public KualiDecimal getTotalReversion() {
        return totalReversion;
    }

    public void addTotalCarryForward(KualiDecimal amount) {
        totalCarryForward = totalCarryForward.add(amount);
    }

    public void setTotalReversion(KualiDecimal totalReversion) {
        this.totalReversion = totalReversion;
    }

    public void addTotalReversion(KualiDecimal amount) {
        totalReversion = totalReversion.add(amount);
    }

    public KualiDecimal getTotalAvailable() {
        return totalAvailable;
    }

    public void addTotalAvailable(KualiDecimal amount) {
        totalAvailable = totalAvailable.add(amount);
    }

    public void setTotalAvailable(KualiDecimal totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public void addTotalCash(KualiDecimal amount) {
        totalCash = totalCash.add(amount);
    }

    public KualiDecimal getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(KualiDecimal totalCash) {
        this.totalCash = totalCash;
    }

    public Map<String, OrgReversionUnitOfWorkCategoryAmount> getCategoryAmounts() {
        return amounts;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    public LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pkMap = new LinkedHashMap();
        pkMap.put("chartOfAccountsCode", this.chartOfAccountsCode);
        pkMap.put("accountNbr", this.accountNumber);
        pkMap.put("subAccountNbr", this.subAccountNumber);
        return pkMap;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
     * Gets the subAccountNumber attribute.
     *
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     *
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

}
