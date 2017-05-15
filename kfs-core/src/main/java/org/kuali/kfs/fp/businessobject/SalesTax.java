/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.util.LinkedHashMap;

/**
 * This class is used to represent a sales tax business object.
 */
public class SalesTax extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String financialDocumentLineTypeCode;
    private Integer financialDocumentLineNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private KualiDecimal financialDocumentGrossSalesAmount;
    private KualiDecimal financialDocumentTaxableSalesAmount;
    private Date financialDocumentSaleDate;

    private Account account;
    private Chart chartOfAccounts;

    /**
     * Default constructor.
     */
    public SalesTax() {

    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the financialDocumentLineTypeCode attribute.
     *
     * @return Returns the financialDocumentLineTypeCode
     */
    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }

    /**
     * Sets the financialDocumentLineTypeCode attribute.
     *
     * @param financialDocumentLineTypeCode The financialDocumentLineTypeCode to set.
     */
    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }


    /**
     * Gets the financialDocumentLineNumber attribute.
     *
     * @return Returns the financialDocumentLineNumber
     */
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    /**
     * Sets the financialDocumentLineNumber attribute.
     *
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
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
     * Gets the financialDocumentGrossSalesAmount attribute.
     *
     * @return Returns the financialDocumentGrossSalesAmount
     */
    public KualiDecimal getFinancialDocumentGrossSalesAmount() {
        return financialDocumentGrossSalesAmount;
    }

    /**
     * Sets the financialDocumentGrossSalesAmount attribute.
     *
     * @param financialDocumentGrossSalesAmount The financialDocumentGrossSalesAmount to set.
     */
    public void setFinancialDocumentGrossSalesAmount(KualiDecimal financialDocumentGrossSalesAmount) {
        this.financialDocumentGrossSalesAmount = financialDocumentGrossSalesAmount;
    }


    /**
     * Gets the financialDocumentTaxableSalesAmount attribute.
     *
     * @return Returns the financialDocumentTaxableSalesAmount
     */
    public KualiDecimal getFinancialDocumentTaxableSalesAmount() {
        return financialDocumentTaxableSalesAmount;
    }

    /**
     * Sets the financialDocumentTaxableSalesAmount attribute.
     *
     * @param financialDocumentTaxableSalesAmount The financialDocumentTaxableSalesAmount to set.
     */
    public void setFinancialDocumentTaxableSalesAmount(KualiDecimal financialDocumentTaxableSalesAmount) {
        this.financialDocumentTaxableSalesAmount = financialDocumentTaxableSalesAmount;
    }


    /**
     * Gets the financialDocumentSaleDate attribute.
     *
     * @return Returns the financialDocumentSaleDate
     */
    public Date getFinancialDocumentSaleDate() {
        return financialDocumentSaleDate;
    }

    /**
     * Sets the financialDocumentSaleDate attribute.
     *
     * @param financialDocumentSaleDate The financialDocumentSaleDate to set.
     */
    public void setFinancialDocumentSaleDate(Date financialDocumentSaleDate) {
        this.financialDocumentSaleDate = financialDocumentSaleDate;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentLineTypeCode", this.financialDocumentLineTypeCode);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}
