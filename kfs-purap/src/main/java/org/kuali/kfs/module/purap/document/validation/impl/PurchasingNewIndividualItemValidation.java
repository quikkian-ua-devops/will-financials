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
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.CommodityCode;

import java.util.HashMap;
import java.util.Map;

public class PurchasingNewIndividualItemValidation extends PurchasingAccountsPayableNewIndividualItemValidation {

    private BusinessObjectService businessObjectService;
    private CapitalAssetManagementModuleService capitalAssetManagementModuleService;
    private PurchasingUnitOfMeasureValidation unitOfMeasureValidation;
    private PurchasingItemUnitPriceValidation itemUnitPriceValidation;
    private PurchasingItemDescriptionValidation itemDescriptionValidation;
    private PurchasingItemQuantityValidation itemQuantityValidation;
    private PurchasingBelowTheLineItemNoUnitCostValidation belowTheLineItemNoUnitCostValidation;

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        valid &= super.validate(event);
        String recurringPaymentTypeCode = ((PurchasingDocument) event.getDocument()).getRecurringPaymentTypeCode();
        //Capital asset validations are only done on line items (not additional charge items).
        if (!getItemForValidation().getItemType().isAdditionalChargeIndicator()) {
            valid &= capitalAssetManagementModuleService.validateItemCapitalAssetWithErrors(recurringPaymentTypeCode, getItemForValidation(), false);
        }
        unitOfMeasureValidation.setItemForValidation(getItemForValidation());
        valid &= unitOfMeasureValidation.validate(event);

        if (getItemForValidation().getItemType().isLineItemIndicator()) {
            itemUnitPriceValidation.setItemForValidation(getItemForValidation());
            valid &= itemUnitPriceValidation.validate(event);

            itemDescriptionValidation.setItemForValidation(getItemForValidation());
            valid &= itemDescriptionValidation.validate(event);

            itemQuantityValidation.setItemForValidation(getItemForValidation());
            valid &= itemQuantityValidation.validate(event);

            valid &= validateCommodityCodes(getItemForValidation(), commodityCodeIsRequired());
        } else {
            // No accounts can be entered on below-the-line items that have no unit cost.
            belowTheLineItemNoUnitCostValidation.setItemForValidation(getItemForValidation());
            valid &= belowTheLineItemNoUnitCostValidation.validate(event);
        }
        return valid;
    }

    protected boolean commodityCodeIsRequired() {
        return false;
    }

    /**
     * Validates that the document contains at least one item.
     *
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the document does not contain at least one item.
     */
    public boolean validateContainsAtLeastOneItem(PurchasingDocument purDocument) {
        boolean valid = false;
        for (PurApItem item : purDocument.getItems()) {
            if (!((PurchasingItemBase) item).isEmpty() && item.getItemType().isLineItemIndicator()) {

                return true;
            }
        }
        String documentType = getDocumentTypeLabel(purDocument.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());

        if (!valid) {
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_REQUIRED, documentType);
        }

        return valid;
    }

    /**
     * Validates whether the commodity code existed on the item, and if existed, whether the
     * commodity code on the item existed in the database, and if so, whether the commodity
     * code is active. Display error if any of these 3 conditions are not met.
     *
     * @param item The PurApItem containing the commodity code to be validated.
     * @return boolean false if the validation fails and true otherwise.
     */
    protected boolean validateCommodityCodes(PurApItem item, boolean commodityCodeRequired) {
        boolean valid = true;
        String identifierString = item.getItemIdentifierString();
        PurchasingItemBase purItem = (PurchasingItemBase) item;

        String errorPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + (item.getItemLineNumber() - 1) + "]." + PurapPropertyConstants.ITEM_COMMODITY_CODE;
        //This validation is only needed if the commodityCodeRequired system parameter is true
        if (commodityCodeRequired && StringUtils.isBlank(purItem.getPurchasingCommodityCode())) {
            //This is the case where the commodity code is required but the item does not currently contain the commodity code.
            valid = false;
            String attributeLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(CommodityCode.class.getName()).
                getAttributeDefinition(PurapPropertyConstants.ITEM_COMMODITY_CODE).getLabel();

            GlobalVariables.getMessageMap().putError(errorPrefix, KFSKeyConstants.ERROR_REQUIRED, attributeLabel + " in " + identifierString);
        } else if (StringUtils.isNotBlank(purItem.getPurchasingCommodityCode())) {
            //Find out whether the commodity code has existed in the database
            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(PurapPropertyConstants.ITEM_COMMODITY_CODE, purItem.getPurchasingCommodityCode());
            if (businessObjectService.countMatching(CommodityCode.class, fieldValues) != 1) {
                //This is the case where the commodity code on the item does not exist in the database.
                valid = false;
                GlobalVariables.getMessageMap().putError(errorPrefix, PurapKeyConstants.PUR_COMMODITY_CODE_INVALID, " in " + identifierString);
            } else {
                valid &= validateThatCommodityCodeIsActive(item);
            }
        }

        return valid;
    }

    protected boolean validateThatCommodityCodeIsActive(PurApItem item) {
        item.refreshReferenceObject(PurapPropertyConstants.COMMODITY_CODE);
        if (!((PurchasingItemBase) item).getCommodityCode().isActive()) {
            //This is the case where the commodity code on the item is not active.
            String errorPrefix = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[" + (item.getItemLineNumber() - 1) + "]." + PurapPropertyConstants.ITEM_COMMODITY_CODE;
            GlobalVariables.getMessageMap().putError(errorPrefix, PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE, " in " + item.getItemIdentifierString());
            return false;
        }
        return true;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return capitalAssetManagementModuleService;
    }

    public void setCapitalAssetManagementModuleService(CapitalAssetManagementModuleService capitalAssetManagementModuleService) {
        this.capitalAssetManagementModuleService = capitalAssetManagementModuleService;
    }

    public PurchasingUnitOfMeasureValidation getUnitOfMeasureValidation() {
        return unitOfMeasureValidation;
    }

    public void setUnitOfMeasureValidation(PurchasingUnitOfMeasureValidation unitOfMeasureValidation) {
        this.unitOfMeasureValidation = unitOfMeasureValidation;
    }

    public PurchasingItemUnitPriceValidation getItemUnitPriceValidation() {
        return itemUnitPriceValidation;
    }

    public void setItemUnitPriceValidation(PurchasingItemUnitPriceValidation itemUnitPriceValidation) {
        this.itemUnitPriceValidation = itemUnitPriceValidation;
    }

    public PurchasingItemDescriptionValidation getItemDescriptionValidation() {
        return itemDescriptionValidation;
    }

    public void setItemDescriptionValidation(PurchasingItemDescriptionValidation itemDescriptionValidation) {
        this.itemDescriptionValidation = itemDescriptionValidation;
    }

    public PurchasingItemQuantityValidation getItemQuantityValidation() {
        return itemQuantityValidation;
    }

    public void setItemQuantityValidation(PurchasingItemQuantityValidation itemQuantityValidation) {
        this.itemQuantityValidation = itemQuantityValidation;
    }

    public PurchasingBelowTheLineItemNoUnitCostValidation getBelowTheLineItemNoUnitCostValidation() {
        return belowTheLineItemNoUnitCostValidation;
    }

    public void setBelowTheLineItemNoUnitCostValidation(PurchasingBelowTheLineItemNoUnitCostValidation belowTheLineItemNoUnitCostValidation) {
        this.belowTheLineItemNoUnitCostValidation = belowTheLineItemNoUnitCostValidation;
    }


}
