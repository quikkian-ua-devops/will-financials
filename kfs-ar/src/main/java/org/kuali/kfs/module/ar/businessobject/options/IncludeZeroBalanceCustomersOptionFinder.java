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
package org.kuali.kfs.module.ar.businessobject.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.valuefinder.ValueFinder;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class IncludeZeroBalanceCustomersOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value for this ValueFinder
     *
     * @return a String with the default key
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return ArConstants.INCLUDE_ZERO_BALANCE_NO;
    }

    /**
     * Returns a list of possible values for this ValueFinder
     *
     * @return a List of key/value pairs to populate radio buttons
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue(ArConstants.INCLUDE_ZERO_BALANCE_YES, ArConstants.INCLUDE_ZERO_BALANCE_YES));
        labels.add(new ConcreteKeyValue(ArConstants.INCLUDE_ZERO_BALANCE_NO, ArConstants.INCLUDE_ZERO_BALANCE_NO));
        return labels;
    }
}
