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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.TicklersReport;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * LookupAction file for Ticklers Report.
 */
public class TicklersReportLookupAction extends ContractsGrantsReportLookupAction {
    /**
     * This report does not have a title
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#generateReportTitle(org.kuali.rice.kns.web.struts.form.LookupForm)
     */
    @Override
    public String generateReportTitle(LookupForm lookupForm) {
        return null;
    }

    /**
     * Returns "ticklersReportBuilderService"
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.TICKLERS;
    }

    /**
     * Returns the sort field for this report's pdf generation, "TicklersReport"
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getSortFieldName()
     */
    @Override
    protected String getSortFieldName() {
        return ArConstants.TICKLERS_REPORT_SORT_FIELD;
    }

    /**
     * Returns the class of TicklersReport
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getPrintSearchCriteriaClass()
     */
    @Override
    public Class<? extends BusinessObject> getPrintSearchCriteriaClass() {
        return TicklersReport.class;
    }

    /**
     * Always returns true, as tickler report rows always have actions
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#shouldDisplayActionsForRow()
     */
    @Override
    public boolean shouldDisplayActionsForRow() {
        return true;
    }
}
