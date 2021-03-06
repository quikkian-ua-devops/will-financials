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
/*
 * Created on Jul 7, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiInteger;

import java.util.LinkedHashMap;

public class CustomerBank extends PersistableBusinessObjectBase implements MutableInactivatable {
    private KualiInteger customerId;
    private String bankCode;
    private String disbursementTypeCode;

    private CustomerProfile customerProfile;
    private Bank bank;
    private DisbursementType disbursementType;

    private boolean active;

    public CustomerBank() {
        super();
    }

    /**
     * Gets the customerId attribute.
     *
     * @return Returns the customerId.
     */
    public KualiInteger getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customerId attribute value.
     *
     * @param customerId The customerId to set.
     */
    public void setCustomerId(KualiInteger customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the customerProfile attribute.
     *
     * @return Returns the customerProfile.
     */
    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    /**
     * Sets the customerProfile attribute value.
     *
     * @param customerProfile The customerProfile to set.
     */
    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }

    /**
     * Gets the bankCode attribute.
     *
     * @return Returns the bankCode.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bankCode attribute value.
     *
     * @param bankCode The bankCode to set.
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    /**
     * Gets the bank attribute.
     *
     * @return Returns the bank.
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Sets the bank attribute value.
     *
     * @param bank The bank to set.
     */
    public void setBank(Bank bank) {
        this.bank = bank;
    }

    /**
     * Gets the disbursementTypeCode attribute.
     *
     * @return Returns the disbursementTypeCode.
     */
    public String getDisbursementTypeCode() {
        return disbursementTypeCode;
    }

    /**
     * Sets the disbursementTypeCode attribute value.
     *
     * @param disbursementTypeCode The disbursementTypeCode to set.
     */
    public void setDisbursementTypeCode(String disbursementTypeCode) {
        this.disbursementTypeCode = disbursementTypeCode;
    }

    /**
     * Gets the disbursementType attribute.
     *
     * @return Returns the disbursementType.
     */
    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    /**
     * Sets the disbursementType attribute value.
     *
     * @param disbursementType The disbursementType to set.
     */
    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PdpPropertyConstants.CUSTOMER_ID, this.customerId);
        m.put(PdpPropertyConstants.DISBURSEMENT_TYPE_CODE, this.disbursementTypeCode);
        m.put(KFSPropertyConstants.BANK_CODE, this.bankCode);

        return m;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }


}
