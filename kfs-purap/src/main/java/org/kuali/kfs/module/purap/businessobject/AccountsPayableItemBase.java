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
package org.kuali.kfs.module.purap.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAssetTransactionType;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.math.BigDecimal;

/**
 * Base class for Accounts Payable Item Business Objects.
 */
public abstract class AccountsPayableItemBase extends PurApItemBase implements AccountsPayableItem {
    private KualiDecimal extendedPrice;
    private String capitalAssetTransactionTypeCode;
    private CapitalAssetManagementAssetTransactionType capitalAssetTransactionType;

    /**
     * Method defaults to {@link #isConsideredEnteredWithZero()}
     *
     * @see org.kuali.module.purap.bo.PurchasingApItem#isConsideredEntered()
     */
    public boolean isConsideredEntered() {
        return isConsideredEnteredWithZero();
    }

    public boolean isEligibleDisplay() {
        return isConsideredEnteredWithZero();
    }

    public boolean isConsideredEnteredWithZero() {
        return isConsideredEntered(true);
    }

    public boolean isConsideredEnteredWithoutZero() {
        return isConsideredEntered(false);
    }

    /**
     * This method is used to determine whether an item has been entered that is we are satisfied there's enough info to continue
     * processing that particular item. It is currently used by the rules class to determine when it's necessary to run rules on
     * items (so that lines processors don't touch won't be validated) and to determine when to show items (in combination with the
     * full entry mode)
     *
     * @param allowsZero if this is true zero will be considered the same as null.
     * @return true if the item is considered entered false otherwise
     */
    private boolean isConsideredEntered(boolean allowsZero) {
        if (getItemType().isLineItemIndicator()) {
            if ((getItemType().isQuantityBasedGeneralLedgerIndicator())) {
                if ((ObjectUtils.isNull(getItemQuantity())) && (ObjectUtils.isNull(getExtendedPrice()) || (allowsZero && getExtendedPrice().isZero()))) {
                    return false;
                }
            } else {
                if (ObjectUtils.isNull(getExtendedPrice()) || (allowsZero && getExtendedPrice().isZero())) {
                    return false;
                }
            }
        } else {
            if ((ObjectUtils.isNull(getItemUnitPrice()) || (allowsZero && this.getItemUnitPrice().compareTo(new BigDecimal(0)) == 0)) && (StringUtils.isBlank(getItemDescription()))) {
                return false;
            }
        }

        return true;
    }

    public boolean isNonZeroAmount() {
        return PurApItemUtils.isNonZeroExtended(this);
    }

    /**
     * Gets the extendedPrice attribute. this override is necessary because extended price needs to be set based on the unit price
     * for below the line(without this it would always be empty)
     *
     * @return Returns the extendedPrice.
     */
    @Override
    public KualiDecimal getExtendedPrice() {
        if (ObjectUtils.isNotNull(this.getItemUnitPrice()) && ObjectUtils.isNotNull(this.getItemType()) && this.getItemType().isAmountBasedGeneralLedgerIndicator()) {
            if (ObjectUtils.isNotNull(this.getItemUnitPrice())) {
                extendedPrice = new KualiDecimal(this.getItemUnitPrice().toString());
            } else {
                extendedPrice = null;
            }
        } else if (ObjectUtils.isNull(this.getItemUnitPrice()) && ObjectUtils.isNotNull(this.getItemType()) &&
            this.getItemType().isAmountBasedGeneralLedgerIndicator() &&
            this.getItemType().isAdditionalChargeIndicator()) { // This additional charges check is needed since non qty items also dont have unit price
            // extendedPrice should be null if the unit price is null
            extendedPrice = null;
        }
        return extendedPrice;
    }

    public void setExtendedPrice(KualiDecimal extendedPrice) {
        this.extendedPrice = extendedPrice;
    }

    /**
     * Override the method in PurApItemBase so that if the item is
     * not eligible to be displayed in the account summary tab,
     * which is if the item's extended price is null or zero,
     * we'll return null and the item won't be added
     * to the list of account summary.
     *
     * @see org.kuali.kfs.module.purap.businessobject.PurApItemBase#getSummaryItem()
     */
    @Override
    public PurApSummaryItem getSummaryItem() {
        if (extendedPrice == null || extendedPrice.isZero()) {
            return null;
        } else {
            return super.getSummaryItem();
        }
    }


    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public CapitalAssetManagementAssetTransactionType getCapitalAssetTransactionType() {
        return capitalAssetTransactionType = (CapitalAssetManagementAssetTransactionType) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CapitalAssetManagementAssetTransactionType.class).retrieveExternalizableBusinessObjectIfNecessary(this, capitalAssetTransactionType, PurapPropertyConstants.ITEM_CAPITAL_ASSET_TRANSACTION_TYPE);
    }

    public void setItemDescription(String itemDescription) {
        if ((itemDescription != null) && (itemDescription.length() > 100)) {
            super.setItemDescription(itemDescription.substring(0, 100));
        } else {
            super.setItemDescription(itemDescription);
        }

    }

}
