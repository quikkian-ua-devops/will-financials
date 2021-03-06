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
package org.kuali.kfs.module.ld;

/**
 * Constants for message keys. Should have corresponding key=message in resources.
 */
public class LaborKeyConstants {
    public static final String ACCOUNTING_LINE_TOTALS_BY_PAYFY_PAYPERIOD_MISMATCH_ERROR = "error.labor.accountingLineTotalsByPayFYPayPeriodMismatch";
    public static final String ACCOUNTING_LINE_TOTALS_MISMATCH_ERROR = "error.labor.accountingLineTotalsMismatch";
    public static final String DISTINCT_OBJECT_CODE_ERROR = "error.labor.distinctObjectCodeError";
    public static final String ERROR_ACCOUNT_NOT_ACCEPT_FRINGES = "error.labor.accountNotAcceptFringes";
    public static final String ERROR_ACCOUNT_NOT_FOUND = "error.labor.accountNotFound";
    public static final String ERROR_ACCOUNT_NOT_SAME = "error.labor.accountNotSame";
    public static final String ERROR_CANNOT_TRANSFER_NEGATIVE_AMOUNT = "error.labor.cannotTransferNegativeAmount";
    public static final String ERROR_DUPLICATE_SOURCE_ACCOUNTING_LINE = "error.labor.duplicateSourceAccountingLine";
    public static final String ERROR_EMPLOYEE_ID_NOT_SAME = "error.labor.employeeIdNotSame";
    public static final String ERROR_EMPLOYEE_ID_NOT_SAME_IN_TARGET = "error.labor.employeeIdNotSameInTarget";
    public static final String ERROR_FRINGE_BENEFIT_PERCENTAGE_INVALID = "error.labor.invalidFringeBenefitPercentageInvalid";
    public static final String ERROR_INVALID_LABOR_OBJECT_CODE = "error.labor.invalidLaborObjectCodeError";
    public static final String ERROR_LABOR_ERROR_CORRECTION_PERSISTED_ORIGIN_ENTRIES_MISSING = "error.labor.correction.persisted.origin.entries.missing";
    public static final String ERROR_SUB_FUND_GROUP_NOT_FOUND = "error.labor.subFundGroupNotFound";
    public static final String ERROR_TRANSFER_AMOUNT_BY_OBJECT_APPROVAL_CHANGE = "error.labor.transferAmountByObjectApprovalChange";
    public static final String ERROR_TRANSFER_AMOUNT_EXCEED_MAXIMUM = "error.labor.tranferAmountExceedMaximum";
    public static final String ERROR_TRANSFER_AMOUNT_NOT_BALANCED_BY_OBJECT = "error.labor.transferAmountNotBalancedByObject";
    public static final String ERROR_UNPOSTABLE_BALANCE_TYPE = "error.labor.unpostableBalanceTypeCode";
    public static final String ERROR_UNPOSTABLE_PERIOD_CODE = "error.labor.unpostablePerioCodes";
    public static final String ERROR_ZERO_TOTAL_AMOUNT = "error.labor.zeroTotalAmount";
    public static final String ERROR_NON_FRINGE_ACCOUNT_ALTERNATIVE_NOT_FOUND = "error.labor.nonFringeAccountAlternativeNotFound";
    public static final String ERROR_SUN_FUND_NOT_ACCEPT_WAGES = "error.labor.subFundNotAcceptwages";
    public static final String ERROR_INVALID_SUSPENSE_ACCOUNT = "error.labor.invalidSuspenseAccount";
    public static final String INVALID_FRINGE_OBJECT_CODE_ERROR = "error.labor.invalidFringeObjectCode";
    public static final String INVALID_PAY_PERIOD_CODE = "error.labor.invalidPayPeriodCodeError";
    public static final String INVALID_PAY_YEAR = "error.labor.invalidPayYearError";
    public static final String INVALID_SALARY_OBJECT_CODE_ERROR = "error.labor.invalidSalaryObjectCode";
    public static final String INVALID_SALARY_ACCOUNT_SUB_FUND_ERROR = "error.labor.invalidSalaryAccountSubFund";
    public static final String LABOR_OBJECT_MISSING_OBJECT_CODE_ERROR = "error.labor.missingObjectCode";
    public static final String LLCP_UPLOAD_FILE_INVALID_RECORD_SIZE_ERROR = "error.labor.llcpInvalidRecordSize";
    public static final String MESSAGE_YEAR_END_TRANSACTION_DESCRIPTON = "message.labor.yearEndTransactionDescription";
    public static final String MESSAGE_SUSPENSE_ACCOUNT_APPLIED = "message.labor.suspenseAccountApplied";
    public static final String MESSAGE_WAGES_MOVED_TO = "message.labor.wagesMovedTo";
    public static final String MESSAGE_FRINGES_MOVED_TO = "message.labor.fringesMovedTo";
    public static final String MISSING_EMPLOYEE_ID = "error.labor.missingEmployeeIdError";
    public static final String PENDING_SALARY_TRANSFER_ERROR = "error.labor.alreadyPendingSalaryTransferError";
    public static final String PENDING_BENEFIT_TRANSFER_ERROR = "error.labor.alreadyPendingBenefitTransferError";
    public static final String EFFORT_VALIDATION_OVERRIDE_MESSAGE = "message.labor.effortValidationOverride";
    public static final String EFFORT_AUTO_DISAPPROVE_MESSAGE = "message.labor.effortAutoDisapprove";
    public static final String ERROR_EFFORT_CLOSED_REPORT_PERIOD = "error.labor.effortReportPeriodClosed";
    public static final String ERROR_EFFORT_OPEN_PERIOD_COST_SHARE = "error.labor.effortOpenPeriodCostShare";
    public static final String ERROR_EFFORT_OPEN_PERIOD_CG_ACCOUNT = "error.labor.effortOpernPeriodCGAccount";
    public static final String ERROR_EFFORT_OPEN_PERIOD_ACCOUNTS_NOT_BALANCED = "error.labor.effortOpenPeriodAccountNonBalanced";

    public static final class Balancing {
        public static final String REPORT_ENTRY_LABEL = "message.ld.balancing.report.entry.label";
        public static final String REPORT_BALANCE_LABEL = "message.ld.balancing.report.balance.label";
    }

    public static final class EnterpriseFeed {
        public static final String ERROR_OUTPUT_FILE_NOT_GENERATED = "error.ld.enterpriseFeed.report.outputFileNotGenerated";
        public static final String ERROR_BENEFIT_CALCULATION_NOT_FOUND = "error.ld.enterpriseFeed.report.benefitCalculationNotFound";
        public static final String ERROR_BENEFIT_TYPE_NOT_FOUND = "error.ld.enterpriseFeed.report.benefitTypeNotFound";
    }

    public static final class ErrorCertification {
        public static final String ERROR_ERROR_CERT_FIELDS_REQ = "error.errorCert.fieldsReq";
        public static final String ERROR_ERROR_CERT_FISCAL_PER_PARAM_TRIGGERED = "error.errorCert.fiscalPerParamTriggered";
        public static final String ERROR_ERROR_CERT_KEY = "document.errorCertification";
        public static final String QUESTION_ERROR_CERTIFICATION_STMT = "question.errorCertStmt";
    }
}
