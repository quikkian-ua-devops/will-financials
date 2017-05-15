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
package org.kuali.kfs.module.ar.document;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.businessobject.MilestoneSchedule;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceMilestoneFixture;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

/**
 * Basic setup to create Invoice Document
 */
public abstract class CGInvoiceDocumentTestBase extends KualiTestBase {

    BusinessObjectService boService;
    DocumentService documentService;
    ContractsGrantsInvoiceCreateDocumentService cginService;
    ContractsAndGrantsBillingAward award;
    ContractsGrantsInvoiceDocument document;

    @Override
    @ConfigureContext(session = khuntley)
    protected void setUp() throws Exception {
        super.setUp();
        boService = SpringContext.getBean(BusinessObjectService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        cginService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        // this award is already present in the test drive's database

        award = ARAwardFixture.CG_AWARD_INV_ACCOUNT.createAward();
        award = ARAwardFixture.CG_AWARD_INV_ACCOUNT.setAgencyFromFixture((Award) award);

        document = (ContractsGrantsInvoiceDocument) documentService.getNewDocument("CINV");
        Date start = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        Date stop = start;
        stop.setYear(start.getYear() + 1);
        if (ObjectUtils.isNotNull(award)) {


            // creating invoice document directly without using the service to get over validations.
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (!awardAccount.isFinalBilledIndicator()) {
                    // I don't want to have to do this, but in order to get the tests to pass with the code as currently
                    // written we need to save these so they can be retrieved later.
                    boService.save((AwardAccount) awardAccount);

                    List<ContractsAndGrantsBillingAwardAccount> list = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
                    list.clear();
                    // only one account is added into the list to create CINV
                    list.add(awardAccount);
                    Map<String, Object> criteria = new HashMap<String, Object>();
                    criteria.put("accountNumber", awardAccount.getAccountNumber());
                    criteria.put("chartOfAccountsCode", awardAccount.getChartOfAccountsCode());

                    Account acct = boService.findByPrimaryKey(Account.class, criteria);
                    String coaCode = acct.getChartOfAccountsCode(); //awardAccount.getAccount().getChartOfAccountsCode();
                    String orgCode = acct.getOrganizationCode(); //awardAccount.getAccount().getOrganizationCode();
                    criteria.clear();
                    Integer currentYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
                    criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode);
                    OrganizationAccountingDefault organizationAccountingDefault = boService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
                    if (ObjectUtils.isNull(organizationAccountingDefault)) {
                        organizationAccountingDefault = new OrganizationAccountingDefault();
                        organizationAccountingDefault.setChartOfAccountsCode(coaCode);
                        organizationAccountingDefault.setOrganizationCode(orgCode);
                        organizationAccountingDefault.setUniversityFiscalYear(currentYear);
                        boService.save(organizationAccountingDefault);
                    }
                    criteria.clear();
                    criteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, currentYear);
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
                    criteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "AC");
                    criteria.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, "CINV");

                    OffsetDefinition offset = boService.findByPrimaryKey(OffsetDefinition.class, criteria);
                    if (ObjectUtils.isNull(offset)) {
                        offset = new OffsetDefinition();
                        offset.setChartOfAccountsCode(coaCode);
                        offset.setUniversityFiscalYear(currentYear);
                        offset.setFinancialObjectCode("8000");
                        offset.setFinancialDocumentTypeCode("CINV");
                        offset.setFinancialBalanceTypeCode("AC");
                        boService.save(offset);
                    }
                    List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
                    document = cginService.createCGInvoiceDocumentByAwardInfo(award, list, coaCode, orgCode, errorMessages, null, null);
                    for (InvoiceAddressDetail invoiceAddressDetail : document.getInvoiceAddressDetails()) {
                        invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
                        invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
                    }
                }
            }
        }
    }

    /**
     * Gets the document attribute.
     *
     * @return Returns the document.
     */
    public ContractsGrantsInvoiceDocument getDocument() throws Exception {
        if (ObjectUtils.isNull(document)) {
            this.setUp();
        }
        return document;
    }

    /**
     * Sets the document attribute value.
     *
     * @param document The document to set.
     */
    public void setDocument(ContractsGrantsInvoiceDocument document) {
        this.document = document;
    }

    protected void setupBills(String documentNumber, String proposalNumber, boolean billed) {
        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_1 = InvoiceBillFixture.INV_BILL_1.createInvoiceBill();
        invBill_1.setDocumentNumber(documentNumber);
        boService.save(invBill_1);
        invoiceBills.add(invBill_1);
        document.setInvoiceBills(invoiceBills);

        Bill bill = new Bill();
        bill.setProposalNumber(proposalNumber);
        bill.setBillNumber(invBill_1.getBillNumber());
        bill.setBillDescription(invBill_1.getBillDescription());
        bill.setBillIdentifier(invBill_1.getBillIdentifier());
        bill.setBillDate(invBill_1.getBillDate());
        bill.setEstimatedAmount(invBill_1.getEstimatedAmount());
        bill.setBilled(billed);
        bill.setAward(document.getInvoiceGeneralDetail().getAward());

        PredeterminedBillingSchedule predeterminedBillingSchedule = new PredeterminedBillingSchedule();
        predeterminedBillingSchedule.setProposalNumber(proposalNumber);
        List<Bill> bills = new ArrayList<Bill>();
        bills.add(bill);
        predeterminedBillingSchedule.setBills(bills);
        boService.save(predeterminedBillingSchedule);
        boService.save(bill);
    }

    protected void setupMilestones(String documentNumber, String proposalNumber, boolean billed) {
        List<InvoiceMilestone> invoiceMilestones = new ArrayList<InvoiceMilestone>();
        InvoiceMilestone invMilestone_1 = InvoiceMilestoneFixture.INV_MLSTN_1.createInvoiceMilestone();
        invMilestone_1.setDocumentNumber(documentNumber);
        boService.save(invMilestone_1);
        invoiceMilestones.add(invMilestone_1);
        document.setInvoiceMilestones(invoiceMilestones);

        Milestone milestone = new Milestone();
        milestone.setProposalNumber(proposalNumber);
        milestone.setMilestoneNumber(invMilestone_1.getMilestoneNumber());
        milestone.setMilestoneIdentifier(invMilestone_1.getMilestoneIdentifier());
        milestone.setMilestoneDescription(invMilestone_1.getMilestoneDescription());
        milestone.setMilestoneAmount(invMilestone_1.getMilestoneAmount());
        milestone.setMilestoneActualCompletionDate(invMilestone_1.getMilestoneActualCompletionDate());
        milestone.setMilestoneExpectedCompletionDate(invMilestone_1.getMilestoneActualCompletionDate());
        milestone.setBilled(billed);
        milestone.setAward(document.getInvoiceGeneralDetail().getAward());

        MilestoneSchedule milestoneSchedule = new MilestoneSchedule();
        milestoneSchedule.setProposalNumber(proposalNumber);
        List<Milestone> milestones = new ArrayList<Milestone>();
        milestones.add(milestone);
        milestoneSchedule.setMilestones(milestones);
        boService.save(milestoneSchedule);
        boService.save(milestone);
    }


}
