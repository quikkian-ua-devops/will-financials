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
package org.kuali.kfs.module.cg.report.service.impl;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.cg.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.cg.report.service.ContractsGrantsAwardBalancesReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Service implementation class for Contracts & Grants Award Balances Report
 */
public class ContractsGrantsAwardBalancesReportServiceImpl implements ContractsGrantsAwardBalancesReportService {

    protected ReportGenerationService reportGenerationService;
    protected DataDictionaryService dataDictionaryService;
    private ReportInfo contractsGrantsAwardBalancesReportInfo;

    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {

        return generateReport(reportDataHolder, contractsGrantsAwardBalancesReportInfo, baos);
    }

    /**
     * @see org.kuali.kfs.module.cg.report.service.ContractsGrantsAwardBalancesReportService#generateReport(org.kuali.kfs.module.cg.report.ContractsGrantsReportDataHolder, org.kuali.kfs.sys.report.ReportInfo, java.io.ByteArrayOutputStream)
     */
    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ReportInfo reportInfo, ByteArrayOutputStream baos) {
        Date runDate = new Date();

        String reportFileName = reportInfo.getReportFileName();
        String reportDirectory = reportInfo.getReportsDirectory();
        String reportTemplateClassPath = reportInfo.getReportTemplateClassPath();
        String reportTemplateName = reportInfo.getReportTemplateName();
        ResourceBundle resourceBundle = reportInfo.getResourceBundle();

        String subReportTemplateClassPath = reportInfo.getSubReportTemplateClassPath();
        Map<String, String> subReports = reportInfo.getSubReports();

        Map<String, Object> reportData = reportDataHolder.getReportData();
        // check title and set
        if (ObjectUtils.isNull(reportData.get(KFSConstants.REPORT_TITLE))) {
            reportData.put(KFSConstants.REPORT_TITLE, reportInfo.getReportTitle());
        }
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        addParametersToReportData(reportData);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "");

        List<String> data = Arrays.asList(KFSConstants.EMPTY_STRING);
        JRDataSource dataSource = new JRBeanCollectionDataSource(data);

        reportGenerationService.generateReportToOutputStream(reportData, dataSource, template, baos);

        return reportFileName;
    }

    /**
     * In order to generate a report with the appropriate labels when KC CGB integration
     * is enabled we need to be able to pass some parameters to report generation template
     * instead of using the generic messages.properties values. The necessary values are
     * fetched from the data dictionary.
     */
    public void addParametersToReportData(Map<String, Object> reportData) {
        BusinessObjectEntry boe = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(ContractsAndGrantsBillingAward.class.getName());
        reportData.put("awardProposalId", boe.getAttributeDefinition("proposalNumber").getLabel());
        reportData.put("agencySponsorCode", boe.getAttributeDefinition("agencyNumber").getLabel());
    }

    /**
     * @return reportGenerationService
     */
    public ReportGenerationService getReportGenerationService() {
        return reportGenerationService;
    }

    /**
     * @param reportGenerationService
     */
    public void setReportGenerationService(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    /**
     * @return contractsGrantsAwardBalancesReportInfo
     */
    public ReportInfo getContractsGrantsAwardBalancesReportInfo() {
        return contractsGrantsAwardBalancesReportInfo;
    }

    /**
     * @param contractsGrantsInvoiceReportInfo
     */
    public void setContractsGrantsAwardBalancesReportInfo(ReportInfo contractsGrantsInvoiceReportInfo) {
        this.contractsGrantsAwardBalancesReportInfo = contractsGrantsInvoiceReportInfo;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
