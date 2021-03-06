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
package org.kuali.kfs.sec.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecKeyConstants;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sec.businessobject.SecurityDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModel;
import org.kuali.kfs.sec.businessobject.SecurityModelDefinition;
import org.kuali.kfs.sec.businessobject.SecurityModelMember;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import java.util.HashMap;
import java.util.Map;


/**
 * Implements business rules checks on the SecurityModel maintenance document
 */
public class SecurityModelRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityModelRule.class);

    private SecurityModel oldSecurityModel;
    private SecurityModel newSecurityModel;

    protected volatile static BusinessObjectService businessObjectService;

    public SecurityModelRule() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(document);

        if (!isValid) {
            return isValid;
        }

        boolean isMaintenanceEdit = document.isEdit();

        isValid &= validateSecurityModel(isMaintenanceEdit);

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        if (!isValid) {
            return isValid;
        }

        boolean isMaintenanceEdit = document.isEdit();

        isValid &= validateSecurityModel(isMaintenanceEdit);

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     * java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);

        if (!isValid) {
            return isValid;
        }

        if (SecPropertyConstants.MODEL_DEFINITIONS.equals(collectionName)) {
            isValid &= validateModelDefinition((SecurityModelDefinition) line, "");
        }

        if (SecPropertyConstants.MODEL_MEMBERS.equals(collectionName)) {
            isValid &= validateModelMember((SecurityModelMember) line, "");
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        oldSecurityModel = (SecurityModel) super.getOldBo();
        newSecurityModel = (SecurityModel) super.getNewBo();
    }

    /**
     * Validates the new security model record
     *
     * @param isMaintenanceEdit boolean indicating whether the maintenance action is an edit (true), or a new/copy (false)
     * @return boolean true if validation was successful, false if there are errors
     */
    protected boolean validateSecurityModel(boolean isMaintenanceEdit) {
        boolean isValid = true;

        if (!isMaintenanceEdit) {
            boolean validModelName = verifyModelNameIsUnique(newSecurityModel, KRADConstants.MAINTENANCE_NEW_MAINTAINABLE);
            if (!validModelName) {
                isValid = false;
            }
        }

        // check to make sure there is at least one model definition
        if (newSecurityModel.getModelDefinitions() == null || newSecurityModel.getModelDefinitions().size() == 0) {
            GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS, SecKeyConstants.ERROR_MODEL_DEFINITION_MISSING);
        }

        int index = 0;
        for (SecurityModelDefinition modelDefinition : newSecurityModel.getModelDefinitions()) {
            String errorKeyPrefix = KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + SecPropertyConstants.MODEL_DEFINITIONS + "[" + index + "].";

            boolean modelDefinitionValid = validateModelDefinition(modelDefinition, errorKeyPrefix);
            if (!modelDefinitionValid) {
                isValid = false;
            }

            index++;
        }

        index = 0;
        for (SecurityModelMember modelMember : newSecurityModel.getModelMembers()) {
            String errorKeyPrefix = KRADConstants.MAINTENANCE_NEW_MAINTAINABLE + SecPropertyConstants.MODEL_MEMBERS + "[" + index + "].";

            boolean modelMemberValid = validateModelMember(modelMember, errorKeyPrefix);
            if (!modelMemberValid) {
                isValid = false;
            }

            index++;
        }


        return isValid;
    }

    /**
     * For new or copy action verifies the name given for the model is not being used by another model or definition
     *
     * @param securityModel  SecurityModel with name to check
     * @param errorKeyPrefix String errorPrefix to use if any errors are found
     * @return boolean true if name exists, false if not
     */
    protected boolean verifyModelNameIsUnique(SecurityModel securityModel, String errorKeyPrefix) {
        boolean isValid = true;

        Map<String, String> searchValues = new HashMap<String, String>();
        searchValues.put(KFSPropertyConstants.NAME, securityModel.getName());

        int matchCount = getBusinessObjectService().countMatching(SecurityModel.class, searchValues);
        if (matchCount > 0) {
            GlobalVariables.getMessageMap().putError(errorKeyPrefix + KFSPropertyConstants.NAME, SecKeyConstants.ERROR_MODEL_NAME_NON_UNIQUE, securityModel.getName());
            isValid = false;
        }

        matchCount = getBusinessObjectService().countMatching(SecurityDefinition.class, searchValues);
        if (matchCount > 0) {
            GlobalVariables.getMessageMap().putError(errorKeyPrefix + KFSPropertyConstants.NAME, SecKeyConstants.ERROR_MODEL_NAME_NON_UNIQUE, securityModel.getName());
            isValid = false;
        }

        return isValid;
    }

    /**
     * Validates a definition assignment to the model
     *
     * @param modelDefinition SecurityModelDefinition to validate
     * @param errorKeyPrefix  String errorPrefix to use if any errors are found
     * @return boolean true if validation was successful, false if there are errors
     */
    protected boolean validateModelDefinition(SecurityModelDefinition modelDefinition, String errorKeyPrefix) {
        boolean isValid = true;

        modelDefinition.refreshNonUpdateableReferences();

        if (ObjectUtils.isNull(modelDefinition.getSecurityDefinition())) {
            return false;
        }

        String attributeName = modelDefinition.getSecurityDefinition().getSecurityAttribute().getName();
        String attributeValue = modelDefinition.getAttributeValue();

        // if value is blank (which is allowed) no need to validate
        if (StringUtils.isBlank(attributeValue)) {
            return true;
        }

        // descend attributes do not allow multiple values or wildcards, and operator must be equal
        if (SecConstants.SecurityAttributeNames.CHART_DESCEND_HIERARCHY.equals(attributeName) || SecConstants.SecurityAttributeNames.ORGANIZATION_DESCEND_HIERARCHY.equals(attributeName)) {
            if (StringUtils.contains(attributeValue, SecConstants.SecurityValueSpecialCharacters.MULTI_VALUE_SEPERATION_CHARACTER)) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.ATTRIBUTE_VALUE, SecKeyConstants.ERROR_MODEL_DEFINITION_MULTI_ATTR_VALUE, attributeName);
                isValid = false;
            }

            if (StringUtils.contains(attributeValue, SecConstants.SecurityValueSpecialCharacters.WILDCARD_CHARACTER)) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.ATTRIBUTE_VALUE, SecKeyConstants.ERROR_MODEL_DEFINITION_WILDCARD_ATTR_VALUE, attributeName);
                isValid = false;
            }

            if (!SecConstants.SecurityDefinitionOperatorCodes.EQUAL.equals(modelDefinition.getOperatorCode())) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.OPERATOR_CODE, SecKeyConstants.ERROR_MODEL_DEFINITION_OPERATOR_CODE_NOT_EQUAL, attributeName);
                isValid = false;
            }
        }

        // validate attribute value for existence
        isValid = isValid && SecurityValidationUtil.validateAttributeValue(attributeName, attributeValue, errorKeyPrefix);

        return isValid;
    }

    /**
     * Validates a member assignment to the model
     *
     * @param modelMember    SecurityModelMember to validate
     * @param errorKeyPrefix String errorPrefix to use if any errors are found
     * @return boolean true if validation was successful, false if there are errors
     */
    protected boolean validateModelMember(SecurityModelMember modelMember, String errorKeyPrefix) {
        boolean isValid = true;

        String memberId = modelMember.getMemberId();
        String memberTypeCode = modelMember.getMemberTypeCode();

        if (StringUtils.isBlank(memberId) || StringUtils.isBlank(memberTypeCode)) {
            return false;
        }

        if (MemberType.PRINCIPAL.getCode().equals(memberTypeCode)) {
            Principal principalInfo = KimApiServiceLocator.getIdentityService().getPrincipal(memberId);
            if (principalInfo == null) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.MEMBER_ID, SecKeyConstants.ERROR_MODEL_MEMBER_ID_NOT_VALID, memberId, memberTypeCode);
                isValid = false;
            }
        } else if (MemberType.ROLE.getCode().equals(memberTypeCode)) {
            Role roleInfo = KimApiServiceLocator.getRoleService().getRole(memberId);
            if (roleInfo == null) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.MEMBER_ID, SecKeyConstants.ERROR_MODEL_MEMBER_ID_NOT_VALID, memberId, memberTypeCode);
                isValid = false;
            }
        } else if (MemberType.GROUP.getCode().equals(memberTypeCode)) {
            Group groupInfo = KimApiServiceLocator.getGroupService().getGroup(memberId);
            if (groupInfo == null) {
                GlobalVariables.getMessageMap().putError(errorKeyPrefix + SecPropertyConstants.MEMBER_ID, SecKeyConstants.ERROR_MODEL_MEMBER_ID_NOT_VALID, memberId, memberTypeCode);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * @return the default implementation of the business object service
     */
    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
}
