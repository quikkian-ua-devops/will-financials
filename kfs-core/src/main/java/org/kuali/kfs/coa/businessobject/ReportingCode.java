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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

/**
 * Reporting Codes Business Object
 */
public class ReportingCode extends PersistableBusinessObjectBase implements MutableInactivatable {

    private static final long serialVersionUID = -1585612121519839488L;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String financialReportingCode;
    private String financialReportingCodeDescription;
    private String financialReportingCodeMgrId;
    private String financialReportsToReportingCode;
    private boolean active;

    private Chart chart;
    private Organization org;
    private Person person;
    private ReportingCode reportingCodes;

    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * @return Returns the financialReportingCode.
     */
    public String getFinancialReportingCode() {
        return financialReportingCode;
    }

    /**
     * @param financialReportingCode The financialReportingCode to set.
     */
    public void setFinancialReportingCode(String financialReportingCode) {
        this.financialReportingCode = financialReportingCode;
    }

    /**
     * @return Returns the financialReportingCodeDescription.
     */
    public String getFinancialReportingCodeDescription() {
        return financialReportingCodeDescription;
    }

    /**
     * @param financialReportingCodeDescription The financialReportingCodeDescription to set.
     */
    public void setFinancialReportingCodeDescription(String financialReportingCodeDescription) {
        this.financialReportingCodeDescription = financialReportingCodeDescription;
    }

    /**
     * @return Returns the financialReportingCodeMgrId.
     */
    public String getFinancialReportingCodeMgrId() {
        return financialReportingCodeMgrId;
    }

    /**
     * @param financialReportingCodeMgrId The financialReportingCodeMgrId to set.
     */
    public void setFinancialReportingCodeMgrId(String financialReportingCodeMgrId) {
        this.financialReportingCodeMgrId = financialReportingCodeMgrId;
    }

    /**
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * @return Returns the financialReportsToReportingCode.
     */
    public String getFinancialReportsToReportingCode() {
        return financialReportsToReportingCode;
    }

    /**
     * @param financialReportsToReportingCode The financialReportsToReportingCode to set.
     */
    public void setFinancialReportsToReportingCode(String financialReportsToReportingCode) {
        this.financialReportsToReportingCode = financialReportsToReportingCode;
    }

    /**
     * @return Returns the chart.
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * @param chart The chart to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @return Returns the org.
     */
    public Organization getOrg() {
        return org;
    }

    /**
     * @param org The org to set.
     * @deprecated
     */
    public void setOrg(Organization org) {
        this.org = org;
    }

    public Person getPerson() {
        person = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(financialReportingCodeMgrId, person);
        return person;
    }

    /**
     * @param person The person to set.
     * @deprecated
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * @return Returns the reportingCodes.
     */
    public ReportingCode getReportingCodes() {
        return reportingCodes;
    }

    /**
     * @param reportingCodes The reportingCodes to set.
     * @deprecated
     */
    public void setReportingCodes(ReportingCode reportingCodes) {
        this.reportingCodes = reportingCodes;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("financialReportingCode", this.financialReportingCode);
        m.put("financialReportingCodeDescription", this.financialReportingCodeDescription);
        m.put("financialReportingCodeMgrId", this.financialReportingCodeMgrId);
        m.put("financialReportsToReportingCode", this.financialReportsToReportingCode);
        return m;
    }

    /**
     * Gets the active attribute.
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}

