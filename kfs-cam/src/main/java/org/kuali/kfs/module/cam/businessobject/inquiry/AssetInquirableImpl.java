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
package org.kuali.kfs.module.cam.businessobject.inquiry;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.kns.inquiry.KualiInquirableImpl;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.web.ui.Section;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.module.cam.document.service.RetirementInfoService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AssetInquirableImpl extends KualiInquirableImpl {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetInquirableImpl.class);

    /**
     * Executes service methods to populate appropriate data in the Asset BO.
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
     */
    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        Asset asset = (Asset) super.getBusinessObject(fieldValues);

        if (ObjectUtils.isNotNull(asset)) {
            // Identifies the latest location information
            AssetLocationService assetlocationService = SpringContext.getBean(AssetLocationService.class);
            assetlocationService.setOffCampusLocation(asset);

            // Calculates payment summary and depreciation summary based on available payment records
            PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
            paymentSummaryService.calculateAndSetPaymentSummary(asset);

            // Identifies the merge history and separation history based on asset disposition records
            AssetService assetService = SpringContext.getBean(AssetService.class);
            assetService.setSeparateHistory(asset);

            // Finds out the latest retirement info, is asset is currently retired.
            RetirementInfoService retirementInfoService = SpringContext.getBean(RetirementInfoService.class);
            retirementInfoService.setRetirementInfo(asset);
            retirementInfoService.setMergeHistory(asset);

            // Finds out the latest equipment loan or return information if available
            EquipmentLoanOrReturnService equipmentLoanOrReturnService = SpringContext.getBean(EquipmentLoanOrReturnService.class);
            equipmentLoanOrReturnService.setEquipmentLoanInfo(asset);
        }

        return asset;
    }

    /**
     * Hide payments if there are more then the allowable number.
     *
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getSections(org.kuali.rice.krad.bo.BusinessObject)
     * <p>
     * KRAD Conversion: Inquirable performs conditional display/hiding of the sections on the inquiry
     * But all field/section definitions are in data dictionary for bo Asset.
     */
    @Override
    public List<Section> getSections(BusinessObject businessObject) {
        List<Section> sections = super.getSections(businessObject);

        // sectionToRemove is hoky but it looks like that section.setHidden doesn't work on inquirable. And to avoid
        // ConcurrentModificationException we do this
        Section sectionToRemove = null;

        Asset asset = (Asset) businessObject;
        for (Section section : sections) {
            if (CamsConstants.Asset.SECTION_ID_PAYMENT_INFORMATION.equals(section.getSectionId()) && asset.getAssetPayments().size() > CamsConstants.Asset.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY) {
                // Hide the payment section if there are more then CamsConstants.ASSET_MAXIMUM_NUMBER_OF_PAYMENT_DISPLAY
                // section.setHidden(true);
                sectionToRemove = section;
            }
        }

        if (sectionToRemove != null) {
            sections.remove(sectionToRemove);
        }
        return sections;
    }

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (CamsPropertyConstants.Asset.ORGANIZATION_CODE.equals(attributeName) && businessObject instanceof Asset) {
            Asset asset = (Asset) businessObject;
            Properties parameters = new Properties();
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
            parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Organization.class.getName());
            parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, asset.getOrganizationOwnerAccount().getChartOfAccountsCode());
            parameters.put(KFSPropertyConstants.ORGANIZATION_CODE, asset.getOrganizationOwnerAccount().getOrganizationCode());

            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, asset.getOrganizationOwnerAccount().getChartOfAccountsCode());
            fieldList.put(KFSPropertyConstants.ORGANIZATION_CODE, asset.getOrganizationOwnerAccount().getOrganizationCode());
            return getHyperLink(Organization.class, fieldList, UrlFactory.parameterizeUrl(KRADConstants.INQUIRY_ACTION, parameters));
        }
        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }
}
