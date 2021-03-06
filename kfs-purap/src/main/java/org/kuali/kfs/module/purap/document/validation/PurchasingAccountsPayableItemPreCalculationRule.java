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
package org.kuali.kfs.module.purap.document.validation;

import org.kuali.kfs.module.purap.businessobject.PurApItem;

/**
 * Continue Purap Rule Interface
 * Defines a rule which gets invoked immediately before continuing to the next step during creation of a Transactional document.
 */
public interface PurchasingAccountsPayableItemPreCalculationRule {

    /**
     * Checks the rules that says percent must be 100% or
     * item total should be equal to the amount of accounts for that item.
     *
     * @param item the item to check
     * @return true if the business rules pass
     */
    public boolean checkPercentOrTotalAmountsEqual(PurApItem item);

}
