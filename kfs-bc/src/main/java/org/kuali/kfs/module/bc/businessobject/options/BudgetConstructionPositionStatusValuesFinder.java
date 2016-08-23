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
package org.kuali.kfs.module.bc.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants.BudgetConstructionPositionConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of ba fund restriction levels.
 */
public class BudgetConstructionPositionStatusValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue(BudgetConstructionPositionConstants.POSITION_STATUS_APPROVED, "Approved"));
        keyValues.add(new ConcreteKeyValue(BudgetConstructionPositionConstants.POSITION_STATUS_DELETED, "Deleted"));
        keyValues.add(new ConcreteKeyValue(BudgetConstructionPositionConstants.POSITION_STATUS_FROZEN, "Frozen"));
        keyValues.add(new ConcreteKeyValue(BudgetConstructionPositionConstants.POSITION_STATUS_TEMPORARILY_INACTIVE, "Temporarily Inactive"));

        return keyValues;
    }

}
