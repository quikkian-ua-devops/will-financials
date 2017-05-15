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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

/**
 * Test file for ContractsGrantsAgingOpenInvoicesReport service.
 */
@ConfigureContext(session = wklykins)
public class ContractsGrantsAgingOpenInvoicesReportServiceTest extends KualiTestBase {

    private final static String CUSTOMER_NUMBER = "ABB2";
    private final static String CUSTOMER_NAME = "WOODS CORPORATION";

    ContractsGrantsAgingOpenInvoicesReportService contractsGrantsAgingOpenInvoicesReportService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        // setting up document
        String chartCode = "BL";
        String orgCode = "UGCS";

        // To create a basic invoice with test data
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, chartCode, orgCode, errorMessages, null, null);

        DocumentHeader documentHeader = cgInvoice.getDocumentHeader();
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();

        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);

        cgInvoice.getAccountsReceivableDocumentHeader().setCustomerNumber(CUSTOMER_NUMBER);
        cgInvoice.getAccountsReceivableDocumentHeader().setDocumentHeader(documentHeader);

        cgInvoice.setBillingDate(new java.sql.Date(new Date().getTime()));
        cgInvoice.getInvoiceGeneralDetail().setAward(award);
        cgInvoice.setOpenInvoiceIndicator(true);
        cgInvoice.setCustomerName(CUSTOMER_NAME);
        for (InvoiceAddressDetail invoiceAddressDetail : cgInvoice.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
            invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
        }
        documentService.saveDocument(cgInvoice);

        contractsGrantsAgingOpenInvoicesReportService = SpringContext.getBean(ContractsGrantsAgingOpenInvoicesReportService.class);
    }

    /**
     * Tests getPopulatedReportDetails() method of service ContractsGrantsAgingOpenInvoicesReportService.
     */
    public void testGetPopulatedReportDetails() {
        Map urlParameters = new HashMap();
        urlParameters.put(KFSPropertyConstants.CUSTOMER_NUMBER, new String[]{CUSTOMER_NUMBER});
        urlParameters.put(KFSPropertyConstants.CUSTOMER_NAME, new String[]{CUSTOMER_NAME});
        List openinvoicesList = contractsGrantsAgingOpenInvoicesReportService.getPopulatedReportDetails(urlParameters);
        assertNotNull(openinvoicesList);
    }
}
