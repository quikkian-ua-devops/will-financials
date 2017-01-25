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
package org.kuali.kfs.krad.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.krad.datadictionary.exception.UnknownBusinessClassAttributeException;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides static utility methods for use within the lookup framework
 */
public class LookupUtils {

    /**
     * Uses the DataDictionary to determine whether to force uppercase the value, and if it should, then it does the
     * uppercase, and returns the upper-cased value.
     *
     * @param dataObjectClass Parent DO class that the fieldName is a member of.
     * @param fieldName       Name of the field to be forced to uppercase.
     * @param fieldValue      Value of the field that may be uppercased.
     * @return The correctly uppercased fieldValue if it should be uppercased, otherwise fieldValue is returned
     * unchanged.
     */
    public static String forceUppercase(Class<?> dataObjectClass, String fieldName, String fieldValue) {
        // short-circuit to exit if there isnt enough information to do the forceUppercase
        if (StringUtils.isBlank(fieldValue)) {
            return fieldValue;
        }

        // parameter validation
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("Parameter dataObjectClass passed in with null value.");
        }

        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("Parameter fieldName passed in with empty value.");
        }

        if (!KRADServiceLocatorWeb.getDataDictionaryService().isAttributeDefined(dataObjectClass, fieldName)
            .booleanValue()) {
            return fieldValue;
        }

        boolean forceUpperCase = false;
        try {
            forceUpperCase = KRADServiceLocatorWeb.getDataDictionaryService()
                .getAttributeForceUppercase(dataObjectClass, fieldName).booleanValue();
        } catch (UnknownBusinessClassAttributeException ubae) {
            // do nothing, don't alter the fieldValue
        }
        if (forceUpperCase && !fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
            return fieldValue.toUpperCase();
        }

        return fieldValue;
    }

    /**
     * Uses the DataDictionary to determine whether to force uppercase the values, and if it should, then it does the
     * uppercase, and returns the upper-cased Map of fieldname/fieldValue pairs.
     *
     * @param dataObjectClass Parent DO class that the fieldName is a member of.
     * @param fieldValues     A Map<String,String> where the key is the fieldName and the value is the fieldValue.
     * @return The same Map is returned, with the appropriate values uppercased (if any).
     */
    public static Map<String, String> forceUppercase(Class<?> dataObjectClass, Map<String, String> fieldValues) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("Parameter boClass passed in with null value.");
        }

        if (fieldValues == null) {
            throw new IllegalArgumentException("Parameter fieldValues passed in with null value.");
        }

        for (String fieldName : fieldValues.keySet()) {
            fieldValues.put(fieldName, forceUppercase(dataObjectClass, fieldName, (String) fieldValues.get(fieldName)));
        }

        return fieldValues;
    }

    /**
     * Parses and returns the lookup result set limit, checking first for the limit
     * for the class being looked up, and then the global application limit if there isn't a limit
     * specific to this data object class
     *
     * @param dataObjectClass - class to get limit for
     */
    public static Integer getSearchResultsLimit(Class dataObjectClass) {
        Integer limit = KRADServiceLocatorWeb.getViewDictionaryService().getResultSetLimitForLookup(dataObjectClass);
        if (limit == null) {
            limit = getApplicationSearchResultsLimit();
        }

        return limit;
    }

    /**
     * Retrieves the default application search limit configured through
     * a system parameter
     */
    public static Integer getApplicationSearchResultsLimit() {
        String limitString = CoreFrameworkServiceLocator.getParameterService()
            .getParameterValueAsString(KRADConstants.KNS_NAMESPACE,
                KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE,
                KRADConstants.SystemGroupParameterNames.LOOKUP_RESULTS_LIMIT);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }

        return null;
    }

    /**
     * Determines what Timestamp should be used for active queries on effective dated records. Determination made as
     * follows:
     * <p>
     * <ul>
     * <li>Use activeAsOfDate value from search values Map if value is not empty</li>
     * <li>If search value given, try to convert to sql date, if conversion fails, try to convert to Timestamp</li>
     * <li>If search value empty, use current Date</li>
     * <li>If Timestamp value not given, create Timestamp from given Date setting the time as 1 second before midnight
     * </ul>
     *
     * @param searchValues - Map containing search key/value pairs
     * @return Timestamp to be used for active criteria
     */
    public static Timestamp getActiveDateTimestampForCriteria(Map searchValues) {
        Date activeDate = CoreApiServiceLocator.getDateTimeService().getCurrentSqlDate();
        Timestamp activeTimestamp = null;
        if (searchValues.containsKey(KRADPropertyConstants.ACTIVE_AS_OF_DATE)) {
            String activeAsOfDate = (String) searchValues.get(KRADPropertyConstants.ACTIVE_AS_OF_DATE);
            if (StringUtils.isNotBlank(activeAsOfDate)) {
                try {
                    activeDate = CoreApiServiceLocator.getDateTimeService()
                        .convertToSqlDate(ObjectUtils.clean(activeAsOfDate));
                } catch (ParseException e) {
                    // try to parse as timestamp
                    try {
                        activeTimestamp = CoreApiServiceLocator.getDateTimeService()
                            .convertToSqlTimestamp(ObjectUtils.clean(activeAsOfDate));
                    } catch (ParseException e1) {
                        throw new RuntimeException("Unable to convert date: " + ObjectUtils.clean(activeAsOfDate));
                    }
                }
            }
        }

        // if timestamp not given set to 1 second before midnight on the given date
        if (activeTimestamp == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(activeDate);
            cal.set(Calendar.HOUR, cal.getMaximum(Calendar.HOUR));
            cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
            cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));

            activeTimestamp = new Timestamp(cal.getTime().getTime());
        }

        return activeTimestamp;
    }

    /**
     * Changes from/to dates into the range operators the lookupable dao expects ("..",">" etc) this method modifies
     * the
     * passed in map and returns a list containing only the modified fields
     *
     * @param searchCriteria - map of criteria currently set for which the date criteria will be adjusted
     */
    public static Map<String, String> preprocessDateFields(Map<String, String> searchCriteria) {
        Map<String, String> fieldsToUpdate = new HashMap<String, String>();
        Set<String> fieldsForLookup = searchCriteria.keySet();
        for (String propName : fieldsForLookup) {
            if (propName.startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
                String from_DateValue = searchCriteria.get(propName);
                String dateFieldName =
                    StringUtils.remove(propName, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
                String to_DateValue = searchCriteria.get(dateFieldName);
                String newPropValue = to_DateValue;// maybe clean above with
                // ObjectUtils.clean(propertyValue)
                if (StringUtils.isNotEmpty(from_DateValue) && StringUtils.isNotEmpty(to_DateValue)) {
                    newPropValue = from_DateValue + SearchOperator.BETWEEN + to_DateValue;
                } else if (StringUtils.isNotEmpty(from_DateValue) && StringUtils.isEmpty(to_DateValue)) {
                    newPropValue = SearchOperator.GREATER_THAN_EQUAL.op() + from_DateValue;
                } else if (StringUtils.isNotEmpty(to_DateValue) && StringUtils.isEmpty(from_DateValue)) {
                    newPropValue = SearchOperator.LESS_THAN_EQUAL.op() + to_DateValue;
                } // could optionally continue on else here

                fieldsToUpdate.put(dateFieldName, newPropValue);
            }
        }

        // update lookup values from found date values to update
        Set<String> keysToUpdate = fieldsToUpdate.keySet();
        for (String updateKey : keysToUpdate) {
            searchCriteria.put(updateKey, fieldsToUpdate.get(updateKey));
        }

        return fieldsToUpdate;
    }

    /**
     * Checks whether any of the fieldValues being passed refer to a property within an ExternalizableBusinessObject.
     */
    public static boolean hasExternalBusinessObjectProperty(Class<?> boClass,
                                                            Map<String, String> fieldValues) throws IllegalAccessException, InstantiationException {
        Object sampleBo = boClass.newInstance();
        for (String key : fieldValues.keySet()) {
            if (isExternalBusinessObjectProperty(sampleBo, key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether the given property represents a property within an EBO starting with the sampleBo object given.
     * This is used to determine if a criteria needs to be applied to the EBO first,
     * before sending to the normal lookup DAO.
     */
    public static boolean isExternalBusinessObjectProperty(Object sampleBo, String propertyName) {
        if (propertyName.indexOf(".") > 0 && !StringUtils.contains(propertyName, "add.")) {
            Class<?> propertyClass =
                ObjectPropertyUtils.getPropertyType(sampleBo, StringUtils.substringBeforeLast(propertyName, "."));
            if (propertyClass != null) {
                return ExternalizableBusinessObjectUtils.isExternalizableBusinessObjectInterface(propertyClass);
            }
        }

        return false;
    }

    /**
     * Returns a map stripped of any properties which refer to ExternalizableBusinessObjects. These values may not be
     * passed into the lookup service, since the objects they refer to are not in the
     * local database.
     */
    public static Map<String, String> removeExternalizableBusinessObjectFieldValues(Class<?> boClass,
                                                                                    Map<String, String> fieldValues) throws IllegalAccessException, InstantiationException {
        Map<String, String> eboFieldValues = new HashMap<String, String>();
        Object sampleBo = boClass.newInstance();
        for (String key : fieldValues.keySet()) {
            if (!isExternalBusinessObjectProperty(sampleBo, key)) {
                eboFieldValues.put(key, fieldValues.get(key));
            }
        }

        return eboFieldValues;
    }

    /**
     * Return the EBO fieldValue entries explicitly for the given eboPropertyName. (I.e., any properties with the given
     * property name as a prefix.
     */
    public static Map<String, String> getExternalizableBusinessObjectFieldValues(String eboPropertyName,
                                                                                 Map<String, String> fieldValues) {
        Map<String, String> eboFieldValues = new HashMap<String, String>();
        for (String key : fieldValues.keySet()) {
            if (key.startsWith(eboPropertyName + ".")) {
                eboFieldValues.put(StringUtils.substringAfterLast(key, "."), fieldValues.get(key));
            }
        }

        return eboFieldValues;
    }

    /**
     * Get the complete list of all properties referenced in the fieldValues that are ExternalizableBusinessObjects.
     * <p>
     * This is a list of the EBO object references themselves, not of the properties within them.
     */
    public static List<String> getExternalizableBusinessObjectProperties(Class<?> boClass,
                                                                         Map<String, String> fieldValues) throws IllegalAccessException, InstantiationException {
        Set<String> eboPropertyNames = new HashSet<String>();

        Object sampleBo = boClass.newInstance();
        for (String key : fieldValues.keySet()) {
            if (isExternalBusinessObjectProperty(sampleBo, key)) {
                eboPropertyNames.add(StringUtils.substringBeforeLast(key, "."));
            }
        }

        return new ArrayList<String>(eboPropertyNames);
    }

    /**
     * Given an property on the main BO class, return the defined type of the ExternalizableBusinessObject. This will
     * be used by other code to determine the correct module service to call for the lookup.
     *
     * @param boClass
     * @param propertyName
     * @return
     */
    public static Class<? extends ExternalizableBusinessObject> getExternalizableBusinessObjectClass(Class<?> boClass,
                                                                                                     String propertyName) throws IllegalAccessException, InstantiationException {
        return (Class<? extends ExternalizableBusinessObject>) ObjectPropertyUtils
            .getPropertyType(boClass.newInstance(), StringUtils.substringBeforeLast(propertyName, "."));
    }

}
