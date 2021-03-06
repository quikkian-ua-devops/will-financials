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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;


public class InvoicePaidApplied extends PersistableBusinessObjectBase {

    private String documentNumber; // document the payment is being applied FROM
    private Integer paidAppliedItemNumber;
    private String financialDocumentReferenceInvoiceNumber; // document the payment is being applied TO
    private Integer invoiceItemNumber;
    private Integer universityFiscalYear;
    private String universityFiscalPeriodCode;
    private KualiDecimal invoiceItemAppliedAmount = KualiDecimal.ZERO;

    private CustomerInvoiceDetail invoiceDetail;
    private AccountingPeriod universityFiscalPeriod;
    private FinancialSystemDocumentHeader documentHeader;
    transient private DocumentService documentService;
    private KualiDecimal paidAppiedDistributionAmount = KualiDecimal.ZERO;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private transient CustomerInvoiceDocument customerInvoiceDocument;

    public InvoicePaidApplied() {
        super();
    }

    public InvoicePaidApplied(String documentNumber, String refInvoiceDocNumber, Integer invoiceSequenceNumber, KualiDecimal appliedAmount, Integer paidAppliedItemNumber, Integer universityFiscalYear, String universityFiscalPeriodCode) {
        this.documentNumber = documentNumber;
        this.financialDocumentReferenceInvoiceNumber = refInvoiceDocNumber;
        this.invoiceItemNumber = invoiceSequenceNumber;
        this.paidAppliedItemNumber = paidAppliedItemNumber;
        this.invoiceItemAppliedAmount = appliedAmount;
        this.universityFiscalYear = universityFiscalYear;
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    /**
     * Constructs a InvoicePaidApplied object, and assumes the current Fiscal Year and FiscalPeriodCode.
     *
     * @param documentNumber
     * @param refInvoiceDocNumber
     * @param invoiceSequenceNumber
     * @param appliedAmount
     * @param paidAppliedItemNumber
     */
    public InvoicePaidApplied(String documentNumber, String refInvoiceDocNumber, Integer invoiceSequenceNumber, KualiDecimal appliedAmount, Integer paidAppliedItemNumber) {
        this.documentNumber = documentNumber;
        this.financialDocumentReferenceInvoiceNumber = refInvoiceDocNumber;
        this.invoiceItemNumber = invoiceSequenceNumber;
        this.paidAppliedItemNumber = paidAppliedItemNumber;
        this.invoiceItemAppliedAmount = appliedAmount;

        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        this.universityFiscalYear = universityDateService.getCurrentFiscalYear();
        this.universityFiscalPeriodCode = universityDateService.getCurrentUniversityDate().getAccountingPeriod().getUniversityFiscalPeriodCode();
    }

    public DocumentService getDocumentService() {
        if (null == documentService) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        CustomerInvoiceDocument customerInvoiceDocument = null;
        try {
            customerInvoiceDocument = (CustomerInvoiceDocument) getDocumentService().getByDocumentHeaderId(getFinancialDocumentReferenceInvoiceNumber());
        } catch (WorkflowException e) {
            throw new RuntimeException("WorkflowException thrown when trying to retrieve Invoice document [" + getFinancialDocumentReferenceInvoiceNumber() + "]", e);
        }
        return customerInvoiceDocument;
    }

    public SystemInformation getSystemInformation() {
        String processingOrgCode = getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        String processingChartCode = getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();

        SystemInformationService sysInfoService = SpringContext.getBean(SystemInformationService.class);
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        SystemInformation systemInformation = sysInfoService.getByProcessingChartOrgAndFiscalYear(processingChartCode, processingOrgCode, currentFiscalYear);

        if (systemInformation == null) {
            throw new RuntimeException("The InvoicePaidApplied doesnt have an associated SystemInformation.  This should never happen.");
        }
        return systemInformation;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the paidAppliedItemNumber attribute.
     *
     * @return Returns the paidAppliedItemNumber
     */
    public Integer getPaidAppliedItemNumber() {
        return paidAppliedItemNumber;
    }

    /**
     * Sets the paidAppliedItemNumber attribute.
     *
     * @param paidAppliedItemNumber The paidAppliedItemNumber to set.
     */
    public void setPaidAppliedItemNumber(Integer paidAppliedItemNumber) {
        this.paidAppliedItemNumber = paidAppliedItemNumber;
    }


    /**
     * Gets the financialDocumentReferenceInvoiceNumber attribute.
     *
     * @return Returns the financialDocumentReferenceInvoiceNumber
     */
    public String getFinancialDocumentReferenceInvoiceNumber() {
        return financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Sets the financialDocumentReferenceInvoiceNumber attribute.
     *
     * @param financialDocumentReferenceInvoiceNumber The financialDocumentReferenceInvoiceNumber to set.
     */
    public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
        this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
    }

    /**
     * Gets the invoiceItemNumber attribute.
     *
     * @return Returns the invoiceItemNumber
     */
    public Integer getInvoiceItemNumber() {
        return invoiceItemNumber;
    }

    /**
     * Sets the invoiceItemNumber attribute.
     *
     * @param invoiceItemNumber The invoiceItemNumber to set.
     */
    public void setInvoiceItemNumber(Integer invoiceItemNumber) {
        this.invoiceItemNumber = invoiceItemNumber;
    }

    /**
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the universityFiscalPeriodCode attribute.
     *
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    /**
     * Sets the universityFiscalPeriodCode attribute.
     *
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    public FinancialSystemDocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }

    /**
     * Gets the invoiceItemAppliedAmount attribute.
     *
     * @return Returns the invoiceItemAppliedAmount
     */
    public KualiDecimal getInvoiceItemAppliedAmount() {
        return invoiceItemAppliedAmount;
    }

    /**
     * Sets the invoiceItemAppliedAmount attribute.
     *
     * @param invoiceItemAppliedAmount The invoiceItemAppliedAmount to set.
     */
    public void setInvoiceItemAppliedAmount(KualiDecimal invoiceItemAppliedAmount) {
        this.invoiceItemAppliedAmount = invoiceItemAppliedAmount;
    }

    /**
     * Gets the invoiceItem attribute.
     *
     * @return Returns the invoiceItem
     */
    public CustomerInvoiceDetail getInvoiceDetail() {
        return invoiceDetail;
    }

    /**
     * Gets the universityFiscalPeriod attribute.
     *
     * @return Returns the universityFiscalPeriod
     */
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod attribute.
     *
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     * @deprecated
     */
    @Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.paidAppliedItemNumber != null) {
            m.put("paidAppliedItemNumber", this.paidAppliedItemNumber.toString());
        }
        return m;
    }

    /**
     * Get the paidAppiedDistributionAmount attribute.
     *
     * @return Returns the paidAppiedDistributionAmount
     */
    public KualiDecimal getPaidAppiedDistributionAmount() {
        return paidAppiedDistributionAmount;
    }

    /**
     * Set the paidAppiedDistributionAmount attribute.
     *
     * @param paidAppiedDistributionAmount The paidAppiedDistributionAmount to set.
     */
    public void setPaidAppiedDistributionAmount(KualiDecimal paidAppiedDistributionAmount) {
        this.paidAppiedDistributionAmount = paidAppiedDistributionAmount;
    }

    /**
     * Gets the nonInvoicedDistributions attribute.
     *
     * @return Returns the nonInvoicedDistributions.
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributions() {
        return nonInvoicedDistributions;
    }

    /**
     * Sets the nonInvoicedDistributions attribute value.
     *
     * @param nonInvoicedDistributions The nonInvoicedDistributions to set.
     */
    public void setNonInvoicedDistributions(Collection<NonInvoicedDistribution> nonInvoicedDistributions) {
        this.nonInvoicedDistributions = nonInvoicedDistributions;
    }

    /**
     * Gets the nonAppliedDistributions attribute.
     *
     * @return Returns the nonAppliedDistributions.
     */
    public Collection<NonAppliedDistribution> getNonAppliedDistributions() {
        return nonAppliedDistributions;
    }

    /**
     * Sets the nonAppliedDistributions attribute value.
     *
     * @param nonAppliedDistributions The nonAppliedDistributions to set.
     */
    public void setNonAppliedDistributions(List<NonAppliedDistribution> nonAppliedDistributions) {
        this.nonAppliedDistributions = nonAppliedDistributions;
    }

    /**
     * Sets the customerInvoiceDocument attribute value.
     *
     * @param customerInvoiceDocument The customerInvoiceDocument to set.
     */
    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }
}
