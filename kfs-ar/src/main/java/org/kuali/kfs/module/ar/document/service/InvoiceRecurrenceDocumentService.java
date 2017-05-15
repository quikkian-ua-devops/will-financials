/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

import java.sql.Date;

public interface InvoiceRecurrenceDocumentService {

    /**
     * This method returns true if customer invoice detail amount can be taxed.
     *
     * @param customer
     * @param customerInvoiceDetail
     * @return
     */
    public boolean isCustomerInvoiceDetailTaxable(CustomerInvoiceDocument document, CustomerInvoiceDetail customerInvoiceDetail);

    /**
     * This method returns the appropriate postal code for taxation
     *
     * @param document
     * @return
     */
    public String getPostalCodeForTaxation(CustomerInvoiceDocument document);

    /**
     * This method returns true if the invoice has an approved status.
     *
     * @param invoiceNumber
     * @return
     */
    public boolean isInvoiceApproved(String invoiceNumber);

    /**
     * This method returns true if the bein date is valid.
     *
     * @param invoiceNumber
     * @return
     */
    public boolean isValidRecurrenceBeginDate(Date beginDate);

    /**
     * This method returns true if the end date is valid.
     *
     * @param invoiceNumber
     * @return
     */
    public boolean isValidRecurrenceEndDate(Date beginDate, Date endDate);

    /**
     * This method returns true if the end date and number of recurrences are valid if entered together.
     *
     * @param invoiceNumber
     * @return
     */
    public boolean isValidEndDateAndTotalRecurrenceNumber(Date beginDate, Date endDate, Integer totalRecurrenceNumber, String intervalCode);

    /**
     * This method returns true if one of the end date or the number of recurrences is entered.
     *
     * @param invoiceNumber
     * @return
     */
    public boolean isValidEndDateOrTotalRecurrenceNumber(Date endDate, Integer totalRecurrenceNumber);

    /**
     * This method returns true if the number of recurrences is not more than the maximum allowed.
     *
     * @param invoiceNumber
     * @return
     */
    public boolean isValidMaximumNumberOfRecurrences(Integer totalRecurrenceNumber, String intervalCode);

    /**
     * This method returns true if the initiator is valid.
     *
     * @param invoiceNumber
     * @return
     */
    public boolean isValidInitiator(String initiator);


}
