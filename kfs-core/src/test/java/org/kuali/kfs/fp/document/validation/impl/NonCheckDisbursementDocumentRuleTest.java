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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.document.NonCheckDisbursementDocument;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.IsDebitTestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.ArrayList;
import java.util.List;

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testRouteDocumentRule_processRouteDocument;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testSaveDocumentRule_ProcessSaveDocument;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.ACCRUED_INCOME_LINE;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.ACCRUED_SICK_PAY_LINE;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE10;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE8;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.service.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.kfs.sys.service.IsDebitTestUtils.Amount.POSITIVE;

@ConfigureContext(session = khuntley)
public class NonCheckDisbursementDocumentRuleTest extends KualiTestBase {
    public static final Class<NonCheckDisbursementDocument> DOCUMENT_CLASS = NonCheckDisbursementDocument.class;


    public void testIsDebit_income_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_income_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_expense_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_expense_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_asset_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_asset_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_liability_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_liability_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));

    }

    public void testIsDebit_errorCorrection_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

//    public void testIsObjectTypeAllowed_InvalidObjectType() throws Exception {
//        testAddAccountingLineRule_IsObjectTypeAllowed(DOCUMENT_CLASS, getInvalidObjectTypeSourceLine(), false);
//    }
//
//    public void testIsObjectTypeAllowed_Valid() throws Exception {
//        testAddAccountingLineRule_IsObjectTypeAllowed(DOCUMENT_CLASS, getValidObjectTypeSourceLine(), true);
//    }
//
//    public void testIsObjectCodeAllowed_Valid() throws Exception {
//        testAddAccountingLineRule_IsObjectCodeAllowed(DOCUMENT_CLASS, getValidObjectCodeSourceLine(), true);
//    }
//
//    public void testIsObjectCodeAllowed_InvalidObjectCode() throws Exception {
//        testAddAccountingLineRule_IsObjectCodeAllowed(DOCUMENT_CLASS, getInvalidObjectCodeSourceLine(), false);
//    }
//
//    public void testAddAccountingLine_InvalidObjectSubType() throws Exception {
//        AccountingDocument doc = createDocumentWithInvalidObjectSubType();
//        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, false);
//    }

    public void testAddAccountingLine_Valid() throws Exception {
        AccountingDocument doc = createDocumentWithValidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
    }

//    public void testIsObjectSubTypeAllowed_InvalidSubType() throws Exception {
//        testAddAccountingLine_IsObjectSubTypeAllowed(DOCUMENT_CLASS, getInvalidObjectSubTypeTargetLine(), false);
//    }
//
//    public void testIsObjectSubTypeAllowed_ValidSubType() throws Exception {
//        testAddAccountingLine_IsObjectSubTypeAllowed(DOCUMENT_CLASS, getValidObjectSubTypeTargetLine(), true);
//    }

    public void testProcessSaveDocument_Valid() throws Exception {
        testSaveDocumentRule_ProcessSaveDocument(createDocument(), true);
    }

    public void testProcessSaveDocument_Invalid() throws Exception {
        testSaveDocumentRule_ProcessSaveDocument(createDocumentInvalidForSave(), false);
    }

    public void testProcessSaveDocument_Invalid1() throws Exception {
        try {
            testSaveDocumentRule_ProcessSaveDocument(null, false);
            fail("validated null doc");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testProcessRouteDocument_Valid() throws Exception {
        testRouteDocumentRule_processRouteDocument(createDocumentValidForRouting(), true);
    }

    public void testProcessRouteDocument_Invalid() throws Exception {
        testRouteDocumentRule_processRouteDocument(createDocument(), false);
    }

    public void testProcessRouteDocument_NoAccountingLines() throws Exception {
        testRouteDocumentRule_processRouteDocument(createDocument(), false);
    }

    private NonCheckDisbursementDocument createDocument() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
    }

    private NonCheckDisbursementDocument createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    private NonCheckDisbursementDocument createDocumentInvalidForSave() throws Exception {
        return getDocumentParameterNoDescription();
    }

    private NonCheckDisbursementDocument createDocumentWithValidObjectSubType() throws Exception {
        NonCheckDisbursementDocument retval = createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        retval.setDefaultBankCode();
        return retval;
    }

    private NonCheckDisbursementDocument getDocumentParameterNoDescription() throws Exception {
        NonCheckDisbursementDocument document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), NonCheckDisbursementDocument.class);
        document.getDocumentHeader().setDocumentDescription(null);
        return document;
    }

    private TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return getAccruedIncomeTargetLineParameter();
    }

    private TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return getAccruedSickPayTargetLineParameter();
    }

    private List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(getAccruedIncomeSourceLineParameter());
        retval.add(getAccruedIncomeSourceLineParameter());
        return retval;
    }

    private List<SourceAccountingLine> getInvalidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(getAccruedSickPaySourceLineParameter());
        retval.add(getAccruedSickPaySourceLineParameter());
        return retval;
    }

    private List<TargetAccountingLine> getInvalidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(getAccruedSickPayTargetLineParameter());
        retval.add(getAccruedSickPayTargetLineParameter());
        return retval;
    }

    private List<TargetAccountingLine> getValidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(getAccruedIncomeTargetLineParameter());
        retval.add(getAccruedIncomeTargetLineParameter());
        return retval;
    }

    private SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return LINE8.createSourceAccountingLine();
    }

    private SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        return getAccruedSickPaySourceLineParameter();
    }

    private SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        return LINE10.createSourceAccountingLine();
    }

    private SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return getAccruedIncomeSourceLineParameter();
    }

    private NonCheckDisbursementDocument createDocumentWithInvalidObjectSubType() throws Exception {
        NonCheckDisbursementDocument retval = createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getInvalidObjectSubTypeTargetLines());
        return retval;
    }

    private SourceAccountingLine getAccruedIncomeSourceLineParameter() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    private SourceAccountingLine getAccruedSickPaySourceLineParameter() throws Exception {
        return ACCRUED_SICK_PAY_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getAccruedIncomeTargetLineParameter() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(TargetAccountingLine.class, KFSConstants.GL_CREDIT_CODE);
    }

    private TargetAccountingLine getAccruedSickPayTargetLineParameter() throws Exception {
        return ACCRUED_SICK_PAY_LINE.createAccountingLine(TargetAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }
}

