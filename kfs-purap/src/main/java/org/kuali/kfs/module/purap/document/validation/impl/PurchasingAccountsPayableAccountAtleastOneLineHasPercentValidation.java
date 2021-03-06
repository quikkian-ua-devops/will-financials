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

import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

import java.math.BigDecimal;

public class PurchasingAccountsPayableAccountAtleastOneLineHasPercentValidation extends GenericValidation {

    private PurApItem itemForValidation;

    /**
     * Verifies at least one account has percent distribution to indicate how an
     * overage is to be funded.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        boolean percentExists = false;

        PurchasingAccountsPayableDocumentBase purapDoc = (PurchasingAccountsPayableDocumentBase) event.getDocument();

        if (PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(purapDoc.getAccountDistributionMethod())) {
            for (PurApAccountingLine account : itemForValidation.getSourceAccountingLines()) {
                if (ObjectUtils.isNotNull(account.getAccountLinePercent())) {
                    //there should be atleast one accounting line where percent should be > 0.00
                    if (account.getAccountLinePercent().compareTo(BigDecimal.ZERO) == 1) {
                        percentExists = true;
                    }
                }
            }

            if (!percentExists) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNTING_LINE_ATLEAST_ONE_PERCENT_MISSING);
                return false;
            }
        }

        return valid;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }
}
