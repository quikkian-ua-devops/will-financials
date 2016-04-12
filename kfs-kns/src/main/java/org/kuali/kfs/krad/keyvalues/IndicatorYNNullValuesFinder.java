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
package org.kuali.kfs.krad.keyvalues;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class IndicatorYNNullValuesFinder extends org.kuali.kfs.krad.keyvalues.KeyValuesBase {

    @Override
	public List<KeyValue> getKeyValues() {
    	List<KeyValue> activeLabels = new ArrayList<KeyValue>();
        activeLabels.add(new ConcreteKeyValue("", ""));
        activeLabels.add(new ConcreteKeyValue("Yes", "Yes"));
        activeLabels.add(new ConcreteKeyValue("No", "No"));
        return activeLabels;
    }

}
