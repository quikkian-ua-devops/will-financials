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
package org.kuali.kfs.module.external.kc.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "unitDTO", propOrder = {
    "organizationId",
    "parentUnitNumber",
    "unitAdministrators",
    "unitName",
    "unitNumber"
})
public class UnitDTO implements ContractsAndGrantsUnit, Serializable {

    private static final long serialVersionUID = 7517946137745989736L;

    private String unitNumber;
    private String parentUnitNumber;

    /* maybe you don't need this? */
    private String organizationId;
    private String unitName;

    /* List of principal ids */
    private List<String> unitAdministrators;

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getParentUnitNumber() {
        return parentUnitNumber;
    }

    public void setParentUnitNumber(String parentUnitNumber) {
        this.parentUnitNumber = parentUnitNumber;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<String> getUnitAdministrators() {
        return unitAdministrators;
    }

    public void setUnitAdministrators(List<String> unitAdministrators) {
        this.unitAdministrators = unitAdministrators;
    }


    public void prepareForWorkflow() {
        // TODO Auto-generated method stub

    }

    public void refresh() {
        // TODO Auto-generated method stub

    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("UnitDTO", this.unitNumber);
        return m;
    }

}

