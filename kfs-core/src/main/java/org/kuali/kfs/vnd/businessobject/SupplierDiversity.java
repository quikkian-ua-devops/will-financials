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

package org.kuali.kfs.vnd.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

/**
 * Ways in which chosen Vendors are demonstrably diverse, including having certification as a Minority-owned or Woman-owned Business
 * Enterprise (M/WBE), as a Small Business Enterprise (SBE), etc.
 */
public class SupplierDiversity extends PersistableBusinessObjectBase implements MutableInactivatable{

    private String vendorSupplierDiversityCode;
    private String vendorSupplierDiversityDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public SupplierDiversity() {

    }

    public String getVendorSupplierDiversityCode() {

        return vendorSupplierDiversityCode;
    }

    public void setVendorSupplierDiversityCode(String vendorSupplierDiversityCode) {
        this.vendorSupplierDiversityCode = vendorSupplierDiversityCode;
    }

    public String getVendorSupplierDiversityDescription() {

        return vendorSupplierDiversityDescription;
    }

    public void setVendorSupplierDiversityDescription(String vendorSupplierDiversityDescription) {
        this.vendorSupplierDiversityDescription = vendorSupplierDiversityDescription;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("vendorSupplierDiversityCode", this.vendorSupplierDiversityCode);

        return m;
    }
}
