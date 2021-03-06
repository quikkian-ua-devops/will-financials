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
package org.kuali.kfs.krad.util;

import org.junit.Test;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.springframework.util.AutoPopulatingList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class tests the ErrorMap methods.
 */
public class MessageMapTest {

    /**
     * ErrorMap should only allow String keys and values.
     */
    @Test
    public void testPut() {
        MessageMap testMap = new MessageMap();

        // should be ok putting strings
        try {
            testMap.putError("accountNbr", RiceKeyConstants.ERROR_INACTIVE);
        } catch (RuntimeException e) {
            fail("ErrorMap threw exception adding string pair");
        }
    }

    /**
     * Test all errors are getting added and counted correctly.
     */
    @Test
    public void testErrorCount() {
        MessageMap testMap = new MessageMap();

        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INACTIVE);
        assertTrue(testMap.getErrorCount() == 1);

        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INVALID_FORMAT);
        assertTrue(testMap.getErrorCount() == 2);

        testMap.putError("chartCode", RiceKeyConstants.ERROR_INVALID_FORMAT);
        testMap.putError("projectCode", RiceKeyConstants.ERROR_INVALID_FORMAT);
        testMap.putError("objectCode", RiceKeyConstants.ERROR_INVALID_FORMAT);
        assertTrue(testMap.getErrorCount() == 5);

        testMap.removeAllErrorMessagesForProperty("accountNbr");
        assertTrue(testMap.getErrorCount() == 3);
    }

    /**
     * Test messages are getting accumulated correctly for a property.
     */
    @Test
    public void testFieldMessages() {
        MessageMap testMap = new MessageMap();

        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INACTIVE);
        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INVALID_FORMAT);
        testMap.putError("accountNbr", RiceKeyConstants.ERROR_PHONE_NUMBER);
        assertEquals(3, testMap.countFieldMessages("accountNbr"));
        assertTrue(testMap.fieldHasMessage("accountNbr", RiceKeyConstants.ERROR_INACTIVE));
        assertTrue(testMap.fieldHasMessage("accountNbr", RiceKeyConstants.ERROR_INVALID_FORMAT));
        assertTrue(testMap.fieldHasMessage("accountNbr", RiceKeyConstants.ERROR_PHONE_NUMBER));
    }

    /**
     * Test error prepending and lack thereof.
     */
    @Test
    public void testErrorPath() {
        MessageMap testMap = new MessageMap();

        assertTrue(testMap.getKeyPath("accountNbr", true).equals("accountNbr"));
        testMap.addToErrorPath("document");
        assertTrue(testMap.getKeyPath("accountNbr", true).equals("document.accountNbr"));
        assertTrue(testMap.getKeyPath("accountNbr", false).equals("accountNbr"));
        testMap.removeFromErrorPath("document");
        assertTrue(testMap.getKeyPath("accountNbr", true).equals("accountNbr"));
        assertTrue(testMap.getKeyPath("accountNbr", false).equals("accountNbr"));
        testMap.addToErrorPath("document");
        testMap.addToErrorPath("newAccountingLine");
        assertTrue(testMap.getKeyPath("accountNbr", true).equals("document.newAccountingLine.accountNbr"));
        assertTrue(testMap.getKeyPath("accountNbr", false).equals("accountNbr"));

        // Verify that with putError, the error path is prepended to the propertyName
        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INACTIVE);
        assertEquals(1, testMap.countFieldMessages("document.newAccountingLine.accountNbr"));
        assertTrue(testMap.fieldHasMessage("document.newAccountingLine.accountNbr", RiceKeyConstants.ERROR_INACTIVE));

        testMap.removeAllErrorMessagesForProperty("document.newAccountingLine.accountNbr");

        // Verify that with putErrorWithoutFullErrorPath, nothing is prepended to the propertyName
        testMap.putErrorWithoutFullErrorPath("accountNbr", RiceKeyConstants.ERROR_INACTIVE);
        assertEquals(1, testMap.countFieldMessages("accountNbr"));
        assertTrue(testMap.fieldHasMessage("accountNbr", RiceKeyConstants.ERROR_INACTIVE));
        assertFalse(testMap.fieldHasMessage("document.newAccountingLine.accountNbr", RiceKeyConstants.ERROR_INACTIVE));

        // global key should not be prepended with key path
        assertTrue(testMap.getKeyPath(KRADConstants.GLOBAL_ERRORS, true).equals(KRADConstants.GLOBAL_ERRORS));

        assertTrue(testMap.getKeyPath("projectCode.code", true).equals("document.newAccountingLine.projectCode.code"));
        testMap.removeFromErrorPath("newAccountingLine");
        assertTrue(testMap.getKeyPath("accountNbr", true).equals("document.accountNbr"));
        testMap.removeFromErrorPath("document");
        assertTrue(testMap.getKeyPath("accountNbr", true).equals("accountNbr"));

    }

    /**
     * Test that properties added with errors are being kept.
     */
    @Test
    public void testPropertiesWithErrors() {
        MessageMap testMap = new MessageMap();

        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INACTIVE);
        testMap.putError("projectCode", RiceKeyConstants.ERROR_INACTIVE);
        testMap.putError("chartCode", RiceKeyConstants.ERROR_INACTIVE);
        testMap.putError("objectCode", RiceKeyConstants.ERROR_INACTIVE);
        testMap.putError("subAccountNbr", RiceKeyConstants.ERROR_INACTIVE);

        assertTrue(testMap.getPropertiesWithErrors().contains("accountNbr"));
        assertTrue(testMap.getPropertiesWithErrors().contains("projectCode"));
        assertTrue(testMap.getPropertiesWithErrors().contains("chartCode"));
        assertTrue(testMap.getPropertiesWithErrors().contains("objectCode"));
        assertTrue(testMap.getPropertiesWithErrors().contains("subAccountNbr"));
    }

    /**
     * Test message parameters are being correctly added and associated with an error message.
     */
    @Test
    public void testMessageParameters() {
        MessageMap testMap = new MessageMap();

        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INACTIVE, "Account Number");
        testMap.putError("accountNbr", RiceKeyConstants.ERROR_REQUIRED, "Account Number");
        // check duplicate message doesn't get added
        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INACTIVE, "Account Number");
        testMap.putError("chartCode", RiceKeyConstants.ERROR_REQUIRED, "Chart Code");

        assertEquals(3, testMap.getErrorCount());

        AutoPopulatingList errorMessages = testMap.getMessages("accountNbr");
        assertEquals(2, errorMessages.size());
        checkMessageParemeters(errorMessages, 0, RiceKeyConstants.ERROR_INACTIVE, new String[]{"Account Number"});
        checkMessageParemeters(errorMessages, 1, RiceKeyConstants.ERROR_REQUIRED, new String[]{"Account Number"});

        errorMessages = testMap.getMessages("chartCode");
        assertEquals(1, errorMessages.size());
        checkMessageParemeters(errorMessages, 0, RiceKeyConstants.ERROR_REQUIRED, new String[]{"Chart Code"});
    }

    private void checkMessageParemeters(AutoPopulatingList errorMessages, int messageIndex, String expectedKeyConstant, String[] expectedParameters) {
        ErrorMessage message1 = (ErrorMessage) errorMessages.get(messageIndex);
        assertEquals(expectedKeyConstant, message1.getErrorKey());
        assertTrue(Arrays.equals(message1.getMessageParameters(), expectedParameters));
    }

    /**
     * Verify that using the same error message multiple times correctly stores different parameters each time. (Reproduces bug
     * KULNRVSYS-943).
     */
    @Test
    public void testMessageCollisions() {
        final String PROPERTY_NAME = "document.sourceAccounting*,document.targetAccounting*,newSourceLine*,newTargetLine*";
        MessageMap testMap = new MessageMap();

        testMap.putError(PROPERTY_NAME, "error.inactive", "Chart Code");
        testMap.putError(PROPERTY_NAME, "error.document.subAccountClosed", "Sub-Account Number");
        testMap.putError(PROPERTY_NAME, "error.inactive", "Object Code");
        testMap.putError(PROPERTY_NAME, "error.inactive", "SubObject Code");
        testMap.putError(PROPERTY_NAME, "error.inactive", "Project Code");

        assertEquals(5, testMap.getErrorCount());

        // retrieve error messages for the one known key
        Object thing = testMap.getErrorMessagesForProperty(PROPERTY_NAME);

        Set usedParams = new HashSet();
        for (Iterator i = testMap.getAllPropertiesAndErrors().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();

            String propertyKey = (String) entry.getKey();
            AutoPopulatingList messageList = (AutoPopulatingList) entry.getValue();
            for (Iterator j = messageList.iterator(); j.hasNext(); ) {
                ErrorMessage message = (ErrorMessage) j.next();

                String[] params = message.getMessageParameters();
                if (usedParams.contains(params)) {
                    fail("usedParams contains duplicate parameters object '" + params + "'");
                }
                usedParams.add(params);
            }
        }
    }

    private final static String MIXED_LIST_PATTERN = "document.sourceAccounting*,document.targetAccounting*,foo,bar,newSourceLine*,newTargetLine*";

    @Test
    public void testContainsKeyMatchingPattern_mixedList_empty() {
        assertEquals(false, new MessageMap().containsKeyMatchingPattern(MIXED_LIST_PATTERN));
    }

    @Test
    public void testContainsKeyMatchingPattern_mixedList_simpleNoMatch() {
        MessageMap testMap = new MessageMap();
        testMap.putError("xxx", "error.inactive", "Chart Code");
        testMap.putError("yyy", "error.inactive", "Chart Code");
        assertEquals(false, testMap.containsKeyMatchingPattern(MIXED_LIST_PATTERN));
    }

    @Test
    public void testContainsKeyMatchingPattern_mixedList_simpleMatch() {
        MessageMap testMap = new MessageMap();
        testMap.putError("xxx", "error.inactive", "Chart Code");
        testMap.putError("foo", "error.inactive", "Chart Code");
        testMap.putError("yyy", "error.inactive", "Chart Code");
        assertEquals(true, testMap.containsKeyMatchingPattern(MIXED_LIST_PATTERN));
    }

    @Test
    public void testContainsKeyMatchingPattern_mixedList_wildcardMatch() {
        MessageMap testMap = new MessageMap();
        testMap.putError("xxx", "error.inactive", "Chart Code");
        testMap.putError("document.targetAccountingLine.something", "error.inactive", "Chart Code");
        testMap.putError("yyy", "error.inactive", "Chart Code");
        assertEquals(true, testMap.containsKeyMatchingPattern(MIXED_LIST_PATTERN));
    }


    @Test
    public void testReplace_testEquals() {
        final MessageMap constantMap = buildReplaceErrorMap();
        MessageMap replaceMap = buildReplaceErrorMap();

        assertEquals(replaceMap, replaceMap);
        assertEquals(replaceMap, constantMap);
        assertEquals(constantMap, replaceMap);

        replaceMap.putError("somethingElse", RiceKeyConstants.ERROR_INACTIVE, "Account Number");

        assertFalse(replaceMap.equals(constantMap));
    }


    @Test
    public void testReplace_noMatchingProperty() {
        final MessageMap constantMap = buildReplaceErrorMap();
        MessageMap replaceMap = buildReplaceErrorMap();

        assertTrue(replaceMap.equals(constantMap));
        assertFalse(replaceMap.containsMessageKey("fooKey"));

        boolean replaced = replaceMap.replaceError("fooName", "fooKey", "fooReplaceKey");
        assertFalse(replaced);

        assertTrue(replaceMap.equals(constantMap));
        assertFalse(replaceMap.containsMessageKey("fooKey"));
    }

    @Test
    public void testReplace_matchingProperty_noMatchingKey() {
        final MessageMap constantMap = buildReplaceErrorMap();
        MessageMap replaceMap = buildReplaceErrorMap();

        assertTrue(replaceMap.equals(constantMap));
        assertFalse(replaceMap.containsMessageKey("fooKey"));

        boolean replaced = replaceMap.replaceError("accountNbr", "fooKey", "fooReplaceKey");
        assertFalse(replaced);

        assertTrue(replaceMap.equals(constantMap));
        assertFalse(replaceMap.containsMessageKey("fooKey"));
    }


    @Test
    public void testReplace_matchingProperty_matchingKey_noParams() {
        final MessageMap constantMap = buildReplaceErrorMap();
        MessageMap replaceMap = buildReplaceErrorMap();

        assertTrue(replaceMap.equals(constantMap));
        assertTrue(replaceMap.containsMessageKey(RiceKeyConstants.ERROR_INACTIVE));
        assertFalse(replaceMap.containsMessageKey(RiceKeyConstants.ERROR_NOT_AMONG));

        AutoPopulatingList preMessages = replaceMap.getMessages("accountNbr");
        assertEquals(2, preMessages.size());

        boolean replaced = replaceMap.replaceError("accountNbr", RiceKeyConstants.ERROR_INACTIVE, RiceKeyConstants.ERROR_NOT_AMONG);
        assertTrue(replaced);

        assertFalse(replaceMap.equals(constantMap));
        assertFalse(replaceMap.containsMessageKey(RiceKeyConstants.ERROR_INACTIVE));
        assertTrue(replaceMap.containsMessageKey(RiceKeyConstants.ERROR_NOT_AMONG));

        AutoPopulatingList postMessages = replaceMap.getMessages("accountNbr");
        assertEquals(2, postMessages.size());

        int replacedCount = 0;
        for (Iterator i = postMessages.iterator(); i.hasNext(); ) {
            ErrorMessage em = (ErrorMessage) i.next();
            if (em.getErrorKey().equals(RiceKeyConstants.ERROR_NOT_AMONG)) {
                String[] params = em.getMessageParameters();
                assertEquals(0, params.length);

                ++replacedCount;
            }
        }
        assertEquals(1, replacedCount);
    }

    @Test
    public void testReplace_matchingProperty_matchingKey_withParams() {
        final MessageMap constantMap = buildReplaceErrorMap();
        MessageMap replaceMap = buildReplaceErrorMap();

        assertTrue(replaceMap.equals(constantMap));
        assertTrue(replaceMap.containsMessageKey(RiceKeyConstants.ERROR_INACTIVE));
        assertFalse(replaceMap.containsMessageKey(RiceKeyConstants.ERROR_NOT_AMONG));

        AutoPopulatingList preMessages = replaceMap.getMessages("accountNbr");
        assertEquals(2, preMessages.size());

        boolean replaced = replaceMap.replaceError("accountNbr", RiceKeyConstants.ERROR_INACTIVE, RiceKeyConstants.ERROR_NOT_AMONG, "zero", "one");
        assertTrue(replaced);

        assertFalse(replaceMap.equals(constantMap));
        assertFalse(replaceMap.containsMessageKey(RiceKeyConstants.ERROR_INACTIVE));
        assertTrue(replaceMap.containsMessageKey(RiceKeyConstants.ERROR_NOT_AMONG));

        AutoPopulatingList postMessages = replaceMap.getMessages("accountNbr");
        assertEquals(2, postMessages.size());

        int replacedCount = 0;
        for (Iterator i = postMessages.iterator(); i.hasNext(); ) {
            ErrorMessage em = (ErrorMessage) i.next();
            if (em.getErrorKey().equals(RiceKeyConstants.ERROR_NOT_AMONG)) {
                String[] params = em.getMessageParameters();
                assertEquals(2, params.length);
                assertEquals("zero", params[0]);
                assertEquals("one", params[1]);

                ++replacedCount;
            }
        }
        assertEquals(1, replacedCount);
    }


    private MessageMap buildReplaceErrorMap() {
        MessageMap testMap = new MessageMap();

        testMap.putError("accountNbr", RiceKeyConstants.ERROR_INACTIVE, "Account Number");
        testMap.putError("accountNbr", RiceKeyConstants.ERROR_REQUIRED, "Account Number");
        testMap.putError("chartCode", RiceKeyConstants.ERROR_REQUIRED, "Chart Code");

        return testMap;
    }
}
