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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.kns.util.FieldUtils;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewMultipleReadOnlyFields;
import org.kuali.kfs.sys.document.web.TableJoining;

import java.util.ArrayList;
import java.util.List;

/**
 * The definition for an accounting line component which displays multiple fields from the command line, but
 * all of them as read only, with their headers displayed first
 */
public class AccountingLineViewMultipleReadOnlyFieldsDefinition extends DataDictionaryDefinitionBase implements AccountingLineViewRenderableElementDefinition {
    public List<String> fieldNames;

    /**
     * Makes sure that the number of fields set is greater than 0
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (fieldNames.isEmpty()) {
            throw new AttributeValidationException("Please specify one or more field names when defining AccountingLineViewMultipleReadOnlyFields " + getId());
        }
    }

    /**
     * Creates a new AccountingLineViewMultipleReadOnlyField
     *
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement(java.lang.Class)
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        List<Field> fields = new ArrayList<Field>();
        for (String fieldName : fieldNames) {
            fields.add(getKNSFieldForDefinition(accountingLineClass, fieldName));
        }
        return new AccountingLineViewMultipleReadOnlyFields(this, fields);
    }

    /**
     * Creates a KNS Field for an AccountingLineViewField definition
     *
     * @param accountingLineClass the class of the accounting line used by this definition
     * @param fieldName           the name of the field to initialize
     * @return a properly initialized KNS field
     */
    public Field getKNSFieldForDefinition(Class<? extends AccountingLine> accountingLineClass, String fieldName) {
        Field realField = FieldUtils.getPropertyField(accountingLineClass, fieldName, false);
        return realField;
    }

    /**
     * @return the field names of fields to display in the cell
     */
    public List<String> getFieldNames() {
        return fieldNames;
    }

    /**
     * Sets the field names to display in the field, in top-down order
     *
     * @param fieldNames the field names to display
     */
    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

}
