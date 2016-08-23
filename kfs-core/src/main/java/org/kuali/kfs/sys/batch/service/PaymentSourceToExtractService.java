/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * Contract of services which will feed PaymentSources to the PaymentSourceExtractionService
 */
public interface PaymentSourceToExtractService<PS extends PaymentSource> {
    /**
     * Retrieves all of the PaymentSource documents which should be extracted in PDP, grouped by campus
     *
     * @param immediatesOnly if true, only payments to be immediately extracted will be retrieved
     * @return a Map, where the key is the campus code and the value is a List of PaymentSource documents to be extracted
     */
    public Map<String, List<PS>> retrievePaymentSourcesByCampus(boolean immediatesOnly);

    /**
     * @return the unit of the customer profile associated with this payment source
     */
    public abstract String getPreDisbursementCustomerProfileUnit();

    /**
     * @return the sub-unit of the customer profile associated with this payment source
     */
    public abstract String getPreDisbursementCustomerProfileSubUnit();

    /**
     * Marks the given document as extracted by PDP
     * @param document the document that has been extracted
     * @param sqlProcessRunDate the date when PDP is asking for the creation of the payment group
     * @param paymentGroupId the id of the payment group that was extracted
     */
    public abstract void markAsExtracted(PS document, Date sqlProcessRunDate, KualiInteger paymentGroupId);

    /**
     * Builds a PaymentGroup which represents a
     * @param document the document to create a PaymentGroup for
     * @param processRunDate the date when PDP is asking for the creation of the payment group
     * @return the generated PaymentGroup
     */
    public abstract PaymentGroup createPaymentGroup(PS document, Date processRunDate);

    /**
     * @return the amount of the payment for the given document
     */
    public abstract KualiDecimal getPaymentAmount(PS document);

    /**
     * Marks the payment source as paid upon the processing date
     * @param processDate the date when this payment source was paid
     */
    public abstract void markAsPaid(PS paymentSource, java.sql.Date processDate);

    /**
     * Marks the payment source as canceled upon the passed-in canceled date
     * @param cancelDate the date when the payment source was canceled
     */
    public abstract void cancelPayment(PS paymentSource, java.sql.Date cancelDate);

    /**
     * Determines if the given pending entry should be rolled back as part of the payment cancellation
     * @param entry the entry to roll back
     * @return true if the entry should be rolled back, false if it should be preserved
     */
    public abstract boolean shouldRollBackPendingEntry(GeneralLedgerPendingEntry entry);

    /**
     * Resets the given PaymentSource so that it seems as if it was not extracted according to values on the document
     */
    public abstract void resetFromExtraction(PS paymentSource);

    /**
     * @return the e-mail address that a notification e-mail, explaining that this payment source has been extracted in the immediate extract process, should be from
     */
    public abstract String getImmediateExtractEMailFromAddress();

    /**
     * @return the e-mail addresses that a notification e-mail, explaining that this payment source has been extracted in the immediate extract process, should be sent to
     */
    public abstract List<String> getImmediateExtractEmailToAddresses();

    /**
     * @return the FSLO document type associated with ach/check entries for this document type
     */
    public abstract String getAchCheckDocumentType(PS paymentSource);

    /**
     * Determines if this PaymentSourceToExtractService will pay or cancel payment details with the given ach check document type
     * @param achCheckDocumentType the ach check document type
     * @return true if this service handles the given document type, false otherwise
     */
    public abstract boolean handlesAchCheckDocumentType(String achCheckDocumentType);

    /**
     * Determines if the given payment source should be extracted
     * @param paymentSource the payment source to determine the extractibility of
     * @return true if the payment source should be extracted, false otherwise
     */
    public abstract boolean shouldExtractPayment(PS paymentSource);
}
