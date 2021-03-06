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
package org.kuali.kfs.module.ar.service;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

/**
 * Tests the AccountsReceivableModuleBillingService
 */
@ConfigureContext(session = khuntley)
public class AccountsReceivableModuleBillingServiceTest extends KualiTestBase {
    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected BusinessObjectService businessObjectService;

    @Override
    public void setUp() {
        accountsReceivableModuleBillingService = SpringContext.getBean(AccountsReceivableModuleBillingService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }

    public void testHasActiveBills() {
        setupPredeterminedBillingSchedule("1", true);
        setupPredeterminedBillingSchedule("2", false);

        assertTrue(accountsReceivableModuleBillingService.hasActiveBills("1"));
        assertFalse(accountsReceivableModuleBillingService.hasActiveBills("2"));
        assertFalse(accountsReceivableModuleBillingService.hasActiveBills("111"));
    }

    public void testHasActiveMilestones() {
        setupMilestoneSchedule("1", true);
        setupMilestoneSchedule("2", false);

        assertTrue(accountsReceivableModuleBillingService.hasActiveMilestones("1"));
        assertFalse(accountsReceivableModuleBillingService.hasActiveMilestones("2"));
        assertFalse(accountsReceivableModuleBillingService.hasActiveMilestones("111"));
    }

    protected void setupMilestoneSchedule(String proposalNumber, boolean active) {
        Milestone milestone = new Milestone();
        milestone.setProposalNumber(proposalNumber);
        milestone.setMilestoneNumber(Long.parseLong(proposalNumber));
        milestone.setMilestoneIdentifier(Long.parseLong(proposalNumber) + 1000L);
        milestone.setActive(active);

        MilestoneSchedule milestoneSchedule = new MilestoneSchedule();
        milestoneSchedule.setProposalNumber(proposalNumber);
        List<Milestone> milestones = new ArrayList<Milestone>();
        milestones.add(milestone);
        milestoneSchedule.setMilestones(milestones);
        businessObjectService.save(milestoneSchedule);
        businessObjectService.save(milestone);
    }

    protected void setupPredeterminedBillingSchedule(String proposalNumber, boolean active) {
        Bill bill = new Bill();
        bill.setProposalNumber(proposalNumber);
        bill.setBillNumber(Long.parseLong(proposalNumber));
        bill.setBillIdentifier(Long.parseLong(proposalNumber) + 1000L);
        bill.setActive(active);

        PredeterminedBillingSchedule predeterminedBillingSchedule = new PredeterminedBillingSchedule();
        predeterminedBillingSchedule.setProposalNumber(proposalNumber);
        List<Bill> bills = new ArrayList<Bill>();
        bills.add(bill);
        predeterminedBillingSchedule.setBills(bills);
        businessObjectService.save(predeterminedBillingSchedule);
        businessObjectService.save(bill);
    }

}
