/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.validation.Address;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.processor.ValidCharactersConstraintProcessor;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;

import java.util.ArrayList;
import java.util.List;


/**
 * Things this test should check:
 * <p>
 * 1. value with all valid characters. (success) {@link #testValueAllValidChars()}
 * 2. value with invalid characters. (failure) {@link #testValueNotValidChars()}
 * 3. value with all valid characters. (success) {@link #test2ValueAllValidChars()}
 * 4. value with invalid characters. (failure) {@link #test2ValueNotValidChars()}
 */
public class CharsetPatternConstraintTest {

    private AttributeDefinition countryDefinition;
    private AttributeDefinition postalCodeDefinition;

    private BusinessObjectEntry addressEntry;
    private DictionaryValidationResult dictionaryValidationResult;

    private ValidCharactersConstraintProcessor processor;

    private Address washingtonDCAddress = new Address("893	Presidential Ave", "(A_123) Suite 800.", "Washington", "DC", "NHW123A", "United States of America", null);
    private Address newYorkNYAddress = new Address("Presidential Street", "(A-123) Suite 800", "New York", "NY", "ZH 3456", "USA", null);
    private Address sydneyAUSAddress = new Address("Presidential Street-Ave.", "Suite_800.", "Sydney", "ZZ", "(ZH-5656)", "USA", null);

    private CharsetPatternConstraint countryCharsetPatternConstraint;
    private CharsetPatternConstraint postalCodeCharsetPatternConstraint;

    @Before
    public void setUp() throws Exception {

        processor = new ValidCharactersConstraintProcessor();

        dictionaryValidationResult = new DictionaryValidationResult();
        dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

        addressEntry = new BusinessObjectEntry();

        List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

        countryCharsetPatternConstraint = new CharsetPatternConstraint();
        countryCharsetPatternConstraint.setValidChars("USA");

        countryDefinition = new AttributeDefinition();
        countryDefinition.setName("country");
        countryDefinition.setValidCharactersConstraint(countryCharsetPatternConstraint);
        attributes.add(countryDefinition);


        postalCodeCharsetPatternConstraint = new CharsetPatternConstraint();
        postalCodeCharsetPatternConstraint.setValidChars("(ZH-123456)");

        postalCodeDefinition = new AttributeDefinition();
        postalCodeDefinition.setName("postalCode");
        postalCodeDefinition.setValidCharactersConstraint(postalCodeCharsetPatternConstraint);
        attributes.add(postalCodeDefinition);

        addressEntry.setAttributes(attributes);
    }

    @Test
    public void testValueAllValidChars() {
        ConstraintValidationResult result = process(newYorkNYAddress, "country", countryCharsetPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void testValueNotValidChars() {
        ConstraintValidationResult result = process(washingtonDCAddress, "country", countryCharsetPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void test2ValueAllValidChars() {
        ConstraintValidationResult result = process(sydneyAUSAddress, "postalCode", postalCodeCharsetPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.OK, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    @Test
    public void test2ValueNotValidChars() {
        ConstraintValidationResult result = process(washingtonDCAddress, "postalCode", postalCodeCharsetPatternConstraint);
        Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
        Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
        Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
        Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
    }

    private ConstraintValidationResult process(Object object, String attributeName, ValidCharactersConstraint constraint) {
        AttributeValueReader attributeValueReader = new DictionaryObjectAttributeValueReader(object, "org.kuali.rice.kns.datadictionary.validation.MockAddress", addressEntry);
        attributeValueReader.setAttributeName(attributeName);

        Object value = attributeValueReader.getValue();
        return processor.process(dictionaryValidationResult, value, constraint, attributeValueReader).getFirstConstraintValidationResult();
    }
}
