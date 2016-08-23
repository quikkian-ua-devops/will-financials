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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.Collections;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.util.BeanPropertyComparator;

public class BusinessObjectPropertyLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService;

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        List<BusinessObjectProperty> matchingBusinessObjectProperties = kfsBusinessObjectMetaDataService.findBusinessObjectProperties(fieldValues.get(KFSPropertyConstants.NAMESPACE_CODE), fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_COMPONENT_LABEL), fieldValues.get(KFSPropertyConstants.PROPERTY_LABEL));
        Collections.sort(matchingBusinessObjectProperties, new BeanPropertyComparator(getDefaultSortColumns(), true));
        return matchingBusinessObjectProperties;
    }

    public void setKfsBusinessObjectMetaDataService(KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService) {
        this.kfsBusinessObjectMetaDataService = kfsBusinessObjectMetaDataService;
    }
}
