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

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants.DisbursementVoucherEditMode;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;

import java.util.Set;


public class DisbursementVoucherDocumentPresentationController extends AccountingDocumentPresentationControllerBase {
    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canBlanketApprove(Document document) {
        return false;
    }

    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);
        documentActions.remove(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);
        return documentActions;
    }

    /**
     * Returns true if DV is approved (FINAL) and hasn't been extracted.
     *
     * @param document
     * @return
     */
    protected boolean canExtractNow(Document document) {
        DisbursementVoucherDocument dvDoc = (DisbursementVoucherDocument) document;
        boolean canExtractNow = true;
        canExtractNow &= dvDoc.getDocumentHeader().getWorkflowDocument().isApproved();
        canExtractNow &= ObjectUtils.isNull(dvDoc.getExtractDate());

        // Note: We don't need to check if the Permission "Use Transactional Document DV extractNow" exists and is active, because we have
        // it set to active by default. If the permission is not defined or is inactive, it means there will be no authorization checking on
        // using this button and thus everyone can use it. Institutions that don't want to allow anyone to use this button should set the
        // role assignments for this permission to inactive or just remove the role assignments.

        return canExtractNow;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TAX_ENTRY);
        editModes.add(KfsAuthorizationConstants.TransactionalEditMode.FRN_ENTRY);
        editModes.add(KfsAuthorizationConstants.TransactionalEditMode.WIRE_ENTRY);
        editModes.add(KfsAuthorizationConstants.TransactionalEditMode.IMMEDIATE_DISBURSEMENT_ENTRY);

        addFullEntryEntryMode(document, editModes);
        addPayeeEditEntryMode(document, editModes);
        addTravelEntryMode(document, editModes);
        addPaymentHandlingEntryMode(document, editModes);
        addVoucherDeadlineEntryMode(document, editModes);
        addSpecialHandlingChagingEntryMode(document, editModes);
        addPaymentReasonEditMode(document, editModes);

        // if DV can be extracted now (based on document status), add the EXTRACT_NOW_ACTION
        if (canExtractNow(document)) {
            editModes.add(DisbursementVoucherEditMode.EXTRACT_NOW);
        }

        addACHSignUpInfoMode(document, editModes);

        return editModes;
    }

    protected void addACHSignUpInfoMode(Document document, Set<String> editModes) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!(workflowDocument.isFinal() ||
            workflowDocument.isProcessed() ||
            workflowDocument.isDisapproved()))
            editModes.add(DisbursementVoucherEditMode.ACH_ACCOUNT_INFO_DISPLAYED);
    }

    protected void addPaymentReasonEditMode(Document document, Set<String> editModes) {
        if (isAtNode(document, DisbursementVoucherConstants.RouteLevelNames.CAMPUS) && document.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYMENT_REASON_EDIT_MODE);
        }
    }

    protected boolean isAtNode(Document document, String nodeName) {
        Set<String> currentNodes = document.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
        return currentNodes.contains(nodeName);
    }


    protected void addPayeeEditEntryMode(Document document, Set<String> editModes) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested())) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYEE_ENTRY);
        } else if (workflowDocument.isEnroute()) {
            Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();
            if (CollectionUtils.isNotEmpty(currentRouteLevels) && (currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.ACCOUNT) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TAX) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.AWARD) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.CAMPUS) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TRAVEL))) {
                editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYEE_ENTRY);
            }
        }
    }

    protected void addFullEntryEntryMode(Document document, Set<String> editModes) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested())) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.FULL_ENTRY);
        }
    }

    /**
     * If at a proper route node, adds the ability to edit payment handling fields
     *
     * @param document  the disbursement voucher document authorization is being sought on
     * @param editModes the edit modes so far, which can be added to
     */
    protected void addPaymentHandlingEntryMode(Document document, Set<String> editModes) {
        final WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested())) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYMENT_HANDLING_ENTRY);
        }
        final Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentRouteLevels) && (currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.ACCOUNT) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.CAMPUS) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TRAVEL) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TAX))) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.PAYMENT_HANDLING_ENTRY);
        }
    }

    /**
     * If at a proper route node, adds the ability to edit the due date for the voucher
     *
     * @param document  the disbursement voucher document authorization is being sought on
     * @param editModes the edit modes so far, which can be added to
     */
    protected void addVoucherDeadlineEntryMode(Document document, Set<String> editModes) {
        final WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if ((workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested())) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.VOUCHER_DEADLINE_ENTRY);
        }
        final Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentRouteLevels) && (currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.ACCOUNT) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.CAMPUS) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TAX) || currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TRAVEL))) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.VOUCHER_DEADLINE_ENTRY);
        }
    }

    /**
     * If at a proper route node, adds the ability to edit the travel information on the disbursement voucher
     *
     * @param document  the disbursement voucher document authorization is being sought on
     * @param editModes the edit modes so far, which can be added to
     */
    protected void addTravelEntryMode(Document document, Set<String> editModes) {
        final WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        final Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentRouteLevels)) {
            if (currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.ACCOUNT)) {  //FO?
            } else if (currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.TAX)) { //tax manager? Then only allow this if we're going to route to travel node anyway
                if (((DisbursementVoucherDocument) document).isTravelReviewRequired()) {
                    editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY);
                }
            } else if (currentRouteLevels.contains(KFSConstants.RouteLevelNames.PAYMENT_METHOD) && ((DisbursementVoucherDocument) document).getDisbVchrPaymentMethodCode().equals(KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_DRAFT)) {
                editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY);
            } else {
                editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY); // we're not FO? Then always add it, as KIM permissions will take it out if we shouldn't have it
            }
        } else {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TRAVEL_ENTRY); // we're not FO? Then always add it, as KIM permissions will take it out if we shouldn't have it
        }
    }

    /**
     * If at a proper route node, adds the ability to edit whether special handling is needed on the disbursement voucher
     *
     * @param document  the disbursement voucher document authorization is being sought on
     * @param editModes the edit modes so far, which can be added to
     */
    protected void addSpecialHandlingChagingEntryMode(Document document, Set<String> editModes) {
        final WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        final Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();

        if (CollectionUtils.isNotEmpty(currentRouteLevels) && !currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.PURCHASING)) {
            editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.SPECIAL_HANDLING_CHANGING_ENTRY);
        }
    }

}
