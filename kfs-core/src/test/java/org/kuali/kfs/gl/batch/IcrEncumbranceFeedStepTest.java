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
package org.kuali.kfs.gl.batch;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchDirectoryHelper;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * This class tests the file generation of the IcrEncumbranceFeedStep batch step
 * and indirectly tests the IcrEncumbranceService and IcrEncumbranceDao classes,
 * since this step can't succeed without the service and dao succeeding.
 */
@ConfigureContext
public class IcrEncumbranceFeedStepTest extends IcrEncumbranceStepTestBase {

    // Based KFS 5.3 contrib branch demo data
    private static final String ICR_ENCUMBRANCE_TEST_DATA_FILE_PATH = "org/kuali/kfs/gl/batch/fixture/gl_icrencmb.data.txt";

    private BatchDirectoryHelper batchDirectoryHelper;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        batchDirectoryHelper = new BatchDirectoryHelper("gl", "originEntry");
        batchDirectoryHelper.createBatchDirectory();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        batchDirectoryHelper.removeBatchDirectory();
    }

    /**
     * This test checks that the IcrEncumbranceFeedStep#execute() produces a good feed file.
     */
    @Override
    public void testExecute() {
        TestUtils.setSystemParameter(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.USE_ICR_ENCUMBRANCE_PARAM, "Y");

        File icrEncumbranceFeedFile = icrEncumbranceService.buildIcrEncumbranceFeed();
        assertTrue("The ICR Encumbrance file was found to be null.", ObjectUtils.isNotNull(icrEncumbranceFeedFile));
        assertTrue("The ICR Encumbrance file does not exist, should be at: " + icrEncumbranceFeedFile.getAbsolutePath(), icrEncumbranceFeedFile.exists());
        assertFalse("The ICR Encumbrance file should not be empty, located at: " + icrEncumbranceFeedFile.getAbsolutePath(), isFileEmpty(icrEncumbranceFeedFile));
        assertTrue("Not all lines are present or generated correctly.", linesGeneratedCorrectly(icrEncumbranceFeedFile));
    }


    /*
     * Ensure generated file contains all lines as expected.
     */
    private boolean linesGeneratedCorrectly(File generatedIcrEncumbranceFeedFile) {

        // Pull the test data into a list
        List<String> expectedLines = null;
        try {
            expectedLines = IOUtils.readLines(IcrEncumbranceFeedStepTest.class.getClassLoader().getResourceAsStream(ICR_ENCUMBRANCE_TEST_DATA_FILE_PATH));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Pull the service generated lines, as assembled from DB queries and logic
        List<String> generatedLines = null;
        try {
            generatedLines = IOUtils.readLines(new FileReader(generatedIcrEncumbranceFeedFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Change generated date (based on post date) to that found in the data fixture
        generatedLines = replaceGeneratedDates(generatedLines);

        // Place into set for easy comparison
        Set<String> expectedSet = new HashSet<String>();
        expectedSet.addAll(expectedLines);

        // Place into set for easy comparison
        Set<String> generatedSet = new HashSet<String>();
        generatedSet.addAll(generatedLines);

        // Check that all test data lines are present in service generated lines
        for (String line : expectedSet) {
            if (!generatedSet.contains(line)) {
                // The generated lines should contain all test data lines, this is a failure
                return false;
            }
        }

        // If we get here, the service generated all lines correctly
        return true;
    }

    /*
     * Replace date field of input lines to sync up with with test fixture,
     * this is necessary since the IcrEncumbrabceService uses "now" as the
     * posting date, whereas the test data is 2009-03-14.
     */
    private List<String> replaceGeneratedDates(List<String> lines) {
        // Format the data to the format of "YYYY-MM-dd"
        String dateStringFormat = "%04d-%02d-%02d";
        String dateString = String.format(dateStringFormat, FIXTURE_CALENDAR_YEAR, FIXTURE_MONTH + 1, FIXTURE_DAY);

        /*
         * The format of a line are fixed positions, and the date position
         * starts at 118 to 128, 0-based, inclusive.
         */
        List<String> results = new ArrayList<String>(lines.size());
        for (String line : lines) {
            String beginning = line.substring(0, 118);
            String middle = dateString;
            String end = line.substring(128, line.length());

            String newLine = beginning + middle + end;
            results.add(newLine);
        }

        return results;
    }

    /*
     * Checks that the input file has at least one non-blank line
     */
    private boolean isFileEmpty(File file) {
        BufferedReader reader = null;
        String testLine = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            testLine = reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return StringUtils.isBlank(testLine);
    }

}
