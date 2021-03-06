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
package org.kuali.kfs.module.purap.businessobject.options;

import org.kuali.kfs.integration.cam.CapitalAssetManagementAssetTransactionType;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Values finder for CapitalAssetBuilderAssetTransactionTypes
 */
public class AssetTransactionTypeValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all CapitalAssetBuilderAssetTransactionTypes.
     *
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<CapitalAssetManagementAssetTransactionType> types = SpringContext.getBean(CapitalAssetManagementModuleService.class).getAllAssetTransactionTypes();
        List labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));
        for (Object type : types) {
            CapitalAssetManagementAssetTransactionType camsType = (CapitalAssetManagementAssetTransactionType) type;
            labels.add(new ConcreteKeyValue(camsType.getCapitalAssetTransactionTypeCode(), camsType.getCapitalAssetTransactionTypeDescription()));
        }

        return labels;
    }

}
