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

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsAgingOpenInvoicesReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsAgingOpenInvoicesReportService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.web.struts.ContractsGrantsAgingOpenInvoicesReportForm;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.ErrorMessage;

/**
 * This class tests the ContractsGrantsAgingOpenInvoicesReport lookup.
 */
@ConfigureContext(session = wklykins)
public class ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImplTest.class);

    private ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl agingOpenInvoicesReportLookupableHelperServiceImpl;
    private ContractsGrantsAgingOpenInvoicesReportForm agingOpenInvoicesReportForm;
    private Map fieldValues;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // setting up document
        String chartCode = "BL";
        //String orgCode = "SRS";
        String orgCode = "UGCS";

        String customerNumber = "ABB2";
        String customerName = "WOODS CORPORATION";

        // To create a basic invoice with test data
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, chartCode, orgCode, errorMessages, null, null);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        cgInvoice.getAccountsReceivableDocumentHeader().setCustomerNumber(customerNumber);

        cgInvoice.getAccountsReceivableDocumentHeader().setDocumentHeader(cgInvoice.getDocumentHeader());

        cgInvoice.setBillingDate(new java.sql.Date(new Date().getTime()));
        cgInvoice.getInvoiceGeneralDetail().setAward(award);
        cgInvoice.setOpenInvoiceIndicator(true);

        cgInvoice.setCustomerName(customerName);
        for (InvoiceAddressDetail invoiceAddressDetail : cgInvoice.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
            invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
        }
        documentService.saveDocument(cgInvoice);

        agingOpenInvoicesReportLookupableHelperServiceImpl = new ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl();
        agingOpenInvoicesReportLookupableHelperServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        agingOpenInvoicesReportLookupableHelperServiceImpl.setBusinessObjectClass(ContractsGrantsAgingOpenInvoicesReport.class);
        agingOpenInvoicesReportLookupableHelperServiceImpl.setContractsGrantsAgingOpenInvoicesReportService(SpringContext.getBean(ContractsGrantsAgingOpenInvoicesReportService.class));
        agingOpenInvoicesReportLookupableHelperServiceImpl.setContractsGrantsReportHelperService(SpringContext.getBean(ContractsGrantsReportHelperService.class));
        fieldValues = new LinkedHashMap();

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        fieldValues.put("backLocation", null);
        fieldValues.put("reportRunDate", dateFormat.format(new Date()));
        fieldValues.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
        fieldValues.put(ArPropertyConstants.BILLING_CHART_CODE, chartCode);
        fieldValues.put("docFormKey", null);
        fieldValues.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
        fieldValues.put(KFSPropertyConstants.CUSTOMER_NAME, customerName);
        fieldValues.put("businessObjectClassName", ContractsGrantsAgingOpenInvoicesReport.class.getName());

        Map<String,String[]> parameters = new HashMap<String, String[]>();
        parameters.put(KFSPropertyConstants.CUSTOMER_NUMBER, new String[] { customerNumber });
        parameters.put(KFSPropertyConstants.CUSTOMER_NAME, new String[] { customerName });
        agingOpenInvoicesReportLookupableHelperServiceImpl.setParameters(parameters);

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
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl#getSearchResults(java.util.Map)}
     * .
     */
    public void testGetSearchResultsMap() {
        Collection<?> displayList;
        assertNotNull("search results not null", displayList = agingOpenInvoicesReportLookupableHelperServiceImpl.getSearchResults(fieldValues));
    }

    /**
     * Test method for
     * {@link org.kuali.kfs.module.ar.businessobject.lookup.ContractsGrantsAgingOpenInvoicesReportLookupableHelperServiceImpl#performLookup(org.kuali.rice.kns.web.struts.form.LookupForm, java.util.Collection, boolean)}
     * .
     */
    public void testPerformLookupLookupFormCollectionBoolean() {
        agingOpenInvoicesReportForm = new ContractsGrantsAgingOpenInvoicesReportForm();
        agingOpenInvoicesReportForm.setFieldsForLookup(fieldValues);

        Collection resultTable = new ArrayList<String>();
        assertNotNull("lookup list not null", agingOpenInvoicesReportLookupableHelperServiceImpl.performLookup(agingOpenInvoicesReportForm, resultTable, true));
    }

}
