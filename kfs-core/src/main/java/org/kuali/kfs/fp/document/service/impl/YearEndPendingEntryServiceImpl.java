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
package org.kuali.kfs.fp.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.fp.document.YearEndDocument;
import org.kuali.kfs.fp.document.service.YearEndPendingEntryService;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.krad.document.TransactionalDocument;
import org.kuali.kfs.krad.util.ObjectUtils;

/**
 * The default implementation of the YearEndPendingEntryService
 */
public class YearEndPendingEntryServiceImpl implements YearEndPendingEntryService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(YearEndPendingEntryServiceImpl.class);

    protected static final String FINAL_ACCOUNTING_PERIOD = KFSConstants.MONTH13;

    protected UniversityDateService universityDateService;
    protected SufficientFundsService sufficientFundsService;
    protected OffsetDefinitionService offsetDefinitionService;

    /**
     * @see org.kuali.kfs.fp.document.service.YearEndPendingEntryService#customizeExplicitGeneralLedgerPendingEntry(org.kuali.rice.krad.document.TransactionalDocument, org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        if (!(transactionalDocument instanceof YearEndDocument)) {
            throw new IllegalArgumentException("invalid (not a year end document) for class:" + transactionalDocument.getClass());
        }
        YearEndDocument yearEndDocument = (YearEndDocument) transactionalDocument;
        explicitEntry.setUniversityFiscalPeriodCode(getAccountingPeriodForYearEndEntry());
        explicitEntry.setUniversityFiscalYear(getPreviousFiscalYear());
    }

    /**
     * @see org.kuali.kfs.fp.document.service.YearEndPendingEntryService#customizeOffsetGeneralLedgerPendingEntry(org.kuali.rice.krad.document.TransactionalDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public boolean customizeOffsetGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        if (!(transactionalDocument instanceof YearEndDocument)) {
            throw new IllegalArgumentException("invalid (not a year end document) for class:" + transactionalDocument.getClass());
        }
        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(getPreviousFiscalYear(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        if (!ObjectUtils.isNull(offsetDefinition)) {
            String offsetObjectCode = getOffsetFinancialObjectCode(offsetDefinition);
            offsetEntry.setFinancialObjectCode(offsetObjectCode);
            if (offsetObjectCode.equals(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode())) {
                // no BO, so punt
                offsetEntry.setAcctSufficientFundsFinObjCd(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode());
            }
            else {
                offsetDefinition.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
                ObjectCode financialObject = offsetDefinition.getFinancialObject();
                // The ObjectCode reference may be invalid because a flexible offset account changed its chart code.
                if (ObjectUtils.isNull(financialObject)) {
                    throw new RuntimeException("offset object code " + offsetEntry.getUniversityFiscalYear() + "-" + offsetEntry.getChartOfAccountsCode() + "-" + offsetEntry.getFinancialObjectCode());
                }
                offsetEntry.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                offsetEntry.setAcctSufficientFundsFinObjCd(sufficientFundsService.getSufficientFundsObjectCode(financialObject, offsetEntry.getAccount().getAccountSufficientFundsCode()));
            }

            offsetEntry.setFinancialObjectTypeCode(getOffsetFinancialObjectTypeCode(offsetDefinition));

            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.YearEndPendingEntryService#getFinalAccountingPeriod()
     */
    @Override
    public String getFinalAccountingPeriod() {
        return FINAL_ACCOUNTING_PERIOD;
    }

    /**
     * @return the accounting period the year end entry should be populated with
     */
    protected String getAccountingPeriodForYearEndEntry() {
        return getFinalAccountingPeriod();
    }

    /**
     * @see org.kuali.kfs.fp.document.service.YearEndPendingEntryService#getPreviousFiscalYear()
     */
    @Override
    public Integer getPreviousFiscalYear() {
        return universityDateService.getCurrentFiscalYear() - 1;
    }

    /**
     * Helper method that determines the offset entry's financial object code.
     *
     * @param offsetDefinition
     * @return String
     */
    protected String getOffsetFinancialObjectCode(OffsetDefinition offsetDefinition) {
        if (null != offsetDefinition) {
            String returnString = (!StringUtils.isBlank(offsetDefinition.getFinancialObjectCode())) ? offsetDefinition.getFinancialObjectCode() : AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode();
            return returnString;
        } else {
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode();
        }
    }

    /**
     * Helper method that determines the offset entry's financial object type code.
     *
     * @param offsetDefinition
     * @return String
     */
    protected String getOffsetFinancialObjectTypeCode(OffsetDefinition offsetDefinition) {
        if (null != offsetDefinition && null != offsetDefinition.getFinancialObject()) {
            String returnString = (!StringUtils.isBlank(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode())) ? offsetDefinition.getFinancialObject().getFinancialObjectTypeCode() : AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType();
            return returnString;
        } else {
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType();
        }

    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setSufficientFundsService(SufficientFundsService sufficientFundsService) {
        this.sufficientFundsService = sufficientFundsService;
    }

    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

}
