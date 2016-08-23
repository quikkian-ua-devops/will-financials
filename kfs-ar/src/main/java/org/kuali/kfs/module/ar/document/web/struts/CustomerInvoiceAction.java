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
package org.kuali.kfs.module.ar.document.web.struts;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.question.ConfirmationQuestion;
import org.kuali.kfs.kns.util.WebUtils;
import org.kuali.kfs.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.krad.service.KualiRuleService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.DiscountCustomerInvoiceDetailEvent;
import org.kuali.kfs.module.ar.document.validation.event.RecalculateCustomerInvoiceDetailEvent;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.service.AccountsReceivablePdfHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kew.api.exception.WorkflowException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

public class CustomerInvoiceAction extends KualiAccountingDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceAction.class);

    /**
     * Overriding to make it easier to distinguish discount lines and lines that are associated to discounts
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceForm customerInvoiceForm = (CustomerInvoiceForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceForm.getCustomerInvoiceDocument();
        if (StringUtils.isBlank(customerInvoiceDocument.getDocumentNumber())) {
            String docId = request.getParameter(KFSConstants.PARAMETER_DOC_ID);
            customerInvoiceDocument.setDocumentNumber(docId);
            customerInvoiceDocument.refresh();
        }
        customerInvoiceDocument.updateAccountReceivableObjectCodes();
        try {
            // proceed as usual
            customerInvoiceForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
            ActionForward result = super.execute(mapping, form, request, response);
            return result;
        } finally {
            // update it again for display purposes
            customerInvoiceForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
        }
    }

    /**
     * Called when customer invoice document is initiated.
     * <p>
     * Makes a call to parent's createDocument method, but also defaults values for customer invoice document. Line which inserts
     * Customer Invoice Detail (i.e. insertSourceLine) has its values defaulted by
     * CustomerInvoiceForm.createNewSourceAccountingLine()
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        // set up the default values for customer invoice document
        CustomerInvoiceForm customerInvoiceForm = (CustomerInvoiceForm) kualiDocumentFormBase;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceForm.getCustomerInvoiceDocument();
        SpringContext.getBean(CustomerInvoiceDocumentService.class).setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);
    }

    /**
     * All document-load operations get routed through here
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        CustomerInvoiceForm form = (CustomerInvoiceForm) kualiDocumentFormBase;
        form.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();

    }

    /**
     * Method that will take the current document, copy it, replace all references to doc header id with a new one, clear pending
     * entries, clear notes, and reset version numbers
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceForm customerInvoiceForm = (CustomerInvoiceForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceForm.getCustomerInvoiceDocument();

        // perform discount check
        ActionForward forward = performInvoiceWithDiscountsCheck(mapping, form, request, response, customerInvoiceDocument);
        if (forward != null) {
            return forward;
        }

        forward = super.copy(mapping, form, request, response);
        // KFSCNTRB-1737- We don't want to copy the closed date if the (copied) invoice isn't closed.
        if (customerInvoiceDocument.isOpenInvoiceIndicator()) {
            customerInvoiceDocument.setClosedDate(null);
        }
        return forward;
    }


    /**
     * This method checks if the user wants to copy a document that contains a discount line.  If yes, this method returns null. If no,
     * this method returns the "basic" forward.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param customerInvoiceDocument
     * @return
     * @throws Exception
     */
    protected ActionForward performInvoiceWithDiscountsCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, CustomerInvoiceDocument customerInvoiceDocument) throws Exception {
        ActionForward forward = null;

        if (customerInvoiceDocument.hasAtLeastOneDiscount()) {

            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {
                return this.performQuestionWithoutInput(mapping, form, request, response, ArConstants.COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION,
                    "This document contains a discount line.  Are you sure you want to copy this document?", KFSConstants.CONFIRMATION_QUESTION,
                    KFSConstants.ROUTE_METHOD, "");
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (ArConstants.COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION.equals(question) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }

    /**
     * This method is the action for refreshing the added source line (or customer invoice detail) based off a provided invoice item
     * code.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshNewSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceForm customerInvoiceForm = (CustomerInvoiceForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceForm.getCustomerInvoiceDocument();
        CustomerInvoiceDetail newCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceForm.getNewSourceLine();

        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        CustomerInvoiceDetail loadedCustomerInvoiceDetail = customerInvoiceDetailService.getCustomerInvoiceDetailFromCustomerInvoiceItemCode(newCustomerInvoiceDetail.getInvoiceItemCode(), customerInvoiceDocument.getBillByChartOfAccountCode(), customerInvoiceDocument.getBilledByOrganizationCode());
        if (loadedCustomerInvoiceDetail == null) {
            loadedCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceForm.getNewSourceLine();
        }

        customerInvoiceForm.setNewSourceLine(loadedCustomerInvoiceDetail);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method is the action for recalculating the amount added line assuming that the unit price or quantity has changed
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward recalculateSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceForm customerInvoiceForm = (CustomerInvoiceForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceForm.getCustomerInvoiceDocument();

        int index = getSelectedLine(request);
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(index);

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";

        boolean rulePassed = true;
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerInvoiceDetailEvent(errorPath, customerInvoiceForm.getDocument(), customerInvoiceDetail));
        if (rulePassed) {

            CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDocument, customerInvoiceDetail);
            customerInvoiceDetailService.updateAccountsForCorrespondingDiscount(customerInvoiceDetail);
        }

        // Update the doc total
        ((FinancialSystemDocumentHeader) customerInvoiceForm.getDocument().getDocumentHeader()).setFinancialDocumentTotalAmount(customerInvoiceDocument.getTotalDollarAmount());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method is used for inserting a discount line based on a selected source line.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward discountSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceForm customerInvoiceForm = (CustomerInvoiceForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceForm.getCustomerInvoiceDocument();

        int index = getSelectedLine(request);
        CustomerInvoiceDetail parentCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(index);

        // document.sourceAccountingLine[0].invoiceItemUnitPrice
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";

        boolean rulePassed = true;
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new DiscountCustomerInvoiceDetailEvent(errorPath, customerInvoiceForm.getDocument(), parentCustomerInvoiceDetail));
        if (rulePassed) {

            CustomerInvoiceDetail discountCustomerInvoiceDetail = SpringContext.getBean(CustomerInvoiceDetailService.class).getDiscountCustomerInvoiceDetailForCurrentYear(parentCustomerInvoiceDetail, customerInvoiceDocument);
            discountCustomerInvoiceDetail.refreshNonUpdateableReferences();
            insertAccountingLine(true, customerInvoiceForm, discountCustomerInvoiceDetail);

            // also set parent customer invoice detail line to have discount line seq number
            parentCustomerInvoiceDetail.setInvoiceItemDiscountLineNumber(discountCustomerInvoiceDetail.getSequenceNumber());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Removed salesTax checking. Need to verify if this check has be moved out later of the KualiAccountingDocumentActionBase
     * class. If so just use the parent class' insertSourceLine method.
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceForm customerInvoiceForm = (CustomerInvoiceForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceForm.getCustomerInvoiceDocument();
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceForm.getNewSourceLine();

        // populate chartOfAccountsCode from account number if accounts cant cross chart and Javascript is turned off
        //SpringContext.getBean(AccountService.class).populateAccountingLineChartIfNeeded(customerInvoiceDetail);

        // make sure amount is up to date before rules
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        service.recalculateCustomerInvoiceDetail(customerInvoiceDocument, customerInvoiceDetail);

        boolean rulePassed = true;
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, customerInvoiceForm.getDocument(), customerInvoiceDetail));

        if (rulePassed) {

            // add accountingLine
            customerInvoiceDetail.refreshNonUpdateableReferences();
            service.prepareCustomerInvoiceDetailForAdd(customerInvoiceDetail, customerInvoiceDocument);
            insertAccountingLine(true, customerInvoiceForm, customerInvoiceDetail);

            // clear the used newTargetLine
            customerInvoiceForm.setNewSourceLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Overrides method to delete accounting line. If line to be deleted has a corresponding discount line, the corresponding
     * discount line is also deleted. If the line to be delete is a discount line, set the reference for the parent to null
     *
     * @param isSource
     * @param financialDocumentForm
     * @param deleteIndex
     */
    @Override
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {

        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceForm) financialDocumentForm).getCustomerInvoiceDocument();

        // if line to delete is a discount parent discountLine, remove discount line too
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(deleteIndex);
        if (customerInvoiceDetail.isDiscountLineParent()) {
            customerInvoiceDocument.removeDiscountLineBasedOnParentLineIndex(deleteIndex);
        } else if (customerInvoiceDocument.isDiscountLineBasedOnSequenceNumber(customerInvoiceDetail.getSequenceNumber())) {

            // if line to delete is a discount line, set discount line reference for parent to null
            CustomerInvoiceDetail parentCustomerInvoiceDetail = customerInvoiceDetail.getParentDiscountCustomerInvoiceDetail();
            if (ObjectUtils.isNotNull(parentCustomerInvoiceDetail)) {
                parentCustomerInvoiceDetail.setInvoiceItemDiscountLineNumber(null);
            }
        }

        // delete line like normal
        super.deleteAccountingLine(isSource, financialDocumentForm, deleteIndex);
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        refreshBillToAddress(mapping, form, request, response);
        refreshShipToAddress(mapping, form, request, response);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method refresh the ShipToAddress CustomerAddress object
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshBillToAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceForm) form).getCustomerInvoiceDocument();

        CustomerAddress customerBillToAddress = null;
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerBillToAddressIdentifier())) {
            int customerBillToAddressIdentifier = customerInvoiceDocument.getCustomerBillToAddressIdentifier();

            customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerBillToAddressIdentifier);
            if (ObjectUtils.isNotNull(customerBillToAddress)) {
                customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
                customerInvoiceDocument.setCustomerBillToAddressOnInvoice(customerBillToAddress);
                customerInvoiceDocument.setCustomerBillToAddressIdentifier(customerBillToAddressIdentifier);
            } else {
                customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getPrimaryAddress(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber());

                if (ObjectUtils.isNotNull(customerBillToAddress)) {
                    customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
                    customerInvoiceDocument.setCustomerBillToAddressOnInvoice(customerBillToAddress);
                    customerInvoiceDocument.setCustomerBillToAddressIdentifier(customerBillToAddress.getCustomerAddressIdentifier());
                } else {
                    customerInvoiceDocument.setCustomerBillToAddress(null);
                    customerInvoiceDocument.setCustomerBillToAddressOnInvoice(null);
                    customerInvoiceDocument.setCustomerBillToAddressIdentifier(null);
                }
            }

        } else {
            if (!ObjectUtils.isNull(customerInvoiceDocument.getAccountsReceivableDocumentHeader()) && !ObjectUtils.isNull(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber())) {
                customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getPrimaryAddress(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber());

                if (ObjectUtils.isNotNull(customerBillToAddress)) {
                    customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
                    customerInvoiceDocument.setCustomerBillToAddressOnInvoice(customerBillToAddress);
                    customerInvoiceDocument.setCustomerBillToAddressIdentifier(customerBillToAddress.getCustomerAddressIdentifier());
                } else {
                    customerInvoiceDocument.setCustomerBillToAddress(null);
                    customerInvoiceDocument.setCustomerBillToAddressOnInvoice(null);
                    customerInvoiceDocument.setCustomerBillToAddressIdentifier(null);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method refresh the ShipToAddress CustomerAddress object
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshShipToAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceForm) form).getCustomerInvoiceDocument();

        CustomerAddress customerShipToAddress = null;
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getCustomerShipToAddressIdentifier())) {
            int customerShipToAddressIdentifier = customerInvoiceDocument.getCustomerShipToAddressIdentifier();

            customerShipToAddress = SpringContext.getBean(CustomerAddressService.class).getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerShipToAddressIdentifier);
            if (ObjectUtils.isNotNull(customerShipToAddress)) {
                customerInvoiceDocument.setCustomerShipToAddress(customerShipToAddress);
                customerInvoiceDocument.setCustomerShipToAddressOnInvoice(customerShipToAddress);
                customerInvoiceDocument.setCustomerShipToAddressIdentifier(customerShipToAddressIdentifier);
            }
        }
        if ((ObjectUtils.isNull(customerInvoiceDocument.getCustomerShipToAddressIdentifier()) || ObjectUtils.isNull(customerShipToAddress)) && !ObjectUtils.isNull(customerInvoiceDocument.getAccountsReceivableDocumentHeader())) {
            customerInvoiceDocument.setCustomerShipToAddress(null);
            customerInvoiceDocument.setCustomerShipToAddressOnInvoice(null);
            customerInvoiceDocument.setCustomerShipToAddressIdentifier(null);
        }


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method...
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocument invoiceDocument = ((CustomerInvoiceForm) form).getCustomerInvoiceDocument();

        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        File report = reportService.generateInvoice(invoiceDocument);

        if (report.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        byte[] content = Files.readAllBytes(report.toPath());
        ByteArrayOutputStream baos = SpringContext.getBean(AccountsReceivablePdfHelperService.class).buildPdfOutputStream(content);

        StringBuilder fileName = new StringBuilder();
        fileName.append(invoiceDocument.getOrganizationInvoiceNumber());
        fileName.append(KFSConstants.DASH);
        fileName.append(invoiceDocument.getDocumentNumber());
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);

        WebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, baos, fileName.toString());

        return null;
    }

}
