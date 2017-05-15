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
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.web.AccountingLineViewSequenceNumberField;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * Data dictionary definition of a field which represents a sequence number on an accounting line.
 */
public class AccountingLineViewSequenceNumberFieldDefinition extends AccountingLineViewFieldDefinition {

    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition#createLayoutElement(java.lang.Class)
     */
    @Override
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        return new AccountingLineViewSequenceNumberField();
    }

}
