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
package org.kuali.kfs.module.ar.web.struts;

import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceDetailReport;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Action Class for Contracts & Grants Suspended Invoice Detail Report Lookup.
 */
public class ContractsGrantsSuspendedInvoiceDetailReportLookupAction extends ContractsGrantsReportLookupAction {
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
     * Returns "contractsGrantsSuspendedInvoiceDetailReportBuilderService"
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getReportBuilderServiceBeanName()
     */
    @Override
    public String getReportBuilderServiceBeanName() {
        return ArConstants.ReportBuilderDataServiceBeanNames.CONTRACTS_GRANTS_SUSPENDED_INVOICE_DETAIL;
    }

    /**
     * Returns the sort field for this report's pdf generation, "ContractsGrantsSuspendedInvoiceDetailReport"
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getSortFieldName()
     */
    @Override
    protected String getSortFieldName() {
        return ArConstants.CONTRACTS_GRANTS_SUSPENDED_INVOICE_DETAIL_REPORT;
    }

    /**
     * Returns the class for ContractsGrantsSuspendedInvoiceDetailReport
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsReportLookupAction#getPrintSearchCriteriaClass()
     */
    @Override
    public Class<? extends BusinessObject> getPrintSearchCriteriaClass() {
        return ContractsGrantsSuspendedInvoiceDetailReport.class;
    }
}
