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
package org.kuali.kfs.module.purap.batch.service;

import java.util.Date;

/**
 * Sometimes it is necessary to rerun a PURAP process on the following day if the previous night's GL batch processes failed. This
 * service facilitates the re-running of PURAP batch processes by allowing the PURAP processes to assume that the PURAP processes are being
 * run the night before
 */
public interface PurapRunDateService {
    /**
     * Returns the assumed runtime given the actual execution time.
     *
     * @param executionDate the actual date that this method is called
     * @return the run date/time to assume when running the PURAP processes
     */
    public Date calculateRunDate(Date executionDate);
}
