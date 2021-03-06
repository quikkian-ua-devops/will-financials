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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.util.List;
import java.util.Set;

/**
 * Validates that if a disbursement voucher had special handling turned off at the campus node, an extra note explaining that change has been added.
 */
public class DisbursementVoucherCampusSpecialHandlingValidation extends GenericValidation {
    protected DisbursementVoucherDocument disbursementVoucherDocumentForValidation;
    protected DocumentService documentService;

    public static final String DOCUMENT_EDITOR_ROLE_NAME = "Document Editor";

    /**
     * Carries out the validation
     *
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;

        if (isAtNodeToCheck()) {
            final DisbursementVoucherDocument persistedDocument = getPersistedDisbursementVoucherDocument();
            if (isSpecialHandlingChanged(persistedDocument) && !isNoteAdded()) {
                result = false;
                GlobalVariables.getMessageMap().putError(AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX + "disbVchrSpecialHandlingCode", KFSKeyConstants.ERROR_DV_CAMPUS_TURNED_OFF_SPECIAL_HANDLING_WITHOUT_EXPLANATORY_NOTE, new String[]{});
            }
        }

        return result;
    }

    /**
     * Determines if the DisbursementVoucherDocumentForValidation is at the Campus route node
     *
     * @return true if the document is at the campus route node, false otherwise
     */
    protected boolean isAtNodeToCheck() {
        Set<String> currentNodes = getDisbursementVoucherDocumentForValidation().getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
        return (CollectionUtils.isNotEmpty(currentNodes) && !currentNodes.contains(DisbursementVoucherConstants.RouteLevelNames.PURCHASING));
    }

    /**
     * Retrieves from the persistence store the persisted version of the given document
     *
     * @param document the document to find the persisted version of
     * @return the persisted version of that document
     */
    protected DisbursementVoucherDocument getPersistedDisbursementVoucherDocument() {
        try {
            return (DisbursementVoucherDocument) getDocumentService().getByDocumentHeaderId(getDisbursementVoucherDocumentForValidation().getDocumentNumber());
        } catch (WorkflowException we) {
            throw new RuntimeException("Could not retrieve persisted version of document " + getDisbursementVoucherDocumentForValidation().getDocumentNumber() + " for Special Handling validation", we);
        }
    }

    /**
     * Determines if special handling was turned off from the DisbursementVoucherDocumentForValidation
     *
     * @param persistedDocument the persisted version of the document
     * @return true if special handling was turned off, false otherwise
     */
    protected boolean isSpecialHandlingChanged(DisbursementVoucherDocument persistedDocument) {
        return persistedDocument.isDisbVchrSpecialHandlingCode() != getDisbursementVoucherDocumentForValidation().isDisbVchrSpecialHandlingCode();
    }

    /**
     * Determines if another note was added from the time the DisbursementVoucherDocumentForValidation was persisted
     *
     * @param persistedDocument the persisted version of the document
     * @return true if an extra note was added, false otherwise
     */
    protected boolean isNoteAdded() {
        boolean foundNoteByCurrentApprover = false;
        int count = 0;
        final int noteCount = getDisbursementVoucherDocumentForValidation().getNotes().size();
        while (!foundNoteByCurrentApprover && count < noteCount) {
            foundNoteByCurrentApprover |= noteAddedByApproverForCurrentNode(getDisbursementVoucherDocumentForValidation().getNote(count));
            count += 1;
        }
        return foundNoteByCurrentApprover;
    }

    /**
     * Determines if the given note was added by the current approver
     *
     * @param note the note to see added
     * @return true if the note was added by the current approver, false otherwise
     */
    protected boolean noteAddedByApproverForCurrentNode(Note note) {
        List<ActionRequest> actionRequests = null;
        try {
            Set<String> currentNodes = getDisbursementVoucherDocumentForValidation().getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
            if (CollectionUtils.isNotEmpty(currentNodes)) {
                actionRequests = KewApiServiceLocator.getWorkflowDocumentService().getActionRequestsForPrincipalAtNode(getDisbursementVoucherDocumentForValidation().getDocumentNumber(), currentNodes.iterator().next(), note.getAuthorUniversalIdentifier());
            }
        } catch (NumberFormatException nfe) {
            throw new RuntimeException("Could not convert Disbursement Voucher document number " + getDisbursementVoucherDocumentForValidation().getDocumentNumber() + " to long", nfe);
        }
        return actionRequests != null && !actionRequests.isEmpty();
    }

    /**
     * Determines the count of notes on the given document
     *
     * @param dvDoc a document to find the count of notes on
     * @return the count of notes on the document
     */
    protected int getNoteCount(DisbursementVoucherDocument dvDoc) {
        return dvDoc.getNotes().size();
    }

    /**
     * Gets the disbursementVoucherDocumentForValidation attribute.
     *
     * @return Returns the disbursementVoucherDocumentForValidation.
     */
    public DisbursementVoucherDocument getDisbursementVoucherDocumentForValidation() {
        return disbursementVoucherDocumentForValidation;
    }

    /**
     * Sets the disbursementVoucherDocumentForValidation attribute value.
     *
     * @param disbursementVoucherDocumentForValidation The disbursementVoucherDocumentForValidation to set.
     */
    public void setDisbursementVoucherDocumentForValidation(DisbursementVoucherDocument disbursementVoucherDocumentForValidation) {
        this.disbursementVoucherDocumentForValidation = disbursementVoucherDocumentForValidation;
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }
}
