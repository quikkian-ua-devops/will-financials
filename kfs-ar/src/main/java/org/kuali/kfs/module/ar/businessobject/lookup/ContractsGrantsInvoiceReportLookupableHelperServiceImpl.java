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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for the Contracts & Grants Invoice Report.
 */
public class ContractsGrantsInvoiceReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected CustomerInvoiceDocumentService customerInvoiceDocumentService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected DateTimeService dateTimeService;

    /**
     * Validate the pattern for the ageInDays and remainingValue
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        validateSearchParametersForOperatorAndValue(fieldValues, ArPropertyConstants.AGE_IN_DAYS);
        validateSearchParametersForOperatorAndValue(fieldValues, ArPropertyConstants.REMAINING_AMOUNT);

        final String upperBoundInvoiceDueDate = fieldValues.get(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE);
        validateDateField(upperBoundInvoiceDueDate, ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, getDateTimeService());
        final String lowerBoundInvoiceDueDate = fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE);
        validateDateField(lowerBoundInvoiceDueDate, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, getDateTimeService());
        final String lowerBoundInvoiceDate = fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_FROM);
        validateDateField(lowerBoundInvoiceDate, ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_FROM, getDateTimeService());
        final String upperBoundInvoiceDate = fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_TO);
        validateDateField(upperBoundInvoiceDate, ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_TO, getDateTimeService());
    }

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsInvoiceReport> displayList = new ArrayList<ContractsGrantsInvoiceReport>();

        Map<String, String> invoiceLookupFields = buildCriteriaForInvoiceLookup(lookupFormFields);
        invoiceLookupFields.put(ArPropertyConstants.OPEN_INVOICE_IND, KRADConstants.YES_INDICATOR_VALUE);
        invoiceLookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, StringUtils.join(getFinancialSystemDocumentService().getSuccessfulDocumentStatuses(), SearchOperator.OR.op()));
        List<CustomerInvoiceDocument> openInvoiceDocs = new ArrayList<CustomerInvoiceDocument>();

        if (GlobalVariables.getMessageMap().getErrorCount() == 0) {
            final boolean docTypeCriteriaSpecified = invoiceLookupFields.containsKey(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME);
            if (!docTypeCriteriaSpecified) {
                invoiceLookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, ArConstants.INV_DOCUMENT_TYPE);
            }
            if (StringUtils.equals(invoiceLookupFields.get(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME), ArConstants.INV_DOCUMENT_TYPE)) {
                if (!invoiceLookupFields.containsKey(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER)) {
                    openInvoiceDocs.addAll(getLookupService().findCollectionBySearchHelper(CustomerInvoiceDocument.class, invoiceLookupFields, true));
                }
            }
            if (!docTypeCriteriaSpecified) {
                invoiceLookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
            }
            if (StringUtils.equals(invoiceLookupFields.get(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME), ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE)) {
                openInvoiceDocs.addAll(getLookupService().findCollectionBySearchHelper(ContractsGrantsInvoiceDocument.class, invoiceLookupFields, true));
            }
        }

        final String invoiceReportOption = lookupForm.getFields().remove(ArConstants.INVOICE_REPORT_OPTION);

        java.util.Date today = new java.util.Date();
        Date sqlToday = new java.sql.Date(today.getTime());

        OperatorAndValue ageInDaysOperator = buildOperatorAndValueFromField(lookupFormFields, ArPropertyConstants.AGE_IN_DAYS);
        OperatorAndValue remainingAmountOperator = buildOperatorAndValueFromField(lookupFormFields, ArPropertyConstants.REMAINING_AMOUNT);

        // build search result fields
        for (CustomerInvoiceDocument openCGInvoiceDoc : openInvoiceDocs) {
            if (invoiceReportOption.equals(ArConstants.PAST_DUE_INVOICES) && !openCGInvoiceDoc.getInvoiceDueDate().before(today)) {  // for past due, we only want invoices with a due date of yesterday or previous
                // check if due date is not passed then not add to result
                continue;
            }

            if (!ObjectUtils.isNull(ageInDaysOperator) && !ageInDaysOperator.applyComparison(openCGInvoiceDoc.getAge())) {
                continue; // skip this one
            }

            // get payment amount
            KualiDecimal paymentAmount = getCustomerInvoiceDocumentService().calculateAppliedPaymentAmount(openCGInvoiceDoc);
            KualiDecimal remainingAmount = openCGInvoiceDoc.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().subtract(paymentAmount);
            if (!ObjectUtils.isNull(remainingAmountOperator) && !remainingAmountOperator.applyComparison(remainingAmount)) {
                continue; // skp this one
            }

            FinancialSystemDocumentHeader documentHeader = (FinancialSystemDocumentHeader) openCGInvoiceDoc.getDocumentHeader();
            ContractsGrantsInvoiceReport cgInvoiceReport = new ContractsGrantsInvoiceReport();

            String documentNumber = ObjectUtils.isNull(documentHeader) ? "" : documentHeader.getDocumentNumber();
            cgInvoiceReport.setDocumentNumber(openCGInvoiceDoc.getDocumentNumber());
            if (openCGInvoiceDoc instanceof ContractsGrantsInvoiceDocument) {
                cgInvoiceReport.setProposalNumber(((ContractsGrantsInvoiceDocument)openCGInvoiceDoc).getInvoiceGeneralDetail().getProposalNumber());
            }
            cgInvoiceReport.setInvoiceType(documentHeader.getWorkflowDocumentTypeName());

            Date docCreateDate = documentHeader.getWorkflowCreateDate();
            cgInvoiceReport.setInvoiceDate(new java.sql.Date(docCreateDate.getTime()));
            cgInvoiceReport.setInvoiceDueDate(openCGInvoiceDoc.getInvoiceDueDate());
            if (openCGInvoiceDoc.isOpenInvoiceIndicator()) {
                cgInvoiceReport.setOpenInvoiceIndicator(ArConstants.ReportsConstants.INVOICE_INDICATOR_OPEN);
            }
            else {
                cgInvoiceReport.setOpenInvoiceIndicator(ArConstants.ReportsConstants.INVOICE_INDICATOR_CLOSE);
            }
            cgInvoiceReport.setCustomerNumber(openCGInvoiceDoc.getAccountsReceivableDocumentHeader().getCustomerNumber());
            cgInvoiceReport.setCustomerName(openCGInvoiceDoc.getAccountsReceivableDocumentHeader().getCustomer().getCustomerName());
            cgInvoiceReport.setInvoiceAmount(documentHeader.getFinancialDocumentTotalAmount());

            cgInvoiceReport.setPaymentAmount(paymentAmount);
            cgInvoiceReport.setRemainingAmount(remainingAmount);

            // calculate ageInDays : current date - created date
            cgInvoiceReport.setAgeInDays(openCGInvoiceDoc.getAge().longValue());

            displayList.add(cgInvoiceReport);
        }

        buildResultTable(lookupForm, displayList, resultTable);

        return displayList;
    }

    /**
     * Pulls fields which can go directly to lookup from the given lookupFormFields and turns them into a Map of only those fields which should be included in the lookup
     * @param lookupFormFields the fields directly from the lookup form to tweak into the fields the lookup service is going to want to use
     * @return the fields as the lookup service will positively react to them
     */
    protected Map<String, String> buildCriteriaForInvoiceLookup(Map lookupFormFields) {
        Map<String, String> lookupFields = new HashMap<String, String>();

        final String lowerBoundInvoiceDate = (String)lookupFormFields.remove(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_FROM);
        final String upperBoundInvoiceDate = (String)lookupFormFields.remove(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_TO);
        final String invoiceDateCriteria = getContractsGrantsReportHelperService().fixDateCriteria(lowerBoundInvoiceDate, upperBoundInvoiceDate, true);
        if (!StringUtils.isBlank(invoiceDateCriteria)) {
            lookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_CREATE_DATE, invoiceDateCriteria);
        }

        final String invoiceAmount = (String)lookupFormFields.remove(ArPropertyConstants.TransmitContractsAndGrantsInvoicesLookupFields.INVOICE_AMOUNT);
        if (!StringUtils.isBlank(invoiceAmount)) {
            lookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, invoiceAmount);
        }

        final String customerNumber = (String)lookupFormFields.remove(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER);
        if (!StringUtils.isBlank(customerNumber)) {
            lookupFields.put(ArPropertyConstants.ACCOUNTS_RECEIVABLE_DOCUMENT_HEADER+"."+ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customerNumber);
        }

        final String invoiceType = (String)lookupFormFields.remove(ArPropertyConstants.INVOICE_TYPE);
        if (!StringUtils.isBlank(invoiceType)) {
            lookupFields.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, invoiceType);
        }

        final String upperBoundInvoiceDueDate = (String)lookupFormFields.remove(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE);
        final String lowerBoundInvoiceDueDate = (String)lookupFormFields.remove(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE);
        final String invoiceDueDateCriteria = getContractsGrantsReportHelperService().fixDateCriteria(lowerBoundInvoiceDueDate, upperBoundInvoiceDueDate, false);
        if (!StringUtils.isBlank(invoiceDueDateCriteria)) {
            lookupFields.put(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DUE_DATE, invoiceDueDateCriteria);
        }

        final String proposalNumber = (String)lookupFormFields.remove(KFSPropertyConstants.PROPOSAL_NUMBER);
        if (!StringUtils.isBlank(proposalNumber)) {
            lookupFields.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, proposalNumber);
        }

        final String documentNumber = (String)lookupFormFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        if (!StringUtils.isBlank(documentNumber)) {
            lookupFields.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        }

        return lookupFields;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }
}
