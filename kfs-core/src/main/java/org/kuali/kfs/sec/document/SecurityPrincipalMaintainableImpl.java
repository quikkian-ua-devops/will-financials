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
package org.kuali.kfs.sec.document;

import java.util.HashMap;

import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModelMember;
import org.kuali.kfs.sec.businessobject.SecurityPrincipal;
import org.kuali.kfs.sec.businessobject.SecurityPrincipalDefinition;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.KRADConstants;


/**
 * Maintainable implementation for the Security Principal maintenance document. Hooks into Post processing to create the KIM permissions for the principal and assign security role
 * members
 */
public class SecurityPrincipalMaintainableImpl extends AbstractSecurityModuleMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityPrincipalMaintainableImpl.class);

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);

        if (documentHeader.getWorkflowDocument().isProcessed()) {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            try {
                MaintenanceDocument document = (MaintenanceDocument) documentService.getByDocumentHeaderId(documentHeader.getDocumentNumber());
                SecurityPrincipal oldSecurityPrincipal = (SecurityPrincipal) document.getOldMaintainableObject().getBusinessObject();
                SecurityPrincipal newSecurityPrincipal = (SecurityPrincipal) document.getNewMaintainableObject().getBusinessObject();

                boolean newMaintenanceAction = getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_NEW_ACTION) || getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_COPY_ACTION);

                assignOrUpdatePrincipalMembershipToDefinitionRoles(oldSecurityPrincipal, newSecurityPrincipal, newMaintenanceAction);
                assignOrUpdatePrincipalModelRoles(newSecurityPrincipal);
            }
            catch (WorkflowException e) {
                LOG.error("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);
                throw new RuntimeException("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);
            }
        }
    }

    /**
     * Iterates through the principal definition list and assigns the principal to the definition role if necessary or updates the current member assignment
     *
     * @param oldSecurityPrincipal SecurityPrincipal before updates
     * @param newSecurityPrincipal SecurityPrincipal which contains the definition list and principal
     * @param newMaintenanceAction boolean indicating whether this is a new record (old side will not contain data)
     */
    protected void assignOrUpdatePrincipalMembershipToDefinitionRoles(SecurityPrincipal oldSecurityPrincipal, SecurityPrincipal newSecurityPrincipal, boolean newMaintenanceAction) {
        RoleService roleService = KimApiServiceLocator.getRoleService();

        String principalId = newSecurityPrincipal.getPrincipalId();

        for (SecurityPrincipalDefinition securityPrincipalDefinition : newSecurityPrincipal.getPrincipalDefinitions()) {
            SecurityDefinition securityDefinition = securityPrincipalDefinition.getSecurityDefinition();

            Role definitionRoleInfo = roleService.getRole(securityDefinition.getRoleId());

            RoleMember principalMembershipInfo = null;
            if (!newMaintenanceAction) {
                SecurityPrincipalDefinition oldPrincipalDefinition = null;
                for (SecurityPrincipalDefinition principalDefinition : oldSecurityPrincipal.getPrincipalDefinitions()) {
                   if ((principalDefinition.getPrincipalDefinitionId() != null) && principalDefinition.getPrincipalDefinitionId().equals(securityPrincipalDefinition.getPrincipalDefinitionId())) {
                       oldPrincipalDefinition = principalDefinition;
                   }
                }

                if (oldPrincipalDefinition != null) {
                    principalMembershipInfo = getRoleMembershipForMemberType(definitionRoleInfo.getId(), principalId, MemberType.PRINCIPAL.getCode(), getRoleQualifiersFromSecurityModelDefinition(oldPrincipalDefinition));
                }
            }

            // only create membership if principal definition record is active
            boolean membershipActive = securityPrincipalDefinition.isActive();

            // if membership already exists, need to remove if the principal record is now inactive or the qualifications need updated
            if (principalMembershipInfo != null) {
                boolean qualificationsMatch = doMembershipQualificationsMatchValues(principalMembershipInfo.getAttributes(), securityPrincipalDefinition.getConstraintCode(), securityPrincipalDefinition.getOperatorCode(), securityPrincipalDefinition.getAttributeValue());
                if (!membershipActive || !qualificationsMatch) {
                    roleService.removePrincipalFromRole(principalMembershipInfo.getMemberId(), definitionRoleInfo.getNamespaceCode(), definitionRoleInfo.getName(), principalMembershipInfo.getAttributes());
                }
            }

            // create of update role if membership should be active
            if (membershipActive) {
                if ( principalMembershipInfo == null ) {
                    principalMembershipInfo = roleService.assignPrincipalToRole( principalId, definitionRoleInfo.getNamespaceCode(), definitionRoleInfo.getName(), getRoleQualifiersFromSecurityModelDefinition(securityPrincipalDefinition));
                } else {
                    RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(principalMembershipInfo);
                    updatedRoleMember.setAttributes(getRoleQualifiersFromSecurityModelDefinition(securityPrincipalDefinition));
                    updatedRoleMember.setMemberId(principalId);
                    roleService.updateRoleMember(updatedRoleMember.build());
                }
            }
        }
    }

    /**
     * Iterates through the principal model list and assigns the principal to the model role or updates the membership
     *
     * @param securityPrincipal SecurityPrincipal which contains the model list and principal
     */
    protected void assignOrUpdatePrincipalModelRoles(SecurityPrincipal securityPrincipal) {
        RoleService roleService = KimApiServiceLocator.getRoleService();
        String principalId = securityPrincipal.getPrincipalId();

        for (SecurityModelMember principalModel : securityPrincipal.getPrincipalModels()) {
            Role modelRole = roleService.getRole(principalModel.getSecurityModel().getRoleId());
            updateSecurityModelRoleMember(modelRole, principalModel, MemberType.PRINCIPAL.getCode(), principalId, new HashMap<String, String>(0));
        }
    }

}
