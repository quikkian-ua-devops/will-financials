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
package org.kuali.kfs.module.ar.report.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AppliedPayment;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerBillingStatement;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.businessobject.defaultvalue.InstitutionNameValueFinder;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.report.service.CustomerAgingReportService;
import org.kuali.kfs.module.ar.report.service.CustomerCreditMemoReportService;
import org.kuali.kfs.module.ar.report.service.CustomerInvoiceReportService;
import org.kuali.kfs.module.ar.report.service.CustomerStatementReportService;
import org.kuali.kfs.module.ar.report.service.OCRLineService;
import org.kuali.kfs.module.ar.report.util.CustomerAgingReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoDetailReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerInvoiceReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerStatementDetailReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerStatementReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerStatementResultHolder;
import org.kuali.kfs.module.ar.report.util.SortTransaction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.PhoneNumberFormatter;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Transactional
public class AccountsReceivableReportServiceImpl implements AccountsReceivableReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableReportServiceImpl.class);

    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected ParameterService parameterService;
    protected CustomerAddressService customerAddressService;
    protected PhoneNumberFormatter phoneNumberFormatter = new PhoneNumberFormatter();
    protected BusinessObjectService businessObjectService;
    protected CurrencyFormatter currencyFormatter = new CurrencyFormatter();
    protected UniversityDateService universityDateService;
    protected CustomerService customerService;
    protected CustomerInvoiceDetailService customerInvoiceDetailService;
    protected CustomerInvoiceDocumentService customerInvoiceDocumentService;
    protected OrganizationService orgService;
    protected InvoicePaidAppliedService<AppliedPayment> invoicePaidAppliedService;
    protected CustomerInvoiceWriteoffDocumentService invoiceWriteoffDocumentService;
    protected CountryService countryService;
    protected CustomerCreditMemoReportService customerCreditMemoReportService;
    protected OCRLineService ocrLineService;
    protected CustomerInvoiceReportService customerInvoiceReportService;
    protected CustomerStatementReportService customerStatementReportService;
    protected CustomerCreditMemoDocumentService customerCreditMemoDocumentService;
    protected CustomerAgingReportService customerAgingReportService;

    /**
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateCreditMemo(org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument)
     */
    @Override
    public File generateCreditMemo(CustomerCreditMemoDocument creditMemo) throws WorkflowException {
        CustomerCreditMemoReportDataHolder reportDataHolder = new CustomerCreditMemoReportDataHolder();
        String invoiceNumber = creditMemo.getFinancialDocumentReferenceInvoiceNumber();
        CustomerInvoiceDocument invoice = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(invoiceNumber);
        String custID = invoice.getAccountsReceivableDocumentHeader().getCustomerNumber();

        Map<String, String> creditMemoMap = new HashMap<String, String>();
        creditMemoMap.put("docNumber", creditMemo.getDocumentNumber());
        creditMemoMap.put("refDocNumber", invoice.getDocumentNumber());
        Date date = creditMemo.getFinancialSystemDocumentHeader().getDocumentFinalDate();
        if (ObjectUtils.isNotNull(date)) {
            creditMemoMap.put("createDate", dateTimeService.toDateString(date));
        }
        reportDataHolder.setCreditmemo(creditMemoMap);
        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", custID);
        customerMap.put("billToName", invoice.getBillingAddressName());
        customerMap.put("billToStreetAddressLine1", invoice.getBillingLine1StreetAddress());
        customerMap.put("billToStreetAddressLine2", invoice.getBillingLine2StreetAddress());
        String billCityStateZip = "";

        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(invoice.getBillingCountryCode())) {
            billCityStateZip = generateCityStateZipLine(invoice.getBillingCityName(), invoice.getBillingStateCode(), invoice.getBillingZipCode());
        } else {
            billCityStateZip = generateCityStateZipLine(invoice.getBillingCityName(), invoice.getBillingAddressInternationalProvinceName(), invoice.getBillingInternationalMailCode());
            if (StringUtils.isNotBlank(invoice.getBillingCountryCode())) {
                Country country = countryService.getCountry(invoice.getBillingCountryCode());
                if (ObjectUtils.isNotNull(country)) {
                    customerMap.put("billToCountry", country.getName());
                }
            }
        }
        customerMap.put("billToCityStateZip", billCityStateZip);

        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        if (ObjectUtils.isNotNull(invoice.getCustomerPurchaseOrderNumber())) {
            invoiceMap.put("poNumber", invoice.getCustomerPurchaseOrderNumber());
        }
        if (invoice.getCustomerPurchaseOrderDate() != null) {
            invoiceMap.put("poDate", dateTimeService.toDateString(invoice.getCustomerPurchaseOrderDate()));
        }

        String initiatorID = invoice.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        Entity user = KimApiServiceLocator.getIdentityService().getEntityByPrincipalId(initiatorID);

        if (user == null) {
            throw new RuntimeException("User '" + initiatorID + "' could not be retrieved.");
        }

        invoiceMap.put("invoicePreparer", user.getDefaultName().getFirstName() + " " + user.getDefaultName().getLastName());
        invoiceMap.put("headerField", (ObjectUtils.isNull(invoice.getInvoiceHeaderText()) ? "" : invoice.getInvoiceHeaderText()));
        invoiceMap.put("billingOrgName", invoice.getBilledByOrganization().getOrganizationName());
        invoiceMap.put("pretaxAmount", invoice.getInvoiceItemPreTaxAmountTotal().toString());
        boolean salesTaxInd = parameterService.getParameterValueAsBoolean("KFS-AR", "Document", ArConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            invoiceMap.put("taxAmount", invoice.getInvoiceItemTaxAmountTotal().toString());
            // KualiDecimal taxPercentage = invoice.getStateTaxPercent().add(invoice.getLocalTaxPercent());
            KualiDecimal taxPercentage = new KualiDecimal(6.85);
            invoiceMap.put("taxPercentage", "*** " + taxPercentage.toString() + "%");
        }
        invoiceMap.put("invoiceAmountDue", invoice.getSourceTotal().toString());
        invoiceMap.put("ocrLine", "");
        reportDataHolder.setInvoice(invoiceMap);

        Map<String, String> sysinfoMap = new HashMap<String, String>();
        InstitutionNameValueFinder finder = new InstitutionNameValueFinder();

        Organization billingOrg = invoice.getBilledByOrganization();
        String chart = billingOrg.getChartOfAccountsCode();
        String org = billingOrg.getOrganizationCode();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chart);
        criteria.put("organizationCode", org);
        OrganizationOptions orgOptions = businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

        String fiscalYear = universityDateService.getCurrentFiscalYear().toString();
        criteria = new HashMap<String, String>();

        Organization processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();

        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        sysinfoMap.put("univName", StringUtils.upperCase(finder.getValue()));
        String univAddr = processingOrg.getOrganizationCityName() + ", " + processingOrg.getOrganizationStateCode() + " " + processingOrg.getOrganizationZipCode();
        sysinfoMap.put("univAddr", univAddr);
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID #" + sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }

        reportDataHolder.setSysinfo(sysinfoMap);

        invoiceMap.put("billingOrgFax", (String) phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationFaxNumber())));
        invoiceMap.put("billingOrgPhone", (String) phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationPhoneNumber())));

        creditMemo.populateCustomerCreditMemoDetailsAfterLoad();
        List<CustomerCreditMemoDetail> detailsList = creditMemo.getCreditMemoDetails();
        List<CustomerCreditMemoDetailReportDataHolder> details = new ArrayList<CustomerCreditMemoDetailReportDataHolder>();
        CustomerCreditMemoDetailReportDataHolder detailDataHolder;
        for (CustomerCreditMemoDetail detail : detailsList) {
            if (detail.getCreditMemoLineTotalAmount().isGreaterThan(KualiDecimal.ZERO)) {
                detailDataHolder = new CustomerCreditMemoDetailReportDataHolder(detail, detail.getCustomerInvoiceDetail());
                details.add(detailDataHolder);
            }
        }

        reportDataHolder.setDetails(details);

        Date runDate = dateTimeService.getCurrentSqlDate();
        File report = customerCreditMemoReportService.generateReport(reportDataHolder, runDate);

        return report;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
    public File generateInvoice(CustomerInvoiceDocument invoice) {

        CustomerInvoiceReportDataHolder reportDataHolder = new CustomerInvoiceReportDataHolder();
        String custID = invoice.getAccountsReceivableDocumentHeader().getCustomerNumber();
        Customer cust = customerService.getByPrimaryKey(custID);
        Integer customerBillToAddressIdentifier = invoice.getCustomerBillToAddressIdentifier();
        Integer customerShipToAddressIdentifier = invoice.getCustomerShipToAddressIdentifier();
        CustomerAddress billToAddr = customerAddressService.getByPrimaryKey(custID, customerBillToAddressIdentifier);
        CustomerAddress shipToAddr = customerAddressService.getByPrimaryKey(custID, customerShipToAddressIdentifier);

        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", custID);
        if (billToAddr != null) {
            customerMap.put("billToName", billToAddr.getCustomerAddressName());
            customerMap.put("billToStreetAddressLine1", billToAddr.getCustomerLine1StreetAddress());
            customerMap.put("billToStreetAddressLine2", billToAddr.getCustomerLine2StreetAddress());
            String billCityStateZip = "";
            if (billToAddr.getCustomerCountryCode().equals("US")) {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerStateCode(), billToAddr.getCustomerZipCode());
            } else {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerAddressInternationalProvinceName(), billToAddr.getCustomerInternationalMailCode());
                customerMap.put("billToCountry", billToAddr.getCustomerCountry().getName());
            }
            customerMap.put("billToCityStateZip", billCityStateZip);
        }
        if (shipToAddr != null) {
            customerMap.put("shipToName", shipToAddr.getCustomerAddressName());
            customerMap.put("shipToStreetAddressLine1", shipToAddr.getCustomerLine1StreetAddress());
            customerMap.put("shipToStreetAddressLine2", shipToAddr.getCustomerLine2StreetAddress());
            String shipCityStateZip = "";
            if (shipToAddr.getCustomerCountryCode().equals("US")) {
                shipCityStateZip = generateCityStateZipLine(shipToAddr.getCustomerCityName(), shipToAddr.getCustomerStateCode(), shipToAddr.getCustomerZipCode());
            } else {
                shipCityStateZip = generateCityStateZipLine(shipToAddr.getCustomerCityName(), shipToAddr.getCustomerAddressInternationalProvinceName(), shipToAddr.getCustomerInternationalMailCode());
                customerMap.put("shipToCountry", shipToAddr.getCustomerCountry().getName());
            }
            customerMap.put("shipToCityStateZip", shipCityStateZip);
        }
        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        invoiceMap.put("poNumber", invoice.getCustomerPurchaseOrderNumber());
        if (invoice.getCustomerPurchaseOrderDate() != null) {
            invoiceMap.put("poDate", dateTimeService.toDateString(invoice.getCustomerPurchaseOrderDate()));
        }

        String initiatorID = invoice.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        Entity user = KimApiServiceLocator.getIdentityService().getEntityByPrincipalId(initiatorID);


        if (user == null) {
            throw new RuntimeException("User '" + initiatorID + "' could not be retrieved.");
        }

        invoiceMap.put("invoicePreparer", user.getDefaultName().getFirstName() + " " + user.getDefaultName().getLastName());
        invoiceMap.put("headerField", invoice.getInvoiceHeaderText());
        invoiceMap.put("customerOrg", invoice.getBilledByOrganizationCode());
        invoiceMap.put("docNumber", invoice.getDocumentNumber());
        invoiceMap.put("invoiceDueDate", dateTimeService.toDateString(invoice.getInvoiceDueDate()));
        invoiceMap.put("createDate", dateTimeService.toDateString(invoice.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate()));
        invoiceMap.put("invoiceAttentionLineText", StringUtils.upperCase(invoice.getInvoiceAttentionLineText()));
        invoiceMap.put("billingOrgName", invoice.getBilledByOrganization().getOrganizationName());
        invoiceMap.put("pretaxAmount", currencyFormatter.format(invoice.getInvoiceItemPreTaxAmountTotal()).toString());
        boolean salesTaxInd = parameterService.getParameterValueAsBoolean("KFS-AR", "Document", ArConstants.ENABLE_SALES_TAX_IND);
        if (salesTaxInd) {
            invoiceMap.put("taxAmount", currencyFormatter.format(invoice.getInvoiceItemTaxAmountTotal()).toString());
            invoiceMap.put("taxPercentage", ""); // suppressing this as its useless ... see KULAR-415
        }
        invoiceMap.put("invoiceAmountDue", currencyFormatter.format(invoice.getSourceTotal()).toString());
        invoiceMap.put("invoiceTermsText", invoice.getInvoiceTermsText());

        String ocrLine = ocrLineService.generateOCRLine(invoice.getSourceTotal(), custID, invoice.getDocumentNumber());
        invoiceMap.put("ocrLine", ocrLine);
        List<CustomerInvoiceDetail> detailsList = (List<CustomerInvoiceDetail>) customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoice(invoice);
        CustomerInvoiceDetail firstDetail = detailsList.get(0);
        String firstChartCode = firstDetail.getChartOfAccountsCode();
        String firstAccount = firstDetail.getAccountNumber();
        invoiceMap.put("chartAndAccountOfFirstItem", firstChartCode + firstAccount);

        Map<String, String> sysinfoMap = new HashMap<String, String>();

        Organization billingOrg = invoice.getBilledByOrganization();
        String chart = billingOrg.getChartOfAccountsCode();
        String org = billingOrg.getOrganizationCode();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chart);
        criteria.put("organizationCode", org);
        OrganizationOptions orgOptions = businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

        String fiscalYear = universityDateService.getCurrentFiscalYear().toString();
        criteria = new HashMap<String, String>();

        Organization processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();

        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        sysinfoMap.put("univName", StringUtils.upperCase(sysinfo.getOrganizationCheckPayableToName()));
        sysinfoMap.put("univAddr", generateCityStateZipLine(processingOrg.getOrganizationCityName(), processingOrg.getOrganizationStateCode(), processingOrg.getOrganizationZipCode()));
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID #" + sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }
        sysinfoMap.put("checkPayableTo", orgOptions.getOrganizationCheckPayableToName());
        sysinfoMap.put("remitToName", orgOptions.getOrganizationRemitToAddressName());
        sysinfoMap.put("remitToAddressLine1", orgOptions.getOrganizationRemitToLine1StreetAddress());
        sysinfoMap.put("remitToAddressLine2", orgOptions.getOrganizationRemitToLine2StreetAddress());

        sysinfoMap.put("remitToCityStateZip", generateCityStateZipLine(orgOptions.getOrganizationRemitToCityName(), orgOptions.getOrganizationRemitToStateCode(), orgOptions.getOrganizationRemitToZipCode()));

        invoiceMap.put("billingOrgFax", (String) phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationFaxNumber())));
        invoiceMap.put("billingOrgPhone", (String) phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationPhoneNumber())));

        invoiceMap.put("orgOptionsMessageText", orgOptions.getOrganizationMessageText());

        reportDataHolder.setSysinfo(sysinfoMap);
        reportDataHolder.setDetails(detailsList);
        reportDataHolder.setInvoice(invoiceMap);

        Date runDate = dateTimeService.getCurrentSqlDate();
        File report = customerInvoiceReportService.generateReport(reportDataHolder, runDate);

        invoice.setPrintDate(runDate);
        documentService.updateDocument(invoice);
        return report;
    }

    /**
     * Constructs the data holder to be used for
     *
     * @param billingChartCode
     * @param billingOrgCode
     * @param customerNumber
     * @param details
     * @return
     */
    public File createStatement(String billingChartCode, String billingOrgCode, String customerNumber, Organization processingOrg, List<CustomerStatementDetailReportDataHolder> details, String statementFormat, String zeroBalance, CustomerStatementResultHolder customerStatementResultHolder) {

        CustomerStatementReportDataHolder reportDataHolder = new CustomerStatementReportDataHolder();
        CustomerAddress billToAddr = customerAddressService.getPrimaryAddress(customerNumber);

        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", customerNumber);
        if (billToAddr != null) {
            customerMap.put("billToName", billToAddr.getCustomerAddressName());
            customerMap.put("billToStreetAddressLine1", billToAddr.getCustomerLine1StreetAddress());
            customerMap.put("billToStreetAddressLine2", billToAddr.getCustomerLine2StreetAddress());
            String billCityStateZip = "";
            if (billToAddr.getCustomerCountryCode().equals("US")) {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerStateCode(), billToAddr.getCustomerZipCode());
            } else {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerAddressInternationalProvinceName(), billToAddr.getCustomerInternationalMailCode());
                customerMap.put("billToCountry", billToAddr.getCustomerCountry().getName());
            }
            customerMap.put("billToCityStateZip", billCityStateZip);
        }

        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        invoiceMap.clear();
        invoiceMap.put("createDate", dateTimeService.toDateString(dateTimeService.getCurrentDate()));
        invoiceMap.put("customerOrg", billingOrgCode);
        Organization billingOrg = orgService.getByPrimaryId(billingChartCode, billingOrgCode);
        invoiceMap.put("billingOrgName", billingOrg.getOrganizationName());

        KualiDecimal amountDue = KualiDecimal.ZERO;
        KualiDecimal previousBalance = KualiDecimal.ZERO;
        String lastReportedDate = "";
        CustomerBillingStatement customerBillingStatement = getCustomerBillingStatement(customerNumber);
        if (ObjectUtils.isNotNull(customerBillingStatement)) {
            previousBalance = customerBillingStatement.getPreviouslyBilledAmount();
            if (statementFormat.equals(ArConstants.STATEMENT_FORMAT_DETAIL)) {
                amountDue = previousBalance;
                lastReportedDate = dateTimeService.toDateString(customerBillingStatement.getReportedDate());
            }
        }

        invoiceMap.put("previousBalance", currencyFormatter.format(previousBalance).toString());
        invoiceMap.put("lastReportedDate", lastReportedDate);


        for (CustomerStatementDetailReportDataHolder data : details) {
            if (data.getFinancialDocumentTotalAmountCharge() != null) {
                amountDue = amountDue.add(data.getFinancialDocumentTotalAmountCharge());
            }
            if (data.getFinancialDocumentTotalAmountCredit() != null) {
                amountDue = amountDue.subtract(data.getFinancialDocumentTotalAmountCredit());
            }
        }

        // This customer has a zero balance and so we do not need to generate the report if the include zero balance is "No."
        if (amountDue.equals(KualiDecimal.ZERO) && zeroBalance.equals(ArConstants.INCLUDE_ZERO_BALANCE_NO)) {
            return null;
        }

        customerStatementResultHolder.setCurrentBilledAmount(amountDue);

        invoiceMap.put("amountDue", currencyFormatter.format(amountDue).toString());
        invoiceMap.put("dueDate", calculateDueDate());

        String ocrLine = ocrLineService.generateOCRLine(amountDue, customerNumber, null);
        invoiceMap.put("ocrLine", ocrLine);

        Map<String, String> sysinfoMap = new HashMap<String, String>();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", billingChartCode);
        criteria.put("organizationCode", billingOrgCode);
        OrganizationOptions orgOptions = businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

        sysinfoMap.put("checkPayableTo", orgOptions.getOrganizationCheckPayableToName());
        sysinfoMap.put("remitToName", orgOptions.getOrganizationRemitToAddressName());
        sysinfoMap.put("remitToAddressLine1", orgOptions.getOrganizationRemitToLine1StreetAddress());
        sysinfoMap.put("remitToAddressLine2", orgOptions.getOrganizationRemitToLine2StreetAddress());
        sysinfoMap.put("remitToCityStateZip", generateCityStateZipLine(orgOptions.getOrganizationRemitToCityName(), orgOptions.getOrganizationRemitToStateCode(), orgOptions.getOrganizationRemitToZipCode()));

        invoiceMap.put("billingOrgFax", (String) phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationFaxNumber())));
        invoiceMap.put("billingOrgPhone", (String) phoneNumberFormatter.format(phoneNumberFormatter.convertFromPresentationFormat(orgOptions.getOrganizationPhoneNumber())));

        String fiscalYear = universityDateService.getCurrentFiscalYear().toString();

        criteria.clear();
        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        sysinfoMap.put("univName", StringUtils.upperCase(sysinfo.getOrganizationCheckPayableToName()));
        sysinfoMap.put("univAddr", generateCityStateZipLine(processingOrg.getOrganizationCityName(), processingOrg.getOrganizationStateCode(), processingOrg.getOrganizationZipCode()));
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID #" + sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }

        KualiDecimal bal = (statementFormat.equals(ArConstants.STATEMENT_FORMAT_DETAIL)) ? previousBalance : KualiDecimal.ZERO;
        calculateAgingAmounts(details, invoiceMap, bal);

        reportDataHolder.setSysinfo(sysinfoMap);
        reportDataHolder.setDetails(details);
        reportDataHolder.setInvoice(invoiceMap);

        Date runDate = dateTimeService.getCurrentSqlDate();
        File f = customerStatementReportService.generateReport(reportDataHolder, runDate, statementFormat);
        return f;
    }


    /**
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByBillingOrg(java.lang.String,
     * java.lang.String, java.sql.Date)
     */
    @Override
    public List<File> generateInvoicesByBillingOrg(String chartCode, String orgCode, Date date) {
        CustomerInvoiceDocumentService invoiceDocService = customerInvoiceDocumentService;
        List<CustomerInvoiceDocument> invoices = invoiceDocService.getPrintableCustomerInvoiceDocumentsByBillingChartAndOrg(chartCode, orgCode);
        List<File> reports = new ArrayList<File>();

        for (CustomerInvoiceDocument doc : invoices) {
            if (date == null) {
                reports.add(generateInvoice(doc));
            } else if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate()) != null) {
                if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate()).equals(dateTimeService.toDateString(date))) {
                    reports.add(generateInvoice(doc));
                }
            }
        }
        return reports;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByProcessingOrg(java.lang.String,
     * java.lang.String, java.sql.Date)
     */
    @Override
    public List<File> generateInvoicesByProcessingOrg(String chartCode, String orgCode, Date date) {
        List<CustomerInvoiceDocument> invoices = customerInvoiceDocumentService.getPrintableCustomerInvoiceDocumentsByProcessingChartAndOrg(chartCode, orgCode);

        List<File> reports = new ArrayList<File>();
        for (CustomerInvoiceDocument doc : invoices) {
            if (date == null) {
                reports.add(generateInvoice(doc));
            } else if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate()) != null) {
                if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate()).equals(dateTimeService.toDateString(date))) {
                    reports.add(generateInvoice(doc));
                }
            }
        }
        return reports;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByInitiator(java.lang.String)
     */
    @Override
    public List<File> generateInvoicesByInitiator(String initiator, java.sql.Date date) {
        List<File> reports = new ArrayList<File>();
        Collection<CustomerInvoiceDocument> invoices = customerInvoiceDocumentService.getPrintableCustomerInvoiceDocumentsByInitiatorPrincipalName(initiator);
        for (CustomerInvoiceDocument invoice : invoices) {
            if (date == null) {
                reports.add(generateInvoice(invoice));
            } else if (dateTimeService.toDateString(invoice.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate()) != null) {
                if (dateTimeService.toDateString(invoice.getDocumentHeader().getWorkflowDocument().getDateCreated().toDate()).equals(dateTimeService.toDateString(date))) {
                    reports.add(generateInvoice(invoice));
                }
            }
        }
        return reports;
    }

    /**
     * Generates detail statement reports
     *
     * @param invoiceList
     * @param creditMemoList
     * @param paymentList
     * @param writeoffList
     * @return CustomerStatementResultHolder
     */
    protected List<CustomerStatementResultHolder> generateStatementReports(Collection<CustomerInvoiceDocument> invoices, String statementFormat, String incldueZeroBalanceCustomers) {

        List<CustomerInvoiceDocument> invoiceList = new ArrayList<CustomerInvoiceDocument>(invoices);
        Collections.sort(invoiceList);
        Map<String, Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>> statementDetailsSorted = sortCustomerStatementData(invoiceList, statementFormat, incldueZeroBalanceCustomers);
        Set<String> processingOrgKeys = statementDetailsSorted.keySet();

        //List<File> reports = new ArrayList<File>();
        List<CustomerStatementResultHolder> result = new ArrayList<CustomerStatementResultHolder>();

        for (String processingChartAndOrg : processingOrgKeys) {
            String processingChartCode = processingChartAndOrg.substring(0, 2);
            String processingOrgCode = processingChartAndOrg.substring(2);
            Organization processingOrg = orgService.getByPrimaryId(processingChartCode, processingOrgCode);

            // sort by organization
            Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>> statementDetailsByProcessingOrg = statementDetailsSorted.get(processingChartAndOrg);

            Set<String> billingOrgKeys = statementDetailsByProcessingOrg.keySet();

            for (String billingChartAndOrg : billingOrgKeys) {
                String billingChartCode = billingChartAndOrg.substring(0, 2);
                String billingOrgCode = billingChartAndOrg.substring(2);

                Map<String, List<CustomerStatementDetailReportDataHolder>> statementDetailsByBillingOrg = statementDetailsByProcessingOrg.get(billingChartAndOrg);

                Set<String> customerKeys = statementDetailsByBillingOrg.keySet();

                for (String customerId : customerKeys) {
                    List<CustomerStatementDetailReportDataHolder> statementDetailsByCustomer = statementDetailsByBillingOrg.get(customerId);

                    CustomerStatementResultHolder customerStatementResultHolder = new CustomerStatementResultHolder();
                    customerStatementResultHolder.setCustomerNumber(customerId);
                    File file = createStatement(billingChartCode, billingOrgCode, customerId, processingOrg, statementDetailsByCustomer, statementFormat, incldueZeroBalanceCustomers, customerStatementResultHolder);
                    if (file != null) {
                        customerStatementResultHolder.setFile(file);
                        if (statementFormat.equalsIgnoreCase(ArConstants.STATEMENT_FORMAT_DETAIL)) {
                            List<String> invoiceNumbers = new ArrayList<String>();
                            for (CustomerStatementDetailReportDataHolder c : statementDetailsByCustomer) {
                                if (c.getDocType().equalsIgnoreCase(ArConstants.INVOICE_DOC_TYPE)) {
                                    invoiceNumbers.add(c.getDocumentNumber());
                                }
                            }
                            customerStatementResultHolder.setInvoiceNumbers(invoiceNumbers);
                        }
                        result.add(customerStatementResultHolder);
                    }
                }
            }
        }

        return result;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByBillingOrg(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<CustomerStatementResultHolder> generateStatementByBillingOrg(String chartCode, String orgCode, String statementFormat, String incldueZeroBalanceCustomers) {
        return generateStatementReports(customerInvoiceDocumentService.getPrintableCustomerInvoiceDocumentsForBillingStatementByBillingChartAndOrg(chartCode, orgCode), statementFormat, incldueZeroBalanceCustomers);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByAccount(java.lang.String)
     */
    @Override
    public List<CustomerStatementResultHolder> generateStatementByAccount(String accountNumber, String statementFormat, String incldueZeroBalanceCustomers) {
        return generateStatementReports(customerInvoiceDocumentService.getCustomerInvoiceDocumentsByAccountNumber(accountNumber), statementFormat, incldueZeroBalanceCustomers);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByCustomer(java.lang.String)
     */
    @Override
    public List<CustomerStatementResultHolder> generateStatementByCustomer(String customerNumber, String statementFormat, String incldueZeroBalanceCustomers) {
        if (StringUtils.equals(statementFormat, ArConstants.STATEMENT_FORMAT_SUMMARY)) {
            return generateStatementReports(customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerNumber(customerNumber), statementFormat, incldueZeroBalanceCustomers);
        } else {
            return generateStatementReports(customerInvoiceDocumentService.getCustomerInvoiceDocumentsByCustomerNumber(customerNumber), statementFormat, incldueZeroBalanceCustomers);
        }
    }


    protected KualiDecimal creditTotalInList(Collection<CustomerStatementDetailReportDataHolder> holderList) {
        KualiDecimal totalCredit = KualiDecimal.ZERO;
        for (CustomerStatementDetailReportDataHolder holder : holderList) {
            totalCredit = totalCredit.add(holder.getFinancialDocumentTotalAmountCredit());
        }
        return totalCredit;
    }

    protected Collection<CustomerStatementDetailReportDataHolder> creditMemosForInvoice(CustomerInvoiceDocument invoice) {
        // credit memo
        List<CustomerStatementDetailReportDataHolder> returnList = new ArrayList<CustomerStatementDetailReportDataHolder>();
        Collection<CustomerCreditMemoDocument> creditMemos = customerCreditMemoDocumentService.getCustomerCreditMemoDocumentByInvoiceDocument(invoice.getDocumentNumber());
        for (CustomerCreditMemoDocument doc : creditMemos) {
            try {
                doc.populateCustomerCreditMemoDetailsAfterLoad();
                WorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
                if (workflowDoc.isFinal() || workflowDoc.isProcessed()) {
                    CustomerCreditMemoDocument creditMemoDoc = (CustomerCreditMemoDocument) documentService.getByDocumentHeaderId(doc.getDocumentNumber());
                    CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(creditMemoDoc.getFinancialSystemDocumentHeader(),
                        invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(),
                        ArConstants.CREDIT_MEMO_DOC_TYPE, doc.getTotalDollarAmount());
                    returnList.add(detail);
                }
            } catch (WorkflowException we) {
                LOG.error(we.getMessage());
                throw new RuntimeException("Problem retrieving full credit memo document for document #" + doc.getDocumentNumber(), we);
            }
        }
        return returnList;
    }


    protected Collection<CustomerStatementDetailReportDataHolder> paymentsForInvoice(CustomerInvoiceDocument invoice) {

        List<CustomerStatementDetailReportDataHolder> returnList = new ArrayList<CustomerStatementDetailReportDataHolder>();
        //  payment
        Collection<InvoicePaidApplied> payments = invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(invoice);
        for (InvoicePaidApplied doc : payments) {
            try {
                Document payAppDoc = documentService.getByDocumentHeaderId(doc.getDocumentNumber());
                if (payAppDoc instanceof PaymentApplicationDocument) {
                    WorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
                    if (workflowDoc.isFinal() || workflowDoc.isProcessed()) {
                        PaymentApplicationDocument paymentApplicationDoc = (PaymentApplicationDocument) payAppDoc;
                        CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(paymentApplicationDoc.getFinancialSystemDocumentHeader(),
                            invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(),
                            ArConstants.PAYMENT_DOC_TYPE, doc.getInvoiceItemAppliedAmount());
                        returnList.add(detail);
                    }
                }
            } catch (WorkflowException we) {
                LOG.error(we.getMessage());
                throw new RuntimeException("Could not load full payment application document #" + doc.getDocumentNumber(), we);
            }
        }
        return returnList;
    }

    /**
     * This method groups invoices according to processingOrg, billedByOrg, customerNumber
     *
     * @param invoiceList
     * @return
     */
    protected Map<String, Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>> sortCustomerStatementData(List<CustomerInvoiceDocument> invoiceList, String statementFormat, String incldueZeroBalanceCustomers) {
        // To group - Map<processingOrg, map<billedByOrg, map<customerNumber, List<CustomerStatementDetailReportDataHolder>
        Map<String, Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>> customerStatementDetailsSorted = new HashMap<String, Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>>();

        // This is where all the magic will begin
        for (CustomerInvoiceDocument invoice : invoiceList) {

            if (invoice.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.APPROVED)) {
                if (invoice.isOpenInvoiceIndicator()) {

                    // if it is detailed report, take only invoices that have not reported yet
                    if (statementFormat.equalsIgnoreCase(ArConstants.STATEMENT_FORMAT_DETAIL) && ObjectUtils.isNotNull(invoice.getReportedDate())) {
                        continue;
                    }

                    // Break down list into a map based on processing org
                    Organization processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();

                    // Retrieve the collection of invoices that already exist for the processing org provided
                    Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>> statementDetailsForGivenProcessingOrg = customerStatementDetailsSorted.get(getChartAndOrgCodesCombined(processingOrg));
                    if (statementDetailsForGivenProcessingOrg == null) {
                        statementDetailsForGivenProcessingOrg = new HashMap<String, Map<String, List<CustomerStatementDetailReportDataHolder>>>();
                    }

                    // Then break down that list for billing chart and org codes
                    Organization billedByOrg = invoice.getBilledByOrganization();
                    Map<String, List<CustomerStatementDetailReportDataHolder>> statementDetailsForGivenBillingOrg = statementDetailsForGivenProcessingOrg.get(getChartAndOrgCodesCombined(billedByOrg));
                    if (statementDetailsForGivenBillingOrg == null) {
                        statementDetailsForGivenBillingOrg = new HashMap<String, List<CustomerStatementDetailReportDataHolder>>();
                    }

                    // Then break down that list by customer
                    String customerNumber = invoice.getCustomer().getCustomerNumber();
                    List<CustomerStatementDetailReportDataHolder> statementDetailsByCustomer = statementDetailsForGivenBillingOrg.get(customerNumber);
                    if (ObjectUtils.isNull(statementDetailsByCustomer)) {
                        statementDetailsByCustomer = new ArrayList<CustomerStatementDetailReportDataHolder>();
                    }

                    // for detail only
                    if (ArConstants.STATEMENT_FORMAT_DETAIL.equals(statementFormat)) {
                        // add credit memo
                        statementDetailsByCustomer.addAll(creditMemosForInvoice(invoice));

                        // add payment
                        statementDetailsByCustomer.addAll(paymentsForInvoice(invoice));

                        // add writeoff
                        Collection<CustomerInvoiceWriteoffDocument> writeoffs = invoiceWriteoffDocumentService.getCustomerCreditMemoDocumentByInvoiceDocument(invoice.getDocumentNumber());
                        for (CustomerInvoiceWriteoffDocument doc : writeoffs) {
                            WorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
                            if (workflowDoc.isFinal() || workflowDoc.isProcessed()) {
                                CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(doc.getFinancialSystemDocumentHeader(),
                                    invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(),
                                    ArConstants.WRITEOFF_DOC_TYPE, doc.getTotalDollarAmount());
                                statementDetailsByCustomer.add(detail);
                            }
                        }
                    }

                    // add invoice
                    CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(invoice.getFinancialSystemDocumentHeader(), invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(), ArConstants.INVOICE_DOC_TYPE, invoice.getTotalDollarAmount());
                    if (detail != null) {
                        if (!statementFormat.equalsIgnoreCase(ArConstants.STATEMENT_FORMAT_DETAIL)) {
                            KualiDecimal totalInvoiceCredit = KualiDecimal.ZERO;
                            totalInvoiceCredit = totalInvoiceCredit.add(creditTotalInList(creditMemosForInvoice(invoice)));
                            totalInvoiceCredit = totalInvoiceCredit.add(creditTotalInList(paymentsForInvoice(invoice)));
                            detail.setFinancialDocumentTotalAmountCredit(totalInvoiceCredit);
                        }
                        statementDetailsByCustomer.add(detail);
                        statementDetailsForGivenBillingOrg.put(customerNumber, statementDetailsByCustomer);
                        statementDetailsForGivenProcessingOrg.put(getChartAndOrgCodesCombined(billedByOrg), statementDetailsForGivenBillingOrg);
                        customerStatementDetailsSorted.put(getChartAndOrgCodesCombined(processingOrg), statementDetailsForGivenProcessingOrg);
                    }
                }
            }
        }

        // sort
        if (ArConstants.STATEMENT_FORMAT_DETAIL.equals(statementFormat)) {
            // To group - Map<processingOrg, map<billedByOrg, map<customerNumber, List<CustomerStatementDetailReportDataHolder>
            Set<String> ProcessingOrgKeys = customerStatementDetailsSorted.keySet();
            for (String processingOrg : ProcessingOrgKeys) {
                Map<String, Map<String, List<CustomerStatementDetailReportDataHolder>>> billedByOrgs = customerStatementDetailsSorted.get(processingOrg);
                Set<String> billedByOrgKeys = billedByOrgs.keySet();
                for (String billedOrg : billedByOrgKeys) {
                    Map<String, List<CustomerStatementDetailReportDataHolder>> customerNumbers = billedByOrgs.get(billedOrg);
                    Set<String> customerNumberKeys = customerNumbers.keySet();
                    for (String customerNumber : customerNumberKeys) {
                        List<CustomerStatementDetailReportDataHolder> transactions = customerNumbers.get(customerNumber);
                        if (ObjectUtils.isNotNull(transactions)) {
                            Collections.sort(transactions, new SortTransaction());
                        }
                    }
                }
            }
        }

        return customerStatementDetailsSorted;
    }

    /**
     * This method...
     *
     * @param city
     * @param state
     * @param zipCode
     * @return
     */
    protected String generateCityStateZipLine(String city, String state, String zipCode) {
        StringBuffer cityStateZip = new StringBuffer(city);
        cityStateZip.append(", ").append(state);
        cityStateZip.append("  ").append(zipCode);
        return cityStateZip.toString();
    }

    /**
     * This method calculates the total aging amounts for a given statement. This will often be a collection of totals from multiple
     * invoices and/or credit memos.
     *
     * @param details
     * @param invoiceMap
     */
    protected void calculateAgingAmounts(List<CustomerStatementDetailReportDataHolder> details, Map<String, String> invoiceMap, KualiDecimal previousBalance) {
        for (CustomerStatementDetailReportDataHolder csdrdh : details) {
            if (csdrdh.getDocType().equals(ArConstants.INVOICE_DOC_TYPE)) {
                CustomerInvoiceDocument ci = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(csdrdh.getDocumentNumber());

                Collection<CustomerInvoiceDetail> invoiceDetails = customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(ci);
                java.util.Date reportRunDate = dateTimeService.getCurrentDate();
                CustomerAgingReportDataHolder agingData = customerAgingReportService.calculateAgingReportAmounts(invoiceDetails, reportRunDate);
                // add previous balance to 30 days and total amount due

                addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_0_TO_30, agingData.getTotal0to30().add(previousBalance), invoiceMap);
                addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_31_TO_60, agingData.getTotal31to60(), invoiceMap);
                addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_61_TO_90, agingData.getTotal61to90(), invoiceMap);
                addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_91_TO_SYSPR, agingData.getTotal91toSYSPR(), invoiceMap);
                // it's not necessary to display an extra bucket on the statement, so I'm rolling up the amounts over 90 days into a single bucket for display
                addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_91_TO_SYSPR, agingData.getTotalSYSPRplus1orMore(), invoiceMap);
                addAgingAmountToInvoiceMap(ArConstants.CustomerAgingReportFields.TOTAL_AMOUNT_DUE, agingData.getTotalAmountDue().add(previousBalance), invoiceMap);
            }
        }
    }

    /**
     * This method...
     *
     * @param mapKey
     * @param amountToAdd
     * @param invoiceMap
     */
    protected void addAgingAmountToInvoiceMap(String mapKey, KualiDecimal amountToAdd, Map<String, String> invoiceMap) {
        BigDecimal amount = BigDecimal.ZERO;
        String currentAmount = invoiceMap.get(mapKey);
        if (StringUtils.isNotEmpty(currentAmount)) {
            try {
                amount = new BigDecimal(StringUtils.remove(currentAmount, ','));
            } catch (NumberFormatException nfe) {
                LOG.error(currentAmount + " is an invalid amount.", nfe);
            }
        }
        if (ObjectUtils.isNull(amountToAdd)) {
            amountToAdd = KualiDecimal.ZERO;
        }
        KualiDecimal newAmount = new KualiDecimal(amount.add(amountToAdd.bigDecimalValue()));
        invoiceMap.put(mapKey, currencyFormatter.format(newAmount).toString());
    }

    /**
     * Gets a CustomerBillingStatement
     *
     * @param customerNumber
     * @return
     */
    protected CustomerBillingStatement getCustomerBillingStatement(String customerNumber) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("customerNumber", customerNumber);
        return businessObjectService.findByPrimaryKey(CustomerBillingStatement.class, criteria);
    }

    protected String calculateDueDate() {
        String parameterValue = getParameterService().getParameterValueAsString(CustomerBillingStatement.class, ArConstants.DUE_DATE_DAYS);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTimeService.getCurrentDate());
        cal.add(Calendar.DATE, Integer.parseInt(parameterValue));
        Date dueDate = null;
        try {
            dueDate = dateTimeService.convertToSqlDate(new Timestamp(cal.getTime().getTime()));
        } catch (ParseException e) {
            // throw an error here, but don't die
            LOG.error("Could not parse date for due date days", e);
        }
        return dateTimeService.toDateString(dueDate);
    }

    /**
     * This method...
     *
     * @param org
     * @return
     */
    protected String getChartAndOrgCodesCombined(Organization org) {
        if (org == null) {
            return null;
        }
        StringBuffer chartAndOrg = new StringBuffer(6);
        chartAndOrg.append(org.getChartOfAccountsCode()).append(org.getOrganizationCode());
        return chartAndOrg.toString();
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * @return the parameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * @param parameterService the parameterService to set
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public CustomerAddressService getCustomerAddressService() {
        return customerAddressService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    /**
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Gets the customerInvoiceDetailService attribute.
     *
     * @return Returns the customerInvoiceDetailService.
     */
    public CustomerInvoiceDetailService getCustomerInvoiceDetailService() {
        return customerInvoiceDetailService;
    }

    /**
     * Sets the customerInvoiceDetailService attribute value.
     *
     * @param customerInvoiceDetailService The customerInvoiceDetailService to set.
     */
    public void setCustomerInvoiceDetailService(CustomerInvoiceDetailService customerInvoiceDetailService) {
        this.customerInvoiceDetailService = customerInvoiceDetailService;
    }

    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }

    public void setOrgService(OrganizationService orgService) {
        this.orgService = orgService;
    }

    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }

    public CustomerInvoiceWriteoffDocumentService getInvoiceWriteoffDocumentService() {
        return invoiceWriteoffDocumentService;
    }

    public void setInvoiceWriteoffDocumentService(CustomerInvoiceWriteoffDocumentService customerInvoiceWriteoffDocumentService) {
        this.invoiceWriteoffDocumentService = customerInvoiceWriteoffDocumentService;
    }

    public CountryService getCountryService() {
        return countryService;
    }

    public void setCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    public CustomerCreditMemoReportService getCustomerCreditMemoReportService() {
        return customerCreditMemoReportService;
    }

    public void setCustomerCreditMemoReportService(CustomerCreditMemoReportService customerCreditMemoReportService) {
        this.customerCreditMemoReportService = customerCreditMemoReportService;
    }

    public OCRLineService getOcrLineService() {
        return ocrLineService;
    }

    public void setOcrLineService(OCRLineService ocrLineService) {
        this.ocrLineService = ocrLineService;
    }

    public CustomerInvoiceReportService getCustomerInvoiceReportService() {
        return customerInvoiceReportService;
    }

    public void setCustomerInvoiceReportService(CustomerInvoiceReportService customerInvoiceReportService) {
        this.customerInvoiceReportService = customerInvoiceReportService;
    }

    public CustomerStatementReportService getCustomerStatementReportService() {
        return customerStatementReportService;
    }

    public void setCustomerStatementReportService(CustomerStatementReportService customerStatementReportService) {
        this.customerStatementReportService = customerStatementReportService;
    }

    public CustomerCreditMemoDocumentService getCustomerCreditMemoDocumentService() {
        return customerCreditMemoDocumentService;
    }

    public void setCustomerCreditMemoDocumentService(CustomerCreditMemoDocumentService customerCreditMemoDocumentService) {
        this.customerCreditMemoDocumentService = customerCreditMemoDocumentService;
    }

    public CustomerAgingReportService getCustomerAgingReportService() {
        return customerAgingReportService;
    }

    public void setCustomerAgingReportService(CustomerAgingReportService customerAgingReportService) {
        this.customerAgingReportService = customerAgingReportService;
    }
}
