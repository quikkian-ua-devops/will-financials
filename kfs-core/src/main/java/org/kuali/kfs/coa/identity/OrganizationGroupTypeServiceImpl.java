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

import org.kuali.kfs.kns.kim.group.GroupTypeServiceBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrganizationGroupTypeServiceImpl extends GroupTypeServiceBase {
    protected static final String DOCUMENT_TYPE_NAME = "ORG";

    List<String> workflowRoutingAttributes = new ArrayList<String>(2);

    {
        workflowRoutingAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        workflowRoutingAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
    }

    @Override
    public List<String> getWorkflowRoutingAttributes(String routeLevel) {
        return Collections.unmodifiableList(workflowRoutingAttributes);
    }

    @Override
    public String getWorkflowDocumentTypeName() {
        return DOCUMENT_TYPE_NAME;
    }
}
