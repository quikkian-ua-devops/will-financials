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
package org.kuali.kfs.module.ar.report.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.LookupService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to get the services for PDF generation and other services for CG Aging report.
 */
public class ContractsGrantsAgingReportServiceImpl implements ContractsGrantsAgingReportService {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected BusinessObjectService businessObjectService;
    protected PersonService personService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected DateTimeService dateTimeService;
    protected LookupService lookupService;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingReportServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#filterContractsGrantsAgingReport(java.util.Map)
     */
    @Override
    @Transactional
    public Map<String, List<ContractsGrantsInvoiceDocument>> filterContractsGrantsAgingReport(Map fieldValues, java.sql.Date begin, java.sql.Date end) throws ParseException {
        Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs = retrieveMatchingContractsGrantsInvoiceDocuments(fieldValues, begin, end);
        Map<String, List<ContractsGrantsInvoiceDocument>> cgMapByCustomer = generateMapFromContractsGrantsInvoiceDocuments(contractsGrantsInvoiceDocs);
        return cgMapByCustomer;
    }

    /**
     * Utility method to lookup matching contracts & grants invoice documents for the given field values
     *
     * @param fieldValues the field values with criteria to lookup report on
     * @param begin       the begin da
     * @param end
     * @return
     */
    protected List<ContractsGrantsInvoiceDocument> retrieveMatchingContractsGrantsInvoiceDocuments(Map fieldValues, java.sql.Date begin, java.sql.Date end) {
        String reportOption = (String) fieldValues.get(ArPropertyConstants.REPORT_OPTION);
        String orgCode = (String) fieldValues.get(KFSPropertyConstants.ORGANIZATION_CODE);
        String chartCode = (String) fieldValues.get(ArPropertyConstants.PROCESSING_OR_BILLING_CHART_CODE);
        String customerNumber = (String) fieldValues.get(KFSPropertyConstants.CUSTOMER_NUMBER);
        String customerName = (String) fieldValues.get(KFSPropertyConstants.CUSTOMER_NAME);
        String accountNumber = (String) fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        String accountChartOfAccountsCode = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.ACCOUNT_CHART_CODE);
        String fundManager = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.FUND_MANAGER);
        String proposalNumber = (String) fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String collectorPrincName = (String) fieldValues.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);
        String collectorPrincipalId = null;

        List<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs = new ArrayList<>();

        if (!StringUtils.isBlank(collectorPrincName)) {
            Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
            if (ObjectUtils.isNull(collUser)) {
                return contractsGrantsInvoiceDocs; // if the principal name is not a real user, then return no values
            } else {
                collectorPrincipalId = collUser.getPrincipalId();
            }
        }

        if (!StringUtils.isBlank(fundManager)) {
            final Person fundManagerUser = getPersonService().getPersonByPrincipalName(fundManager);
            if (ObjectUtils.isNull(fundManagerUser)) {
                return contractsGrantsInvoiceDocs; // the fund manager doesn't exist, so empty results
            }
        }

        String awardDocumentNumber = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_DOCUMENT_NUMBER);
        String markedAsFinal = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.MARKED_AS_FINAL);
        String endDateString = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_END_DATE);

        String invoiceAmountFrom = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_AMT_FROM);
        String invoiceAmountTo = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_AMT_TO);
        String invoiceNumber = (String) fieldValues.get(ArPropertyConstants.INVOICE_NUMBER);

        String invoiceDateFromString = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_FROM);
        String invoiceDateToString = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_TO);
        String invoiceDateCriteria = getContractsGrantsReportHelperService().fixDateCriteria(invoiceDateFromString, invoiceDateToString, true);

        String responsibilityId = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.CG_ACCT_RESP_ID);

        String awardEndFromDate = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_END_DATE_FROM);
        String awardEndToDate = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_END_DATE_TO);

        Map<String, String> fieldValuesForInvoice = new HashMap<String, String>();
        fieldValuesForInvoice.put(ArPropertyConstants.OPEN_INVOICE_IND, KFSConstants.Booleans.TRUE);
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
        //Now to involve reportOption and handle chart and org
        if (ObjectUtils.isNotNull(reportOption)) {
            if (reportOption.equalsIgnoreCase(ArConstants.ReportOptionFieldValues.PROCESSING_ORG) && StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.PROCESSING_ORGANIZATION_CODE, orgCode);
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.PROCESSING_CHART_OF_ACCOUNT_CODE, chartCode);
            }
            if (reportOption.equalsIgnoreCase(ArConstants.ReportOptionFieldValues.BILLING_ORG) && StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, orgCode);
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT_CODE, chartCode);
            }
        }
        if (!StringUtils.isBlank(customerNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        }
        if (!StringUtils.isBlank(customerName)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CUSTOMER_NAME, customerName);
        }
        if (!StringUtils.isBlank(proposalNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, proposalNumber);
        }
        if (!StringUtils.isBlank(markedAsFinal)) {
            if (markedAsFinal.equalsIgnoreCase(KFSConstants.ParameterValues.YES)) {
                fieldValuesForInvoice.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.FINAL_BILL, KFSConstants.Booleans.TRUE);
            } else if (markedAsFinal.equalsIgnoreCase(KFSConstants.ParameterValues.NO)) {
                fieldValuesForInvoice.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.FINAL_BILL, KFSConstants.Booleans.FALSE);
            }
        }
        if (!StringUtils.isBlank(invoiceNumber)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.DOCUMENT_NUMBER, invoiceNumber);
        }
        if (!StringUtils.isBlank(responsibilityId)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CG_ACCT_RESP_ID, responsibilityId);
        }

        if (!StringUtils.isBlank(accountChartOfAccountsCode)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountChartOfAccountsCode);
        }
        if (!StringUtils.isBlank(accountNumber)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES + "." + KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        }

        if (!StringUtils.isBlank(invoiceDateCriteria)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_CREATE_DATE, invoiceDateCriteria);
        }

        final String sourceTotalCriteria = getAmountCriteria(invoiceAmountFrom, invoiceAmountTo);
        if (!StringUtils.isBlank(sourceTotalCriteria)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.SOURCE_TOTAL, sourceTotalCriteria);
        }

        String billingBeginDateString = null;
        if (begin != null) {
            billingBeginDateString = getDateTimeService().toDateString(begin);
        }
        String billingEndDateString = null;
        if (end != null) {
            billingEndDateString = getDateTimeService().toDateString(end);
        }
        final String billingDateCriteria = getContractsGrantsReportHelperService().fixDateCriteria(billingBeginDateString, billingEndDateString, false);
        if (!StringUtils.isBlank(billingDateCriteria)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE, billingDateCriteria);
        }

        final Set<String> awardIds = lookupBillingAwards(awardDocumentNumber, awardEndFromDate, awardEndToDate, fundManager);

        // here put all criterias and find the docs
        contractsGrantsInvoiceDocs.addAll(getLookupService().findCollectionBySearch(ContractsGrantsInvoiceDocument.class, fieldValuesForInvoice));

        filterContractsGrantsInvoiceDocumentsByAwardAndCollector(contractsGrantsInvoiceDocs, collectorPrincipalId, awardIds);

        return contractsGrantsInvoiceDocs;
    }

    /**
     * Removes contracts & grants invoice documents from the given collection if they do not match the given award ids or if the collector permissions do not allow viewing of the document
     *
     * @param contractsGrantsInvoiceDocs a Collection of ContractsGrantsInvoiceDocuments
     * @param collectorPrincipalId       the principal name of the collector
     * @param awardIds                   a Set of proposal numbers of awards which match given criteria
     */
    protected void filterContractsGrantsInvoiceDocumentsByAwardAndCollector(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs, String collectorPrincipalId, Set<String> awardIds) {
        // filter by collector and user performing the search
        Person user = GlobalVariables.getUserSession().getPerson();

        if (!CollectionUtils.isEmpty(contractsGrantsInvoiceDocs)) {
            for (Iterator<ContractsGrantsInvoiceDocument> iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext(); ) {
                ContractsGrantsInvoiceDocument document = iter.next();
                if (!ObjectUtils.isNull(document.getInvoiceGeneralDetail()) && !ObjectUtils.isNull(document.getInvoiceGeneralDetail().getAward()) && awardIds != null && !awardIds.contains(document.getInvoiceGeneralDetail().getAward().getProposalNumber())) {
                    iter.remove();
                } else if (StringUtils.isNotEmpty(collectorPrincipalId)) {
                    if (!contractsGrantsInvoiceDocumentService.canViewInvoice(document, collectorPrincipalId)) {
                        iter.remove();
                    }
                } else if (!contractsGrantsInvoiceDocumentService.canViewInvoice(document, user.getPrincipalId())) {
                    iter.remove();
                }
            }
        }
    }

    /**
     * Generates a Map keyed by customer number and name from a Collection of ContractsGrantsInvoiceDocuments
     *
     * @param contractsGrantsInvoiceDocs a Collection of ContractsGrantsInvoiceDocuments to convert into a Map
     * @return a Map of CINV docs, keyed by customer number and name
     */
    protected Map<String, List<ContractsGrantsInvoiceDocument>> generateMapFromContractsGrantsInvoiceDocuments(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs) {
        Map<String, List<ContractsGrantsInvoiceDocument>> cgMapByCustomer = null;
        if (!CollectionUtils.isEmpty(contractsGrantsInvoiceDocs)) {
            cgMapByCustomer = new HashMap<String, List<ContractsGrantsInvoiceDocument>>();
            for (ContractsGrantsInvoiceDocument cgDoc : contractsGrantsInvoiceDocs) {
                List<ContractsGrantsInvoiceDocument> cgInvoiceDocs;
                String customerNbr = cgDoc.getCustomer().getCustomerNumber();
                String customerNm = cgDoc.getCustomer().getCustomerName();
                String key = customerNbr + "-" + customerNm;
                if (cgMapByCustomer.containsKey(key)) {
                    cgInvoiceDocs = cgMapByCustomer.get(key);
                } else {
                    cgInvoiceDocs = new ArrayList<ContractsGrantsInvoiceDocument>();
                }
                cgInvoiceDocs.add(cgDoc);
                cgMapByCustomer.put(key, cgInvoiceDocs);
            }
        }
        return cgMapByCustomer;
    }

    /**
     * Generates a Set of proposal ids for awards which match the given criteria
     *
     * @param awardDocumentNumber the document number of the award
     * @param awardEndFromDate    the award ending date of the award
     * @param awardEndToDate      the award ending date of the award
     * @param fundManager         the principal name of the fund manager
     * @return a Set of Award ids to filter on, or null if no search was actually completed
     */
    protected Set<String> lookupBillingAwards(String awardDocumentNumber, String awardEndFromDate, String awardEndToDate, String fundManager) {
        if (StringUtils.isBlank(awardDocumentNumber) && StringUtils.isBlank(awardEndFromDate) && StringUtils.isBlank(awardEndToDate) && StringUtils.isBlank(fundManager)) {
            return null; // nothing to search on?  then return null to note that no search was completed
        }

        final Set<String> fundManagerIds = getContractsGrantsReportHelperService().lookupPrincipalIds(fundManager);

        Map<String, String> fieldValues = new HashMap<>();
        if (!StringUtils.isBlank(awardDocumentNumber)) {
            fieldValues.put(KFSPropertyConstants.AWARD_DOCUMENT_NUMBER, awardDocumentNumber);
        }

        final String awardEnd = getContractsGrantsReportHelperService().fixDateCriteria(awardEndFromDate, awardEndToDate, false);
        if (!StringUtils.isBlank(awardEnd)) {
            fieldValues.put(KFSPropertyConstants.AWARD_ENDING_DATE, awardEnd);
        }
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        final List<? extends ContractsAndGrantsAward> awards = getContractsAndGrantsModuleBillingService().lookupAwards(fieldValues, true);

        Set<String> billingAwardIds = new HashSet<>();
        for (ContractsAndGrantsAward award : awards) {
            if (award instanceof ContractsAndGrantsBillingAward) {
                final ContractsAndGrantsBillingAward cgbAward = (ContractsAndGrantsBillingAward) award;
                if (ObjectUtils.isNull(cgbAward.getAwardPrimaryFundManager()) || fundManagerIds.isEmpty() || fundManagerIds.contains(cgbAward.getAwardPrimaryFundManager().getPrincipalId())) {
                    billingAwardIds.add(cgbAward.getProposalNumber());
                }
            }
        }
        return billingAwardIds;
    }

    /**
     * Turns a from amount and to amount into a lookupable criteria
     *
     * @param fromAmount the lower bound amount
     * @param toAmount   the upper bound amount
     * @return a lookupable criteria
     */
    protected String getAmountCriteria(String fromAmount, String toAmount) {
        if (!StringUtils.isBlank(fromAmount)) {
            if (!StringUtils.isBlank(toAmount)) {
                return fromAmount + SearchOperator.BETWEEN.op() + toAmount;
            } else {
                return SearchOperator.GREATER_THAN_EQUAL.op() + fromAmount;
            }
        } else if (!StringUtils.isBlank(toAmount)) {
            return SearchOperator.LESS_THAN_EQUAL.op() + toAmount;
        }
        return null;
    }

    /**
     * This method is used to get the Invoice Details by account number and chart code
     *
     * @param accountChartCode
     * @param accountNumber
     * @return a List of the CustomerInvoiceDetails associated with a given Account Number
     */
    @SuppressWarnings("unchecked")
    protected Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsByAccountNumber(String accountChartCode, String accountNumber) {
        Map<String, Object> args = new HashMap<>();
        if (!StringUtils.isBlank(accountNumber)) {
            args.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        }
        if (!StringUtils.isBlank(accountChartCode)) {
            args.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountChartCode);
        }
        return businessObjectService.findMatching(CustomerInvoiceDetail.class, args);
    }

    /**
     * Figures out the reportRunDate and then uses filterContractsGrantsAgingReport to look up the right documents
     *
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#lookupContractsGrantsInvoiceDocumentsForAging(java.util.Map)
     */
    @Override
    public List<ContractsGrantsInvoiceDocument> lookupContractsGrantsInvoiceDocumentsForAging(Map<String, Object> fieldValues) {
        try {
            java.util.Date today = getDateTimeService().getCurrentDate();
            String reportRunDateStr = (String) fieldValues.get(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE);

            java.util.Date reportRunDate = (ObjectUtils.isNull(reportRunDateStr) || reportRunDateStr.isEmpty()) ?
                today :
                getDateTimeService().convertToDate(reportRunDateStr);

            // retrieve filtered data according to the lookup
            List<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocuments = retrieveMatchingContractsGrantsInvoiceDocuments(fieldValues, null, new java.sql.Date(reportRunDate.getTime()));
            return contractsGrantsInvoiceDocuments;

        } catch (ParseException ex) {
            throw new RuntimeException("Could not parse report run date for lookup", ex);
        }
    }

    @NonTransactional
    public PersonService getPersonService() {
        return personService;
    }

    @NonTransactional
    public void setPersonService(PersonService personService) {
        this.personService = personService;
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

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }

    public LookupService getLookupService() {
        return lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    @NonTransactional
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the contractsGrantsInvoiceDocumentService attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentService.
     */
    @NonTransactional
    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    /**
     * Sets the collectorHierarchyDao attribute value.
     *
     * @param collectorHierarchyDao The collectorHierarchyDao to set.
     */
    @NonTransactional
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }
}
