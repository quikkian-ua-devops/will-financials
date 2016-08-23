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

package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a Wire Charge business object, which is a method of payment to an institution.
 */
public class WireCharge extends PersistableBusinessObjectBase implements FiscalYearBasedBusinessObject {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String incomeFinancialObjectCode;
    private String expenseFinancialObjectCode;
    private KualiDecimal domesticChargeAmt;
    private KualiDecimal foreignChargeAmt;

    private SystemOptions fiscalYear;
    private Chart chartOfAccounts;
    private ObjectCode incomeFinancialObject;
    private ObjectCode expenseFinancialObject;
    private Account account;

    /**
     * Default no-arg constructor.
     */
    public WireCharge() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }


    /**
     * Sets the universityFiscalYear attribute.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the incomeFinancialObjectCode attribute.
     *
     * @return Returns the incomeFinancialObjectCode
     */
    public String getIncomeFinancialObjectCode() {
        return incomeFinancialObjectCode;
    }


    /**
     * Sets the incomeFinancialObjectCode attribute.
     *
     * @param incomeFinancialObjectCode The incomeFinancialObjectCode to set.
     */
    public void setIncomeFinancialObjectCode(String incomeFinancialObjectCode) {
        this.incomeFinancialObjectCode = incomeFinancialObjectCode;
    }

    /**
     * Gets the expenseFinancialObjectCode attribute.
     *
     * @return Returns the expenseFinancialObjectCode
     */
    public String getExpenseFinancialObjectCode() {
        return expenseFinancialObjectCode;
    }


    /**
     * Sets the expenseFinancialObjectCode attribute.
     *
     * @param expenseFinancialObjectCode The expenseFinancialObjectCode to set.
     */
    public void setExpenseFinancialObjectCode(String expenseFinancialObjectCode) {
        this.expenseFinancialObjectCode = expenseFinancialObjectCode;
    }

    /**
     * Gets the domesticChargeAmt attribute.
     *
     * @return Returns the domesticChargeAmt
     */
    public KualiDecimal getDomesticChargeAmt() {
        return domesticChargeAmt;
    }


    /**
     * Sets the domesticChargeAmt attribute.
     *
     * @param domesticChargeAmt The domesticChargeAmt to set.
     */
    public void setDomesticChargeAmt(KualiDecimal domesticChargeAmt) {
        this.domesticChargeAmt = domesticChargeAmt;
    }

    /**
     * Gets the foreignChargeAmt attribute.
     *
     * @return Returns the foreignChargeAmt
     */
    public KualiDecimal getForeignChargeAmt() {
        return foreignChargeAmt;
    }


    /**
     * Sets the foreignChargeAmt attribute.
     *
     * @param foreignChargeAmt The foreignChargeAmt to set.
     */
    public void setForeignChargeAmt(KualiDecimal foreignChargeAmt) {
        this.foreignChargeAmt = foreignChargeAmt;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }


    /**
     * Sets the chartOfAccounts attribute.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the incomeFinancialObject attribute.
     *
     * @return Returns the incomeFinancialObject
     */
    public ObjectCode getIncomeFinancialObject() {
        return incomeFinancialObject;
    }


    /**
     * Sets the incomeFinancialObject attribute.
     *
     * @param incomeFinancialObject The incomeFinancialObject to set.
     * @deprecated
     */
    public void setIncomeFinancialObject(ObjectCode incomeFinancialObject) {
        this.incomeFinancialObject = incomeFinancialObject;
    }

    /**
     * Gets the expenseFinancialObject attribute.
     *
     * @return Returns the expenseFinancialObject
     */
    public ObjectCode getExpenseFinancialObject() {
        return expenseFinancialObject;
    }


    /**
     * Sets the expenseFinancialObject attribute.
     *
     * @param expenseFinancialObject The expenseFinancialObject to set.
     * @deprecated
     */
    public void setExpenseFinancialObject(ObjectCode expenseFinancialObject) {
        this.expenseFinancialObject = expenseFinancialObject;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }


    /**
     * Sets the account attribute.
     *
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return Returns the fiscalYear.
     */
    public SystemOptions getFiscalYear() {
        return fiscalYear;
    }


    /**
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(SystemOptions fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * This method (a hack by any other name...) returns a string so that an wire charge can have a link to view its own
     * inquiry page after a look up
     *
     * @return the String "View Wire Charge"
     */
    public String getWireChargeViewer() {
        return "View Wire Charge";
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("universityFiscalYear", getUniversityFiscalYear());
        return m;
    }
}
