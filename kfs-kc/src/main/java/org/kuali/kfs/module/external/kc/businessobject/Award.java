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

package org.kuali.kfs.module.external.kc.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableBillingFrequency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.integration.cg.ContractsAndGrantsOrganization;
import org.kuali.kfs.integration.cg.ContractsAndGrantsProjectDirector;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines a financial award object.
 */
public class Award implements ContractsAndGrantsBillingAward {
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";

    private String proposalNumber;
    private Long awardId;
    private String awardNumber;
    private String agencyNumber;
    private String primeAgencyNumber;
    private String awardTitle;
    private String grantNumber;
    private String cfdaNumber;

    private Proposal proposal;
    private Agency agency;
    private Agency primeAgency;
    private List<AwardAccount> awardAccounts;

    //BillingAward fields
    private Date awardBeginningDate;
    private Date awardEndingDate;
    private Date lastBilledDate;
    private KualiDecimal awardTotalAmount;
    private String awardAddendumNumber;
    private KualiDecimal awardAllocatedUniversityComputingServicesAmount;
    private KualiDecimal federalPassThroughFundedAmount;
    private Date awardEntryDate;
    private KualiDecimal agencyFuture1Amount;
    private KualiDecimal agencyFuture2Amount;
    private KualiDecimal agencyFuture3Amount;
    private String awardDocumentNumber;
    private Timestamp awardLastUpdateDate;
    private boolean federalPassThroughIndicator;
    private String oldProposalNumber;
    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private KualiDecimal federalFundedAmount;
    private Timestamp awardCreateTimestamp;
    private Date awardClosingDate;
    private String proposalAwardTypeCode;
    private String awardStatusCode;
    private String letterOfCreditFundCode;
    private String grantDescriptionCode;
    private String federalPassThroughAgencyNumber;
    private String agencyAnalystName;
    private String analystTelephoneNumber;
    private String billingFrequencyCode;
    private String awardProjectTitle;
    private String awardPurposeCode;
    private boolean active;
    private String kimGroupNames;
    private String routingOrg;
    private String routingChart;
    private boolean stateTransferIndicator;
    private boolean excludedFromInvoicing;
    private boolean additionalFormsRequiredIndicator;
    private String additionalFormsDescription;
    private String excludedFromInvoicingReason;
    private String instrumentTypeCode;
    private String invoicingOptionCode;
    private String invoicingOptionDescription;
    private KualiDecimal minInvoiceAmount;
    private boolean autoApproveIndicator;
    private String lookupPersonUniversalIdentifier;
    private Person lookupPerson;
    private String lookupFundMgrPersonUniversalIdentifier;
    private Person lookupFundMgrPerson;
    private String userLookupRoleNamespaceCode;
    private ContractsAndGrantsLetterOfCreditFund letterOfCreditFund;
    private String userLookupRoleName;
    private AwardFundManager awardPrimaryFundManager;
    private AccountsReceivableBillingFrequency billingFrequency;
    private ContractsAndGrantsProjectDirector awardPrimaryProjectDirector;
    private ContractsAndGrantsOrganization primaryAwardOrganization;
    private Date fundingExpirationDate;
    private String dunningCampaign;
    private boolean stopWorkIndicator;
    private String stopWorkReason;

    private Integer sequenceNumber;
    private String sequenceStatus;

    /**
     * Default no-args constructor.
     */
    public Award() {
        awardAccounts = new ArrayList<AwardAccount>();
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber
     */
    @Override
    public String getProposalNumber() {
        return proposalNumber;
    }

    @Override
    public String getObjectId() {
        return proposalNumber.toString();
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * @return a String to represent this field on the inquiry
     */
    @Override
    public String getAwardInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(AWARD_INQUIRY_TITLE_PROPERTY);
    }


    @Override
    public Proposal getProposal() {
        return proposal;
    }


    public void prepareForWorkflow() {
    }


    @Override
    public void refresh() {
    }


    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }


    @Override
    public Agency getAgency() {
        return agency;
    }


    public void setAgency(Agency agency) {
        this.agency = agency;
    }


    /**
     * Gets the awardAccounts list.
     *
     * @return Returns the awardAccounts.
     */
    public List<AwardAccount> getAwardAccounts() {
        return awardAccounts;
    }

    /**
     * Sets the awardAccounts list.
     *
     * @param awardAccounts The awardAccounts to set.
     */
    public void setAwardAccounts(List<AwardAccount> awardAccounts) {
        this.awardAccounts = awardAccounts;
    }

    @Override
    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getAwardTitle() {
        return awardTitle;
    }

    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }

    public String getPrimeAgencyNumber() {
        return primeAgencyNumber;
    }

    public void setPrimeAgencyNumber(String primeAgencyNumber) {
        this.primeAgencyNumber = primeAgencyNumber;
    }

    public Agency getPrimeAgency() {
        return primeAgency;
    }

    public void setPrimeAgency(Agency primeAgency) {
        this.primeAgency = primeAgency;
    }

    public Long getAwardId() {
        return awardId;
    }

    public void setAwardId(Long awardId) {
        this.awardId = awardId;
    }

    public String getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }


    /**
     * Gets the cfdaNumber attribute.
     *
     * @return Returns the cfdaNumber
     */
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute.
     *
     * @param cfdaNumber The cfdaNumber to set.
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    @Override
    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }

    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }

    @Override
    public Date getAwardEndingDate() {
        return awardEndingDate;
    }

    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }

    @Override
    public Date getLastBilledDate() {
        return lastBilledDate;
    }

    public void setLastBilledDate(Date lastBilledDate) {
        this.lastBilledDate = lastBilledDate;
    }

    @Override
    public KualiDecimal getAwardTotalAmount() {
        return awardTotalAmount;
    }

    public void setAwardTotalAmount(KualiDecimal awardTotalAmount) {
        this.awardTotalAmount = awardTotalAmount;
    }

    @Override
    public String getAwardAddendumNumber() {
        return awardAddendumNumber;
    }

    public void setAwardAddendumNumber(String awardAddendumNumber) {
        this.awardAddendumNumber = awardAddendumNumber;
    }

    @Override
    public KualiDecimal getAwardAllocatedUniversityComputingServicesAmount() {
        return awardAllocatedUniversityComputingServicesAmount;
    }

    public void setAwardAllocatedUniversityComputingServicesAmount(KualiDecimal awardAllocatedUniversityComputingServicesAmount) {
        this.awardAllocatedUniversityComputingServicesAmount = awardAllocatedUniversityComputingServicesAmount;
    }

    @Override
    public KualiDecimal getFederalPassThroughFundedAmount() {
        return federalPassThroughFundedAmount;
    }

    public void setFederalPassThroughFundedAmount(KualiDecimal federalPassThroughFundedAmount) {
        this.federalPassThroughFundedAmount = federalPassThroughFundedAmount;
    }

    @Override
    public Date getAwardEntryDate() {
        return awardEntryDate;
    }

    public void setAwardEntryDate(Date awardEntryDate) {
        this.awardEntryDate = awardEntryDate;
    }

    @Override
    public KualiDecimal getAgencyFuture1Amount() {
        return agencyFuture1Amount;
    }

    public void setAgencyFuture1Amount(KualiDecimal agencyFuture1Amount) {
        this.agencyFuture1Amount = agencyFuture1Amount;
    }

    @Override
    public KualiDecimal getAgencyFuture2Amount() {
        return agencyFuture2Amount;
    }

    public void setAgencyFuture2Amount(KualiDecimal agencyFuture2Amount) {
        this.agencyFuture2Amount = agencyFuture2Amount;
    }

    @Override
    public KualiDecimal getAgencyFuture3Amount() {
        return agencyFuture3Amount;
    }

    public void setAgencyFuture3Amount(KualiDecimal agencyFuture3Amount) {
        this.agencyFuture3Amount = agencyFuture3Amount;
    }

    @Override
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }

    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }

    @Override
    public Timestamp getAwardLastUpdateDate() {
        return awardLastUpdateDate;
    }

    public void setAwardLastUpdateDate(Timestamp awardLastUpdateDate) {
        this.awardLastUpdateDate = awardLastUpdateDate;
    }

    public boolean isFederalPassThroughIndicator() {
        return federalPassThroughIndicator;
    }

    @Override
    public boolean getFederalPassThroughIndicator() {
        return federalPassThroughIndicator;
    }

    public void setFederalPassThroughIndicator(boolean federalPassThroughIndicator) {
        this.federalPassThroughIndicator = federalPassThroughIndicator;
    }

    @Override
    public String getOldProposalNumber() {
        return oldProposalNumber;
    }

    public void setOldProposalNumber(String oldProposalNumber) {
        this.oldProposalNumber = oldProposalNumber;
    }

    @Override
    public KualiDecimal getAwardDirectCostAmount() {
        return awardDirectCostAmount;
    }

    public void setAwardDirectCostAmount(KualiDecimal awardDirectCostAmount) {
        this.awardDirectCostAmount = awardDirectCostAmount;
    }

    @Override
    public KualiDecimal getAwardIndirectCostAmount() {
        return awardIndirectCostAmount;
    }

    public void setAwardIndirectCostAmount(KualiDecimal awardIndirectCostAmount) {
        this.awardIndirectCostAmount = awardIndirectCostAmount;
    }

    @Override
    public KualiDecimal getFederalFundedAmount() {
        return federalFundedAmount;
    }

    public void setFederalFundedAmount(KualiDecimal federalFundedAmount) {
        this.federalFundedAmount = federalFundedAmount;
    }

    @Override
    public Timestamp getAwardCreateTimestamp() {
        return awardCreateTimestamp;
    }

    public void setAwardCreateTimestamp(Timestamp awardCreateTimestamp) {
        this.awardCreateTimestamp = awardCreateTimestamp;
    }

    @Override
    public Date getAwardClosingDate() {
        return awardClosingDate;
    }

    public void setAwardClosingDate(Date awardClosingDate) {
        this.awardClosingDate = awardClosingDate;
    }

    @Override
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    @Override
    public String getAwardStatusCode() {
        return awardStatusCode;
    }

    public void setAwardStatusCode(String awardStatusCode) {
        this.awardStatusCode = awardStatusCode;
    }

    @Override
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    @Override
    public String getGrantDescriptionCode() {
        return grantDescriptionCode;
    }

    public void setGrantDescriptionCode(String grantDescriptionCode) {
        this.grantDescriptionCode = grantDescriptionCode;
    }

    @Override
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }

    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }

    @Override
    public String getAgencyAnalystName() {
        return agencyAnalystName;
    }

    public void setAgencyAnalystName(String agencyAnalystName) {
        this.agencyAnalystName = agencyAnalystName;
    }

    @Override
    public String getAnalystTelephoneNumber() {
        return analystTelephoneNumber;
    }

    public void setAnalystTelephoneNumber(String analystTelephoneNumber) {
        this.analystTelephoneNumber = analystTelephoneNumber;
    }

    @Override
    public String getBillingFrequencyCode() {
        return billingFrequencyCode;
    }

    public void setBillingFrequencyCode(String billingFrequencyCode) {
        this.billingFrequencyCode = billingFrequencyCode;
    }


    @Override
    public String getAwardProjectTitle() {
        return awardProjectTitle;
    }

    public void setAwardProjectTitle(String awardProjectTitle) {
        this.awardProjectTitle = awardProjectTitle;
    }

    @Override
    public String getAwardPurposeCode() {
        return awardPurposeCode;
    }

    public void setAwardPurposeCode(String awardPurposeCode) {
        this.awardPurposeCode = awardPurposeCode;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getKimGroupNames() {
        return kimGroupNames;
    }

    public void setKimGroupNames(String kimGroupNames) {
        this.kimGroupNames = kimGroupNames;
    }

    @Override
    public String getRoutingOrg() {
        return routingOrg;
    }

    public void setRoutingOrg(String routingOrg) {
        this.routingOrg = routingOrg;
    }

    @Override
    public String getRoutingChart() {
        return routingChart;
    }

    public void setRoutingChart(String routingChart) {
        this.routingChart = routingChart;
    }

    @Override
    public boolean isAdditionalFormsRequiredIndicator() {
        return additionalFormsRequiredIndicator;
    }

    public void setAdditionalFormsRequiredIndicator(boolean additionalFormsRequiredIndicator) {
        this.additionalFormsRequiredIndicator = additionalFormsRequiredIndicator;
    }

    @Override
    public String getAdditionalFormsDescription() {
        return additionalFormsDescription;
    }

    public void setAdditionalFormsDescription(String additionalFormsDescription) {
        this.additionalFormsDescription = additionalFormsDescription;
    }

    @Override
    public String getExcludedFromInvoicingReason() {
        return excludedFromInvoicingReason;
    }

    public void setExcludedFromInvoicingReason(String excludedFromInvoicingReason) {
        this.excludedFromInvoicingReason = excludedFromInvoicingReason;
    }

    @Override
    public String getInstrumentTypeCode() {
        return instrumentTypeCode;
    }

    public void setInstrumentTypeCode(String instrumentTypeCode) {
        this.instrumentTypeCode = instrumentTypeCode;
    }

    @Override
    public String getInvoicingOptionCode() {
        return invoicingOptionCode;
    }

    public void setInvoicingOptionCode(String invoicingOptionCode) {
        this.invoicingOptionCode = invoicingOptionCode;
    }

    @Override
    public KualiDecimal getMinInvoiceAmount() {
        return minInvoiceAmount;
    }

    public void setMinInvoiceAmount(KualiDecimal minInvoiceAmount) {
        this.minInvoiceAmount = minInvoiceAmount;
    }

    public boolean isAutoApproveIndicator() {
        return autoApproveIndicator;
    }

    @Override
    public boolean getAutoApproveIndicator() {
        return autoApproveIndicator;
    }

    public void setAutoApproveIndicator(boolean autoApproveIndicator) {
        this.autoApproveIndicator = autoApproveIndicator;
    }

    @Override
    public String getLookupPersonUniversalIdentifier() {
        return lookupPersonUniversalIdentifier;
    }

    public void setLookupPersonUniversalIdentifier(String lookupPersonUniversalIdentifier) {
        this.lookupPersonUniversalIdentifier = lookupPersonUniversalIdentifier;
    }

    @Override
    public Person getLookupPerson() {
        return lookupPerson;
    }

    public void setLookupPerson(Person lookupPerson) {
        this.lookupPerson = lookupPerson;
    }

    @Override
    public String getLookupFundMgrPersonUniversalIdentifier() {
        return lookupFundMgrPersonUniversalIdentifier;
    }

    public void setLookupFundMgrPersonUniversalIdentifier(String lookupFundMgrPersonUniversalIdentifier) {
        this.lookupFundMgrPersonUniversalIdentifier = lookupFundMgrPersonUniversalIdentifier;
    }

    @Override
    public Person getLookupFundMgrPerson() {
        return lookupFundMgrPerson;
    }

    public void setLookupFundMgrPerson(Person lookupFundMgrPerson) {
        this.lookupFundMgrPerson = lookupFundMgrPerson;
    }

    @Override
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    public void setUserLookupRoleNamespaceCode(String userLookupRoleNamespaceCode) {
        this.userLookupRoleNamespaceCode = userLookupRoleNamespaceCode;
    }

    @Override
    public ContractsAndGrantsLetterOfCreditFund getLetterOfCreditFund() {
        return letterOfCreditFund;
    }

    @Override
    public void setLetterOfCreditFund(ContractsAndGrantsLetterOfCreditFund letterOfCreditFund) {
        this.letterOfCreditFund = letterOfCreditFund;
    }

    @Override
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    public void setUserLookupRoleName(String userLookupRoleName) {
        this.userLookupRoleName = userLookupRoleName;
    }

    @Override
    public AwardFundManager getAwardPrimaryFundManager() {
        return awardPrimaryFundManager;
    }

    public void setAwardPrimaryFundManager(AwardFundManager awardPrimaryFundManager) {
        this.awardPrimaryFundManager = awardPrimaryFundManager;
    }

    @Override
    public AccountsReceivableBillingFrequency getBillingFrequency() {
        if (billingFrequency == null || !StringUtils.equals(billingFrequency.getFrequency(), billingFrequencyCode)) {
            billingFrequency = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(AccountsReceivableBillingFrequency.class).retrieveExternalizableBusinessObjectIfNecessary(this, billingFrequency, KcConstants.BILLING_FREQUENCY);
        }
        return billingFrequency;
    }

    public void setBillingFrequency(AccountsReceivableBillingFrequency billingFrequency) {
        this.billingFrequency = billingFrequency;
    }

    @Override
    public ContractsAndGrantsProjectDirector getAwardPrimaryProjectDirector() {
        return awardPrimaryProjectDirector;
    }

    public void setAwardPrimaryProjectDirector(ContractsAndGrantsProjectDirector awardPrimaryProjectDirector) {
        this.awardPrimaryProjectDirector = awardPrimaryProjectDirector;
    }

    @Override
    public ContractsAndGrantsOrganization getPrimaryAwardOrganization() {
        return primaryAwardOrganization;
    }

    public void setPrimaryAwardOrganization(ContractsAndGrantsOrganization primaryAwardOrganization) {
        this.primaryAwardOrganization = primaryAwardOrganization;
    }

    @Override
    public Date getFundingExpirationDate() {
        return fundingExpirationDate;
    }

    public void setFundingExpirationDate(Date fundingExpirationDate) {
        this.fundingExpirationDate = fundingExpirationDate;
    }

    @Override
    public String getDunningCampaign() {
        return dunningCampaign;
    }

    public void setDunningCampaign(String dunningCampaign) {
        this.dunningCampaign = dunningCampaign;
    }

    @Override
    public boolean isStopWorkIndicator() {
        return stopWorkIndicator;
    }

    public void setStopWorkIndicator(boolean stopWorkIndicator) {
        this.stopWorkIndicator = stopWorkIndicator;
    }

    @Override
    public String getStopWorkReason() {
        return stopWorkReason;
    }

    public void setStopWorkReason(String stopWorkReason) {
        this.stopWorkReason = stopWorkReason;
    }

    @Override
    public List<ContractsAndGrantsBillingAwardAccount> getActiveAwardAccounts() {
        List<ContractsAndGrantsBillingAwardAccount> activeAwardAccounts = new ArrayList<>();
        for (AwardAccount awardAccount : awardAccounts) {
            if (awardAccount.isActive()) {
                activeAwardAccounts.add(awardAccount);
            }
        }
        return activeAwardAccounts;
    }

    @Override
    public boolean isStateTransferIndicator() {
        return stateTransferIndicator;
    }

    public void setStateTransferIndicator(boolean stateTransferIndicator) {
        this.stateTransferIndicator = stateTransferIndicator;
    }

    @Override
    public boolean isExcludedFromInvoicing() {
        return excludedFromInvoicing;
    }

    public void setExcludedFromInvoicing(boolean excludedFromInvoicing) {
        this.excludedFromInvoicing = excludedFromInvoicing;
    }

    @Override
    public String getInvoicingOptionDescription() {
        return invoicingOptionDescription;
    }

    public void setInvoicingOptionDescription(String invoicingOptionDescription) {
        this.invoicingOptionDescription = invoicingOptionDescription;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getSequenceStatus() {
        return sequenceStatus;
    }

    public void setSequenceStatus(String sequenceStatus) {
        this.sequenceStatus = sequenceStatus;
    }

    public String getAwardNumber() {
        return awardNumber;
    }

    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }
}

