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
package org.kuali.kfs.module.ar.batch;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService;
import org.kuali.kfs.sys.batch.AbstractStep;

import java.util.Date;

/**
 * This step of LetterOFCreditJob would create and route a cash control document and payment application documents
 * for CG Invoices.
 */
public class LetterOfCreditCreateAndRouteDocumentsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditCreateAndRouteDocumentsStep.class);

    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected LetterOfCreditCreateService letterOfCreditCreateService;
    protected String batchFileDirectoryName;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {
        if (getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            getLetterOfCreditCreateService().processLettersOfCredit(batchFileDirectoryName);
        } else {
            LOG.info("Contracts & Grants Billing enhancement not turned on; therefore, not running letterOfCreditCreateAndRouteDocumentsStep");
        }
        return true;
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

    /**
     * Gets the letterOfCreditCreateService attribute.
     *
     * @return Returns the letterOfCreditCreateService.
     */
    public LetterOfCreditCreateService getLetterOfCreditCreateService() {
        return letterOfCreditCreateService;
    }

    /**
     * Sets the letterOfCreditCreateService attribute value.
     *
     * @param letterOfCreditCreateService The letterOfCreditCreateService to set.
     */
    public void setLetterOfCreditCreateService(LetterOfCreditCreateService letterOfCreditCreateService) {
        this.letterOfCreditCreateService = letterOfCreditCreateService;
    }

    /**
     * This method...
     * Indeed.  This method.  Remember all the times this method has been used?  Every time this step is run?  It's amazing, really.
     * Just a simple setter, making sure we can inject the correct batch file directory name.  Without out, though, there would be no
     * place to write out the results file for the step and none of us would know what had gone on.  So really, there's no better way
     * we could say it better.  This method...
     *
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
