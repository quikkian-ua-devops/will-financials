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
package org.kuali.kfs.gl.businessobject.lookup;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.gl.web.TestDataGenerator;
import org.kuali.kfs.kns.lookup.LookupableHelperService;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is a template being used by the test case classes of GL lookupable implementation.
 */
public abstract class AbstractGeneralLedgerLookupableHelperServiceTestBase extends KualiTestBase {

    protected Date date;
    protected GeneralLedgerPendingEntry pendingEntry;
    protected TestDataGenerator testDataGenerator;
    protected LookupableHelperService lookupableHelperServiceImpl;
    protected GeneralLedgerPendingEntryService pendingEntryService;

    /**
     * Sets up the test by initializing several properties
     *
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        date = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        pendingEntry = new GeneralLedgerPendingEntry();
        testDataGenerator = new TestDataGenerator();

        setPendingEntryService(SpringContext.getBean(GeneralLedgerPendingEntryService.class));
    }

    /**
     * test cases for getSearchResults method of LookupableImpl class
     */
    public abstract void testGetSearchResults() throws Exception;

    /**
     * This method defines the lookup fields
     *
     * @param isExtended flag if the non required fields are included
     * @return a list of lookup fields
     */
    public abstract List getLookupFields(boolean isExtended);

    /**
     * This method defines the primary key fields
     *
     * @return a list of primary key fields
     */
    public List getPrimaryKeyFields() {
        return lookupableHelperServiceImpl.getReturnKeys();
    }

    /**
     * test cases for getInquiryUrl method of LookupableImpl class
     */
    public void testGetInquiryUrl() throws Exception {
    }

    /**
     * This method tests if the search results have the given entry
     *
     * @param searchResults  the search results
     * @param businessObject the given business object
     * @return true if the given business object is in the search results
     */
    protected boolean contains(List searchResults, PersistableBusinessObjectBase businessObject) {
        boolean isContains = false;
        List priamryKeyFields = getPrimaryKeyFields();
        int numberOfPrimaryKeyFields = priamryKeyFields.size();

        String propertyName, resultPropertyValue, propertyValue;

        Iterator searchResultsIterator = searchResults.iterator();
        while (searchResultsIterator.hasNext() && !isContains) {
            Object resultRecord = searchResultsIterator.next();

            isContains = true;
            for (int i = 0; i < numberOfPrimaryKeyFields; i++) {
                try {
                    propertyName = (String) (priamryKeyFields.get(i));
                    resultPropertyValue = PropertyUtils.getProperty(resultRecord, propertyName).toString();
                    propertyValue = PropertyUtils.getProperty(businessObject, propertyName).toString();

                    if (!resultPropertyValue.equals(propertyValue)) {
                        isContains = false;
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return isContains;
    }

    /**
     * This method creates the lookup form fields with the given business object and lookup fields
     *
     * @param businessObject the given business object
     * @param isExtended     determine if the extended lookup fields are used
     * @return a lookup form fields
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public Map getLookupFieldValues(PersistableBusinessObjectBase businessObject, boolean isExtended) throws Exception {
        List lookupFields = this.getLookupFields(isExtended);
        return testDataGenerator.generateLookupFieldValues(businessObject, lookupFields);
    }

    /**
     * This method inserts a new pending ledger Entry record into database
     *
     * @param pendingEntry the given pending ledger Entry
     */
    protected void insertNewPendingEntry(GeneralLedgerPendingEntry pendingEntry) {
        try {
            getPendingEntryService().save(pendingEntry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the pendingEntryService attribute.
     *
     * @return Returns the pendingEntryService.
     */
    public GeneralLedgerPendingEntryService getPendingEntryService() {
        return pendingEntryService;
    }

    /**
     * Sets the pendingEntryService attribute value.
     *
     * @param pendingEntryService The pendingEntryService to set.
     */
    public void setPendingEntryService(GeneralLedgerPendingEntryService pendingEntryService) {
        this.pendingEntryService = pendingEntryService;
    }
}
