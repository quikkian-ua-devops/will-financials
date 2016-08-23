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
package org.kuali.kfs.krad.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.rice.krad.bo.BusinessObject;

import java.beans.PropertyDescriptor;

/**
 * Pulled this logic out of the org.kuali.rice.krad.workflow.service.impl.WorkflowAttributePropertyResolutionServiceImpl
 * since it wasn't really service logic at all, just util logic.
 *
 */
public class DataTypeUtil {

	public static String determineFieldDataType(Class<? extends BusinessObject> businessObjectClass, String attributeName) {
		final Class<?> attributeClass = thieveAttributeClassFromBusinessObjectClass(businessObjectClass, attributeName);
		return determineDataType(attributeClass);
	}

	public static String determineDataType(Class<?> attributeClass) {
        if (isStringy(attributeClass)) return org.kuali.kfs.krad.util.KRADConstants.DATA_TYPE_STRING; // our most common case should go first
        if (isDecimaltastic(attributeClass)) return org.kuali.kfs.krad.util.KRADConstants.DATA_TYPE_FLOAT;
        if (isDateLike(attributeClass)) return org.kuali.kfs.krad.util.KRADConstants.DATA_TYPE_DATE;
        if (isIntsy(attributeClass)) return org.kuali.kfs.krad.util.KRADConstants.DATA_TYPE_LONG;
        if (isBooleanable(attributeClass)) return org.kuali.kfs.krad.util.KRADConstants.DATA_TYPE_BOOLEAN;
        return org.kuali.kfs.krad.util.KRADConstants.DATA_TYPE_STRING; // default to String
    }

    /**
     * Determines if the given Class is a String
     * @param clazz the class to check for Stringiness
     * @return true if the Class is a String, false otherwise
     */
	public static boolean isStringy(Class clazz) {
        return java.lang.String.class.isAssignableFrom(clazz);
    }

    /**
     * Determines if the given class is enough like a date to store values of it as a SearchableAttributeDateTimeValue
     * @param clazz the class to determine the type of
     * @return true if it is like a date, false otherwise
     */
	public static boolean isDateLike(Class clazz) {
        return java.util.Date.class.isAssignableFrom(clazz);
    }

    /**
     * Determines if the given class is enough like a Float to store values of it as a SearchableAttributeFloatValue
     * @param clazz the class to determine of the type of
     * @return true if it is like a "float", false otherwise
     */
	public static boolean isDecimaltastic(Class clazz) {
        return java.lang.Double.class.isAssignableFrom(clazz) || java.lang.Float.class.isAssignableFrom(clazz) || clazz.equals(Double.TYPE) || clazz.equals(Float.TYPE) || java.math.BigDecimal.class.isAssignableFrom(clazz) || org.kuali.rice.core.api.util.type.KualiDecimal.class.isAssignableFrom(clazz);
    }

    /**
     * Determines if the given class is enough like a "long" to store values of it as a SearchableAttributeLongValue
     * @param clazz the class to determine the type of
     * @return true if it is like a "long", false otherwise
     */
	public static boolean isIntsy(Class clazz) {
        return java.lang.Integer.class.isAssignableFrom(clazz) || java.lang.Long.class.isAssignableFrom(clazz) || java.lang.Short.class.isAssignableFrom(clazz) || java.lang.Byte.class.isAssignableFrom(clazz) || java.math.BigInteger.class.isAssignableFrom(clazz) || clazz.equals(Integer.TYPE) || clazz.equals(Long.TYPE) || clazz.equals(Short.TYPE) || clazz.equals(Byte.TYPE);
    }

    /**
     * Determines if the given class is enough like a boolean, to index it as a String "Y" or "N"
     * @param clazz the class to determine the type of
     * @return true if it is like a boolean, false otherwise
     */
	public static boolean isBooleanable(Class clazz) {
        return java.lang.Boolean.class.isAssignableFrom(clazz) || clazz.equals(Boolean.TYPE);
    }

    /**
     * Given a BusinessObject class and an attribute name, determines the class of that attribute on the BusinessObject class
     * @param boClass a class extending BusinessObject
     * @param attributeKey the name of a field on that class
     * @return the Class of the given attribute
     */
    private static Class thieveAttributeClassFromBusinessObjectClass(Class<? extends BusinessObject> boClass, String attributeKey) {
        for (PropertyDescriptor prop : PropertyUtils.getPropertyDescriptors(boClass)) {
            if (prop.getName().equals(attributeKey)) {
                return prop.getPropertyType();
            }
        }
        return null;
    }

}
