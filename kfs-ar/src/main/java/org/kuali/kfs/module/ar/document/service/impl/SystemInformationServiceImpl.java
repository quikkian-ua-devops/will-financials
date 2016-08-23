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
package org.kuali.kfs.module.ar.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class...
 */
@Transactional
public class SystemInformationServiceImpl implements SystemInformationService {

    protected BusinessObjectService businessObjectService;
    protected UniversityDateService universityDateService;

    @Override
    public SystemInformation getByLockboxNumberForCurrentFiscalYear(String lockboxNumber) {
        Integer universityFiscalYear = universityDateService.getCurrentFiscalYear();
        return getByLockboxNumber(lockboxNumber, universityFiscalYear);
    }

    @Override
    public SystemInformation getByLockboxNumber(String lockboxNumber, Integer universityFiscalYear) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(ArPropertyConstants.SystemInformationFields.LOCKBOX_NUMBER, lockboxNumber);
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);

        final Collection<SystemInformation> systemInformations = getBusinessObjectService().findMatching(SystemInformation.class, fieldValues);
        SystemInformation result = null;
        for (SystemInformation systemInformation : systemInformations) {
            result = systemInformation; // we should only be in this loop once
        }

        return result;
    }

    @Override
    public SystemInformation getByProcessingChartOrgAndFiscalYear(String chartCode, String orgCode, Integer fiscalYear) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        fieldValues.put(KFSPropertyConstants.PROCESSING_CHART_OF_ACCT_CD, chartCode);
        fieldValues.put(KFSPropertyConstants.PROCESSING_ORGANIZATION_CODE, orgCode);
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        final Collection<SystemInformation> systemInformations = getBusinessObjectService().findMatching(SystemInformation.class, fieldValues);
        SystemInformation result = null;
        for (SystemInformation systemInformation : systemInformations) {
            result = systemInformation; // we should only be in this loop once
        }

        return result;
    }

    /**
     * Grabs all of the SystemInformation records associated with the given lockbox id; filters out records associated with the given processing
     * chart and organization code.  We do the filtering in Java so a) we could use BusinessObjectService and b) because filtering the records in Java
     * should be cheaper than doing two counts and subtracting - at least that's what Explain Plan told us.
     * <p>
     * In the best of all worlds, this method always returns 0.
     *
     * @see org.kuali.kfs.module.ar.document.service.SystemInformationService#getCountByChartOrgAndLockboxNumber(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public int getCountByChartOrgAndLockboxNumber(String processingChartCode, String processingOrgCode, String lockboxNumber) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(ArPropertyConstants.SystemInformationFields.LOCKBOX_NUMBER, lockboxNumber);
        final Collection<SystemInformation> sysInfos = getBusinessObjectService().findMatching(SystemInformation.class, fieldValues);
        List<SystemInformation> filteredSysInfos = new ArrayList<SystemInformation>();
        for (SystemInformation sysInfo : sysInfos) {
            if (!StringUtils.equals(sysInfo.getProcessingChartOfAccountCode(), processingChartCode) && !StringUtils.equals(sysInfo.getProcessingOrganizationCode(), processingOrgCode)) {
                filteredSysInfos.add(sysInfo);
            }
        }
        return filteredSysInfos.size();
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

}
