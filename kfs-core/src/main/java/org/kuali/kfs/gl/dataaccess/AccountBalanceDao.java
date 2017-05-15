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
package org.kuali.kfs.gl.dataaccess;

import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An interface that declares methods needed for AccountBalances to interact with the database
 */
public interface AccountBalanceDao {
    /**
     * Given a transaction, finds a matching account balance in the database
     *
     * @param t a transaction to find an appropriate related account balance for
     * @return an appropriate account balance
     */
    public AccountBalance getByTransaction(Transaction t);

    /**
     * This method finds the available account balances according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @return the summary records of account balance entries
     */
    public Iterator findConsolidatedAvailableAccountBalance(Map fieldValues);

    /**
     * This method finds the available account balances according to input fields and values
     *
     * @param fieldValues the input fields and values
     * @return account balance entries
     */
    public Iterator findAvailableAccountBalance(Map fieldValues);

    /**
     * Get available balances by consolidation for specific object types
     *
     * @param objectTypes          the object types that reported account balances must have
     * @param universityFiscalYear the university fiscal year of account balances to find
     * @param chartOfAccountsCode  the chart of accounts of account balances to find
     * @param accountNumber        the account number of account balances to find
     * @param isExcludeCostShare   whether cost share entries should be excluded from this inquiry
     * @param isConsolidated       whether the results of this should be consolidated or not
     * @param pendingEntriesCode   whether to include no pending entries, approved pending entries, or all pending entries
     * @param options              system options
     * @param today                current university date
     * @return a List of Maps with the appropriate query results
     */
    public List findAccountBalanceByConsolidationByObjectTypes(String[] objectTypes, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, boolean isExcludeCostShare, boolean isConsolidated, int pendingEntriesCode, SystemOptions options, UniversityDate today);

    /**
     * Get available balances by level
     *
     * @param universityFiscalYear             the university fiscal year of account balances to find
     * @param chartOfAccountsCode              the chart of accounts of account balances to find
     * @param accountNumber                    the account number of account balances to find
     * @param financialConsolidationObjectCode the consolidation code of account balances to find
     * @param isCostShareExcluded              whether cost share entries should be excluded from this inquiry
     * @param isConsolidated                   whether the results of this should be consolidated or not
     * @param pendingEntryCode
     * @param today                            the current university date
     * @return a List of Mapswith the appropriate query results
     */
    public List findAccountBalanceByLevel(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialConsolidationObjectCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode, UniversityDate today, SystemOptions options);

    /**
     * Get available balances by object
     *
     * @param universityFiscalYear       the university fiscal year of account balances to find
     * @param chartOfAccountsCode        the chart of accounts of account balances to find
     * @param accountNumber              the account number of account balances to find
     * @param financialObjectLevelCode   the object level code of account balances to find
     * @param financialReportingSortCode
     * @param isCostShareExcluded        whether cost share entries should be excluded from this inquiry
     * @param isConsolidated             whether the results of this should be consolidated or not
     * @param pendingEntryCode
     * @param today                      the current university date
     * @return a List of Maps with the appropriate query results
     */
    public List findAccountBalanceByObject(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectLevelCode, String financialReportingSortCode, boolean isCostShareExcluded, boolean isConsolidated, int pendingEntryCode, UniversityDate today, SystemOptions options);

    /**
     * Purge an entire fiscal year for a single chart.
     *
     * @param chartOfAccountsCode the chart of accounts code of account balances to purge
     * @param year                the fiscal year of account balances to purge
     */
    public void purgeYearByChart(String chartOfAccountscode, int year);

    /**
     * @param year the given university fiscal year
     * @return count of rows for the given fiscal year
     */
    public Integer findCountGreaterOrEqualThan(Integer year);
}
