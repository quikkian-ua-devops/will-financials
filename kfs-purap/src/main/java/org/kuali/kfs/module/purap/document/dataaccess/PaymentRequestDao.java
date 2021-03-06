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
package org.kuali.kfs.module.purap.document.dataaccess;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

/**
 * Payment Request DAO Interface.
 */
public interface PaymentRequestDao {

    /**
     * Get all the payment requests that need to be extracted that match a credit memo.
     *
     * @param campusCode                      - limit results to a single chart
     * @param paymentRequestIdentifier        - Payment Request Identifier (can be null)
     * @param purchaseOrderIdentifier         - PO Identifier (can be null)
     * @param vendorHeaderGeneratedIdentifier - Vendor Header ID
     * @param vendorDetailAssignedIdentifier  - Vendor Detail ID
     * @param currentSqlDateMidnight          current SQL date midnight
     * @return - list of payment requests that need to be extracted
     */
    public List<PaymentRequestDocument> getPaymentRequestsToExtract(String campusCode, Integer paymentRequestIdentifier, Integer purchaseOrderIdentifier, Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Date currentSqlDateMidnight);

    /**
     * Get all the payment requests that need to be extracted that match a credit memo.
     *
     * @param campusCode                      - limit results to a single chart
     * @param vendor                          - Vendor Header ID, Vendor Detail ID, Country, Zip Code
     * @param onOrBeforePaymentRequestPayDate only payment requests with a pay date on or before this value will be returned in the
     *                                        iterator
     * @return - list of payment requests that need to be extracted
     */
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractForVendor(String campusCode, VendorGroupingHelper vendor, Date onOrBeforePaymentRequestPayDate);

    /**
     * Get all the payment requests that need to be extracted to PDP.
     *
     * @param onlySpecialPayments - true only include special payments, False - include all
     * @param chartCode           - if not null, limit results to a single chart
     * @return - Collection of payment requests
     */
    public List<PaymentRequestDocument> getPaymentRequestsToExtract(boolean onlySpecialPayments, String chartCode, Date onOrBeforePaymentRequestPayDate);

    /**
     * Get all the payment requests that are marked immediate that need to be extracted to PDP.
     *
     * @param chartCode - chart of accounts code
     * @return - Collection of payment requests
     */
    public List<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode);

    /**
     * Get all payment request documents that are eligible for auto-approval. Whether or not a document is eligible for
     * auto-approval is determined according to whether or not the document total is below a pre-determined minimum amount. This
     * amount is derived from the accounts, charts and/or organizations associated with a given document. If no minimum amount can
     * be determined from chart associations a default minimum specified as a system parameter is used to determine the minimum
     * amount threshold.
     *
     * @param todayAtMidnight
     * @return - an Iterator over all payment request documents eligible for automatic approval
     */
    public List<String> getEligibleForAutoApproval(Date todayAtMidnight);

    /**
     * Get a payment request document number by id.
     *
     * @param id - PaymentRequest Id
     * @return - PaymentRequest or null if not found
     */
    public String getDocumentNumberByPaymentRequestId(Integer id);

    /**
     * Retrieves a list of document numbers by purchase order id.
     *
     * @param id - purchase order id
     * @return - list of document numbers
     */
    public List<String> getDocumentNumbersByPurchaseOrderId(Integer id);


    /**
     * Retrieves a list of Payment Requests with the given vendor id and invoice number.
     *
     * @param vendorHeaderGeneratedId - header id of the vendor id
     * @param vendorDetailAssignedId  - detail id of the vendor id
     * @param invoiceNumber           - invoice number as entered by AP
     * @return - List of Payment Requests.
     */
    public List getActivePaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId, String invoiceNumber);

    /**
     * Retrieves a list of Payment Requests with the given vendor id and invoice number.
     *
     * @param vendorHeaderGeneratedId - header id of the vendor id
     * @param vendorDetailAssignedId  - detail id of the vendor id
     * @return - List of Payment Requests.
     */
    public List getActivePaymentRequestsByVendorNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId);

    /**
     * Retrieves a list of Payment Requests with the given PO Id, invoice amount, and invoice date.
     *
     * @param poId          - purchase order ID
     * @param invoiceAmount - amount of the invoice as entered by AP
     * @param invoiceDate   - date of the invoice as entered by AP
     * @return - List of Pay Reqs.
     */
    public List getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal invoiceAmount, Date invoiceDate);

    /**
     * Retrieves the number of active payment requests for a purchase order by status code. Active being defined as being
     * enroute and before final, and workflow status not canceled or exception. The issue is that a status of vendor_tax_review
     * may not mean that it's in review, but could be in final (as there isn't a final status code for payment request).
     *
     * @param purchaseOrderId
     * @return
     */
    public int getActivePaymentRequestCountForPurchaseOrder(Integer purchaseOrderId);

    /**
     * Get all payment request which are waiting in receiving status queue
     *
     * @return
     */
    public List<PaymentRequestDocument> getPaymentRequestInReceivingStatus();

}
