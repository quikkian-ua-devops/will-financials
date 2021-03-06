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
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.gl.batch.service.impl.exception.FatalErrorException;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.service.ReportWriterService;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface OrganizationReversionProcess {
    /**
     * Runs the organization reversion process.
     *
     * @param jobParameters               the parameters used in the process
     * @param organizationReversionCounts a Map of named statistics generated by running the process
     */
    public abstract void organizationReversionProcess(Map jobParameters, Map<String, Integer> organizationReversionCounts);

    /**
     * This method initializes several properties needed for the process to run correctly
     */
    public abstract void initializeProcess();

    /**
     * Given a list of balances, this method generates the origin entries for the organization reversion/carry forward process, and saves those
     * to an initialized origin entry group
     *
     * @param balances an iterator of balances to process; each balance returned by the iterator will be processed by this method
     */
    public abstract void processBalances(Iterator<Balance> balances);

    /**
     * This method determines which origin entries (reversion, cash reversion, or carry forward) need to be generated for the current unit of work,
     * and then delegates to the origin entry generation methods to create those entries
     *
     * @return a list of OriginEntries which need to be written
     * @throws FatalErrorException thrown if object codes are missing in any of the generation methods
     */
    public abstract List<OriginEntryFull> generateOutputOriginEntries() throws FatalErrorException;

    /**
     * This method generates cash reversion origin entries for the current organization reversion, and adds them to the given list
     *
     * @param originEntriesToWrite a list of OriginEntryFulls to stick generated origin entries into
     * @throws FatalErrorException thrown if an origin entry's object code can't be found
     */
    public abstract void generateCashReversions(List<OriginEntryFull> originEntriesToWrite) throws FatalErrorException;

    /**
     * Generates carry forward origin entries on a category by category basis (if the organization reversion record asks for that), assuming carry
     * forwards are required for the current unit of work
     *
     * @param originEntriesToWrite a list of origin entries to write, which any generated origin entries should be added to
     * @throws FatalErrorException thrown if an object code cannot be found
     */
    public abstract void generateMany(List<OriginEntryFull> originEntriesToWrite) throws FatalErrorException;

    /**
     * If carry forwards need to be generated for this unit of work, this method will generate the origin entries to accomplish those object codes.
     * Note: this will only be called if the organization reversion record tells the process to munge all carry forwards for all categories
     * together; if the organization reversion record does not call for such a thing, then generateMany will be called
     *
     * @param originEntriesToWrite a list of origin entries to write, that any generated origin entries should be added to
     * @throws FatalErrorException thrown if the current object code can't be found in the database
     */
    public abstract void generateCarryForwards(List<OriginEntryFull> originEntriesToWrite) throws FatalErrorException;

    /**
     * If reversions are necessary, this will generate the origin entries for those reversions
     *
     * @param originEntriesToWrite the list of origin entries to add reversions into
     * @throws FatalErrorException thrown if object code if the entry can't be found
     */
    public abstract void generateReversions(List<OriginEntryFull> originEntriesToWrite) throws FatalErrorException;

    /**
     * This method calculates the totals for a given unit of work's reversion
     *
     * @throws FatalErrorException
     */
    public abstract void calculateTotals() throws FatalErrorException;

    /**
     * Writes out the encapsulated origin entry ledger report to the given reportWriterService
     *
     * @param reportWriterService the report to write the ledger summary report to
     */
    public abstract void writeLedgerSummaryReport(ReportWriterService reportWriterService);

    /**
     * Sets the holdGeneratedOriginEntries attribute value.
     *
     * @param holdGeneratedOriginEntries The holdGeneratedOriginEntries to set.
     */
    public abstract void setHoldGeneratedOriginEntries(boolean holdGeneratedOriginEntries);

    /**
     * Gets the generatedOriginEntries attribute.
     *
     * @return Returns the generatedOriginEntries.
     */
    public abstract List<OriginEntryFull> getGeneratedOriginEntries();

    /**
     * Returns the total number of balances for the previous fiscal year
     *
     * @return the total number of balances for the previous fiscal year
     */
    public abstract int getBalancesRead();

    /**
     * Returns the total number of balances selected for inclusion in this process
     *
     * @return the total number of balances selected for inclusion in this process
     */
    public abstract int getBalancesSelected();
}
