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

import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;

/**
 * Purchase Order Void Document
 */
public class PurchaseOrderVoidDocument extends PurchaseOrderDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderVoidDocument.class);

    /**
     * General Ledger pending entries are not created on save for this document. They are created when the document has been finally
     * processed. Overriding this method so that entries are not created yet.
     *
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderDocument#prepareForSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isCanceled()) {
            setSourceAccountingLines(new ArrayList());
            setGeneralLedgerPendingEntries(new ArrayList());
        }
    }

    /**
     * When Purchase Order Void document has been processed through Workflow, the general ledger entries are created and the PO
     * status changes to "VOID".
     *
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderDocument#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        try {
            // DOCUMENT PROCESSED
            if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
                // generate GL entries
                SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesVoidPurchaseOrder(this);

                // update indicators
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForApprovedPODocuments(this);

                // set purap status
                updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_VOID);
            }
            // DOCUMENT DISAPPROVED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isDisapproved()) {
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(this);
                // for app doc status
                try {
                    String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(this.getFinancialSystemDocumentHeader().getWorkflowDocument());
                    String reqStatus = PurapConstants.PurchaseOrderStatuses.getPurchaseOrderAppDocDisapproveStatuses().get(nodeName);
                    updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.getPurchaseOrderAppDocDisapproveStatuses().get(reqStatus));
                } catch (WorkflowException e) {
                    logAndThrowRuntimeException("Error saving routing data while saving App Doc Status " + getDocumentNumber(), e);
                }
            }
            // DOCUMENT CANCELED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isCanceled()) {
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledChangePODocuments(this);
                // for app doc status
                updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_CANCELLED);
            }
        } catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    /**
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument,
     * org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);

        SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(this, (AccountingLine) postable, explicitEntry, getPurapDocumentIdentifier(), GL_CREDIT_CODE, PurapDocTypeCodes.PO_DOCUMENT, true);

        // don't think i should have to override this, but default isn't getting the right PO doc
        explicitEntry.setFinancialDocumentTypeCode(PurapDocTypeCodes.PO_VOID_DOCUMENT);
        explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
    }

    @Override
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        List<GeneralLedgerPendingEntrySourceDetail> accountingLines = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        if (getGlOnlySourceAccountingLines() != null) {
            Iterator iter = getGlOnlySourceAccountingLines().iterator();
            while (iter.hasNext()) {
                accountingLines.add((GeneralLedgerPendingEntrySourceDetail) iter.next());
            }
        }
        return accountingLines;
    }
}
