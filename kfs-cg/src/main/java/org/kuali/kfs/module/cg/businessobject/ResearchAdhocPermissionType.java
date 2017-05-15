/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cg.businessobject;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

import java.util.LinkedHashMap;

public class ResearchAdhocPermissionType extends PersistableBusinessObjectBase {
    private String permissionTypeCode;
    private String permissionTypeDescription;

    public ResearchAdhocPermissionType() {
        super();
    }

    public ResearchAdhocPermissionType(String permissionTypeCode, String permissionTypeDescription) {
        this.permissionTypeCode = permissionTypeCode;
        this.permissionTypeDescription = permissionTypeDescription;
    }

    public String getPermissionTypeCode() {
        return permissionTypeCode;
    }

    public void setPermissionTypeCode(String permissionTypeCode) {
        this.permissionTypeCode = permissionTypeCode;
    }

    public String getPermissionTypeDescription() {
        return permissionTypeDescription;
    }

    public void setPermissionTypeDescription(String permissionTypeDescription) {
        this.permissionTypeDescription = permissionTypeDescription;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("permissionTypeCode", this.permissionTypeCode);
        m.put("permissionTypeDescription", this.permissionTypeDescription);

        return m;
    }
}
