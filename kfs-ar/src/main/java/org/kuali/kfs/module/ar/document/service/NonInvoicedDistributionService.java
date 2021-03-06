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
package org.kuali.kfs.module.ar.document.service;

import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

import java.util.Collection;

public interface NonInvoicedDistributionService {

    /**
     * This method returns NonInvoicedDistribution objects corresponding to an invoice by documentNumber.
     *
     * @param documentNumber used to find the invoice and get the NonInvoicedDistribution objects
     * @return Collection<NonInvoicedDistribution> non invoice distributions for the invoice
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber);

    /**
     * This method returns NonInvoicedDistribution objects corresponding to an invoice.
     *
     * @param invoice used to find the NonInvoiceDistribution objects
     * @return Collection<NonInvoicedDistribution> non invoice distributions for the invoice
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(CustomerInvoiceDocument invoice);

}
