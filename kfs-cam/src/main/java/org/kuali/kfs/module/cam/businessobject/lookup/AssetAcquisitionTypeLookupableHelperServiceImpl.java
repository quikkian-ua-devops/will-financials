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
package org.kuali.kfs.module.cam.businessobject.lookup;

import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class overrides the getReturnUrl, setFieldConversions and getActionUrls for {@link OrganizationRoutingModelName}
 */
public class AssetAcquisitionTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected boolean initializingAssetGlobal = true;


    /**
     * Overrides the base implementation to add in new parameters to the return url
     * <ul>
     * <li>{@link KFSConstants.DISPATCH_REQUEST_PARAMETER}</li>
     * <li>{@link KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE}</li>
     * <li>{@link KFSConstants.OVERRIDE_KEYS}</li>
     * </ul>
     * {@link KFSConstants.DISPATCH_REQUEST_PARAMETER}
     *
     * @see org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnUrl(org.kuali.rice.krad.bo.BusinessObject,
     * java.util.Map, java.lang.String)
     */
    @Override
    public HtmlData getReturnUrl(BusinessObject businessObject, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        AssetAcquisitionType assetAcquisitionType = (AssetAcquisitionType) businessObject;
        AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);
        if (initializingAssetGlobal && !assetAcquisitionType.isActive()) {
            // no return URL if we are initializing asset global and the record is inactive
            return getEmptyAnchorHtmlData();

        } else if (assetGlobalService.getNewAcquisitionTypeCode().equalsIgnoreCase(assetAcquisitionType.getAcquisitionTypeCode())) {
            // no return if the user is not authorized to initiate 'New' acquisition type.
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentDictionaryService.class).getDocumentAuthorizer(CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL);
            boolean isAuthorized = documentAuthorizer.isAuthorized(businessObject, CamsConstants.CAM_MODULE_CODE, CamsConstants.PermissionNames.USE_ACQUISITION_TYPE_NEW, GlobalVariables.getUserSession().getPerson().getPrincipalId());

            if (!isAuthorized) {
                return getEmptyAnchorHtmlData();
            }
        }
        // return URL
        Properties parameters = generateUrlParameters(businessObject, lookupForm, returnKeys);
        return getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);

    }

    protected Properties generateUrlParameters(BusinessObject businessObject, LookupForm lookupForm, List returnKeys) {
        AssetAcquisitionType assetAcquisitionType = (AssetAcquisitionType) businessObject;
        Properties parameters = getParameters(businessObject, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName(), returnKeys);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, AssetGlobal.class.getName());
        parameters.put(KFSConstants.OVERRIDE_KEYS, CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE);
        parameters.put(KFSConstants.REFRESH_CALLER, CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE_CODE + "::" + assetAcquisitionType.getAcquisitionTypeCode());
        setBackLocation(KFSConstants.MAINTENANCE_ACTION);

        return parameters;
    }

    /**
     * A couple of acquisition type code won't be listed in Asset Global and we need to apply this constraint from lookup. They are:
     * P-Pre-asset tagging - this acquisition type code is assigned in CAB to assets that are created with a pre-tagging record.
     * C-Fabrication - this acquisition type is assigned to assets created via the fabrication document.
     *
     * @see org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<? extends BusinessObject> searchResults = super.getSearchResults(fieldValues);
        if (searchResults == null || searchResults.isEmpty() || !initializingAssetGlobal) {
            return searchResults;
        }
        Integer searchResultsLimit = LookupUtils.getSearchResultsLimit(AssetAcquisitionType.class);
        Long matchingResultsCount = null;
        List<AssetAcquisitionType> newList = new ArrayList<AssetAcquisitionType>();
        for (BusinessObject businessObject : searchResults) {
            AssetAcquisitionType acquisition = (AssetAcquisitionType) businessObject;
            if (ObjectUtils.isNotNull(acquisition) && !CamsConstants.Asset.ACQUISITION_TYPE_CODE_C.equalsIgnoreCase(acquisition.getAcquisitionTypeCode()) && !CamsConstants.AssetGlobal.PRE_TAGGING_ACQUISITION_TYPE_CODE.equalsIgnoreCase(acquisition.getAcquisitionTypeCode())) {
                newList.add(acquisition);
            }

        }
        matchingResultsCount = Long.valueOf(newList.size());
        if (matchingResultsCount.intValue() <= searchResultsLimit.intValue()) {
            matchingResultsCount = new Long(0);
        }
        return new CollectionIncomplete(newList, matchingResultsCount);
    }

    /**
     * Overrides base implementation to determine whether or not we are dealing with looking up the model or editing it
     *
     * @see org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl#setFieldConversions(java.util.Map)
     */
    @Override
    public void setFieldConversions(Map fieldConversions) {
        super.setFieldConversions(fieldConversions);
        if (fieldConversions == null || fieldConversions.size() == 0) {
            initializingAssetGlobal = false;
        }
    }

    /**
     * Overrides base implementation to remove the action urls if we are initializing the asset retirement reason
     *
     * @see org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     * List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        if (!initializingAssetGlobal) {
            return super.getCustomActionUrls(businessObject, pkNames);
        } else {
            return super.getEmptyActionUrls();
        }
    }
}
