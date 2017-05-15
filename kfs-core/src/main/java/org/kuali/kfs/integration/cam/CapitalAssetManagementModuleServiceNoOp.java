/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.integration.purap.ExternalPurApItem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CapitalAssetManagementModuleServiceNoOp implements CapitalAssetManagementModuleService {

    private Logger LOG = Logger.getLogger(getClass());

    public void deleteAssetLocks(String documentNumber, String lockingInformation) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    public void deleteDocumentAssetLocks(Document document) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    public void generateCapitalAssetLock(Document document, String documentTypeNames) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    public boolean isAssetLocked(List<Long> assetNumbers, String documentTypeName, String excludingDocumentNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    public boolean isAssetLockedByCurrentDocument(String blockingDocumentNumber, String lockingInformation) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    public boolean isFpDocumentEligibleForAssetLock(AccountingDocument accountingDocument, String documentType) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    public boolean storeAssetLocks(List<Long> capitalAssetNumbers, String documentNumber, String documentType, String lockingInformation) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean doesAccountingLineFailAutomaticPurchaseOrderRules(AccountingLine accountingLine) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public boolean doesDocumentFailAutomaticPurchaseOrderRules(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public boolean doesItemNeedCapitalAsset(String itemTypeCode, List accountingLines) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public List<CapitalAssetManagementAssetTransactionType> getAllAssetTransactionTypes() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return Collections.emptyList();
    }

    @Override
    public String getCurrentPurchaseOrderDocumentNumber(String camsDocumentNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public boolean hasCapitalAssetObjectSubType(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public boolean hasCapitalAssetObjectSubType(AccountingLine accountingLine) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public void notifyRouteStatusChange(DocumentHeader documentHeader) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public boolean validateAccountsPayableData(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateAddItemCapitalAssetBusinessRules(ItemCapitalAsset asset) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateAllFieldRequirementsByChart(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateFinancialProcessingData(AccountingDocument accountingDocument, CapitalAssetInformation capitalAssetInformation, int index) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateItemCapitalAssetWithErrors(String recurringPaymentTypeCode, ExternalPurApItem item, boolean apoCheck) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validatePurchasingData(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validatePurchasingObjectSubType(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateUpdateCAMSView(AccountingDocument accountingDocumen) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean warningObjectLevelCapital(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public boolean isAssetTypeExisting(String assetTypeCode) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    //capital accounting lines validations called from fp documents side..
    @Override
    public boolean validateAllCapitalAccountingLinesProcessed(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateTotalAmountMatch(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateCapitlAssetsAmountToAccountingLineAmount(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateCapitalAccountingLines(AccountingDocument accountingDocument) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean markProcessedGLEntryLine(String documentNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    @Override
    public boolean validateAssetTags(AccountingDocument accountingDocumen) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }


    public boolean isDocumentEligibleForCABBatch(String documentType) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    public boolean isAssetLineEligibleForCABBatch(
            CapitalAssetInformation assetInfoLine, Integer postingYear,
            List<String> includedObjectSubTypeCodes,
            List<String> excludedChartCodes, List<String> excludedSubFundCodes) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    public List<String> getBatchIncludedObjectSubTypes() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList<String>();
    }

    public List<String> getBatchExcludedChartCodes() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList<String>();
    }

    public List<String> getBatchExcludedSubFundCodes() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return new ArrayList<String>();
    }


    @Override
    public void reactivatePretagDetails(String campusTagNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public boolean hasCAMSCapitalAssetObjectSubType(AccountingLine line) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return true;
    }

    public void filterNonCapitalAssets(List<CapitalAssetInformation> infos) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }
}
