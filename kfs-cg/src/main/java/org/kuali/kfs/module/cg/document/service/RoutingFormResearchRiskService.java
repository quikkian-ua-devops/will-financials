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
package org.kuali.kfs.module.cg.document.service;

import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;

import java.util.List;

/**
 * This interface defines methods that a RoutingFormResearchRiskService must provide
 */
public interface RoutingFormResearchRiskService {

    /**
     * Get the list of active research risk types from the database.
     *
     * @param exceptCodes the codes of research risk types to exclude from the results
     * @return List<ResearchRiskType>
     */
    public List<ResearchRiskType> getResearchRiskTypes(String[] exceptCodes);
}
