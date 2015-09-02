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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Defines a service class for creating Cash Control documents from the LOC Review Document.
 */
@NonTransactional
public class LetterOfCreditCreateServiceImpl implements LetterOfCreditCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LetterOfCreditCreateServiceImpl.class);
    protected CashControlDocumentService cashControlDocumentService;
    protected ConfigurationService configService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected KualiModuleService kualiModuleService;
    protected ParameterService parameterService;
    protected WorkflowDocumentService workflowDocumentService;
    protected PaymentApplicationDocumentService paymentApplicationDocumentService;

    /**
     * @see org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService#routeCashControlDocument(org.kuali.kfs.module.ar.document.CashControlDocument, java.io.PrintWriter)
     */
    @Override
    @Transactional
    public void routeCashControlDocument(CashControlDocument cashControlDocument, PrintWriter errorFile) {
        try {
            if (LOG.isInfoEnabled()) {
                LOG.info("Routing Cash control document # " + cashControlDocument.getDocumentNumber() + ".");
            }
            documentService.prepareWorkflowDocument(cashControlDocument);

            // calling workflow service to bypass business rule checks
            workflowDocumentService.route(cashControlDocument.getDocumentHeader().getWorkflowDocument(), "", null);
        }
        catch (WorkflowException e) {
            final String errorString = "Error routing document # " + cashControlDocument.getDocumentNumber();
            errorFile.println(errorString);
            LOG.error(errorString + " " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService#processLettersOfCreditByFund(java.lang.String)
     */
    @Override
    public void processLettersOfCredit(String batchFileDirectoryName) {
        String customerNumber = null;
        String locValue = null;
        String locCreationType = null;
        boolean cashControlDocumentExists = false;
        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());

        String errOutputFile = batchFileDirectoryName + File.separator + ArConstants.BatchFileSystem.LOC_CREATION_BY_LOCF_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
        File errOutPutFile = new File(errOutputFile);
        PrintWriter errorFile = null;
        try {
            errorFile = new PrintWriter(errOutPutFile);
            Collection<ContractsGrantsInvoiceDocument> cgInvoices = retrieveLetterOfCreditInvoices();
            if (CollectionUtils.isNotEmpty(cgInvoices)) {
                CashControlDocument cashControlDoc = createCashControlDocument(errorFile);
                for (ContractsGrantsInvoiceDocument cgInvoice : cgInvoices) {
                    if (cgInvoice.getOpenAmount().isGreaterThan(KualiDecimal.ZERO)) {
                        processLetterOfCreditInvoice(cgInvoice, cashControlDoc, errorFile);
                    } else {
                        String errorString = configService.getPropertyValueAsString(ArKeyConstants.LOC_CREATION_ERROR_INVOICE_PAID);
                        errorFile.println(MessageFormat.format(errorString, cgInvoice.getDocumentNumber()));
                    }
                }
                routeCashControlDocument(cashControlDoc, errorFile);
            } else {
                String errorString = configService.getPropertyValueAsString(ArKeyConstants.LOC_CREATION_ERROR_NO_INVOICES_TO_PROCESS);
                errorFile.println(errorString);
            }
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Could not write to file in "+batchFileDirectoryName, fnfe);
        } finally {
            // Close the error file.
            if (errorFile != null) {
                errorFile.close();
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService#processLetterOfCreditInvoice(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument, org.kuali.kfs.module.ar.document.CashControlDocument, java.io.PrintWriter)
     */
    @Override
    @Transactional
    public void processLetterOfCreditInvoice(ContractsGrantsInvoiceDocument cgInvoice, CashControlDocument cashControlDoc, PrintWriter errorFile) {
        CashControlDetail cashControlDetail = createCashControlDetail(cgInvoice);
        try {
            cashControlDocumentService.addNewCashControlDetail(configService.getPropertyValueAsString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), cashControlDoc, cashControlDetail);
            String payAppDocNumber = cashControlDetail.getReferenceFinancialDocumentNumber();
            PaymentApplicationDocument payAppDoc;
            payAppDoc = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(payAppDocNumber);
            payAppDoc = paymentApplicationDocumentService.createInvoicePaidAppliedsForEntireInvoiceDocument(cgInvoice, payAppDoc);
            getGeneralLedgerPendingEntryService().generateGeneralLedgerPendingEntries(payAppDoc);
            documentService.saveDocument(payAppDoc);

            if (LOG.isInfoEnabled()) {
                LOG.info("Routing Payment Application document # " + payAppDoc.getDocumentNumber() + ".");
            }
            documentService.prepareWorkflowDocument(payAppDoc);
            // calling workflow service to bypass business rule checks
            workflowDocumentService.route(payAppDoc.getDocumentHeader().getWorkflowDocument(), "", null);
        } catch (WorkflowException ex) {
            String error = "Error creating Cash Control Detail/Payment Application Document, Cash Control doc # " + cashControlDoc.getDocumentNumber();
            errorFile.println(error);
            LOG.error(error + " " + ex.getMessage());
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService#createCashControlDocument(java.io.PrintWriter)
     */
    @Override
    @Transactional
    public CashControlDocument createCashControlDocument(PrintWriter errorFile) {
        CashControlDocument cashControlDoc = null;

        try {
            cashControlDoc = (CashControlDocument) documentService.getNewDocument(CashControlDocument.class);
            cashControlDoc.getDocumentHeader().setDocumentDescription(configService.getPropertyValueAsString(ArKeyConstants.CASH_CTRL_DOC_CREATED_BY_BATCH));
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
            accountsReceivableDocumentHeader.setDocumentNumber(cashControlDoc.getDocumentNumber());
            String defaultProcessingChartCode = parameterService.getParameterValueAsString(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_CHART);
            String defaultProcessingOrgCode = parameterService.getParameterValueAsString(CashControlDocument.class, ArConstants.DEFAULT_PROCESSING_ORG);
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(defaultProcessingChartCode);
            accountsReceivableDocumentHeader.setProcessingOrganizationCode(defaultProcessingOrgCode);
            cashControlDoc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
            cashControlDoc.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.WIRE_TRANSFER);
            documentService.saveDocument(cashControlDoc);
        } catch (WorkflowException ex) {
            String error = "Error creating Cash Control Document, Cash Control doc # " + (ObjectUtils.isNotNull(cashControlDoc)?cashControlDoc.getDocumentNumber():null);
            errorFile.println(error);
            LOG.error(error + " " + ex.getMessage());
            throw new RuntimeException(ex.getMessage(), ex);
        }

        return cashControlDoc;
    }


    /**
     * This method created cashcontrol documents and payment application based on the loc creation type and loc value passed.
     *
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    protected CashControlDetail createCashControlDetail(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        CashControlDetail cashControlDetail = new CashControlDetail();
        cashControlDetail.setCustomerNumber(contractsGrantsInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber());
        cashControlDetail.setFinancialDocumentLineAmount(contractsGrantsInvoiceDocument.getOpenAmount());
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        cashControlDetail.setCustomerPaymentDate(today);

        return cashControlDetail;
    }

    /**
     * Retrieves ContractsAndGrantsInvoiceDocument documents which are open and which have the given letter of credit fund and letter of credit creation type (either fund or fund group)
     * @return a Collection of matching letter of credit created Contracts & Grants Invoices
     */
    protected Collection<ContractsGrantsInvoiceDocument> retrieveLetterOfCreditInvoices() {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.LETTER_OF_CREDIT_CREATION_TYPE, SearchOperator.NOT_NULL.op());
        fieldValues.put(ArPropertyConstants.OPEN_INVOICE_IND, KFSConstants.Booleans.TRUE);
        fieldValues.put(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = contractsGrantsInvoiceDocumentService.retrieveAllCGInvoicesByCriteria(fieldValues);
        return cgInvoices;
    }

    /**
     * Gets the documentService attribute.
     *
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the cashControlDocumentService attribute.
     *
     * @return Returns the cashControlDocumentService.
     */
    public CashControlDocumentService getCashControlDocumentService() {
        return cashControlDocumentService;
    }

    /**
     * Sets the cashControlDocumentService attribute value.
     *
     * @param cashControlDocumentService The cashControlDocumentService to set.
     */
    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }

    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
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

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
    public ConfigurationService getConfigService() {
        return configService;
    }

    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public PaymentApplicationDocumentService getPaymentApplicationDocumentService() {
        return paymentApplicationDocumentService;
    }

    public void setPaymentApplicationDocumentService(PaymentApplicationDocumentService paymentApplicationDocumentService) {
        this.paymentApplicationDocumentService = paymentApplicationDocumentService;
    }
}
