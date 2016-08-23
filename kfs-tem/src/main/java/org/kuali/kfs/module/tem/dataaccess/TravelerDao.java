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
package org.kuali.kfs.module.tem.dataaccess;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;

import java.util.Collection;
import java.util.Map;

/**
 * This is the data access interface for Travelers.
 */
public interface TravelerDao {

    /**
     * Try to find {@link TravelerDetail} instances of employees. Employees have a <code>travelerTypeCode</code>
     * for employees. This means they also have valid employment information.
     *
     * @return {@link Collection} of {@linK TravelerDetail} instances
     */
    Collection<AccountsReceivableCustomer> findCustomersBy(final Map<String, String> criteria);

}
