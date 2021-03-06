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
package org.kuali.kfs.sys.batch;

import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;

import java.util.Date;

public class PaymentSourceToPdpExtractStep extends AbstractStep {
    private PaymentSourceExtractionService paymentSourceExtractionService;

    /**
     * Extracts the payments to PDP via the injected PaymentSourceExtractionService
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return paymentSourceExtractionService.extractPayments();
    }

    /**
     * @return the implementation of the PaymentSourceExtractionService to use for this step
     */
    public PaymentSourceExtractionService getPaymentSourceExtractionService() {
        return paymentSourceExtractionService;
    }

    /**
     * Sets the implementation of PaymentSourceExtractionService to use
     *
     * @param paymentSourceExtractionService the implementation of PaymentSourceExtractionService to use
     */
    public void setPaymentSourceExtractionService(PaymentSourceExtractionService paymentSourceExtractionService) {
        this.paymentSourceExtractionService = paymentSourceExtractionService;
    }

}
