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
package org.kuali.kfs.fp.document;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.UniversityDateService;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities to help with the persistence of previous year object codes for year end tests
 */
public class YearEndObjectCodePersistenceUtils {
    /**
     * Saves the object code for a fiscal year
     *
     * @param accountingLine the accounting line which has the object code to save, but for the previous year
     */
    protected static void persistPreviousYearObjectCode(AccountingLine accountingLine, Set<String> storedObjectCodes) {
        ObjectCode objectCode = new ObjectCode();

        final Integer postingYear = new Integer(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().intValue() - 1);
        final ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);

        final ObjectCode existingObjectCode = objectCodeService.getByPrimaryId(postingYear, accountingLine.getChartOfAccountsCode(), accountingLine.getFinancialObjectCode());

        if (existingObjectCode == null) {
            objectCode.setUniversityFiscalYear(postingYear);
            objectCode.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
            objectCode.setFinancialObjectCode(accountingLine.getFinancialObjectCode());

            if (!storedObjectCodes.contains(buildObjectCodeString(objectCode))) {
                final ObjectCode alObjectCode = accountingLine.getObjectCode();
                copyObjectCodeNonPKProperties(objectCode, alObjectCode);

                SpringContext.getBean(BusinessObjectService.class).save(objectCode);
                storedObjectCodes.add(buildObjectCodeString(objectCode));
            }
        }

        // now, find the offset object code and add that if needed
        final OffsetDefinitionService offsetDefinitionService = SpringContext.getBean(OffsetDefinitionService.class);
        final OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(accountingLine.getPostingYear(), accountingLine.getChartOfAccountsCode(), "YEGE", KFSConstants.BALANCE_TYPE_ACTUAL);
        if (offsetDefinition != null) {
            final ObjectCode offsetObjectCode = objectCodeService.getByPrimaryId(offsetDefinition.getUniversityFiscalYear(), offsetDefinition.getChartOfAccountsCode(), offsetDefinition.getFinancialObjectCode());
            if (offsetObjectCode != null) {
                final ObjectCode existingOffsetObjectCode = objectCodeService.getByPrimaryId(postingYear, offsetDefinition.getChartOfAccountsCode(), offsetDefinition.getFinancialObjectCode());
                if (existingOffsetObjectCode == null) {
                    ObjectCode newOffsetObjectCode = new ObjectCode();
                    newOffsetObjectCode.setUniversityFiscalYear(postingYear);
                    newOffsetObjectCode.setChartOfAccountsCode(offsetObjectCode.getChartOfAccountsCode());
                    newOffsetObjectCode.setFinancialObjectCode(offsetObjectCode.getFinancialObjectCode());
                    if (!storedObjectCodes.contains(buildObjectCodeString(newOffsetObjectCode))) {
                        copyObjectCodeNonPKProperties(newOffsetObjectCode, offsetObjectCode);
                        SpringContext.getBean(BusinessObjectService.class).save(newOffsetObjectCode);
                        storedObjectCodes.add(buildObjectCodeString(newOffsetObjectCode));
                    }
                }
            }
        }
    }

    /**
     * Copies the non primary key properties on an object code from the source to the target
     *
     * @param target the object code to copy to
     * @param source the object code to copy from
     */
    protected static void copyObjectCodeNonPKProperties(ObjectCode target, ObjectCode source) {
        target.setName(source.getName());
        target.setActive(true);
        target.setFinancialObjectLevelCode(source.getFinancialObjectLevelCode());
        target.setReportsToChartOfAccountsCode(source.getReportsToChartOfAccountsCode());
        target.setReportsToFinancialObjectCode(source.getReportsToFinancialObjectCode());
        target.setFinancialObjectTypeCode(source.getFinancialObjectTypeCode());
        target.setFinancialObjectSubTypeCode(source.getFinancialObjectSubTypeCode());
        target.setFinancialBudgetAggregationCd(source.getFinancialBudgetAggregationCd());
        target.setNextYearFinancialObjectCode(source.getNextYearFinancialObjectCode());
        target.setFinObjMandatoryTrnfrelimCd(source.getFinObjMandatoryTrnfrelimCd());
        target.setFinancialFederalFundedCode(source.getFinancialFederalFundedCode());
    }

    /**
     * Persists all object codes used on a document in the previous year
     *
     * @param document the document with object codes to persist to the previous year
     */
    public static Set<String> persistPreviousYearObjectCodesForDocument(AccountingDocument document) {
        Set<String> storedObjectCodes = new HashSet<String>();
        for (Object accountingLineAsObject : document.getSourceAccountingLines()) {
            final AccountingLine accountingLine = (AccountingLine) accountingLineAsObject;
            persistPreviousYearObjectCode(accountingLine, storedObjectCodes);
        }
        for (Object accountingLineAsObject : document.getTargetAccountingLines()) {
            final AccountingLine accountingLine = (AccountingLine) accountingLineAsObject;
            persistPreviousYearObjectCode(accountingLine, storedObjectCodes);
        }
        return storedObjectCodes;
    }

    /**
     * Deletes all of the object codes saved to make this test work
     *
     * @param objectCodesToDelete the object codes to remove
     */
    public static void removePreviousYearObjectCodes(Set<String> objectCodesToDelete) {
        final BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        for (String objectCodeRepresenation : objectCodesToDelete) {
            final ObjectCode objectCode = retrieveObjectCodeFromRepresentation(objectCodeRepresenation);
            boService.delete(objectCode);
        }
    }

    /**
     * Builds a unique and complete String representation of the PK of an object code
     *
     * @param objectCode the object code to build a represenation of
     * @return a unique and complete representation of the PK of an object code
     */
    protected static String buildObjectCodeString(ObjectCode objectCode) {
        return objectCode.getUniversityFiscalYear() + "::" + objectCode.getChartOfAccountsCode() + "::" + objectCode.getFinancialObjectCode();
    }

    /**
     * From the given representation, builds an object code with all the primary key fields filled in, so it can be deleted
     *
     * @return an object code with PK fields filled in
     */
    protected static ObjectCode retrieveObjectCodeFromRepresentation(String objectCodeRepresenation) {
        Pattern pat = Pattern.compile("(\\d+)\\:\\:([^\\:]+)\\:\\:(.+)");
        Matcher mat = pat.matcher(objectCodeRepresenation);
        if (mat.matches()) {
            ObjectCode objectCode = new ObjectCode();
            objectCode.setUniversityFiscalYear(new Integer(mat.group(1)));
            objectCode.setChartOfAccountsCode(mat.group(2));
            objectCode.setFinancialObjectCode(mat.group(3));
            objectCode.setVersionNumber(1L);
            return objectCode;
        }
        return null;
    }
}
