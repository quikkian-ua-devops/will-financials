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
package org.kuali.kfs.module.cam.businessobject.defaultvalue;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.service.KeyValuesService;
import org.kuali.kfs.module.cam.businessobject.AssetCondition;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class AssetConditionValuesFinder extends KeyValuesBase {
    public List<KeyValue> getKeyValues() {
        List<AssetCondition> assetConditions = (List<AssetCondition>) SpringContext.getBean(KeyValuesService.class).findAll(AssetCondition.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if (assetConditions == null) {
            assetConditions = new ArrayList<AssetCondition>(0);
        } else {
            assetConditions = new ArrayList<AssetCondition>(assetConditions);
        }

        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));

        for (AssetCondition assetCondition : assetConditions) {
            if (assetCondition.isActive()) {
                labels.add(new ConcreteKeyValue(assetCondition.getAssetConditionCode(), assetCondition.getAssetConditionName()));
            }
        }

        return labels;
    }

}
