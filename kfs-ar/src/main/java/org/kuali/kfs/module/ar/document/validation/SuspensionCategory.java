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
package org.kuali.kfs.module.ar.document.validation;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;

/**
 * Interface for suspension categories to be used for Suspension Category Validation of Contracts & Grants Invoices.
 * <p>
 * To add a new Suspension Category, extend SuspensionCategoryBase which implements this interface, and implement the
 * shouldSuspend method with the validation logic for the new suspension category. Create a bean definition for the
 * new Suspension Category class in spring-ar.xml and add to the suspensionCategories list that is injected into the
 * ContractsGrantsInvoiceDocumentServiceImpl class.
 */
public interface SuspensionCategory {

    /**
     * Perform validation to determine if the passed in invoice document should be suspended
     *
     * @param contractsGrantsinvoiceDocument
     * @return true if invoice should be suspended, false otherwise
     */
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument);

    /**
     * Getter for code for this Suspension Category
     *
     * @return code
     */
    public String getCode();

}
