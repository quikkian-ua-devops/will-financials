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
package org.kuali.kfs.module.ec.batch.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.batch.service.EffortCertificationCreateService;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.service.EffortCertificationDocumentService;
import org.kuali.kfs.module.ec.util.EffortCertificationParameterFinder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

/**
 * This Process creates effort certification documents from the temporary build table created by the batch process and routes effort
 * certification documents to project directors, fiscal officers, and central workgroups. The process includes the following steps:
 * <p>
 * <li>construct an effort certification document from a temporary effort certification document; </li>
 * <li>route each effort certification document; </li>
 * <li>delete the temporary effort certification document after routing successfully. </li>
 */
@Transactional
public class EffortCertificationCreateServiceImpl implements EffortCertificationCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationCreateServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private EffortCertificationDocumentService effortCertificationDocumentService;

    /**
     * @see org.kuali.kfs.module.ec.batch.service.EffortCertificationCreateService#create()
     */

    public void create() {
        Integer fiscalYear = EffortCertificationParameterFinder.getCreateReportFiscalYear();
        String reportNumber = EffortCertificationParameterFinder.getCreateReportNumber();

        this.create(fiscalYear, reportNumber);
    }

    /**
     * @see org.kuali.kfs.module.ec.batch.service.EffortCertificationCreateService#create(java.lang.Integer, java.lang.String)
     */

    public void create(Integer fiscalYear, String reportNumber) {
        Map<String, String> fieldValues = EffortCertificationReportDefinition.buildKeyMap(fiscalYear, reportNumber);

        String errorMessage = this.validateReportDefintion(fieldValues);
        if (StringUtils.isNotEmpty(errorMessage)) {
            LOG.fatal(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Collection<EffortCertificationDocumentBuild> documentsBuild = businessObjectService.findMatching(EffortCertificationDocumentBuild.class, fieldValues);
        for (EffortCertificationDocumentBuild documentBuild : documentsBuild) {
            boolean isCreated = effortCertificationDocumentService.createAndRouteEffortCertificationDocument(documentBuild);

            if (isCreated) {
                businessObjectService.delete(documentBuild);
            }
        }
    }

    /**
     * check if a report has been defined and its docuemnts have not been generated. The combination of fiscal year and report
     * number can determine a report definition.
     *
     * @param fieldValues the map containing fiscalYear and report number
     * @return a message if a report has not been defined or its documents have been gerenated; otherwise, return null
     */
    protected String validateReportDefintion(Map<String, String> fieldValues) {
        String fiscalYear = fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        String reportNumber = fieldValues.get(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);
        String combinedFieldValues = new StringBuilder(fiscalYear).append(EffortConstants.VALUE_SEPARATOR).append(reportNumber).toString();

        // Fiscal Year is required
        if (StringUtils.isEmpty(fiscalYear)) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_MISSING, null).getMessage();
        }

        // Report Number is required
        if (StringUtils.isEmpty(reportNumber)) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_NUMBER_MISSING, null).getMessage();
        }

        // check if a report has been defined
        EffortCertificationReportDefinition reportDefinition = (EffortCertificationReportDefinition) businessObjectService.findByPrimaryKey(EffortCertificationReportDefinition.class, fieldValues);
        if (reportDefinition == null) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_OR_REPORT_NUMBER_INVALID, combinedFieldValues).getMessage();
        }

        // check if the selected report definition is still active
        if (!reportDefinition.isActive()) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_INACTIVE, combinedFieldValues).getMessage();
        }

        // check if the report period of the selected report definition is open. If not, throws an error message
        if (!KFSConstants.PeriodStatusCodes.OPEN.equals(reportDefinition.getEffortCertificationReportPeriodStatusCode())) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_PERIOD_NOT_OPENED, combinedFieldValues).getMessage();
        }

        // check if any document has been generated for the selected report definition. If so, return with an error message
        int countOfDocuments = businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues);
        if (countOfDocuments > 0) {
            return MessageBuilder.buildMessageWithPlaceHolder(EffortKeyConstants.ERROR_REPORT_DOCUMENT_EXIST, reportNumber, fiscalYear).getMessage();
        }

        // check if any document build has been generated for the selected report definition. If not, return with an error message
        int countOfDocumentsBuild = businessObjectService.countMatching(EffortCertificationDocumentBuild.class, fieldValues);
        if (countOfDocumentsBuild <= 0) {
            return MessageBuilder.buildMessageWithPlaceHolder(EffortKeyConstants.ERROR_REPORT_DOCUMENT_BUILD_NOT_EXIST, reportNumber, fiscalYear).getMessage();
        }

        return null;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the effortCertificationDocumentService attribute value.
     *
     * @param effortCertificationDocumentService The effortCertificationDocumentService to set.
     */
    public void setEffortCertificationDocumentService(EffortCertificationDocumentService effortCertificationDocumentService) {
        this.effortCertificationDocumentService = effortCertificationDocumentService;
    }
}
