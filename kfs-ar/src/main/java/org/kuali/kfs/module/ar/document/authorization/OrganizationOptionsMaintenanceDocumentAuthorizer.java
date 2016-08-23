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
package org.kuali.kfs.module.ar.document.authorization;

import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

import java.util.Map;

public class OrganizationOptionsMaintenanceDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {
    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        OrganizationOptions organizationOptions = null;
        if (businessObject instanceof MaintenanceDocument) {
            organizationOptions = (OrganizationOptions) ((MaintenanceDocument) businessObject).getNewMaintainableObject().getBusinessObject();
        } else {
            organizationOptions = (OrganizationOptions) businessObject;
        }
        attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, organizationOptions.getChartOfAccountsCode());
        attributes.put(KfsKimAttributes.ORGANIZATION_CODE, organizationOptions.getOrganizationCode());
        attributes.put(KFSPropertyConstants.PROCESSING_CHART_OF_ACCT_CD, organizationOptions.getProcessingChartOfAccountCode());
        attributes.put(KFSPropertyConstants.PROCESSING_ORGANIZATION_CODE, organizationOptions.getProcessingOrganizationCode());
    }
}
