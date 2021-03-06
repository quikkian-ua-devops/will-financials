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
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class returns list of string key value pairs for LOC Creation Types.
 */
public class DaysPastDueValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        List<KeyValue> activeLabels = new ArrayList<KeyValue>();
        // This blank option would be useful for Cash Control document on change event
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT, ArConstants.DunningLetters.DYS_PST_DUE_CURRENT));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_31_60, ArConstants.DunningLetters.DYS_PST_DUE_31_60));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_61_90, ArConstants.DunningLetters.DYS_PST_DUE_61_90));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_91_120, ArConstants.DunningLetters.DYS_PST_DUE_91_120));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_121, ArConstants.DunningLetters.DYS_PST_DUE_121));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_FINAL, ArConstants.DunningLetters.DYS_PST_DUE_FINAL));
        activeLabels.add(new ConcreteKeyValue(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL, ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL));
        return activeLabels;
    }
}
