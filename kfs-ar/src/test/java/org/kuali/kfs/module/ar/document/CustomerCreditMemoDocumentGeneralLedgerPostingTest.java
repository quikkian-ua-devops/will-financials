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
package org.kuali.kfs.module.ar.document;

import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDetailFixture;
import org.kuali.kfs.module.ar.fixture.CustomerInvoiceDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.math.BigDecimal;
import java.util.List;

@ConfigureContext(session = UserNameFixture.khuntley)
public class CustomerCreditMemoDocumentGeneralLedgerPostingTest extends KualiTestBase {

    private TaxService taxService;

    @Override
    public void setUp() throws Exception {
        //  wire up the mock instead of the regular TaxService
        taxService = SpringContext.getBean(TaxService.class, "arTaxServiceMock");
    }

    public void testMockTaxServiceWorks() {
        List<TaxDetail> taxDetails;

        taxDetails = taxService.getSalesTaxDetails(null, "12345", new KualiDecimal(100));
        assertTrue("taxDetails should be empty.", (taxDetails.size() == 0));

        taxDetails = taxService.getSalesTaxDetails(null, "85705", new KualiDecimal(100));
        assertTrue("taxDetails should have three elements.", taxDetails.size() == 3);

    }

    /**
     * This method tests if general ledger entries are created correctly when the receivable is set to use the Chart of Accounts
     * Code
     */
    public void testGenerateGeneralLedgerPendingEntries_ReceivableChart() throws WorkflowException {
        // get document with GLPE's generated
        CustomerCreditMemoDocument doc = getCustomerCreditMemoDocumentWithGLPEs(CustomerInvoiceDocumentFixture.BASE_CIDOC_WITH_CUSTOMER, CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE);

        // check the receivable
        CustomerInvoiceDetail testCustomerInvoiceDetail = CustomerInvoiceDetailFixture.CUSTOMER_INVOICE_DETAIL_CHART_RECEIVABLE.createCustomerInvoiceDetail();
        testCustomerInvoiceDetail.refreshReferenceObject("chart");
        String receivableChartOfAccountsCode = testCustomerInvoiceDetail.getChartOfAccountsCode();
        String receivableAccountNumber = testCustomerInvoiceDetail.getAccountNumber();
        String receivableSubAccountNumber = testCustomerInvoiceDetail.getSubAccountNumber();
        String receivableFinancialObjectCode = testCustomerInvoiceDetail.getChart().getFinAccountsReceivableObjCode();
        String receivableFinancialSubObjectCode = GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode(); //TODO What should this value really be?
        String receivableProjectCode = testCustomerInvoiceDetail.getProjectCode();
        String receivableOrgRefId = testCustomerInvoiceDetail.getOrganizationReferenceId();

        checkReceivableGeneralLedgerPendingEntries(doc, receivableChartOfAccountsCode, receivableAccountNumber, receivableSubAccountNumber, receivableFinancialObjectCode, receivableFinancialSubObjectCode, receivableProjectCode, receivableOrgRefId);
    }

    /**
     * This method returns a Customer Credit Memo Document with generated GLPE's based on the receivable offset generation method
     * specified
     *
     * @param receivableOffsetGenerationMethodValue
     * @param customerInvoiceDocumentFixture
     * @return
     */
    public CustomerCreditMemoDocument getCustomerCreditMemoDocumentWithGLPEs(CustomerInvoiceDocumentFixture customerInvoiceDocumentFixture, CustomerInvoiceDetailFixture customerInvoiceDetailFixture) throws WorkflowException {
        // create Customer Invoice Document
        CustomerInvoiceDetailFixture[] customerInvoiceDetailFixtures = new CustomerInvoiceDetailFixture[]{customerInvoiceDetailFixture};
        CustomerInvoiceDocument invoice = customerInvoiceDocumentFixture.createCustomerInvoiceDocument(customerInvoiceDetailFixtures);

        // create/populate Customer Credit Memo Document
        CustomerCreditMemoDocument customerCreditMemoDocument = createCustomerCreditMemoDocument(invoice);

        // generate general pending entries
        SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(customerCreditMemoDocument);

        return customerCreditMemoDocument;
    }

    /**
     * This method creates a customer credit memo document based on the passed in customer invoice document
     *
     * @param customer invoice document
     * @return
     */
    public CustomerCreditMemoDocument createCustomerCreditMemoDocument(CustomerInvoiceDocument invoice) {

        CustomerCreditMemoDetail customerCreditMemoDetail;
        CustomerCreditMemoDocument customerCreditMemoDocument = null;
        KualiDecimal invItemTaxAmount, itemAmount;
        Integer itemLineNumber;
        BigDecimal itemQuantity;
        String documentNumber;

        try {
            // the document header is created and set here
            customerCreditMemoDocument = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CustomerCreditMemoDocument.class);
        } catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }

        customerCreditMemoDocument.setInvoice(invoice); // invoice, not sure I need to set it at all for this test
        customerCreditMemoDocument.setFinancialDocumentReferenceInvoiceNumber(invoice.getDocumentNumber());

        List<CustomerInvoiceDetail> customerInvoiceDetails = invoice.getCustomerInvoiceDetailsWithoutDiscounts();
        if (customerInvoiceDetails == null) {
            return customerCreditMemoDocument;
        }

        for (CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDetails) {
            customerCreditMemoDetail = new CustomerCreditMemoDetail();
            customerCreditMemoDetail.setDocumentNumber(customerCreditMemoDocument.getDocumentNumber());

            invItemTaxAmount = customerInvoiceDetail.getInvoiceItemTaxAmount();
            if (invItemTaxAmount == null) {
                invItemTaxAmount = KualiDecimal.ZERO;
            }
            customerCreditMemoDetail.setCreditMemoItemTaxAmount(invItemTaxAmount.divide(new KualiDecimal(2)));

            customerCreditMemoDetail.setReferenceInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
            itemQuantity = customerInvoiceDetail.getInvoiceItemQuantity().divide(new BigDecimal(2));
            customerCreditMemoDetail.setCreditMemoItemQuantity(itemQuantity);

            itemAmount = customerInvoiceDetail.getAmount().divide(new KualiDecimal(2));
            customerCreditMemoDetail.setCreditMemoItemTotalAmount(itemAmount);
            customerCreditMemoDetail.setFinancialDocumentReferenceInvoiceNumber(invoice.getDocumentNumber());
            customerCreditMemoDetail.setCustomerInvoiceDetail(customerInvoiceDetail);
            customerCreditMemoDocument.getCreditMemoDetails().add(customerCreditMemoDetail);
        }
        return customerCreditMemoDocument;
    }

    /**
     * This method checks the customer credit memo document state tax, district tax GLPE entries
     * <p>
     * TODO add tests for tax GLPEs
     *
     * @param income
     * @param testCustomerInvoiceDetail
     */
    public void checkBasicGeneralLedgerPendingEntries(CustomerCreditMemoDocument doc, CustomerInvoiceDetail testCustomerInvoiceDetail) {
        GeneralLedgerPendingEntry income = doc.getGeneralLedgerPendingEntries().get(1);

        //TODO: mock the tax service for Olga

        assertEquals("Income Chart of Accounts Code should be " + testCustomerInvoiceDetail.getChartOfAccountsCode() + " but is actually " + income.getChartOfAccountsCode(), testCustomerInvoiceDetail.getChartOfAccountsCode(), income.getChartOfAccountsCode());
        assertEquals("Income Account Number should be " + testCustomerInvoiceDetail.getAccountNumber() + " but is actually " + income.getAccountNumber(), testCustomerInvoiceDetail.getAccountNumber(), income.getAccountNumber());
        assertEquals("Income Sub Account Number should be " + testCustomerInvoiceDetail.getSubAccountNumber() + " but is actually " + income.getSubAccountNumber(), testCustomerInvoiceDetail.getSubAccountNumber(), income.getSubAccountNumber());
        assertEquals("Income Financial Object Code should be " + testCustomerInvoiceDetail.getFinancialObjectCode() + " but is actually " + income.getFinancialObjectCode(), testCustomerInvoiceDetail.getFinancialObjectCode(), income.getFinancialObjectCode());
        assertEquals("Income Financial Sub Object Code should be " + testCustomerInvoiceDetail.getFinancialSubObjectCode() + " but is actually " + income.getFinancialSubObjectCode(), testCustomerInvoiceDetail.getFinancialSubObjectCode(), income.getFinancialSubObjectCode());
        assertEquals("Income Project Code should be " + testCustomerInvoiceDetail.getProjectCode() + " but is actually " + income.getProjectCode(), testCustomerInvoiceDetail.getProjectCode(), income.getProjectCode());
        assertEquals("Income Org Ref Id should be " + testCustomerInvoiceDetail.getOrganizationReferenceId() + " but is actually " + income.getOrganizationReferenceId(), testCustomerInvoiceDetail.getOrganizationReferenceId(), income.getOrganizationReferenceId());
    }

    /**
     * This method tests if the passed in receivable GLPE is equal to the passed in expected receivable chart, account number, sub account number
     * object code, sub object code, project code, and org ref id.
     *
     * @param receivable
     * @param expectedReceivableChartOfAccountsCode
     * @param expectedReceivableAccountNumber
     * @param expectedReceivableSubAccountNumber
     * @param expectedReceivableFinancialObjectCode
     * @param expectedReceivableFinancialSubObjectCode
     * @param expectedReceivableProjectCode
     * @param expectedReceivableOrgRefId
     */
    public void checkReceivableGeneralLedgerPendingEntries(CustomerCreditMemoDocument doc,
                                                           String expectedReceivableChartOfAccountsCode,
                                                           String expectedReceivableAccountNumber,
                                                           String expectedReceivableSubAccountNumber,
                                                           String expectedReceivableFinancialObjectCode,
                                                           String expectedReceivableFinancialSubObjectCode,
                                                           String expectedReceivableProjectCode,
                                                           String expectedReceivableOrgRefId) {
        // receivable is always at index 0
        List<GeneralLedgerPendingEntry> glpEntries = doc.getGeneralLedgerPendingEntries();
        GeneralLedgerPendingEntry receivable = glpEntries.get(0);
        assertEquals("Receivable Chart of Accounts Code should be " + expectedReceivableChartOfAccountsCode + " but is actually " + receivable.getChartOfAccountsCode(), expectedReceivableChartOfAccountsCode, receivable.getChartOfAccountsCode());
        assertEquals("Receivable Account Number should be " + expectedReceivableChartOfAccountsCode + " but is actually " + receivable.getAccountNumber(), expectedReceivableAccountNumber, receivable.getAccountNumber());
        assertEquals("Receivable Financial Object Code should be " + expectedReceivableFinancialObjectCode + " but is actually " + receivable.getFinancialObjectCode(), expectedReceivableFinancialObjectCode, receivable.getFinancialObjectCode());
        assertEquals("Receivable Sub Account Number should be " + expectedReceivableSubAccountNumber + " but is actually " + receivable.getSubAccountNumber(), expectedReceivableSubAccountNumber, receivable.getSubAccountNumber());
        assertEquals("Receivable Financial Sub Object Code should be " + expectedReceivableFinancialSubObjectCode + " but is actually " + receivable.getFinancialSubObjectCode(), expectedReceivableFinancialSubObjectCode, receivable.getFinancialSubObjectCode());
        //assertEquals("Receivable Project Code should be " + expectedReceivableProjectCode + " but is actually " + receivable.getProjectCode(), expectedReceivableProjectCode, receivable.getProjectCode());
        assertEquals("Receivable Org Ref Id should be " + expectedReceivableOrgRefId + " but is actually " + receivable.getOrganizationReferenceId(), expectedReceivableOrgRefId, receivable.getOrganizationReferenceId());
    }
}

