/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingFrequency;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.*;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsBillingAwardVerificationService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.util.ObjectUtils;

public class ContractsGrantsBillingAwardVerificationServiceImpl implements ContractsGrantsBillingAwardVerificationService {
    protected AccountingPeriodService accountingPeriodService;
    protected BusinessObjectService businessObjectService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected CustomerService customerService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected KualiModuleService kualiModuleService;
    protected ParameterService parameterService;
    protected VerifyBillingFrequencyService verifyBillingFrequencyService;
    protected UniversityDateService universityDateService;
    protected OptionsService optionsService;

    /**
     * Check if Billing Frequency is set correctly.
     *
     * @param award
     * @return False if billing frequency code is blank, or set as predetermined billing schedule or milestone billing schedule
     *         and award has no award account or more than 1 award accounts assigned.
     */
    @Override
    public boolean isBillingFrequencySetCorrectly(ContractsAndGrantsBillingAward award) {

        if (StringUtils.isBlank(award.getBillingFrequencyCode()) || ((award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) || award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) && award.getActiveAwardAccounts().size() != 1)) {
            return false;
        }
        return true;
    }


    /**
     * Check if the value of the billing frequency code is in the BillingFrequency value set.
     *
     * @param award
     * @return
     */
    @Override
    public boolean isValueOfBillingFrequencyValid(ContractsAndGrantsBillingAward award) {
        if (!StringUtils.isBlank(award.getBillingFrequencyCode())) {
            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put(KFSPropertyConstants.FREQUENCY, award.getBillingFrequencyCode());
            criteria.put(KFSPropertyConstants.ACTIVE, true);
            Collection<ContractsAndGrantsBillingFrequency> matchingBillingFrequencies = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingFrequency.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingFrequency.class, criteria);

            if (matchingBillingFrequencies != null && matchingBillingFrequencies.size() > 0) {
                return true;
            }
        }

        return false;
    }



    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#isAwardFinalInvoiceAlreadyBuilt(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean isAwardFinalInvoiceAlreadyBuilt(ContractsAndGrantsBillingAward award) {
        for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
            if (awardAccount.isFinalBilledIndicator()) {
                return true;
            }
        }

        return false;
    }

    /**
     * this method checks If all accounts of award has invoices in progress.
     * @param award
     * @return
     */
    @Override
    public boolean isInvoiceInProgress(ContractsAndGrantsBillingAward award) {
        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, award.getProposalNumber());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, getFinancialSystemDocumentService().getPendingDocumentStatuses());

        return getBusinessObjectService().countMatching(ContractsGrantsInvoiceDocument.class, fieldValues) > 0;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoMilestonesToInvoice(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasMilestonesToInvoice(ContractsAndGrantsBillingAward award) {
        boolean hasMilestonesToInvoice = true;
        if (award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
            List<Milestone> milestones = new ArrayList<Milestone>();
            List<Milestone> validMilestones = new ArrayList<Milestone>();

            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
            map.put(KFSPropertyConstants.ACTIVE, true);
            milestones = (List<Milestone>) businessObjectService.findMatching(Milestone.class, map);

            // To retrieve the previous period end Date to check for milestones and billing schedule.

            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            BillingPeriod billingPeriod = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            java.sql.Date invoiceDate = billingPeriod.getEndDate();


            for (Milestone awdMilestone : milestones) {
                if (awdMilestone.getMilestoneActualCompletionDate() != null && !invoiceDate.before(awdMilestone.getMilestoneActualCompletionDate()) && !awdMilestone.isBilled() && awdMilestone.getMilestoneAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    validMilestones.add(awdMilestone);
                }
            }
            if (CollectionUtils.isEmpty(validMilestones)) {
                hasMilestonesToInvoice = false;
            }
        }
        return hasMilestonesToInvoice;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#hasNoBillsToInvoice(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean hasBillsToInvoice(ContractsAndGrantsBillingAward award) {
        boolean hasBillsToInvoice = true;
        if (award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {

            List<Bill> bills = new ArrayList<Bill>();
            List<Bill> validBills = new ArrayList<Bill>();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
            map.put(KFSPropertyConstants.ACTIVE, true);

            bills = (List<Bill>) businessObjectService.findMatching(Bill.class, map);
            // To retrieve the previous period end Date to check for milestones and billing schedule.

            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            BillingPeriod billingPeriod = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            java.sql.Date invoiceDate = billingPeriod.getEndDate();

            for (Bill awdBill : bills) {
                if (awdBill.getBillDate() != null && !invoiceDate.before(awdBill.getBillDate()) && !awdBill.isBilled() && awdBill.getEstimatedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                    validBills.add(awdBill);
                }
            }
            if (CollectionUtils.isEmpty(validBills)) {
                hasBillsToInvoice = false;
            }
        }
        return hasBillsToInvoice;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService#owningAgencyHasNoCustomerRecord(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward)
     */
    @Override
    public boolean owningAgencyHasCustomerRecord(ContractsAndGrantsBillingAward award) {
        boolean isValid = true;
        if (ObjectUtils.isNotNull(award.getAgency().getCustomerNumber())) {
            Customer customer = customerService.getByPrimaryKey(award.getAgency().getCustomerNumber());
            return !ObjectUtils.isNull(customer);
        }

        return false;
    }

    /**
     * This method checks if the System Information and ORganization Accounting Default are setup for the Chart Code and Org Code
     * from the award accounts.
     *
     * @param award
     * @return
     */
    @Override
    public boolean isChartAndOrgSetupForInvoicing(ContractsAndGrantsBillingAward award) {
        String coaCode = award.getPrimaryAwardOrganization().getChartOfAccountsCode();
        String orgCode = award.getPrimaryAwardOrganization().getOrganizationCode();
        String procCoaCode = null, procOrgCode = null;
        Integer currentYear = universityDateService.getCurrentFiscalYear();

        Map<String, Object> criteria = new HashMap<String, Object>();
        Map<String, Object> sysCriteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        sysCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);


        // To retrieve processing codes based on billing codes using organization options
        List<String> procCodes = getContractsGrantsInvoiceDocumentService().getProcessingFromBillingCodes(coaCode, orgCode);
        if (!CollectionUtils.isEmpty(procCodes) && procCodes.size() > 1) {

            sysCriteria.put(ArPropertyConstants.OrganizationAccountingDefaultFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, procCodes.get(0));
            sysCriteria.put(ArPropertyConstants.OrganizationAccountingDefaultFields.PROCESSING_ORGANIZATION_CODE, procCodes.get(1));
            OrganizationAccountingDefault organizationAccountingDefault = businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);

            SystemInformation systemInformation = businessObjectService.findByPrimaryKey(SystemInformation.class, sysCriteria);
            if (ObjectUtils.isNotNull(organizationAccountingDefault) && ObjectUtils.isNotNull(systemInformation)) {
                return true;
            }
        }
        return false;

    }

    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public VerifyBillingFrequencyService getVerifyBillingFrequencyService() {
        return verifyBillingFrequencyService;
    }

    public void setVerifyBillingFrequencyService(VerifyBillingFrequencyService verifyBillingFrequencyService) {
        this.verifyBillingFrequencyService = verifyBillingFrequencyService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
}
