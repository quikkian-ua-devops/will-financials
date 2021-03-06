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
package org.kuali.kfs.module.cam.document.dataaccess.impl;

import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Transactional
public class MockDepreciationBatchDao implements DepreciationBatchDao {
    private DepreciationBatchDao impl;
    private List<String> assetPaymentsStr = new ArrayList<>();

    @Override
    public void updateAssetPayments(List<AssetPaymentInfo> assetPayments, Integer fiscalMonth) {
        impl.updateAssetPayments(assetPayments, fiscalMonth);
        for (AssetPaymentInfo assetPaymentInfo : assetPayments) {
            String t = fiscalMonth + "-" + assetPaymentInfo.getCapitalAssetNumber() + "-" + assetPaymentInfo.getPaymentSequenceNumber() + "-" + assetPaymentInfo.getTransactionAmount().bigDecimalValue();
            this.assetPaymentsStr.add(t);
        }
    }

    @Override
    public Integer getFullyDepreciatedAssetCount() {
        return impl.getFullyDepreciatedAssetCount();
    }

    @Override
    public Collection<AssetPaymentInfo> getListOfDepreciableAssetPaymentInfo(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate) {
        return impl.getListOfDepreciableAssetPaymentInfo(fiscalYear, fiscalMonth, depreciationDate);
    }

    @Override
    public void resetPeriodValuesWhenFirstFiscalPeriod(Integer fiscalPeriod) throws Exception {
        impl.resetPeriodValuesWhenFirstFiscalPeriod(fiscalPeriod);
    }

    @Override
    public void savePendingGLEntries(List<GeneralLedgerPendingEntry> glPendingEntries) {
        impl.savePendingGLEntries(glPendingEntries);

    }

    @Override
    public Map<Long, KualiDecimal> getPrimaryDepreciationBaseAmountForSV() {
        return impl.getPrimaryDepreciationBaseAmountForSV();
    }

    /**
     * Gets the assetPaymentsStr attribute.
     *
     * @return Returns the assetPaymentsStr.
     */
    public List<String> getAssetPaymentsStr() {
        return assetPaymentsStr;
    }

    /**
     * Sets the assetPaymentsStr attribute value.
     *
     * @param assetPaymentsStr The assetPaymentsStr to set.
     */
    public void setAssetPaymentsStr(List<String> assetPaymentsStr) {
        this.assetPaymentsStr = assetPaymentsStr;
    }

    /**
     * Gets the impl attribute.
     *
     * @return Returns the impl.
     */
    public DepreciationBatchDao getImpl() {
        return impl;
    }

    /**
     * Sets the impl attribute value.
     *
     * @param impl The impl to set.
     */
    public void setImpl(DepreciationBatchDao impl) {
        this.impl = impl;
    }

    @Override
    public Object[] getAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate, boolean includePending) {
        return impl.getAssetAndPaymentCount(fiscalYear, fiscalMonth, depreciationDate, includePending);
    }

    @Override
    public Object[] getFederallyOwnedAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate) {
        return impl.getFederallyOwnedAssetAndPaymentCount(fiscalYear, fiscalMonth, depreciationDate);
    }

    @Override
    public Integer getRetireDocLockedAssetCount() {
        return impl.getRetireDocLockedAssetCount();
    }

    @Override
    public Integer getTransferDocLockedAssetCount() {
        return impl.getTransferDocLockedAssetCount();
    }

    @Override
    public Set<Long> getLockedAssets() {
        return impl.getLockedAssets();
    }

    @Override
    public List<Map<String, Object>> getAssetsByDepreciationConvention(Date lastFiscalYearDate, List<String> movableEquipmentObjectSubTypes, String depreciationConventionCd) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateAssetInServiceAndDepreciationDate(List<String> selectedAssets, Date inServiceDate, Date depreciationDate) {
        // TODO Auto-generated method stub

    }

    @Override
    public Set<Long> getAssetsWithNoDepreciation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Long> getTransferDocPendingAssets() {
        // TODO Auto-generated method stub
        return null;
    }
}
