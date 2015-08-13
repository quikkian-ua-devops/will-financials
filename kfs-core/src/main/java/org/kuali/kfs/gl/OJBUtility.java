/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.gl;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaClass;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.kns.datadictionary.FieldDefinition;
import org.kuali.kfs.krad.dao.LookupDao;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

/**
 * This class provides a set of utilities that can handle common tasks related to business objects.
 */
public class OJBUtility {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OJBUtility.class);

    public static final String LOOKUP_DAO = "lookupDao";

    /**
     * This method builds a map of business object with its property names and values
     * 
     * @param businessObject the given business object
     * @return the map of business object with its property names and values
     */
    public static LinkedHashMap buildPropertyMap(Object businessObject) {
        DynaClass dynaClass = WrapDynaClass.createDynaClass(businessObject.getClass());
        DynaProperty[] properties = dynaClass.getDynaProperties();
        LinkedHashMap propertyMap = new LinkedHashMap();

        try {
            for (int numOfProperty = 0; numOfProperty < properties.length; numOfProperty++) {
                String propertyName = properties[numOfProperty].getName();
                if (PropertyUtils.isWriteable(businessObject, propertyName)) {
                    Object propertyValue = PropertyUtils.getProperty(businessObject, propertyName);
                    propertyMap.put(propertyName, propertyValue);
                }
            }
        }
        catch (Exception e) {
            LOG.error("OJBUtility.buildPropertyMap()" + e);
        }
        return propertyMap;
    }

    /**
     * This method builds an OJB query criteria based on the input field map
     * 
     * @param fieldValues the input field map
     * @param businessObject the given business object
     * @return an OJB query criteria
     */
    public static Criteria buildCriteriaFromMap(Map fieldValues, Object businessObject) {

        Criteria criteria = new Criteria();
        BusinessObjectEntry entry = (BusinessObjectEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObject.getClass().getName());
           //FieldDefinition lookupField = entry.getLookupDefinition().getLookupField(attributeName);
        //System.out.println(entry.getTitleAttribute());
        try {
            Iterator propsIter = fieldValues.keySet().iterator();
            while (propsIter.hasNext()) {
                String propertyName = (String) propsIter.next();
                Object propertyValueObject = fieldValues.get(propertyName);
                String propertyValue = "";
         
                 
                FieldDefinition lookupField = (entry != null) ? entry.getLookupDefinition().getLookupField(propertyName) : null;
                if (lookupField != null && lookupField.isTreatWildcardsAndOperatorsAsLiteral()) {
                    propertyValue = (propertyValueObject != null) ? StringUtils.replace(propertyValueObject.toString().trim(), "*", "\\*") : "";
                } else {
                    //propertyValue = (propertyValueObject != null) ? propertyValueObject.toString().trim() : "";
                    propertyValue = (propertyValueObject != null) ? StringUtils.replace(propertyValueObject.toString().trim(), "*", "%") : "";
                }

                // if searchValue is empty and the key is not a valid property ignore
                boolean isCreated = createCriteria(businessObject, propertyValue, propertyName, criteria);
                if (!isCreated) {
                    continue;
                }
            }
        }
        catch (Exception e) {
            LOG.error("OJBUtility.buildCriteriaFromMap()" + e);
        }
        return criteria;
    }

    /**
     * Limit the size of the result set from the given query operation
     * 
     * @param query the given query operation
     */
    public static void limitResultSize(Query query) {
        int startingIndex = 1;
        int endingIndex = getResultLimit().intValue();

        query.setStartAtIndex(startingIndex);
        query.setEndAtIndex(endingIndex);
    }

    /**
     * This method calculates the actual size of given selection results
     * 
     * @param result the given selection results
     * @param recordCount the possible number of the given results
     * @param fieldValues the input field map
     * @param businessObject the given business object
     * @return the actual size of given selection results
     */
    public static Long getResultActualSize(Collection result, Integer recordCount, Map fieldValues, Object businessObject) {
        int resultSize = result.size();
        Integer limit = getResultLimit();
        Long resultActualSize = new Long(resultSize);

        if (recordCount > limit) {
            long actualCount = recordCount.longValue() + resultSize - limit.longValue();
            resultActualSize = new Long(actualCount);
        }
        return resultActualSize;
    }

    /**
     * This method gets the size of a result set from the given search criteria
     * 
     * @param fieldValues the input field map
     * @param businessObject the given business object
     * @return the size of a result set from the given search criteria
     */
    public static Long getResultSizeFromMap(Map fieldValues, Object businessObject) {
        LookupDao lookupDao = SpringContext.getBean(LookupDao.class);
        return lookupDao.findCountByMap(businessObject, fieldValues);
    }

    /**
     * This method gets the limit of the selection results
     * 
     * @return the limit of the selection results
     */
    public static Integer getResultLimit() {
        // get the result limit number from configuration
        String limitConfig = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.NERVOUS_SYSTEM_LOOKUP.class, KFSConstants.LOOKUP_RESULTS_LIMIT_URL_KEY);

        Integer limit = Integer.MAX_VALUE;
        if (limitConfig != null) {
            limit = Integer.valueOf(limitConfig);
        }
        return limit;
    }

    /**
     * This method build OJB criteria from the given property value and name
     */
    public static boolean createCriteria(Object businessObject, String propertyValue, String propertyName, Criteria criteria) {
        LookupDao lookupDao = SpringContext.getBean(LookupDao.class);
        return lookupDao.createCriteria(businessObject, propertyValue, propertyName, criteria);
    }
}
