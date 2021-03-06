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
package org.kuali.kfs.pdp.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.lookup.AbstractPayeeLookupableHelperServiceImpl;
import org.kuali.kfs.kns.web.ui.ResultRow;
import org.kuali.kfs.krad.exception.ValidationException;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.krad.util.BeanPropertyComparator;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.businessobject.ACHPayee;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Payee lookupable for PDP Payee ACH. Builds off of DV Payee lookup by taking off payment reason code, and adding adding entity id
 * to search and return url
 */
public class ACHPayeeLookupableHelperServiceImpl extends AbstractPayeeLookupableHelperServiceImpl {

    /**
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     * <p>
     * KRAD Conversion: Lookupable performs customization of the search results and performs a sort
     * by retrieving the default sort columns using data dictionary service..
     * <p>
     * Uses data dictionary.
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<DisbursementPayee> searchResults = new ArrayList<DisbursementPayee>();

        String payeeTypeCode = fieldValues.get(KFSPropertyConstants.PAYEE_TYPE_CODE);
        if (StringUtils.isBlank(payeeTypeCode)) {
            GlobalVariables.getMessageMap().putInfo(KFSPropertyConstants.PAYEE_TYPE_CODE, PdpKeyConstants.MESSAGE_PDP_ACH_PAYEE_LOOKUP_NO_PAYEE_TYPE);
        }

        if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER)) || StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.VENDOR_NAME)) || (StringUtils.isNotBlank(payeeTypeCode) && PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(payeeTypeCode))) {
            searchResults.addAll(this.getVendorsAsPayees(fieldValues));
        } else if (StringUtils.isNotBlank(fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID)) || StringUtils.isNotBlank(fieldValues.get(KIMPropertyConstants.Person.ENTITY_ID)) || (StringUtils.isNotBlank(payeeTypeCode) && (PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equals(payeeTypeCode) || PdpConstants.PayeeIdTypeCodes.ENTITY.equals(payeeTypeCode)))) {
            searchResults.addAll(this.getPersonAsPayees(fieldValues));
        } else {
            searchResults.addAll(this.getVendorsAsPayees(fieldValues));
            searchResults.addAll(this.getPersonAsPayees(fieldValues));
        }

        CollectionIncomplete results = new CollectionIncomplete(searchResults, Long.valueOf(searchResults.size()));

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }

        return results;
    }


    /**
     * Override to set entity id as the payee id and set the pdp payee type
     *
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#getPayeeFromPerson(org.kuali.rice.kim.api.identity.Person,
     * java.util.Map)
     */
    @Override
    protected DisbursementPayee getPayeeFromPerson(Person personDetail, Map<String, String> fieldValues) {
        DisbursementPayee payee = super.getPayeeFromPerson(personDetail, fieldValues);

        String payeeTypeCode = fieldValues.get(KFSPropertyConstants.PAYEE_TYPE_CODE);

        ACHPayee achPayee = new ACHPayee();

        if (PdpConstants.PayeeIdTypeCodes.ENTITY.equals(payeeTypeCode)) {
            achPayee.setPayeeIdNumber(personDetail.getEntityId());
            achPayee.setPayeeTypeCode(PdpConstants.PayeeIdTypeCodes.ENTITY);
        } else {
            achPayee.setPayeeIdNumber(personDetail.getEmployeeId());
            achPayee.setPayeeTypeCode(PdpConstants.PayeeIdTypeCodes.EMPLOYEE);
        }

        achPayee.setPayeeName(payee.getPayeeName());
        achPayee.setPrincipalId(payee.getPrincipalId());
        achPayee.setTaxNumber(payee.getTaxNumber());
        achPayee.setAddress(payee.getAddress());
        achPayee.setActive(payee.isActive());

        return achPayee;
    }


    /**
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#getPayeeFromVendor(org.kuali.kfs.vnd.businessobject.VendorDetail,
     * java.util.Map)
     */
    @Override
    protected DisbursementPayee getPayeeFromVendor(VendorDetail vendorDetail, Map<String, String> fieldValues) {
        DisbursementPayee payee = super.getPayeeFromVendor(vendorDetail, fieldValues);

        ACHPayee achPayee = new ACHPayee();

        achPayee.setPayeeIdNumber(payee.getPayeeIdNumber());
        achPayee.setPayeeTypeCode(PdpConstants.PayeeIdTypeCodes.VENDOR_ID);
        achPayee.setPayeeName(payee.getPayeeName());
        achPayee.setPrincipalId(payee.getPrincipalId());
        achPayee.setTaxNumber(payee.getTaxNumber());
        achPayee.setAddress(payee.getAddress());
        achPayee.setActive(payee.isActive());

        return achPayee;
    }


    /**
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        String vendorName = (String) fieldValues.get(KFSPropertyConstants.VENDOR_NAME);
        String vendorNumber = (String) fieldValues.get(KFSPropertyConstants.VENDOR_NUMBER);
        String employeeId = (String) fieldValues.get(KIMPropertyConstants.Person.EMPLOYEE_ID);
        String entityId = (String) fieldValues.get(KIMPropertyConstants.Person.ENTITY_ID);
        String payeeTypeCode = (String) fieldValues.get(KFSPropertyConstants.PAYEE_TYPE_CODE);

        // only can use the vendor name and vendor number fields or the employee id field, but not both.
        boolean isVendorInfoEntered = StringUtils.isNotBlank(vendorName) || StringUtils.isNotBlank(vendorNumber);
        if (StringUtils.isNotBlank(entityId) && isVendorInfoEntered) {
            String messageKey = KFSKeyConstants.ERROR_DV_VENDOR_EMPLOYEE_CONFUSION;

            String vendorNameLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NAME);
            String vendorNumberLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NUMBER);
            String entityIdLabel = this.getAttributeLabel(KIMPropertyConstants.Person.ENTITY_ID);

            GlobalVariables.getMessageMap().putError(KIMPropertyConstants.Person.ENTITY_ID, messageKey, entityIdLabel, vendorNameLabel, vendorNumberLabel);
        }

        boolean isEmployeeInfoEntered = StringUtils.isNotBlank(employeeId) || StringUtils.isNotBlank(entityId);
        boolean payeeTypeEntered = StringUtils.isNotBlank(payeeTypeCode);

        if (payeeTypeEntered && PdpConstants.PayeeIdTypeCodes.VENDOR_ID.equals(payeeTypeCode) && isEmployeeInfoEntered) {
            String messageKey = PdpKeyConstants.ERROR_PAYEE_LOOKUP_VENDOR_EMPLOYEE_CONFUSION;

            String employeeIdLabel = this.getAttributeLabel(KIMPropertyConstants.Person.EMPLOYEE_ID);
            String entityIdLabel = this.getAttributeLabel(KIMPropertyConstants.Person.ENTITY_ID);
            String payeeTypeLabel = this.getAttributeLabel(KFSPropertyConstants.PAYEE_TYPE_CODE);

            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_TYPE_CODE, messageKey, payeeTypeLabel, payeeTypeCode, employeeIdLabel, entityIdLabel);
        } else if (payeeTypeEntered && (PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equals(payeeTypeCode) || PdpConstants.PayeeIdTypeCodes.ENTITY.equals(payeeTypeCode)) && isVendorInfoEntered) {
            String messageKey = PdpKeyConstants.ERROR_PAYEE_LOOKUP_VENDOR_EMPLOYEE_CONFUSION;

            String vendorNameLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NAME);
            String vendorNumberLabel = this.getAttributeLabel(KFSPropertyConstants.VENDOR_NUMBER);
            String payeeTypeLabel = this.getAttributeLabel(KFSPropertyConstants.PAYEE_TYPE_CODE);

            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.PAYEE_TYPE_CODE, messageKey, payeeTypeLabel, payeeTypeCode, vendorNameLabel, vendorNumberLabel);
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    /**
     * Override to not filter rows based on payment reason
     *
     * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#filterReturnUrl(java.util.List,
     * java.util.List, java.lang.String)
     * <p>
     * KRAD Conversion: Performs customization of the result list of rows.
     * <p>
     * No use of data dictionary
     */
    @Override

    protected void filterReturnUrl(List<ResultRow> resultRowList, List<DisbursementPayee> payeeList, String paymentReasonCode) {
    }

}
