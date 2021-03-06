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
package org.kuali.kfs.module.ld.businessobject.lookup;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.web.TestDataGenerator;
import org.kuali.kfs.kns.lookup.LookupableHelperService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.PersistenceService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds;
import org.kuali.kfs.module.ld.service.LaborInquiryOptionsService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class contains test cases that can be applied to methods in Account Status Base Funds class.
 */
@ConfigureContext
public class BaseFundsLookupableHelperServiceTest extends KualiTestBase {
    private BusinessObjectService businessObjectService;
    private LookupableHelperService lookupableHelperService;
    private PersistenceService persistenceService;

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private OriginEntryGroup groupToPost;
    private Map fieldValues, groupFieldValues;

    private int csfNumberOfTestData;
    private int csfExpectedInsertion;
    private int baseFundsNumberOfTestData;
    private int baseFundsExpectedInsertion;

    @Override

    /**
     * Get things ready for the test
     */
    protected void setUp() throws Exception {
        super.setUp();


        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        lookupableHelperService = LookupableSpringContext.getLookupableHelperService(LaborConstants.BASE_FUNDS_LOOKUP_HELPER_SRVICE_NAME);
        lookupableHelperService.setBusinessObjectClass(AccountStatusBaseFunds.class);

        // Clear up the database so that any existing data cannot affact your test result
        Map keys = new HashMap();
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, "1031400");
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, TestUtils.getFiscalYearForTesting().toString());
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
        businessObjectService.deleteMatching(AccountStatusBaseFunds.class, keys);
    }

    /**
     * This method will run the base funds balance inquiry to test that the BaseFundsLookupableHelperService
     * is returning data correctly.
     *
     * @throws Exception
     */
    public void testGetSearchResults() throws Exception {
        insertBaseFundsRecords();
        insertCSFRecords();
        System.out.println("**** RECORDS INSERTED !!!!!");

        String messageFileName = "org/kuali/kfs/module/ld/testdata/message.properties";
        String propertiesFileName = "org/kuali/kfs/module/ld/testdata/accountStatusBaseFunds.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();

        AccountStatusBaseFunds accountStatusBaseFunds = new AccountStatusBaseFunds();
        accountStatusBaseFunds.setAccountNumber("1031400");
        accountStatusBaseFunds.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        accountStatusBaseFunds.setChartOfAccountsCode("BL");

        // test the search results before the specified entry is inserted into the database
        Map fieldValues = buildFieldValues(accountStatusBaseFunds, this.getLookupFields(false));

        // Tells the lookupable I want detailed results
        getInquiryOptionsService().getConsolidationField(lookupableHelperService.getRows()).setPropertyValue(Constant.DETAIL);
        fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.DETAIL);

        //List<String> groupByList = new ArrayList<String>();
        List<AccountStatusBaseFunds> searchResults = (List<AccountStatusBaseFunds>) lookupableHelperService.getSearchResults(fieldValues);

        // Make sure the basic search parameters are returned from the inquiry
        for (AccountStatusBaseFunds accountStatusBaseFundsReturn : searchResults) {
            assertTrue((accountStatusBaseFundsReturn.getAccountNumber().equals(accountStatusBaseFunds.getAccountNumber()) &&
                accountStatusBaseFundsReturn.getUniversityFiscalYear().equals(accountStatusBaseFunds.getUniversityFiscalYear()) &&
                accountStatusBaseFundsReturn.getChartOfAccountsCode().equals(accountStatusBaseFunds.getChartOfAccountsCode())));
        }

        if (searchResults != null) {
            System.out.println("*** Results Size:" + searchResults.size());
        }

        // compare the search results with the expected and see if they match with each other
        assertEquals(this.baseFundsExpectedInsertion, searchResults.size());


        /*int expectedOfData = Integer.valueOf(properties.getProperty("getAccountStatusBaseFunds.expectedNumOfData"));
        Collection entries = businessObjectService.findMatching(AccountStatusBaseFunds.class, fieldValues);
        List expectedDataList = TestDataPreparator.buildExpectedValueList(AccountStatusBaseFunds.class, properties, "getAccountStatusBaseFunds.expected", fieldNames, deliminator, this.baseFundsExpectedInsertion);
        for (Object entry : entries) {
            LedgerEntryForTesting ledgerEntryForTesting = new LedgerEntryForTesting();
            ObjectUtil.buildObject(ledgerEntryForTesting, entry);
            assertTrue(expectedDataList.contains(ledgerEntryForTesting));
        }
        assertEquals(expectedNumOfData, ledgerEntries.size());*/

        String testTarget = "getAccountStatusBaseFunds.";
        //Collection ledgerEntries = businessObjectService.findMatching(AccountStatusBaseFunds.class, fieldValues);
        System.out.println("****1111");
        List expectedDataList = TestDataPreparator.buildExpectedValueList(AccountStatusBaseFunds.class, properties, testTarget + "expected", fieldNames, deliminator, this.baseFundsExpectedInsertion);
        for (Object expectedAccountStatusBaseFundsAsObject : expectedDataList) {
            AccountStatusBaseFunds expectedAccountStatusBaseFunds = (AccountStatusBaseFunds) expectedAccountStatusBaseFundsAsObject;
            expectedAccountStatusBaseFunds.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
        }
        System.out.println("****2222");

        for (int i = 0; i < searchResults.size(); i++) {
            //AccountStatusBaseFundsForTesting accountStatusBaseFundsForTesting = new AccountStatusBaseFundsForTesting();
            //accountStatusBaseFunds = new AccountStatusBaseFunds();
            //ObjectUtil.buildObject(accountStatusBaseFunds, entry);
            accountStatusBaseFunds = ((AccountStatusBaseFunds) searchResults.get(i));
            System.out.println("*********DATA:" + accountStatusBaseFunds.toString());
            assertTrue("Expected data list doesn't contain accountStatusBaseFunds", expectedDataList.contains(accountStatusBaseFunds));
        }
        assertEquals(this.baseFundsExpectedInsertion, searchResults.size());
    }

    /**
     *
     * This method will run the base funds balance inquiry to test that the BaseFundsLookupableHelperService
     * is returning data correctly.
     * @throws Exception
     *
    public void testGetSearchResultsConsolidated() throws Exception {
    insertBaseFundsRecords();
    //insertCSFRecords();

    AccountStatusBaseFunds accountStatusBaseFunds = new AccountStatusBaseFunds();
    accountStatusBaseFunds.setAccountNumber("1031400");
    accountStatusBaseFunds.setUniversityFiscalYear(2007);
    accountStatusBaseFunds.setChartOfAccountsCode("BL");

    // test the search results before the specified entry is inserted into the database
    Map fieldValues = buildFieldValues(accountStatusBaseFunds, this.getLookupFields(false));

    // Tells the lookupable I want consolidated results
    getInquiryOptionsService().getConsolidationField(lookupableHelperService.getRows()).setPropertyValue(Constant.CONSOLIDATION);
    fieldValues.put(Constant.CONSOLIDATION_OPTION, Constant.CONSOLIDATION);

    List<String> groupByList = new ArrayList<String>();
    List<AccountStatusBaseFunds> searchResults = lookupableHelperService.getSearchResults(fieldValues);

    // Make sure the basic search parameters are returned from the inquiry
    for (AccountStatusBaseFunds accountStatusBaseFundsReturn : searchResults) {
    assertFalse(!(accountStatusBaseFundsReturn.getAccountNumber().equals(accountStatusBaseFunds.getAccountNumber()) &&
    accountStatusBaseFundsReturn.getUniversityFiscalYear().equals(accountStatusBaseFunds.getUniversityFiscalYear()) &&
    accountStatusBaseFundsReturn.getChartOfAccountsCode().equals(accountStatusBaseFunds.getChartOfAccountsCode())));
    }

    if (searchResults != null) {
    System.out.println("Results Size:" + searchResults.size());
    }

    // compare the search results with the expected and see if they match with each other
    assertEquals(this.baseFundsExpectedInsertion,searchResults.size());

    }*/

    /**
     * This method uses property file parameters to create insert database records for this test
     *
     * @param accountStatusBaseFunds
     * @param lookupFields
     * @return
     */
    private Map<String, String> buildFieldValues(AccountStatusBaseFunds accountStatusBaseFunds, List<String> lookupFields) {
        Map<String, String> fieldValues = new HashMap<String, String>();

        Map<String, Object> tempFieldValues = ObjectUtil.buildPropertyMap(accountStatusBaseFunds, lookupFields);
        for (String key : tempFieldValues.keySet()) {
            fieldValues.put(key, tempFieldValues.get(key).toString());
        }
        return fieldValues;
    }

    /**
     * This method adds property constatants for future lookups
     *
     * @param isExtended
     * @return
     */
    private List<String> getLookupFields(boolean isExtended) {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        lookupFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);

        return lookupFields;
    }

    /**
     * This method will add temporary test data to the CSF Tracker table
     */
    protected void insertCSFRecords() {
        String messageFileName = "org/kuali/kfs/module/ld/testdata/message.properties";
        String propertiesFileName = "org/kuali/kfs/module/ld/testdata/csfTracker.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        String documentFieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        //CalculatedSalaryFoundationTracker cleanup = new CalculatedSalaryFoundationTracker();
        //ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        //Map fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        //businessObjectService.deleteMatching(CalculatedSalaryFoundationTracker.class, fieldValues);

        TestDataGenerator testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        String testTarget = "getCSFTracker.";
        this.csfNumberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numberOfDocuments"));
        this.csfExpectedInsertion = Integer.valueOf(properties.getProperty(testTarget + "expectedInsertion"));
    }

    /**
     * This method will add temporary test data to the Ledger Balance table
     */
    protected void insertBaseFundsRecords() {
        String testTarget = "getAccountStatusBaseFunds.";
        String propertyKey;

        AccountStatusBaseFunds inputData;
        String messageFileName = "org/kuali/kfs/module/ld/testdata/message.properties";
        String propertiesFileName = "org/kuali/kfs/module/ld/testdata/accountStatusBaseFunds.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        TestDataGenerator testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        int numberOfData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));

        List<AccountStatusBaseFunds> inputDataList = new ArrayList<AccountStatusBaseFunds>();

        for (int i = 1; i <= numberOfData; i++) {
            inputData = new AccountStatusBaseFunds();

            propertyKey = testTarget + "testData" + i;

            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldNames, deliminator);
            inputData.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
            inputDataList.add(inputData);
        }
        this.baseFundsNumberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        this.baseFundsExpectedInsertion = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));
        businessObjectService.save(inputDataList);


        /*int expectedOfData = Integer.valueOf(properties.getProperty("getAccountStatusBaseFunds.expectedNumOfData"));
        Collection ledgerEntries = businessObjectService.findMatching(LedgerBalance.class, fieldValues);
        List expectedDataList = TestDataPreparator.buildExpectedValueList(LedgerEntryForTesting.class, properties, "getAccountStatusBaseFunds.expected", fieldNames, deliminator, expectedNumOfData);
        for (Object entry : ledgerEntries) {
            LedgerEntryForTesting ledgerEntryForTesting = new LedgerEntryForTesting();
            ObjectUtil.buildObject(ledgerEntryForTesting, entry);
            assertTrue(expectedDataList.contains(ledgerEntryForTesting));
        }
        assertEquals(expectedNumOfData, ledgerEntries.size());*/
    }

    private LaborInquiryOptionsService getInquiryOptionsService() {
        return SpringContext.getBean(LaborInquiryOptionsService.class);
    }
}
