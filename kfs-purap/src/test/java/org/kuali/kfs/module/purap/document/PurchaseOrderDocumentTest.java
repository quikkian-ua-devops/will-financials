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
package org.kuali.kfs.module.purap.document;

import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderItemAccountsFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.DocumentStatus;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.kfs.module.purap.fixture.PurchaseOrderItemAccountsFixture.WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT;
import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rjweiss;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rorenfro;

/**
 * Used to create and test populated Purchase Order Documents of various kinds.
 */
@ConfigureContext(session = UserNameFixture.parke)
public class PurchaseOrderDocumentTest extends KualiTestBase {
    public static final Class<PurchaseOrderDocument> DOCUMENT_CLASS = PurchaseOrderDocument.class;

    private List<PurchaseOrderItemAccountsFixture> getItemParametersFromFixtures() {
        List<PurchaseOrderItemAccountsFixture> list = new ArrayList<PurchaseOrderItemAccountsFixture>();
        list.add(WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT);
        return list;
    }

    private int getExpectedPrePeCount() {
        return 0;
    }

    public final void testAddItem() throws Exception {
        List<PurchasingItem> items = new ArrayList<PurchasingItem>();
        for (PurchaseOrderItem item : generateItems()) {
            items.add(item);
        }
        int expectedItemTotal = items.size();
        PurchasingDocumentTestUtils.testAddItem(DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildSimpleDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = UserNameFixture.parke, shouldCommitTransactions = true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(buildSimpleDocument(), getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = parke, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        PurchaseOrderDocument poDocument = buildSimpleDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        poDocument.prepareForSave();
        assertFalse("Document should not have been in ENROUTE status.", DocumentStatus.ENROUTE.equals(poDocument.getDocumentHeader().getWorkflowDocument().getStatus()));
        AccountingDocumentTestUtils.routeDocument(poDocument, "test annotation", null, documentService);
        WorkflowTestUtils.waitForDocumentApproval(poDocument.getDocumentNumber());
        WorkflowDocument workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(poDocument.getDocumentNumber(), UserNameFixture.kfs.getPerson());
        assertTrue("Document should now be final.", workflowDocument.isFinal());
    }

    @ConfigureContext(session = parke, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        PurchaseOrderDocument poDocument = buildSimpleDocument();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        poDocument.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(poDocument, documentService);
        PurchaseOrderDocument result = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocument.getDocumentNumber());
        assertMatch(poDocument, result);
    }

    public final void testHandleNegativeEntryAmount() {
        PurchaseOrderDocument doc = new PurchaseOrderDocument();
        GeneralLedgerPendingEntry entry = new GeneralLedgerPendingEntry();
        entry.setTransactionLedgerEntryAmount(new KualiDecimal(-1));
        doc.handleNegativeEntryAmount(entry);

        assertEquals(new KualiDecimal(1), entry.getTransactionLedgerEntryAmount());
        assertEquals(KFSConstants.GL_CREDIT_CODE, entry.getTransactionDebitCreditCode());
    }

    public final void testHandleNegativeEntryAmountNonNegative() {
        PurchaseOrderDocument doc = new PurchaseOrderDocument();
        GeneralLedgerPendingEntry entry = new GeneralLedgerPendingEntry();
        entry.setTransactionLedgerEntryAmount(new KualiDecimal(1));
        doc.handleNegativeEntryAmount(entry);

        assertEquals(new KualiDecimal(1), entry.getTransactionLedgerEntryAmount());
        assertEquals(KFSConstants.GL_DEBIT_CODE, entry.getTransactionDebitCreditCode());
    }

    // test util methods

    /**
     * Matches two Purchase Order Documents by comparing their most important persistant fields;
     * Fails the assertion if any of these fields don't match.
     */
    public static void assertMatch(PurchaseOrderDocument doc1, PurchaseOrderDocument doc2) {
        // match header
        Assert.assertEquals(doc1.getDocumentNumber(), doc2.getDocumentNumber());
        Assert.assertEquals(doc1.getDocumentHeader().getWorkflowDocument().getDocumentTypeName(), doc2.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());

        // match posting year
        if (StringUtils.isNotBlank(doc1.getPostingPeriodCode()) && StringUtils.isNotBlank(doc2.getPostingPeriodCode())) {
            Assert.assertEquals(doc1.getPostingPeriodCode(), doc2.getPostingPeriodCode());
        }
        Assert.assertEquals(doc1.getPostingYear(), doc2.getPostingYear());

        // match important fields in PO

        Assert.assertEquals(doc1.getVendorHeaderGeneratedIdentifier(), doc2.getVendorHeaderGeneratedIdentifier());
        Assert.assertEquals(doc1.getVendorDetailAssignedIdentifier(), doc2.getVendorDetailAssignedIdentifier());
        Assert.assertEquals(doc1.getVendorName(), doc2.getVendorName());
        Assert.assertEquals(doc1.getVendorNumber(), doc2.getVendorNumber());
        Assert.assertEquals(doc1.getApplicationDocumentStatus(), doc2.getApplicationDocumentStatus());

        Assert.assertEquals(doc1.getChartOfAccountsCode(), doc2.getChartOfAccountsCode());
        Assert.assertEquals(doc1.getOrganizationCode(), doc2.getOrganizationCode());
        Assert.assertEquals(doc1.getDeliveryCampusCode(), doc2.getDeliveryCampusCode());
        Assert.assertEquals(doc1.getDeliveryRequiredDate(), doc2.getDeliveryRequiredDate());
        Assert.assertEquals(doc1.getRequestorPersonName(), doc2.getRequestorPersonName());
        Assert.assertEquals(doc1.getContractManagerCode(), doc2.getContractManagerCode());
        Assert.assertEquals(doc1.getVendorContractName(), doc2.getVendorContractName());
        Assert.assertEquals(doc1.getPurchaseOrderAutomaticIndicator(), doc2.getPurchaseOrderAutomaticIndicator());
        Assert.assertEquals(doc1.getPurchaseOrderTransmissionMethodCode(), doc2.getPurchaseOrderTransmissionMethodCode());

        Assert.assertEquals(doc1.getRequisitionIdentifier(), doc2.getRequisitionIdentifier());
        Assert.assertEquals(doc1.getPurchaseOrderPreviousIdentifier(), doc2.getPurchaseOrderPreviousIdentifier());
        Assert.assertEquals(doc1.isPurchaseOrderCurrentIndicator(), doc2.isPurchaseOrderCurrentIndicator());
        Assert.assertEquals(doc1.getPurchaseOrderCreateTimestamp(), doc2.getPurchaseOrderCreateTimestamp());
        Assert.assertEquals(doc1.getPurchaseOrderLastTransmitTimestamp(), doc2.getPurchaseOrderLastTransmitTimestamp());
    }

    private List<PurchaseOrderItem> generateItems() throws Exception {
        List<PurchaseOrderItem> items = new ArrayList<PurchaseOrderItem>();
        // set items to document
        for (PurchaseOrderItemAccountsFixture itemFixture : getItemParametersFromFixtures()) {
            items.add(itemFixture.populateItem());
        }

        return items;
    }

    public PurchaseOrderDocument buildSimpleDocument() throws Exception {
        return PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
    }

    private UserNameFixture getInitialUserName() {
        return rjweiss;
    }

    protected UserNameFixture getTestUserName() {
        return rorenfro;
    }
}

