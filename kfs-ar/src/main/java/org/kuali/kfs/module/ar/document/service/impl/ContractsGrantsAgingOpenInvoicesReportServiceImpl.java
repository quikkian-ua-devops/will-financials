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
package org.kuali.kfs.module.ar.document.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsAgingOpenInvoicesReport;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsAgingOpenInvoicesReportService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.util.ObjectUtils;

/**
 * This class is used to get the services for PDF generation and other services for Contracts & Grants Aging open Invoices report
 */
public class ContractsGrantsAgingOpenInvoicesReportServiceImpl implements ContractsGrantsAgingOpenInvoicesReportService {
    protected ContractsGrantsAgingReportService contractsGrantsAgingReportService;
    protected CustomerInvoiceDocumentService customerInvoiceDocumentService;
    protected DateTimeService dateTimeService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingOpenInvoicesReportServiceImpl.class);
    protected KualiModuleService kualiModuleService;

    /**
     * Gets the contractsGrantsAgingReportService attribute.
     *
     * @return Returns the contractsGrantsAgingReportService.
     */
    public ContractsGrantsAgingReportService getContractsGrantsAgingReportService() {
        return contractsGrantsAgingReportService;
    }

    /**
     * Sets the contractsGrantsAgingReportService attribute value.
     *
     * @param contractsGrantsAgingReportService The contractsGrantsAgingReportService to set.
     */
    public void setContractsGrantsAgingReportService(ContractsGrantsAgingReportService contractsGrantsAgingReportService) {
        this.contractsGrantsAgingReportService = contractsGrantsAgingReportService;
    }

    /**
     * Gets the customerInvoiceDocumentService attribute.
     *
     * @return Returns the customerInvoiceDocumentService.
     */
    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    /**
     * Sets the customerInvoiceDocumentService attribute value.
     *
     * @param customerInvoiceDocumentService The customerInvoiceDocumentService to set.
     */
    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }

    /**
     * This method populates ContractsGrantsAgingOpenInvoicesReportDetails (Contracts & Grants Open Invoices Report)
     *
     * @param urlParameters
     */
    @Override
    public List getPopulatedReportDetails(Map urlParameters) {
        List results = new ArrayList();
        String customerNumber = ((String[]) urlParameters.get(KFSPropertyConstants.CUSTOMER_NUMBER))[0];
        String customerName = ((String[]) urlParameters.get(KFSPropertyConstants.CUSTOMER_NAME))[0];

        String orgCode = ObjectUtils.isNotNull(urlParameters.get(KFSPropertyConstants.ORGANIZATION_CODE)) ? ((String[]) urlParameters.get(KFSPropertyConstants.ORGANIZATION_CODE))[0] : null;
        String chartCode = ObjectUtils.isNotNull(urlParameters.get(ArPropertyConstants.BILLING_CHART_CODE)) ? ((String[]) urlParameters.get(ArPropertyConstants.BILLING_CHART_CODE))[0] : null;
        String strBeginDate = ObjectUtils.isNotNull(urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_BEGIN_DATE)) ? ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_BEGIN_DATE))[0] : null;
        String strEndDate = ObjectUtils.isNotNull(urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_END_DATE)) ? ((String[]) urlParameters.get(KFSConstants.CustomerOpenItemReport.REPORT_END_DATE))[0] : null;
        java.sql.Date startDate = null;
        java.sql.Date endDate = null;
        List<ContractsGrantsInvoiceDocument> selectedInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        try {

            if (ObjectUtils.isNotNull(strBeginDate) && StringUtils.isNotEmpty(strBeginDate)) {
                startDate = getDateTimeService().convertToSqlDate(strBeginDate);
            }

            if (ObjectUtils.isNotNull(strEndDate) && StringUtils.isNotEmpty(strEndDate)) {
                endDate = getDateTimeService().convertToSqlDate(strEndDate);
            }

            Map<String, String> fieldValueMap = new HashMap<String, String>();
            for (Object key : urlParameters.keySet()) {
                String val = ((String[]) urlParameters.get(key))[0];
                fieldValueMap.put(key.toString(), val);
            }

            Map<String, List<ContractsGrantsInvoiceDocument>> map = contractsGrantsAgingReportService.filterContractsGrantsAgingReport(fieldValueMap, startDate, endDate);
            if (ObjectUtils.isNotNull(map) && !map.isEmpty()) {
                selectedInvoices = map.get(customerNumber + "-" + customerName);
            }

            if (selectedInvoices.size() == 0) {
                return results;
            }

        }
        catch (ParseException ex) {
            LOG.error("problem during ContractsGrantsAgingOpenInvoicesReportServiceImpl.getPopulatedReportDetails",ex);
            throw new RuntimeException("Couldn't parse a date", ex);
        }

        populateReportDetails(selectedInvoices, results);
        return results;
    }

    /**
     * This method prepare the report model object to display on jsp page.
     *
     * @param invoices
     * @param results
     */
    protected void populateReportDetails(List<ContractsGrantsInvoiceDocument> invoices, List results) {
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            ContractsGrantsAgingOpenInvoicesReport detail = new ContractsGrantsAgingOpenInvoicesReport();
            // Document Type
            detail.setDocumentType(invoice.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            // Document Number
            detail.setDocumentNumber(invoice.getDocumentNumber());
            // Document Description
            String documentDescription = invoice.getDocumentHeader().getDocumentDescription();
            if (ObjectUtils.isNotNull(documentDescription)) {
                detail.setDocumentDescription(documentDescription);
            }
            else {
                detail.setDocumentDescription("");
            }
            // Billing Date
            detail.setBillingDate(invoice.getBillingDate());
            // Due Date
            detail.setDueApprovedDate(invoice.getInvoiceDueDate());
            // Document Payment Amount
            detail.setDocumentPaymentAmount(invoice.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount());
            // Unpaid/Unapplied Amount
            detail.setUnpaidUnappliedAmount(customerInvoiceDocumentService.getOpenAmountForCustomerInvoiceDocument(invoice));
            detail.setFinalInvoice(!ObjectUtils.isNull(invoice.getInvoiceGeneralDetail()) && invoice.getInvoiceGeneralDetail().isFinalBillIndicator() ? KFSConstants.ParameterValues.STRING_YES : KFSConstants.ParameterValues.STRING_NO);
            // set agency number, proposal number, account number
            if (!ObjectUtils.isNull(invoice.getInvoiceGeneralDetail()) && !ObjectUtils.isNull(invoice.getInvoiceGeneralDetail().getProposalNumber())) {
                detail.setProposalNumber(invoice.getInvoiceGeneralDetail().getProposalNumber().toString());
            }

            // Set Agency Number
            ContractsAndGrantsBillingAgency cgAgency = this.getAgencyByCustomer(invoice.getAccountsReceivableDocumentHeader().getCustomerNumber());
            if (ObjectUtils.isNotNull(cgAgency)) {
                detail.setAgencyNumber(cgAgency.getAgencyNumber());
            }

            // Set Account number
            List<CustomerInvoiceDetail> details = invoice.getSourceAccountingLines();
            String accountNum = (CollectionUtils.isNotEmpty(details) && ObjectUtils.isNotNull(details.get(0))) ? details.get(0).getAccountNumber() : "";
            detail.setAccountNumber(accountNum);
            results.add(detail);

        }
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * This method retrives the agecy for particular customer
     *
     * @param customerNumber
     * @return Returns the agency for the customer
     */
    protected ContractsAndGrantsBillingAgency getAgencyByCustomer(String customerNumber) {
        Map args = new HashMap();
        args.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
        return kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAgency.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAgency.class, args);
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
