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
package org.kuali.kfs.integration.cam;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.integration.cam.CapitalAssetManagementAssetTransactionType;
import org.kuali.kfs.integration.purap.ExternalPurApItem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;

import java.util.List;

public interface CapitalAssetManagementModuleService {
    /**
     * FP document eligible for asset lock when any of its accounting line is taken into CAB during CAB batch.
     *
     * @param accountingDocument
     * @return
     */
    boolean isFpDocumentEligibleForAssetLock(AccountingDocument accountingDocument, String documentType);

    /**
     * Check and store AssetLocks if they are not locked by other blocking documents. Either store all of the asset locks or none of
     * them being stored in case of dead lock. If any of the asset is blocked, the error message will be built including link(s) to
     * the blocking document(s).
     *
     * @param capitalAssetNumbers
     * @param documentNumber
     * @param documentType
     * @param additionalInformation
     * @return return true if all of the asset locks can be granted.
     */
    boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation);

    /**
     * Delete AssetLocks by document number and lockingInfomation for PurAp doc only.
     *
     * @param documentNumber
     * @param lockingInformation
     */
    void deleteAssetLocks(String documentNumber, String lockingInformation);


    /**
     * Check if the given document hold any asset locks.
     *
     * @param documentNumber
     * @param lockingInformation
     * @return
     */
    boolean isAssetLockedByCurrentDocument(String blockingDocumentNumber, String lockingInformation);

    /**
     * Check if the given asset Numbers are locked by other documents already.
     *
     * @param assetNumbers
     * @param documentTypeName
     * @param excludingDocumentNumber
     * @return
     */
    boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber);

    /**
     * Generate asset locks for FP document if it collects capital
     * asset number(s) and has capital asset transactions eligible for CAB
     * batch. Creating asset lock is based on each capital asset line rather
     * than the whole FP document.
     *
     * @param document
     */
    public void generateCapitalAssetLock(Document document, String documentTypeNames);

    /**
     * Deletes the asset locks associated with a particular document
     *
     * @param document
     */
    public void deleteDocumentAssetLocks(Document document);

    /**
     * Check the existence of asset type code
     *
     * @param assetTypeCode
     * @return
     */
    public boolean isAssetTypeExisting(String assetTypeCode);

    /**
     * Get current Purchase Order Document number for given CAMS Document Number
     *
     * @param camsDocumentNumber
     * @return
     */
    String getCurrentPurchaseOrderDocumentNumber(String camsDocumentNumber);

    /**
     * validate the capitalAssetManagementAsset data associated with the given accounting document
     *
     * @param accountingDocument          the given accounting document
     * @param capitalAssetManagementAsset data to be validated
     * @return validation succeeded or errors present
     */
    public boolean validateFinancialProcessingData(AccountingDocument accountingDocument, CapitalAssetInformation capitalAssetInformation, int index);


    public boolean validatePurchasingData(AccountingDocument accountingDocument);

    public boolean validateAccountsPayableData(AccountingDocument accountingDocument);

    public boolean doesAccountingLineFailAutomaticPurchaseOrderRules(AccountingLine accountingLine);

    public boolean doesDocumentFailAutomaticPurchaseOrderRules(AccountingDocument accountingDocument);

    public boolean doesItemNeedCapitalAsset(String itemTypeCode, List accountingLines);

    public boolean validateUpdateCAMSView(AccountingDocument accountingDocumen);

    public boolean validateAddItemCapitalAssetBusinessRules(ItemCapitalAsset asset);

    public boolean warningObjectLevelCapital(AccountingDocument accountingDocument);

    public boolean validateItemCapitalAssetWithErrors(String recurringPaymentTypeCode, ExternalPurApItem item, boolean apoCheck);

    public List<CapitalAssetManagementAssetTransactionType> getAllAssetTransactionTypes();

    /**
     * External modules can notify CAB if a document changed its route status. CAB Uses this notification to release records or to
     * update other modules about the changes
     *
     * @param documentHeader DocumentHeader
     */
    public void notifyRouteStatusChange(DocumentHeader documentHeader);


    /**
     * determine whether there is any object code of the given source accounting lines with a capital asset object sub type
     *
     * @param accountingLines the given source accounting lines
     * @return true if there is at least one object code of the given source accounting lines with a capital asset object sub type;
     * otherwise, false
     */
    public boolean hasCapitalAssetObjectSubType(AccountingDocument accountingDocument);

    public boolean validateAllFieldRequirementsByChart(AccountingDocument accountingDocument);

    public boolean validatePurchasingObjectSubType(AccountingDocument accountingDocument);

    public boolean hasCapitalAssetObjectSubType(AccountingLine accountingLine);

    public boolean validateAssetTags(AccountingDocument accountingDocument);

    /**
     * validates all capital accounting lines that have been processed.
     *
     * @param accountingDocumentForValidation
     * @return true if all lines have been processes else return false
     */
    public boolean validateAllCapitalAccountingLinesProcessed(AccountingDocument accountingDocumentForValidation);

    /**
     * determine whether the given document's all capital accounting lines totals
     * match to that of capital assets.
     *
     * @param accountingDocumentForValidation
     * @return true if totals match else return false
     */
    public boolean validateTotalAmountMatch(AccountingDocument accountingDocumentForValidation);

    /**
     * determine whether the any capital accounting line's amount matches
     * with all the capital assets for that capital accounting line.
     *
     * @param accountingDocument
     * @return true if totals match else return false
     */
    public boolean validateCapitlAssetsAmountToAccountingLineAmount(AccountingDocument accountingDocument);

    /**
     * validates whether capital assets exist for any given capital accounting line.
     *
     * @param accountingDocumentForValidation
     * @return true if capital assets exist for capital accounting line else return false.
     */
    public boolean validateCapitalAccountingLines(AccountingDocument accountingDocumentForValidation);

    /**
     * mark the gl entry line if all the capital asset lines have been processed
     *
     * @param documentNumber
     * @return true if gl entry line marked as processed else return false.
     */
    public boolean markProcessedGLEntryLine(String documentNumber);

    /**
     * Sets the PretagDetails to active that are associated with the campusTagNumber
     * passed in as a parameter. This needs to be done when the AssetGlobalDocument is cancelled.
     *
     * @param campusTagNumber String key used to find pretagDetails to reactivate
     */
    public void reactivatePretagDetails(String campusTagNumber);

    public boolean hasCAMSCapitalAssetObjectSubType(AccountingLine line);

    /**
     * Check FP document eligibility by document type for CAB Extract batch.
     *
     * @param documentType
     * @return
     */
    public boolean isDocumentEligibleForCABBatch(String documentType);

    /**
     * Check FP document individual Capital Asset line eligibility for CAB Extract Batch
     *
     * @param assetInfoLine
     * @param postingYear
     * @return
     */
    public boolean isAssetLineEligibleForCABBatch(
            CapitalAssetInformation assetInfoLine, Integer postingYear,
            List<String> includedObjectSubTypeCodes,
            List<String> excludedChartCodes, List<String> excludedSubFundCodes);

    /**
     * Get CAB Batch parameter value of allowed financial object sub types
     *
     * @return
     */
    public List<String> getBatchIncludedObjectSubTypes();

    /**
     * Get CAB Batch parameter value of disallowed chart codes
     *
     * @return
     */
    public List<String> getBatchExcludedChartCodes();

    /**
     * Get CAB Batch parameter value of disallowed sub fund codes
     *
     * @return
     */
    public List<String> getBatchExcludedSubFundCodes();

    /**
     * This function removes CapitalAssetInformations that don't have at least one capital asset object
     * code in their group details.
     *
     * @param infos
     */
    public void filterNonCapitalAssets(List<CapitalAssetInformation> infos);
}
