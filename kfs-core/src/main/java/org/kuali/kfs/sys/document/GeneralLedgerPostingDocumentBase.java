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
package org.kuali.kfs.sys.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.krad.exception.ValidationException;
import org.kuali.kfs.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.kfs.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for a general ledger posting document.
 */
public class GeneralLedgerPostingDocumentBase extends LedgerPostingDocumentBase implements GeneralLedgerPostingDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerPostingDocumentBase.class);

    protected List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;

    /**
     * Default constructor.
     */
    public GeneralLedgerPostingDocumentBase() {
        super();
        setGeneralLedgerPendingEntries(new ArrayList<GeneralLedgerPendingEntry>());
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#getGeneralLedgerPendingEntries()
     */
    @Override
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#getGeneralLedgerPendingEntry(int)
     */
    @Override
    public GeneralLedgerPendingEntry getGeneralLedgerPendingEntry(int index) {
        while (generalLedgerPendingEntries.size() <= index) {
            generalLedgerPendingEntries.add(new GeneralLedgerPendingEntry());
        }
        return generalLedgerPendingEntries.get(index);
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#setGeneralLedgerPendingEntries(java.util.List)
     */
    @Override
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.generalLedgerPendingEntries = generalLedgerPendingEntries;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#checkSufficientFunds()
     */
    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        LOG.debug("checkSufficientFunds() started");

        if (documentPerformsSufficientFundsCheck()) {
            SufficientFundsService sufficientFundsService = SpringContext.getBean(SufficientFundsService.class);
            return sufficientFundsService.checkSufficientFunds(this);
        } else {
            return new ArrayList<SufficientFundsItem>();
        }
    }

    /**
     * This method checks to see if SF checking should be done for this document. This was originally part of
     * SufficientFundsService.checkSufficientFunds() but was externalized so documents that need to override any of the SF methods
     * can still explicitly check this
     *
     * @return
     */
    public boolean documentPerformsSufficientFundsCheck() {
        // check for reversing entries generated by an error correction.
        return StringUtils.isBlank(this.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber());
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocument#getPendingLedgerEntriesForSufficientFundsChecking()
     */
    @Override
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        return getGeneralLedgerPendingEntries();
    }

    /**
     * Override to call super and then iterate over all GLPEs and update the approved code appropriately.
     *
     * @see Document#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            changeGeneralLedgerPendingEntriesApprovedStatusCode(); // update all glpes for doc and set their status to approved
        }
        // general ledger pending entries are getting removed on ReCall
        else if (getDocumentHeader().getWorkflowDocument().isCanceled() || getDocumentHeader().getWorkflowDocument().isDisapproved() || getDocumentHeader().getWorkflowDocument().isRecalled()) {
            removeGeneralLedgerPendingEntries();
            if (this instanceof ElectronicPaymentClaiming) {
                ((ElectronicPaymentClaiming) this).declaimElectronicPaymentClaims();
            }
        }
    }

    /**
     * This method iterates over all of the GLPEs for a document and sets their approved status code to APPROVED "A".
     */
    protected void changeGeneralLedgerPendingEntriesApprovedStatusCode() {
        for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
            glpe.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
        }
    }

    /**
     * This method calls the service to remove all of the GLPE's associated with this document
     */
    protected void removeGeneralLedgerPendingEntries() {
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        glpeService.delete(getDocumentHeader().getDocumentNumber());
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        getGeneralLedgerPendingEntries().clear();
    }

    /**
     * @see org.kuali.rice.krad.document.TransactionalDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        getGeneralLedgerPendingEntries().clear();
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        super.prepareForSave(event);
        // TODO - add KFS wrappers of Rice Events to list
        if (event instanceof RouteDocumentEvent || event instanceof ApproveDocumentEvent) {
            // generate general ledger pending entries should be called prior to sufficient funds checking
            List<SufficientFundsItem> sfItems = checkSufficientFunds();
            if (!sfItems.isEmpty()) {
                for (SufficientFundsItem sfItem : sfItems) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.SufficientFunds.ERROR_INSUFFICIENT_FUNDS, new String[]{sfItem.getAccount().getChartOfAccountsCode(), sfItem.getAccount().getAccountNumber(), StringUtils.isNotBlank(sfItem.getSufficientFundsObjectCode()) ? sfItem.getSufficientFundsObjectCode() : KFSConstants.NOT_AVAILABLE_STRING, sfItem.getAccountSufficientFundsCode()});
                }
                throw new ValidationException("Insufficient Funds on this Document:");
            }
        }
    }

    /**
     * Adds a GeneralLedgerPendingEntry to this document's list of pending entries
     *
     * @param pendingEntry a pending entry to add
     */
    public void addPendingEntry(GeneralLedgerPendingEntry pendingEntry) {
        pendingEntry.refreshReferenceObject("financialObject");
        generalLedgerPendingEntries.add(pendingEntry);
    }

    /**
     * This resets this document's list of general ledger pending etnries, though it does not delete those entries (however, the GeneralLedgerPendingEntryService will in most cases when this method is called).
     */
    public void clearAnyGeneralLedgerPendingEntries() {
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
    }
}
