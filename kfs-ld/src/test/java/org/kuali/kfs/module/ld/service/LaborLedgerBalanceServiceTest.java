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
package org.kuali.kfs.module.ld.service;

import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.List;
import java.util.Properties;

@ConfigureContext
public class LaborLedgerBalanceServiceTest extends KualiTestBase {
    private Properties properties;
    private String fieldNames;
    private String transactionFieldNames;
    private String deliminator;
    private LaborLedgerBalanceService laborLedgerBalanceService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborLedgerBalanceService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);

        fieldNames = properties.getProperty("fieldNames");
        transactionFieldNames = properties.getProperty("transactionFieldNames");
        deliminator = properties.getProperty("deliminator");
        laborLedgerBalanceService = SpringContext.getBean(LaborLedgerBalanceService.class);
    }

    public void testFindLedgerBalance_Found() throws Exception {
        String testTarget = "findLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfTransaction"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborOriginEntry> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "transaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborOriginEntry transaction : transactionList) {
            assertNotNull(laborLedgerBalanceService.findLedgerBalance(ledgerBalanceList, transaction));
        }
    }

    public void testFindLedgerBalance_NotFound() throws Exception {
        String testTarget = "findLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfNotFoundTransaction"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborOriginEntry> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "notFoundTransaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborOriginEntry transaction : transactionList) {
            assertNull(laborLedgerBalanceService.findLedgerBalance(ledgerBalanceList, transaction));
        }
    }

    public void testAddLedgerBalance_New() throws Exception {
        String testTarget = "addLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfNewTransaction"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborOriginEntry> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "newTransaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborOriginEntry transaction : transactionList) {
            laborLedgerBalanceService.addLedgerBalance(ledgerBalanceList, transaction);
        }

        int expectedNumberOfBalances = numberOfLedgerBalance + numberOfTransaction;
        assertEquals(expectedNumberOfBalances, ledgerBalanceList.size());
    }

    public void testAddLedgerBalance_Existing() throws Exception {
        String testTarget = "addLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfExistingTransaction"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborOriginEntry> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "existingTransaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborOriginEntry transaction : transactionList) {
            laborLedgerBalanceService.addLedgerBalance(ledgerBalanceList, transaction);
        }

        int expectedNumberOfBalances = numberOfLedgerBalance;
        assertEquals(expectedNumberOfBalances, ledgerBalanceList.size());
    }

    public void testUpdateLedgerBalance() throws Exception {
        String testTarget = "updateLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfTransaction"));
        int numberOfExpected = Integer.valueOf(properties.getProperty(testTarget + "numOfExpected"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborOriginEntry> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "transaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborOriginEntry transaction : transactionList) {
            LedgerBalance ledgerBalance = laborLedgerBalanceService.findLedgerBalance(ledgerBalanceList, transaction);
            if (ledgerBalance != null) {
                laborLedgerBalanceService.updateLedgerBalance(ledgerBalance, transaction);
            }
        }

        List<LaborOriginEntry> expectedList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "expected", transactionFieldNames, deliminator, numberOfExpected);
        for (LaborOriginEntry expected : expectedList) {
            LedgerBalance ledgerBalance = laborLedgerBalanceService.findLedgerBalance(ledgerBalanceList, expected);
            assertNotNull(ledgerBalance);
            ledgerBalance.getMonth1Amount().equals(expected.getTransactionLedgerEntryAmount());
        }
    }
}
