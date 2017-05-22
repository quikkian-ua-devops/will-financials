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
package org.kuali.kfs.sys.document.service.impl;

import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.rice.core.api.util.type.KualiInteger;

import java.util.Iterator;
import java.util.List;

@ConfigureContext
public class PaymentSourceHelperServiceImplTest extends KualiTestBase {
    protected PaymentSourceHelperService paymentSourceHelperService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        paymentSourceHelperService = SpringContext.getBean(PaymentSourceHelperService.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Assumption: KFSConstants.MAX_NOTE_LINE_SIZE = 90
     */
    public void testBuildNotesForCheckStubTextNoWrappingOneLine() {
        // No wrapping, one line
        List<PaymentNoteText> paymentNoteTexts1 = paymentSourceHelperService.buildNotesForCheckStubText("Super Man File number: 1234567890 Deduction Dode: 10", 1);
        Iterator<PaymentNoteText> iteratorPaymentNoteTexts1 = paymentNoteTexts1.iterator();
        PaymentNoteText iteratorPaymentNoteTexts1_1 = iteratorPaymentNoteTexts1.next();
        assertEquals(new KualiInteger(1), iteratorPaymentNoteTexts1_1.getCustomerNoteLineNbr());
        assertEquals("Super Man File number: 1234567890 Deduction Dode: 10", iteratorPaymentNoteTexts1_1.getCustomerNoteText());
    }

    public void testBuildNotesForCheckStubTextWrappedCaseTwoLines() {
        // Wrapped case, two lines
        List<PaymentNoteText> paymentNoteTexts2 = paymentSourceHelperService.buildNotesForCheckStubText("This line is over 90 characters so that we can see if it properly line breaks if we exceed MAX_NOTE_LINE_SIZE.", 1);
        Iterator<PaymentNoteText> iteratorPaymentNoteTexts2 = paymentNoteTexts2.iterator();
        PaymentNoteText iteratorPaymentNoteTexts2_1 = iteratorPaymentNoteTexts2.next();
        assertEquals(new KualiInteger(1), iteratorPaymentNoteTexts2_1.getCustomerNoteLineNbr());
        assertEquals("This line is over 90 characters so that we can see if it properly line breaks if we exceed", iteratorPaymentNoteTexts2_1.getCustomerNoteText());
        PaymentNoteText iteratorPaymentNoteTexts2_2 = iteratorPaymentNoteTexts2.next();
        assertEquals(new KualiInteger(2), iteratorPaymentNoteTexts2_2.getCustomerNoteLineNbr());
        assertEquals("MAX_NOTE_LINE_SIZE.", iteratorPaymentNoteTexts2_2.getCustomerNoteText());
    }

    public void testBuildNotesForCheckStubTextWrappedCaseAtLimitTwoLines() {
        // Wrapped case at limit, two lines
        List<PaymentNoteText> paymentNoteTexts3 = paymentSourceHelperService.buildNotesForCheckStubText("This line is over 90 characters so that we can see if it properly line breaks if we- exceed MAX_NOTE_LINE_SIZE.", 1);
        Iterator<PaymentNoteText> iteratorPaymentNoteTexts3 = paymentNoteTexts3.iterator();
        PaymentNoteText iteratorPaymentNoteTexts3_1 = iteratorPaymentNoteTexts3.next();
        assertEquals(new KualiInteger(1), iteratorPaymentNoteTexts3_1.getCustomerNoteLineNbr());
        assertEquals("This line is over 90 characters so that we can see if it properly line breaks if we-", iteratorPaymentNoteTexts3_1.getCustomerNoteText());
        PaymentNoteText iteratorPaymentNoteTexts3_2 = iteratorPaymentNoteTexts3.next();
        assertEquals(new KualiInteger(2), iteratorPaymentNoteTexts3_2.getCustomerNoteLineNbr());
        assertEquals("exceed MAX_NOTE_LINE_SIZE.", iteratorPaymentNoteTexts3_2.getCustomerNoteText());
    }

    public void testBuildNotesForCheckStubTextWrappedCaseThreeLinesNewlinesForced() {
        // Wrapped case, three lines, newlines forced in input
        List<PaymentNoteText> paymentNoteTexts4 = paymentSourceHelperService.buildNotesForCheckStubText("Super Man\n File number: 1234567890\n Deduction Dode: 10", 1);
        Iterator<PaymentNoteText> iteratorPaymentNoteTexts4 = paymentNoteTexts4.iterator();
        PaymentNoteText iteratorPaymentNoteTexts4_1 = iteratorPaymentNoteTexts4.next();
        assertEquals(new KualiInteger(1), iteratorPaymentNoteTexts4_1.getCustomerNoteLineNbr());
        assertEquals("Super Man", iteratorPaymentNoteTexts4_1.getCustomerNoteText());
        PaymentNoteText iteratorPaymentNoteTexts4_2 = iteratorPaymentNoteTexts4.next();
        assertEquals(new KualiInteger(2), iteratorPaymentNoteTexts4_2.getCustomerNoteLineNbr());
        assertEquals("File number: 1234567890", iteratorPaymentNoteTexts4_2.getCustomerNoteText());
        PaymentNoteText iteratorPaymentNoteTexts4_3 = iteratorPaymentNoteTexts4.next();
        assertEquals(new KualiInteger(3), iteratorPaymentNoteTexts4_3.getCustomerNoteLineNbr());
        assertEquals("Deduction Dode: 10", iteratorPaymentNoteTexts4_3.getCustomerNoteText());
    }

    public void testBuildNotesForCheckStubTextNoWrappingOneLineStartingLine5() {
        // No wrapping, one line, starting line number at 5
        List<PaymentNoteText> paymentNoteTexts5 = paymentSourceHelperService.buildNotesForCheckStubText("Super Man File number: 1234567890 Deduction Dode: 10", 5);
        Iterator<PaymentNoteText> iteratorPaymentNoteTexts5 = paymentNoteTexts5.iterator();
        PaymentNoteText iteratorPaymentNoteTexts5_1 = iteratorPaymentNoteTexts5.next();
        assertEquals(new KualiInteger(5), iteratorPaymentNoteTexts5_1.getCustomerNoteLineNbr());
        assertEquals("Super Man File number: 1234567890 Deduction Dode: 10", iteratorPaymentNoteTexts5_1.getCustomerNoteText());
    }

    public void testBuildNotesForCheckStubTextWrappedExceedingMaxNoteLineSize() {
        // Wrapped case due to exceeding MAX_NOTE_LINE_SIZE
        List<PaymentNoteText> paymentNoteTexts6 = paymentSourceHelperService.buildNotesForCheckStubText("0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789", 1);
        Iterator<PaymentNoteText> iteratorPaymentNoteTexts6 = paymentNoteTexts6.iterator();
        PaymentNoteText iteratorPaymentNoteTexts6_1 = iteratorPaymentNoteTexts6.next();
        assertEquals(new KualiInteger(1), iteratorPaymentNoteTexts6_1.getCustomerNoteLineNbr());
        assertEquals("012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789", iteratorPaymentNoteTexts6_1.getCustomerNoteText());
        PaymentNoteText iteratorPaymentNoteTexts6_2 = iteratorPaymentNoteTexts6.next();
        assertEquals(new KualiInteger(2), iteratorPaymentNoteTexts6_2.getCustomerNoteLineNbr());
        assertEquals("0123456789", iteratorPaymentNoteTexts6_2.getCustomerNoteText());
    }
}
