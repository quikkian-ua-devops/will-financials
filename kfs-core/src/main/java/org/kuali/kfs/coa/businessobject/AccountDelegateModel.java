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

package org.kuali.kfs.coa.businessobject;

import org.apache.log4j.Logger;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class AccountDelegateModel extends PersistableBusinessObjectBase implements MutableInactivatable {
    private static final Logger LOG = Logger.getLogger(AccountDelegateModel.class);

    private String chartOfAccountsCode;
    private String organizationCode;
    private String accountDelegateModelName;
    private boolean active;
    private List<AccountDelegateModelDetail> accountDelegateModelDetails;

    private Organization organization;
    private Chart chartOfAccounts;

    /**
     * Default constructor.
     */
    public AccountDelegateModel() {
        accountDelegateModelDetails = new ArrayList<AccountDelegateModelDetail>();
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     *
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     *
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the accountDelegateModelName attribute.
     *
     * @return Returns the accountDelegateModelName
     */
    public String getAccountDelegateModelName() {
        return accountDelegateModelName;
    }

    /**
     * Sets the accountDelegateModelName attribute.
     *
     * @param accountDelegateModelName The accountDelegateModelName to set.
     */
    public void setAccountDelegateModelName(String organizationRoutingModelName) {
        this.accountDelegateModelName = organizationRoutingModelName;
    }


    /**
     * Gets the organization attribute.
     *
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     *
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the accountDelegateModelDetails attribute.
     *
     * @return Returns the accountDelegateModelDetails.
     */
    public List<AccountDelegateModelDetail> getAccountDelegateModelDetails() {
        return accountDelegateModelDetails;
    }

    /**
     * Sets the accountDelegateModelDetails attribute value.
     *
     * @param accountDelegateModelDetails The accountDelegateModelDetails to set.
     */
    public void setAccountDelegateModelDetails(List<AccountDelegateModelDetail> organizationRoutingModel) {
        this.accountDelegateModelDetails = organizationRoutingModel;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("accountDelegateModelName", this.accountDelegateModelName);
        return m;
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#linkEditableUserFields()
     */
    @Override
    public void linkEditableUserFields() {
        super.linkEditableUserFields();
        if (this == null) {
            throw new IllegalArgumentException("parameter passed in was null");
        }
        List bos = new ArrayList();
        bos.addAll(getAccountDelegateModelDetails());
        SpringContext.getBean(BusinessObjectService.class).linkUserFields(bos);
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
}
