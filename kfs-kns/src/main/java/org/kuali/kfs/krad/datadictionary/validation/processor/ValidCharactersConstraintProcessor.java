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
package org.kuali.kfs.krad.datadictionary.validation.processor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;
import org.kuali.kfs.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.kfs.krad.datadictionary.validation.capability.Formatable;
import org.kuali.kfs.krad.datadictionary.validation.constraint.Constraint;
import org.kuali.kfs.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.kfs.krad.datadictionary.validation.result.ConstraintValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.DictionaryValidationResult;
import org.kuali.kfs.krad.datadictionary.validation.result.ProcessorResult;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.DateFormatter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This class defines a constraint processor to ensure that attribute values are constrained to valid characters, as defined by some regular expression. Of the
 * constraint processors written for this version, this one is potentially the most difficult to understand because it holds on to a lot of legacy processing.
 */
public class ValidCharactersConstraintProcessor extends MandatoryElementConstraintProcessor<ValidCharactersConstraint> {

    public static final String VALIDATE_METHOD = "validate";

    private static final Logger LOG = Logger.getLogger(ValidCharactersConstraintProcessor.class);
    private static final String[] DATE_RANGE_ERROR_PREFIXES = {KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX, KRADConstants.LOOKUP_RANGE_UPPER_BOUND_PROPERTY_PREFIX};

    private static final String CONSTRAINT_NAME = "valid characters constraint";

    /**
     * @see ConstraintProcessor#process(DictionaryValidationResult, Object, org.kuali.rice.krad.datadictionary.validation.capability.Validatable, AttributeValueReader)
     */
    @Override
    public ProcessorResult process(DictionaryValidationResult result, Object value, ValidCharactersConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

        return new ProcessorResult(processSingleValidCharacterConstraint(result, value, constraint, attributeValueReader));
    }

    @Override
    public String getName() {
        return CONSTRAINT_NAME;
    }

    /**
     * @see ConstraintProcessor#getConstraintType()
     */
    @Override
    public Class<? extends Constraint> getConstraintType() {
        return ValidCharactersConstraint.class;
    }


    protected ConstraintValidationResult processSingleValidCharacterConstraint(DictionaryValidationResult result, Object value, ValidCharactersConstraint constraint, AttributeValueReader attributeValueReader) throws AttributeValidationException {

        if (constraint == null)
            return result.addNoConstraint(attributeValueReader, CONSTRAINT_NAME);

        if (ValidationUtils.isNullOrEmpty(value))
            return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);

        // This mix-in interface is here to allow some definitions to avoid the extra processing that goes on in KNS
        // to decipher and validate things like date range strings -- something that looks like "02/02/2002..03/03/2003"
        Constrainable definition = attributeValueReader.getDefinition(attributeValueReader.getAttributeName());
        if (definition instanceof Formatable) {
            return doProcessFormattableValidCharConstraint(result, constraint, (Formatable) definition, value, attributeValueReader);
        }

        ConstraintValidationResult constraintValidationResult = doProcessValidCharConstraint(constraint, value);
        if (constraintValidationResult == null)
            return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);

        result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);
        constraintValidationResult.setConstraintLabelKey(constraint.getLabelKey());
        constraintValidationResult.setErrorParameters(constraint.getValidationMessageParamsArray());
        return constraintValidationResult;
    }

    protected ConstraintValidationResult doProcessFormattableValidCharConstraint(DictionaryValidationResult result, ValidCharactersConstraint validCharsConstraint, Formatable definition, Object value, AttributeValueReader attributeValueReader) throws AttributeValidationException {
        String entryName = attributeValueReader.getEntryName();
        String attributeName = attributeValueReader.getAttributeName();

        // This is a strange KNS thing for validating searchable fields -- they sometimes come in a date range format, for example 2/12/2010..2/14/2010, and need to be split up
        List<String> parsedAttributeValues = attributeValueReader.getCleanSearchableValues(attributeName);

        if (parsedAttributeValues != null) {

            Class<?> formatterClass = null;
            Boolean doValidateDateRangeOrder = null;

            // It can't be a date range if it's more than two fields, for example "a .. b | c" is not a date range -- this saves us a tiny bit of processing later
            if (parsedAttributeValues.size() != 2)
                doValidateDateRangeOrder = Boolean.FALSE;

            // Use integer to iterate since we need to track which field we're looking at
            for (int i = 0; i < parsedAttributeValues.size(); i++) {
                String parsedAttributeValue = parsedAttributeValues.get(i);

                ConstraintValidationResult constraintValidationResult = doProcessValidCharConstraint(validCharsConstraint, parsedAttributeValue);

                // If this is an error then some non-null validation result will be returned
                if (constraintValidationResult != null) {
                    constraintValidationResult.setConstraintLabelKey(validCharsConstraint.getLabelKey());
                    constraintValidationResult.setErrorParameters(validCharsConstraint.getValidationMessageParamsArray());
                    // Another strange KNS thing -- if the validation fails (not sure why only in that case) then some further error checking is done using the formatter, if one exists
                    if (formatterClass == null) {
                        String formatterClassName = definition.getFormatterClass();
                        if (formatterClassName != null)
                            formatterClass = ClassLoaderUtils.getClass(formatterClassName);
                    }

                    if (formatterClass != null) {
                        // Use the Boolean value being null to ensure we only do this once
                        if (doValidateDateRangeOrder == null) {
                            // We only want to validate a date range if we're dealing with something that has a date formatter on it and that looks like an actual range (is made up of 2 values with a between operator between them)
                            doValidateDateRangeOrder = Boolean.valueOf(DateFormatter.class.isAssignableFrom(formatterClass) && StringUtils.contains(ValidationUtils.getString(value), SearchOperator
                                .BETWEEN.toString()));
                        }

                        constraintValidationResult = processFormatterValidation(result, formatterClass, entryName, attributeName, parsedAttributeValue, DATE_RANGE_ERROR_PREFIXES[i]);

                        if (constraintValidationResult != null) {
                            result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);
                            return constraintValidationResult;
                        }
                    } else {
                        // Otherwise, just report the validation result (apparently the formatter can't provide any fall-through validation because it doesn't exist)
                        result.addConstraintValidationResult(attributeValueReader, constraintValidationResult);
                        return constraintValidationResult;
                    }
                }
            }

            if (doValidateDateRangeOrder != null && doValidateDateRangeOrder.booleanValue()) {
                ConstraintValidationResult dateOrderValidationResult = validateDateOrder(parsedAttributeValues.get(0), parsedAttributeValues.get(1), entryName, attributeName);

                if (dateOrderValidationResult != null) {
                    result.addConstraintValidationResult(attributeValueReader, dateOrderValidationResult);
                    return dateOrderValidationResult;
                }
            }

            return result.addSuccess(attributeValueReader, CONSTRAINT_NAME);
        }
        return result.addSkipped(attributeValueReader, CONSTRAINT_NAME);
    }

    protected ConstraintValidationResult doProcessValidCharConstraint(ValidCharactersConstraint validCharsConstraint, Object value) {

        StringBuilder fieldValue = new StringBuilder();
        String validChars = validCharsConstraint.getValue();

        fieldValue.append(ValidationUtils.getString(value));

//        int typIdx = validChars.indexOf(":");
//        String processorType = "regex";
//        if (-1 == typIdx) {
//            validChars = "[" + validChars + "]*";
//        } else {
//            processorType = validChars.substring(0, typIdx);
//            validChars = validChars.substring(typIdx + 1);
//        }

//        if ("regex".equalsIgnoreCase(processorType) && !validChars.equals(".*")) {
        if (!fieldValue.toString().matches(validChars)) {
            ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
            if (validCharsConstraint.getLabelKey() != null) {
                // FIXME: This shouldn't surface label key itself to the user - it should look up the label key, but this needs to be implemented in Rice
                constraintValidationResult.setError(RiceKeyConstants.ERROR_CUSTOM, validCharsConstraint.getLabelKey());
                return constraintValidationResult;
            }

            constraintValidationResult.setError(RiceKeyConstants.ERROR_INVALID_FORMAT, fieldValue.toString());
            constraintValidationResult.setConstraintLabelKey(validCharsConstraint.getLabelKey());
            constraintValidationResult.setErrorParameters(validCharsConstraint.getValidationMessageParamsArray());
            return constraintValidationResult;
        }
//        }

        return null;
    }

    protected ConstraintValidationResult processFormatterValidation(DictionaryValidationResult result, Class<?> formatterClass, String entryName, String attributeName, String parsedAttributeValue, String errorKeyPrefix) {

        boolean isError = false;

        try {
            Method validatorMethod = formatterClass.getDeclaredMethod(VALIDATE_METHOD, new Class<?>[]{String.class});
            Object o = validatorMethod.invoke(formatterClass.newInstance(), parsedAttributeValue);
            if (o instanceof Boolean) {
                isError = !((Boolean) o).booleanValue();
            }
        } catch (Exception e) {
            if (LOG.isDebugEnabled())
                LOG.debug(e.getMessage(), e);

            isError = true;
        }

        if (isError) {
            String errorMessageKey = getDataDictionaryService().getAttributeValidatingErrorMessageKey(entryName, attributeName);
            String[] errorMessageParameters = getDataDictionaryService().getAttributeValidatingErrorMessageParameters(entryName, attributeName);

            ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
            constraintValidationResult.setEntryName(entryName);
            constraintValidationResult.setAttributeName(errorKeyPrefix + attributeName);
            constraintValidationResult.setError(errorMessageKey, errorMessageParameters);

            return constraintValidationResult;
        }

        return null;
    }

    protected ConstraintValidationResult validateDateOrder(String firstDateTime, String secondDateTime, String entryName, String attributeName) {
        // this means that we only have 2 values and it's a date range.
        java.sql.Timestamp lVal = null;
        java.sql.Timestamp uVal = null;
        try {
            lVal = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(firstDateTime);
            uVal = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(secondDateTime);
        } catch (Exception ex) {
            // this shouldn't happen because the tests passed above.
            String errorMessageKey = KRADServiceLocatorWeb.getDataDictionaryService().getAttributeValidatingErrorMessageKey(entryName, attributeName);
            String[] errorMessageParameters = KRADServiceLocatorWeb.getDataDictionaryService().getAttributeValidatingErrorMessageParameters(entryName, attributeName);
            ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
            constraintValidationResult.setEntryName(entryName);
            constraintValidationResult.setAttributeName(
                KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + attributeName);
            constraintValidationResult.setError(errorMessageKey, errorMessageParameters);
            return constraintValidationResult;
        }

        if (lVal != null && lVal.compareTo(uVal) > 0) { // check the bounds
            String errorMessageKey = KRADServiceLocatorWeb.getDataDictionaryService().getAttributeValidatingErrorMessageKey(entryName, attributeName);
            String[] errorMessageParameters = KRADServiceLocatorWeb.getDataDictionaryService().getAttributeValidatingErrorMessageParameters(entryName, attributeName);
            ConstraintValidationResult constraintValidationResult = new ConstraintValidationResult(CONSTRAINT_NAME);
            constraintValidationResult.setEntryName(entryName);
            constraintValidationResult.setAttributeName(
                KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + attributeName);
            constraintValidationResult.setError(errorMessageKey + ".range", errorMessageParameters);
            return constraintValidationResult;
        }

        return null;
    }

}
