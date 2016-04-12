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
package org.kuali.kfs.krad.kim;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.impl.permission.PermissionBo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentTypeAndNodeAndFieldsPermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

    @Override
    protected boolean isCheckRequiredAttributes() {
        return true;
    }

	/**
	 * 
	 *	consider the document type hierarchy - check for a permission that just specifies the document type first at each level 
	 *	- then if you don't find that, check for the doc type and the node, then the doc type and the field.
	 *
	 *	- if the field value passed in starts with the value on the permission detail it is a match.  so...
	 *	permision detail sourceAccountingLines will match passed in value of sourceAccountingLines.amount and sourceAccountingLines 
	 *	permission detail sourceAccountingLines.objectCode will match sourceAccountingLines.objectCode but not sourceAccountingLines
	 */
	@Override
	protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails,
			List<Permission> permissionsList) {

        List<Permission> matchingPermissions = new ArrayList<Permission>();
		// loop over the permissions, checking the non-document-related ones
		for ( Permission kpi : permissionsList ) {
            PermissionBo bo = PermissionBo.from(kpi);
			if ( routeNodeMatches(requestedDetails, bo.getDetails()) &&
					doesPropertyNameMatch(requestedDetails.get(KimConstants.AttributeConstants.PROPERTY_NAME), bo.getDetails().get(KimConstants.AttributeConstants.PROPERTY_NAME)) ) {
				matchingPermissions.add( kpi );
			}			
		}
		// now, filter the list to just those for the current document
		matchingPermissions = super.performPermissionMatches( requestedDetails, matchingPermissions );
		return matchingPermissions;
	}
		
	protected boolean routeNodeMatches(Map<String, String> requestedDetails, Map<String, String> permissionDetails) {
		if ( StringUtils.isBlank( permissionDetails.get(KimConstants.AttributeConstants.ROUTE_NODE_NAME) ) ) {
			return true;
		}
		return StringUtils.equals(requestedDetails.get(KimConstants.AttributeConstants.ROUTE_NODE_NAME), permissionDetails.get(
                KimConstants.AttributeConstants.ROUTE_NODE_NAME));
	}
}
