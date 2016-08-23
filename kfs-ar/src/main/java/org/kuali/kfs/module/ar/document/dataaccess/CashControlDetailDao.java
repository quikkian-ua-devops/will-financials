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
package org.kuali.kfs.module.ar.document.dataaccess;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;

public interface CashControlDetailDao {

    /**
     *
     * Retrieves the CashControlDetail object associated with a given reference Document
     * Number.
     *
     * This will typically be the PaymentApplication Document Number that is generated by
     * the CashControl document.
     *
     * @param referenceDocumentNumber The PayApp document number you want to find the associated
     *                                CashControlDetail record for.
     * @return The associated CashControlDetail record if it exists, null if it doesnt.
     */
    public CashControlDetail getCashControlDetailByRefDocNumber(String referenceDocumentNumber);

}
