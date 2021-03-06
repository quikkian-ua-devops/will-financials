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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;

import java.util.HashMap;

/**
 * Contains logic for use by the individual AccountsPayable documents
 */
public interface AccountsPayableService {

    /**
     * Generates a list of continuation accounts for expired or closed accounts as well as a list of expired or closed
     * accounts with no continuation accounts.
     *
     * @param document The accounts payable document whose accounts we are trying to retrieve.
     * @return A HashMap where the keys are the string representations of the chart and account of the
     * original account and the values are the ExpiredOrClosedAccountEntry.
     */
    public HashMap<String, ExpiredOrClosedAccountEntry> getExpiredOrClosedAccountList(AccountsPayableDocument document);

    /**
     * Generates a note of where continuation accounts were used and adds them as a note to the document.
     *
     * @param document                   The accounts payable document to which we're adding the notes.
     * @param expiredOrClosedAccountList The HashMap where the keys are the string representations of the chart and
     *                                   account of the original account and the values are the ExpiredOrClosedAccountEntry.
     */
    public void generateExpiredOrClosedAccountNote(AccountsPayableDocument document, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList);

    /**
     * Adds a warning message to the message list if expired or closed accounts have been used on the document and the
     * document is not in any of these state: Initiate, In Process or Awaiting Accounts Payable Review and the
     * current user is a fiscal user.
     *
     * @param document The accounts payable document to which we're adding the warning message.
     */
    public void generateExpiredOrClosedAccountWarning(AccountsPayableDocument document);

    /**
     * Performs the replacement of an expired/closed account with a continuation account.
     *
     * @param acctLineBase               The accounting line whose chart and account we're going to replace.
     * @param expiredOrClosedAccountList The HashMap where the keys are the string representations of the chart
     *                                   and account of the original account and the values are the ExpiredOrClosedAccountEntry.
     */
    public void processExpiredOrClosedAccount(PurApAccountingLineBase acctLineBase, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList);

    /**
     * This method cancels a document, it uses DocumentSpecificService to call the actual logic on the PaymentRequestService
     * or CreditMemoService as appropriate.  In certain cases it will also reopen a closed PurchaseOrderDocument
     *
     * @param apDocument      The accounts payable document to be canceled.
     * @param currentNodeName The string representing the current node, which we'll need when we
     *                        want to update the document status by node.  Note: if this is blank it is assumed
     *                        the request is not coming from workflow.
     */
    public void cancelAccountsPayableDocument(AccountsPayableDocument apDocument, String currentNodeName);

    /**
     * This method cancels an AccountsPayableDocument according to the document status.
     *
     * @param apDocument The accounts payable document to be canceled.
     * @param noteText   Notes users input when canceling the document
     */
    public void cancelAccountsPayableDocumentByCheckingDocumentStatus(AccountsPayableDocument apDocument, String noteText) throws Exception;

    /**
     * Updates the item list based on what's eligible to be payed on purchase order.
     *
     * @param apDocument The accounts payable document containing the items to be updated.
     */
    public void updateItemList(AccountsPayableDocument apDocument);

    /**
     * Determines if item is eligible for payment.
     *
     * @param poi The purchase order item whose eligibility for payment is to be determined.
     * @return boolean true if the item is eligible for payment.
     */
    public boolean purchaseOrderItemEligibleForPayment(PurchaseOrderItem poi);

    /**
     * Performs all the actions on an update document.
     *
     * @param purapDocument PurchasingAccountsPayableDocument
     */
    public void performLogicForFullEntryCompleted(PurchasingAccountsPayableDocument purapDocument);


    public HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountsList(PurchaseOrderDocument po);

    /**
     * checks if an accounting line with zero dollar amount can be copied from PO to PREQ.  This will check
     * the system parameter COPY_ACCOUNTING_LINES_WITH_ZERO_AMOUNT_FROM_PO_TO_PREQ_IND and determines if the
     * line can be copied or not.
     *
     * @return true if the system parameter value is Y else returns N.
     */
    public boolean canCopyAccountingLinesWithZeroAmount();
}
