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
package org.kuali.kfs.gl.batch.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionAccount;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterIndirectCostRecoveryEntriesStep;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.IndirectCostRecoveryService;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * This implementation of PostTransaction creates ExpenditureTransactions, temporary records used
 * for ICR generation
 */
@Transactional
public class PostExpenditureTransaction implements IndirectCostRecoveryService, PostTransaction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PostExpenditureTransaction.class);

    private static final String INDIRECT_COST_FISCAL_PERIODS_PARAMETER = "INDIRECT_COST_FISCAL_PERIODS";
    private static final String ICR_EXCLUSIONS_AT_TRANSACTION_AND_TOP_LEVEL_ONLY_PARAMETER_NAME = "ICR_EXCLUSIONS_AT_TRANSACTION_AND_TOP_LEVEL_ONLY_IND";

    protected AccountingCycleCachingService accountingCycleCachingService;
    protected PersistenceStructureService persistenceStructureService;
    protected ParameterService parameterService;
    protected BusinessObjectService businessObjectService;
    protected ParameterEvaluatorService parameterEvaluatorService;


    /**
     * This will determine if this transaction is an ICR eligible transaction
     *
     * @param transaction the transaction which is being determined to be ICR or not
     * @param objectType the object type of the transaction
     * @param account the account of the transaction
     * @param objectCode the object code of the transaction
     * @return true if the transaction is an ICR transaction and therefore should have an expenditure transaction created for it; false if otherwise
     */
    @Override
    public boolean isIcrTransaction(Transaction transaction, ReportWriterService reportWriterService) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("isIcrTransaction() started");
        }

        // Is the ICR indicator set?
        // Is the period code a non-balance period, as specified by KFS-GL / Poster Indirect Cost Recoveries Step / INDIRECT_COST_FISCAL_PERIODS? If so, continue, if not, we aren't posting this transaction
        if (transaction.getObjectType().isFinObjectTypeIcrSelectionIndicator() && getParameterEvaluatorService().getParameterEvaluator(PosterIndirectCostRecoveryEntriesStep.class, PostExpenditureTransaction.INDIRECT_COST_FISCAL_PERIODS_PARAMETER, transaction.getUniversityFiscalPeriodCode()).evaluationSucceeds()) {
            // Continue on the posting process

            // Check the sub account type code. A21 sub-accounts with the type of CS don't get posted
            A21SubAccount a21SubAccount = getAccountingCycleCachingService().getA21SubAccount(transaction.getAccount().getChartOfAccountsCode(), transaction.getAccount().getAccountNumber(), transaction.getSubAccountNumber());
            String financialIcrSeriesIdentifier;
            String indirectCostRecoveryTypeCode;

            // first, do a check to ensure that if the sub-account is set up for ICR, that the account is also set up for ICR
            if (a21SubAccount != null) {
                if (StringUtils.hasText(a21SubAccount.getFinancialIcrSeriesIdentifier()) && StringUtils.hasText(a21SubAccount.getIndirectCostRecoveryTypeCode())) {
                    // the sub account is set up for ICR, make sure that the corresponding account is as well, just for validation purposes
                    if (!StringUtils.hasText(transaction.getAccount().getFinancialIcrSeriesIdentifier()) || !StringUtils.hasText(transaction.getAccount().getAcctIndirectCostRcvyTypeCd())) {
                        List<Message> warnings = new ArrayList<Message>();
                        warnings.add(new Message("Warning - excluding transaction from Indirect Cost Recovery because Sub-Account is set up for ICR, but Account is not.", Message.TYPE_WARNING));
                        reportWriterService.writeError(transaction, warnings);
                    }
                }

                if (StringUtils.hasText(a21SubAccount.getFinancialIcrSeriesIdentifier()) && StringUtils.hasText(a21SubAccount.getIndirectCostRecoveryTypeCode())) {
                    // A21SubAccount info set up correctly
                    financialIcrSeriesIdentifier = a21SubAccount.getFinancialIcrSeriesIdentifier();
                    indirectCostRecoveryTypeCode = a21SubAccount.getIndirectCostRecoveryTypeCode();
                }
                else {
                    // we had an A21SubAccount, but it was not set up for ICR, use account values instead
                    financialIcrSeriesIdentifier = transaction.getAccount().getFinancialIcrSeriesIdentifier();
                    indirectCostRecoveryTypeCode = transaction.getAccount().getAcctIndirectCostRcvyTypeCd();
                }
            }
            else {
                // no A21SubAccount found, default to using Account
                financialIcrSeriesIdentifier = transaction.getAccount().getFinancialIcrSeriesIdentifier();
                indirectCostRecoveryTypeCode = transaction.getAccount().getAcctIndirectCostRcvyTypeCd();
            }

            // the ICR Series identifier set?
            if (!StringUtils.hasText(financialIcrSeriesIdentifier)) {
                LOG.debug("isIcrTransaction() Not ICR Account");
                return false;
            }

            if ((a21SubAccount != null) && KFSConstants.SubAccountType.COST_SHARE.equals(a21SubAccount.getSubAccountTypeCode())) {
                // No need to post this
                LOG.debug("isIcrTransaction() A21 subaccounts with type of CS - not posted");
                return false;
            }

            // do we have an exclusion by type or by account?  then we don't have to post no expenditure transaction
            final boolean selfAndTopLevelOnly = getParameterService().getParameterValueAsBoolean(PosterIndirectCostRecoveryEntriesStep.class, PostExpenditureTransaction.ICR_EXCLUSIONS_AT_TRANSACTION_AND_TOP_LEVEL_ONLY_PARAMETER_NAME);
            if (excludedByType(indirectCostRecoveryTypeCode, transaction.getFinancialObject(), selfAndTopLevelOnly)) {
                return false;
            }
            if (excludedByAccount(transaction.getAccount(), transaction.getFinancialObject(), selfAndTopLevelOnly)) {
                return false;
            }

            return true;  // still here?  then I guess we don't have an exclusion
        }
        else {
            // Don't need to post anything
            LOG.debug("isIcrTransaction() invalid period code - not posted");
            return false;
        }
    }

    /**
     * Determines if there's an exclusion by type record existing for the given ICR type code and object code or object codes within the object code's reportsTo hierarchy
     * @param indirectCostRecoveryTypeCode the ICR type code to check
     * @param objectCode the object code to check for, as well as check the reports-to hierarchy
     * @param selfAndTopLevelOnly whether only the given object code and the top level object code should be checked
     * @return true if the transaction with the given ICR type code and object code have an exclusion by type record, false otherwise
     */
    protected boolean excludedByType(String indirectCostRecoveryTypeCode, ObjectCode objectCode, boolean selfAndTopLevelOnly) {
        // If the ICR type code is empty or excluded by the KFS-GL / Batch / INDIRECT_COST_TYPES parameter, don't post
        if ((!StringUtils.hasText(indirectCostRecoveryTypeCode)) || !getParameterEvaluatorService().getParameterEvaluator(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.INDIRECT_COST_TYPES_PARAMETER, indirectCostRecoveryTypeCode).evaluationSucceeds()) {
            // No need to post this
            if (LOG.isDebugEnabled()) {
                LOG.debug("isIcrTransaction() ICR type is null or excluded by the KFS-GL / Poster Indirect Cost Recoveries Step / INDIRECT_COST_TYPES parameter - not posted");
            }
            return true;
        }

        if (hasExclusionByType(indirectCostRecoveryTypeCode, objectCode)) {
            return true;
        }

        ObjectCode currentObjectCode = getReportsToObjectCode(objectCode);
        while (currentObjectCode != null && !currentObjectCode.isReportingToSelf()) {
            if (!selfAndTopLevelOnly && hasExclusionByType(indirectCostRecoveryTypeCode, currentObjectCode)) {
                return true;
            }

            currentObjectCode = getReportsToObjectCode(currentObjectCode);
        }
        if (currentObjectCode != null && hasExclusionByType(indirectCostRecoveryTypeCode, currentObjectCode))
         {
            return true; // we must be top level if the object code isn't null
        }

        return false;
    }

    /**
     * Determines if the given object code and indirect cost recovery type code have an exclusion by type record associated with them
     * @param indirectCostRecoveryTypeCode the indirect cost recovery type code to check
     * @param objectCode the object code to check
     * @return true if there's an exclusion by type record for this type code and object code
     */
    protected boolean hasExclusionByType(String indirectCostRecoveryTypeCode, ObjectCode objectCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.ACCOUNT_INDIRECT_COST_RECOVERY_TYPE_CODE, indirectCostRecoveryTypeCode);
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        final IndirectCostRecoveryExclusionType excType = getBusinessObjectService().findByPrimaryKey(IndirectCostRecoveryExclusionType.class, keys);
        return !ObjectUtils.isNull(excType) && excType.isActive();
    }

    /**
     * Determine if the given account and object code have an exclusion by account associated which should prevent this transaction from posting an ExpenditureTransaction
     * @param account account to check
     * @param objectCode object code to check
     * @param selfAndTopLevelOnly if only the given object code and the top level object code should seek exclusion by account records or not
     * @return true if the given account and object code have an associated exclusion by account, false otherwise
     */
    protected boolean excludedByAccount(Account account, ObjectCode objectCode, boolean selfAndTopLevelOnly) {
        if (hasExclusionByAccount(account, objectCode)) {
            return true;
        }

        ObjectCode currentObjectCode = getReportsToObjectCode(objectCode);
        while (currentObjectCode != null && !currentObjectCode.isReportingToSelf()) {
            if (!selfAndTopLevelOnly && hasExclusionByAccount(account, currentObjectCode)) {
                return true;
            }

            currentObjectCode = getReportsToObjectCode(currentObjectCode);
        }
        if (currentObjectCode != null && hasExclusionByAccount(account, currentObjectCode))
         {
            return true; // we must be top level if we got this far
        }

        return false;
    }

    /**
     * Determines if there's an exclusion by account record for the given account and object code
     * @param account the account to check
     * @param objectCode the object code to check
     * @return true if the given account and object code have an exclusion by account record, false otherwise
     */
    protected boolean hasExclusionByAccount(Account account, ObjectCode objectCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CHART_OF_ACCOUNT_CODE, objectCode.getChartOfAccountsCode());
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        keys.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);
        final IndirectCostRecoveryExclusionAccount excAccount = getBusinessObjectService().findByPrimaryKey(
        		IndirectCostRecoveryExclusionAccount.class, keys);
        boolean hasExclusion = !ObjectUtils.isNull(excAccount);
        if (LOG.isDebugEnabled()) {
        	LOG.debug("hasExclusionByAccount for account " + account.getAccountNumber() + " and object code " +
        		objectCode.getCode() + " is returning " + (hasExclusion));
        }

        return hasExclusion;
    }

    /**
     * Determines if the given object code has a valid reports-to hierarchy
     * @param objectCode the object code to check
     * @return true if the object code has a valid reports-to hierarchy with no nulls; false otherwise
     */
    protected boolean hasValidObjectCodeReportingHierarchy(ObjectCode objectCode) {
        ObjectCode currentObjectCode = objectCode;
        while (hasValidReportsToFields(currentObjectCode) && !currentObjectCode.isReportingToSelf()) {
            currentObjectCode = getReportsToObjectCode(currentObjectCode);
            if (ObjectUtils.isNull(currentObjectCode) || !currentObjectCode.isActive()) {
                return false;
            }
        }
        if (!hasValidReportsToFields(currentObjectCode)) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the given object code has all the fields it would need for a strong and healthy reports to hierarchy
     * @param objectCode the object code to give a little check
     * @return true if everything is good, false if the object code has a bad, rotted reports to hierarchy
     */
    protected boolean hasValidReportsToFields(ObjectCode objectCode) {
        return !org.apache.commons.lang.StringUtils.isBlank(objectCode.getReportsToChartOfAccountsCode()) && !org.apache.commons.lang.StringUtils.isBlank(objectCode.getReportsToFinancialObjectCode());
    }

    /**
     * Uses the caching DAO instead of regular OJB to find the reports-to object code
     * @param objectCode the object code to get the reporter of
     * @return the reports to object code, or, if that is impossible, null
     */
    protected ObjectCode getReportsToObjectCode(ObjectCode objectCode) {
       return getAccountingCycleCachingService().getObjectCode(objectCode.getUniversityFiscalYear(), objectCode.getReportsToChartOfAccountsCode(), objectCode.getReportsToFinancialObjectCode());
    }

    /**
     * If the transaction is a valid ICR transaction, posts an expenditure transaction record for the transaction
     *
     * @param t the transaction which is being posted
     * @param mode the mode the poster is currently running in
     * @param postDate the date this transaction should post to
     * @param posterReportWriterService the writer service where the poster is writing its report
     * @return the accomplished post type
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#post(org.kuali.kfs.gl.businessobject.Transaction, int, java.util.Date)
     */
    @Override
    public String post(Transaction t, int mode, Date postDate, ReportWriterService posterReportWriterService) {
        LOG.debug("post() started");

        if (ObjectUtils.isNull(t.getFinancialObject()) || !hasValidObjectCodeReportingHierarchy(t.getFinancialObject())) {
            // I agree with the commenter below...this seems totally lame
            return GeneralLedgerConstants.ERROR_CODE + ": Warning - excluding transaction from Indirect Cost Recovery because "+t.getUniversityFiscalYear().toString()+"-"+t.getChartOfAccountsCode()+"-"+t.getFinancialObjectCode()+" has an invalid reports to hierarchy (either has an non-existent object or an inactive object)";
        }
        else if (isIcrTransaction(t, posterReportWriterService)) {
            return postTransaction(t, mode);
        }
        return GeneralLedgerConstants.EMPTY_CODE;
    }

    /**
     * Actually posts the transaction to the appropriate expenditure transaction record
     *
     * @param t the transaction to post
     * @param mode the mode of the poster as it is currently running
     * @return the accomplished post type
     */
    protected String postTransaction(Transaction t, int mode) {
        LOG.debug("postTransaction() started");

        String returnCode = GeneralLedgerConstants.UPDATE_CODE;
        ExpenditureTransaction et = getAccountingCycleCachingService().getExpenditureTransaction(t);
        if (et == null) {
            LOG.debug("Posting expenditure transation");
            et = new ExpenditureTransaction(t);
            returnCode = GeneralLedgerConstants.INSERT_CODE;
        }

        if (org.apache.commons.lang.StringUtils.isBlank(t.getOrganizationReferenceId())) {
            et.setOrganizationReferenceId(GeneralLedgerConstants.getDashOrganizationReferenceId());
        }

        if (KFSConstants.GL_DEBIT_CODE.equals(t.getTransactionDebitCreditCode()) || KFSConstants.GL_BUDGET_CODE.equals(t.getTransactionDebitCreditCode())) {
            et.setAccountObjectDirectCostAmount(et.getAccountObjectDirectCostAmount().add(t.getTransactionLedgerEntryAmount()));
        }
        else {
            et.setAccountObjectDirectCostAmount(et.getAccountObjectDirectCostAmount().subtract(t.getTransactionLedgerEntryAmount()));
        }

        if (returnCode.equals(GeneralLedgerConstants.INSERT_CODE)) {
            //TODO: remove this log statement. Added to troubleshoot FSKD-194.
            LOG.info("Inserting a GLEX record. Transaction:"+t);
            getAccountingCycleCachingService().insertExpenditureTransaction(et);
        } else {
            //TODO: remove this log statement. Added to troubleshoot FSKD-194.
            LOG.info("Updating a GLEX record. Transaction:"+t);
            getAccountingCycleCachingService().updateExpenditureTransaction(et);
        }

        return returnCode;
    }

    /**
     * @see org.kuali.kfs.gl.batch.service.PostTransaction#getDestinationName()
     */
    @Override
    public String getDestinationName() {
        return getPersistenceStructureService().getTableName(ExpenditureTransaction.class);
    }

    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

    public AccountingCycleCachingService getAccountingCycleCachingService() {
		return accountingCycleCachingService;
	}

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public PersistenceStructureService getPersistenceStructureService() {
		return persistenceStructureService;
	}

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	public ParameterEvaluatorService getParameterEvaluatorService() {
		return parameterEvaluatorService;
	}

	public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
		this.parameterEvaluatorService = parameterEvaluatorService;
	}

}
