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
package org.kuali.kfs.sys.dataaccess;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This interface defines basic methods that GeneralLedgerPendingEntry Dao's must provide
 */
public interface GeneralLedgerPendingEntryDao {

    /**
     * Get summary of amounts in the pending entry table
     *
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param objectCodes
     * @param balanceTypeCodes
     * @param isDebit
     * @return
     */
    public KualiDecimal getTransactionSummary(Collection universityFiscalYears, String chartOfAccountsCode, String accountNumber, Collection objectCodes, Collection balanceTypeCodes, boolean isDebit);

    /**
     * Get summary of amounts in the pending entry table
     *
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param objectTypeCodes
     * @param balanceTypeCodes
     * @param acctSufficientFundsFinObjCd
     * @param isDebit
     * @param isYearEnd
     * @param transactionDocumentNumbers
     * @return
     */
    public KualiDecimal getTransactionSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, Collection objectTypeCodes, Collection balanceTypeCodes, String acctSufficientFundsFinObjCd, boolean isDebit, boolean isYearEnd, List<String> transactionDocumentNumbers);

    /**
     * Get summary of amounts in the pending entry table
     *
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param objectTypeCodes
     * @param balanceTypeCodes
     * @param acctSufficientFundsFinObjCd
     * @param isYearEnd
     * @return
     */
    public KualiDecimal getTransactionSummary(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, Collection objectTypeCodes, Collection balanceTypeCodes, String acctSufficientFundsFinObjCd, boolean isYearEnd);

    /**
     * Find Pending Entries
     *
     * @param fieldValues
     * @param isApproved
     * @param currentFiscalPeriodCode
     * @param currentFiscalYear
     * @param encumbranceBalanceTypes a list of encumbranceBalanceTypes for the given universityFiscalYear if this is passed in the
     *                                fieldValues or the Current Year Encumbrance Balance Types if universityFiscalYear is not present
     * @return
     */
    public Collection findPendingEntries(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, List<String> encumbranceBalanceTypes);

    /**
     * Delete all pending entries for a given document
     *
     * @param documentHeaderId
     */
    public void delete(String documentHeaderId);

    /**
     * Delete all pending entries based on the document approved code
     *
     * @param financialDocumentApprovedCode
     */
    public void deleteByFinancialDocumentApprovedCode(String financialDocumentApprovedCode);

    /**
     * This method retrieves all approved pending ledger entries
     *
     * @return all approved pending ledger entries
     */
    public Iterator findApprovedPendingLedgerEntries();

    /**
     * This method counts all approved pending ledger entries by account
     *
     * @param account the given account
     * @return count of entries
     */
    public int countPendingLedgerEntries(Account account);

    /**
     * This method retrieves all pending ledger entries for the given encumbrance
     *
     * @param encumbrance the encumbrance entry in the GL_Encumbrance_T table
     * @param isApproved  the flag that indicates whether the pending entries are approved or don't care
     * @return all pending ledger entries of the given encumbrance
     */
    public Iterator findPendingLedgerEntries(Encumbrance encumbrance, boolean isApproved);

    /**
     * This method retrieves all pending ledger entries for the given encumbrance
     *
     * @param balance        the balance entry
     * @param isApproved     the flag that indicates whether the pending entries are approved or don't care
     * @param isConsolidated consolidation option is applied or not
     * @return all pending ledger entries of the given balance
     */
    public Iterator findPendingLedgerEntries(Balance balance, boolean isApproved, boolean isConsolidated);

    /**
     * This method retrieves all pending ledger entries matching the given entry criteria
     *
     * @param isApproved              the flag that indicates whether the pending entries are approved or don't care
     * @param fieldValues             the input fields and values
     * @param currentFiscalPeriodCode current fiscal year period code
     * @param currentFY               current fiscal year
     * @param encumbranceBalanceTypes a list of encumbranceBalanceTypes
     * @return all pending ledger entries matching the given balance criteria
     */
    public Iterator findPendingLedgerEntriesForEntry(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFY, List<String> encumbranceBalanceTypes);

    /**
     * This method retrieves all pending ledger entries matching the given balance criteria
     *
     * @param isApproved              the flag that indicates whether the pending entries are approved or don't care
     * @param fieldValues             the input fields and values
     * @param currentFiscalPeriodCode current fiscal year period code
     * @param currentFY               current fiscal year
     * @param encumbranceBalanceTypes a list of encumbranceBalanceTypes
     * @return all pending ledger entries matching the given balance criteria
     */
    public Iterator findPendingLedgerEntriesForBalance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFY, List<String> encumbranceBalanceTypes);

    /**
     * This method retrieves all pending ledger entries matching the given cash balance criteria
     *
     * @param isApproved              the flag that indicates whether the pending entries are approved or don't care
     * @param fieldValues             the input fields and values
     * @param currentFiscalPeriodCode current fiscal year period code
     * @param currentFY               current fiscal year
     * @param encumbranceBalanceTypes a list of encumbranceBalanceTypes for the given universityFiscalYear if this is passed in the
     *                                fieldValues or the Current Year Encumbrance Balance Types if universityFiscalYear is not present
     * @return all pending ledger entries matching the given cash balance criteria
     */
    public Iterator findPendingLedgerEntriesForCashBalance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFY, List<String> encumbranceBalanceTypes);

    /**
     * This method retrieves all pending ledger entries that may belong to encumbrance table in the future
     *
     * @param isApproved              the flag that indicates whether the pending entries are approved or don't care
     * @param fieldValues             the input fields and values
     * @param currentFiscalPeriodCode current fiscal year period code
     * @param currentFY               current fiscal year
     * @param currentYearOptions      the current year system options
     * @param encumbranceBalanceTypes a list of encumbranceBalanceTypes for the given universityFiscalYear if this is passed in the
     *                                fieldValues or the Current Year Encumbrance Balance Types if universityFiscalYear is not present
     * @return all pending ledger entries that may belong to encumbrance table
     */
    public Iterator findPendingLedgerEntriesForEncumbrance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, SystemOptions currentYearOptions, List<String> encumbranceBalanceTypes);

    /**
     * This method retrieves all pending ledger entries that may belong to the given account balance record in the future
     *
     * @param fieldValues             the input fields and values
     * @param isApproved              the flag that indicates whether the pending entries are approved or don't care
     * @param currentFiscalPeriodCode current fiscal year period code
     * @param currentFY               current fiscal year
     * @param encumbranceBalanceTypes a list of encumbranceBalanceTypes for the given universityFiscalYear if this is passed in the
     *                                fieldValues or the Current Year Encumbrance Balance Types if universityFiscalYear is not present
     * @return all pending ledger entries that may belong to encumbrance table
     */
    public Iterator findPendingLedgerEntrySummaryForAccountBalance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, List<String> encumbranceBalanceTypes);

    /**
     * This method retrieves all pending ledger entries that may belong to the given account balance record in the future
     *
     * @param fieldValues             the input fields and values
     * @param isApproved              the flag that indicates whether the pending entries are approved or don't care
     * @param currentFiscalPeriodCode current fiscal year period code
     * @param currentFY               current fiscal year
     * @param encumbranceBalanceTypes a list of encumbranceBalanceTypes for the given universityFiscalYear if this is passed in the
     *                                fieldValues or the Current Year Encumbrance Balance Types if universityFiscalYear is not present
     * @return all pending ledger entries that may belong to encumbrance table
     */
    public Iterator findPendingLedgerEntriesForAccountBalance(Map fieldValues, boolean isApproved, String currentFiscalPeriodCode, int currentFiscalYear, List<String> encumbranceBalanceTypes);

}
