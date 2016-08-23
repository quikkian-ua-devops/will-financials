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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.krad.bo.TransientBusinessObjectBase;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Business object for the Generate Dunning Letters Lookup Result.
 */
public class GenerateDunningLettersLookupResult extends TransientBusinessObjectBase {

    private String principalId;
    private String proposalNumber;
    private String accountNumber;
    private String agencyNumber;
    private String customerNumber;
    private String invoiceDocumentNumber;
    private String campaignID;
    private String agingBucket;
    private KualiDecimal awardTotal = KualiDecimal.ZERO;
    private Collection<ContractsGrantsInvoiceDocument> invoices;
    private ContractsAndGrantsAward award;
    private ContractsAndGrantsAgency agency;
    private Customer customer;
    private String chartOfAccountsCode;
    private String organizationCode;
    private String reportOption = ArConstants.ReportOptionFieldValues.PROCESSING_ORG;

    private Person collector;
    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;

    private DunningCampaign dunningCampaign;

    /**
     * Gets the principalId attribute.
     *
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }


    /**
     * Sets the principalId attribute value.
     *
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the collector attribute.
     *
     * @return Returns the collector.
     */
    public Person getCollector() {
        collector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, collector);
        return collector;
    }


    /**
     * Sets the collector attribute value.
     *
     * @param collector The collector to set.
     */
    public void setCollector(Person collector) {
        this.collector = collector;
    }


    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     *
     * @return Returns the userLookupRoleNamespaceCode.
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }


    /**
     * Gets the userLookupRoleName attribute.
     *
     * @return Returns the userLookupRoleName.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }


    /**
     * Gets the agingBucket attribute.
     *
     * @return Returns the agingBucket.
     */
    public String getAgingBucket() {
        return agingBucket;
    }


    /**
     * Sets the agingBucket attribute value.
     *
     * @param agingBucket The agingBucket to set.
     */
    public void setAgingBucket(String agingBucket) {
        this.agingBucket = agingBucket;
    }


    /**
     * Gets the campaignID attribute.
     *
     * @return Returns the campaignID.
     */
    public String getCampaignID() {
        return campaignID;
    }


    /**
     * Sets the campaignID attribute value.
     *
     * @param campaignID The campaignID to set.
     */
    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }


    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public String getProposalNumber() {
        return proposalNumber;
    }


    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * /** Gets the awardTotal attribute.
     *
     * @return Returns the awardTotal.
     */
    public KualiDecimal getAwardTotal() {
        return awardTotal;
    }


    /**
     * Sets the awardTotal attribute value.
     *
     * @param awardTotal The awardTotal to set.
     */
    public void setAwardTotal(KualiDecimal awardTotal) {
        this.awardTotal = awardTotal;
    }


    /**
     * Gets the invoices attribute.
     *
     * @return Returns the invoices.
     */
    public Collection<ContractsGrantsInvoiceDocument> getInvoices() {
        return invoices;
    }


    /**
     * Sets the invoices attribute value.
     *
     * @param invoices The invoices to set.
     */
    public void setInvoices(Collection<ContractsGrantsInvoiceDocument> invoices) {
        this.invoices = invoices;
    }


    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }


    /**
     * Sets the agencyNumber attribute value.
     *
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }


    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }


    /**
     * Sets the customerNumber attribute value.
     *
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    /**
     * Gets the invoiceDocumentNumber attribute.
     *
     * @return Returns the invoiceDocumentNumber.
     */
    public String getInvoiceDocumentNumber() {
        return invoiceDocumentNumber;
    }


    /**
     * Sets the invoiceDocumentNumber attribute value.
     *
     * @param invoiceDocumentNumber The invoiceDocumentNumber to set.
     */
    public void setInvoiceDocumentNumber(String invoiceDocumentNumber) {
        this.invoiceDocumentNumber = invoiceDocumentNumber;
    }


    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsAward getAward() {
        return award;
    }


    /**
     * Sets the award attribute value.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsAward award) {
        this.award = award;
    }


    /**
     * Gets the agency attribute.
     *
     * @return Returns the agency.
     */
    public ContractsAndGrantsAgency getAgency() {
        return agency;
    }


    /**
     * Sets the agency attribute value.
     *
     * @param agency The agency to set.
     */
    public void setAgency(ContractsAndGrantsAgency agency) {
        this.agency = agency;
    }


    /**
     * Gets the customer attribute.
     *
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }


    /**
     * Sets the customer attribute value.
     *
     * @param customer The customer to set.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber attribute value.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }



    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public String getOrganizationCode() {
        return organizationCode;
    }


    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    public String getReportOption() {
        return reportOption;
    }


    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

    public DunningCampaign getDunningCampaign() {
        return dunningCampaign;
    }

    public void setDunningCampaign(DunningCampaign dunningCampaign) {
        this.dunningCampaign = dunningCampaign;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        m.put(KFSPropertyConstants.CUSTOMER_NUMBER, this.customerNumber);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.invoiceDocumentNumber);


        return m;
    }

    public List<String> getInvoiceAttributesForDisplay() {
        List<String> invoiceAttributesForDisplay = new ArrayList<String>();
        invoiceAttributesForDisplay.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        invoiceAttributesForDisplay.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.AGE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.DunningLetterDistributionFields.DUNNING_LETTER_TEMPLATE_SENT_DATE);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.SOURCE_TOTAL);
        invoiceAttributesForDisplay.add(ArPropertyConstants.CustomerInvoiceDocumentFields.OPEN_AMOUNT);
        return invoiceAttributesForDisplay;
    }

}
