/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.rice.core.api.util.KeyValue; import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of string key value pairs for CustomerCreationOptions
 */
public class CustomerCreationOptionsValuesFinder extends KeyValuesBase {
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        List<KeyValue> activeLabels = new ArrayList<KeyValue>();

        activeLabels.add(new ConcreteKeyValue(CGConstants.AGENCY_USE_EXISTING_CUSTOMER_CODE, CGConstants.AGENCY_USE_EXISTING_CUSTOMER));
        activeLabels.add(new ConcreteKeyValue(CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE, CGConstants.AGENCY_CREATE_NEW_CUSTOMER));
        activeLabels.add(new ConcreteKeyValue(CGConstants.AGENCY_NO_CUSTOMER_CODE, CGConstants.AGENCY_NO_CUSTOMER));

        return activeLabels;
    }
}
