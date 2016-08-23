/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.service.impl.SalaryExpenseTransferTransactionAgeServiceImpl;
import org.kuali.kfs.module.ld.document.validation.impl.SalaryExpenseTransferDocumentRuleConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.service.impl.UniversityDateServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * The unit tests for methods in SalaryExpenseTransferTransactionAgeService. This specifically tests to make sure the correct
 * parameters are used based on the age of the transaction and the subfund. It only sets up a target accounting line because 1) the
 * same logic to test a source accounting line is triggered and 2) the subfund logic only applies to target accounting lines.
 *
 * @see org.kuali.kfs.module.ld.document.service.impl.SalaryExpenseTransferTransactionAgeServiceImpl
 */

@ConfigureContext
public class SalaryExpenseTransferTransactionAgeServiceTest extends KualiTestBase {
    private static final Integer DEFAULT_PARM_FISCAL_PERIODS = new Integer("3");
    private static final Integer SUBFUND_PARM_FISCAL_PERIODS = new Integer("2");
    private static final String DEFAULT_PARM_SUBFUND = "FEDERA=2";
    private static final String YOUNGER_FISCAL_PER = "11";
    private static final String OLDER_FISCAL_PER = "7";
    private static final String NON_PARM_SUBFUND = "AG";
    private static final String PARM_SUBFUND = "FEDERA";
    private static final String TEST_FISCAL_PERIOD = "13";

    private SalaryExpenseTransferTransactionAgeServiceImpl salaryExpenseTransferTransactionAgeService;
    private MyUniversityDateService universityDateService;
    private UniversityDateService oldDateService;
    private SalaryExpenseTransferDocument stDoc;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        salaryExpenseTransferTransactionAgeService = new SalaryExpenseTransferTransactionAgeServiceImpl();
        universityDateService = new MyUniversityDateService();
        oldDateService = salaryExpenseTransferTransactionAgeService.getUniversityDateService();

        salaryExpenseTransferTransactionAgeService.setUniversityDateService(universityDateService);
        salaryExpenseTransferTransactionAgeService.setParameterService(SpringContext.getBean(ParameterService.class));
        TestUtils.setSystemParameter(KfsParameterConstants.LABOR_DOCUMENT.class, SalaryExpenseTransferDocumentRuleConstants.DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED, DEFAULT_PARM_FISCAL_PERIODS.toString());
        TestUtils.setSystemParameter(KfsParameterConstants.LABOR_DOCUMENT.class, SalaryExpenseTransferDocumentRuleConstants.ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND, DEFAULT_PARM_SUBFUND);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        this.salaryExpenseTransferTransactionAgeService.setUniversityDateService(oldDateService);
    }

    /**
     * Test the case where a target accounting line is "younger" and the subfund is not in the
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter. This is therefore based on fiscal periods and the
     * DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED parameter. The target accounting line will use the current
     * year and will be set to YOUNGER_FISCAL_PER. The validation will use the UniversityDateService that is defined in this test
     * class.
     */
    public void testYoungerAccountingLine() {
        List<LaborLedgerExpenseTransferAccountingLine> accountingLines = createAccountingLinesList(YOUNGER_FISCAL_PER, NON_PARM_SUBFUND);

        boolean isYounger = salaryExpenseTransferTransactionAgeService.defaultNumberOfFiscalPeriodsCheck(accountingLines, DEFAULT_PARM_FISCAL_PERIODS);

        assertTrue("The transaction is older, but should be younger.", isYounger);
    }


    /**
     * Test the case where a target accounting line is "older" and the subfund is not in the
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter. This is therefore based on fiscal periods and the
     * DEFAULT_NUMBER_OF_FISCAL_PERIODS_ERROR_CERTIFICATION_TAB_REQUIRED parameter. The target accounting line will use the current
     * year and will be set to OLDER_FISCAL_PER. The validation will use the UniversityDateService that is defined in this test
     * class. Note that defaultNumberOfFiscalPeriodsCheck returns true of all transactions are younger.
     */
    public void testOlderAccountingLine() {
        List<LaborLedgerExpenseTransferAccountingLine> accountingLines = createAccountingLinesList(OLDER_FISCAL_PER, NON_PARM_SUBFUND);

        boolean isYounger = salaryExpenseTransferTransactionAgeService.defaultNumberOfFiscalPeriodsCheck(accountingLines, DEFAULT_PARM_FISCAL_PERIODS);

        assertFalse("The transaction is younger, but should be older.", isYounger);
    }

    /**
     * Test the case where a target accounting line is "older" and the subfund is in the
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter. This is based on fiscal periods and the
     * ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter. The target accounting line will use the current year and will be
     * set to OLDER_FISCAL_PER. The validation will use the UniversityDateService that is defined in this test class. Note that
     * defaultNumberOfFiscalPeriodsCheck returns true of all transactions are younger.
     */
    public void testSubFundParameterTrigger() {
        List<LaborLedgerExpenseTransferAccountingLine> accountingLines = createAccountingLinesList(OLDER_FISCAL_PER, PARM_SUBFUND);

        boolean isYounger = salaryExpenseTransferTransactionAgeService.defaultNumberOfFiscalPeriodsCheck(accountingLines, SUBFUND_PARM_FISCAL_PERIODS);

        assertFalse("The transaction should have been considered older and had a subfund that is in the ERROR_CERTIFICATION_DEFAULT_OVERRIDE_BY_SUB_FUND parameter.", isYounger);
    }

    /**
     * Creates target accounting lines for use in the unit tests based on the fiscalPeriod and subFund that are passed in.
     *
     * @param fiscalPeriod
     * @param subFund
     * @return accountingLines for testing
     */
    public List createAccountingLinesList(String fiscalPeriod, String subFund) {
        List<LaborLedgerExpenseTransferAccountingLine> accountingLines = new ArrayList<LaborLedgerExpenseTransferAccountingLine>();
        Account account = new Account();
        SubFundGroup subFundGroup = new SubFundGroup();
        subFundGroup.setSubFundGroupCode(subFund);
        account.setSubFundGroup(subFundGroup);

        // create a target accounting line for testing in set object
        ExpenseTransferTargetAccountingLine targetAccountingLine = new ExpenseTransferTargetAccountingLine();
        targetAccountingLine.setPayrollEndDateFiscalYear(salaryExpenseTransferTransactionAgeService.getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        targetAccountingLine.setPayrollEndDateFiscalPeriodCode(fiscalPeriod);
        targetAccountingLine.setAccount(account);

        accountingLines.add(targetAccountingLine);

        return accountingLines;
    }

    /**
     * Fake the current university date for this test to be in the TEST_FISCAL_PERIOD.
     */
    static class MyUniversityDateService extends UniversityDateServiceImpl {
        @Override
        public UniversityDate getCurrentUniversityDate() {
            UniversityDate universityDate = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
            universityDate.setUniversityFiscalAccountingPeriod(TEST_FISCAL_PERIOD);
            return universityDate;
        }
    }
}
