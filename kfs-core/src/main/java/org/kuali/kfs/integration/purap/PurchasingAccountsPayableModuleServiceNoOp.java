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
package org.kuali.kfs.integration.purap;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

public class PurchasingAccountsPayableModuleServiceNoOp implements PurchasingAccountsPayableModuleService {

    private Logger LOG = Logger.getLogger(getClass());

    @Override
    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, String authorId, String noteText) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public List<PurchasingAccountsPayableSensitiveData> getAllSensitiveDatas() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return Collections.emptyList();
    }

    @Override
    public String getB2BUrlString() {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return "";
    }

    @Override
    public String getPurchaseOrderInquiryUrl(Integer purchaseOrderNumber) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return "";
    }

    @Override
    public PurchasingAccountsPayableSensitiveData getSensitiveDataByCode(String sensitiveDataCode) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

    @Override
    public void handlePurchasingBatchCancels(String documentNumber, String financialSystemDocumentTypeCode, boolean primaryCancel, boolean disbursedPayment) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public void handlePurchasingBatchPaids(String documentNumber, String financialSystemDocumentTypeCode, Date processDate) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
    }

    @Override
    public boolean isPurchasingBatchDocument(String financialSystemDocumentTypeCode) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return false;
    }

    @Override
    public KualiDecimal getTotalPaidAmountToRequisitions(List<String> documentNumbers) {
        LOG.warn("Using No-Op " + getClass().getSimpleName() + " service.");
        return null;
    }

	@Override
	public boolean hasUseTax(String documentNumber) {
		LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
		return false;
	}

	@Override
	public KualiDecimal getTotalPreTaxDollarAmount(String documentNumber) {
		LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
		return null;
	}
    
    @Override
    public List<String> findPaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId, String invoiceNumber) {
    	LOG.warn( "Using No-op " + getClass().getSimpleName() + " service. " );
    	return Collections.emptyList();
    }
}
