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
package org.kuali.kfs.module.ld.batch.service.impl;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl;
import org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTestBase;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.kns.bo.Step;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.LaborBalancingStep;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceHistory;
import org.kuali.kfs.module.ld.businessobject.LaborEntryHistory;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * LD LaborBalancingServiceImpl test cases
 */
@ConfigureContext
public class LaborBalancingServiceImplTest extends BalancingServiceImplTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBalancingServiceImplTest.class);

    private boolean batchFileDirectoryCreated = false;

    @Override
    protected void setUp() throws Exception {
        balancingService = (BalancingServiceBaseImpl<Entry, Balance>) TestUtils.getUnproxiedService("laborBalancingService");
        ledgerEntryHistoryBalancingDao = (LedgerEntryHistoryBalancingDao) SpringContext.getService("laborLedgerEntryHistoryDao");
        ledgerBalancingDao = (LedgerBalancingDao) SpringContext.getService("laborBalancingDao");
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // Delete all data so that balancing has an empty table set to work with
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        businessObjectService.deleteMatching(LedgerEntry.class, fieldValues);
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
        businessObjectService.deleteMatching(LaborEntryHistory.class, fieldValues);
        businessObjectService.deleteMatching(LaborBalanceHistory.class, fieldValues);

        // Because KULDBA doesn't support FYs more then 1 year back we need to limit our range in order to properly test boundary cases
        TestUtils.setSystemParameter(LaborBalancingStep.class, LaborConstants.Balancing.NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE, "0");

        File batchFileDirectory = new File(this.getBatchFileDirectoryName());
        if (!batchFileDirectory.exists()) {
            batchFileDirectory.mkdir();
            batchFileDirectoryCreated = true;
        }

        // careful: super.setUp needs to happen at the end because of service initialization and NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        if (batchFileDirectoryCreated) {
            File batchFileDirectory = new File(this.getBatchFileDirectoryName());
            for (File f : batchFileDirectory.listFiles()) {
                f.delete();
            }
            batchFileDirectory.delete();
            batchFileDirectoryCreated = false;
        }

        super.tearDown();
    }

    @Override
    public void testRunBalancingPopulateData() {
        LOG.debug("No data present scenario, hence process should populate data");

        // First confirm tables are empty. If this fails then the test scenario is incorrectly set up
        assertEquals(0, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(0, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(0, this.getHistoryCount(null, LaborEntryHistory.class));
        assertEquals(0, this.getHistoryCount(null, LaborBalanceHistory.class));

        // Generate some data and check that poster operated as expected. If this fails then the test scenario is incorrectly set up
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertEquals(12, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(3, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());

        // Balancing should succeed successfully since this is an expected use case where it does an initial population of history tables
        assertTrue(balancingService.runBalancing());

        // Once it ran, we expect data in balancing tables. This data is per INPUT_TRANSACTIONS data structure
        assertEquals(12, ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(10, this.getHistoryCount(null, LaborEntryHistory.class));
        assertEquals(3, this.getHistoryCount(null, LaborBalanceHistory.class));

        // Finally make sure there isn't any comparison failure. Since we just populated the history tables this should hold true
        this.assertCompareHistorySuccess();
    }

    @Override
    public void testRunBalancingDeleteObsoleteUniversityFiscalYearData() {
        // Run the poster to pick up the last two entries which are out of range
        this.postTestCaseEntries(INPUT_TRANSACTIONS);

        // Manually populate our history tables and force entries to be picked up. This essentially does the same as testRunBalancingPopulateData
        assertTrue("Populate should have copied some data", 0 != ledgerBalancingDao.populateLedgerEntryHistory(obsoleteUniversityFiscalYear));
        assertTrue("Populate should have copied some data", 0 != ledgerBalancingDao.populateLedgerBalanceHistory(obsoleteUniversityFiscalYear));

        // Pretty silly at this point, but lets double check that it copied the entries
        assertTrue("Found no LaborEntryHistory", 0 != this.getHistoryCount(obsoleteUniversityFiscalYear, LaborEntryHistory.class));
        assertTrue("Found no LaborBalanceHistory", 0 != this.getHistoryCount(obsoleteUniversityFiscalYear, LaborBalanceHistory.class));

        // Run Balancing, it should hit the case that deletes the obsolete entries. Coincidentally: This will also ignore the two out of range entries in
        // INPUT_TRANSACTIONS (the last two) but we're not actively testing for that here
        assertTrue(balancingService.runBalancing());

        // Verify that it deleted the entries
        assertEquals(0, this.getHistoryCount(obsoleteUniversityFiscalYear, LaborEntryHistory.class));
        assertEquals(0, this.getHistoryCount(obsoleteUniversityFiscalYear, LaborBalanceHistory.class));
    }

    @Override
    public void testRunBalancingHistoryUpdate() {
        // First pass is exactly the same as testRunBalancingPopulateData. This serves to populate the tables
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertTrue("Populate should have copied some data", 0 != ledgerBalancingDao.populateLedgerEntryHistory(obsoleteUniversityFiscalYear));
        assertTrue("Populate should have copied some data", 0 != ledgerBalancingDao.populateLedgerBalanceHistory(obsoleteUniversityFiscalYear));

        try {
            // Briefly sleep to ensure that we get unique timestamps on the rename step. This is obsolete since the input files will
            // be the same but done to avoid future bugs if there are changes to the test setup
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            assertTrue("No reason that this job should have gotten interrupted.", false);
        }

        // Next we post exactly the same entries and verify data exists. Note, now we have more entries
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertEquals(24, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(3, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());

        // And run balancing again. Again, this should succeed
        assertTrue(balancingService.runBalancing());

        // Once it ran, we expect data in balancing tables, but different this time since the balancing job ran updates
        assertEquals(24, ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(10, this.getHistoryCount(null, LaborEntryHistory.class));
        assertEquals(3, this.getHistoryCount(null, LaborBalanceHistory.class));

        // Finally make sure there wasn't any comparison failure. We do this by running comparison methods directly (again)
        this.assertCompareHistorySuccess();
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTestBase#postTestCaseEntries(java.lang.String[])
     */
    @Override
    protected void postTestCaseEntries(String[] inputTransactions) {
        // Write test file
        TestUtils.writeFile(this.getBatchFileDirectoryName() + File.separator + LaborConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION, inputTransactions);

        try {
            // Run the poster
            Step laborPosterStep = BatchSpringContext.getStep("laborPosterStep");
            assertTrue("laborPosterStep should have succeeded", laborPosterStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));

            // Rename the file because that's what happens before the balancing job runs
            Step laborFileRenameStep = BatchSpringContext.getStep("laborFileRenameStep");
            assertTrue("laborFileRenameStep should have succeeded", laborFileRenameStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
        } catch (InterruptedException e) {
            assertTrue("laborPosterStep or laborFileRenameStep failed: " + e.getMessage(), true);
        }
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTest#getInputTransactions()
     */
    @Override
    protected String[] getInputTransactions() {
        // These inputTransactions are missing an initial 4 character FY string. It is added in setUp to test Balancing skipping old entries. Also, first 4
        // of the following array are in error intentionally.
        return new String[]{
            "BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                       619.90D2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                       276.47D2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                       448.77D2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                       619.90C2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "BL1031400-----5760---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                       276.47C2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "BL1031400-----5772---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                       448.77C2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "BL1031400-----5625---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                       619.90D2009-02-05                                                                     0.00     200906000000000010                                                      ",
            "BL1031400-----5760---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                       276.47D2009-02-05                                                                     0.00     200906000000000010                                                      ",
            "BL1031400-----5772---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                       448.77D2009-02-05                                                                     0.00     200906000000000010                                                      ",
            "BL1031400-----5625---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                       619.90C2009-02-05                                                                     0.00     200906000000000010                                                      ",
            "BL1031400-----5760---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                       276.47C2009-02-05                                                                     0.00     200906000000000010                                                      ",
            "BL1031400-----5772---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                       448.77C2009-02-05                                                                     0.00     200906000000000010                                                      ",
            "BL1031400-----5625---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                       619.90C2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "BL1031400-----5625---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                       619.90C2009-02-05                                                                     0.00     200906000000000010                                                      ",
            "BL1031400-----5760---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                       276.47C2009-02-05                                                                     0.00     200905000000000010                                                      ",
            "BL1031400-----5760---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                       276.47C2009-02-05                                                                     0.00     200906000000000010                                                      ",
            CHART_OF_ACCOUNTS_CODE + "1031400-----" + FINANCIAL_OBJECT_CODE + "---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                       448.77C2009-02-05                                                                     0.00     200905000000000010                                                      ",
            CHART_OF_ACCOUNTS_CODE + "1031400-----" + FINANCIAL_OBJECT_CODE + "---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                       448.77C2009-02-05                                                                     0.00     200906000000000010                                                      ",
        };
    }
}
