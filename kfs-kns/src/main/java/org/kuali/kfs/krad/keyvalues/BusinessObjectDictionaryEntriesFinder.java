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
package org.kuali.kfs.krad.keyvalues;

import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class returns list of business objects defined in the data dictionary.
 */
public class BusinessObjectDictionaryEntriesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.kfs.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<String> businessObjects =
            KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectClassNames();
        List<KeyValue> boKeyLabels = new ArrayList<KeyValue>();

        for (String string : businessObjects) {
            String className = string;
            boKeyLabels.add(new ConcreteKeyValue(className, className));
        }

        return boKeyLabels;
    }

}
