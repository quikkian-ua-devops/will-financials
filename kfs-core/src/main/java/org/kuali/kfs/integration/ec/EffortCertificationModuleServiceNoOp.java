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
package org.kuali.kfs.integration.ec;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class EffortCertificationModuleServiceNoOp implements EffortCertificationModuleService {

    private Logger LOG = Logger.getLogger(getClass());

    public List<EffortCertificationReport> findReportDefinitionsForPeriod(Integer fiscalYear, String periodCode, String positionObjectGroupCode) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return Collections.emptyList();
    }

    public List<String> getCostShareSubAccountTypeCodes() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return Collections.emptyList();
    }

    public EffortCertificationReport isEmployeeWithOpenCertification(List<EffortCertificationReport> effortCertificationReports, String emplid) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

}
