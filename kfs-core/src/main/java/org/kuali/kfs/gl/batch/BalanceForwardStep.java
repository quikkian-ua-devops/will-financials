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
package org.kuali.kfs.gl.batch;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.YearEndService;
import org.kuali.kfs.sys.batch.AbstractWrappedBatchStep;
import org.kuali.kfs.sys.batch.service.WrappedBatchExecutorService.CustomBatchExecutor;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.springframework.util.StopWatch;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * This step runs the balance forward year end process.
 */
public class BalanceForwardStep extends AbstractWrappedBatchStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceForwardStep.class);

    private YearEndService yearEndService;

    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * @see org.kuali.kfs.sys.batch.AbstractWrappedBatchStep#getCustomBatchExecutor()
     */
    @Override
    protected CustomBatchExecutor getCustomBatchExecutor() {
        return new CustomBatchExecutor() {
            /**
             * This step runs the balance forward service, specifically finding the parameters the job needs, creating the origin entry
             * groups for the output origin entries, and creating the process's reports.
             * @return that the job finished successfully
             * @see org.kuali.kfs.sys.batch.Step#execute(String, java.util.Date)
             */
            @Override
            public boolean execute() {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("Balance Forward Step");

                Date varTransactionDate;
                try {
                    DateFormat transactionDateFormat = new SimpleDateFormat(TRANSACTION_DATE_FORMAT_STRING);
                    varTransactionDate = new Date(transactionDateFormat.parse(getParameterService().getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM)).getTime());
                } catch (ParseException e) {
                    LOG.error("forwardBalances() Unable to parse transaction date", e);
                    throw new IllegalArgumentException("Unable to parse transaction date");
                }

                Integer varFiscalYear = new Integer(getParameterService().getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));

                String balanceForwardsUnclosedFileName = GeneralLedgerConstants.BatchFileSystem.BALANCE_FORWARDS_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
                String balanceForwardsclosedFileName = GeneralLedgerConstants.BatchFileSystem.BALANCE_FORWARDS_CLOSED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

                BalanceForwardRuleHelper balanceForwardRuleHelper = new BalanceForwardRuleHelper(varFiscalYear, varTransactionDate, balanceForwardsclosedFileName, balanceForwardsUnclosedFileName);

                if (balanceForwardRuleHelper.isAnnualClosingChartParamterBlank()) {
                    //execute delivered foundation code, either ANNUAL_CLOSING_CHARTS parameter did not exist or there were no values specified
                    yearEndService.logAllMissingPriorYearAccounts(varFiscalYear);
                    yearEndService.logAllMissingSubFundGroups(varFiscalYear);
                } else {
                    //ANNUAL_CLOSING_CHARTS parameter was detected and contained values
                    yearEndService.logAllMissingPriorYearAccounts(varFiscalYear, balanceForwardRuleHelper.getAnnualClosingCharts());
                    yearEndService.logAllMissingSubFundGroups(varFiscalYear, balanceForwardRuleHelper.getAnnualClosingCharts());
                }
                //methods called internally deal with chart param being populated so was not moved inside if-then-else

                yearEndService.forwardBalances(balanceForwardsUnclosedFileName, balanceForwardsclosedFileName, balanceForwardRuleHelper);

                stopWatch.stop();
                LOG.info("Balance Forward Step took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

                return true;
            }
        };
    }

    /**
     * Sets the yearEndService attribute, allowing injection of an implementation of the service
     *
     * @param yearEndService an implementation of the yearEndService
     * @see org.kuali.module.gl.service.yearEndService
     */
    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }
}
