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
package org.kuali.kfs.kns.document.authorization;


import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MaintenanceDocumentAuthorizerBase extends DocumentAuthorizerBase implements MaintenanceDocumentAuthorizer {
    // private static final org.apache.log4j.Logger LOG =
    // org.apache.log4j.Logger.getLogger(MaintenanceDocumentAuthorizerBase.class);

    transient protected static DocumentDictionaryService documentDictionaryService;

    public boolean canCreate(Class boClass, Person user) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,
            getDocumentDictionaryService().getMaintenanceDocumentTypeName(
                boClass));
        permissionDetails.put(KRADConstants.MAINTENANCE_ACTN,
            KRADConstants.MAINTENANCE_NEW_ACTION);
        return !permissionExistsByTemplate(KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
            permissionDetails)
            || getPermissionService()
            .isAuthorizedByTemplate(user.getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS, permissionDetails,
                new HashMap<String, String>());
    }

    public boolean canMaintain(Object dataObject, Person user) {
        Map<String, String> permissionDetails = new HashMap<String, String>(2);
        permissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME,
            getDocumentDictionaryService().getMaintenanceDocumentTypeName(
                dataObject.getClass()));
        permissionDetails.put(KRADConstants.MAINTENANCE_ACTN,
            KRADConstants.MAINTENANCE_EDIT_ACTION);
        return !permissionExistsByTemplate(KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
            permissionDetails)
            || isAuthorizedByTemplate(
            dataObject,
            KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
            user.getPrincipalId(), permissionDetails, null);
    }

    public boolean canCreateOrMaintain(
        MaintenanceDocument maintenanceDocument, Person user) {
        return !permissionExistsByTemplate(maintenanceDocument,
            KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS)
            || isAuthorizedByTemplate(
            maintenanceDocument,
            KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.CREATE_MAINTAIN_RECORDS,
            user.getPrincipalId());
    }

    public Set<String> getSecurePotentiallyHiddenSectionIds() {
        return new HashSet<String>();
    }

    public Set<String> getSecurePotentiallyReadOnlySectionIds() {
        return new HashSet<String>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        super.addRoleQualification(dataObject, attributes);
        if (dataObject instanceof MaintenanceDocument) {
            MaintenanceDocument maintDoc = (MaintenanceDocument) dataObject;
            if (maintDoc.getNewMaintainableObject() != null) {
                attributes.putAll(
                    KRADUtils.getNamespaceAndComponentSimpleName(maintDoc.getNewMaintainableObject().getDataObjectClass()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addPermissionDetails(Object dataObject, Map<String, String> attributes) {
        super.addPermissionDetails(dataObject, attributes);
        if (dataObject instanceof MaintenanceDocument) {
            MaintenanceDocument maintDoc = (MaintenanceDocument) dataObject;
            if (maintDoc.getNewMaintainableObject() != null) {
                attributes.putAll(
                    KRADUtils.getNamespaceAndComponentSimpleName(maintDoc.getNewMaintainableObject().getDataObjectClass()));
                attributes.put(KRADConstants.MAINTENANCE_ACTN, maintDoc.getNewMaintainableObject().getMaintenanceAction());
            }
        }
    }

    protected static DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

}
