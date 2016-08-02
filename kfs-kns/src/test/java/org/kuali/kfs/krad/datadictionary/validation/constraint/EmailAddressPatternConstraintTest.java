/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.datadictionary.validation.constraint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.Employee;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.processor.ValidCharactersConstraintProcessor;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;


/**
 * Things this test should check:
 *
 * 1. empty value check. (failure) {@link #testValueInvalidEmailAddressEmpty()}
 * 2. value with valid email address. (success) {@link #testValueValidEmailAddress()}
 * 3. value with valid email address. (success) {@link #testValueValidEmailAddress1()}
 * 4. value with valid email address. (success) {@link #testValueValidEmailAddress2()}
 * 5. value with valid email address. (success) {@link #testValueValidEmailAddress3()}
 * 6. value with valid email address. (success) {@link #testValueValidEmailAddress4()}
 * 7. value with valid email address. (success) {@link #testValueValidEmailAddress5()}
 * 8. value with invalid email address. (failure) {@link #testValueInvalidEmailAddress()}
 * 9. value with invalid email address. (failure) {@link #testValueInvalidEmailAddress1()}
 * 10. value with invalid email address. (failure) {@link #testValueInvalidEmailAddress2()}
 * 11. value with invalid email address. (failure) {@link #testValueInvalidEmailAddress3()}
 * 12. value with invalid email address. (failure) {@link #testValueInvalidEmailAddress4()}
 * 13. value with invalid email address. (failure) {@link #testValueInvalidEmailAddress5()}
 * 14. value with invalid email address. (failure) {@link #testValueInvalidEmailAddress6()}
 * 15. value with invalid email address. (failure) {@link #testValueInvalidEmailAddress7()}
 *
 *
 * 
 */
public class EmailAddressPatternConstraintTest {

	private final String PATTERN_CONSTRAINT = "validationPatternRegex.emailAddress";

	private AttributeDefinition contactEmailDefinition;

	private BusinessObjectEntry addressEntry;
	private DictionaryValidationResult dictionaryValidationResult;

	private ValidCharactersConstraintProcessor processor;

	private Employee validEmailEmployee = new Employee();
	private Employee validEmailEmployee1 = new Employee();
	private Employee validEmailEmployee2 = new Employee();
	private Employee validEmailEmployee3 = new Employee();
	private Employee validEmailEmployee4 = new Employee();
	private Employee validEmailEmployee5 = new Employee();
	private Employee invalidEmailEmployeeEmpty = new Employee();
	private Employee invalidEmailEmployee = new Employee();
	private Employee invalidEmailEmployee1 = new Employee();
	private Employee invalidEmailEmployee2 = new Employee();
	private Employee invalidEmailEmployee3 = new Employee();
	private Employee invalidEmailEmployee4 = new Employee();
	private Employee invalidEmailEmployee5 = new Employee();
	private Employee invalidEmailEmployee6 = new Employee();
	private Employee invalidEmailEmployee7 = new Employee();

	private ConfigurationBasedRegexPatternConstraint contactEmailAddressPatternConstraint;

	@Before
	public void setUp() throws Exception {

		String regex = ApplicationResources.getProperty(PATTERN_CONSTRAINT);

		processor = new ValidCharactersConstraintProcessor();

		dictionaryValidationResult = new DictionaryValidationResult();
		dictionaryValidationResult.setErrorLevel(ErrorLevel.NOCONSTRAINT);

		addressEntry = new BusinessObjectEntry();

		List<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();

		validEmailEmployee.setContactEmail("ww5@a.b.c.org");
		validEmailEmployee1.setContactEmail("something.else@a2.com");
		validEmailEmployee2.setContactEmail("something_else@something.else.com");
		validEmailEmployee3.setContactEmail("something-else@et-tu.com");
		validEmailEmployee4.setContactEmail("dmerkal@gmail.com");
		validEmailEmployee5.setContactEmail("m.modi@gmail.com");
		invalidEmailEmployeeEmpty.setContactEmail("");
		invalidEmailEmployee.setContactEmail("@a.b.c.org");
		invalidEmailEmployee1.setContactEmail("a");
		invalidEmailEmployee2.setContactEmail("1@a.b.c.org");
		invalidEmailEmployee3.setContactEmail("1@org");
		invalidEmailEmployee4.setContactEmail("1@a");
		invalidEmailEmployee5.setContactEmail(".@a.org");
		invalidEmailEmployee6.setContactEmail("_@a.org");
		invalidEmailEmployee7.setContactEmail("something@a.o-rg");

		contactEmailAddressPatternConstraint = new ConfigurationBasedRegexPatternConstraint();
		contactEmailAddressPatternConstraint.setValue(regex);

		contactEmailDefinition = new AttributeDefinition();
		contactEmailDefinition.setName("contactEmail");
		contactEmailDefinition.setValidCharactersConstraint(contactEmailAddressPatternConstraint);
		attributes.add(contactEmailDefinition);

		addressEntry.setAttributes(attributes);
	}

	@Test
	public void testValueInvalidEmailAddressEmpty() {
		ConstraintValidationResult result = process(invalidEmailEmployeeEmpty, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.INAPPLICABLE, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidEmailAddress() {
		ConstraintValidationResult result = process(validEmailEmployee, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidEmailAddress1() {
		ConstraintValidationResult result = process(validEmailEmployee1, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidEmailAddress2() {
		ConstraintValidationResult result = process(validEmailEmployee2, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidEmailAddress3() {
		ConstraintValidationResult result = process(validEmailEmployee3, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidEmailAddress4() {
		ConstraintValidationResult result = process(validEmailEmployee4, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueValidEmailAddress5() {
		ConstraintValidationResult result = process(validEmailEmployee5, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.OK, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidEmailAddress() {
		ConstraintValidationResult result = process(invalidEmailEmployee, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidEmailAddress1() {
		ConstraintValidationResult result = process(invalidEmailEmployee1, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidEmailAddress2() {
		ConstraintValidationResult result = process(invalidEmailEmployee2, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidEmailAddress3() {
		ConstraintValidationResult result = process(invalidEmailEmployee3, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidEmailAddress4() {
		ConstraintValidationResult result = process(invalidEmailEmployee4, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidEmailAddress5() {
		ConstraintValidationResult result = process(invalidEmailEmployee5, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidEmailAddress6() {
		ConstraintValidationResult result = process(invalidEmailEmployee6, "contactEmail", contactEmailAddressPatternConstraint);
		Assert.assertEquals(0, dictionaryValidationResult.getNumberOfWarnings());
		Assert.assertEquals(1, dictionaryValidationResult.getNumberOfErrors());
		Assert.assertEquals(ErrorLevel.ERROR, result.getStatus());
		Assert.assertEquals(new ValidCharactersConstraintProcessor().getName(), result.getConstraintName());
	}

	@Test
	public void testValueInvalidEmailAddress7() {
		ConstraintValidationResult result = process(invalidEmailEmployee7, "contactEmail", contactEmailAddressPatternConstraint);
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