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
package org.kuali.kfs.module.cg.batch.service;


/**
 * Service which will help move notes from agency and award maintenance documents to the business objects themselves
 */
public interface MaintenanceDocumentNotesMigrationService {
    /**
     * Moves notes on agency maintenance documents to corresponding agency business objects
     */
    public void moveAgencyMaintenanceDocumentNotesToBusinessObjects();

    /**
     * Moves notes on award maintenance documents to corresponding award business objects
     */
    public void moveAwardMaintenanceDocumentNotesToBusinessObjects();
}
