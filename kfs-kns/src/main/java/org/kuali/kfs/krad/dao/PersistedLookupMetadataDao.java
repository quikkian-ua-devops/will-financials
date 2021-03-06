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
package org.kuali.kfs.krad.dao;

import java.sql.Timestamp;

public interface PersistedLookupMetadataDao {

    /**
     * removes all LookupResults BO where the lookup date attribute is older than
     * the parameter
     *
     * @param expirationDate all LookupResults having a lookup date before this date
     *                       will be removed
     */
    public void deleteOldLookupResults(Timestamp expirationDate);

    /**
     * removes all LookupResults BO where the lookup date attribute is older than
     * the parameter
     *
     * @param expirationDate all LookupResults having a lookup date before this date
     *                       will be removed
     */
    public void deleteOldSelectedObjectIds(Timestamp expirationDate);
}
