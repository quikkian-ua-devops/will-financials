/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.junit.Test;
import org.kuali.kfs.module.ar.ArConstants;

public class TimeBasedBillingPeriodTest extends BillingPeriodTest {

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MONTHLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_nullLastBilled_2() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-03-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-02-28";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MONTHLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_LastBilledLastMonth() {

        String awardStartDate = "2014-07-01";
        String lastBilled = "2015-03-29";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2015-03-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MONTHLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_LastBilledTwoMonthsAgo() {

        String awardStartDate = "2014-07-01";
        String lastBilled = "2015-02-17";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2015-02-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MONTHLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_SpanCalendarYears() {

        String awardStartDate = "2013-07-01";
        String lastBilled = "2014-06-15";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-06-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MONTHLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_MayNotBillNow() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-15";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.MONTHLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Monthly_MayBillNowLastYear() {
        String awardStartDate = "2014-03-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2014-04-15";
        String expectedBillingPeriodStart = "2014-04-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.MONTHLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Predetermined_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Predetermined_nullLastBilled_2() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-03-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-02-28";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Predetermined_LastBilledLastMonth() {

        String awardStartDate = "2014-07-01";
        String lastBilled = "2015-03-29";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2015-03-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Predetermined_LastBilledTwoMonthsAgo() {

        String awardStartDate = "2014-07-01";
        String lastBilled = "2015-02-17";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2015-02-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Predetermined_SpanCalendarYears() {

        String awardStartDate = "2013-07-01";
        String lastBilled = "2014-06-15";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-06-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Predetermined_MayNotBillNow() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-15";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Predetermined_MayBillNowLastYear() {
        String awardStartDate = "2014-03-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2014-04-15";
        String expectedBillingPeriodStart = "2014-04-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Milestone_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MILESTONE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Milestone_nullLastBilled_2() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-03-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-02-28";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MILESTONE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Milestone_LastBilledLastMonth() {

        String awardStartDate = "2014-07-01";
        String lastBilled = "2015-03-29";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2015-03-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MILESTONE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Milestone_LastBilledTwoMonthsAgo() {

        String awardStartDate = "2014-07-01";
        String lastBilled = "2015-02-17";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2015-02-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MILESTONE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Milestone_SpanCalendarYears() {

        String awardStartDate = "2013-07-01";
        String lastBilled = "2014-06-15";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-06-01";
        String expectedBillingPeriodEnd = "2015-03-31";
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilled, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.MILESTONE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Milestone_MayNotBillNow() {
        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2015-04-15";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.MILESTONE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Milestone_MayBillNowLastYear() {
        String awardStartDate = "2014-03-01";
        String currentDate = "2015-04-21";
        String lastBilledDate = "2014-04-15";
        String expectedBillingPeriodStart = "2014-04-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.MILESTONE);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_nullLastBilled_1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.QUARTERLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_nullLastBilled_2() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-08-21";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-06-30";

        verifyBillingPeriodPriorTo(awardStartDate, currentDate, null, expectedBillingPeriodStart, expectedBillingPeriodEnd, true, ArConstants.BillingFrequencyValues.QUARTERLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_MayNotBillNow() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        final String lastBilledDate = "2015-04-20";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        final boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.QUARTERLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_LastBilledEarlierQuarter() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-08-21";
        final String lastBilledDate = "2015-04-20";
        String expectedBillingPeriodStart = "2015-04-01";
        String expectedBillingPeriodEnd = "2015-06-30";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.QUARTERLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_LastBilledPreviousQuarter() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        final String lastBilledDate = "2015-03-29";
        String expectedBillingPeriodStart = "2015-01-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.QUARTERLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_MayNotBillAwardInFuture() {
        String awardStartDate = "2015-07-01";
        String currentDate = "2015-04-21";
        final String lastBilledDate = null;
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        final boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.QUARTERLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_BilledLastCalendarYear() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        final String lastBilledDate = "2014-11-15";
        String expectedBillingPeriodStart = "2014-10-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.QUARTERLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Quarterly_BilledLastFiscalYear() {

        String awardStartDate = "2013-07-01";
        String currentDate = "2015-04-21";
        final String lastBilledDate = "2014-06-15";
        String expectedBillingPeriodStart = "2014-04-01";
        String expectedBillingPeriodEnd = "2015-03-31";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.QUARTERLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_SemiAnnual_nullLastBilled1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        final String lastBilledDate = null;
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2014-12-31";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.SEMI_ANNUALLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_SemiAnnual_nullLastBilled2() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-08-21";
        final String lastBilledDate = null;
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-06-30";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.SEMI_ANNUALLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_SemiAnnual_MayNotBillNow() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-04-21";
        final String lastBilledDate = "2015-04-20";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        final boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.SEMI_ANNUALLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_SemiAnnual_LastBilledPreviousSemiAnnual() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-08-21";
        final String lastBilledDate = "2015-04-20";
        String expectedBillingPeriodStart = "2015-01-01";
        String expectedBillingPeriodEnd = "2015-06-30";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.SEMI_ANNUALLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_SemiAnnual_LastBilledEarlierSemiAnnual() {

        String awardStartDate = "2013-07-01";
        String currentDate = "2015-04-21";
        final String lastBilledDate = "2014-11-29";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2014-12-31";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.SEMI_ANNUALLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Annual_nullLastBilled1() {

        String awardStartDate = "2014-07-01";
        String currentDate = "2015-08-21";
        final String lastBilledDate = null;
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-06-30";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.ANNUALLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Annual_MayNotBillNow() {

        String awardStartDate = "2013-07-01";
        String currentDate = "2015-06-21";
        final String lastBilledDate = "2014-10-20";
        String expectedBillingPeriodStart = null;
        String expectedBillingPeriodEnd = null;

        final boolean expectedBillable = false;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.ANNUALLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Annual_LastBilledPreviousAnnual() {

        String awardStartDate = "2013-07-01";
        String currentDate = "2015-08-21";
        final String lastBilledDate = "2015-04-20";
        String expectedBillingPeriodStart = "2014-07-01";
        String expectedBillingPeriodEnd = "2015-06-30";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.ANNUALLY);
    }

    @Test
    public void testDetermineBillingPeriodPriorTo_Annual_LastBilledEarlierAnnual() {

        String awardStartDate = "2012-07-01";
        String currentDate = "2015-08-21";
        final String lastBilledDate = "2013-11-20";
        String expectedBillingPeriodStart = "2013-07-01";
        String expectedBillingPeriodEnd = "2015-06-30";

        final boolean expectedBillable = true;
        verifyBillingPeriodPriorTo(awardStartDate, currentDate, lastBilledDate, expectedBillingPeriodStart, expectedBillingPeriodEnd, expectedBillable, ArConstants.BillingFrequencyValues.ANNUALLY);
    }
}
