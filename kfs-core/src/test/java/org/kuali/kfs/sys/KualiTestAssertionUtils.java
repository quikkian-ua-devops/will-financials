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
package org.kuali.kfs.sys;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.krad.bo.BusinessObject;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Contains assertion related convenience methods for testing (not for production use).
 *
 * @see org.kuali.rice.kns.util.AssertionUtils
 */
public class KualiTestAssertionUtils {


    public static void assertEquality(Object a, Object b) {
        assertNotNull(a);
        assertNotNull(b);

        assertTrue(a.equals(b));
    }

    public static void assertInequality(Object a, Object b) {
        assertNotNull(a);
        assertNotNull(b);

        assertFalse(a.equals(b));
    }

    /**
     * @see #assertSparselyEqualBean(String, Object, Object)
     */
    public static void assertSparselyEqualBean(Object expectedBean, Object actualBean) throws InvocationTargetException, NoSuchMethodException {
        assertSparselyEqualBean(null, expectedBean, actualBean);
    }

    /**
     * Asserts that the non-null non-BO properties of the expected bean are equal to those of the actual bean. Any null or
     * BusinessObject expected properties are ignored. Attributes are reflected bean-style via any public no-argument methods
     * starting with "get" (or "is" for booleans), including inherited methods. The expected and actual beans do not need to be of
     * the same class.
     * <p>
     * Reflection wraps primitives, so differences in primitiveness are ignored. Properties that are BusinessObjects (generally
     * relations based on foreign key properties) are also ignored, because this method is not testing OJB foreign key resolution
     * (e.g., via the <code>refresh</code> method), we do not want to have to put all the related BOs into the test fixture
     * (redundant with the foreign keys), and many (all?) of our BOs implement the <code>equals</code> method in terms of identity
     * so would fail this assertion anyway. This is a data-oriented assertion, for our data-oriented tests and persistence layer.
     *
     * @param message      a description of this test assertion
     * @param expectedBean a java bean containing expected properties
     * @param actualBean   a java bean containing actual properties
     * @throws InvocationTargetException if a getter method throws an exception (the cause)
     * @throws NoSuchMethodException     if an expected property does not exist in the actualBean
     */
    public static void assertSparselyEqualBean(String message, Object expectedBean, Object actualBean) throws InvocationTargetException, NoSuchMethodException {
        if (message == null) {
            message = "";
        } else {
            message = message + " ";
        }
        assertNotNull(message + "actual bean is null", actualBean);
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(expectedBean);
        for (int i = 0; i < descriptors.length; i++) {
            PropertyDescriptor descriptor = descriptors[i];
            if (PropertyUtils.getReadMethod(descriptor) != null) {
                try {
                    Object expectedValue = PropertyUtils.getSimpleProperty(expectedBean, descriptor.getName());
                    if (expectedValue != null && !(expectedValue instanceof BusinessObject)) {
                        assertEquals(message + descriptor.getName(), expectedValue, PropertyUtils.getSimpleProperty(actualBean, descriptor.getName()));
                    }
                } catch (IllegalAccessException e) {
                    throw new AssertionError(e); // can't happen because getReadMethod() returns only public methods
                }
            }
        }
    }

    public static void assertGlobalMessageMapContains(String expectedFieldName, String expectedErrorKey) {
        assertGlobalMessageMapContains("", expectedFieldName, expectedErrorKey, null, true);
    }

    public static void assertGlobalMessageMapContains(String message, String expectedFieldName, String expectedErrorKey) {
        assertGlobalMessageMapContains(message, expectedFieldName, expectedErrorKey, null, true);
    }

    public static void assertGlobalMessageMapContains(String expectedFieldName, String expectedErrorKey, String[] expectedErrorParameters) {
        assertGlobalMessageMapContains("", expectedFieldName, expectedErrorKey, expectedErrorParameters, true);
    }

    public static void assertGlobalMessageMapContains(String message, String expectedFieldName, String expectedErrorKey, String[] expectedErrorParameters) {
        assertGlobalMessageMapContains(message, expectedFieldName, expectedErrorKey, expectedErrorParameters, true);
    }

    public static void assertGlobalMessageMapNotContains(String expectedFieldName, String expectedErrorKey) {
        assertGlobalMessageMapContains("", expectedFieldName, expectedErrorKey, null, false);
    }

    public static void assertGlobalMessageMapNotContains(String message, String expectedFieldName, String expectedErrorKey) {
        assertGlobalMessageMapContains(message, expectedFieldName, expectedErrorKey, null, false);
    }

    private static void assertGlobalMessageMapContains(String message, String expectedFieldName, String expectedErrorKey, String[] expectedErrorParameters, boolean contains) {
        String header = message.length() == 0 ? "" : message + ": ";
        MessageMap map = GlobalVariables.getMessageMap();
        assertEquals(header + "no error path (global error map path size)", 0, map.getErrorPath().size());
        assertEquals(header + "error property '" + expectedFieldName + "' has message key " + expectedErrorKey + ": " + map, contains, map.fieldHasMessage(expectedFieldName, expectedErrorKey));

        if (contains && expectedErrorParameters != null) {
            List expectedParameterList = Arrays.asList(expectedErrorParameters);
            List fieldMessages = map.getMessages(expectedFieldName);
            if (fieldMessages != null) {
                for (Iterator i = fieldMessages.iterator(); i.hasNext(); ) {
                    ErrorMessage errorMessage = (ErrorMessage) i.next();
                    if (sparselyEqualLists(expectedParameterList, Arrays.asList(errorMessage.getMessageParameters()))) {
                        return; // success;
                    }
                }
            }
            fail(header + "error property '" + expectedFieldName + "' message key " + expectedErrorKey + " does not contain expected parameters " + expectedParameterList + ": " + map);
        }
    }

    private static boolean sparselyEqualLists(List expected, List actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        Iterator actualIterator = actual.iterator();
        for (Iterator expectedIterator = expected.iterator(); expectedIterator.hasNext(); ) {
            Object expectedItem = expectedIterator.next();
            Object actualItem = actualIterator.next();
            if (expectedItem != null && !expectedItem.equals(actualItem)) {
                return false;
            }
        }
        return true;
    }

    public static void assertGlobalMessageMapEmpty() {
        assertGlobalMessageMapSize("", 0);
    }

    public static void assertGlobalMessageMapEmpty(String message) {
        assertGlobalMessageMapSize(message, 0);
    }

    public static void assertGlobalMessageMapSize(int expectedSize) {
        assertGlobalMessageMapSize("", expectedSize);
    }

    public static void assertGlobalMessageMapSize(String message, int expectedSize) {
        String header = message.length() == 0 ? "" : message + ": ";
        MessageMap map = GlobalVariables.getMessageMap();
        assertEquals(header + "ThreadLocal MessageMap size: " + map, expectedSize, map.getNumberOfPropertiesWithErrors());
    }

    /**
     * Asserts that the given reference is either null or an OJB proxy that references to a nonexistant object. This is different
     * from assertNull() because ObjectUtils.isNull() checks for OJB proxies and goes to the database to see if real data is
     * available.
     *
     * @param expectedNull the reference to check
     */
    public static void assertNullHandlingOjbProxies(Object expectedNull) {
        assertNullHandlingOjbProxies(null, expectedNull);
    }

    /**
     * Asserts that the given reference is either null or an OJB proxy that references a nonexistant object. This is different from
     * assertNotNull() because ObjectUtils.isNull() checks for OJB proxies and goes to the database to see if real data is
     * available.
     *
     * @param message      the context of this assertion, if it fails
     * @param expectedNull the reference to check
     */
    public static void assertNullHandlingOjbProxies(String message, Object expectedNull) {
        assertTrue(message, ObjectUtils.isNull(expectedNull));
    }

    /**
     * Asserts that the given reference is neither null nor an OJB proxy that references a nonexistant object. This is different
     * from assertNotNull() because ObjectUtils.isNull() checks for OJB proxies and goes to the database to see if real data is
     * available.
     *
     * @param expectedNotNull the reference to check
     */
    public static void assertNotNullHandlingOjbProxies(Object expectedNotNull) {
        assertNotNullHandlingOjbProxies(null, expectedNotNull);
    }

    /**
     * Asserts that the given reference is neither null nor an OJB proxy that references a nonexistant object. This is different
     * from assertNotNull() because ObjectUtils.isNull() checks for OJB proxies and goes to the database to see if real data is
     * available.
     *
     * @param message         the context of this assertion, if it fails
     * @param expectedNotNull the reference to check
     */
    public static void assertNotNullHandlingOjbProxies(String message, Object expectedNotNull) {
        assertFalse(message, ObjectUtils.isNull(expectedNotNull));
    }
}
