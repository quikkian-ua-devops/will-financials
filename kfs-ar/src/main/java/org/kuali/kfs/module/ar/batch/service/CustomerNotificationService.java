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
package org.kuali.kfs.module.ar.batch.service;

import java.sql.Timestamp;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

/**
 * define the customer notification service
 */
public interface CustomerNotificationService {

    /**
     * send aging reports to the customers
     */
    public void sendCustomerAgingReport();

    /**
     * send the aging report of the given invoice to the customer
     *
     * @param invoiceDocument
     * @param agingReportSentTime
     */
    public void sendCustomerAgingReport(CustomerInvoiceDocument invoiceDocument, Timestamp agingReportSentTime);
}
