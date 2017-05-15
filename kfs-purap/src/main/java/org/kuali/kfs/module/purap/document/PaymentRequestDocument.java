/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.document;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.module.purap.document.service.AccountsPayableDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.ShippingPaymentTerms;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Payment Request Document Business Object. Contains the fields associated with the main document table.
 */
public class PaymentRequestDocument extends AccountsPayableDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestDocument.class);

    protected Date invoiceDate;
    protected String invoiceNumber;
    protected KualiDecimal vendorInvoiceAmount;
    protected String vendorPaymentTermsCode;
    protected String vendorShippingPaymentTermsCode;
    protected Date paymentRequestPayDate;
    protected String paymentRequestCostSourceCode;
    protected boolean paymentRequestedCancelIndicator;
    protected boolean paymentAttachmentIndicator;
    protected boolean immediatePaymentIndicator;
    protected String specialHandlingInstructionLine1Text;
    protected String specialHandlingInstructionLine2Text;
    protected String specialHandlingInstructionLine3Text;
    protected Timestamp paymentPaidTimestamp;
    protected boolean paymentRequestElectronicInvoiceIndicator;
    protected String accountsPayableRequestCancelIdentifier;
    protected Integer originalVendorHeaderGeneratedIdentifier;
    protected Integer originalVendorDetailAssignedIdentifier;
    protected Integer alternateVendorHeaderGeneratedIdentifier;
    protected Integer alternateVendorDetailAssignedIdentifier;
    protected String purchaseOrderNotes;
    protected String recurringPaymentTypeCode;
    protected boolean receivingDocumentRequiredIndicator;
    protected boolean paymentRequestPositiveApprovalIndicator;
    private boolean achSignUpStatusFlag;

    //KFSCNTRB-1207 - UMD - Muddu -- start
    //the indicator which tells if the preq has been auto approved and this value will be used
    //by the doRouteStatus method to change the app doc status.
    protected boolean autoApprovedIndicator;
    //KFSCNTRB-1207 - UMD - Muddu -- end

    // TAX EDIT AREA FIELDS
    protected String taxClassificationCode;
    protected String taxCountryCode;
    protected String taxNQIId;
    protected BigDecimal taxFederalPercent; // number is in whole form so 5% is 5.00
    protected BigDecimal taxStatePercent; // number is in whole form so 5% is 5.00
    protected KualiDecimal taxSpecialW4Amount;
    protected Boolean taxGrossUpIndicator;
    protected Boolean taxExemptTreatyIndicator;
    protected Boolean taxForeignSourceIndicator;
    protected Boolean taxUSAIDPerDiemIndicator;
    protected Boolean taxOtherExemptIndicator;

    protected String justification;

    // NOT PERSISTED IN DB
    protected String vendorShippingTitleCode;
    protected Date purchaseOrderEndDate;
    protected String primaryVendorName;

    // BELOW USED BY ROUTING
    protected Integer requisitionIdentifier;

    // REFERENCE OBJECTS
    protected PaymentTermType vendorPaymentTerms;
    protected ShippingPaymentTerms vendorShippingPaymentTerms;
    protected PurchaseOrderCostSource paymentRequestCostSource;
    protected RecurringPaymentType recurringPaymentType;

    /**
     * Default constructor.
     */
    public PaymentRequestDocument() {
        super();

        //KFSCNTRB-12207 - UMD - Muddu
        this.setAutoApprovedIndicator(false);
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    public boolean isBoNotesSupport() {
        return true;
    }

    public Integer getPostingYearPriorOrCurrent() {
        if (SpringContext.getBean(PaymentRequestService.class).allowBackpost(this)) {
            // allow prior; use it
            return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear() - 1;
        }
        // don't allow prior; use CURRENT
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }


    /**
     * Overrides the method in PurchasingAccountsPayableDocumentBase to add the criteria specific to Payment Request Document.
     *
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#isInquiryRendered()
     */
    @Override
    public boolean isInquiryRendered() {
        if (isPostingYearPrior() && (getApplicationDocumentStatus().equals(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED) || getApplicationDocumentStatus().equals(PurapConstants.PaymentRequestStatuses.APPDOC_AUTO_APPROVED) || getApplicationDocumentStatus().equals(PurapConstants.PaymentRequestStatuses.APPDOC_CANCELLED_POST_AP_APPROVE) || getApplicationDocumentStatus().equals(PurapConstants.PaymentRequestStatuses.APPDOC_CANCELLED_IN_PROCESS))) {
            return false;
        } else {
            return true;
        }
    }

    public Integer getRequisitionIdentifier() {
        return getPurchaseOrderDocument().getRequisitionIdentifier();
    }

    public void setRequisitionIdentifier(Integer requisitionIdentifier) {
        this.requisitionIdentifier = requisitionIdentifier;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        this.setRequisitionIdentifier(getPurchaseOrderDocument().getRequisitionIdentifier());
        super.populateDocumentForRouting();
    }

    /**
     * Decides whether receivingDocumentRequiredIndicator functionality shall be enabled according to the controlling parameter.
     */
    public boolean isEnableReceivingDocumentRequiredIndicator() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.RECEIVING_DOCUMENT_REQUIRED_IND);
    }

    /**
     * Decides whether paymentRequestPositiveApprovalIndicator functionality shall be enabled according to the controlling parameter.
     */
    public boolean isEnablePaymentRequestPositiveApprovalIndicator() {
        return SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.PAYMENT_REQUEST_POSITIVE_APPROVAL_IND);
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        if (!StringUtils.isEmpty(invoiceNumber)) {
            this.invoiceNumber = invoiceNumber.toUpperCase();
        } else {
            this.invoiceNumber = invoiceNumber;
        }
    }

    public KualiDecimal getVendorInvoiceAmount() {
        return vendorInvoiceAmount;
    }

    public void setVendorInvoiceAmount(KualiDecimal vendorInvoiceAmount) {
        this.vendorInvoiceAmount = vendorInvoiceAmount;
    }

    public String getVendorPaymentTermsCode() {
        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
        refreshReferenceObject("vendorPaymentTerms");
    }

    public PaymentTermType getVendorPaymentTerms() {
        if (ObjectUtils.isNull(vendorPaymentTerms) || !StringUtils.equalsIgnoreCase(getVendorPaymentTermsCode(), vendorPaymentTerms.getVendorPaymentTermsCode())) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_PAYMENT_TERMS);
        }
        return vendorPaymentTerms;
    }

    public void setVendorPaymentTerms(PaymentTermType vendorPaymentTerms) {
        this.vendorPaymentTerms = vendorPaymentTerms;
    }

    public String getVendorShippingPaymentTermsCode() {
        if (ObjectUtils.isNull(vendorPaymentTerms)) {
            refreshReferenceObject(VendorPropertyConstants.VENDOR_SHIPPING_PAYMENT_TERMS);
        }
        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public Date getPaymentRequestPayDate() {
        return paymentRequestPayDate;
    }

    public void setPaymentRequestPayDate(Date paymentRequestPayDate) {
        this.paymentRequestPayDate = paymentRequestPayDate;
    }

    public String getPaymentRequestCostSourceCode() {
        return paymentRequestCostSourceCode;
    }

    public void setPaymentRequestCostSourceCode(String paymentRequestCostSourceCode) {
        this.paymentRequestCostSourceCode = paymentRequestCostSourceCode;
    }

    public boolean getPaymentRequestedCancelIndicator() {
        return paymentRequestedCancelIndicator;
    }

    public boolean isPaymentRequestedCancelIndicator() {
        return paymentRequestedCancelIndicator;
    }

    public void setPaymentRequestedCancelIndicator(boolean paymentRequestedCancelIndicator) {
        this.paymentRequestedCancelIndicator = paymentRequestedCancelIndicator;
    }

    public boolean getPaymentAttachmentIndicator() {
        return paymentAttachmentIndicator;
    }

    public void setPaymentAttachmentIndicator(boolean paymentAttachmentIndicator) {
        this.paymentAttachmentIndicator = paymentAttachmentIndicator;
    }

    public boolean getImmediatePaymentIndicator() {
        return immediatePaymentIndicator;
    }

    public void setImmediatePaymentIndicator(boolean immediatePaymentIndicator) {
        this.immediatePaymentIndicator = immediatePaymentIndicator;
    }

    public String getSpecialHandlingInstructionLine1Text() {
        return specialHandlingInstructionLine1Text;
    }

    public void setSpecialHandlingInstructionLine1Text(String specialHandlingInstructionLine1Text) {
        this.specialHandlingInstructionLine1Text = specialHandlingInstructionLine1Text;
    }

    public String getSpecialHandlingInstructionLine2Text() {
        return specialHandlingInstructionLine2Text;
    }

    public void setSpecialHandlingInstructionLine2Text(String specialHandlingInstructionLine2Text) {
        this.specialHandlingInstructionLine2Text = specialHandlingInstructionLine2Text;
    }

    public String getSpecialHandlingInstructionLine3Text() {
        return specialHandlingInstructionLine3Text;
    }

    public void setSpecialHandlingInstructionLine3Text(String specialHandlingInstructionLine3Text) {
        this.specialHandlingInstructionLine3Text = specialHandlingInstructionLine3Text;
    }

    public Timestamp getPaymentPaidTimestamp() {
        return paymentPaidTimestamp;
    }

    public void setPaymentPaidTimestamp(Timestamp paymentPaidTimestamp) {
        this.paymentPaidTimestamp = paymentPaidTimestamp;
    }

    public boolean getPaymentRequestElectronicInvoiceIndicator() {
        return paymentRequestElectronicInvoiceIndicator;
    }

    public void setPaymentRequestElectronicInvoiceIndicator(boolean paymentRequestElectronicInvoiceIndicator) {
        this.paymentRequestElectronicInvoiceIndicator = paymentRequestElectronicInvoiceIndicator;
    }

    public String getAccountsPayableRequestCancelIdentifier() {
        return accountsPayableRequestCancelIdentifier;
    }

    public void setAccountsPayableRequestCancelIdentifier(String accountsPayableRequestCancelIdentifier) {
        this.accountsPayableRequestCancelIdentifier = accountsPayableRequestCancelIdentifier;
    }

    public Integer getOriginalVendorHeaderGeneratedIdentifier() {
        return originalVendorHeaderGeneratedIdentifier;
    }

    public void setOriginalVendorHeaderGeneratedIdentifier(Integer originalVendorHeaderGeneratedIdentifier) {
        this.originalVendorHeaderGeneratedIdentifier = originalVendorHeaderGeneratedIdentifier;
    }

    public Integer getOriginalVendorDetailAssignedIdentifier() {
        return originalVendorDetailAssignedIdentifier;
    }

    public void setOriginalVendorDetailAssignedIdentifier(Integer originalVendorDetailAssignedIdentifier) {
        this.originalVendorDetailAssignedIdentifier = originalVendorDetailAssignedIdentifier;
    }

    public Integer getAlternateVendorHeaderGeneratedIdentifier() {
        return alternateVendorHeaderGeneratedIdentifier;
    }

    public void setAlternateVendorHeaderGeneratedIdentifier(Integer alternateVendorHeaderGeneratedIdentifier) {
        this.alternateVendorHeaderGeneratedIdentifier = alternateVendorHeaderGeneratedIdentifier;
    }

    public Integer getAlternateVendorDetailAssignedIdentifier() {
        return alternateVendorDetailAssignedIdentifier;
    }

    public void setAlternateVendorDetailAssignedIdentifier(Integer alternateVendorDetailAssignedIdentifier) {
        this.alternateVendorDetailAssignedIdentifier = alternateVendorDetailAssignedIdentifier;
    }

    public ShippingPaymentTerms getVendorShippingPaymentTerms() {
        return vendorShippingPaymentTerms;
    }

    public void setVendorShippingPaymentTerms(ShippingPaymentTerms vendorShippingPaymentTerms) {
        this.vendorShippingPaymentTerms = vendorShippingPaymentTerms;
    }

    public String getVendorShippingTitleCode() {
        if (ObjectUtils.isNotNull(this.getPurchaseOrderDocument())) {
            return this.getPurchaseOrderDocument().getVendorShippingTitleCode();
        }
        return vendorShippingTitleCode;
    }

    public void setVendorShippingTitleCode(String vendorShippingTitleCode) {
        this.vendorShippingTitleCode = vendorShippingTitleCode;
    }

    public Date getPurchaseOrderEndDate() {
        return purchaseOrderEndDate;
    }

    public void setPurchaseOrderEndDate(Date purchaseOrderEndDate) {
        this.purchaseOrderEndDate = purchaseOrderEndDate;
    }

    /**
     * Gets the paymentRequestPositiveApprovalIndicator attribute.
     *
     * @return Returns the paymentRequestPositiveApprovalIndicator.
     */
    public boolean isPaymentRequestPositiveApprovalIndicator() {
        return paymentRequestPositiveApprovalIndicator;
    }

    /**
     * Sets the paymentRequestPositiveApprovalIndicator attribute value.
     *
     * @param paymentRequestPositiveApprovalIndicator The paymentRequestPositiveApprovalIndicator to set.
     */
    public void setPaymentRequestPositiveApprovalIndicator(boolean paymentRequestPositiveApprovalIndicator) {
        // if paymentRequestPositiveApprovalIndicator functionality is disabled, always set it to false, overriding the passed-in value
        if (!isEnablePaymentRequestPositiveApprovalIndicator()) {
            paymentRequestPositiveApprovalIndicator = false;
        } else {
            this.paymentRequestPositiveApprovalIndicator = paymentRequestPositiveApprovalIndicator;
        }
    }

    /**
     * Gets the receivingDocumentRequiredIndicator attribute.
     *
     * @return Returns the receivingDocumentRequiredIndicator.
     */
    public boolean isReceivingDocumentRequiredIndicator() {
        return receivingDocumentRequiredIndicator;
    }

    /**
     * Sets the receivingDocumentRequiredIndicator attribute value.
     *
     * @param receivingDocumentRequiredIndicator The receivingDocumentRequiredIndicator to set.
     */
    public void setReceivingDocumentRequiredIndicator(boolean receivingDocumentRequiredIndicator) {
        // if receivingDocumentRequiredIndicator functionality is disabled, always set it to false, overriding the passed-in value
        if (!isEnableReceivingDocumentRequiredIndicator()) {
            receivingDocumentRequiredIndicator = false;
        } else {
            this.receivingDocumentRequiredIndicator = receivingDocumentRequiredIndicator;
        }
    }

    /**
     * Perform logic needed to initiate PREQ Document
     */
    public void initiateDocument() throws WorkflowException {
        LOG.debug("initiateDocument() started");
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_INITIATE);
        this.setAccountsPayableProcessorIdentifier(currentUser.getPrincipalId());
        this.setProcessingCampusCode(currentUser.getCampusCode());
        this.refreshNonUpdateableReferences();
    }

    /**
     * Perform logic needed to clear the initial fields on a PREQ Document
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");
        // Clearing document overview fields
        this.getDocumentHeader().setDocumentDescription(null);
        this.getDocumentHeader().setExplanation(null);
        this.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(null);
        this.getDocumentHeader().setOrganizationDocumentNumber(null);

        // Clearing document Init fields
        this.setPurchaseOrderIdentifier(null);
        this.setInvoiceNumber(null);
        this.setInvoiceDate(null);
        this.setVendorInvoiceAmount(null);
        this.setSpecialHandlingInstructionLine1Text(null);
        this.setSpecialHandlingInstructionLine2Text(null);
        this.setSpecialHandlingInstructionLine3Text(null);
    }

    /**
     * Populates a preq from a PO - delegate method
     *
     * @param po -
     */
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po) {
        populatePaymentRequestFromPurchaseOrder(po, new HashMap<String, ExpiredOrClosedAccountEntry>());
    }


    /**
     * Populates a preq from a PO
     *
     * @param po                         Purchase Order Document used for populating the PREQ
     * @param expiredOrClosedAccountList a list of closed or expired accounts
     */
    public void populatePaymentRequestFromPurchaseOrder(PurchaseOrderDocument po, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        this.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
        this.getDocumentHeader().setOrganizationDocumentNumber(po.getDocumentHeader().getOrganizationDocumentNumber());
        this.setPostingYear(po.getPostingYear());
        this.setReceivingDocumentRequiredIndicator(po.isReceivingDocumentRequiredIndicator());
        this.setUseTaxIndicator(po.isUseTaxIndicator());
        this.setPaymentRequestPositiveApprovalIndicator(po.isPaymentRequestPositiveApprovalIndicator());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        this.setAccountDistributionMethod(po.getAccountDistributionMethod());

        if (po.getPurchaseOrderCostSource() != null) {
            this.setPaymentRequestCostSource(po.getPurchaseOrderCostSource());
            this.setPaymentRequestCostSourceCode(po.getPurchaseOrderCostSourceCode());
        }

        if (po.getVendorShippingPaymentTerms() != null) {
            this.setVendorShippingPaymentTerms(po.getVendorShippingPaymentTerms());
            this.setVendorShippingPaymentTermsCode(po.getVendorShippingPaymentTermsCode());
        }

        if (po.getVendorPaymentTerms() != null) {
            this.setVendorPaymentTermsCode(po.getVendorPaymentTermsCode());
            this.setVendorPaymentTerms(po.getVendorPaymentTerms());
        }

        if (po.getRecurringPaymentType() != null) {
            this.setRecurringPaymentType(po.getRecurringPaymentType());
            this.setRecurringPaymentTypeCode(po.getRecurringPaymentTypeCode());
        }

        this.setVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());
        this.setVendorCustomerNumber(po.getVendorCustomerNumber());
        this.setVendorName(po.getVendorName());

        // set original vendor
        this.setOriginalVendorHeaderGeneratedIdentifier(po.getVendorHeaderGeneratedIdentifier());
        this.setOriginalVendorDetailAssignedIdentifier(po.getVendorDetailAssignedIdentifier());

        // set alternate vendor info as well
        this.setAlternateVendorHeaderGeneratedIdentifier(po.getAlternateVendorHeaderGeneratedIdentifier());
        this.setAlternateVendorDetailAssignedIdentifier(po.getAlternateVendorDetailAssignedIdentifier());

        // populate preq vendor address with the default remit address type for the vendor if found
        String userCampus = GlobalVariables.getUserSession().getPerson().getCampusCode();
        VendorAddress vendorAddress = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(po.getVendorHeaderGeneratedIdentifier(), po.getVendorDetailAssignedIdentifier(), VendorConstants.AddressTypes.REMIT, userCampus);
        if (vendorAddress != null) {
            this.templateVendorAddress(vendorAddress);
            this.setVendorAddressGeneratedIdentifier(vendorAddress.getVendorAddressGeneratedIdentifier());
            setVendorAttentionName(StringUtils.defaultString(vendorAddress.getVendorAttentionName()));
        } else {
            // set address from PO
            this.setVendorAddressGeneratedIdentifier(po.getVendorAddressGeneratedIdentifier());
            this.setVendorLine1Address(po.getVendorLine1Address());
            this.setVendorLine2Address(po.getVendorLine2Address());
            this.setVendorCityName(po.getVendorCityName());
            this.setVendorAddressInternationalProvinceName(po.getVendorAddressInternationalProvinceName());
            this.setVendorStateCode(po.getVendorStateCode());
            this.setVendorPostalCode(po.getVendorPostalCode());
            this.setVendorCountryCode(po.getVendorCountryCode());

            boolean blankAttentionLine = StringUtils.equalsIgnoreCase("Y", SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "Document", PurapParameterConstants.BLANK_ATTENTION_LINE_FOR_PO_TYPE_ADDRESS));

            if (blankAttentionLine) {
                setVendorAttentionName(StringUtils.EMPTY);
            } else {
                setVendorAttentionName(StringUtils.defaultString(po.getVendorAttentionName()));
            }
        }

        this.setPaymentRequestPayDate(SpringContext.getBean(PaymentRequestService.class).calculatePayDate(this.getInvoiceDate(), this.getVendorPaymentTerms()));

        AccountsPayableService accountsPayableService = SpringContext.getBean(AccountsPayableService.class);

        if (SpringContext.getBean(PaymentRequestService.class).encumberedItemExistsForInvoicing(po)) {
            for (PurchaseOrderItem poi : (List<PurchaseOrderItem>) po.getItems()) {
                // check to make sure it's eligible for payment (i.e. active and has encumbrance available
                if (getDocumentSpecificService().poItemEligibleForAp(this, poi)) {
                    PaymentRequestItem paymentRequestItem = new PaymentRequestItem(poi, this, expiredOrClosedAccountList);
                    this.getItems().add(paymentRequestItem);
                    PurchasingCapitalAssetItem purchasingCAMSItem = po.getPurchasingCapitalAssetItemByItemIdentifier(poi.getItemIdentifier());
                    if (purchasingCAMSItem != null) {
                        paymentRequestItem.setCapitalAssetTransactionTypeCode(purchasingCAMSItem.getCapitalAssetTransactionTypeCode());
                    }

                    /*
                    // copy usetaxitems over
                    paymentRequestItem.getUseTaxItems().clear();
                    for (PurApItemUseTax useTax : poi.getUseTaxItems()) {
                        paymentRequestItem.getUseTaxItems().add(useTax);
                    }
                    */
                }
            }
        }

        // add missing below the line
        SpringContext.getBean(PurapService.class).addBelowLineItems(this);
        this.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

        //fix up below the line items
        SpringContext.getBean(PaymentRequestService.class).removeIneligibleAdditionalCharges(this);

        this.fixItemReferences();
        this.refreshNonUpdateableReferences();
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#getDocumentTitle()
     */
    @Override
    public String getDocumentTitle() {
        if (SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(PaymentRequestDocument.class, PurapParameterConstants.PURAP_OVERRIDE_PREQ_DOC_TITLE)) {
            return getCustomDocumentTitle();
        }
        return this.buildDocumentTitle(super.getDocumentTitle());
    }

    /**
     * Returns a custom document title based on the workflow document title. Depending on what route level the document is currently
     * in, the PO, vendor, amount, account number, dept, campus may be added to the documents title.
     *
     * @return - Customized document title text dependent upon route level.
     */
    protected String getCustomDocumentTitle() {

        // set the workflow document title
        String poNumber = getPurchaseOrderIdentifier().toString();
        String vendorName = StringUtils.trimToEmpty(getVendorName());
        String preqAmount = getGrandTotal().toString();

        String documentTitle = "";
        Set<String> nodeNames = this.getFinancialSystemDocumentHeader().getWorkflowDocument().getCurrentNodeNames();

        // if this doc is final or will be final
        if (CollectionUtils.isEmpty(nodeNames) || this.getFinancialSystemDocumentHeader().getWorkflowDocument().isFinal()) {
            documentTitle = (new StringBuilder("PO: ")).append(poNumber).append(" Vendor: ").append(vendorName).append(" Amount: ").append(preqAmount).toString();
        } else {
            PurApAccountingLine theAccount = getFirstAccount();
            String accountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getAccountNumber()) : "n/a");
            String subAccountNumber = (theAccount != null ? StringUtils.trimToEmpty(theAccount.getSubAccountNumber()) : "");
            String accountChart = (theAccount != null ? theAccount.getChartOfAccountsCode() : "");
            String payDate = getDateTimeService().toDateString(getPaymentRequestPayDate());
            String indicator = getTitleIndicator();
            // set title to: PO# - VendorName - Chart/Account - total amt - Pay Date - Indicator (ie Hold, Request Cancel)
            documentTitle = (new StringBuilder("PO: ")).append(poNumber).append(" Vendor: ").append(vendorName).
                append(" Account: ").append(accountChart).append(" ").append(accountNumber).append(" ").append(subAccountNumber)
                .append(" Amount: ").append(preqAmount).append(" Pay Date: ").append(payDate).append(" ").append(indicator).toString();
        }
        return documentTitle;
    }

    /**
     * Returns the first payment item's first account (assuming the item list is sequentially ordered).
     *
     * @return - Accounting Line object for first account of first payment item.
     */
    public PurApAccountingLine getFirstAccount() {
        // loop through items, and pick the first item
        if ((getItems() != null) && (!getItems().isEmpty())) {
            PaymentRequestItem itemToUse = null;
            for (Iterator iter = getItems().iterator(); iter.hasNext(); ) {
                PaymentRequestItem item = (PaymentRequestItem) iter.next();
                if ((item.isConsideredEntered()) && ((item.getSourceAccountingLines() != null) && (!item.getSourceAccountingLines().isEmpty()))) {
                    // accounting lines are not empty so pick the first account
                    PurApAccountingLine accountLine = item.getSourceAccountingLine(0);
                    accountLine.refreshNonUpdateableReferences();
                    return accountLine;
                }
                /*
                if (((item.getExtendedPrice() != null) && item.getExtendedPrice().compareTo(BigDecimal.ZERO) > 0) && ((item.getAccounts() != null) && (!item.getAccounts().isEmpty()))) {
                    // accounting lines are not empty so pick the first account
               List accts = (List)item.getAccounts();
               PaymentRequestAccount accountLine = (PaymentRequestAccount)accts.get(0);
                    return accountLine.getFinancialChartOfAccountsCode() + "-" + accountLine.getAccountNumber();
                }
                */
            }
        }
        return null;
    }

    /**
     * Determines the indicator text that will appear in the workflow document title
     *
     * @return - Text of hold or request cancel
     */
    protected String getTitleIndicator() {
        if (isHoldIndicator()) {
            return PurapConstants.PaymentRequestIndicatorText.HOLD;
        } else if (isPaymentRequestedCancelIndicator()) {
            return PurapConstants.PaymentRequestIndicatorText.REQUEST_CANCEL;
        }
        return "";
    }


    /**
     * @see org.kuali.rice.krad.document.DocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        LOG.debug("doRouteStatusChange() started");

        super.doRouteStatusChange(statusChangeEvent);
        try {
            //KFSCNTRB-1207 - UMD - Muddu -- start
            // if the document was processed but approved by the auto approve payment request job
            // then all we want to do is to change the application document status to auto-approved
            // by looking at the autoApprovedIndicator on the preq.
            // DOCUEMNT PROCESSED BY THE AUTOAPPROVEPAYMENTREQUEST JOB...
            if (this.isAutoApprovedIndicator()) {
                updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_AUTO_APPROVED);
            } // DOCUMENT PROCESSED .. //KFSCNTRB-1207 - UMD - Muddu -- end
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
                if (!PaymentRequestStatuses.APPDOC_AUTO_APPROVED.equals(getApplicationDocumentStatus())) {
                    populateDocumentForRouting();
                    updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
                }
            }
            // DOCUMENT DISAPPROVED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isDisapproved()) {
                String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(getDocumentHeader().getWorkflowDocument());
                String disapprovalStatus = PurapConstants.PaymentRequestStatuses.getPaymentRequestAppDocDisapproveStatuses().get(nodeName);

                if (ObjectUtils.isNotNull(nodeName)) {
                    if (((StringUtils.isBlank(disapprovalStatus)) && ((PaymentRequestStatuses.APPDOC_INITIATE.equals(getApplicationDocumentStatus())) || (PaymentRequestStatuses.APPDOC_IN_PROCESS.equals(getApplicationDocumentStatus()))))) {
                        disapprovalStatus = PaymentRequestStatuses.APPDOC_CANCELLED_IN_PROCESS;
                    }
                    if (StringUtils.isNotBlank(disapprovalStatus)) {
                        SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, nodeName);
                    }
                } else {
                    logAndThrowRuntimeException("No status found to set for document being disapproved in node '" + nodeName + "'");
                }
            }
            // DOCUMENT CANCELED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isCanceled()) {
                String currentNodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(this.getDocumentHeader().getWorkflowDocument());
                String cancelledStatus = PurapConstants.PaymentRequestStatuses.getPaymentRequestAppDocDisapproveStatuses().get(currentNodeName);

                //**START AZ** KATTS-37 KevinMcO
                if (StringUtils.isBlank(cancelledStatus) &&
                    StringUtils.isBlank(PurapConstants.PaymentRequestStatuses.getPaymentRequestAppDocDisapproveStatuses().get(currentNodeName)) &&
                    (PaymentRequestStatuses.APPDOC_INITIATE.equals(getApplicationDocumentStatus()) || PaymentRequestStatuses.APPDOC_IN_PROCESS.equals(getApplicationDocumentStatus()))) {
                    cancelledStatus = PaymentRequestStatuses.APPDOC_CANCELLED_IN_PROCESS;
                }
                //**END AZ**

                if (ObjectUtils.isNotNull(cancelledStatus)) {
                    SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(this, currentNodeName);
                    updateAndSaveAppDocStatus(cancelledStatus);
                } else {
                    logAndThrowRuntimeException("No status found to set for document being canceled in node '" + currentNodeName + "'");
                }
            }
        } catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
    }

    /**
     * Generates correcting entries to the GL if accounts are modified.
     *
     * @see org.kuali.rice.krad.document.Document#doActionTaken(org.kuali.rice.kew.clientapp.vo.ActionTakenEventDTO)
     */
    @Override
    public void doActionTaken(ActionTakenEvent event) {
        super.doActionTaken(event);
        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();
        String currentNode = null;
        Set<String> currentNodes = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(currentNodes)) {
            Object[] names = currentNodes.toArray();
            if (names.length > 0) {
                currentNode = (String) names[0];
            }
        }

        // everything in the below list requires correcting entries to be written to the GL
        if (PaymentRequestStatuses.getNodesRequiringCorrectingGeneralLedgerEntries().contains(currentNode)) {
            SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesModifyPaymentRequest(this);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#processNodeChange(java.lang.String, java.lang.String)
     */
    @Override
    public boolean processNodeChange(String newNodeName, String oldNodeName) {
        if (PaymentRequestStatuses.APPDOC_AUTO_APPROVED.equals(getApplicationDocumentStatus())) {
            // do nothing for an auto approval
            return false;
        }
        if (PaymentRequestStatuses.NODE_ADHOC_REVIEW.equals(oldNodeName)) {
            SpringContext.getBean(AccountsPayableService.class).performLogicForFullEntryCompleted(this);
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#saveDocumentFromPostProcessing()
     */
    @Override
    public void saveDocumentFromPostProcessing() {
        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);

        // if we've hit full entry completed then close/reopen po
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(this) && this.isClosePurchaseOrderIndicator()) {
            SpringContext.getBean(PurapService.class).performLogicForCloseReopenPO(this);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return PaymentRequestItem.class;
    }

    @Override
    public Class getItemUseTaxClass() {
        return PaymentRequestItemUseTax.class;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentIfPossible()
     */
    @Override
    public PurchaseOrderDocument getPurApSourceDocumentIfPossible() {
        return getPurchaseOrderDocument();
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getPurApSourceDocumentLabelIfPossible()
     */
    @Override
    public String getPurApSourceDocumentLabelIfPossible() {
        return SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER);
    }

    public String getPurchaseOrderNotes() {

        ArrayList poNotes = (ArrayList) this.getPurchaseOrderDocument().getNotes();

        if (poNotes.size() > 0) {
            return "Yes";
        }
        return "No";
    }

    public void setPurchaseOrderNotes(String purchaseOrderNotes) {
        this.purchaseOrderNotes = purchaseOrderNotes;
    }

    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    /**
     * Returns the total encumbered amount from the purchase order excluding below the line.
     *
     * @return Total cost excluding below the line
     */
    public KualiDecimal getItemTotalPoEncumbranceAmount() {
        // get total from po excluding below the line and inactive
        return this.getPurchaseOrderDocument().getTotalDollarAmount(false, false);
    }

    public KualiDecimal getItemTotalPoEncumbranceAmountRelieved() {
        return getItemTotalPoEncumbranceAmountRelieved(false);
    }

    public KualiDecimal getItemTotalPoEncumbranceAmountRelieved(boolean includeBelowTheLine) {

        KualiDecimal total = KualiDecimal.ZERO;

        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getPurchaseOrderDocument().getItems()) {
            ItemType it = item.getItemType();
            if (includeBelowTheLine || it.isLineItemIndicator()) {
                total = total.add(item.getItemEncumbranceRelievedAmount());
            }
        }
        return total;
    }

    public KualiDecimal getLineItemTotal() {
        return this.getTotalDollarAmountAboveLineItems();
    }

    public KualiDecimal getLineItemPreTaxTotal() {
        return this.getTotalPreTaxDollarAmountAboveLineItems();
    }

    public KualiDecimal getLineItemTaxAmount() {
        return this.getTotalTaxAmountAboveLineItems();
    }

    @Override
    public KualiDecimal getGrandTotal() {
        return this.getTotalDollarAmount();
    }

    public KualiDecimal getGrandTotalExcludingDiscount() {
        String[] discountCode = new String[]{PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE};
        return this.getTotalDollarAmountWithExclusions(discountCode, true);
    }

    /**
     * This method is here due to a setter requirement by the htmlControlAttribute
     *
     * @param amount - Grand total for document, excluding discount
     */
    public void setGrandTotalExcludingDiscount(KualiDecimal amount) {
        // do nothing
    }

    public KualiDecimal getGrandPreTaxTotal() {
        return this.getTotalPreTaxDollarAmount();
    }

    public KualiDecimal getGrandPreTaxTotalExcludingDiscount() {
        String[] discountCode = new String[]{PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE};
        return this.getTotalPreTaxDollarAmountWithExclusions(discountCode, true);
    }

    public KualiDecimal getGrandTaxAmount() {
        return this.getTotalTaxAmount();
    }

    public KualiDecimal getGrandTaxAmountExcludingDiscount() {
        String[] discountCode = new String[]{PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE};
        return this.getTotalTaxAmountWithExclusions(discountCode, true);
    }

    public boolean isDiscount() {
        return SpringContext.getBean(PaymentRequestService.class).hasDiscountItem(this);
    }

    /**
     * The total that was paid on the po excluding below the line
     *
     * @return total paid
     */
    public KualiDecimal getItemTotalPoPaidAmount() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (PurchaseOrderItem item : (List<PurchaseOrderItem>) getPurchaseOrderDocument().getItems()) {
            ItemType iT = item.getItemType();
            if (iT.isLineItemIndicator()) {
                KualiDecimal itemPaid = item.getItemPaidAmount();
                total = total.add(itemPaid);
            }
        }
        return total;
    }

    /**
     * Returns the name of who requested cancel.
     *
     * @return - name of who requested cancel.
     */
    public String getAccountsPayableRequestCancelPersonName() {
        String personName = null;
        Person user = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPerson(getAccountsPayableRequestCancelIdentifier());
        if (user != null) {
            personName = user.getName();
        } else {
            personName = "";
        }

        return personName;
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     *
     * @param amount - total po amount paid
     */
    public void setItemTotalPoPaidAmount(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     *
     * @param amount - total po encumbrance
     */
    public void setItemTotalPoEncumbranceAmount(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     *
     * @param amount - total po encumbrance amount relieved
     */
    public void setItemTotalPoEncumbranceAmountRelieved(KualiDecimal amount) {
        // do nothing
    }

    /**
     * Determinines the route levels for a given document.
     *
     * @param workflowDocument - work flow document
     * @return List - list of route levels
     */
    protected List<String> getCurrentRouteLevels(WorkflowDocument workflowDocument) {
        Set<String> names = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(names)) {
            return new ArrayList<String>(names);
        }
        return null;
    }

    public RecurringPaymentType getRecurringPaymentType() {
        if (ObjectUtils.isNull(recurringPaymentType)) {
            refreshReferenceObject(PurapPropertyConstants.RECURRING_PAYMENT_TYPE);
        }
        return recurringPaymentType;
    }

    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }

    public PurchaseOrderCostSource getPaymentRequestCostSource() {
        return paymentRequestCostSource;
    }

    public void setPaymentRequestCostSource(PurchaseOrderCostSource paymentRequestCostSource) {
        this.paymentRequestCostSource = paymentRequestCostSource;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#getPoDocumentTypeForAccountsPayableDocumentApprove()
     */
    @Override
    public String getPoDocumentTypeForAccountsPayableDocumentCancel() {
        return PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#getInitialAmount()
     */
    @Override
    public KualiDecimal getInitialAmount() {
        return this.getVendorInvoiceAmount();
    }

    /**
     * Populates the payment request document, then continues with preparing for save.
     *
     * @see org.kuali.rice.krad.document.Document#prepareForSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        WorkflowDocument workflowDocument = this.getDocumentHeader().getWorkflowDocument();
        String workflowDocumentTitle = this.buildDocumentTitle(workflowDocument.getTitle());

        this.getFinancialSystemDocumentHeader().getWorkflowDocument().setTitle(workflowDocumentTitle);

        // first populate, then call super
        if (event instanceof AttributedContinuePurapEvent) {
            SpringContext.getBean(PaymentRequestService.class).populatePaymentRequest(this);
        }
        super.prepareForSave(event);

    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase#isAttachmentRequired()
     */
    @Override
    protected boolean isAttachmentRequired() {
        if (getPaymentRequestElectronicInvoiceIndicator()) {
            return false;
        }
        return StringUtils.equalsIgnoreCase("Y", SpringContext.getBean(ParameterService.class).getParameterValueAsString(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PREQ_REQUIRE_ATTACHMENT));
    }

    /**
     * @see org.kuali.kfs.module.purap.document.AccountsPayableDocument#getDocumentSpecificService()
     */
    @Override
    public AccountsPayableDocumentSpecificService getDocumentSpecificService() {
        return SpringContext.getBean(PaymentRequestService.class);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase#getItem(int)
     */
    @Override
    public PurApItem getItem(int pos) {
        PaymentRequestItem item = (PaymentRequestItem) super.getItem(pos);
        if (item.getPaymentRequest() == null) {
            item.setPaymentRequest(this);
        }
        return item;
    }

    public String getPrimaryVendorName() {

        if (primaryVendorName == null) {
            VendorDetail vd = SpringContext.getBean(VendorService.class).getVendorDetail(this.getOriginalVendorHeaderGeneratedIdentifier(), this.getOriginalVendorDetailAssignedIdentifier());

            if (vd != null) {
                primaryVendorName = vd.getVendorName();
            }
        }

        return primaryVendorName;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setPrimaryVendorName(String primaryVendorName) {
    }

    /**
     * Forces general ledger entries to be approved, does not wait for payment request document final approval.
     *
     * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument,
     * org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);

        SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(this, (AccountingLine) postable, explicitEntry, getPurchaseOrderIdentifier(), getDebitCreditCodeForGLEntries(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT, isGenerateEncumbranceEntries());

        // PREQs do not wait for document final approval to post GL entries; here we are forcing them to be APPROVED
        explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
    }

    /**
     * Provides answers to the following splits: PurchaseWasReceived VendorIsEmployeeOrNonResidentAlien
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.REQUIRES_IMAGE_ATTACHMENT)) {
            return requiresAccountsPayableReviewRouting();
        }
        if (nodeName.equals(PurapWorkflowConstants.PURCHASE_WAS_RECEIVED)) {
            return shouldWaitForReceiving();
        }
        if (nodeName.equals(PurapWorkflowConstants.VENDOR_IS_EMPLOYEE_OR_NON_RESIDENT_ALIEN)) {
            return isVendorEmployeeOrNonResidentAlien();
        }
        if (nodeName.equals(PurapWorkflowConstants.IS_DOCUMENT_AUTO_APPROVED)) {
            return isAutoApprovedIndicator();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    protected boolean isVendorEmployeeOrNonResidentAlien() {
        String vendorHeaderGeneratedId = this.getVendorHeaderGeneratedIdentifier().toString();
        if (StringUtils.isBlank(vendorHeaderGeneratedId)) {
            // no vendor header id so can't check for proper tax routing
            return false;
        }
        VendorService vendorService = SpringContext.getBean(VendorService.class);
        boolean routeDocumentAsEmployeeVendor = vendorService.isVendorInstitutionEmployee(Integer.valueOf(vendorHeaderGeneratedId));
        boolean routeDocumentAsForeignVendor = vendorService.isVendorForeign(Integer.valueOf(vendorHeaderGeneratedId));
        if ((!routeDocumentAsEmployeeVendor) && (!routeDocumentAsForeignVendor)) {
            // no need to route
            return false;
        }

        return true;
    }

    /**
     * Payment Request needs to wait for receiving if the receiving requirements have NOT been met.
     *
     * @return
     */
    protected boolean shouldWaitForReceiving() {
        // only require if PO was marked to require receiving
        if (isReceivingDocumentRequiredIndicator()) {
            return !isReceivingRequirementMet();
        }

        //receiving is not required or has already been fulfilled, no need to stop for routing
        return false;
    }

    /**
     * Determine if the receiving requirement has been met for all items on the payment request. If any item does not have receiving
     * requirements met, return false. Receiving requirement has NOT been met if the quantity invoiced on the Payment Request is
     * greater than the quantity of "unpaid and received" items determined by (poQtyReceived - (poQtyInvoiced - preqQtyInvoiced)).
     * We have to subtract preqQtyInvoiced from the poQtyInvoiced because this payment request has already updated the totals on the
     * po.
     *
     * @return boolean return true if the receiving requirement has been met for all items on the payment request; false if
     * requirement has not been met
     */
    public boolean isReceivingRequirementMet() {

        for (Iterator iter = getItems().iterator(); iter.hasNext(); ) {
            PaymentRequestItem preqItem = (PaymentRequestItem) iter.next();

            if (preqItem.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                PurchaseOrderItem poItem = preqItem.getPurchaseOrderItem();
                KualiDecimal preqQuantityInvoiced = preqItem.getItemQuantity() == null ? KualiDecimal.ZERO : preqItem.getItemQuantity();
                KualiDecimal poQuantityReceived = poItem.getItemReceivedTotalQuantity() == null ? KualiDecimal.ZERO : poItem.getItemReceivedTotalQuantity();
                KualiDecimal poQuantityInvoiced = poItem.getItemInvoicedTotalQuantity() == null ? KualiDecimal.ZERO : poItem.getItemInvoicedTotalQuantity();

                // receiving has NOT been met if preqQtyInvoiced is greater than (poQtyReceived & (poQtyInvoiced & preqQtyInvoiced))
                if (preqQuantityInvoiced.compareTo(poQuantityReceived.subtract(poQuantityInvoiced.subtract(preqQuantityInvoiced))) > 0) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Date getTransactionTaxDate() {
        return getInvoiceDate();
    }

    public String getTaxClassificationCode() {
        return taxClassificationCode;
    }

    public void setTaxClassificationCode(String taxClassificationCode) {
        this.taxClassificationCode = taxClassificationCode;
    }

    public KualiDecimal getTaxFederalPercentShort() {
        return new KualiDecimal(taxFederalPercent);
    }

    public BigDecimal getTaxFederalPercent() {
        return taxFederalPercent;
    }

    public void setTaxFederalPercent(BigDecimal taxFederalPercent) {
        this.taxFederalPercent = taxFederalPercent;
    }

    public KualiDecimal getTaxStatePercentShort() {
        return new KualiDecimal(taxStatePercent);
    }

    public BigDecimal getTaxStatePercent() {
        return taxStatePercent;
    }

    public void setTaxStatePercent(BigDecimal taxStatePercent) {
        this.taxStatePercent = taxStatePercent;
    }

    public String getTaxCountryCode() {
        return taxCountryCode;
    }

    public void setTaxCountryCode(String taxCountryCode) {
        this.taxCountryCode = taxCountryCode;
    }

    public Boolean getTaxGrossUpIndicator() {
        return taxGrossUpIndicator;
    }

    public void setTaxGrossUpIndicator(Boolean taxGrossUpIndicator) {
        this.taxGrossUpIndicator = taxGrossUpIndicator;
    }

    public Boolean getTaxExemptTreatyIndicator() {
        return taxExemptTreatyIndicator;
    }

    public void setTaxExemptTreatyIndicator(Boolean taxExemptTreatyIndicator) {
        this.taxExemptTreatyIndicator = taxExemptTreatyIndicator;
    }

    public Boolean getTaxForeignSourceIndicator() {
        return taxForeignSourceIndicator;
    }

    public void setTaxForeignSourceIndicator(Boolean taxForeignSourceIndicator) {
        this.taxForeignSourceIndicator = taxForeignSourceIndicator;
    }

    public KualiDecimal getTaxSpecialW4Amount() {
        return taxSpecialW4Amount;
    }

    public void setTaxSpecialW4Amount(KualiDecimal taxSpecialW4Amount) {
        this.taxSpecialW4Amount = taxSpecialW4Amount;
    }

    public Boolean getTaxUSAIDPerDiemIndicator() {
        return taxUSAIDPerDiemIndicator;
    }

    public void setTaxUSAIDPerDiemIndicator(Boolean taxUSAIDPerDiemIndicator) {
        this.taxUSAIDPerDiemIndicator = taxUSAIDPerDiemIndicator;
    }

    public Boolean getTaxOtherExemptIndicator() {
        return taxOtherExemptIndicator;
    }

    public void setTaxOtherExemptIndicator(Boolean taxOtherExemptIndicator) {
        this.taxOtherExemptIndicator = taxOtherExemptIndicator;
    }

    public String getTaxNQIId() {
        return taxNQIId;
    }

    public void setTaxNQIId(String taxNQIId) {
        this.taxNQIId = taxNQIId;
    }

    public boolean isPaymentRequestedCancelIndicatorForSearching() {
        return paymentRequestedCancelIndicator;
    }

    /**
     * @return the payment request positive approval indicator
     */
    public boolean getPaymentRequestPositiveApprovalIndicatorForSearching() {
        return paymentRequestPositiveApprovalIndicator;
    }

    /**
     * @return the achSignUpStatusFlag
     */
    public boolean isAchSignUpStatusFlag() {
        return achSignUpStatusFlag;
    }

    /**
     * @param achSignUpStatusFlag the achSignUpStatusFlag to set
     */
    public void setAchSignUpStatusFlag(boolean achSignUpStatusFlag) {
        this.achSignUpStatusFlag = achSignUpStatusFlag;
    }

    /**
     * @return the receiving document required indicator
     */
    public boolean getReceivingDocumentRequiredIndicatorForSearching() {
        return receivingDocumentRequiredIndicator;
    }


    public String getRequestCancelIndicatorForResult() {
        return isPaymentRequestedCancelIndicator() ? "Yes" : "No";
    }

    public String getPaidIndicatorForResult() {
        return getPaymentPaidTimestamp() != null ? "Yes" : "No";
    }

    public Date getAccountsPayableApprovalDateForSearching() {
        if (this.getAccountsPayableApprovalTimestamp() == null) {
            return null;
        }
        try {
            Date date = SpringContext.getBean(DateTimeService.class).convertToSqlDate(this.getAccountsPayableApprovalTimestamp());
            if (LOG.isDebugEnabled()) {
                LOG.debug("getAccountsPayableApprovalDateForSearching() returns " + date);
            }
            return date;
        } catch (Exception e) {
            return new Date(this.getAccountsPayableApprovalTimestamp().getTime());
        }
    }

    /**
     * Checks all documents notes for attachments.
     *
     * @return - true if document does not have an image attached, false otherwise
     */
    @Override
    public boolean documentHasNoImagesAttached() {
        List boNotes = this.getNotes();
        if (ObjectUtils.isNotNull(boNotes)) {
            for (Object obj : boNotes) {
                Note note = (Note) obj;

                note.refreshReferenceObject("attachment");
                if (ObjectUtils.isNotNull(note.getAttachment()) && PurapConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_INVOICE_IMAGE.equals(note.getAttachment().getAttachmentTypeCode())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets the justification attribute.
     *
     * @return Returns the justification.
     */
    public String getJustification() {
        return justification;
    }

    /**
     * Sets the justification attribute value.
     *
     * @param justification The justification to set.
     */
    public void setJustification(String justification) {
        this.justification = justification;
    }

    //KFSCNTRB-1207 - UMD - Muddu -- start

    /**
     * Gets the autoApprovedIndicator attribute.
     *
     * @return Returns the autoApprovedIndicator
     */
    public boolean isAutoApprovedIndicator() {
        return autoApprovedIndicator;
    }

    /**
     * Sets the autoApprovedIndicator attribute.
     *
     * @param autoApprovedIndicator The autoApprovedIndicator to set.
     */
    public void setAutoApprovedIndicator(boolean autoApprovedIndicator) {
        this.autoApprovedIndicator = autoApprovedIndicator;
    }
    //KFSCNTRB-1207 - UMD - Muddu -- end

    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();

        // KFSMI-9022 : JHK : This is a bit of a hack, but it seems that the document header is not being loaded properly from
        // within the post-processor and causes problems.
        if (ObjectUtils.isNull(getDocumentHeader()) || StringUtils.isBlank(getDocumentHeader().getDocumentNumber())) {
            WorkflowDocument workflowDocument = null;
            if (getDocumentHeader().hasWorkflowDocument()) {
                workflowDocument = getDocumentHeader().getWorkflowDocument();
            }
            refreshReferenceObject(KFSPropertyConstants.DOCUMENT_HEADER);
            if (!ObjectUtils.isNull(getDocumentHeader())) {
                getDocumentHeader().setWorkflowDocument(workflowDocument);
            }
        }
    }

}
