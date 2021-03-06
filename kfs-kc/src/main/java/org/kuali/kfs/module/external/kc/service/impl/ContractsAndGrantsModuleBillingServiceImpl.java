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
package org.kuali.kfs.module.external.kc.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.module.external.kc.service.ExternalizableLookupableBusinessObjectService;
import org.kuali.kfs.module.external.kc.webService.AwardWebSoapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kra.external.award.AwardBillingUpdateDto;
import org.kuali.kra.external.award.AwardBillingUpdateStatusDto;
import org.kuali.kra.external.award.AwardFieldValuesDto;
import org.kuali.kra.external.award.AwardWebService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

import java.net.MalformedURLException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of Contracts & Grants module billing service which will allow AR to utilize KC functionality to perform CGB actions.
 */
public class ContractsAndGrantsModuleBillingServiceImpl implements ContractsAndGrantsModuleBillingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleBillingServiceImpl.class);

    protected ExternalizableLookupableBusinessObjectService awardService;

    @Override
    public List<? extends ContractsAndGrantsAward> lookupAwards(Map<String, String> fieldValues, boolean unbounded) {
        return (List<Award>) getAwardService().getSearchResults(fieldValues);
    }

    @Override
    public ContractsAndGrantsBillingAward updateAwardIfNecessary(String proposalNumber, ContractsAndGrantsBillingAward currentAward) {
        ContractsAndGrantsBillingAward award = currentAward;

        if (ObjectUtils.isNull(proposalNumber)) {
            award = null;
        } else {
            if (ObjectUtils.isNull(currentAward) || !StringUtils.equals(currentAward.getProposalNumber(), proposalNumber)) {
                Map<String, String> criteria = new HashMap<>();
                criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
                List<Award> awards = (List<Award>) awardService.findMatching(criteria);
                if (awards.size() > 0) {
                    award = awards.get(0);
                }
            }
        }
        return award;
    }

    public ExternalizableLookupableBusinessObjectService getAwardService() {
        return awardService;
    }

    public void setAwardService(ExternalizableLookupableBusinessObjectService awardService) {
        this.awardService = awardService;
    }

    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean invoiceReversal, Date lastBilledDate) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();

        if (invoiceReversal) {
            // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
            updateDto.setRestorePreviousBillDate(true);
        } else {
            updateDto.setDoLastBillDateUpdate(true);
            updateDto.setLastBillDate(lastBilledDate);
        }

        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));
    }

    protected AwardFieldValuesDto buildSearchDto(Map<String, Object> criteria) {
        AwardFieldValuesDto dto = new AwardFieldValuesDto();
        dto.setAccountNumber((String) criteria.get(KFSPropertyConstants.ACCOUNT_NUMBER));
        dto.setChartOfAccounts((String) criteria.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        dto.setAwardNumber((String) criteria.get(KFSPropertyConstants.PROPOSAL_NUMBER));
        return dto;

    }

    @Override
    public void setLastBilledDateToAward(String proposalNumber, Date lastBilledDate) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoLastBillDateUpdate(true);
        updateDto.setLastBillDate(lastBilledDate);
        AwardFieldValuesDto searchDto = new AwardFieldValuesDto();
        searchDto.setAwardNumber(proposalNumber);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(searchDto, updateDto));
    }

    @Override
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoFinalBilledUpdate(true);
        updateDto.setFinalBilledIndicator(finalBilled);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));
    }

    public AwardWebService getAwardWebService() {
        return getWebService();
    }


    @Override
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, boolean invoiceReversal, Date lastBilledDate) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoFinalBilledUpdate(true);
        updateDto.setFinalBilledIndicator(finalBilled);

        if (invoiceReversal) {
            // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
            updateDto.setRestorePreviousBillDate(true);
        } else {
            updateDto.setDoLastBillDateUpdate(true);
            updateDto.setLastBillDate(lastBilledDate);
        }

        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(mapKey), updateDto));
    }

    protected void handleBillingStatusResult(AwardBillingUpdateStatusDto dto) {
        if (!dto.isSuccess()) {
            throw new RuntimeException(dto.getErrorMessages().get(0));
        }
    }

    @Override
    public Map<String, Object> getLetterOfCreditAwardCriteria(String fundGroupCode, String fundCode) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        if (ObjectUtils.isNotNull(fundGroupCode)) {
            criteria.put("locFundGroupCode", fundGroupCode);
        }
        if (ObjectUtils.isNotNull(fundCode)) {
            criteria.put("locFundCode", fundCode);
        }
        return criteria;
    }

    protected AwardWebService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        AwardWebService awardWebService = (AwardWebService) GlobalResourceLoader.getService(KcConstants.Award.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (awardWebService == null) {
            LOG.warn("Couldn't get AwardWebService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            AwardWebSoapService ss = null;
            try {
                ss = new AwardWebSoapService();
            } catch (MalformedURLException ex) {
                LOG.error("Could not intialize AwardWebSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize AwardWebSoapService: " + ex.getMessage());
            }
            awardWebService = ss.getAwardWebServicePort();
        }

        return awardWebService;
    }
}
