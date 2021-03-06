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
package org.kuali.kfs.module.ar.document.validation;

import org.kuali.kfs.krad.document.TransactionalDocument;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;

/**
 * Rule invoked when a new customer invoice detail is added
 */
public interface AddCashControlDetailRule<F extends TransactionalDocument> extends CashControlDetailRule {


    /**
     * This method is called when a cash control detail is added
     *
     * @param transactionalDocument the cash control document
     * @param cashControlDetail     the detail to be added
     * @return true if valid to be added, false otherwise
     */
    public boolean processAddCashControlDetailBusinessRules(F transactionalDocument, CashControlDetail cashControlDetail);

}
