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
package org.kuali.kfs.gl.batch;

import org.apache.commons.io.IOUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchDirectoryHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class tests posting of ICR Encumbrance entries to the general ledger table.
 */
@ConfigureContext
public class PosterIcrEncumbranceEntriesStepTest extends IcrEncumbranceStepTestBase {
    private BatchDirectoryHelper batchDirectoryHelper;

    // Services
    private IcrEncumbranceSortStep icrEncumbranceSortStep;
    private PosterIcrEncumbranceEntriesStep posterIcrEncumbranceEntriesStep;
    private UnitTestSqlDao unitTestSqlDao;


    /**
     * This method sets up the services used during testing.
     *
     * @see org.kuali.kfs.gl.batch.IcrEncumbranceStepTestBase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();

        batchDirectoryHelper = new BatchDirectoryHelper("gl", "originEntry");
        batchDirectoryHelper.createBatchDirectory();

        // Init services
        this.icrEncumbranceSortStep = SpringContext.getBean(IcrEncumbranceSortStep.class);
        this.icrEncumbranceSortStep.setParameterService(SpringContext.getBean(ParameterService.class));
        this.posterIcrEncumbranceEntriesStep = SpringContext.getBean(PosterIcrEncumbranceEntriesStep.class);
        this.posterIcrEncumbranceEntriesStep.setParameterService(SpringContext.getBean(ParameterService.class));
        this.unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        batchDirectoryHelper.removeBatchDirectory();
    }

    /*
     * Populate a list of OriginEntryFull objects from the lines
     * contained in a generated feed file.
     */
    private List<OriginEntryFull> getOriginEntriesFromFile(File feedFile) {

        List<String> lines = getLinesFromFile(feedFile);

        List<OriginEntryFull> entryList = new ArrayList<OriginEntryFull>();
        int lineNumber = 1;
        for (String line : lines) {
            OriginEntryFull entry = new OriginEntryFull();
            entry.setFromTextFileForBatch(line, lineNumber);
            entryList.add(entry);
            lineNumber++;
        }

        return entryList;
    }


    /*
     * Helper method to pull lines from a file and create one
     * String for each line.
     */
    private List<String> getLinesFromFile(File feedFile) {
        Reader feedReader = null;
        List<String> lines = null;
        try {
            feedReader = new FileReader(feedFile);
            lines = IOUtils.readLines(feedReader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(feedReader);
        }

        return lines;
    }

    /**
     * This method tests the execute() mehthod of the PosterIcrEncumbranceEntriesStep.
     * <p>
     * The sequence of steps for the ICR Encumbrance posting job is:
     * 1.) icrEncumbranceFeedStep
     * 2.) icrEncumbranceSortStep
     * 3.) posterIcrEncumbranceEntriesStep <--- This is what we are testing, stop here
     * 4.) fileRenameStep
     *
     * @see org.kuali.kfs.gl.batch.IcrEncumbranceStepTestBase#testExecute()
     */
    @Override
    public void testExecute() {
        TestUtils.setSystemParameter(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.USE_ICR_ENCUMBRANCE_PARAM, "Y");

        // Generate a feed file, (see IcrEncumbranceFeedTest for full test coverage)
        File feedFile = icrEncumbranceService.buildIcrEncumbranceFeed();

        // Sort file contents (see IcrEncumbranceSortStepTest for full test coverage)
        this.icrEncumbranceSortStep.execute("testIcrEncumbranceSortStep", dateTimeService.getCurrentDate());

        // Ensure OriginEntryFull objects can be instantiated from the generated feed
        // file, and capture how many entries were generated
        int originEntryCount = getOriginEntriesFromFile(feedFile).size();
        assertTrue("The feed file did not produce the expected number of origin entries.", originEntryCount > 0);

        // Clear the general ledger table, then ensure no records are present,
        // this means any new records are from our test posting them
        unitTestSqlDao.sqlCommand("DELETE FROM GL_ENTRY_T");
        int glEntryTableRowCount = unitTestSqlDao.sqlSelect("SELECT UNIV_FISCAL_PRD_CD FROM GL_ENTRY_T").size();
        assertTrue("The GL_ENTRY_T should be empty.", glEntryTableRowCount == 0);

        // Perform posting, this step is the consumer of the feed file contents
        posterIcrEncumbranceEntriesStep.execute("testPosterIcrEncumbranceEntriesStep", dateTimeService.getCurrentDate());

        // Ensure the general ledger table contains twice the number of records as origin entries from the feed file
        glEntryTableRowCount = unitTestSqlDao.sqlSelect("SELECT UNIV_FISCAL_PRD_CD FROM GL_ENTRY_T").size();
        int expectedCount = originEntryCount * 2;
        String msg = String.format("The GL_ENTRY_T should have %d records but only has %d.", expectedCount, glEntryTableRowCount);
        assertTrue(msg, glEntryTableRowCount == expectedCount);

    }

}
