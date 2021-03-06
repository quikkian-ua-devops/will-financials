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
package org.kuali.kfs.krad.bo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * This is a description of what this class does - chang don't forget to fill this in.
 */
public class BusinessObjectAttributeEntryTest {

    BusinessObjectAttributeEntry dummyBOAE;

    @Before
    public void setUp() throws Exception {
        dummyBOAE = new BusinessObjectAttributeEntry();
    }

    /**
     * This method ...
     *
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        dummyBOAE = null;
    }

    @Test
    public void testAttributeControlType() {
        dummyBOAE.setAttributeControlType("ControlType");
        assertEquals("Testing AttributeControlType in BusiessObjectAtributeEntry", "ControlType", dummyBOAE.getAttributeControlType());
    }

    @Test
    public void testAttributeDescription() {
        dummyBOAE.setAttributeDescription("attributeDescription");
        assertEquals("Testing AttributeDescription in BusiessObjectAtributeEntry", "attributeDescription", dummyBOAE.getAttributeDescription());
    }

    @Test
    public void testAttributeFormatterClassName() {
        dummyBOAE.setAttributeFormatterClassName("attributeFormatterClassName");
        assertEquals("Testing AttributeFormatterClassName in BusiessObjectAtributeEntry", "attributeFormatterClassName", dummyBOAE.getAttributeFormatterClassName());
    }

    @Test
    public void testAttributeLabel() {
        dummyBOAE.setAttributeLabel("attributeLabel");
        assertEquals("Testing AttributeLabel in BusiessObjectAtributeEntry", "attributeLabel", dummyBOAE.getAttributeLabel());
    }

    @Test
    public void testAttributeMaxLength() {
        dummyBOAE.setAttributeMaxLength("100");
        assertEquals("Testing AttributeMaxLength in BusiessObjectAtributeEntry", "100", dummyBOAE.getAttributeMaxLength());
    }

    @Test
    public void testAttributeName() {
        dummyBOAE.setAttributeName("AttributeName");
        assertEquals("Testing AttributeName in BusiessObjectAtributeEntry", "AttributeName", dummyBOAE.getAttributeName());
    }

    @Test
    public void testAttributeShortLabel() {
        dummyBOAE.setAttributeShortLabel("AttributeShortLabel");
        assertEquals("Testing AttributeShortLabel in BusiessObjectAtributeEntry", "AttributeShortLabel", dummyBOAE.getAttributeShortLabel());
    }

    @Test
    public void testAttributeSummary() {
        dummyBOAE.setAttributeSummary("AttributeSummary");
        assertEquals("Testing AttributeSummary in BusiessObjectAtributeEntry", "AttributeSummary", dummyBOAE.getAttributeSummary());
    }


    @Test
    public void testAttributeValidatingExpression() {
        dummyBOAE.setAttributeValidatingExpression("AttributeValidatingExpression");
        assertEquals("Testing AttributeValidatingExpression in BusiessObjectAtributeEntry", "AttributeValidatingExpression", dummyBOAE.getAttributeValidatingExpression());
    }

    @Test
    public void testDictionaryBusinessObjectName() {
        dummyBOAE.setDictionaryBusinessObjectName("DictionaryBusinessObjectName");
        assertEquals("Testing DictionaryBusinessObjectName in BusiessObjectAtributeEntry", "DictionaryBusinessObjectName", dummyBOAE.getDictionaryBusinessObjectName());
    }
}
