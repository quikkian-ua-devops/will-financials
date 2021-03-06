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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

import java.util.LinkedHashMap;

/**
 * Method of Invoice Transmission
 */
public class InvoiceTransmissionMethod extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String invoiceTransmissionMethodCode;
    private String invoiceTransmissionMethodDescription;
    private boolean active;


    public String getInvoiceTransmissionMethodCode() {
        return invoiceTransmissionMethodCode;
    }

    public void setInvoiceTransmissionMethodCode(String invoiceTransmissionMethodCode) {
        this.invoiceTransmissionMethodCode = invoiceTransmissionMethodCode;
    }

    public String getInvoiceTransmissionMethodDescription() {
        return invoiceTransmissionMethodDescription;
    }

    public void setInvoiceTransmissionMethodDescription(String invoiceTransmissionMethodDescription) {
        this.invoiceTransmissionMethodDescription = invoiceTransmissionMethodDescription;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("invoiceTransmissionMethodCode", this.invoiceTransmissionMethodCode);
        m.put("invoiceTransmissionMethodDescription", this.invoiceTransmissionMethodDescription);
        return m;
    }

}
