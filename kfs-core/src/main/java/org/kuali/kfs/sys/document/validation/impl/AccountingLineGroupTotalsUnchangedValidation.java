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
package org.kuali.kfs.sys.document.validation.impl;

import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedSaveDocumentEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kew.api.exception.WorkflowException;

/**
 * A validation, used on accounting document approval, that accounting line totals are unchanged
 */
public class AccountingLineGroupTotalsUnchangedValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;

    /**
     * Checks that the source and total amounts on the current version of the accounting document
     * are equal to the persisted source and total totals.
     * <strong>Expects a document to be sent in as the first parameter</strong>
     *
     * @see org.kuali.kfs.sys.document.validation.GenericValidation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        AccountingDocument persistedDocument = null;

        if (event instanceof AttributedSaveDocumentEvent && (!accountingDocumentForValidation.getDocumentHeader().getWorkflowDocument().isEnroute() || accountingDocumentForValidation.getDocumentHeader().getWorkflowDocument().isCompletionRequested())) {
            return true; // only check save document events if the document is enroute
        }

        persistedDocument = retrievePersistedDocument(accountingDocumentForValidation);

        boolean isUnchanged = true;
        if (persistedDocument == null) {
            handleNonExistentDocumentWhenApproving(accountingDocumentForValidation);
        } else {
            // retrieve the persisted totals
            KualiDecimal persistedSourceLineTotal = persistedDocument.getSourceTotal();
            KualiDecimal persistedTargetLineTotal = persistedDocument.getTargetTotal();

            // retrieve the updated totals
            KualiDecimal currentSourceLineTotal = accountingDocumentForValidation.getSourceTotal();
            KualiDecimal currentTargetLineTotal = accountingDocumentForValidation.getTargetTotal();

            // make sure that totals have remained unchanged, if not, recognize that, and
            // generate appropriate error messages
            if (currentSourceLineTotal.compareTo(persistedSourceLineTotal) != 0) {
                isUnchanged = false;

                // build out error message
                buildTotalChangeErrorMessage(KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS, persistedSourceLineTotal, currentSourceLineTotal);
            }

            if (currentTargetLineTotal.compareTo(persistedTargetLineTotal) != 0) {
                isUnchanged = false;

                // build out error message
                buildTotalChangeErrorMessage(KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS, persistedTargetLineTotal, currentTargetLineTotal);
            }
        }

        return isUnchanged;
    }

    /**
     * attempt to retrieve the document from the DB for comparison
     *
     * @param accountingDocument
     * @return AccountingDocument
     */
    protected AccountingDocument retrievePersistedDocument(AccountingDocument accountingDocument) {
        AccountingDocument persistedDocument = null;

        try {
            persistedDocument = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(accountingDocument.getDocumentNumber());
        } catch (WorkflowException we) {
            handleNonExistentDocumentWhenApproving(accountingDocument);
        }

        return persistedDocument;
    }

    /**
     * This method builds out the error message for when totals have changed.
     *
     * @param propertyName
     * @param persistedSourceLineTotal
     * @param currentSourceLineTotal
     */
    protected void buildTotalChangeErrorMessage(String propertyName, KualiDecimal persistedSourceLineTotal, KualiDecimal currentSourceLineTotal) {
        String persistedTotal = (String) new CurrencyFormatter().format(persistedSourceLineTotal);
        String currentTotal = (String) new CurrencyFormatter().format(currentSourceLineTotal);
        GlobalVariables.getMessageMap().putError(propertyName, KFSKeyConstants.ERROR_DOCUMENT_SINGLE_ACCOUNTING_LINE_SECTION_TOTAL_CHANGED, new String[]{persistedTotal, currentTotal});
    }

    /**
     * Handles the case when a non existent document is attempted to be retrieve and that if it's in an initiated state, it's ok.
     *
     * @param accountingDocument
     */
    protected final void handleNonExistentDocumentWhenApproving(AccountingDocument accountingDocument) {
        // check to make sure this isn't an initiated document being blanket approved
        if (!accountingDocument.getDocumentHeader().getWorkflowDocument().isInitiated()) {
            throw new IllegalStateException("Document " + accountingDocument.getDocumentNumber() + " is not a valid document that currently exists in the system.");
        }
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     *
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     *
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
