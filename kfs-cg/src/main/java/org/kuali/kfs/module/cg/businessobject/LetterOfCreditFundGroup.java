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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

/**
 * Letter Of Credit Fund Group.
 */
public class LetterOfCreditFundGroup extends PersistableBusinessObjectBase implements MutableInactivatable, ContractsAndGrantsLetterOfCreditFundGroup {
    private String letterOfCreditFundGroupCode;
    private String letterOfCreditFundGroupDescription;
    private boolean active;

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode
     */
    @Override
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute.
     *
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the letterOfCreditFundGroupDescription attribute.
     *
     * @return Returns the letterOfCreditFundGroupDescription
     */
    @Override
    public String getLetterOfCreditFundGroupDescription() {
        return letterOfCreditFundGroupDescription;
    }

    /**
     * Sets the letterOfCreditFundGroupDescription attribute.
     *
     * @param letterOfCreditFundGroupDescription The letterOfCreditFundGroupDescription to set.
     */
    public void setLetterOfCreditFundGroupDescription(String letterOfCreditFundGroupDescription) {
        this.letterOfCreditFundGroupDescription = letterOfCreditFundGroupDescription;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
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
        m.put("letterOfCreditFundGroupCode", this.letterOfCreditFundGroupCode);
        m.put("letterOfCreditFundGroupDescription", letterOfCreditFundGroupDescription);
        m.put(KFSPropertyConstants.ACTIVE, active);
        return m;
    }
}
