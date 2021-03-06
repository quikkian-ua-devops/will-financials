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
package org.kuali.kfs.kns.authorization;

import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Map;


public interface BusinessObjectAuthorizer {
    public boolean isAuthorized(BusinessObject businessObject,
                                String namespaceCode, String permissionName, String principalId);

    public boolean isAuthorizedByTemplate(BusinessObject businessObject,
                                          String namespaceCode, String permissionTemplateName,
                                          String principalId);

    public boolean isAuthorized(BusinessObject businessObject,
                                String namespaceCode, String permissionName, String principalId,
                                Map<String, String> additionalPermissionDetails,
                                Map<String, String> additionalRoleQualifiers);

    public boolean isAuthorizedByTemplate(Object dataObject,
                                          String namespaceCode, String permissionTemplateName,
                                          String principalId,
                                          Map<String, String> additionalPermissionDetails,
                                          Map<String, String> additionalRoleQualifiers);

    public Map<String, String> getCollectionItemRoleQualifications(BusinessObject collectionItemBusinessObject);

    public Map<String, String> getCollectionItemPermissionDetails(BusinessObject collectionItemBusinessObject);
}
