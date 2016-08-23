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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;

public class PurchasingAccountsPayableBelowTheLineValuesValidation extends GenericValidation {

    private DataDictionaryService dataDictionaryService;
    private ParameterService parameterService;
    private PurApItem itemForValidation;

    /**
     * Performs validations for below the line items. If the unit price is zero, and the system parameter indicates that the item
     * should not allow zero, then the validation fails. If the unit price is positive and the system parameter indicates that the
     * item should not allow positive values, then the validation fails. If the unit price is negative and the system parameter
     * indicates that the item should not allow negative values, then the validation fails. If the unit price is entered and is not
     * zero and the item description is empty and the system parameter indicates that the item requires user to enter description,
     * then the validation fails.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) event.getDocument();
        String documentType = dataDictionaryService.getDocumentTypeNameByClass(purapDocument.getClass());

        try {
            if (ObjectUtils.isNotNull(itemForValidation.getItemUnitPrice()) && (new KualiDecimal(itemForValidation.getItemUnitPrice())).isZero()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_ZERO) && !/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_ZERO, itemForValidation.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, itemForValidation.getItemType().getItemTypeDescription(), "zero");
                }
            }
            else if (ObjectUtils.isNotNull(itemForValidation.getItemUnitPrice()) && (new KualiDecimal(itemForValidation.getItemUnitPrice())).isPositive()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_POSITIVE) && !/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_POSITIVE, itemForValidation.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, itemForValidation.getItemType().getItemTypeDescription(), "positive");
                }
            }
            else if (ObjectUtils.isNotNull(itemForValidation.getItemUnitPrice()) && (new KualiDecimal(itemForValidation.getItemUnitPrice())).isNegative()) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_NEGATIVE) && !/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_NEGATIVE, itemForValidation.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, itemForValidation.getItemType().getItemTypeDescription(), "negative");
                }
            }
            if (ObjectUtils.isNotNull(itemForValidation.getItemUnitPrice()) && (new KualiDecimal(itemForValidation.getItemUnitPrice())).isNonZero() && StringUtils.isEmpty(itemForValidation.getItemDescription())) {
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION) && /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_REQUIRES_USER_ENTERED_DESCRIPTION, itemForValidation.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.ITEM_DESCRIPTION, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, "The item description of " + itemForValidation.getItemType().getItemTypeDescription(), "empty");
                }
            }

            //now check total amount, if positive check if they should really be negative
            if (ObjectUtils.isNotNull(itemForValidation.getTotalAmount()) && itemForValidation.getTotalAmount().isPositive()){
                if (parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_POSITIVE) && !/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), PurapConstants.ITEM_ALLOWS_POSITIVE, itemForValidation.getItemTypeCode()).evaluationSucceeds()) {
                    valid = false;
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.TOTAL_AMOUNT, PurapKeyConstants.ERROR_ITEM_BELOW_THE_LINE, itemForValidation.getItemType().getItemTypeDescription() + " Total Amount", "positive");
                }
            }
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("The valideBelowTheLineValues of PurchasingAccountsPayableDocumentRuleBase was unable to resolve a document type class: " + PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType), e);
        }

        return valid;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
