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

package org.kuali.kfs.module.ld.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

/**
 * Labor business object for LaborObject.
 */
public class LaborObject extends PersistableBusinessObjectBase implements LaborLedgerObject, MutableInactivatable, FiscalYearBasedBusinessObject {
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String financialObjectCode;
    private boolean detailPositionRequiredIndicator;
    private boolean financialObjectHoursRequiredIndicator;
    private String financialObjectPayTypeCode;
    private String financialObjectFringeOrSalaryCode;
    private String positionObjectGroupCode;
    private boolean active;

    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private PositionObjectGroup positionObjectGroup;
    private SystemOptions option;

    /**
     * Default constructor.
     */
    public LaborObject() {

    }

    /**
     * Gets the universityFiscalYear
     *
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the financialObjectCode
     *
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the detailPositionRequiredIndicator
     *
     * @return Returns the detailPositionRequiredIndicator
     */
    public boolean isDetailPositionRequiredIndicator() {
        return detailPositionRequiredIndicator;
    }

    /**
     * Sets the detailPositionRequiredIndicator
     *
     * @param detailPositionRequiredIndicator The detailPositionRequiredIndicator to set.
     */
    public void setDetailPositionRequiredIndicator(boolean detailPositionRequiredIndicator) {
        this.detailPositionRequiredIndicator = detailPositionRequiredIndicator;
    }

    /**
     * Gets the financialObjectHoursRequiredIndicator
     *
     * @return Returns the financialObjectHoursRequiredIndicator
     */
    public boolean isFinancialObjectHoursRequiredIndicator() {
        return financialObjectHoursRequiredIndicator;
    }

    /**
     * Sets the financialObjectHoursRequiredIndicator
     *
     * @param financialObjectHoursRequiredIndicator The financialObjectHoursRequiredIndicator to set.
     */
    public void setFinancialObjectHoursRequiredIndicator(boolean financialObjectHoursRequiredIndicator) {
        this.financialObjectHoursRequiredIndicator = financialObjectHoursRequiredIndicator;
    }

    /**
     * Gets the financialObjectPayTypeCode
     *
     * @return Returns the financialObjectPayTypeCode
     */
    public String getFinancialObjectPayTypeCode() {
        return financialObjectPayTypeCode;
    }

    /**
     * Sets the financialObjectPayTypeCode
     *
     * @param financialObjectPayTypeCode The financialObjectPayTypeCode to set.
     */
    public void setFinancialObjectPayTypeCode(String financialObjectPayTypeCode) {
        this.financialObjectPayTypeCode = financialObjectPayTypeCode;
    }

    /**
     * Gets the financialObjectFringeOrSalaryCode
     *
     * @return Returns the financialObjectFringeOrSalaryCode
     */
    public String getFinancialObjectFringeOrSalaryCode() {
        return financialObjectFringeOrSalaryCode;
    }

    /**
     * Sets the financialObjectFringeOrSalaryCode
     *
     * @param financialObjectFringeOrSalaryCode The financialObjectFringeOrSalaryCode to set.
     */
    public void setFinancialObjectFringeOrSalaryCode(String financialObjectFringeOrSalaryCode) {
        this.financialObjectFringeOrSalaryCode = financialObjectFringeOrSalaryCode;
    }

    /**
     * Gets the positionObjectGroupCode
     *
     * @return Returns the positionObjectGroupCode
     */
    public String getPositionObjectGroupCode() {
        return positionObjectGroupCode;
    }

    /**
     * Sets the positionObjectGroupCode
     *
     * @param positionObjectGroupCode The positionObjectGroupCode to set.
     */
    public void setPositionObjectGroupCode(String positionObjectGroupCode) {
        this.positionObjectGroupCode = positionObjectGroupCode;
    }

    /**
     * Gets the financialObject
     *
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject
     *
     * @param financialObject The financialObject to set.
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the chartOfAccounts
     *
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the positionObjectGroup attribute.
     *
     * @return Returns the positionObjectGroup.
     */
    public PositionObjectGroup getPositionObjectGroup() {
        return positionObjectGroup;
    }

    /**
     * Sets the positionObjectGroup attribute value.
     *
     * @param positionObjectGroup The positionObjectGroup to set.
     */
    @Deprecated
    public void setPositionObjectGroup(PositionObjectGroup positionObjectGroup) {
        this.positionObjectGroup = positionObjectGroup;
    }

    /**
     * @see org.kuali.kfs.bo.LaborLedgerObject#getLaborLedgerPositionObjectGroup()
     */
    public LaborLedgerPositionObjectGroup getLaborLedgerPositionObjectGroup() {
        return this.positionObjectGroup;
    }

    /**
     * @see org.kuali.kfs.bo.LaborLedgerObject#setLaborLedgerPositionObjectGroup(org.kuali.kfs.bo.LaborLedgerPositionObjectGroup)
     */
    @Deprecated
    public void setLaborLedgerPositionObjectGroup(LaborLedgerPositionObjectGroup laborLedgerPositionObjectGroup) {
        this.positionObjectGroup = (PositionObjectGroup) laborLedgerPositionObjectGroup;
    }

    /**
     * Gets the option
     *
     * @return Returns the option.
     */
    public SystemOptions getOption() {
        return option;
    }

    /**
     * Sets the option
     *
     * @param option The option to set.
     */
    public void setOption(SystemOptions option) {
        this.option = option;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * This method (a hack by any other name...) returns a string so that an Labor Object Code can have a link to view its own
     * inquiry page after a look up
     *
     * @return the String "View Labor Object Code"
     */
    public String getLaborObjectCodeViewer() {
        return "View Labor Object Code";
    }

    /**
     * construct the key list of the business object.
     *
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectCode", this.financialObjectCode);

        return m;
    }
}
