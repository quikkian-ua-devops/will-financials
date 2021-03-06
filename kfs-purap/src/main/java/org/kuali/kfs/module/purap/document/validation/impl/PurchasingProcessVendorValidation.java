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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocumentBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class PurchasingProcessVendorValidation extends PurchasingAccountsPayableProcessVendorValidation {

    private VendorService vendorService;
    private ParameterService parameterService;
    private PostalCodeValidationService postalCodeValidationService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingDocument purDocument = (PurchasingDocument) event.getDocument();
        MessageMap errorMap = GlobalVariables.getMessageMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(PurapConstants.VENDOR_ERRORS);

        valid &= super.validate(event);

        if (!purDocument.getRequisitionSourceCode().equals(PurapConstants.RequisitionSources.B2B)) {

            //If there is a vendor and the transmission method is FAX and the fax number is blank, display
            //error that the fax number is required.
            if (purDocument.getVendorHeaderGeneratedIdentifier() != null && purDocument.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.FAX) && StringUtils.isBlank(purDocument.getVendorFaxNumber())) {
                valid &= false;
                String attributeLabel = SpringContext.getBean(DataDictionaryService.class).
                    getDataDictionary().getBusinessObjectEntry(VendorAddress.class.getName()).
                    getAttributeDefinition(VendorPropertyConstants.VENDOR_FAX_NUMBER).getLabel();
                errorMap.putError(VendorPropertyConstants.VENDOR_FAX_NUMBER, KFSKeyConstants.ERROR_REQUIRED, attributeLabel);
            }
        }

        VendorDetail vendorDetail = vendorService.getVendorDetail(purDocument.getVendorHeaderGeneratedIdentifier(), purDocument.getVendorDetailAssignedIdentifier());
        if (ObjectUtils.isNull(vendorDetail)) {
            return valid;
        }
        VendorHeader vendorHeader = vendorDetail.getVendorHeader();

        // make sure that the vendor is not debarred
        if (vendorDetail.isVendorDebarred()) {
            if (parameterService.getParameterValueAsBoolean(KFSConstants.OptionalModuleNamespaces.PURCHASING_ACCOUNTS_PAYABLE, "Requisition", PurapParameterConstants.SHOW_DEBARRED_VENDOR_WARNING_IND)) {
                if (StringUtils.isEmpty(((PurchasingDocumentBase) purDocument).getJustification())) {
                    errorMap.putWarning(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.WARNING_DEBARRED_VENDOR, vendorDetail.getVendorName());
                    valid &= false;
                }
            } else {
                errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_DEBARRED_VENDOR);
                valid &= false;
            }
        }

        // make sure that the vendor is of allowed type
        List<String> allowedVendorTypes = new ArrayList<String>(parameterService.getParameterValuesAsString(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapRuleConstants.PURAP_VENDOR_TYPE_ALLOWED_ON_REQ_AND_PO));
        if (allowedVendorTypes != null && !allowedVendorTypes.isEmpty()) {
            if (ObjectUtils.isNotNull(vendorHeader) && ObjectUtils.isNotNull(vendorHeader.getVendorTypeCode()) && !allowedVendorTypes.contains(vendorHeader.getVendorTypeCode())) {
                valid &= false;
                errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INVALID_VENDOR_TYPE);
            }
        }

        // make sure that the vendor is active
        if (!vendorDetail.isActiveIndicator()) {
            valid &= false;
            errorMap.putError(VendorPropertyConstants.VENDOR_NAME, PurapKeyConstants.ERROR_INACTIVE_VENDOR);
        }

        //make sure that the vendor contract is active and not expired.
        if (ObjectUtils.isNotNull(purDocument.getVendorContractGeneratedIdentifier())) {
            VendorContract vendorContract = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(VendorContract.class, purDocument.getVendorContractGeneratedIdentifier());
            Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();

            if (currentDate.compareTo(vendorContract.getVendorContractEndDate()) > 0 || !vendorContract.isActive()) {
                valid &= false;
                errorMap.putError(VendorPropertyConstants.VENDOR_CONTRACT_NAME, PurapKeyConstants.ERROR_INACTIVE_OR_EXPIRED_VENDOR_CONTRACT);
            }
        }
        // validate vendor address
        postalCodeValidationService.validateAddress(purDocument.getVendorCountryCode(), purDocument.getVendorStateCode(), purDocument.getVendorPostalCode(), PurapPropertyConstants.VENDOR_STATE_CODE, PurapPropertyConstants.VENDOR_POSTAL_CODE);

        errorMap.clearErrorPath();
        return valid;

    }

    public VendorService getVendorService() {
        return vendorService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the postalCodeValidationService attribute.
     *
     * @return Returns the postalCodeValidationService
     */

    public PostalCodeValidationService getPostalCodeValidationService() {
        return postalCodeValidationService;
    }

    /**
     * Sets the postalCodeValidationService attribute.
     *
     * @param postalCodeValidationService The postalCodeValidationService to set.
     */
    public void setPostalCodeValidationService(PostalCodeValidationService postalCodeValidationService) {
        this.postalCodeValidationService = postalCodeValidationService;
    }
}
