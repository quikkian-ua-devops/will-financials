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
package org.kuali.kfs.sys.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.io.Serializable;

/**
 * Represents a sufficient fund item which is used to show if a document has sufficient funds
 */
public class SufficientFundsItem implements Serializable, Comparable {
    private SystemOptions year;
    private Account account;
    private ObjectCode financialObject;
    private ObjectType financialObjectType;
    private String sufficientFundsObjectCode;
    private KualiDecimal amount;
    private String documentTypeCode;
    private BalanceType balanceTyp;

    public BalanceType getBalanceTyp() {
        return balanceTyp;
    }

    public void setBalanceTyp(BalanceType balanceTyp) {
        this.balanceTyp = balanceTyp;
    }

    public SufficientFundsItem() {
        amount = KualiDecimal.ZERO;
    }

    /**
     * Constructs a SufficientFundsItem.java.
     *
     * @param universityFiscalYear
     * @param tran
     * @param sufficientFundsObjectCode
     */
    public SufficientFundsItem(SystemOptions universityFiscalYear, Transaction tran, String sufficientFundsObjectCode) {

        amount = KualiDecimal.ZERO;
        year = universityFiscalYear;
        account = tran.getAccount();
        financialObject = tran.getFinancialObject();
        financialObjectType = tran.getObjectType();
        this.sufficientFundsObjectCode = sufficientFundsObjectCode;
        this.balanceTyp = tran.getBalanceType();

        add(tran);
    }

    /**
     * Constructs a SufficientFundsItem.java.
     *
     * @param universityFiscalYear
     * @param accountLine
     * @param sufficientFundsObjectCode
     */
    public SufficientFundsItem(SystemOptions universityFiscalYear, AccountingLine accountLine, String sufficientFundsObjectCode) {

        amount = KualiDecimal.ZERO;
        year = universityFiscalYear;
        account = accountLine.getAccount();
        financialObject = accountLine.getObjectCode();
        financialObjectType = accountLine.getObjectType();
        this.sufficientFundsObjectCode = sufficientFundsObjectCode;
        this.balanceTyp = accountLine.getBalanceTyp();

        add(accountLine);
    }

    /**
     * Adds an accounting line's amount to this sufficient funds item
     *
     * @param a accounting line
     */
    public void add(AccountingLine a) {
        if (a.getObjectType().getFinObjectTypeDebitcreditCd().equals(a.getDebitCreditCode()) || KFSConstants.EMPTY_STRING.equals(a.getDebitCreditCode())) {
            amount = amount.add(a.getAmount());
        } else {
            amount = amount.subtract(a.getAmount());
        }
    }

    /**
     * Adds a transactions amount to this sufficient funds item
     *
     * @param t transactions
     */
    public void add(Transaction t) {
        if (t.getObjectType().getFinObjectTypeDebitcreditCd().equals(t.getTransactionDebitCreditCode()) || KFSConstants.EMPTY_STRING.equals(t.getTransactionDebitCreditCode())) {
            amount = amount.add(t.getTransactionLedgerEntryAmount());
        } else {
            amount = amount.subtract(t.getTransactionLedgerEntryAmount());
        }
    }

    /**
     * Compare to other sufficient funds item based on key
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        SufficientFundsItem item = (SufficientFundsItem) arg0;
        return getKey().compareTo(item.getKey());
    }

    /**
     * Returns string to uniquely represent this sufficient funds item
     *
     * @return string with the following concatenated: fiscal year, chart of accounts code, account number, financial object type code, sufficient funds object code and balance type code
     */
    public String getKey() {
        return year.getUniversityFiscalYear() + account.getChartOfAccountsCode() + account.getAccountNumber() + financialObjectType.getCode() + sufficientFundsObjectCode + balanceTyp.getCode();
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    public String getAccountSufficientFundsCode() {
        return account.getAccountSufficientFundsCode();
    }

    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    @Override
    public String toString() {
        return year.getUniversityFiscalYear() + "-" + account.getChartOfAccountsCode() + "-" + account.getAccountNumber() + "-" + financialObject.getFinancialObjectCode() + "-" + account.getAccountSufficientFundsCode() + "-" + sufficientFundsObjectCode + "-" + amount.toString();
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public KualiDecimal getAmount() {
        return amount;
    }

    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }

    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    public String getSufficientFundsObjectCode() {
        return sufficientFundsObjectCode;
    }

    public void setSufficientFundsObjectCode(String sufficientFundsObjectCode) {
        this.sufficientFundsObjectCode = sufficientFundsObjectCode;
    }

    public SystemOptions getYear() {
        return year;
    }

    public void setYear(SystemOptions year) {
        this.year = year;
    }


}
