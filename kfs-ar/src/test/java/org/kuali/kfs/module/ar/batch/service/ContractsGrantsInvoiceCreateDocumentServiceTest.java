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
package org.kuali.kfs.module.ar.batch.service;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ARAgencyFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateTestBase;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.io.File;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceCreateDocumentServiceTest extends ContractsGrantsInvoiceCreateTestBase {

    @ConfigureContext(session = wklykins)
    public void testCreateCGInvoiceDocumentByAwardInfo() {
        String coaCode = "BL";
        String orgCode = "UGCS";

        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_1 = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        contractsGrantsInvoiceDocument_1.setBillByChartOfAccountCode(coaCode);
        contractsGrantsInvoiceDocument_1.setBilledByOrganizationCode(orgCode);

        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(contractsGrantsInvoiceDocument_1.getDocumentNumber());

        List<String> procCodes = contractsGrantsInvoiceDocumentService.getProcessingFromBillingCodes(coaCode, orgCode);
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(procCodes.get(0));
        accountsReceivableDocumentHeader.setProcessingOrganizationCode(procCodes.get(1));

        contractsGrantsInvoiceDocument_1.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);


        contractsGrantsInvoiceDocument_1.getInvoiceGeneralDetail().setAward(award);
        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL3.createInvoiceAccountDetail();

        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument_1.setAccountDetails(accountDetails);

        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_2;

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        contractsGrantsInvoiceDocument_2 = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode, errorMessages, null, null);

        assertEquals(contractsGrantsInvoiceDocument_1.getInvoiceGeneralDetail().getProposalNumber(), contractsGrantsInvoiceDocument_2.getInvoiceGeneralDetail().getProposalNumber());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountDetails().get(0).getAccountNumber(), contractsGrantsInvoiceDocument_2.getAccountDetails().get(0).getAccountNumber());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountDetails().get(0).getChartOfAccountsCode(), contractsGrantsInvoiceDocument_2.getAccountDetails().get(0).getChartOfAccountsCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode(), contractsGrantsInvoiceDocument_2.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode(), contractsGrantsInvoiceDocument_2.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getBillByChartOfAccountCode(), contractsGrantsInvoiceDocument_2.getBillByChartOfAccountCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getBilledByOrganizationCode(), contractsGrantsInvoiceDocument_2.getBilledByOrganizationCode());
    }

    @ConfigureContext(session = wklykins)
    public void testManualCreateCGInvoiceDocumentsByAwardsOneValid() throws Exception {
        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(buildMockUniversityDateService());

        List<ContractsAndGrantsBillingAward> awards = setupBillableAwards();

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        assertEquals("errorMessages should be empty.", 0, errorMessages.size());

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertEquals("no errors should be persisted", 0, persistedErrors.size());

        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(originalUniversityDateService);
    }

    public void testManualCreateCGInvoiceDocumentsByAwardsEmptyAwardsList() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();
        contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());
        assertTrue(contractsGrantsInvoiceDocumentErrorLogs.size() == 0);

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testManualCreateCGInvoiceDocumentsByAwardsNullAwardsList() {
        List<ContractsAndGrantsBillingAward> awards = null;

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testManualCreateCGInvoiceDocumentsByAwardsNoOrg() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = (Award) awards.get(0);
        award.setAwardOrganizations(new ArrayList<AwardOrganization>());

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_ORGANIZATION_ON_AWARD);
        errorMessage = MessageFormat.format(errorMessage, award.getProposalNumber().toString());

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    @ConfigureContext(session = wklykins)
    public void testManualCreateCGInvoiceDocumentsByAccountNonBillable() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award) awards2.get(0);
        award2.setInvoicingOptionCode(ArConstants.INV_ACCOUNT);

        errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, award2.getActiveAwardAccounts().get(0).getAccountNumber(), award.getProposalNumber().toString());

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    @ConfigureContext(session = wklykins)
    public void testManualCreateCGInvoiceDocumentsByAccountOneBillableOneNonBillable() throws Exception {
        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(buildMockUniversityDateService());

        List<ContractsAndGrantsBillingAward> awards = setupBillableAwards();
        Award award = ((Award) awards.get(0));
        award.setInvoicingOptionCode(ArConstants.INV_ACCOUNT);
        AwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_2.createAwardAccount();
        awardAccount_2.setCurrentLastBilledDate(new Date(System.currentTimeMillis()));
        awardAccount_2.refreshReferenceObject("account");
        award.getAwardAccounts().add(awardAccount_2);

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, awardAccount_2.getAccountNumber(), award.getProposalNumber().toString());

        assertEquals("errorMessages should contain one message", 1, errorMessages.size());
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertEquals("one error should be persisted", 1, persistedErrors.size());
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }

        contractsGrantsInvoiceCreateDocumentService.setUniversityDateService(originalUniversityDateService);
    }

    @ConfigureContext(session = wklykins)
    public void testManualCreateCGInvoiceDocumentsByCCAContractAccountNotBillable() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award) awards2.get(0);
        award2.setInvoicingOptionCode(ArConstants.INV_CONTRACT_CONTROL_ACCOUNT);

        errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.CONTROL_ACCOUNT_NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, award2.getActiveAwardAccounts().get(0).getAccount().getContractControlAccount().getAccountNumber(), award.getProposalNumber().toString());

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    @ConfigureContext(session = wklykins)
    public void testManualCreateCGInvoiceDocumentsByAwardNotAllBillableAccounts() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award) awards.get(0));
        award.setBillingFrequencyCode(ArConstants.BillingFrequencyValues.PREDETERMINED_BILLING.getCode());
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages, null, null);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award) awards2.get(0);
        award2.setInvoicingOptionCode(ArConstants.INV_AWARD);

        errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NOT_ALL_BILLABLE_ACCOUNTS);
        errorMessage = MessageFormat.format(errorMessage, award2.getProposalNumber().toString());

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError : persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    private boolean messagesContainsExpectedError(List<ErrorMessage> errorMessages, String expectedErrorMessage) {
        ErrorMessage errorMessage = errorMessages.get(0);
        String errorMessageString = MessageFormat.format(configurationService.getPropertyValueAsString(errorMessage.getErrorKey()), (Object[]) errorMessage.getMessageParameters());
        return StringUtils.equals(errorMessageString, expectedErrorMessage);
    }

    private boolean errorLogContainsAward(Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs, ContractsAndGrantsBillingAward award) {
        for (ContractsGrantsInvoiceDocumentErrorLog contractsGrantsInvoiceDocumentErrorLog : contractsGrantsInvoiceDocumentErrorLogs) {
            if (contractsGrantsInvoiceDocumentErrorLog.getProposalNumber().equals(award.getProposalNumber())) {
                return true;
            }
        }
        return false;
    }


}
