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

import org.kuali.kfs.kns.web.ui.ExtraButton;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.web.struts.PurchaseOrderForm;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rorenfro;

/**
 * This class is used to test the authorization of the
 * buttons in Purchase Order Documents. It will invoke the canXXX
 * methods in PurchaseOrderDocumentActionAuthorizer to
 * test whether certain buttons could be displayed.
 */
@ConfigureContext(session = parke)
public class PurchaseOrderDocumentActionAuthorizerTest extends KualiTestBase {

    private PurchaseOrderDocument purchaseOrderDocument = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        purchaseOrderDocument = null;
        super.tearDown();
    }

    // The tests in this class have apparently been commented out because of the removal of PurchaseOrderDocumentActionAuthorizer.
    public void testPlaceHolder() {
        assertTrue(true);
    }

//    /**
//     * Tests that the retransmit button is displayed.
//     *
//     * @throws Exception
//     */
//    @ConfigureContext(session = parke, shouldCommitTransactions=true)
//    public final void testValidForRetransmit() throws Exception {
//        Map editMode = new HashMap();
//        Map documentActions = new HashMap();
//        PurchaseOrderDocument po = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_RETRANSMIT.createPurchaseOrderDocument();
//        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(po, editMode, documentActions);
//        assertTrue(auth.canRetransmit());
//    }
//

    /**
     * Tests that the print retransmit button is displayed when the purchase order
     * is not an APO. It should allow purchasing users (in this case we use parke)
     * to see the button if the purchase order is not an APO.
     *
     * @throws Exception
     */
    @ConfigureContext(session = parke, shouldCommitTransactions = false)
    public final void DISABLED_502_testValidForPrintingRetransmitNonAPO() throws Exception {
        Map editMode = new HashMap();
        Map documentActions = new HashMap();

        editMode.put(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB, true);
        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_RETRANSMIT.createPurchaseOrderDocument();
        poDocument.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForDocumentApproval(poDocument.getDocumentNumber());
        assertTrue("Document should now be final.", poDocument.getDocumentHeader().getWorkflowDocument().isFinal());

        PurchaseOrderService purchaseOrderService = SpringContext.getBean(PurchaseOrderService.class);
        PurchaseOrderDocument poRetransmitDocument = purchaseOrderService.createAndRoutePotentialChangeDocument(poDocument.getDocumentNumber(), PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, null, null, "RTPE");
        poRetransmitDocument.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS);
        PurchaseOrderForm poForm = new PurchaseOrderForm();
        poForm.setDocument(poRetransmitDocument);
        poForm.setEditingMode(editMode);
        boolean buttonFound = false;
        for (ExtraButton button : poForm.getExtraButtons()) {
            if (button.getExtraButtonProperty().equals("methodToCall.printingRetransmitPo")) {
                buttonFound = true;
                break;
            }
        }
        assertTrue(buttonFound);
    }

    /**
     * Tests that the print retransmit button is displayed when the purchase order is an
     * APO and the user can be anyone (here it is set as rorenfro prior to checking for the authorizer).
     * It should allow anyone to see the print retransmit button if it's an APO.
     *
     * @throws Exception
     */
    @ConfigureContext(session = parke, shouldCommitTransactions = false)
    public final void DISABLED_502_testValidForPrintingRetransmitAPO() throws Exception {
        Map editMode = new HashMap();
        Map documentActions = new HashMap();
        editMode.put(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB, true);

        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_RETRANSMIT.createPurchaseOrderDocument();
        poDocument.prepareForSave();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForDocumentApproval(poDocument.getDocumentNumber());
        assertTrue("Document should now be final.", poDocument.getDocumentHeader().getWorkflowDocument().isFinal());

        PurchaseOrderService purchaseOrderService = SpringContext.getBean(PurchaseOrderService.class);
        PurchaseOrderDocument poRetransmitDocument = purchaseOrderService.createAndRoutePotentialChangeDocument(poDocument.getDocumentNumber(), PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, null, null, "RTPE");
        poRetransmitDocument.setApplicationDocumentStatus(PurchaseOrderStatuses.APPDOC_CHANGE_IN_PROCESS);
        poRetransmitDocument.setPurchaseOrderAutomaticIndicator(true);
        changeCurrentUser(rorenfro);
        PurchaseOrderForm poForm = new PurchaseOrderForm();
        poForm.setDocument(poRetransmitDocument);
        poForm.setEditingMode(editMode);
        boolean buttonFound = false;
        for (ExtraButton button : poForm.getExtraButtons()) {
            if (button.getExtraButtonProperty().equals("methodToCall.printingRetransmitPo")) {
                buttonFound = true;
                break;
            }
        }
        assertTrue(buttonFound);
    }

//    /**
//     * Tests that the print button for first transmit is displayed.
//     *
//     * @throws Exception
//     */
//    @ConfigureContext(session = parke, shouldCommitTransactions=true)
//    public final void testFirstTransmitPrintPO() throws Exception {
//        Map editMode = new HashMap();
//        Map documentActions = new HashMap();
//
//        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_FIRST_TRANSMIT_PRINT.createPurchaseOrderDocument();
//        DocumentService documentService = SpringContext.getBean(DocumentService.class);
//        poDocument.prepareForSave();
//        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
//        WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), DocumentStatus.FINAL);
//        assertTrue("Document should now be final.", poDocument.getDocumentHeader().getWorkflowDocument().isFinal());
//
//        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode, documentActions);
//        assertTrue(auth.canFirstTransmitPrintPo());
//    }
//
//    /**
//     * Tests that the open order button is displayed.
//     *
//     * @throws Exception
//     */
//    @ConfigureContext(session = parke, shouldCommitTransactions=true)
//    public final void testReopen() throws Exception {
//        Map editMode = new HashMap();
//        Map documentActions = new HashMap();
//
//        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_REOPEN.createPurchaseOrderDocument();
//        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode, documentActions);
//        assertTrue(auth.canReopen());
//    }
//
//    /**
//     * Tests that the close order button is displayed.
//     *
//     * @throws Exception
//     */
//    @ConfigureContext(session = parke, shouldCommitTransactions=true)
//    public final void testClose() throws Exception {
//        Map editMode = new HashMap();
//        Map documentActions = new HashMap();
//
//        DocumentService documentService = SpringContext.getBean(DocumentService.class);
//        //We create and save this req to obtain a number from the AP document link identifier sequencer in the database
//        RequisitionDocument dummyReqDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        documentService.saveDocument(dummyReqDocument);
//        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_CLOSE.createPurchaseOrderDocument();
//        poDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(dummyReqDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());
//        poDocument.prepareForSave();
//        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
//        WorkflowTestUtils.waitForStatusChange(poDocument.getDocumentHeader().getWorkflowDocument(), DocumentStatus.FINAL);
//        changeCurrentUser(appleton);
//        PaymentRequestDocument preq = PaymentRequestDocumentFixture.PREQ_FOR_PO_CLOSE_DOC.createPaymentRequestDocument();
//        preq.setPurchaseOrderIdentifier(poDocument.getPurapDocumentIdentifier());
//        preq.setProcessingCampusCode("BL");
//        preq.setAccountsPayablePurchasingDocumentLinkIdentifier(poDocument.getAccountsPayablePurchasingDocumentLinkIdentifier());
//        preq.prepareForSave();
//        AccountingDocumentTestUtils.saveDocument(preq, documentService);
//        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode, documentActions);
//        assertTrue(auth.canClose());
//    }
//
//    /**
//     * Tests that the payment hold buttons are displayed.
//     *
//     * @throws Exception
//     */
//    @ConfigureContext(session = parke, shouldCommitTransactions=true)
//    public final void testPaymentHold() throws Exception {
//        Map editMode = new HashMap();
//        Map documentActions = new HashMap();
//
//        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_CLOSE.createPurchaseOrderDocument();
//        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode, documentActions);
//        assertTrue(auth.canHoldPayment());
//    }
//
//    /**
//     * Tests that the void button is displayed when the purchase order
//     * is in Pending Print status.
//     *
//     * @throws Exception
//     */
//    @ConfigureContext(session = parke, shouldCommitTransactions=true)
//    public final void testVoidPendingPrint() throws Exception {
//        Map editMode = new HashMap();
//        Map documentActions = new HashMap();
//
//        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_VOID_PENDING_PRINT.createPurchaseOrderDocument();
//        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode, documentActions);
//        assertTrue(auth.canVoid());
//    }
//
//    /**
//     * Tests that the void button is displayed when the purchase order
//     * is in OPEN status and there is no payment request associated
//     * with the purchase order.
//     *
//     * @throws Exception
//     */
//    @ConfigureContext(session = parke, shouldCommitTransactions=true)
//    public final void testVoidOpenNoPreq() throws Exception {
//        Map editMode = new HashMap();
//        Map documentActions = new HashMap();
//
//        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_VOID_OPEN_NO_PREQ.createPurchaseOrderDocument();
//        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode, documentActions);
//        assertTrue(auth.canVoid());
//    }
//
//    /**
//     * Tests that the remove hold button is displayed.
//     *
//     * @throws Exception
//     */
//    @ConfigureContext(session = parke, shouldCommitTransactions=true)
//    public final void testRemoveHold() throws Exception {
//        Map editMode = new HashMap();
//        Map documentActions = new HashMap();
//
//        PurchaseOrderDocument poDocument = PurchaseOrderForPurchaseOrderDocumentActionAuthorizerFixture.PO_VALID_REMOVE_HOLD.createPurchaseOrderDocument();
//        PurchaseOrderDocumentActionAuthorizer auth = new PurchaseOrderDocumentActionAuthorizer(poDocument, editMode, documentActions);
//        assertTrue(auth.canRemoveHold());
//    }
}

