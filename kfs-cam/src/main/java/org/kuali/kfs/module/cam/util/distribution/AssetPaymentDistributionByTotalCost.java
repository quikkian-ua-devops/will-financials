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
package org.kuali.kfs.module.cam.util.distribution;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a calculator which will distribute the payment amounts by ratio. Inputs received are
 * <li>Asset Payment Details</li>
 * <li>Asset Details</li>
 * <li>total historical cost for asset</li>
 * <p>
 * It provides a table mapping of asset payment distributions key off of AssetPaymentDetail and
 * AssetPaymentAssetDetail
 * <p>
 * Logic is best explained as below
 * <li>Compute the asset ratio of amount to be distributed per asset</li>
 * <li>For each Asset Payment Details, create proportional asset payments base on the asset ratio</li>
 * <li>* Keep track of unallocated amount within each asset payment loop* <li>
 * <li>* For the last asset in each payment detail iteration, use the rest of unallocated amount</li>
 */
public class AssetPaymentDistributionByTotalCost extends AssetDistribution {


    private KualiDecimal totalHistoricalCost;

    private Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> paymentDistributionMap;

    /**
     * Constructor and instantiate the detai lists as empty
     */
    public AssetPaymentDistributionByTotalCost(AssetPaymentDocument doc) {
        super(doc);
        this.totalHistoricalCost = doc.getAssetsTotalHistoricalCost();

        //init method
        calculateAssetPaymentDistributions();
    }

    /**
     * Pre-calculate the asset payments base on AssetPaymentDetail(AccountSouceLines) and AssetPaymentAssetDetails
     * <p>
     * This will iterate by the AssetPaymentDetail as the outer iterator such that payment totals will match up by the AccountingSouceLines
     * in (GL).  The unallocated payment amount will be depleted per each AssetPaymentDetails
     * <p>
     * NOTE: reversing the iteration sequence will cause a discrepancy in the AssetPaymentDetail totals
     *
     * @param document
     * @param assetPaymentDetailLines
     * @param assetPaymentAssetDetails
     */
    private void calculateAssetPaymentDistributions() {

        Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> assetPaymentAssetDetailMap = new HashMap<String, Map<AssetPaymentAssetDetail, KualiDecimal>>();

        // calculate the asset payment percentage and store into a map
        Map<AssetPaymentAssetDetail, Double> assetPaymentsPercentage = new HashMap<AssetPaymentAssetDetail, Double>(doc.getAssetPaymentAssetDetail().size());

        for (AssetPaymentAssetDetail assetPaymentAssetDetail : doc.getAssetPaymentAssetDetail()) {
            // Doing the re-distribution of the cost based on the previous total cost of each asset compared with the total previous cost of the assets.
            // store the result in a temporary map
            assetPaymentsPercentage.put(assetPaymentAssetDetail, getAssetDetailPercentage(doc.getAssetPaymentAssetDetail().size(), new Double(totalHistoricalCost.toString()), assetPaymentAssetDetail));
        }

        // Start the iteration base from the AssetPaymentDetail - accountingSouceLines
        for (AssetPaymentDetail assetPaymentDetail : getAssetPaymentDetailLines()) {

            int paymentCount = doc.getAssetPaymentAssetDetail().size();
            KualiDecimal amount = KualiDecimal.ZERO;

            // Keep unallocated amount so it could be used for last payment amount for the asset (to avoid rounding issue)
            KualiDecimal unallocatedAmount = assetPaymentDetail.getAmount();

            Map<AssetPaymentAssetDetail, KualiDecimal> assetDetailMap = new HashMap<AssetPaymentAssetDetail, KualiDecimal>();
            for (AssetPaymentAssetDetail assetPaymentAssetDetail : doc.getAssetPaymentAssetDetail()) {
                // Doing the re-distribution of the cost based on the previous total cost of each asset compared with the total
                // previous cost of the assets.
                Double percentage = assetPaymentsPercentage.get(assetPaymentAssetDetail);

                if (paymentCount-- == 1) {
                    // Deplete the rest of the payment for last payment
                    amount = unallocatedAmount;
                } else {
                    // Normal payment will be calculated by asset percentage
                    Double paymentAmount = new Double(assetPaymentDetail.getAmount().toString());
                    amount = new KualiDecimal(paymentAmount.doubleValue() * percentage.doubleValue());
                    unallocatedAmount = unallocatedAmount.subtract(amount);
                }
                assetDetailMap.put(assetPaymentAssetDetail, amount);
            }
            assetPaymentAssetDetailMap.put(assetPaymentDetail.getAssetPaymentDetailKey(), assetDetailMap);
        }

        paymentDistributionMap = assetPaymentAssetDetailMap;
    }

    /**
     * Retrieve the asset payment distributions
     *
     * @return
     */
    public Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> getAssetPaymentDistributions() {
        return paymentDistributionMap;
    }

    /**
     * Get each Asset's allocation totals base the payment distributions
     *
     * @return map of asset detail and its totals
     */
    public Map<AssetPaymentAssetDetail, KualiDecimal> getTotalAssetAllocations() {
        Map<AssetPaymentAssetDetail, KualiDecimal> assetTotalAllocationMap = new HashMap<AssetPaymentAssetDetail, KualiDecimal>();
        KualiDecimal allocation, total;

        //iterate all the distributions
        for (Map<AssetPaymentAssetDetail, KualiDecimal> assetDistrbution : getAssetPaymentDistributions().values()) {

            for (AssetPaymentAssetDetail assetDetail : assetDistrbution.keySet()) {
                allocation = assetDistrbution.get(assetDetail);
                total = assetTotalAllocationMap.get(assetDetail);

                assetTotalAllocationMap.put(assetDetail, total == null ? allocation : total.add(allocation));
            }
        }
        return assetTotalAllocationMap;
    }

    /**
     * Doing the re-distribution of the cost based on the previous total cost of each asset compared with the total previous cost of
     * the assets.
     *
     * @param detailSize
     * @param totalHistoricalCost
     * @param assetPaymentAssetDetail
     * @return
     */
    private Double getAssetDetailPercentage(int detailSize, Double totalHistoricalCost, AssetPaymentAssetDetail assetPaymentAssetDetail) {
        Double previousTotalCostAmount = new Double("0");
        if (assetPaymentAssetDetail.getPreviousTotalCostAmount() != null) {
            previousTotalCostAmount = new Double(StringUtils.defaultIfEmpty(assetPaymentAssetDetail.getPreviousTotalCostAmount().toString(), "0"));
        }

        Double percentage = new Double(0);
        if (totalHistoricalCost.compareTo(new Double(0)) != 0)
            percentage = (previousTotalCostAmount / totalHistoricalCost);
        else
            percentage = (1 / (new Double(detailSize)));
        return percentage;
    }

    public String getLabel() {
        return getClass().getSimpleName();
    }
}
