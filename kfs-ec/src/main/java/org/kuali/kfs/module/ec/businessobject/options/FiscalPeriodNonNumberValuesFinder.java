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
package org.kuali.kfs.module.ec.businessobject.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.api.util.ConcreteKeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * returns valid effort reporting fiscal periods 1 - 12
 */
public class FiscalPeriodNonNumberValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("01", "1"));
        keyValues.add(new ConcreteKeyValue("02", "2"));
        keyValues.add(new ConcreteKeyValue("03", "3"));
        keyValues.add(new ConcreteKeyValue("04", "4"));
        keyValues.add(new ConcreteKeyValue("05", "5"));
        keyValues.add(new ConcreteKeyValue("06", "6"));
        keyValues.add(new ConcreteKeyValue("07", "7"));
        keyValues.add(new ConcreteKeyValue("08", "8"));
        keyValues.add(new ConcreteKeyValue("09", "9"));
        keyValues.add(new ConcreteKeyValue("10", "10"));
        keyValues.add(new ConcreteKeyValue("11", "11"));
        keyValues.add(new ConcreteKeyValue("12", "12"));
        keyValues.add(new ConcreteKeyValue("13", "13"));
        return keyValues;
    }
}
