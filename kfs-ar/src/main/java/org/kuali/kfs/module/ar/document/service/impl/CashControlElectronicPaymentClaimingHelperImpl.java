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
package org.kuali.kfs.module.ar.document.service.impl;

import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.UrlFactory;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;

import java.util.List;
import java.util.Properties;

public class CashControlElectronicPaymentClaimingHelperImpl implements ElectronicPaymentClaimingDocumentGenerationStrategy {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlElectronicPaymentClaimingHelperImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected DocumentService documentService;
    protected ElectronicPaymentClaimingService electronicPaymentClaimingService;
    protected CashControlDocumentService cashControlDocumentService;
    protected ConfigurationService kualiConfigurationService;
    protected FinancialSystemUserService financialSystemUserService;
    private static DocumentTypeService documentTypeService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#createDocumentFromElectronicPayments(java.util.List,
     * org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
        CashControlDocument document = null;
        try {
            document = (CashControlDocument) documentService.getNewDocument(getClaimingDocumentWorkflowDocumentType());
            document.setCustomerPaymentMediumCode(ArConstants.PaymentMediumCode.WIRE_TRANSFER);

            //create and set AccountsReceivableDocumentHeader
            ChartOrgHolder currentUser = financialSystemUserService.getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
            accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
            document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

            addDescriptionToDocument(document);
            addNotesToDocument(document, electronicPayments, user);
            addCashControlDetailsToDocument(document, electronicPayments);
            documentService.saveDocument(document);
            electronicPaymentClaimingService.claimElectronicPayments(electronicPayments, document.getDocumentNumber());
        } catch (WorkflowException we) {
            throw new RuntimeException("WorkflowException while creating a CashControlDocument to claim ElectronicPaymentClaim records.", we);
        }

        return getURLForDocument(document);
    }

    /**
     * This method add a description to the cash control document
     *
     * @param document the cash control document
     */
    protected void addDescriptionToDocument(CashControlDocument document) {
        document.getDocumentHeader().setDocumentDescription(kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.ELECTRONIC_PAYMENT_CLAIM));
    }

    /**
     * This method adds notes to the cash control document
     *
     * @param claimingDoc the cash control document
     * @param claims      the list of electronic payments being claimed
     * @param user        the current user
     */
    protected void addNotesToDocument(CashControlDocument claimingDoc, List<ElectronicPaymentClaim> claims, Person user) {
        for (String noteText : electronicPaymentClaimingService.constructNoteTextsForClaims(claims)) {
            Note note = documentService.createNoteFromDocument(claimingDoc, noteText);
            claimingDoc.addNote(note);
            documentService.saveDocumentNotes(claimingDoc);
        }
    }

    /**
     * This method adds new cash control details to the cash control document based on the list of electronic payments.
     *
     * @param document           cash control document
     * @param electronicPayments the electronic payments to be claimed
     * @throws WorkflowException workflow exception
     */
    protected void addCashControlDetailsToDocument(CashControlDocument document, List<ElectronicPaymentClaim> electronicPayments) throws WorkflowException {
        for (ElectronicPaymentClaim electronicPaymentClaim : electronicPayments) {
            CashControlDetail newCashControlDetail = new CashControlDetail();
            newCashControlDetail.setCashControlDocument(document);
            newCashControlDetail.setDocumentNumber(document.getDocumentNumber());
            newCashControlDetail.setFinancialDocumentLineAmount(electronicPaymentClaim.getGeneratingAccountingLine().getAmount());
            newCashControlDetail.setCustomerPaymentDescription(electronicPaymentClaim.getGeneratingAccountingLine().getFinancialDocumentLineDescription());
            cashControlDocumentService.addNewCashControlDetail(kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), document, newCashControlDetail);
        }

    }

    /**
     * Builds the URL that can be used to redirect to the correct document
     *
     * @param doc the document to build the URL for
     * @return the relative URL to redirect to
     */
    protected String getURLForDocument(CashControlDocument doc) {
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.DOC_HANDLER_METHOD);
        parameters.put(KFSConstants.PARAMETER_COMMAND, KewApiConstants.DOCSEARCH_COMMAND);
        parameters.put(KFSConstants.PARAMETER_DOC_ID, doc.getDocumentNumber());

        return UrlFactory.parameterizeUrl(ArConstants.UrlActions.CASH_CONTROL_DOCUMENT, parameters);
    }

    /**
     * @return the name CashControlDocument workflow document type
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getClaimingDocumentWorkflowDocumentType()
     */
    @Override
    public String getClaimingDocumentWorkflowDocumentType() {
        return KFSConstants.FinancialDocumentTypeCodes.CASH_CONTROL;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentLabel()
     */
    @Override
    public String getDocumentLabel() {
        return getDocumentTypeService().getDocumentTypeByName(getClaimingDocumentWorkflowDocumentType()).getLabel();
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#isDocumentReferenceValid(java.lang.String)
     */
    @Override
    public boolean isDocumentReferenceValid(String referenceDocumentNumber) {
        boolean isValid = false;
        try {
            long docNumberAsLong = Long.parseLong(referenceDocumentNumber);
            if (docNumberAsLong > 0L) {
                isValid = documentService.documentExists(referenceDocumentNumber);
            }
        } catch (NumberFormatException nfe) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#userMayUseToClaim(org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean userMayUseToClaim(Person claimingUser) {
        final String documentTypeName = this.getClaimingDocumentWorkflowDocumentType();

        final boolean canClaim = electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, documentTypeName) || electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, null);

        return canClaim;
    }

    public void setCashControlDocumentService(CashControlDocumentService cashControlDocumentService) {
        this.cashControlDocumentService = cashControlDocumentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KewApiServiceLocator.getDocumentTypeService();
        }
        return documentTypeService;
    }

    /**
     * Sets the accountsReceivableDocumentHeaderService attribute value.
     *
     * @param accountsReceivableDocumentHeaderService The accountsReceivableDocumentHeaderService to set.
     */
    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    public FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }
}

