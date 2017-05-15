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
package org.kuali.kfs.sec.businessobject.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;


/**
 * Returns list of operator codes for security definition
 */
public class SecurityOperatorCodeFinder extends KeyValuesBase {

    protected static final List<KeyValue> OPTIONS = new ArrayList<KeyValue>();

    static {
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.EQUAL, "Equal"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.NOT_EQUAL, "Not Equal"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.GREATER_THAN, "Greater Than"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.GREATER_THAN_EQUAL, "Greater Than or Equal"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.LESS_THAN, "Less Than"));
        OPTIONS.add(new ConcreteKeyValue(SecConstants.SecurityDefinitionOperatorCodes.LESS_THAN_EQUAL, "Less Than or Equal"));
    }

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        return OPTIONS;
    }
}
