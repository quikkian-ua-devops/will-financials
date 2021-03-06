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
package org.kuali.kfs.module.cam.document.gl;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlpeSourceDetail;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class AssetGlobalGeneralLedgerPendingEntrySource extends CamsGeneralLedgerPendingEntrySourceBase {

    public AssetGlobalGeneralLedgerPendingEntrySource(FinancialSystemDocumentHeader documentHeader) {
        super(documentHeader);
    }

    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        boolean debit = false;
        AssetGlpeSourceDetail assetPostable = (AssetGlpeSourceDetail) postable;
        KualiDecimal amount = assetPostable.getAmount();

        if ((assetPostable.isCapitalization() && amount.isPositive()) || (assetPostable.isCapitalizationOffset() && amount.isNegative()) || (assetPostable.isPayment() && amount.isPositive()) || (assetPostable.isPaymentOffset() && amount.isNegative())) {
            debit = true;
        }
        return debit;
    }

    public String getFinancialDocumentTypeCode() {
        return CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL;
    }

}
