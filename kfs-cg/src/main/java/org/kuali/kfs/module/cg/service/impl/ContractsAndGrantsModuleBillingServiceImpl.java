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
package org.kuali.kfs.module.cg.service.impl;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.LookupService;
import org.kuali.kfs.krad.util.ObjectUtils;

/**
 * This Class provides implementation to the services required for inter-module communication, allowing AR to utilize
 * the CG module to do Contracts & Grants Billing operations
 */
public class ContractsAndGrantsModuleBillingServiceImpl implements ContractsAndGrantsModuleBillingService {

    protected AwardService awardService;
    protected BusinessObjectService businessObjectService;
    protected LookupService LookupService;

    /**
     * This method would return list of business object - in this case Awards for CG Invoice functionality in AR.
     * @param fieldValues
     * @param unbounded
     * @return
     */
    @Override
    public List<? extends ContractsAndGrantsAward> lookupAwards(Map<String, String> fieldValues, boolean unbounded) {
        // Alter the map, convert the key's as per Award's lookup screen, might need to add more in the future
        String value = fieldValues.remove("accountNumber");
        if (StringUtils.isNotBlank(value)) {
            fieldValues.put("awardAccounts.account.accountNumber", value);
        }
        value = fieldValues.remove("awardBillingFrequency");
        if (StringUtils.isNotBlank(value)) {
            fieldValues.put("billingFrequencyCode", value);
        }
        value = fieldValues.remove("awardTotal");
        if (StringUtils.isNotBlank(value)) {
            fieldValues.put("awardTotalAmount", value);
        }

        if(StringUtils.isNotEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate")) && StringUtils.isNotEmpty(fieldValues.get("awardBeginningDate"))){
            String date = fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate") + ".." + fieldValues.remove("awardBeginningDate");
            fieldValues.put("awardBeginningDate", date);
        }
        else if(StringUtils.isEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate")) && StringUtils.isNotEmpty(fieldValues.get("awardBeginningDate"))){
            String date = "<=" + fieldValues.remove("awardBeginningDate");
            fieldValues.put("awardBeginningDate", date);
        }
        else if(StringUtils.isEmpty(fieldValues.get("awardBeginningDate")) && StringUtils.isNotEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate"))){
            String date = ">=" + fieldValues.get("rangeLowerBoundKeyPrefix_awardBeginningDate");
            fieldValues.put("awardBeginningDate", date);
        }

        if(StringUtils.isNotEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate")) && StringUtils.isNotEmpty(fieldValues.get("awardEndingDate"))){
            String date = fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate") + ".." + fieldValues.remove("awardEndingDate");
            fieldValues.put("awardEndingDate", date);
        }
        else if(StringUtils.isEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate")) && StringUtils.isNotEmpty(fieldValues.get("awardEndingDate"))){
            String date = "<=" + fieldValues.remove("awardEndingDate");
            fieldValues.put("awardEndingDate", date);
        }
        else if(StringUtils.isEmpty(fieldValues.get("awardEndingDate")) && StringUtils.isNotEmpty(fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate"))){
            String date = ">=" + fieldValues.get("rangeLowerBoundKeyPrefix_awardEndingDate");
            fieldValues.put("awardEndingDate", date);
        }

        return (List<Award>) LookupService.findCollectionBySearchHelper(Award.class, fieldValues, unbounded);
    }

    @Override
    public ContractsAndGrantsBillingAward updateAwardIfNecessary(String proposalNumber, ContractsAndGrantsBillingAward currentAward ) {
        ContractsAndGrantsBillingAward award = currentAward;

        if ( ObjectUtils.isNull(proposalNumber)) {
            award = null;
        } else {
            if ( ObjectUtils.isNull(currentAward) || !StringUtils.equals(currentAward.getProposalNumber(), proposalNumber))  {
                award = awardService.getByPrimaryId(proposalNumber);
            }
        }
        return award;
    }

    /**
     * This method sets last Billed Date to award Account.
     *
     * @param criteria
     * @param invoiceStatus
     * @param lastBilledDate
     */
    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean invoiceReversal, Date lastBilledDate) {
        AwardAccount awardAccount = getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);

        if (invoiceReversal) {
            // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
            awardAccount.setCurrentLastBilledDate(awardAccount.getPreviousLastBilledDate());
            awardAccount.setPreviousLastBilledDate(null);
        } else {
            // If the invoice is final, transpose current last billed date to previous and set invoice last billed date to current.
            awardAccount.setPreviousLastBilledDate(awardAccount.getCurrentLastBilledDate());
            awardAccount.setCurrentLastBilledDate(lastBilledDate);
        }

        getBusinessObjectService().save(awardAccount);

    }


    /**
     * This method sets last billed Date to Award
     *
     * @param proposalNumber
     * @param lastBilledDate
     */
    @Override
    public void setLastBilledDateToAward(String proposalNumber, Date lastBilledDate) {
        // This is a No-Op since getLastBilledDate on the CG Award is deriving the value from the AwardAccounts
    }

    /**
     * This method sets final billed to award Account.
     *
     * @param criteria
     * @param finalBilled
     */
    @Override
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        AwardAccount awardAccount = getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        awardAccount.setFinalBilledIndicator(finalBilled);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService#setFinalBilledAndLastBilledDateToAwardAccount(java.util.Map, java.lang.String, java.sql.Date)
     */
    @Override
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean finalBilled, boolean invoiceReversal, Date lastBilledDate) {
        AwardAccount awardAccount = getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);

        if (invoiceReversal) {
            // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
            awardAccount.setCurrentLastBilledDate(awardAccount.getPreviousLastBilledDate());
            awardAccount.setPreviousLastBilledDate(null);
        } else {
            // If the invoice is final, transpose current last billed date to previous and set invoice last billed date to current.
            awardAccount.setPreviousLastBilledDate(awardAccount.getCurrentLastBilledDate());
            awardAccount.setCurrentLastBilledDate(lastBilledDate);
        }

        awardAccount.setFinalBilledIndicator(finalBilled);
        getBusinessObjectService().save(awardAccount);
    }

    @Override
    public Map<String, Object> getLetterOfCreditAwardCriteria(String fundGroupCode, String fundCode) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        if (ObjectUtils.isNotNull(fundGroupCode)) {
            criteria.put("letterOfCreditFund.letterOfCreditFundGroupCode", fundGroupCode);
        }
        if (ObjectUtils.isNotNull(fundCode)) {
            criteria.put("letterOfCreditFund.letterOfCreditFundCode", fundCode);
        }
        return criteria;
    }

    /**
     * Returns an implementation of the BusinessObjectService
     *
     * @return an implementation of the BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Returns an implementation of the BusinessObjectService
     *
     * @return an implementation of the BusinessObjectService
     */
    public AwardService getAwardService() {
        return awardService;
    }

    public void setAwardService(AwardService awardService) {
        this.awardService = awardService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public LookupService getLookupService() {
        return LookupService;
    }

    public void setLookupService(LookupService lookupService) {
        LookupService = lookupService;
    }

}
