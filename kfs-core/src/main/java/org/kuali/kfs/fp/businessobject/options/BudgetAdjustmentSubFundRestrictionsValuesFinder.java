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
package org.kuali.kfs.fp.businessobject.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.sys.KFSConstants.BudgetAdjustmentDocumentConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * This class returns list of ba sub fund restriction levels.
 */
public class BudgetAdjustmentSubFundRestrictionsValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue(BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_NONE, "None"));
        keyValues.add(new ConcreteKeyValue(BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_SUBFUND, "SubFund"));
        keyValues.add(new ConcreteKeyValue(BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_CHART, "Chart"));
        keyValues.add(new ConcreteKeyValue(BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_ORGANIZATION, "Organization"));
        keyValues.add(new ConcreteKeyValue(BudgetAdjustmentDocumentConstants.ADJUSTMENT_RESTRICTION_LEVEL_ACCOUNT, "Account"));

        return keyValues;
    }

}
