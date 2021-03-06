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
package org.kuali.kfs.module.purap.service;

import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;


public interface PurapGeneralLedgerService {

    public final static String CREATE_PAYMENT_REQUEST = "create";
    public final static String CANCEL_PAYMENT_REQUEST = "cancel";
    public final static String MODIFY_PAYMENT_REQUEST = "modify";
    public final static boolean CREATE_CREDIT_MEMO = false;
    public final static boolean CANCEL_CREDIT_MEMO = !CREATE_CREDIT_MEMO;

    /**
     * Customize the given general ledger entry based on the document type.
     *
     * @param purapDocument           Document creating entries
     * @param accountingLine          AccountingLine from document used to create the pending entry
     * @param explicitEntry           GeneralLedgerPendingEntry that has been created with account info
     * @param referenceDocumentNumber Number of the referenced document
     * @param debitCreditCode         String field indicating if the entry is a debit or credit
     * @param docType                 Document type creating the pending entries
     * @param isEncumbrance           Boolean to indicate if the entry is an encumbrance
     */
    public void customizeGeneralLedgerPendingEntry(PurchasingAccountsPayableDocument purapDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry, Integer referenceDocumentNumber, String debitCreditCode, String docType, boolean isEncumbrance);

    /**
     * Generates general ledger pending entries for the creation of a Payment Request
     *
     * @param preq PaymentRequestDocument which holds the accounts to create the entries
     */
    public void generateEntriesCreatePaymentRequest(PaymentRequestDocument preq);

    /**
     * Generates general ledger pending entries for the modification of a Payment Request. No entries will be created if the
     * calculated change is zero (meaning no change was made). Also, no encumbrance entries will be created.
     *
     * @param preq PaymentRequestDocument which holds the accounts to create the entries
     */
    public void generateEntriesModifyPaymentRequest(PaymentRequestDocument preq);

    /**
     * Generates general ledger pending entries for the creation of a Credit Memo
     *
     * @param cm CreditMemoDocument which holds the accounts to create the entries
     */
    public void generateEntriesCreateCreditMemo(VendorCreditMemoDocument cm);

    /**
     * Generates general ledger pending entries for the cancellation of an Accounts Payable document.
     *
     * @param apDocument AccountsPayableDocument which holds the accounts to create the entries for the cancellation
     */
    public void generateEntriesCancelAccountsPayableDocument(AccountsPayableDocument apDocument);

    /**
     * Generates general ledger pending entries for the amendment of a Purchase Order
     *
     * @param po PurchaseOrderDocument which holds the accounts to create the entries
     */
    public void generateEntriesApproveAmendPurchaseOrder(PurchaseOrderDocument po);

    /**
     * Generates general ledger pending entries for when a Purchase Order is closed which will disencumber all the remaining
     * encumbrances
     *
     * @param po PurchaseOrderDocument which holds the accounts to create the entries
     */
    public void generateEntriesClosePurchaseOrder(PurchaseOrderDocument po);

    /**
     * Generates general ledger pending entries for when a Purchase Order is reopened which will calculate the funds to be
     * re-encumbered
     *
     * @param po PurchaseOrderDocument which holds the accounts to create the entries
     */
    public void generateEntriesReopenPurchaseOrder(PurchaseOrderDocument po);

    /**
     * Generates general ledger pending entries for when a Purchase Order is voided
     *
     * @param po PurchaseOrderDocument which holds the accounts to create the entries
     */
    public void generateEntriesVoidPurchaseOrder(PurchaseOrderDocument po);

}
