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
package org.kuali.kfs.coa.document.web;

import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.CodeDescriptionFormatterBase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SubFundGroupCodeDescriptionFormatter extends CodeDescriptionFormatterBase {

    @Override
    protected String getDescriptionOfBO(PersistableBusinessObject bo) {
        return ((SubFundGroup) bo).getSubFundGroupDescription();
    }

    @Override
    protected Map<String, PersistableBusinessObject> getValuesToBusinessObjectsMap(Set values) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSConstants.SUB_FUND_GROUP_CODE_PROPERTY_NAME, values);

        Map<String, PersistableBusinessObject> map = new HashMap<String, PersistableBusinessObject>();

        Collection<SubFundGroup> coll = SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(SubFundGroup.class, criteria, "versionNumber", true);
        for (SubFundGroup sfg : coll) {
            map.put(sfg.getSubFundGroupCode(), sfg);
        }
        return map;
    }

}
