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
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

import java.util.LinkedHashMap;

/**
 * Class representing a ResearchRiskType.
 */
public class ResearchRiskType extends PersistableBusinessObjectBase implements MutableInactivatable {
    /**
     * Constant values for research risk type notification values
     */
    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String ALL = "A";
    public static final String NEVER = "X";

    private String researchRiskTypeCode;
    private boolean active;
    private String researchRiskTypeDescription;
    private Integer researchRiskTypeSortNumber;
    private String researchRiskTypeNotificationValue;

    /**
     * Gets the researchRiskTypeCode attribute.
     *
     * @return Returns the researchRiskTypeCode
     */
    public String getResearchRiskTypeCode() {
        return researchRiskTypeCode;
    }

    /**
     * Sets the researchRiskTypeCode attribute.
     *
     * @param researchRiskTypeCode The researchRiskTypeCode to set.
     */
    public void setResearchRiskTypeCode(String researchRiskTypeCode) {
        this.researchRiskTypeCode = researchRiskTypeCode;
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
     * Gets the researchRiskTypeDescription attribute.
     *
     * @return Returns the researchRiskTypeDescription
     */
    public String getResearchRiskTypeDescription() {
        return researchRiskTypeDescription;
    }

    /**
     * Sets the researchRiskTypeDescription attribute.
     *
     * @param researchRiskTypeDescription The researchRiskTypeDescription to set.
     */
    public void setResearchRiskTypeDescription(String researchRiskTypeDescription) {
        this.researchRiskTypeDescription = researchRiskTypeDescription;
    }


    /**
     * Gets the researchRiskTypeSortNumber attribute.
     *
     * @return Returns the researchRiskTypeSortNumber.
     */
    public Integer getResearchRiskTypeSortNumber() {
        return researchRiskTypeSortNumber;
    }

    /**
     * Sets the researchRiskTypeSortNumber attribute value.
     *
     * @param researchRiskTypeSortNumber The researchRiskTypeSortNumber to set.
     */
    public void setResearchRiskTypeSortNumber(Integer researchRiskTypeSortNumber) {
        this.researchRiskTypeSortNumber = researchRiskTypeSortNumber;
    }


    /**
     * Gets the researchRiskTypeNotificationValue attribute.
     *
     * @return Returns the researchRiskTypeNotificationValue.
     */
    public String getResearchRiskTypeNotificationValue() {
        return researchRiskTypeNotificationValue;
    }

    /**
     * Sets the researchRiskTypeNotificationValue attribute value.
     *
     * @param researchRiskTypeNotificationValue The researchRiskTypeNotificationValue to set.
     */
    public void setResearchRiskTypeNotificationValue(String researchRiskTypeNotificationValue) {
        this.researchRiskTypeNotificationValue = researchRiskTypeNotificationValue;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("researchRiskTypeCode", this.researchRiskTypeCode);
        return m;
    }
}
