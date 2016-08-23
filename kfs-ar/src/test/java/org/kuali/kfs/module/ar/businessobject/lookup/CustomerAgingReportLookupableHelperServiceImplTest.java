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
package org.kuali.kfs.module.ar.businessobject.lookup;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.web.struts.CustomerAgingReportForm;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.BusinessObjectService;

/**
 * This class tests the CustomerAgingReport lookup and totals calculations
 */
@ConfigureContext(session = khuntley)
public class CustomerAgingReportLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerAgingReportLookupableHelperServiceImplTest.class);

    private CustomerAgingReportLookupableHelperServiceImpl customerAgingReportLookupableHelperServiceImpl;
    private CustomerAgingReportForm customerAgingReportForm;
    private Map fieldValues;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        customerAgingReportLookupableHelperServiceImpl = new CustomerAgingReportLookupableHelperServiceImpl();
        customerAgingReportLookupableHelperServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        customerAgingReportLookupableHelperServiceImpl.setParameterService(SpringContext.getBean(ParameterService.class));
        customerAgingReportLookupableHelperServiceImpl.setDateTimeService(SpringContext.getBean(DateTimeService.class));
        customerAgingReportLookupableHelperServiceImpl.afterPropertiesSet();
        fieldValues = new LinkedHashMap();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        fieldValues.put("backLocation", null);
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, "1111111");
        fieldValues.put("reportRunDate", dateFormat.format(new Date()));
        fieldValues.put("reportOption", "Account");
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "");
        fieldValues.put(KFSPropertyConstants.ORGANIZATION_CODE, "");
        fieldValues.put("docFormKey", null);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.CustomerAgingReportLookupableHelperServiceImpl#getSearchResults(java.util.Map)}
     * .
     */
    public void testGetSearchResultsMap() {
        Collection<?> displayList;

        // run search
        KualiDecimal test0to30total = new KualiDecimal("0.00");
        KualiDecimal test31to60total = new KualiDecimal("0.00");
        KualiDecimal test61to90total = new KualiDecimal("0.00");
        KualiDecimal test91toSYSPRtotal = new KualiDecimal("0.00");
        KualiDecimal testSYSPRplus1orMoretotal = new KualiDecimal("0.00");
        assertNotNull("search results not null", displayList = customerAgingReportLookupableHelperServiceImpl.getSearchResults(fieldValues));

        // add all 0to30 totals
        for (Object aDisplayList : displayList) {
            CustomerAgingReportDetail detail = (CustomerAgingReportDetail) aDisplayList;
            test0to30total = test0to30total.add(detail.getUnpaidBalance0to30());
            test31to60total = test31to60total.add(detail.getUnpaidBalance31to60());
            test61to90total = test61to90total.add(detail.getUnpaidBalance61to90());
            test91toSYSPRtotal = test91toSYSPRtotal.add(detail.getUnpaidBalance91toSYSPR());
            testSYSPRplus1orMoretotal = testSYSPRplus1orMoretotal.add(detail.getUnpaidBalanceSYSPRplus1orMore());
        }
        assertEquals(customerAgingReportLookupableHelperServiceImpl.getTotal0to30().toString(), test0to30total.toString());
        assertEquals(customerAgingReportLookupableHelperServiceImpl.getTotal31to60().toString(), test31to60total.toString());
        assertEquals(customerAgingReportLookupableHelperServiceImpl.getTotal61to90().toString(), test61to90total.toString());
        LOG.info("\n\n\n\n***************************************************************************************\n" + "\n\t\t testtotal0to30 = " + customerAgingReportLookupableHelperServiceImpl.getTotal0to30().toString() + "\t\t\t\t\tactualtotal0to30 = " + test0to30total.toString() + "\n\t\t testtotal31to60 = " + customerAgingReportLookupableHelperServiceImpl.getTotal31to60().toString() + "\t\t\t\t\t\tactualtotal31to60 = " + test31to60total.toString() + "\n\t\t testtotal61to90 = " + customerAgingReportLookupableHelperServiceImpl.getTotal61to90().toString() + "\t\t\t\t\t\tactualtotal61to90 = " + test61to90total.toString() + "\n\t\t testtotal91toSYSPR = " + customerAgingReportLookupableHelperServiceImpl.getTotal91toSYSPR().toString() + "\t\t\t\t\tactualtotal91toSYSPR = " + test91toSYSPRtotal.toString() + "\n\t\t testtotalSYSPRplus1orMore = " + customerAgingReportLookupableHelperServiceImpl.getTotalSYSPRplus1orMore().toString() + "\t\t\tactualtotalSYSPRplus1orMore = "
                + testSYSPRplus1orMoretotal.toString() + "\n\n***************************************************************************************\n\n");
    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.CustomerAgingReportLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)}
     * .
     */
    public void testPerformLookupLookupFormCollectionBoolean() {

        customerAgingReportForm = new CustomerAgingReportForm();
        customerAgingReportForm.setFieldsForLookup(fieldValues);

        Collection resultTable = new ArrayList<String>();
        assertNotNull("lookup list not null", customerAgingReportLookupableHelperServiceImpl.performLookup(customerAgingReportForm, resultTable, true));
    }

}
