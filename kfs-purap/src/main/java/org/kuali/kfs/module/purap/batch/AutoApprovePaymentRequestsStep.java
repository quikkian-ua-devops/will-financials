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
package org.kuali.kfs.module.purap.batch;

import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.util.Date;

/**
 * Step used to auto approve payment requests that meet a certain criteria
 */
public class AutoApprovePaymentRequestsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoApprovePaymentRequestsStep.class);

    private PaymentRequestService paymentRequestService;

    public AutoApprovePaymentRequestsStep() {
        super();
    }

    /**
     * Calls service method to approve payment requests
     *
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        return paymentRequestService.autoApprovePaymentRequests();
    }

    /**
     * Invoke execute method
     *
     * @return
     * @throws InterruptedException
     */
    public boolean execute() throws InterruptedException {
        try {
            return execute(null, SpringContext.getBean(DateTimeService.class).getCurrentDate());
        } catch (InterruptedException e) {
            LOG.error("Exception occured executing step", e);
            throw e;
        } catch (RuntimeException e) {
            LOG.error("Exception occured executing step", e);
            throw e;
        }
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

}
