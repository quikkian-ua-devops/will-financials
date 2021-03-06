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
package org.kuali.kfs.module.cam.document.dataaccess;

import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface declaring DAO methods required by CAMS depreciation batch job
 */
public interface DepreciationBatchDao {

    /**
     * Updates payments as batch, columns updated are accumulated depreciation amount and current period column decided by fiscal
     * period
     *
     * @param assetPayments Batch of asset payments
     * @param fiscalPeriod  Current fiscal period
     */
    void updateAssetPayments(List<AssetPaymentInfo> assetPayments, Integer fiscalPeriod);

    /**
     * Sum all period column and set as previous year value and then reset period columns with zero dollar
     *
     * @param fiscalMonth Fiscal period
     * @throws Exception
     */
    void resetPeriodValuesWhenFirstFiscalPeriod(Integer fiscalPeriod) throws Exception;


    /**
     * Saves a batch of GL Pending entries
     *
     * @param glPendingEntries GLPE list to be saved
     */
    void savePendingGLEntries(final List<GeneralLedgerPendingEntry> glPendingEntries);

    /**
     * Gets the list of depreciable asset payment list and corresponding details
     *
     * @param fiscalYear       Fiscal year
     * @param fiscalMonth      Fiscal period
     * @param depreciationDate Depreciation Date
     * @return List found matching depreciation criteria
     */
    Collection<AssetPaymentInfo> getListOfDepreciableAssetPaymentInfo(Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate);

    /**
     * Counts the number of assets which has (SUM(Primary Depreciation Amount - Accumulated Depreciation) - Salvage Amount) is zero
     *
     * @return count of assets matching condition
     */
    Integer getFullyDepreciatedAssetCount();

    /**
     * Primary depreciation base amount for assets with Salvage Value depreciation method code.
     *
     * @return Map
     */
    Map<Long, KualiDecimal> getPrimaryDepreciationBaseAmountForSV();

    /**
     * Retrieves list of asset IDs that have no depreciation.
     *
     * @return
     */
    Set<Long> getAssetsWithNoDepreciation();

    /**
     * Retrieves asset and asset payment count eligible for depreciation
     *
     * @param fiscalYear
     * @param fiscalMonth
     * @param depreciationDate
     * @param inincludePending
     * @return
     */
    Object[] getAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, final Calendar depreciationDate, boolean includePending);

    /**
     * This method...
     *
     * @param fiscalYear
     * @param fiscalMonth
     * @param depreciationDate
     * @return
     */
    Object[] getFederallyOwnedAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, final Calendar depreciationDate);

    /**
     * Transfer document locked count
     *
     * @return
     */
    Integer getTransferDocLockedAssetCount();

    /**
     * Assets with pending transfer docs
     *
     * @return
     */
    Set<Long> getTransferDocPendingAssets();

    /**
     * Retirement document locked count
     *
     * @return
     */
    Integer getRetireDocLockedAssetCount();

    /**
     * Returns the list of locked asset by pending transfer and retirement documents
     *
     * @return
     */
    Set<Long> getLockedAssets();

    /**
     * Depreciation (end of year) Period 13 assets incorrect depreciation start date
     * <P> Get assets in accordance with,
     * <ul>
     * <li>Asset type has valid depreciation life time limit;
     * <li>Asset is created in period 13 of current fiscal year;
     * <li>Asset has depreciation convention restricted by parameter
     * <li>Asset are movable assets. Movable assets are defined by system parameter MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES
     * </ul>
     *
     * @param lastFiscalYearDate
     * @param movableEquipmentObjectSubTypes
     * @param depreciationConventionCd
     * @return
     */
    List<Map<String, Object>> getAssetsByDepreciationConvention(Date lastFiscalYearDate, List<String> movableEquipmentObjectSubTypes, String depreciationConventionCd);

    /**
     * Depreciation (end of year) Period 13 assets incorrect depreciation start date
     * <P> Update asset in service date and depreciation date
     *
     * @param selectedAssets
     * @param inServiceDate
     * @param depreciationDate
     */
    void updateAssetInServiceAndDepreciationDate(List<String> selectedAssets, Date inServiceDate, Date depreciationDate);
}
