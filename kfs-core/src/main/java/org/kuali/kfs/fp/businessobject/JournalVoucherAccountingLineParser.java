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

package org.kuali.kfs.fp.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.krad.service.BusinessObjectService;

import java.util.List;
import java.util.Map;

import static org.kuali.kfs.sys.KFSPropertyConstants.*;


/**
 * This class represents a <code>JournalVoucherDocument</code> accounting line parser.
 */
public class JournalVoucherAccountingLineParser extends AuxiliaryVoucherAccountingLineParser {
    private String balanceTypeCode;
    private final Integer currentUniversityFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    protected static final String[] NON_OFFSET_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, OBJECT_TYPE_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, FINANCIAL_DOCUMENT_LINE_DESCRIPTION, ORGANIZATION_REFERENCE_ID, AMOUNT };
    protected static final String[] OFFSET_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, OBJECT_TYPE_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, FINANCIAL_DOCUMENT_LINE_DESCRIPTION, ORGANIZATION_REFERENCE_ID, DEBIT, CREDIT };
    protected static final String[] ENCUMBRANCE_FORMAT = { CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, OBJECT_TYPE_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, ENCUMBRANCE_UPDATE_CODE, FINANCIAL_DOCUMENT_LINE_DESCRIPTION, REFERENCE_ORIGIN_CODE, REFERENCE_TYPE_CODE, REFERENCE_NUMBER, DEBIT, CREDIT };

    /**
     * Constructs a JournalVoucherAccountingLineParser.java.
     *
     * @param balanceTypeCode
     */
    public JournalVoucherAccountingLineParser(String balanceTypeCode) {
        super();
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParserBase#performCustomSourceAccountingLinePopulation(java.util.Map,
     * org.kuali.rice.krad.bo.SourceAccountingLine, java.lang.String)
     */
    @Override
    protected void performCustomSourceAccountingLinePopulation(Map<String, String> attributeValueMap, SourceAccountingLine sourceAccountingLine, String accountingLineAsString) {

        boolean isFinancialOffsetGeneration = SpringContext.getBean(BalanceTypeService.class).getBalanceTypeByCode(balanceTypeCode).isFinancialOffsetGenerationIndicator();
        if (isFinancialOffsetGeneration || StringUtils.equals(balanceTypeCode, SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(SystemOptions.class, currentUniversityFiscalYear).getExtrnlEncumFinBalanceTypCd())) {
            super.performCustomSourceAccountingLinePopulation(attributeValueMap, sourceAccountingLine, accountingLineAsString);
        }
        sourceAccountingLine.setBalanceTypeCode(balanceTypeCode);
    }

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(selectFormat());
    }

    /**
     * chooses line format based on balance type code
     *
     * @return String []
     */
    private String[] selectFormat() {
        if (isEncumbranceBalanceType()) {
            return ENCUMBRANCE_FORMAT;
        } else if (SpringContext.getBean(BalanceTypeService.class).getBalanceTypeByCode(balanceTypeCode).isFinancialOffsetGenerationIndicator()) {
            return OFFSET_FORMAT;
        } else {
            return NON_OFFSET_FORMAT;
        }
    }
    
    private boolean isEncumbranceBalanceType() {
    	List<String> encumbranceBalanceTypes = SpringContext.getBean(BalanceTypeService.class).getEncumbranceBalanceTypes(currentUniversityFiscalYear);
    	for(String encumbranceBalanceType : encumbranceBalanceTypes) {
    		if(StringUtils.equals(balanceTypeCode, encumbranceBalanceType)) {
    			return true;
    		}
    	}
    	return false;
    }
}
