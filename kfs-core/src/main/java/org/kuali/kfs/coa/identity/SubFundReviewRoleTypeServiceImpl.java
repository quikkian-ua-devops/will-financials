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
package org.kuali.kfs.coa.identity;

import org.kuali.kfs.kns.kim.role.RoleTypeServiceBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.util.KimCommonUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SubFundReviewRoleTypeServiceImpl extends RoleTypeServiceBase {
    private DocumentTypeService documentTypeService;

    @Override
    protected boolean performMatch(Map<String, String> qualification, Map<String, String> roleQualifier) {
        if (KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.SUB_FUND_GROUP_CODE)) {
            Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
            if (roleQualifier.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                potentialParentDocumentTypeNames.add(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
            }
            if (qualification == null || qualification.isEmpty()) {
                return potentialParentDocumentTypeNames.isEmpty();
            }
            return potentialParentDocumentTypeNames.isEmpty() || qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME).equalsIgnoreCase(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
        }
        return false;
    }

    protected DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        }
        return this.documentTypeService;
    }

}
