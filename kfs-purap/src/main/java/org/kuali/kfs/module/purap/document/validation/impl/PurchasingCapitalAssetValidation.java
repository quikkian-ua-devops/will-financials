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
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetSystem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.util.RiceKeyConstants;

public class PurchasingCapitalAssetValidation extends GenericValidation {
    CapitalAssetManagementModuleService capitalAssetManagementModuleService;
    PurchasingService purchasingService;
    protected static String ERROR_PATH_PREFIX_FOR_IND_SYSTEM = "document.purchasingCapitalAssetItems[";
    protected static String ERROR_PATH_SUFFIX_FOR_IND_SYSTEM = "].purchasingCapitalAssetSystem";
    protected static String ERROR_PATH_PREFIX_FOR_ONE_SYSTEM = "document.purchasingCapitalAssetSystems[0]";

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        GlobalVariables.getMessageMap().clearErrorPath();
        boolean valid = true;
        PurchasingDocument purchasingDocument = (PurchasingDocument) event.getDocument();

        boolean requiredByObjectSubType = !capitalAssetManagementModuleService.validatePurchasingObjectSubType(purchasingDocument);
        boolean requiredByChart = !capitalAssetManagementModuleService.validateAllFieldRequirementsByChart(purchasingDocument);
        boolean capitalAssetRequired = requiredByObjectSubType && requiredByChart;

        if (capitalAssetRequired) {
            // if capital asset required, check to see if the capital asset data are setup
            String typeCode = purchasingDocument.getCapitalAssetSystemTypeCode();
            if (StringUtils.isBlank(typeCode) || StringUtils.isBlank(purchasingDocument.getCapitalAssetSystemStateCode()) ||
                purchasingDocument.getPurchasingCapitalAssetSystems() == null || purchasingDocument.getPurchasingCapitalAssetItems() == null) {
                valid = false;
            } else if ((typeCode.equals(PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM) || typeCode.equals(PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS)) &&
                purchasingDocument.getPurchasingCapitalAssetSystems().size() == 0) {
                valid = false;
            }
            /* TODO
             * either complete the following with checking that capital asset items are correctly setup, or replace this whole part (and above)
             * with checking on a flag that indicates whether select/update capital asset has been done since last item changes
             */
            else if (purchasingDocument.getPurchasingCapitalAssetItems().isEmpty()) {
                valid = false;
            } else {
                int expectedCapAssetItems = 0;
                for (PurApItem purapItem : purchasingDocument.getItems()) {
                    if (purapItem.getItemType().isLineItemIndicator()) {
                        if (capitalAssetManagementModuleService.doesItemNeedCapitalAsset(purapItem.getItemTypeCode(), purapItem.getSourceAccountingLines())) {
                            expectedCapAssetItems++;
                        }
                    }
                }
                if (purchasingDocument.getPurchasingCapitalAssetItems().size() != expectedCapAssetItems) {
                    valid = false;
                }
            }
            if (!valid) {
                GlobalVariables.getMessageMap().putError("newPurchasingItemCapitalAssetLine", PurapKeyConstants.ERROR_CAPITAL_ASSET_REQD_FOR_PUR_OBJ_SUB_TYPE);
                return valid;
            }
        } else {
            // if capital asset not required, reset system type and state code in case they are filled in
            // if capital asset items are empty, then set sytem type code and system state code to null
            // fix to jira KFSMI-5146
            if (purchasingDocument.getPurchasingCapitalAssetItems().isEmpty()) {
                purchasingDocument.setCapitalAssetSystemTypeCode(null);
                purchasingDocument.setCapitalAssetSystemStateCode(null);
            }
        }

        // We only need to do capital asset validations if the capital asset system type is not blank.
        if (StringUtils.isNotBlank(purchasingDocument.getCapitalAssetSystemTypeCode())) {
            valid &= capitalAssetManagementModuleService.validatePurchasingData(purchasingDocument);

            // FIXME hjs move this to cab module service
            // validate complete location addresses
            if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
                for (CapitalAssetSystem system : purchasingDocument.getPurchasingCapitalAssetSystems()) {
                    for (CapitalAssetLocation location : system.getCapitalAssetLocations()) {
                        valid &= purchasingService.checkCapitalAssetLocation(location);
                    }
                }
            } else if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM)) {
                CapitalAssetSystem system = purchasingDocument.getPurchasingCapitalAssetSystems().get(0);
                for (CapitalAssetLocation location : system.getCapitalAssetLocations()) {
                    valid &= purchasingService.checkCapitalAssetLocation(location);
                }
            }

            // Validate asset type code if entered by user.
            valid &= validateAssetTypeExistence(purchasingDocument);
        }


        return valid;
    }

    /**
     * Validate user input asset type code.
     *
     * @param purchasingDocument
     * @return
     */
    protected boolean validateAssetTypeExistence(PurchasingDocument purchasingDocument) {
        boolean valid = true;
        // validate for Individual system
        if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL)) {
            int i = 0;
            for (PurchasingCapitalAssetItem capitalAssetItem : purchasingDocument.getPurchasingCapitalAssetItems()) {
                if (ObjectUtils.isNotNull(capitalAssetItem) && ObjectUtils.isNotNull(capitalAssetItem.getPurchasingCapitalAssetSystem())) {
                    String assetTypeCode = capitalAssetItem.getPurchasingCapitalAssetSystem().getCapitalAssetTypeCode();
                    if (StringUtils.isNotBlank(assetTypeCode) && !capitalAssetManagementModuleService.isAssetTypeExisting(assetTypeCode)) {
                        valid = false;
                        String errorPath = ERROR_PATH_PREFIX_FOR_IND_SYSTEM + new Integer(i).toString() + ERROR_PATH_SUFFIX_FOR_IND_SYSTEM;
                        addAssetTypeErrorWithFullErrorPath(errorPath);
                    }
                }
                i++;
            }
        } else if (purchasingDocument.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.ONE_SYSTEM)) {
            // validate for One system
            if (ObjectUtils.isNotNull(purchasingDocument.getPurchasingCapitalAssetSystems())) {
                CapitalAssetSystem system = purchasingDocument.getPurchasingCapitalAssetSystems().get(0);
                if (ObjectUtils.isNotNull(system) && StringUtils.isNotBlank(system.getCapitalAssetTypeCode()) && !capitalAssetManagementModuleService.isAssetTypeExisting(system.getCapitalAssetTypeCode())) {
                    valid = false;
                    String errorPath = ERROR_PATH_PREFIX_FOR_ONE_SYSTEM;
                    addAssetTypeErrorWithFullErrorPath(errorPath);
                }
            }
        }
        // Validate for Multiple system is ignored since currently it's not supported to enter.
        return valid;
    }

    /**
     * Add asset type error to the global message map.
     *
     * @param errorPath
     */
    protected void addAssetTypeErrorWithFullErrorPath(String errorPath) {
        GlobalVariables.getMessageMap().addToErrorPath(errorPath);
        String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(RequisitionCapitalAssetSystem.class.getName()).getAttributeDefinition(PurapPropertyConstants.CAPITAL_ASSET_TYPE_CODE).getLabel();
        GlobalVariables.getMessageMap().putError(PurapPropertyConstants.CAPITAL_ASSET_TYPE_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
        GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
    }

    public CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return capitalAssetManagementModuleService;
    }

    public void setCapitalAssetManagementModuleService(CapitalAssetManagementModuleService capitalAssetManagementModuleService) {
        this.capitalAssetManagementModuleService = capitalAssetManagementModuleService;
    }

    public PurchasingService getPurchasingService() {
        return purchasingService;
    }

    public void setPurchasingService(PurchasingService purchasingService) {
        this.purchasingService = purchasingService;
    }

}
