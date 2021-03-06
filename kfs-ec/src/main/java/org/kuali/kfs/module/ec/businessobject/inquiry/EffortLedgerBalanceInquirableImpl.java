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
package org.kuali.kfs.module.ec.businessobject.inquiry;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl;
import org.kuali.kfs.integration.ld.LaborLedgerBalanceForEffortCertification;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class EffortLedgerBalanceInquirableImpl extends AbstractGeneralLedgerInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortLedgerBalanceInquirableImpl.class);

    private KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
        BusinessObject businessObject = this.getBusinessObject();
        EffortCertificationDocument document = null;

        if (businessObject instanceof EffortCertificationDetailBuild) {
            EffortCertificationDetailBuild effortCertificationDetail = (EffortCertificationDetailBuild) businessObject;
            document = effortCertificationDetail.getEffortCertificationDocumentBuild();
        } else if (businessObject instanceof EffortCertificationDetail) {
            EffortCertificationDetail effortCertificationDetail = (EffortCertificationDetail) businessObject;
            document = effortCertificationDetail.getEffortCertificationDocument();
        }

        if (document != null) {
            parameter.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, ObjectUtils.toString(document.getUniversityFiscalYear()));
            parameter.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, document.getEffortCertificationReportNumber());
            parameter.put(KFSPropertyConstants.EMPLID, document.getEmplid());
        }
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    @Override
    protected List<String> buildUserDefinedAttributeKeyList() {
        List<String> keys = new ArrayList<String>();

        keys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keys.add(KFSPropertyConstants.POSITION_NUMBER);

        return keys;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
     */
    @Override
    protected String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getBaseUrl()
     */
    @Override
    protected String getBaseUrl() {
        return KFSConstants.GL_BALANCE_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    @Override
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return kualiModuleService.getResponsibleModuleService(LaborLedgerBalanceForEffortCertification.class).createNewObjectFromExternalizableClass(LaborLedgerBalanceForEffortCertification.class).getClass();
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyName(java.lang.String)
     */
    @Override
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    @Override
    protected Object getKeyValue(String keyName, Object keyValue) {
        return keyValue;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    @Override
    protected String getLookupableImplAttributeName() {
        return null;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    @Override
    protected Map<String, Object> getUserDefinedAttributeMap() {
        Map<String, Object> userDefinedAttributeMap = new HashMap<String, Object>();

        userDefinedAttributeMap.put(EffortPropertyConstants.EFFORT_CERTIFICATION_PAYROLL_AMOUNT, KualiDecimal.ZERO);
        userDefinedAttributeMap.put(EffortPropertyConstants.EFFORT_CERTIFICATION_ORIGINAL_PAYROLL_AMOUNT, KualiDecimal.ZERO);

        return userDefinedAttributeMap;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#isExclusiveField(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isExclusiveField(Object keyName, Object keyValue) {
        if (super.isExclusiveField(keyName, keyValue)) {
            return true;
        }

        if (keyName != null && keyValue != null) {
            if (keyName.equals(EffortPropertyConstants.SOURCE_ACCOUNT_NUMBER) && keyValue.equals(EffortConstants.DASH_ACCOUNT_NUMBER)) {
                return true;
            } else if (keyName.equals(EffortPropertyConstants.SOURCE_CHART_OF_ACCOUNTS_CODE) && keyValue.equals(EffortConstants.DASH_CHART_OF_ACCOUNTS_CODE)) {
                return true;
            }
        }
        return false;
    }
}
