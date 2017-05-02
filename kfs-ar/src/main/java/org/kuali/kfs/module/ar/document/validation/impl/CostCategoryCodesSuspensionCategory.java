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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.validation.SuspensionCategoryBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Suspension Category that checks to see if cost category codes are setup correctly. An object code might not
 * be assigned or could be assigned to more than one Cost Category. Check by comparing total current expenditure
 * to the sum of account current expenditure.
 */
public class CostCategoryCodesSuspensionCategory extends SuspensionCategoryBase {

    /**
     * @see org.kuali.kfs.module.ar.document.validation.SuspensionCategory#shouldSuspend(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        if (!ArConstants.BillingFrequencyValues.isMilestone(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAward()) &&
            !ArConstants.BillingFrequencyValues.isPredeterminedBilling(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAward())) {
            ContractsGrantsInvoiceDetail totalCostInvoiceDetail = contractsGrantsInvoiceDocument.getTotalCostInvoiceDetail();
            KualiDecimal categoryCumulativeExpenditure = totalCostInvoiceDetail.getCumulativeExpenditures();
            KualiDecimal accountDetailsCumulativeExpenditure = KualiDecimal.ZERO;

            for (InvoiceAccountDetail invoiceAccountDetail : contractsGrantsInvoiceDocument.getAccountDetails()) {
                accountDetailsCumulativeExpenditure = accountDetailsCumulativeExpenditure.add(invoiceAccountDetail.getCumulativeExpenditures());
            }

            return !categoryCumulativeExpenditure.equals(accountDetailsCumulativeExpenditure);
        }

        return false;
    }

}
