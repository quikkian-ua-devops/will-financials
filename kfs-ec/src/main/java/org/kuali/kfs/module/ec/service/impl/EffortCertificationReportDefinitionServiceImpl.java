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
package org.kuali.kfs.module.ec.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportEarnPaygroup;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportPosition;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.MessageBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provide the implementation of the service methods related to EffortCertificationReportDefinition
 *
 * @see org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition
 */
public class EffortCertificationReportDefinitionServiceImpl implements EffortCertificationReportDefinitionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationReportDefinitionServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#findReportDefinitionByPrimaryKey(java.util.Map)
     */
    public EffortCertificationReportDefinition findReportDefinitionByPrimaryKey(Map<String, String> fieldValues) {
        return (EffortCertificationReportDefinition) businessObjectService.findByPrimaryKey(EffortCertificationReportDefinition.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#validateEffortCertificationReportDefinition(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public String validateEffortCertificationReportDefinition(EffortCertificationReportDefinition effortCertificationReportDefinition) {
        Integer fiscalYear = effortCertificationReportDefinition.getUniversityFiscalYear();
        String reportNumber = effortCertificationReportDefinition.getEffortCertificationReportNumber();

        // Fiscal Year is required
        if (ObjectUtils.isNull(fiscalYear)) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_FISCAL_YEAR_MISSING, null).getMessage();
        }

        // Report Number is required
        if (StringUtils.isEmpty(reportNumber)) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_NUMBER_MISSING, null).getMessage();
        }

        String combinedFieldValues = new StringBuilder(fiscalYear.toString()).append(EffortConstants.VALUE_SEPARATOR).append(reportNumber).toString();

        // check if there exists the given report definition
        effortCertificationReportDefinition = (EffortCertificationReportDefinition) businessObjectService.retrieve(effortCertificationReportDefinition);
        if (effortCertificationReportDefinition == null) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_NOT_EXIST, combinedFieldValues).getMessage();
        }

        // check if the given report definition is still active
        if (!effortCertificationReportDefinition.isActive()) {
            return MessageBuilder.buildMessage(EffortKeyConstants.ERROR_REPORT_DEFINITION_INACTIVE, combinedFieldValues).getMessage();
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#findPositionObjectGroupCodes(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public List<String> findPositionObjectGroupCodes(EffortCertificationReportDefinition reportDefinition) {
        Map<String, String> fieldValues = reportDefinition.buildKeyMapForCurrentReportDefinition();
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE.toString());

        Collection<EffortCertificationReportPosition> reportPosition = businessObjectService.findMatching(EffortCertificationReportPosition.class, fieldValues);

        List<String> positionGroupCodes = new ArrayList<String>();
        for (EffortCertificationReportPosition position : reportPosition) {
            positionGroupCodes.add(position.getEffortCertificationReportPositionObjectGroupCode());
        }

        return positionGroupCodes;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#findReportEarnCodePayGroups(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public Map<String, Set<String>> findReportEarnCodePayGroups(EffortCertificationReportDefinition reportDefinition) {
        Collection<EffortCertificationReportEarnPaygroup> reportEarnPay = this.findReportEarnPay(reportDefinition);
        Map<String, Set<String>> earnCodePayGroups = new HashMap<String, Set<String>>();

        for (EffortCertificationReportEarnPaygroup earnPay : reportEarnPay) {
            String payGroup = earnPay.getPayGroup();
            String earnCode = earnPay.getEarnCode();

            if (earnCodePayGroups.containsKey(payGroup)) {
                Set<String> earnCodeSet = earnCodePayGroups.get(payGroup);
                earnCodeSet.add(earnCode);
            } else {
                Set<String> earnCodeSet = new HashSet<String>();
                earnCodeSet.add(earnCode);
                earnCodePayGroups.put(payGroup, earnCodeSet);
            }
        }

        return earnCodePayGroups;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#findReportEarnPay(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public Collection<EffortCertificationReportEarnPaygroup> findReportEarnPay(EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_TYPE_CODE, reportDefinition.getEffortCertificationReportTypeCode());
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE.toString());

        return businessObjectService.findMatching(EffortCertificationReportEarnPaygroup.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#existsPendingEffortCertification(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public boolean hasPendingEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());

        int numOfPendingDocuments = businessObjectService.countMatching(EffortCertificationDocumentBuild.class, fieldValues);
        if (numOfPendingDocuments > 0) {
            return true;
        }

        List<String> pendingStatusCodes = Arrays.asList(KFSConstants.DocumentStatusCodes.ENROUTE);
        fieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, pendingStatusCodes);

        return businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues) > 0;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#hasApprovedEffortCertification(java.lang.String, org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public boolean hasApprovedEffortCertification(String emplid, EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());
        fieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);

        return businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues) > 0;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#hasBeenUsedForEffortCertificationGeneration(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public boolean hasBeenUsedForEffortCertificationGeneration(EffortCertificationReportDefinition reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());

        return businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues) > 0;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationReportDefinitionService#hasBeenUsedForEffortCertificationGeneration(java.lang.String, org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public boolean hasBeenUsedForEffortCertificationGeneration(String emplid, EffortCertificationReport reportDefinition) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportDefinition.getUniversityFiscalYear());
        fieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());
        fieldValues.put(KFSPropertyConstants.EMPLID, emplid);

        return businessObjectService.countMatching(EffortCertificationDocument.class, fieldValues) > 0;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
