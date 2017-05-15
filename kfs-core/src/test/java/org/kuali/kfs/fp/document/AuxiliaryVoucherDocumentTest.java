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
package org.kuali.kfs.fp.document;

import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE15;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

/**
 * This class is used to test NonCheckDisbursementDocumentTest.
 */
@ConfigureContext(session = khuntley)
public class AuxiliaryVoucherDocumentTest extends KualiTestBase {

    public static final Class<AuxiliaryVoucherDocument> DOCUMENT_CLASS = AuxiliaryVoucherDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        // AV document has a restriction on accounting period cannot be more than 2 periods behind current
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE15);
        return list;
    }

    private AuxiliaryVoucherDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        AuxiliaryVoucherDocument document = (AuxiliaryVoucherDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            SourceAccountingLine line = sourceFixture.createAccountingLine(SourceAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextSourceLineNumber());
            SourceAccountingLine balance = sourceFixture.createAccountingLine(SourceAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextSourceLineNumber());
            balance.setDebitCreditCode(GL_DEBIT_CODE.equals(line.getDebitCreditCode()) ? GL_CREDIT_CODE : GL_DEBIT_CODE);
            document.addSourceAccountingLine(line);
            document.addSourceAccountingLine(balance);

        }

        return document;
    }


    private int getExpectedPrePeCount() {
        return 2;
    }


    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = new ArrayList<TargetAccountingLine>();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size();
        AccountingDocumentTestUtils.testAddAccountingLine(DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    public final void testConvertIntoErrorCorrection_errorCorrectionDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_errorCorrectionDisallowed(buildDocument(), SpringContext.getBean(DataDictionaryService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        AccountingDocumentTestUtils.testRouteDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testSaveDocument() throws Exception {
        AccountingDocumentTestUtils.testSaveDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testConvertIntoCopy() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy(buildDocument(), SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
    }

    // test util mehtods
    private List<SourceAccountingLine> generateSouceAccountingLines() throws Exception {
        List<SourceAccountingLine> sourceLines = new ArrayList<SourceAccountingLine>();
        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceLines.add(sourceFixture.createSourceAccountingLine());
            sourceLines.add(sourceFixture.createAccountingLine(SourceAccountingLine.class, GL_DEBIT_CODE.equals(sourceFixture.debitCreditCode) ? GL_CREDIT_CODE : GL_DEBIT_CODE));
        }

        return sourceLines;
    }

}

