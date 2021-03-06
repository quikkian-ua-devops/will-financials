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
package org.kuali.kfs.fp.document.service;

import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyTestUtil;
import org.kuali.kfs.krad.exception.ValidationException;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.util.Iterator;
import java.util.List;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

@ConfigureContext(session = khuntley)
public class CashReceiptServiceTest extends KualiTestBase {
    // TODO: once we stop returning default campusCode for unknown verificationUnit, need a test for unknown verificationUnit
    private static final String TEST_CAMPUS_CD = "KO";

    private static final String DEFAULT_CAMPUS_CD = "BL";

    private static final String UNKNOWN_CAMPUS_CODE = "ZZ";

    // TODO fix this so it doesn't use constants built into the non-test classes
    /*
     * public final void testGetCampusCodeForCashReceiptVerificationUnit_knownVerificationUnit() { String returnedCode =
     * SpringContext.getBean(CashReceiptService.class).getCampusCodeForCashReceiptVerificationUnit(TEST_UNIT_NAME);
     * assertNotNull(returnedCode); assertEquals(TEST_CAMPUS_CD, returnedCode); }
     */

    // TODO: once we stop returning default campusCode for unknown verificationUnit, need a test for unknown verificationUnit
    /*
     * public final void testGetCashReceiptVerificationUnitForCampusCode_knownCampusCode() { String returnedUnit =
     * SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForCampusCode(TEST_CAMPUS_CD);
     * assertNotNull(returnedUnit); assertEquals(TEST_UNIT_NAME, returnedUnit); }
     */


    public final void testGetCashReceiptVerificationUnit_nullUser() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForUser(null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceiptVerificationUnit_validUser() {
        String expectedUnit = DEFAULT_CAMPUS_CD;

        String unit = SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForUser(GlobalVariables.getUserSession().getPerson());
        assertEquals(expectedUnit, unit);
    }

    // TODO: once we stop returning default campus code for every user, need a test for a user who is not in a verification unit

    public final void testGetCashReceipts1_blankUnitName() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts("   ", (String) null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceipts1_blankStatusCode() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts(TEST_CAMPUS_CD, "");
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    // TODO: once we stop returning default campus code for unknown unit, need tests for unknown unit

    public final void testGetCashReceipts1_knownVerificationUnit_noVerifiedReceipts() {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        List receipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, CashReceipt.VERIFIED);
        assertEquals(0, receipts.size());
    }

    public final void testGetCashReceipts1_knownVerificationUnit_noInterimReceipts() {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        List receipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, CashReceipt.INTERIM);
        assertEquals(0, receipts.size());
    }


    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts1_knownVerificationUnit_interimReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", CashReceipt.INTERIM, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", CashReceipt.INTERIM, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are only interim CRs
        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, CashReceipt.VERIFIED);
        assertEquals(0, vreceipts.size());

        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, CashReceipt.INTERIM);
        assertEquals(2, ireceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts1_knownVerificationUnit_verifiedReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", CashReceipt.VERIFIED, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", CashReceipt.VERIFIED, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are only verified CRs
        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, CashReceipt.INTERIM);
        assertEquals(0, ireceipts.size());

        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, CashReceipt.VERIFIED);
        assertEquals(2, vreceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts1_knownVerificationUnit_mixedReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", CashReceipt.INTERIM, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", CashReceipt.VERIFIED, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are some of each
        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, CashReceipt.INTERIM);
        assertEquals(1, ireceipts.size());

        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, CashReceipt.VERIFIED);
        assertEquals(1, vreceipts.size());


        // clean up afterwards
        denatureCashReceipts(workgroup);
    }


    private static final String[] BOTH_STATII = {CashReceipt.VERIFIED, CashReceipt.INTERIM};
    private static final String[] ISTATII = {CashReceipt.INTERIM};
    private static final String[] VSTATII = {CashReceipt.VERIFIED};


    public final void testGetCashReceipts2_blankUnitName() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts("   ", (String[]) null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceipts2_nullStatii() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts(TEST_CAMPUS_CD, (String[]) null);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceipts2_emptyStatii() {
        boolean failedAsExpected = false;

        String[] emptyStatii = {};
        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts(TEST_CAMPUS_CD, emptyStatii);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceipts2_blankStatii() {
        boolean failedAsExpected = false;

        String[] blankStatii = {"  "};
        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts(TEST_CAMPUS_CD, blankStatii);
        } catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    // TODO: once we stop returning default campus code for unknown unit, need tests for unknown unit

    public final void testGetCashReceipts2_knownVerificationUnit_noVerifiedReceipts() {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        List receipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, VSTATII);
        assertEquals(0, receipts.size());
    }

    public final void testGetCashReceipts2_knownVerificationUnit_noInterimReceipts() {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        List receipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, ISTATII);
        assertEquals(0, receipts.size());
    }


    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts2_knownVerificationUnit_interimReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", CashReceipt.INTERIM, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", CashReceipt.INTERIM, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are only interim CRs
        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, VSTATII);
        assertEquals(0, vreceipts.size());

        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, ISTATII);
        assertEquals(2, ireceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts2_knownVerificationUnit_verifiedReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", CashReceipt.VERIFIED, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", CashReceipt.VERIFIED, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are only verified CRs
        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, ISTATII);
        assertEquals(0, ireceipts.size());

        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, VSTATII);
        assertEquals(2, vreceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts2_knownVerificationUnit_mixedReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", CashReceipt.INTERIM, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", CashReceipt.VERIFIED, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are some of each
        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, ISTATII);
        assertEquals(1, ireceipts.size());

        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, VSTATII);
        assertEquals(1, vreceipts.size());

        List mixedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, BOTH_STATII);
        assertEquals(2, mixedReceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }


    private void denatureCashReceipts(String campusCode) {
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(campusCode, BOTH_STATII);

        for (Iterator i = verifiedReceipts.iterator(); i.hasNext(); ) {
            CashReceiptDocument receipt = (CashReceiptDocument) i.next();
            receipt.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode("Z");
            SpringContext.getBean(DocumentService.class).updateDocument(receipt);
        }
    }

    private CashReceiptDocument buildCashReceiptDoc(String campusCode, String description, String status, KualiDecimal currencyAmount, KualiDecimal checkAmount) throws WorkflowException {
        CashReceiptDocument crDoc = (CashReceiptDocument) SpringContext.getBean(DocumentService.class).getNewDocument(CashReceiptDocument.class);

        crDoc.getDocumentHeader().setDocumentDescription(description);
        crDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(status);

        crDoc.setCheckEntryMode(CashReceiptDocument.CHECK_ENTRY_TOTAL);
        crDoc.setTotalCheckAmount(checkAmount);

        crDoc.setCampusLocationCode(campusCode);

        crDoc.addSourceAccountingLine(CashReceiptFamilyTestUtil.buildSourceAccountingLine(crDoc.getDocumentNumber(), crDoc.getPostingYear(), crDoc.getNextSourceLineNumber()));

        try {
            SpringContext.getBean(DocumentService.class).saveDocument(crDoc);
        } catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }
        return crDoc;
    }
}

