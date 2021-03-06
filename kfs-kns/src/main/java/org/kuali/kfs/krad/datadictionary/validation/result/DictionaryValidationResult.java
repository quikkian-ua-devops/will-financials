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
package org.kuali.kfs.krad.datadictionary.validation.result;

import org.kuali.kfs.krad.datadictionary.validation.AttributeValueReader;
import org.kuali.kfs.krad.datadictionary.validation.ErrorLevel;
import org.kuali.kfs.krad.datadictionary.validation.ValidationUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 *
 */
public class DictionaryValidationResult implements Iterable<ConstraintValidationResult> {

    private Map<String, EntryValidationResult> entryValidationResultMap;
    private ErrorLevel errorLevel;

    private int numberOfErrors;
    private int numberOfWarnings;

    private Iterator<ConstraintValidationResult> iterator;

    public DictionaryValidationResult() {
        this.entryValidationResultMap = new LinkedHashMap<String, EntryValidationResult>();
        this.errorLevel = ErrorLevel.ERROR;
        this.numberOfErrors = 0;
        this.numberOfWarnings = 0;
    }

    public void addConstraintValidationResult(AttributeValueReader attributeValueReader, ConstraintValidationResult constraintValidationResult) {

        // Don't bother to store this if the error level of the constraint validation result is lower than the level this dictionary validation result is tracking
        if (constraintValidationResult.getStatus().getLevel() < errorLevel.getLevel())
            return;

        switch (constraintValidationResult.getStatus()) {
            case ERROR:
                numberOfErrors++;
                break;
            case WARN:
                numberOfWarnings++;
                break;
            default:
                // Do nothing
        }

        // Give the constraint a chance to override the entry and attribute name - important if the attribute name is not the same as the one in the attribute value reader!
        String entryName = constraintValidationResult.getEntryName();
        String attributeName = constraintValidationResult.getAttributeName();
        String attributePath = constraintValidationResult.getAttributePath();

        if (entryName == null) {
            entryName = attributeValueReader.getEntryName();
        }

        if (attributeName == null) {
            attributeName = attributeValueReader.getAttributeName();
        }

        if (attributePath == null) {
            attributePath = attributeValueReader.getPath();
        }

        constraintValidationResult.setEntryName(entryName);
        constraintValidationResult.setAttributeName(attributeName);
        constraintValidationResult.setAttributePath(attributePath);

        String entryKey = getEntryValdidationResultKey(entryName, attributePath);
        getEntryValidationResult(entryKey).getAttributeValidationResult(attributeName).addConstraintValidationResult(constraintValidationResult);
    }

    public ConstraintValidationResult addError(AttributeValueReader attributeValueReader, String constraintName, String errorKey, String... errorParameters) {
        ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
        constraintValidationResult.setError(errorKey, errorParameters);
        numberOfErrors++;
        return constraintValidationResult;
    }

    public ConstraintValidationResult addError(String constraintLabelKey, AttributeValueReader attributeValueReader, String constraintName, String errorKey, String... errorParameters) {
        ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
        constraintValidationResult.setError(errorKey, errorParameters);
        constraintValidationResult.setConstraintLabelKey(constraintLabelKey);
        numberOfErrors++;
        return constraintValidationResult;
    }

    public ConstraintValidationResult addWarning(AttributeValueReader attributeValueReader, String constraintName, String errorKey, String... errorParameters) {
        if (errorLevel.getLevel() > ErrorLevel.WARN.getLevel())
            return new ConstraintValidationResult(constraintName, ErrorLevel.WARN);

        ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
        constraintValidationResult.setWarning(errorKey, errorParameters);
        numberOfWarnings++;
        return constraintValidationResult;
    }

    public ConstraintValidationResult addSuccess(AttributeValueReader attributeValueReader, String constraintName) {
        if (errorLevel.getLevel() > ErrorLevel.OK.getLevel())
            return new ConstraintValidationResult(constraintName, ErrorLevel.OK);

        return getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
    }

    public ConstraintValidationResult addSkipped(AttributeValueReader attributeValueReader, String constraintName) {
        if (errorLevel.getLevel() > ErrorLevel.OK.getLevel())
            return new ConstraintValidationResult(constraintName, ErrorLevel.INAPPLICABLE);

        ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
        constraintValidationResult.setStatus(ErrorLevel.INAPPLICABLE);
        return constraintValidationResult;
    }

    public ConstraintValidationResult addNoConstraint(AttributeValueReader attributeValueReader, String constraintName) {
        if (errorLevel.getLevel() > ErrorLevel.OK.getLevel())
            return new ConstraintValidationResult(constraintName, ErrorLevel.NOCONSTRAINT);

        ConstraintValidationResult constraintValidationResult = getConstraintValidationResult(attributeValueReader.getEntryName(), attributeValueReader.getAttributeName(), attributeValueReader.getPath(), constraintName);
        constraintValidationResult.setStatus(ErrorLevel.NOCONSTRAINT);
        return constraintValidationResult;
    }

    public Iterator<ConstraintValidationResult> iterator() {

        iterator = new Iterator<ConstraintValidationResult>() {

            private Iterator<EntryValidationResult> entryIterator;
            private Iterator<AttributeValidationResult> attributeIterator;
            private Iterator<ConstraintValidationResult> constraintIterator;

            @Override
            public boolean hasNext() {
                Iterator<ConstraintValidationResult> currentConstraintIterator = getCurrentConstraintIterator();
                return currentConstraintIterator != null && currentConstraintIterator.hasNext();
            }

            @Override
            public ConstraintValidationResult next() {
                Iterator<ConstraintValidationResult> currentConstraintIterator = getCurrentConstraintIterator();
                return currentConstraintIterator != null ? currentConstraintIterator.next() : null;
            }

            @Override
            public void remove() {
                throw new RuntimeException("Can't remove from this iterator!");
            }

            private Iterator<ConstraintValidationResult> getCurrentConstraintIterator() {
                if (constraintIterator == null || constraintIterator.hasNext() == false) {
                    Iterator<AttributeValidationResult> currentAttributeIterator = getCurrentAttributeIterator();
                    if (currentAttributeIterator != null && currentAttributeIterator.hasNext()) {
                        AttributeValidationResult currentAttributeValidationResult = currentAttributeIterator.next();
                        constraintIterator = currentAttributeValidationResult.iterator();
                    }
                }
                return constraintIterator;
            }

            private Iterator<AttributeValidationResult> getCurrentAttributeIterator() {
                if (attributeIterator == null || attributeIterator.hasNext() == false) {
                    Iterator<EntryValidationResult> currentEntryIterator = getCurrentEntryIterator();
                    if (currentEntryIterator != null && currentEntryIterator.hasNext()) {
                        EntryValidationResult currentEntryValidationResult = currentEntryIterator.next();
                        attributeIterator = currentEntryValidationResult.iterator();
                    }
                }
                return attributeIterator;
            }

            private Iterator<EntryValidationResult> getCurrentEntryIterator() {
                if (entryIterator == null) // || entryIterator.hasNext() == false)
                    entryIterator = entryValidationResultMap.values().iterator();
                return entryIterator;
            }

        };

        return iterator;
    }

    protected EntryValidationResult getEntryValidationResult(String entryName) {
        EntryValidationResult entryValidationResult = entryValidationResultMap.get(entryName);
        if (entryValidationResult == null) {
            entryValidationResult = new EntryValidationResult(entryName);
            entryValidationResultMap.put(entryName, entryValidationResult);
        }
        return entryValidationResult;
    }

    private ConstraintValidationResult getConstraintValidationResult(String entryName, String attributeName, String attributePath, String constraintName) {
        String entryKey = getEntryValdidationResultKey(entryName, attributePath);
        ConstraintValidationResult constraintValidationResult = getEntryValidationResult(entryKey).getAttributeValidationResult(attributeName).getConstraintValidationResult(constraintName);
        constraintValidationResult.setEntryName(entryName);
        constraintValidationResult.setAttributeName(attributeName);
        constraintValidationResult.setAttributePath(attributePath);
        return constraintValidationResult;
    }

    /**
     * Returns the key to the EntryValidationResult entry in the EntryValidationResultMap.
     * Most cases entry key will be the entryName, unless the attribute is part of a collection,
     * in which case entry key will be suffixed with index of attribute's parent item.
     *
     * @param entryName
     * @param attributePath
     * @return
     */
    private String getEntryValdidationResultKey(String entryName, String attributePath) {
        if (attributePath.contains("[")) {
            return entryName + "[" + ValidationUtils.getLastPathIndex(attributePath) + "]";
        }
        return entryName;
    }

    /**
     * @return the errorLevel
     */
    public ErrorLevel getErrorLevel() {
        return this.errorLevel;
    }

    /**
     * @param errorLevel the errorLevel to set
     */
    public void setErrorLevel(ErrorLevel errorLevel) {
        this.errorLevel = errorLevel;
    }

    /**
     * @return the numberOfErrors
     */
    public int getNumberOfErrors() {
        return this.numberOfErrors;
    }

    /**
     * @return the numberOfWarnings
     */
    public int getNumberOfWarnings() {
        return this.numberOfWarnings;
    }

}
