/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.exception.ValidationException;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.BusinessObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractsGrantsInvoiceDocumentErrorLogLookupableHelperServiceImpl extends AccountsReceivableLookupableHelperServiceImplBase {

    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected DateTimeService dateTimeService;

    /**
     * Overridden so we can manipulate the search criteria fields.
     *
     * @see org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return getSearchResultsHelper(
                org.kuali.kfs.krad.lookup.LookupUtils.forceUppercase(getBusinessObjectClass(), updateFieldValuesForSearchCriteria(fieldValues)), false);
    }


    /**
     * Overridden so we can manipulate the search criteria fields.
     *
     * @see org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getSearchResultsHelper(
                org.kuali.kfs.krad.lookup.LookupUtils.forceUppercase(getBusinessObjectClass(), updateFieldValuesForSearchCriteria(fieldValues)), true);
    }

    /**
     * Manipulate fields for search criteria in order to get the results the user really wants.
     *
     * @param fieldValues
     * @return updated search criteria fieldValues
     */
    protected Map<String, String> updateFieldValuesForSearchCriteria(Map<String, String> fieldValues) {
        Map<String, String> newFieldValues = new HashMap<>();
        newFieldValues.putAll(fieldValues);

        // Add wildcard character to start and end of accounts field so users can search for single account
        // within the delimited list of accounts without having to add the wildcards explicitly themselves.
        String accounts = newFieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ACCOUNTS);
        if (StringUtils.isNotBlank(accounts)) {
            // only add wildcards if they haven't already been added (for some reason this method gets called twice when generating the pdf report)
            if (!StringUtils.startsWith(accounts, KFSConstants.WILDCARD_CHARACTER)) {
                accounts = KFSConstants.WILDCARD_CHARACTER + accounts;
            }
            if (!StringUtils.endsWith(accounts, KFSConstants.WILDCARD_CHARACTER)) {
                accounts += KFSConstants.WILDCARD_CHARACTER;
            }
        }
        newFieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ACCOUNTS, accounts);

        // Increment to error date by one day so that the correct results are retrieved.
        // Since the error date is stored as both a date and time in the database records with an error date
        // the same as the error date the user enters on the search criteria aren't retrieved without this modification.
        String errorDate = newFieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO);

        int index = StringUtils.indexOf(errorDate, SearchOperator.LESS_THAN_EQUAL.toString());
        if (index == StringUtils.INDEX_NOT_FOUND) {
            index = StringUtils.indexOf(errorDate, SearchOperator.BETWEEN.toString());
            if (index != StringUtils.INDEX_NOT_FOUND) {
                incrementErrorDate(newFieldValues, errorDate, index);
            }
        } else {
            incrementErrorDate(newFieldValues, errorDate, index);
        }

        return newFieldValues;
    }

    /**
     * Add one day to the error date field.
     *
     * @param newFieldValues Map of field values for search criteria to be modified
     * @param errorDate String date from original fieldValues
     * @param index index of date prefix used to get actual date portion of errorDate string
     */
    protected void incrementErrorDate(Map<String, String> newFieldValues, String errorDate, int index) {
        String errorDatePrefix = errorDate.substring(0, index + 2);
        String newDateString = contractsGrantsReportHelperService.correctEndDateForTime(errorDate.substring(index + 2));
        if (StringUtils.isNotBlank(errorDate.substring(index + 2))) {
            newFieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO, errorDatePrefix + newDateString);
        }
    }

    @Override
    public void validateSearchParameters(Map<String,String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        String proposalNumber = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);

        String awardBeginningDateFromString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_FROM);
        String awardBeginningDateToString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_TO);
        String awardEndingpDateFromString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_FROM);
        String awardEndingDateToString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_TO);
        String awardTotalAmount = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_TOTAL_AMOUNT);
        String cumulativeExpensesAmount = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.CUMULATIVE_EXPENSES_AMOUNT);
        String errorDateFromString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_FROM);
        String errorDateToString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO);
        String primaryFundManagerPrincipalId = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PRIMARY_FUND_MANAGER_PRINCIPAL_NAME);

        final String awardBeginningDateLabel = lookupPropertyLabel(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_TO);
        validateDate(awardBeginningDateFromString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_FROM, awardBeginningDateLabel+ArConstants.FROM_SUFFIX);
        validateDate(awardBeginningDateToString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_TO, awardBeginningDateLabel+ArConstants.TO_SUFFIX);

        final String awardEndingDateLabel = lookupPropertyLabel(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_TO);
        validateDate(awardEndingpDateFromString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_FROM, awardEndingDateLabel+ArConstants.FROM_SUFFIX);
        validateDate(awardEndingDateToString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_TO, awardEndingDateLabel+ArConstants.TO_SUFFIX);

        if (StringUtils.isNotBlank(awardTotalAmount) && !KualiDecimal.isNumeric(awardTotalAmount)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_TOTAL_AMOUNT, KFSKeyConstants.ERROR_NUMERIC, lookupPropertyLabel(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_TOTAL_AMOUNT));
        }

        if (StringUtils.isNotBlank(cumulativeExpensesAmount) && !KualiDecimal.isNumeric(cumulativeExpensesAmount)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.CUMULATIVE_EXPENSES_AMOUNT, KFSKeyConstants.ERROR_NUMERIC, lookupPropertyLabel(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.CUMULATIVE_EXPENSES_AMOUNT));
        }

        final String errorDateLabel = lookupPropertyLabel(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO);
        validateDate(errorDateFromString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_FROM, errorDateLabel+ArConstants.FROM_SUFFIX);
        validateDate(errorDateToString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO, errorDateLabel+ArConstants.TO_SUFFIX);

        if (StringUtils.isNotEmpty(primaryFundManagerPrincipalId)) {
            Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(primaryFundManagerPrincipalId);
            if (ObjectUtils.isNull(person)) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PRIMARY_FUND_MANAGER_PRINCIPAL_NAME, ArKeyConstants.NO_PRINCIPAL_NAME_FOUND, lookupPropertyLabel(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PRIMARY_FUND_MANAGER_PRINCIPAL_NAME));
            }
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("Error(s) in search criteria");
        }
    }

    /**
     * Looks up the attribute label for a propery of ContractsGrantsInvoiceDocumentErrorLog
     * @param propertyName the property name to look up
     * @return the label
     */
    protected String lookupPropertyLabel(String propertyName) {
        final String attributeLabel = getDataDictionaryService().getAttributeLabel(ContractsGrantsInvoiceDocumentErrorLog.class, propertyName);
        return attributeLabel;
    }


    /**
     * Validate date, adding error message to global message map with appropriate field name and field label if invalid.
     *
     * @param dateString
     * @param fieldName
     * @param fieldLabel
     */
    protected void validateDate(String dateString, String fieldName, String fieldLabel) {
        if (StringUtils.isNotBlank(dateString)) {
            try {
                dateTimeService.convertToDate(dateString);
            }
            catch (ParseException e) {
                GlobalVariables.getMessageMap().putError(fieldName, KFSKeyConstants.ERROR_DATE_TIME, fieldLabel);
            }
        }
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
