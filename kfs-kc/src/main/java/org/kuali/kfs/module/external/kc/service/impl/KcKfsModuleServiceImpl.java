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
package org.kuali.kfs.module.external.kc.service.impl;

import org.apache.log4j.Logger;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsModuleServiceImpl;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KcKfsModuleServiceImpl extends KfsModuleServiceImpl {

    protected static final Logger LOG = Logger.getLogger(KcKfsModuleServiceImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected ConfigurationService configurationService;

    @Override
    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        Class<? extends ExternalizableBusinessObject> implementationClass = getExternalizableBusinessObjectImplementation(businessObjectClass);
        return (T) getExternalizableBusinessObjectService(implementationClass).findByPrimaryKey(fieldValues);
    }

    @Override
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
        Class<? extends ExternalizableBusinessObject> implementationClass = getExternalizableBusinessObjectImplementation(businessObjectClass);
        return (List<T>) getExternalizableBusinessObjectService(implementationClass).findMatching(fieldValues);
    }

    @Override
    public boolean isExternal(Class boClass) {
        return isExternalizable(boClass);
    }

    /**
     * Finds the business object service via the class to service mapping provided in the module configuration.
     *
     * @param clazz
     * @return
     */
    private ExternalizableBusinessObjectService getExternalizableBusinessObjectService(Class clazz) {
        String serviceName = null;
        ExternalizableBusinessObjectService eboService = null;

        Map<Class, String> externalizableBusinessObjectServices = ((KcFinancialSystemModuleConfiguration) getModuleConfiguration()).getExternalizableBusinessObjectServiceImplementations();

        if (ObjectUtils.isNotNull(externalizableBusinessObjectServices) && ObjectUtils.isNotNull(clazz)) {
            serviceName = externalizableBusinessObjectServices.get(clazz);
            eboService = (ExternalizableBusinessObjectService) SpringContext.getService(serviceName);
        }

        return eboService;
    }

    /**
     * Gets primary key fields from the Datadictionary entries for the object.
     *
     * @see org.kuali.kfs.krad.service.impl.ModuleServiceBase#listPrimaryKeyFieldNames(java.lang.Class)
     */
    @Override
    public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
        Class clazz = getExternalizableBusinessObjectImplementation(businessObjectInterfaceClass);
        final org.kuali.kfs.krad.datadictionary.BusinessObjectEntry boEntry = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(clazz.getName());
        if (boEntry == null) {
            return null;
        }
        return boEntry.getPrimaryKeys();
    }

    /**
     * Changing the base url to KC url
     *
     * @see org.kuali.kfs.krad.service.impl.ModuleServiceBase#getInquiryUrl(java.lang.Class)
     */
    @Override
    protected String getInquiryUrl(Class inquiryBusinessObjectClass) {
        String baseUrl = configurationService.getPropertyValueAsString(KFSConstants.KC_APPLICATION_URL_KEY);
        String inquiryUrl = baseUrl;
        if (!inquiryUrl.endsWith("/")) {
            inquiryUrl = inquiryUrl + "/";
        }
        return inquiryUrl + "kr/" + KRADConstants.INQUIRY_ACTION;
    }

    /**
     * Mapping the kfs classes and parameters over to KC equivalents
     *
     * @see org.kuali.kfs.krad.service.impl.ModuleServiceBase#getUrlParameters(java.lang.String, java.util.Map)
     */
    @Override
    protected Properties getUrlParameters(String businessObjectClassAttribute, Map<String, String[]> parameters) {
        Properties urlParameters = new Properties();
        String paramNameToConvert = null;
        Map<String, String> kfsToKcInquiryUrlParameterMapping = ((KcFinancialSystemModuleConfiguration) getModuleConfiguration()).getKfsToKcInquiryUrlParameterMapping();
        Map<String, String> kfsToKcInquiryUrlClassMapping = ((KcFinancialSystemModuleConfiguration) getModuleConfiguration()).getKfsToKcInquiryUrlClassMapping();

        for (String paramName : parameters.keySet()) {
            String parameterName = paramName;
            String[] parameterValues = parameters.get(paramName);

            if (parameterValues.length > 0) {
                //attempt to convert parameter name if necessary
                paramNameToConvert = businessObjectClassAttribute + "." + paramName;
                if (kfsToKcInquiryUrlParameterMapping.containsKey(paramNameToConvert)) {
                    parameterName = kfsToKcInquiryUrlParameterMapping.get(paramNameToConvert);
                }
                urlParameters.put(parameterName, parameterValues[0]);
            }
        }

        urlParameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, kfsToKcInquiryUrlClassMapping.get(businessObjectClassAttribute));
        urlParameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER,
            KRADConstants.CONTINUE_WITH_INQUIRY_METHOD_TO_CALL);
        return urlParameters;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
