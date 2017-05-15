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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.sys.businessobject.AccountingLine;

import java.util.Map;

/**
 * An interface that represents a transformation on a single field; they can be grouped
 * together and run at one time by FieldChangingAccountingLineRenderTransformImpl.
 */
public interface AccountingLineFieldRenderingTransformation {
    /**
     * Performs a transformation on the field
     *
     * @param accountingLine    the accounting line that is being rendered
     * @param field             the field to transform
     * @param fieldDefinition   the accounting line definition of the accounting line being rendered
     * @param unconvertedValues a map of unconverted values
     */
    public abstract void transformField(AccountingLine accountingLine, Field field, MaintainableFieldDefinition fieldDefinition, Map unconvertedValues);
}
