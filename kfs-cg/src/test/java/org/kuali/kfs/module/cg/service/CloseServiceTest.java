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
package org.kuali.kfs.module.cg.service;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.exception.ValidationException;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.businessobject.defaultvalue.NextProposalNumberFinder;
import org.kuali.kfs.module.cg.document.ProposalAwardCloseDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Vector;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

@ConfigureContext(session = khuntley)
public class CloseServiceTest extends KualiTestBase {

    private static final String VALID_AWARD_STATUS_CODE = "R";
    private static final String INVALID_AWARD_STATUS_CODE = "U";
    private static final Long thirteen = new Long(13);
    private static final Long fourteen = new Long(14);
    private static final Long fifteen = new Long(15);

    private DateFormat dateFormat;
    private Date today;
    private Date tomorrow;

    private int timeout = 0;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateFormat = DateFormat.getDateInstance();
        today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        tomorrow = new Date(calendar.getTime().getTime());
    }

    public void testClose_awardEntryDateLessThanCloseOnOrBeforeDate() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not

        Date closeCloseOnOrBeforeDate = today;
        Date awardEntryDate = new Date(dateFormat.parse("May 20, 2000").getTime()); // must be <= closeCloseOnOrBeforeDate
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than
        // closeCloseOnOrBeforeDate

        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant

        Date proposalClosingDate = null; // must be null
        Date awardClosingDate = null; // must be null

        //

        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringContext.getBean(BusinessObjectService.class).save(proposal);

        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(award);

        ProposalAwardCloseDocument close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
        // SpringContext.getBean(CloseService.class).save(close);

        // Verify that everything should be OK for the close.

        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close);

        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);

        // Run the close.
        SpringContext.getBean(CloseService.class).close();

        // Verify.
        assertEquals("Awards were not closed properly.", fourteen, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", fifteen, close.getProposalClosedCount());
    }

    public void testClose_awardEntryDateEqualToCloseOnOrBeforeDate() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not

        Date closeCloseOnOrBeforeDate = today;
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime()); // must be <= closeCloseOnOrBeforeDate
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than
        // closeCloseOnOrBeforeDate

        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant

        Date proposalClosingDate = null; // must be null
        Date awardClosingDate = null; // must be null

        //

        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringContext.getBean(BusinessObjectService.class).save(proposal);

        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(award);

        ProposalAwardCloseDocument close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
        // SpringContext.getBean(CloseService.class).save(close);

        // Verify that everything should be OK for the close.

        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close);

        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);

        // Run the close.
        SpringContext.getBean(CloseService.class).close();

        // Verify.
        assertEquals("Awards were not closed properly.", fourteen, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", fifteen, close.getProposalClosedCount());
    }

    public void testClose_awardClosingDateNotNull() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not

        Date closeCloseOnOrBeforeDate = today;
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime());
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than
        // closeCloseOnOrBeforeDate

        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant

        Date proposalClosingDate = null; // must be null
        Date awardClosingDate = today;

        // Create and save objects for closing.

        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringContext.getBean(BusinessObjectService.class).save(proposal);

        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(award);

        ProposalAwardCloseDocument close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
        // SpringContext.getBean(CloseService.class).save(close);

        // Verify that everything should be OK for the close.

        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close, false, true, true);

        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);

        // Run the close.
        SpringContext.getBean(CloseService.class).close();

        assertEquals("Awards were not closed properly.", thirteen, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", fifteen, close.getProposalClosedCount());
    }

    public void testClose_awardStatusCodeInvalid() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not

        Date closeCloseOnOrBeforeDate = today;
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime());
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than
        // closeCloseOnOrBeforeDate

        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant

        Date proposalClosingDate = null;
        Date awardClosingDate = null;

        // Create and save objects for closing.

        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringContext.getBean(BusinessObjectService.class).save(proposal);

        Award award = createAward(proposal, awardEntryDate, awardClosingDate, INVALID_AWARD_STATUS_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(award);

        ProposalAwardCloseDocument close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
        // SpringContext.getBean(CloseService.class).save(close);

        // Verify that everything should be OK for the close.

        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close, true, false, true);

        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);

        // Run the close.
        SpringContext.getBean(CloseService.class).close();

        assertEquals("Awards were not closed properly.", thirteen, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", fifteen, close.getProposalClosedCount());
    }

    public void testClose_awardEntryDateGreaterThanCloseOnOrBeforeDate() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not

        Date closeCloseOnOrBeforeDate = today;
        Date awardEntryDate = tomorrow;
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than
        // closeCloseOnOrBeforeDate

        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant

        Date proposalClosingDate = null; // must be null
        Date awardClosingDate = null; // must be null

        // Create and save objects for closing.

        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringContext.getBean(BusinessObjectService.class).save(proposal);

        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(award);

        ProposalAwardCloseDocument close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
        // SpringContext.getBean(CloseService.class).save(close);

        // Verify that everything should be OK for the close.

        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close, true, true, false);

        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close);

        // Run the close.
        SpringContext.getBean(CloseService.class).close();

        assertEquals("Awards were not closed properly.", thirteen, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", fifteen, close.getProposalClosedCount());
    }

    public void testClose_proposalClosingDateNotNull() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not

        Date closeCloseOnOrBeforeDate = today;
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime());
        Date proposalSubmissionDate = new Date(dateFormat.parse("May 2, 1999").getTime()); // must be less than
        // closeCloseOnOrBeforeDate

        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime()); // not relevant
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime()); // not relevant

        Date proposalClosingDate = today;
        Date awardClosingDate = null;

        // Create and save objects for closing.

        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringContext.getBean(BusinessObjectService.class).save(proposal);

        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(award);

        ProposalAwardCloseDocument close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
        // SpringContext.getBean(CloseService.class).save(close);

        // Verify that everything should be OK for the close.

        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close);

        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close, false, true);

        // Run the close.
        SpringContext.getBean(CloseService.class).close();

        assertEquals("Awards were not closed properly.", fourteen, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", fourteen, close.getProposalClosedCount());
    }

    public void testClose_proposalSubmissionDateGreaterThanCloseCloseOnOrBeforeDate() throws Exception {
        Date closeInitiatedDate = today; // must be today, close will abort if not

        Date closeCloseOnOrBeforeDate = today;
        Date awardEntryDate = new Date(dateFormat.parse("June 1, 2000").getTime());
        Date proposalSubmissionDate = tomorrow;

        Date proposalBeginningDate = new Date(dateFormat.parse("Jul 1, 1999").getTime());
        Date proposalEndingDate = new Date(dateFormat.parse("Aug 1, 1999").getTime());

        Date proposalClosingDate = null;
        Date awardClosingDate = null;

        // Create and save objects for closing.

        Proposal proposal = createProposal(proposalBeginningDate, proposalEndingDate, proposalSubmissionDate, proposalClosingDate);
        SpringContext.getBean(BusinessObjectService.class).save(proposal);

        Award award = createAward(proposal, awardEntryDate, awardClosingDate, VALID_AWARD_STATUS_CODE);
        SpringContext.getBean(BusinessObjectService.class).save(award);

        ProposalAwardCloseDocument close = createClose(closeCloseOnOrBeforeDate);
        saveAndRoute(close);
        // SpringContext.getBean(CloseService.class).save(close);

        // Verify that everything should be OK for the close.

        // Be sure that the award should be closed.
        verifyAwardWillBeIncludedInClose(award, close);

        // Be sure that the proposal should be closed.
        verifyProposalWillBeIncludedInClose(proposal, close, true, false);

        // Run the close.
        SpringContext.getBean(CloseService.class).close();

        assertEquals("Awards were not closed properly.", fourteen, close.getAwardClosedCount());
        assertEquals("Proposals were not closed properly.", fourteen, close.getProposalClosedCount());
    }

    private void verifyAwardWillBeIncludedInClose(Award award, ProposalAwardCloseDocument close) {
        verifyAwardWillBeIncludedInClose(award, close, true, true, true);
    }

    private void verifyAwardWillBeIncludedInClose(Award award, ProposalAwardCloseDocument close, boolean verifyAwardClosingDate, boolean verifyAwardStatusCode, boolean verifyAwardEntryDate) {
        if (verifyAwardClosingDate) {
            assertNull("Award closing date must be null to be included in closing.", award.getAwardClosingDate());
        }
        if (verifyAwardStatusCode) {
            assertNotSame("Award status code must not be 'U' to be included in closing.", award.getAwardStatusCode(), "U");
        }
        if (verifyAwardEntryDate) {
            assertTrue("Award entry date must be less than or equal to the close last closed date.", award.getAwardEntryDate().getTime() <= close.getCloseOnOrBeforeDate().getTime());
        }
    }

    private void verifyProposalWillBeIncludedInClose(Proposal proposal, ProposalAwardCloseDocument close) {
        verifyProposalWillBeIncludedInClose(proposal, close, true, true);
    }

    private void verifyProposalWillBeIncludedInClose(Proposal proposal, ProposalAwardCloseDocument close, boolean verifyProposalClosingDate, boolean verifyProposalSubmissionDate) {
        if (verifyProposalClosingDate) {
            assertNull("Proposal closing date must be null to be included in closing.", proposal.getProposalClosingDate());
        }
        if (verifyProposalSubmissionDate) {
            assertTrue("Proposal submission date must be less than or equal to closing last closed date.", proposal.getProposalSubmissionDate().getTime() <= close.getCloseOnOrBeforeDate().getTime());
        }
    }

    private Proposal createProposal(Date proposalBeginningDate, Date proposalEndingDate, Date proposalSubmissionDate, Date proposalClosingDate) {
        // Create and save a proposal
        Proposal proposal = new Proposal();
        proposal.setProposalNumber(NextProposalNumberFinder.getLongValue().toString());
        // set required fields
        proposal.setAgencyNumber("12851");
        proposal.setProposalProjectTitle("Testing CG Close Process");
        proposal.setProposalBeginningDate(proposalBeginningDate);
        proposal.setProposalEndingDate(proposalEndingDate);
        proposal.setProposalDirectCostAmount(new KualiDecimal("3840.00"));
        proposal.setProposalIndirectCostAmount(new KualiDecimal("2016.00"));
        proposal.setProposalTotalAmount(proposal.getProposalDirectCostAmount().add(proposal.getProposalIndirectCostAmount()));
        proposal.setProposalSubmissionDate(proposalSubmissionDate);
        proposal.setProposalClosingDate(proposalClosingDate);
        proposal.setProposalAwardTypeCode("N");
        proposal.setProposalPurposeCode("C");
        return proposal;
    }

    private Award createAward(Proposal proposal, Date awardEntryDate, Date awardClosingDate, String awardStatusCode) {
        // Create and save an award
        Award award = new Award(proposal);
        award.setAwardEntryDate(awardEntryDate);
        award.setAwardClosingDate(awardClosingDate);
        award.setAwardStatusCode(awardStatusCode);
        return award;
    }

    private ProposalAwardCloseDocument createClose(Date closeCloseOnOrBeforeDate) throws WorkflowException {
        Document document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), ProposalAwardCloseDocument.class);
        ProposalAwardCloseDocument close = (ProposalAwardCloseDocument) document;// SpringContext.getBean(DocumentService.class).getNewDocument(Close.class);
        close.setUserInitiatedCloseDate(today);
        close.setCloseOnOrBeforeDate(closeCloseOnOrBeforeDate);
        return close;
    }

    private void saveAndRoute(ProposalAwardCloseDocument close) throws Exception {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        saveDocument(close, documentService);
        close = (ProposalAwardCloseDocument) documentService.getByDocumentHeaderId(close.getDocumentNumber());
        routeDocument(close, documentService);
    }

    public static void routeDocument(Document document, DocumentService documentService) throws Exception {

        // Verify that the doc isn't yet routed.
        assertFalse("Document should not be already ENROUTE: " + document, DocumentStatus.ENROUTE.equals(document.getDocumentHeader().getWorkflowDocument().getStatus()));

        // Route the doc.
        try {
            documentService.routeDocument(document, "routing test doc", new Vector());
        } catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }

        // Routing should be configured to go straight to final.
        //  assertTrue(FINAL_STATUS.equals(document.getDocumentHeader().getWorkflowDocument().getStatus()));
    }

    public static void saveDocument(Document document, DocumentService documentService) throws WorkflowException {
        try {
            documentService.saveDocument(document);
        } catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }
    }
}

