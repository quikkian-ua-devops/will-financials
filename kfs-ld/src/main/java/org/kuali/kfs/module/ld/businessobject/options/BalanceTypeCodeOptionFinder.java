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
package org.kuali.kfs.module.ld.businessobject.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.valuefinder.ValueFinder;
import org.kuali.rice.core.api.util.ConcreteKeyValue;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.kfs.module.ld.LaborConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21;
import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_ACTUAL;
import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE;

/**
 * Option Finder for Labor Balance Type Code.
 */
public class BalanceTypeCodeOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue(BALANCE_TYPE_ACTUAL, "Actual"));
        labels.add(new ConcreteKeyValue(BALANCE_TYPE_AC_AND_A21, "A21"));
        labels.add(new ConcreteKeyValue(BALANCE_TYPE_INTERNAL_ENCUMBRANCE, "Internal Encumbrance"));

        return labels;
    }

    /**
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    public String getValue() {
        return BALANCE_TYPE_ACTUAL;
    }
}
