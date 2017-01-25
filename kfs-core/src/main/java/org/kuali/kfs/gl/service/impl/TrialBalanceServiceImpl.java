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
package org.kuali.kfs.gl.service.impl;

import net.sf.jasperreports.engine.JRParameter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.dataaccess.TrialBalanceDao;
import org.kuali.kfs.gl.service.TrialBalanceService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.ReportGenerationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * This class is the OJB implementation of the Balance Service
 */
@Transactional
public class TrialBalanceServiceImpl implements TrialBalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TrialBalanceServiceImpl.class);

    private String msuLogoPath;
    private String ebsLogoPath;

    protected TrialBalanceDao trialBalanceDao;
    protected ReportInfo glTrialBalanceReportInfo;
    protected ReportGenerationService reportGenerationService;
    protected DateTimeService dateTimeService;


    @Override
    public List findTrialBalance(String selectedFiscalYear, String chartCode, String periodCode) {

        if (StringUtils.isBlank(chartCode) || KFSConstants.WILDCARD_CHARACTER.equals(chartCode)) {
            chartCode = "";
        }

        //for invalid options of financialPeriodCode, it will be considered empty, which is equivalent to getting the current fp
        if (StringUtils.isBlank(periodCode) || KFSConstants.WILDCARD_CHARACTER.equals(periodCode)) {
            periodCode = "";
        }

        try {
            Integer period = Integer.parseInt(periodCode);
            if (0 >= period || period > 13) {
                periodCode = "";
            }
        } catch (NumberFormatException e) {
            periodCode = "";
        }

        return trialBalanceDao.findBalanceByFields(selectedFiscalYear, chartCode, periodCode);
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportService#generateReportForExtractProcess(org.kuali.kfs.module.ec.util.ExtractProcessReportDataHolder,
     * java.util.Date)
     */
    @Override
    public String generateReportForExtractProcess(Collection dataSource, String fiscalYear, String periodCode) {
        String fiscalPeriod = "Current";

        if (!StringUtils.isBlank(periodCode) && !KFSConstants.WILDCARD_CHARACTER.equals(periodCode)) {
            try {
                Integer period = Integer.parseInt(periodCode);
                if (0 >= period || period > 13) {
                    periodCode = "";
                }
            } catch (NumberFormatException e) {
                periodCode = "";
            }
            if (!StringUtils.isEmpty(periodCode)) {
                fiscalPeriod = periodCode;
            }
        }

        String reportFileName = glTrialBalanceReportInfo.getReportFileName();
        String reportDirectory = glTrialBalanceReportInfo.getReportsDirectory();
        String reportTemplateClassPath = glTrialBalanceReportInfo.getReportTemplateClassPath();
        String reportTemplateName = glTrialBalanceReportInfo.getReportTemplateName();
        ResourceBundle resourceBundle = glTrialBalanceReportInfo.getResourceBundle();
        String subReportTemplateClassPath = glTrialBalanceReportInfo.getSubReportTemplateClassPath();
        Map<String, String> subReports = glTrialBalanceReportInfo.getSubReports();

        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put(KFSConstants.TRIAL_BAL_REPORT_YEAR, fiscalYear);
        reportData.put(KFSConstants.TRIAL_BAL_REPORT_PERIOD, fiscalPeriod);
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(dateTimeService.getCurrentDate(), reportDirectory, reportFileName, "");
        reportGenerationService.generateReportToPdfFile(reportData, dataSource, template, fullReportFileName);

        return fullReportFileName + ".pdf";

    }

    /**
     * Gets the trialBalanceDao attribute.
     *
     * @return Returns the trialBalanceDao.
     */
    public TrialBalanceDao getTrialBalanceDao() {
        return trialBalanceDao;
    }

    /**
     * Sets the trialBalanceDao attribute value.
     *
     * @param trialBalanceDao The trialBalanceDao to set.
     */
    public void setTrialBalanceDao(TrialBalanceDao trialBalanceDao) {
        this.trialBalanceDao = trialBalanceDao;
    }

    /**
     * Gets the glTrialBalanceReportInfo attribute.
     *
     * @return Returns the glTrialBalanceReportInfo.
     */
    public ReportInfo getGlTrialBalanceReportInfo() {
        return glTrialBalanceReportInfo;
    }

    /**
     * Sets the glTrialBalanceReportInfo attribute value.
     *
     * @param glTrialBalanceReportInfo The glTrialBalanceReportInfo to set.
     */
    public void setGlTrialBalanceReportInfo(ReportInfo glTrialBalanceReportInfo) {
        this.glTrialBalanceReportInfo = glTrialBalanceReportInfo;
    }

    /**
     * Gets the reportGenerationService attribute.
     *
     * @return Returns the reportGenerationService.
     */
    public ReportGenerationService getReportGenerationService() {
        return reportGenerationService;
    }

    /**
     * Sets the reportGenerationService attribute value.
     *
     * @param reportGenerationService The reportGenerationService to set.
     */
    public void setReportGenerationService(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public String getMsuLogoPath() {
        return msuLogoPath;
    }

    public void setMsuLogoPath(String msuLogoPath) {
        this.msuLogoPath = msuLogoPath;
    }

    public String getEbsLogoPath() {
        return ebsLogoPath;
    }

    public void setEbsLogoPath(String ebsLogoPath) {
        this.ebsLogoPath = ebsLogoPath;
    }


}
