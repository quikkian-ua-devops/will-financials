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
package org.kuali.kfs.module.cg.service;

import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.module.cg.businessobject.Agency;

import java.util.List;

/**
 * Services for Agency
 */
public interface AgencyService {

    /**
     * Finds a Agency by agency number.
     *
     * @param agencyNumber the primary key of the Agency to get
     * @return the corresponding Agency, or null if none
     */
    public Agency getByPrimaryId(String agencyNumber);


    /**
     * Gets Notes for Agency maintenance document
     *
     * @param agencyNumber
     * @return
     */
    public List<Note> getAgencyNotes(String agencyNumber);
}
