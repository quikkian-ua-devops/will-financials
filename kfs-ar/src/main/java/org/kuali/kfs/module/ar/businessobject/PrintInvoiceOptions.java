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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

import java.util.LinkedHashMap;


public class PrintInvoiceOptions extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String printInvoiceIndicator;
    private String printInvoiceDescription;
    private boolean active;

    /**
     * Gets the printInvoiceIndicator attribute.
     *
     * @return Returns the printInvoiceIndicator
     */
    public String getPrintInvoiceIndicator() {
        return printInvoiceIndicator;
    }

    /**
     * Sets the printInvoiceIndicator attribute.
     *
     * @param printInvoiceIndicator The printInvoiceIndicator to set.
     */
    public void setPrintInvoiceIndicator(String printInvoiceIndicator) {
        this.printInvoiceIndicator = printInvoiceIndicator;
    }


    /**
     * Gets the printInvoiceDescription attribute.
     *
     * @return Returns the printInvoiceDescription
     */
    public String getPrintInvoiceDescription() {
        return printInvoiceDescription;
    }

    /**
     * Sets the printInvoiceDescription attribute.
     *
     * @param printInvoiceDescription The printInvoiceDescription to set.
     */
    public void setPrintInvoiceDescription(String printInvoiceDescription) {
        this.printInvoiceDescription = printInvoiceDescription;
    }


    /**
     * Gets the active attribute.
     *
     * @return Returns the active
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("printInvoiceIndicator", this.printInvoiceIndicator);
        return m;
    }
}
