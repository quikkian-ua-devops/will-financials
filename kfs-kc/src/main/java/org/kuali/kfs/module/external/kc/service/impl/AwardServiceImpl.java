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
package org.kuali.kfs.module.external.kc.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.businessobject.Agency;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.module.external.kc.businessobject.AwardAccount;
import org.kuali.kfs.module.external.kc.businessobject.AwardFundManager;
import org.kuali.kfs.module.external.kc.businessobject.AwardOrganization;
import org.kuali.kfs.module.external.kc.businessobject.AwardProjectDirector;
import org.kuali.kfs.module.external.kc.businessobject.BillingFrequency;
import org.kuali.kfs.module.external.kc.businessobject.LetterOfCreditFund;
import org.kuali.kfs.module.external.kc.businessobject.Proposal;
import org.kuali.kfs.module.external.kc.dto.AwardAccountDTO;
import org.kuali.kfs.module.external.kc.dto.AwardDTO;
import org.kuali.kfs.module.external.kc.dto.AwardFieldValuesDto;
import org.kuali.kfs.module.external.kc.dto.AwardSearchCriteriaDto;
import org.kuali.kfs.module.external.kc.service.AccountDefaultsService;
import org.kuali.kfs.module.external.kc.service.BillingFrequencyService;
import org.kuali.kfs.module.external.kc.service.ExternalizableLookupableBusinessObjectService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.webService.AwardWebSoapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kra.external.award.AwardWebService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

import javax.xml.ws.WebServiceException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 05:29:28 HST 2010
 * Generated source version: 2.2.10
 *
 */

public class AwardServiceImpl implements ExternalizableLookupableBusinessObjectService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardServiceImpl.class);

    protected AccountDefaultsService accountDefaultsService;
    protected BillingFrequencyService billingFrequencyService;
    protected ConfigurationService configurationService;
    protected ParameterService parameterService;
    protected PersonService personService;
    protected BusinessObjectService businessObjectService;

    protected AwardWebService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        AwardWebService awardWebService = (AwardWebService) GlobalResourceLoader.getService(KcConstants.Award.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (awardWebService == null) {
            LOG.warn("Couldn't get AwardWebService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            AwardWebSoapService ss =  null;
            try {
                ss = new AwardWebSoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize AwardWebSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize AwardWebSoapService: " + ex.getMessage());
            }
            awardWebService = ss.getAwardWebServicePort();
        }

        return awardWebService;
    }

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        //use the proposal number as its the awardId on the KC side.
        Long proposalNumber;
        if (primaryKeys.get("proposalNumber") instanceof String) {
            proposalNumber = Long.parseLong((String)primaryKeys.get("proposalNumber"));
        } else {
            proposalNumber = (Long)primaryKeys.get("proposalNumber");
        }
        AwardDTO dto  = this.getWebService().getAward(proposalNumber);
        return awardFromDTO(dto);
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        List<AwardDTO> result = null;
        AwardFieldValuesDto criteria = new AwardFieldValuesDto();
        Object awardId = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        if (awardId instanceof String) {
            criteria.setAwardId(Long.parseLong((String) awardId));
        } else {
            criteria.setAwardId((Long) awardId);
        }
        criteria.setAwardNumber((String) fieldValues.get("awardNumber"));
        criteria.setChartOfAccounts((String) fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        criteria.setAccountNumber((String) fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER));
        criteria.setPrincipalInvestigatorId((String) fieldValues.get(KimConstants.AttributeConstants.PRINCIPAL_ID));
        criteria.setFundManagerId((String) fieldValues.get(KcConstants.FUND_MANAGER_ID));

        try {
          result  = this.getWebService().getMatchingAwards(criteria);
        } catch (WebServiceException ex) {
            LOG.error(KcConstants.WEBSERVICE_UNREACHABLE, ex);
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, getConfigurationService().getPropertyValueAsString(KFSConstants.KC_APPLICATION_URL_KEY));
        }

        if (result == null) {
            return new ArrayList();
        } else {
            List<Award> awards = new ArrayList<Award>();
            for (AwardDTO dto : result) {
                awards.add(awardFromDTO(dto));
            }
            return awards;
        }
    }

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<AwardDTO> result = null;
        AwardSearchCriteriaDto criteria = new AwardSearchCriteriaDto();
        criteria.setAwardId(fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER));
        criteria.setAwardNumber(fieldValues.get("awardNumber"));
        criteria.setChartOfAccounts(fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        criteria.setAccountNumber(fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER));
        criteria.setPrincipalInvestigatorId(fieldValues.get("principalId"));
        criteria.setSponsorCode(fieldValues.get("agencyNumber"));
        //the below should only be passed in from the lookup framework, meaning they will all be strings
        criteria.setStartDate(fieldValues.get("awardBeginningDate"));
        criteria.setStartDateLowerBound(fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate"));
        criteria.setEndDate(fieldValues.get("awardEndingDate"));
        criteria.setEndDateLowerBound(fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate"));
        criteria.setBillingFrequency(fieldValues.get("awardBillingFrequency"));
        criteria.setAwardTotal(fieldValues.get("awardTotal"));
        try {
          result  = this.getWebService().searchAwards(criteria);
        } catch (WebServiceException ex) {
            GlobalVariablesExtractHelper.insertError(KcConstants.WEBSERVICE_UNREACHABLE, getConfigurationService().getPropertyValueAsString(KFSConstants.KC_APPLICATION_URL_KEY));
        }

        if (result == null) {
            return new ArrayList();
        } else {
            List<Award> awards = new ArrayList<Award>();
            for (AwardDTO dto : result) {
                awards.add(awardFromDTO(dto));
            }
            return awards;
        }
    }

    protected Award awardFromDTO(AwardDTO kcAward) {
        Award award = new Award();
        award.setProposalNumber(kcAward.getAwardId());
        award.setAwardNumber(kcAward.getAwardNumber());
        award.setAwardBeginningDate(kcAward.getAwardStartDate() == null ? null : new java.sql.Date(kcAward.getAwardStartDate().getDate()));
        award.setAwardEndingDate(kcAward.getAwardEndDate() == null ? null : new java.sql.Date(kcAward.getAwardEndDate().getDate()));
        award.setAwardTotalAmount(new KualiDecimal(kcAward.getAwardTotalAmount()));
        award.setAwardDirectCostAmount(new KualiDecimal(kcAward.getAwardDirectCostAmount()));
        award.setAwardIndirectCostAmount(new KualiDecimal(kcAward.getAwardIndirectCostAmount()));
        award.setAwardDocumentNumber(kcAward.getAwardDocumentNumber());
        award.setAwardLastUpdateDate(kcAward.getAwardLastUpdateDate() == null ? null : new java.sql.Timestamp(kcAward.getAwardLastUpdateDate().getDate()));
        award.setAwardCreateTimestamp(kcAward.getAwardCreateTimestamp() == null ? null : new java.sql.Timestamp(kcAward.getAwardCreateTimestamp().getDate()));
        award.setProposalAwardTypeCode(kcAward.getProposalAwardTypeCode());
        award.setAwardStatusCode(kcAward.getAwardStatusCode());
        award.setActive(kcAward.isActive());
        award.setAgencyNumber(kcAward.getSponsorCode());
        award.setAwardTitle(kcAward.getTitle());
        award.setAgency(new Agency(kcAward.getSponsor()));
        if (kcAward.getProposal() != null) {
            award.setProposal(new Proposal(kcAward.getProposal()));
            award.getProposal().setAward(award);
        }
        award.setAdditionalFormsRequiredIndicator(kcAward.isAdditionalFormsRequired());
        award.setAutoApproveIndicator(kcAward.isAutoApproveInvoice());
        award.setMinInvoiceAmount(new KualiDecimal(kcAward.getMinInvoiceAmount()));
        award.setAdditionalFormsDescription(kcAward.getAdditionalFormsDescription());
        award.setStopWorkIndicator(kcAward.isStopWork());
        award.setStopWorkReason(kcAward.getStopWorkReason());
        award.setInvoicingOptionCode(kcAward.getInvoicingOption());
        award.setInvoicingOptionDescription(kcAward.getInvoicingOptionDescription());
        award.setDunningCampaign(kcAward.getDunningCampaignId());
        if (StringUtils.isNotEmpty(kcAward.getFundManagerId())) {
            award.setAwardPrimaryFundManager(new AwardFundManager(award.getProposalNumber(), kcAward.getFundManagerId()));
        }
        AccountAutoCreateDefaults defaults = getAccountDefaultsService().getAccountDefaults(kcAward.getUnitNumber());
        if (defaults != null) {
            AwardOrganization awardOrg = new AwardOrganization();
            awardOrg.setActive(true);
            awardOrg.setAwardPrimaryOrganizationIndicator(true);
            awardOrg.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
            awardOrg.setChartOfAccounts(defaults.getChartOfAccounts());
            awardOrg.setOrganization(defaults.getOrganization());
            awardOrg.setOrganizationCode(defaults.getOrganizationCode());
            awardOrg.setProposalNumber(award.getProposalNumber());
            award.setPrimaryAwardOrganization(awardOrg);
        }
        if (kcAward.getMethodOfPayment() != null) {
            award.setLetterOfCreditFundCode(kcAward.getMethodOfPayment().getMethodOfPaymentCode());
            award.setLetterOfCreditFund(new LetterOfCreditFund(kcAward.getMethodOfPayment().getMethodOfPaymentCode(), kcAward.getMethodOfPayment().getDescription()));
        }
        BillingFrequency billingFrequency = getBillingFrequencyService().createBillingFrequency(kcAward.getInvoiceBillingFrequency());
        award.setBillingFrequency(billingFrequency);
        award.setBillingFrequencyCode(billingFrequency.getKcFrequencyCode());
        award.setAwardPrimaryProjectDirector(getProjectDirector(kcAward));
        award.setExcludedFromInvoicing(kcAward.isExcludedFromInvoicing());
        award.setExcludedFromInvoicingReason(kcAward.getExcludedFromInvoicingReason());
        award.setSequenceNumber(kcAward.getSequenceNumber());
        award.setSequenceStatus(kcAward.getSequenceStatus());
        award.setAwardAccounts(awardAccountsFromAwardAccountDTOs(kcAward.getAwardAccounts()));
        return award;
    }

    protected List<AwardAccount> awardAccountsFromAwardAccountDTOs(List<AwardAccountDTO> awardAccountDTOs) {
        List<AwardAccount> awardAccounts = new ArrayList<>();

        for(AwardAccountDTO awardAccountDTO : awardAccountDTOs){
            if(StringUtils.isEmpty(awardAccountDTO.getErrorMessage())){
                AwardAccount awardAccount = new AwardAccount(awardAccountDTO);
                Account account = businessObjectService.findByPrimaryKey(Account.class, awardAccount.getPrimaryKeys());
                awardAccount.setAccount(account);
                awardAccounts.add(awardAccount);
            }
        }

        return awardAccounts;
    }

    protected AwardProjectDirector getProjectDirector(AwardDTO kcAward) {
        AwardProjectDirector director = new AwardProjectDirector();
        director.setPrincipalId(kcAward.getPrincipalInvestigatorId());
        director.setProjectDirector(getPersonService().getPerson(kcAward.getPrincipalInvestigatorId()));
        director.setProposalNumber(kcAward.getAwardId());
        return director;
    }

    protected AccountDefaultsService getAccountDefaultsService() {
        return accountDefaultsService;
    }

    public void setAccountDefaultsService(AccountDefaultsService accountDefaultsService) {
        this.accountDefaultsService = accountDefaultsService;
    }

    protected ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected BillingFrequencyService getBillingFrequencyService() {
        return billingFrequencyService;
    }

    public void setBillingFrequencyService(BillingFrequencyService billingFrequencyService) {
        this.billingFrequencyService = billingFrequencyService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
