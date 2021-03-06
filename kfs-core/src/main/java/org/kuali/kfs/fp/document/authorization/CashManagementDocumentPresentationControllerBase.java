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
package org.kuali.kfs.fp.document.authorization;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.kfs.krad.service.MaintenanceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.LedgerPostingDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.ValidActions;

import java.util.Set;

public class CashManagementDocumentPresentationControllerBase extends LedgerPostingDocumentPresentationControllerBase implements CashManagementDocumentPresentationController {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        if (!this.canHaveBankEntry(document)) {
            editModes.add(KFSConstants.BANK_ENTRY_VIEWABLE_EDITING_MODE);
        }

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isSaved()) {
            editModes.add(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_CANCEL_DEPOSITS);

            CashManagementDocument cashManagementDocument = (CashManagementDocument) document;
            if (!cashManagementDocument.hasFinalDeposit()) {
                editModes.add(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_ADDITIONAL_DEPOSITS);
            }
        }

        return editModes;
    }

    /**
     * @see org.kuali.kfs.krad.document.authorization.DocumentPresentationControllerBase#canApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canApprove(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isEnroute()) {
            ValidActions validActions = workflowDocument.getValidActions();
            return validActions.getValidActions().contains(ActionType.APPROVE);
        }

        return super.canApprove(document);
    }

    /**
     * @see org.kuali.kfs.krad.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canBlanketApprove(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
            CashManagementDocument cmDoc = (CashManagementDocument) document;
            if (!cmDoc.hasFinalDeposit() || !SpringContext.getBean(CashManagementService.class).allVerifiedCashReceiptsAreDeposited(cmDoc)) {
                return false;
            }

            // CM document can only be routed if it contains a Final Deposit
            ValidActions validActions = workflowDocument.getValidActions();
            return validActions.getValidActions().contains(ActionType.BLANKET_APPROVE);
        }

        return super.canBlanketApprove(document);
    }

    /**
     * @see org.kuali.kfs.krad.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCancel(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested()) {
            CashManagementDocument cmDoc = (CashManagementDocument) document;
            if (!SpringContext.getBean(CashManagementService.class).allowDocumentCancellation(cmDoc)) {
                return false;
            }

            // CM document can only be routed if it contains a Final Deposit
            ValidActions validActions = workflowDocument.getValidActions();
            return validActions.getValidActions().contains(ActionType.CANCEL);
        }

        return super.canCancel(document);
    }

    /**
     * @see org.kuali.kfs.krad.document.authorization.DocumentPresentationControllerBase#canDisapprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canDisapprove(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isEnroute()) {
            ValidActions validActions = workflowDocument.getValidActions();
            return validActions.getValidActions().contains(ActionType.DISAPPROVE);
        }

        return super.canDisapprove(document);
    }

    /**
     * @see org.kuali.kfs.krad.document.authorization.DocumentPresentationControllerBase#canRoute(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canRoute(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested()) {
            CashManagementDocument cmDoc = (CashManagementDocument) document;
            if (!cmDoc.hasFinalDeposit() || !SpringContext.getBean(CashManagementService.class).allVerifiedCashReceiptsAreDeposited(cmDoc)) {
                return false;
            }

            // CM document can only be routed if it contains a Final Deposit
            ValidActions validActions = workflowDocument.getValidActions();
            return validActions.getValidActions().contains(ActionType.ROUTE);
        }

        return super.canRoute(document);
    }

    /**
     * @see org.kuali.kfs.krad.document.authorization.DocumentPresentationControllerBase#canSave(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canSave(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested()) {
            CashManagementDocument cmDoc = (CashManagementDocument) document;
            if (cmDoc.getCashDrawerStatus() == null || cmDoc.getCashDrawerStatus().equals(CashDrawerConstants.STATUS_CLOSED)) {
                return false;
            }

            // CM document can only be saved (via the save button) if the CashDrawer is not closed
            ValidActions validActions = workflowDocument.getValidActions();
            return validActions.getValidActions().contains(ActionType.SAVE);
        }

        return super.canRoute(document);
    }

    /**
     * @see org.kuali.kfs.krad.document.authorization.DocumentPresentationControllerBase#canAdHocRoute(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canAddAdhocRequests(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isEnroute()) {
            ValidActions validActions = workflowDocument.getValidActions();
            return validActions.getValidActions().contains(ActionType.ADHOC_REQUEST);
        }

        return super.canAddAdhocRequests(document);
    }

    /**
     * Determines if the cash drawer can be opened by testing two things:
     * <ol>
     * <li>That the cash drawer is currently closed.</li>
     * <li>That no cash drawer maintenance documents have a lock on the cash drawer.</li>
     * </ol>
     *
     * @param document the document that wishes to open the cash drawer
     * @return true if the cash drawer can be opened, false otherwise
     */
    @Override
    public boolean canOpenCashDrawer(Document document) {
        final CashDrawer cashDrawer = retrieveCashDrawer(document);
        return cashDrawer.isClosed() && noExistCashDrawerMaintLocks(cashDrawer, document.getDocumentNumber());
    }

    /**
     * Retrieves the cash drawer associated with the given cash management document
     *
     * @param document a CashManagementDocument with an associated cash drawer
     * @return the associated cash drawer
     */
    protected CashDrawer retrieveCashDrawer(Document document) {
        final CashManagementDocument cmDoc = (CashManagementDocument) document;
        final CashDrawer cashDrawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(cmDoc.getCampusCode());
        return cashDrawer;
    }

    /**
     * Determines that no maintenance documents have locks on the given cash drawer
     *
     * @param cashDrawer the cash drawer that may have locks on it
     * @return true if there are no maintenance documents with locks on the cash drawer, false otherwise
     */
    protected boolean noExistCashDrawerMaintLocks(CashDrawer cashDrawer, String documentNumber) {
        final MaintenanceDocumentEntry cashDrawerMaintDocEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(cashDrawer.getClass());
        Maintainable cashDrawerMaintainable = createCashDrawerMaintainable(cashDrawerMaintDocEntry);
        cashDrawerMaintainable.setDataObjectClass(cashDrawer.getClass());
        cashDrawerMaintainable.setDataObject(cashDrawer);
        cashDrawerMaintainable.setDocumentNumber(documentNumber);

        final String lockingDocument = SpringContext.getBean(MaintenanceDocumentService.class).getLockingDocumentId(cashDrawerMaintainable, documentNumber);
        return StringUtils.isBlank(lockingDocument);
    }

    /**
     * Builds an instance of the appropriate Maintainable implementation for the Cash Drawer Maintainable
     *
     * @param cashDrawerMaintenanceDocumentEntry the data dictionary entry from the Cash Drawer's maintenance document
     * @return an appropriate Maintainable
     */
    protected Maintainable createCashDrawerMaintainable(MaintenanceDocumentEntry cashDrawerMaintenanceDocumentEntry) {
        Maintainable cashDrawerMaintainable;
        try {
            cashDrawerMaintainable = cashDrawerMaintenanceDocumentEntry.getMaintainableClass().newInstance();
        } catch (InstantiationException ie) {
            throw new RuntimeException("Cannot instantiate instance of maintainable implementation " + cashDrawerMaintenanceDocumentEntry.getMaintainableClass().getName(), ie);
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("Illegal access occurred while instantiating instance of maintainable implementation " + cashDrawerMaintenanceDocumentEntry.getMaintainableClass().getName(), iae);
        }
        return cashDrawerMaintainable;
    }

    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);

        if (!canHaveBankEntry(document)) {
            documentActions.add(KFSConstants.KFS_ACTION_CAN_EDIT_BANK);
        }

        return documentActions;
    }
}
