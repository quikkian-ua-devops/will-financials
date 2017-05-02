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

package org.kuali.kfs.fp.businessobject;

import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.krad.bo.TransientBusinessObjectBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.KIMPropertyConstants;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DisbursementPayee extends TransientBusinessObjectBase implements MutableInactivatable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementPayee.class);

    private String payeeIdNumber;
    private String payeeTypeCode;
    private String payeeTypeDescription;
    private String payeeName;

    private String paymentReasonCode;
    private String taxNumber;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String vendorName;
    private String vendorNumber;
    private String address;
    private boolean active;

    private String principalId;

    public final static String addressPattern = "{0}, {1}, {2} {3}";

    /**
     * Constructs a DisbursementPayee.java.
     */
    public DisbursementPayee() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(KFSPropertyConstants.PAYEE_ID_NUMBER, this.payeeIdNumber);
        map.put(KFSPropertyConstants.PAYEE_TYPE_CODE, this.payeeTypeCode);
        map.put(KFSPropertyConstants.PAYEE_NAME, this.payeeName);

        return map;
    }

    /**
     * convert the field names between Payee and Vendor
     *
     * @return a field name map of Payee and Vendor. The map key is a field name of Payee, and its value is a field name of Vendor
     */
    public static Map<String, String> getFieldConversionBetweenPayeeAndVendor() {
        Map<String, String> fieldConversionMap = new HashMap<String, String>();

        fieldConversionMap.put(KFSPropertyConstants.TAX_NUMBER, VendorPropertyConstants.VENDOR_TAX_NUMBER);

        fieldConversionMap.put(KFSPropertyConstants.VENDOR_NAME, KFSPropertyConstants.VENDOR_NAME);
        fieldConversionMap.put(KFSPropertyConstants.VENDOR_NUMBER, KFSPropertyConstants.VENDOR_NUMBER);

        fieldConversionMap.put(KFSPropertyConstants.PERSON_FIRST_NAME, VendorPropertyConstants.VENDOR_FIRST_NAME);
        fieldConversionMap.put(KFSPropertyConstants.PERSON_LAST_NAME, VendorPropertyConstants.VENDOR_LAST_NAME);

        fieldConversionMap.put(KFSPropertyConstants.ACTIVE, KFSPropertyConstants.ACTIVE_INDICATOR);

        return fieldConversionMap;
    }

    /**
     * convert the field names between Payee and Person
     *
     * @return a field name map of Payee and Person. The map key is a field name of Payee, and its value is a field name of Person
     */
    public static Map<String, String> getFieldConversionBetweenPayeeAndPerson() {
        Map<String, String> fieldConversionMap = new HashMap<String, String>();

        //    fieldConversionMap.put(KFSPropertyConstants.TAX_NUMBER, KIMPropertyConstants.Person.EXTERNAL_ID);

        fieldConversionMap.put(KFSPropertyConstants.PERSON_FIRST_NAME, KIMPropertyConstants.Person.FIRST_NAME);
        fieldConversionMap.put(KFSPropertyConstants.PERSON_LAST_NAME, KIMPropertyConstants.Person.LAST_NAME);

        fieldConversionMap.put(KFSPropertyConstants.EMPLOYEE_ID, KIMPropertyConstants.Person.EMPLOYEE_ID);
        fieldConversionMap.put(KFSPropertyConstants.ACTIVE, KFSPropertyConstants.ACTIVE);

        return fieldConversionMap;
    }

    /**
     * build a payee object from the given vendor object
     *
     * @param vendorDetail the given vendor object
     * @return a payee object built from the given vendor object
     */
    public static DisbursementPayee getPayeeFromVendor(VendorDetail vendorDetail) {
        DisbursementPayee disbursementPayee = new DisbursementPayee();

        disbursementPayee.setActive(vendorDetail.isActiveIndicator());

        disbursementPayee.setPayeeIdNumber(vendorDetail.getVendorNumber());
        disbursementPayee.setPayeeName(vendorDetail.getAltVendorName());
        disbursementPayee.setTaxNumber(vendorDetail.getVendorHeader().getVendorTaxNumber());

        String vendorTypeCode = vendorDetail.getVendorHeader().getVendorTypeCode();
        String payeeTypeCode = getVendorPayeeTypeCodeMapping().get(vendorTypeCode);
        disbursementPayee.setPayeeTypeCode(payeeTypeCode);

        String vendorAddress = MessageFormat.format(addressPattern, vendorDetail.getDefaultAddressLine1(), vendorDetail.getDefaultAddressCity(), vendorDetail.getDefaultAddressStateCode(), vendorDetail.getDefaultAddressCountryCode());
        disbursementPayee.setAddress(vendorAddress);

        return disbursementPayee;
    }

    /**
     * build a payee object from the given person object
     *
     * @param person the given person object
     * @return a payee object built from the given person object
     */
    public static DisbursementPayee getPayeeFromPerson(Person person) {
        DisbursementPayee disbursementPayee = new DisbursementPayee();

        disbursementPayee.setActive(person.isActive());

        disbursementPayee.setPayeeIdNumber(person.getEmployeeId());
        disbursementPayee.setPrincipalId(person.getPrincipalId());

        disbursementPayee.setPayeeName(person.getName());
        disbursementPayee.setTaxNumber(KFSConstants.BLANK_SPACE);

        disbursementPayee.setPayeeTypeCode(KFSConstants.PaymentPayeeTypes.EMPLOYEE);

        String personAddress = MessageFormat.format(addressPattern, person.getAddressLine1(), person.getAddressCity(), person.getAddressStateProvinceCode(), person.getAddressCountryCode());
        disbursementPayee.setAddress(personAddress);

        return disbursementPayee;
    }

    /**
     * build a payee object from the given customer object
     *
     * @param customer the given customer object
     * @return a payee object built from the given customer object
     */
    public static DisbursementPayee getPayeeFromCustomer(AccountsReceivableCustomer customer) {
        DisbursementPayee disbursementPayee = new DisbursementPayee();

        disbursementPayee.setActive(customer.isActive());

        disbursementPayee.setPayeeIdNumber(customer.getCustomerNumber());
        disbursementPayee.setPayeeName(customer.getCustomerName());
        disbursementPayee.setTaxNumber(customer.getCustomerTaxNbr());

        disbursementPayee.setPayeeTypeCode(KFSConstants.PaymentPayeeTypes.CUSTOMER);

        String vendorAddress = MessageFormat.format(addressPattern, customer.getPrimaryAddress().getCustomerLine1StreetAddress(),
            customer.getPrimaryAddress().getCustomerCityName(), customer.getPrimaryAddress().getCustomerStateCode(), customer.getPrimaryAddress().getCustomerCountryCode());
        disbursementPayee.setAddress(vendorAddress);

        return disbursementPayee;
    }

    /**
     * Gets the payeeIdNumber attribute.
     *
     * @return Returns the payeeIdNumber.
     */
    public String getPayeeIdNumber() {
        return payeeIdNumber;
    }

    /**
     * Sets the payeeIdNumber attribute value.
     *
     * @param payeeIdNumber The payeeIdNumber to set.
     */
    public void setPayeeIdNumber(String payeeIdNumber) {
        this.payeeIdNumber = payeeIdNumber;
    }

    /**
     * Gets the payeeTypeCode attribute.
     *
     * @return Returns the payeeTypeCode.
     */
    public String getPayeeTypeCode() {
        return payeeTypeCode;
    }

    /**
     * Sets the payeeTypeCode attribute value.
     *
     * @param payeeTypeCode The payeeTypeCode to set.
     */
    public void setPayeeTypeCode(String payeeTypeCode) {
        this.payeeTypeCode = payeeTypeCode;
    }

    /**
     * Gets the payeeName attribute.
     *
     * @return Returns the payeeName.
     */
    public String getPayeeName() {
        return payeeName;
    }

    /**
     * Sets the payeeName attribute value.
     *
     * @param payeeName The payeeName to set.
     */
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    /**
     * Gets the paymentReasonCode attribute.
     *
     * @return Returns the paymentReasonCode.
     */
    public String getPaymentReasonCode() {
        return paymentReasonCode;
    }

    /**
     * Sets the paymentReasonCode attribute value.
     *
     * @param paymentReasonCode The paymentReasonCode to set.
     */
    public void setPaymentReasonCode(String paymentReasonCode) {
        this.paymentReasonCode = paymentReasonCode;
    }

    /**
     * Gets the taxNumber attribute.
     *
     * @return Returns the taxNumber.
     */
    public String getTaxNumber() {
        return taxNumber;
    }

    /**
     * Sets the taxNumber attribute value.
     *
     * @param taxNumber The taxNumber to set.
     */
    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    /**
     * Gets the employeeId attribute.
     *
     * @return Returns the employeeId.
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets the employeeId attribute value.
     *
     * @param employeeId The employeeId to set.
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Gets the vendorName attribute.
     *
     * @return Returns the vendorName.
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute value.
     *
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Gets the address attribute.
     *
     * @return Returns the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address attribute value.
     *
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the vendorNumber attribute.
     *
     * @return Returns the vendorNumber.
     */
    public String getVendorNumber() {
        return vendorNumber;
    }

    /**
     * Sets the vendorNumber attribute value.
     *
     * @param vendorNumber The vendorNumber to set.
     */
    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
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
     * Gets the firstName attribute.
     *
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the firstName attribute value.
     *
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the lastName attribute.
     *
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastName attribute value.
     *
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the payeeTypeDescription attribute.
     *
     * @return Returns the payeeTypeDescription.
     */
    public String getPayeeTypeDescription() {
        DisbursementVoucherPayeeService payeeService = SpringContext.getBean(DisbursementVoucherPayeeService.class);

        return payeeService.getPayeeTypeDescription(payeeTypeCode);
    }

    /**
     * Sets the payeeTypeDescription attribute value.
     *
     * @param payeeTypeDescription The payeeTypeDescription to set.
     */
    public void setPayeeTypeDescription(String payeeTypeDescription) {
        this.payeeTypeDescription = payeeTypeDescription;
    }

    /**
     * Gets the principalId attribute.
     *
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute value.
     *
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    // do mapping between vendor type code and payee type code
    private static Map<String, String> getVendorPayeeTypeCodeMapping() {
        Map<String, String> payeeVendorTypeCodeMapping = new HashMap<String, String>();

        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.PURCHASE_ORDER, KFSConstants.PaymentPayeeTypes.VENDOR);
        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.DISBURSEMENT_VOUCHER, KFSConstants.PaymentPayeeTypes.VENDOR);
        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.REVOLVING_FUND, KFSConstants.PaymentPayeeTypes.REVOLVING_FUND_VENDOR);
        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.SUBJECT_PAYMENT, KFSConstants.PaymentPayeeTypes.SUBJECT_PAYMENT_VENDOR);
        payeeVendorTypeCodeMapping.put(VendorConstants.VendorTypes.REFUND_PAYMENT, KFSConstants.PaymentPayeeTypes.REFUND_VENDOR);

        return payeeVendorTypeCodeMapping;
    }
}
