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
package org.kuali.kfs.module.ar.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * This class represents a invoice detail on the customer invoice document.
 */
public class InvoiceDetailAccountObjectCode extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String proposalNumber;
    private String accountNumber;
    private String chartOfAccountsCode;
    private String financialObjectCode;
    private String categoryCode;
    private KualiDecimal currentExpenditures = KualiDecimal.ZERO;
    ;
    private KualiDecimal cumulativeExpenditures = KualiDecimal.ZERO;
    ;
    private KualiDecimal totalBilled = KualiDecimal.ZERO;
    ;

    private Account account;
    private Chart chartOfAccounts;

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber
     */
    public String getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
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
     * @return Returns the chartOfAccountsCode
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
     * Gets the financialObjectCode attribute.
     *
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the categoryCode attribute.
     *
     * @return Returns the categoryCode
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * Sets the categoryCode attribute value.
     *
     * @param categoryCode The categoryCode to set.
     */
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    /**
     * Gets the currentExpenditures attribute.
     *
     * @return Returns the currentExpenditures
     */
    public KualiDecimal getCurrentExpenditures() {
        return currentExpenditures;
    }

    /**
     * Sets the currentExpenditures attribute value.
     *
     * @param currentExpenditures The currentExpenditures to set.
     */
    public void setCurrentExpenditures(KualiDecimal currentExpenditures) {
        this.currentExpenditures = currentExpenditures;
    }

    /**
     * Gets the cumulativeExpenditures attribute.
     *
     * @return Returns the cumulativeExpenditures
     */
    public KualiDecimal getCumulativeExpenditures() {
        return cumulativeExpenditures;
    }

    /**
     * Sets the cumulativeExpenditures attribute value.
     *
     * @param cumulativeExpenditures The cumulativeExpenditures to set.
     */
    public void setCumulativeExpenditures(KualiDecimal cumulativeExpenditures) {
        this.cumulativeExpenditures = cumulativeExpenditures;
    }

    /**
     * Gets the totalBilled attribute.
     *
     * @return Returns the totalBilled
     */
    public KualiDecimal getTotalBilled() {
        return totalBilled;
    }

    /**
     * Sets the totalBilled attribute value.
     *
     * @param totalBilled The totalBilled to set.
     */
    public void setTotalBilled(KualiDecimal totalBilled) {
        this.totalBilled = totalBilled;
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
     * @return Returns the chartOfAccounts
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
     * Negates current expenditures and sets document number to null.
     */
    public void correctInvoiceDetailAccountObjectCodeExpenditureAmount() {
        this.setCurrentExpenditures(getCurrentExpenditures().negated());
        this.setDocumentNumber(null);
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, getDocumentNumber());
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, getProposalNumber());
        m.put(KFSPropertyConstants.ACCOUNT_NUMBER, getAccountNumber());
        m.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, getChartOfAccountsCode());
        m.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, getFinancialObjectCode());
        m.put("categoryCode", getCategoryCode());
        m.put("currentExpenditures", getCurrentExpenditures());
        m.put("cumulativeExpenditures", getCumulativeExpenditures());
        m.put("totalBilled", getTotalBilled());
        return m;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                InvoiceDetailAccountObjectCode other = (InvoiceDetailAccountObjectCode) obj;
                if (StringUtils.equalsIgnoreCase(this.documentNumber, other.documentNumber)) {
                    if (this.proposalNumber.equals(other.proposalNumber)) {
                        if (StringUtils.equalsIgnoreCase(this.accountNumber, other.accountNumber)) {
                            if (StringUtils.equalsIgnoreCase(this.chartOfAccountsCode, other.chartOfAccountsCode)) {
                                if (StringUtils.equalsIgnoreCase(this.financialObjectCode, other.financialObjectCode)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return ObjectUtil.generateHashCode(this, Arrays.asList("documentNumber", "proposalNumber", "accountNumber", "chartOfAccountsCode", "financialObjectCode"));
    }

}
