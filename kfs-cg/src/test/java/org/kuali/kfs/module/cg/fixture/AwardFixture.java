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
package org.kuali.kfs.module.cg.fixture;

import org.kuali.kfs.integration.cg.CGIntegrationConstants;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;

/**
 * Fixture class for Award
 */
public enum AwardFixture {

    CG_AWARD1("111", "2011-10-01", "2011-09-22", null, null, null, false, null, true, null),
    CG_AWARD2("11", "1968-07-01", "1969-06-30", new KualiDecimal(7708.00), new KualiDecimal(2016.00), KualiDecimal.ZERO, false, null, true, null),
    CG_AWARD3("1234", "2011-01-01", "2011-09-22", new KualiDecimal(0), new KualiDecimal(0), new KualiDecimal(0), false, null, true, null),

    CG_AWARD_INV_AWARD("111", "2011-10-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, null),
    CG_AWARD_INV_ACCOUNT("111", "2011-10-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.ACCOUNT.getCode(), true, CGConstants.MONTHLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_INV_CCA("111", "2011-10-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.CONTRACT_CONTROL.getCode(), true, null),

    CG_AWARD_MONTHLY_BILLED_DATE_NULL("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.MONTHLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_MILESTONE_BILLED_DATE_NULL("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.MILESTONE_BILLING_SCHEDULE_CODE),
    CG_AWARD_PREDETERMINED_BILLED_DATE_NULL("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.PREDETERMINED_BILLING_SCHEDULE_CODE),
    CG_AWARD_QUAR_BILLED_DATE_NULL("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.QUATERLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_SEMI_ANN_BILLED_DATE_NULL("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_ANNUAL_BILLED_DATE_NULL("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.ANNUALLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_LOCB_BILLED_DATE_NULL("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.LOC_BILLING_SCHEDULE_CODE),
    CG_AWARD_MONTHLY_BILLED_DATE_VALID("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.MONTHLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_MILESTONE_BILLED_DATE_VALID("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.MILESTONE_BILLING_SCHEDULE_CODE),
    CG_AWARD_PREDETERMINED_BILLED_DATE_VALID("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.PREDETERMINED_BILLING_SCHEDULE_CODE),
    CG_AWARD_QUAR_BILLED_DATE_VALID("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.QUATERLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_SEMI_ANN_BILLED_DATE_VALID("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_ANNUAL_BILLED_DATE_VALID("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.ANNUALLY_BILLING_SCHEDULE_CODE),
    CG_AWARD_LOCB_BILLED_DATE_VALID("111", "2011-01-01", "2011-09-22", null, null, null, false, CGIntegrationConstants.AwardInvoicingOption.Types.AWARD.getCode(), true, CGConstants.LOC_BILLING_SCHEDULE_CODE);

    private String proposalNumber;
    private String awardBeginningDate;
    private String awardEndingDate;

    private KualiDecimal awardDirectCostAmount;
    private KualiDecimal awardIndirectCostAmount;
    private KualiDecimal minInvoiceAmount;
    private boolean excludedFromInvoicing;
    private String invoicingOptionCode;
    private boolean active;
    private String billingFrequencyCode;

    private AwardFixture(String proposalNumber, String awardBeginningDate, String awardEndingDate, KualiDecimal awardDirectCostAmount, KualiDecimal awardIndirectCostAmount, KualiDecimal minInvoiceAmount, boolean excludedFromInvoicing, String invoicingOptionCode, boolean active, String billingFrequencyCode) {

        this.proposalNumber = proposalNumber;
        this.awardBeginningDate = awardBeginningDate;
        this.awardEndingDate = awardEndingDate;
        this.awardDirectCostAmount = awardDirectCostAmount;
        this.awardIndirectCostAmount = awardIndirectCostAmount;
        this.minInvoiceAmount = minInvoiceAmount;
        this.excludedFromInvoicing = excludedFromInvoicing;
        this.invoicingOptionCode = invoicingOptionCode;
        this.active = active;
        this.billingFrequencyCode = billingFrequencyCode;
    }

    public Award createAward() {
        Award award = new Award();
        award.setProposalNumber(this.proposalNumber);
        award.setAwardBeginningDate(Date.valueOf(this.awardBeginningDate));
        award.setAwardEndingDate(Date.valueOf(this.awardEndingDate));
        award.setAwardDirectCostAmount(this.awardDirectCostAmount);
        award.setAwardIndirectCostAmount(this.awardIndirectCostAmount);
        award.setMinInvoiceAmount(this.minInvoiceAmount);
        award.setExcludedFromInvoicing(this.excludedFromInvoicing);
        award.setInvoicingOptionCode(this.invoicingOptionCode);
        award.setActive(this.active);
        award.setBillingFrequencyCode(this.billingFrequencyCode);
        return award;
    }


}
