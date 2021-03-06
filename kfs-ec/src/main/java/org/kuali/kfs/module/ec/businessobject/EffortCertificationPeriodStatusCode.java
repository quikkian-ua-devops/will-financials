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
package org.kuali.kfs.module.ec.businessobject;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

import java.util.LinkedHashMap;

/**
 * Business Object for the Effort Certification Report Status Code Table.
 */
public class EffortCertificationPeriodStatusCode extends PersistableBusinessObjectBase {
    private String effortCertificationReportPeriodStatusCode;
    private String effortCertificationReportPeriodStatusDescription;

    /**
     * Constructs a EffortCertificationPeriodStatusCode.java.
     */
    public EffortCertificationPeriodStatusCode() {

    }

    /**
     * Gets the effortCertificationReportPeriodStatusCode attribute.
     *
     * @return Returns the effortCertificationReportPeriodStatusCode.
     */
    public String getEffortCertificationReportPeriodStatusCode() {
        return effortCertificationReportPeriodStatusCode;
    }

    /**
     * Sets the effortCertificationReportPeriodStatusCode attribute value.
     *
     * @param effortCertificationReportPeriodStatusCode The effortCertificationReportPeriodStatusCode to set.
     */
    public void setEffortCertificationReportPeriodStatusCode(String effortCertificationReportPeriodStatusCode) {
        this.effortCertificationReportPeriodStatusCode = effortCertificationReportPeriodStatusCode;
    }

    /**
     * Gets the effortCertificationReportPeriodStatusDescription attribute.
     *
     * @return Returns the effortCertificationReportPeriodStatusDescription.
     */
    public String getEffortCertificationReportPeriodStatusDescription() {
        return effortCertificationReportPeriodStatusDescription;
    }

    /**
     * Sets the effortCertificationReportPeriodStatusDescription attribute value.
     *
     * @param effortCertificationReportPeriodStatusDescription The effortCertificationReportPeriodStatusDescription to set.
     */
    public void setEffortCertificationReportPeriodStatusDescription(String effortCertificationReportPeriodStatusDescription) {
        this.effortCertificationReportPeriodStatusDescription = effortCertificationReportPeriodStatusDescription;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("effortCertificationReportPeriodStatusCode", this.effortCertificationReportPeriodStatusCode);
        return m;
    }


}
