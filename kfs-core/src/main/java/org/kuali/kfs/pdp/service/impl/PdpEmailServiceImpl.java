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
package org.kuali.kfs.pdp.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.batch.ExtractAchPaymentsStep;
import org.kuali.kfs.pdp.batch.LoadPaymentsStep;
import org.kuali.kfs.pdp.batch.SendAchAdviceNotificationsStep;
import org.kuali.kfs.pdp.businessobject.ACHBank;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.service.AchBankService;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class PdpEmailServiceImpl implements PdpEmailService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpEmailServiceImpl.class);

    protected CustomerProfileService customerProfileService;
    protected ConfigurationService kualiConfigurationService;
    protected EmailService emailService;
    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;
    protected AchBankService achBankService;

    @Override
    public void sendErrorEmail(PaymentFileLoad paymentFile, MessageMap errors) {
        LOG.debug("sendErrorEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        BodyMailMessage message = new BodyMailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if (StringUtils.isEmpty(returnAddress)) {
            returnAddress = emailService.getDefaultFromAddress();
        }
        message.setFromAddress(returnAddress);
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_FAILURE_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuilder body = new StringBuilder();
        List<String> ccAddresses = new ArrayList<>(parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC));

        if (paymentFile == null) {
            if (ccAddresses.isEmpty()) {
                LOG.error("sendErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                return;
            }

            message.getToAddresses().addAll(ccAddresses);

            body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_BAD_FILE_PARSE) + "\n\n");
        } else {
            CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());
            if (customer == null) {
                LOG.error("sendErrorEmail() Invalid Customer.  Sending email to CC addresses");

                if (ccAddresses.isEmpty()) {
                    LOG.error("sendErrorEmail() No HARD_EDIT_CC addresses.  No email sent");
                    return;
                }

                message.getToAddresses().addAll(ccAddresses);

                body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_INVALID_CUSTOMER) + "\n\n");
            } else {
                String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
                List<String> toAddressList = Arrays.asList(toAddresses.split(","));

                message.getToAddresses().addAll(toAddressList);
                message.getCcAddresses().addAll(ccAddresses);
                //TODO: for some reason the mail service does not work unless the bcc list has addresss. This is a temporary workaround
                message.getBccAddresses().addAll(ccAddresses);
            }
        }

        if (paymentFile != null) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_NOT_LOADED) + "\n\n");
            addPaymentFieldsToBody(body, null, paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());
        }

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_ERROR_MESSAGES) + "\n");
        List<ErrorMessage> errorMessages = errors.getMessages(KFSConstants.GLOBAL_ERRORS);
        if (errorMessages != null) {
            for (ErrorMessage errorMessage : errorMessages) {
                body.append(getMessage(errorMessage.getErrorKey(), (Object[]) errorMessage.getMessageParameters()) + "\n\n");
            }

            message.setMessage(body.toString());

            emailService.sendMessage(message,false);
        }
    }

    @Override
    public void sendLoadEmail(PaymentFileLoad paymentFile, List<String> warnings) {
        LOG.debug("sendLoadEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        BodyMailMessage message = new BodyMailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if (StringUtils.isEmpty(returnAddress)) {
            returnAddress = emailService.getDefaultFromAddress();
        }
        message.setFromAddress(returnAddress);
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_SUCCESS_EMAIL_SUBJECT_PARAMETER_NAME));

        List<String> ccAddresses = new ArrayList<String>(parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC));
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);

        CustomerProfile customer = customerProfileService.get(paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit());
        String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));

        message.getToAddresses().addAll(toAddressList);

        StringBuilder body = new StringBuilder();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_WARNING_MESSAGES) + "\n");
        for (String warning : warnings) {
            body.append(warning + "\n\n");
        }

        message.setMessage(body.toString());

        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }

        if (paymentFile.isFileThreshold()) {
            sendThresholdEmail(true, paymentFile, customer);
        }

        if (paymentFile.isDetailThreshold()) {
            sendThresholdEmail(false, paymentFile, customer);
        }
    }

    /**
     * Sends email for a payment that was over the customer file threshold or the detail threshold
     *
     * @param fileThreshold indicates whether the file threshold (true) was violated or the detail threshold (false)
     * @param paymentFile   parsed payment file object
     * @param customer      payment customer
     */
    protected void sendThresholdEmail(boolean fileThreshold, PaymentFileLoad paymentFile, CustomerProfile customer) {
        BodyMailMessage message = new BodyMailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if (StringUtils.isEmpty(returnAddress)) {
            returnAddress = emailService.getDefaultFromAddress();
        }
        message.setFromAddress(returnAddress);
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_THRESHOLD_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuilder body = new StringBuilder();

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        if (fileThreshold) {
            String toAddresses = StringUtils.deleteWhitespace(customer.getFileThresholdEmailAddress());
            List<String> toAddressList = Arrays.asList(toAddresses.split(","));

            message.getToAddresses().addAll(toAddressList);
            body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_THRESHOLD, paymentFile.getPaymentTotalAmount(), customer.getFileThresholdAmount()));
        } else {
            String toAddresses = StringUtils.deleteWhitespace(customer.getPaymentThresholdEmailAddress());
            List<String> toAddressList = Arrays.asList(toAddresses.split(","));

            message.getToAddresses().addAll(toAddressList);

            body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_DETAIL_THRESHOLD, customer.getPaymentThresholdAmount()) + "\n\n");
            for (PaymentDetail paymentDetail : paymentFile.getThresholdPaymentDetails()) {
                paymentDetail.refresh();
                body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_THRESHOLD, paymentDetail.getPaymentGroup().getPayeeName(), paymentDetail.getNetPaymentAmount()) + "\n");
            }
        }

        List<String> ccAddresses = new ArrayList<String>(parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC));
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);

        message.setMessage(body.toString());

        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    @Override
    public void sendTaxEmail(PaymentFileLoad paymentFile) {
        LOG.debug("sendTaxEmail() starting");

        BodyMailMessage message = new BodyMailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if (StringUtils.isEmpty(returnAddress)) {
            returnAddress = emailService.getDefaultFromAddress();
        }
        message.setFromAddress(returnAddress);
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_TAX_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuilder body = new StringBuilder();

        String taxEmail = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.TAX_GROUP_EMAIL_ADDRESS);
        if (StringUtils.isBlank(taxEmail)) {
            LOG.error("No Tax E-mail Application Setting found to send notification e-mail");
            return;
        } else {
            message.addToAddress(taxEmail);
        }
        List<String> ccAddresses = new ArrayList<String>(parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC));
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_TAX_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, paymentFile.getBatchId().intValue(), paymentFile.getChart(), paymentFile.getUnit(), paymentFile.getSubUnit(), paymentFile.getCreationDate(), paymentFile.getPaymentCount(), paymentFile.getPaymentTotalAmount());

        body.append("\n" + getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_GO_TO_PDP) + "\n");

        message.setMessage(body.toString());

        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    @Override
    public void sendLoadEmail(Batch batch) {
        LOG.debug("sendLoadEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        BodyMailMessage message = new BodyMailMessage();

        String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if (StringUtils.isEmpty(returnAddress)) {
            returnAddress = emailService.getDefaultFromAddress();
        }
        message.setFromAddress(returnAddress);
        message.setSubject(getEmailSubject(PdpParameterConstants.PAYMENT_LOAD_SUCCESS_EMAIL_SUBJECT_PARAMETER_NAME));

        StringBuilder body = new StringBuilder();

        List<String> ccAddresses = new ArrayList<String>(parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.HARD_EDIT_CC));
        message.getCcAddresses().addAll(ccAddresses);
        message.getBccAddresses().addAll(ccAddresses);

        CustomerProfile customer = batch.getCustomerProfile();
        String toAddresses = StringUtils.deleteWhitespace(customer.getProcessingEmailAddr());
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));

        message.getToAddresses().addAll(toAddressList);

        body.append(getMessage(PdpKeyConstants.MESSAGE_PAYMENT_EMAIL_FILE_LOADED) + "\n\n");
        addPaymentFieldsToBody(body, batch.getId().intValue(), customer.getChartCode(), customer.getUnitCode(), customer.getSubUnitCode(), batch.getCustomerFileCreateTimestamp(), batch.getPaymentCount().intValue(), batch.getPaymentTotalAmount());

        message.setMessage(body.toString());

        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    @Override
    public void sendExceedsMaxNotesWarningEmail(List<String> creditMemos, List<String> paymentRequests, int lineTotal, int maxNoteLines) {
        LOG.debug("sendExceedsMaxNotesWarningEmail() starting");

        // check email configuration
        if (!isPaymentEmailEnabled()) {
            return;
        }

        BodyMailMessage message = new BodyMailMessage();
        String returnAddress = parameterService.getParameterValueAsString(KFSConstants.ParameterNamespaces.PDP, "Batch", KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if (StringUtils.isEmpty(returnAddress)) {
            returnAddress = emailService.getDefaultFromAddress();
        }
        message.setFromAddress(returnAddress);

        StringBuilder body = new StringBuilder();
        message.setSubject(getMessage(PdpKeyConstants.MESSAGE_PURAP_EXTRACT_MAX_NOTES_SUBJECT));

        // Get recipient email address
        String toAddresses = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.PDP_ERROR_EXCEEDS_NOTE_LIMIT_EMAIL);
        List<String> toAddressList = Arrays.asList(toAddresses.split(","));
        message.getToAddresses().addAll(toAddressList);

        List<String> ccAddresses = new ArrayList<String>(parameterService.getParameterValuesAsString(LoadPaymentsStep.class, PdpParameterConstants.SOFT_EDIT_CC));
        message.getCcAddresses().addAll(ccAddresses);


        message.getBccAddresses().addAll(ccAddresses);

        body.append(getMessage(PdpKeyConstants.MESSAGE_PURAP_EXTRACT_MAX_NOTES_MESSAGE, StringUtils.join(creditMemos, ","), StringUtils.join(paymentRequests, ","), lineTotal, maxNoteLines));
        message.setMessage(body.toString());

        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendExceedsMaxNotesWarningEmail() Invalid email address. Message not sent", e);
        }
    }

    @Override
    public void sendAchSummaryEmail(Map<String, Integer> unitCounts, Map<String, KualiDecimal> unitTotals, Date disbursementDate) {
        LOG.debug("sendAchSummaryEmail() starting");

        BodyMailMessage message = new BodyMailMessage();

        List<String> toAddressList = new ArrayList<String>(parameterService.getParameterValuesAsString(ExtractAchPaymentsStep.class, PdpParameterConstants.ACH_SUMMARY_TO_EMAIL_ADDRESS_PARMAETER_NAME));
        message.getToAddresses().addAll(toAddressList);
        message.getCcAddresses().addAll(toAddressList);
        message.getBccAddresses().addAll(toAddressList);

        String returnAddress = parameterService.getParameterValueAsString(KFSConstants.ParameterNamespaces.PDP, "Batch", KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
        if (StringUtils.isEmpty(returnAddress)) {
            returnAddress = emailService.getDefaultFromAddress();
        }
        message.setFromAddress(returnAddress);

        String subject = parameterService.getParameterValueAsString(ExtractAchPaymentsStep.class, PdpParameterConstants.ACH_SUMMARY_EMAIL_SUBJECT_PARAMETER_NAME);
        message.setSubject(subject);

        StringBuilder body = new StringBuilder();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_DISB_DATE, disbursementDate) + "\n");

        Integer totalCount = 0;
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (String unit : unitCounts.keySet()) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_UNIT_TOTAL, StringUtils.leftPad(unit, 13), StringUtils.leftPad(unitCounts.get(unit).toString(), 10), StringUtils.leftPad(unitTotals.get(unit).toString(), 20)) + "\n");

            totalCount = totalCount + unitCounts.get(unit);
            totalAmount = totalAmount.add(unitTotals.get(unit));
        }

        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_EXTRACT_TOTALS, StringUtils.leftPad(totalCount.toString(), 10), StringUtils.leftPad(totalAmount.toString(), 20)) + "\n");
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_SUMMARY_EMAIL_COMPLETE));

        message.setMessage(body.toString());

        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendAchSummaryEmail() Invalid email address. Message not sent", e);
        }
    }

    @Override
    public void sendAchAdviceEmail(PaymentGroup paymentGroup, PaymentDetail paymentDetail, CustomerProfile customer) {
        LOG.debug("sendAchAdviceEmail() starting");

        BodyMailMessage message = new BodyMailMessage();
        String fromAddresses = customer.getAdviceReturnEmailAddr();
        String toAddresses = paymentGroup.getAdviceEmailAddress();
        Collection<String> ccAddresses = parameterService.getParameterValuesAsString(SendAchAdviceNotificationsStep.class, PdpParameterConstants.ACH_SUMMARY_CC_EMAIL_ADDRESSES_PARMAETER_NAME);
        Collection<String> bccAddresses = parameterService.getParameterValuesAsString(SendAchAdviceNotificationsStep.class, PdpParameterConstants.ACH_SUMMARY_BCC_EMAIL_ADDRESSES_PARMAETER_NAME);
        String subject = customer.getAdviceSubjectLine();

        message.addToAddress(toAddresses);
        if (!ccAddresses.isEmpty()) {
            message.getCcAddresses().addAll(ccAddresses);
        }
        if (!bccAddresses.isEmpty()) {
            message.getBccAddresses().addAll(bccAddresses);
        }
        message.setFromAddress(fromAddresses);
        message.setSubject(subject);

        LOG.debug("sendAchAdviceEmail() sending email to " + toAddresses + " for disb # " + paymentGroup.getDisbursementNbr());

        StringBuilder body = new StringBuilder();
        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_TOFROM, paymentGroup.getPayeeName(), customer.getAchPaymentDescription()));

        // formatter for payment amounts
        Formatter formatter = new CurrencyFormatter();

        // get bank name to which the payment is being transferred
        String bankName = "";

        ACHBank achBank = achBankService.getByPrimaryId(paymentGroup.getAchBankRoutingNbr());
        if (achBank == null) {
            LOG.error("Bank cound not be found for routing number " + paymentGroup.getAchBankRoutingNbr());
        } else {
            bankName = achBank.getBankName();
        }

        body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_BANKAMOUNT, bankName, formatter.formatForPresentation(paymentDetail.getNetPaymentAmount())));

        // print detail amounts
        int labelPad = 25;

        String newPaymentAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT);
        body.append(StringUtils.rightPad(newPaymentAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getNetPaymentAmount()) + "\n");

        if (paymentDetail.getOrigInvoiceAmount().isNonZero()) {
            String origInvoiceAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_ORIGINAL_INVOICE_AMOUNT);
            body.append(StringUtils.rightPad(origInvoiceAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getOrigInvoiceAmount()) + "\n");
        }

        if (paymentDetail.getInvTotDiscountAmount().isNonZero()) {
            String invTotDiscountAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_DISCOUNT_AMOUNT);
            body.append(StringUtils.rightPad(invTotDiscountAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotDiscountAmount()) + "\n");
        }

        if (paymentDetail.getInvTotShipAmount().isNonZero()) {
            String invTotShippingAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_SHIPPING_AMOUNT);
            body.append(StringUtils.rightPad(invTotShippingAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotShipAmount()) + "\n");
        }

        if (paymentDetail.getInvTotOtherDebitAmount().isNonZero()) {
            String invTotOtherDebitAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_OTHER_DEBIT_AMOUNT);
            body.append(StringUtils.rightPad(invTotOtherDebitAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotOtherDebitAmount()) + "\n");
        }

        if (paymentDetail.getInvTotOtherCreditAmount().isNonZero()) {
            String invTotOtherCreditAmountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_TOTAL_OTHER_CREDIT_AMOUNT);
            body.append(StringUtils.rightPad(invTotOtherCreditAmountLabel, labelPad) + formatter.formatForPresentation(paymentDetail.getInvTotOtherCreditAmount()) + "\n");
        }

        body.append("\n" + customer.getAdviceHeaderText() + "\n");

        if (StringUtils.isNotBlank(paymentDetail.getPurchaseOrderNbr())) {
            String purchaseOrderNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER);
            body.append(StringUtils.rightPad(purchaseOrderNbrLabel, labelPad) + paymentDetail.getPurchaseOrderNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentDetail.getInvoiceNbr())) {
            String invoiceNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER);
            body.append(StringUtils.rightPad(invoiceNbrLabel, labelPad) + paymentDetail.getInvoiceNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentDetail.getCustPaymentDocNbr())) {
            String custPaymentDocNbrLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER);
            body.append(StringUtils.rightPad(custPaymentDocNbrLabel, labelPad) + paymentDetail.getCustPaymentDocNbr() + "\n");
        }

        if (StringUtils.isNotBlank(paymentDetail.getCustomerInstitutionNumber())) {
            String customerInstituitionNbrLabel = dataDictionaryService.getAttributeLabel(PaymentGroup.class, PdpPropertyConstants.CUSTOMER_INSTITUTION_NUMBER);
            body.append(StringUtils.rightPad(customerInstituitionNbrLabel, labelPad) + paymentDetail.getCustomerInstitutionNumber() + "\n");
        }

        body.append("\n");

        // print payment notes
        for (PaymentNoteText paymentNoteText : paymentDetail.getNotes()) {
            body.append(paymentNoteText.getCustomerNoteText() + "\n");
        }

        if (paymentDetail.getNotes().isEmpty()) {
            body.append(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_EMAIL_NONOTES));
        }

        message.setMessage(body.toString());

        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendAchAdviceEmail() Invalid email address. Sending message to " + customer.getAdviceReturnEmailAddr(), e);

            // send notification to advice return address with payment details- need a new MailMessage here of course
            BodyMailMessage msg = new BodyMailMessage();
            msg.addToAddress(customer.getAdviceReturnEmailAddr());

            String returnAddress = parameterService.getParameterValueAsString(KFSConstants.ParameterNamespaces.PDP, "Batch", KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
            if (StringUtils.isEmpty(returnAddress)) {
                returnAddress = emailService.getDefaultFromAddress();
            }
            msg.setFromAddress(returnAddress);
            msg.setSubject(getMessage(PdpKeyConstants.MESSAGE_PDP_ACH_ADVICE_INVALID_EMAIL_ADDRESS));

            LOG.warn("sendAchAdviceEmail() bouncing email to " + customer.getAdviceReturnEmailAddr() + " for disb # " + paymentGroup.getDisbursementNbr());

            emailService.sendMessage(msg,false);
        }
    }

    @Override
    public void sendCancelEmail(PaymentGroup paymentGroup, String note, Person user) {
        LOG.debug("sendCancelEmail() starting");

        BodyMailMessage message = new BodyMailMessage();

        message.setSubject("PDP --- Cancelled Payment by Tax");

        CustomerProfile cp = paymentGroup.getBatch().getCustomerProfile();
        String toAddresses = cp.getAdviceReturnEmailAddr();
        String toAddressList[] = toAddresses.split(",");

        if (toAddressList.length > 0) {
            for (int i = 0; i < toAddressList.length; i++) {
                if (toAddressList[i] != null) {
                    message.addToAddress(toAddressList[i].trim());
                    message.addBccAddress(toAddressList[i].trim());
                }
            }
        }

        String ccAddresses = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.TAX_CANCEL_EMAIL_LIST);
        String ccAddressList[] = ccAddresses.split(",");

        if (ccAddressList.length > 0) {
            for (int i = 0; i < ccAddressList.length; i++) {
                if (ccAddressList[i] != null) {
                    message.addCcAddress(ccAddressList[i].trim());
                }
            }
        }

        message.setFromAddress(emailService.getDefaultFromAddress());

        StringBuilder body = new StringBuilder();

        String messageKey = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_1);
        body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");

        body.append(note + "\n\n");
        String taxEmail = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.TAX_GROUP_EMAIL_ADDRESS);
        String taxContactDepartment = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.TAX_CANCEL_CONTACT);
        if (StringUtils.isBlank(taxEmail)) {
            messageKey = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_2);
            body.append(MessageFormat.format(messageKey, new Object[]{taxContactDepartment}) + " \n\n");
        } else {
            messageKey = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_3);
            body.append(MessageFormat.format(messageKey, new Object[]{taxContactDepartment, taxEmail}) + " \n\n");
        }

        messageKey = kualiConfigurationService.getPropertyValueAsString(PdpKeyConstants.MESSAGE_PDP_PAYMENT_MAINTENANCE_EMAIL_LINE_4);
        body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");

        for (PaymentDetail pd : paymentGroup.getPaymentDetails()) {
            String payeeLabel = dataDictionaryService.getAttributeLabel(PaymentGroup.class, PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_NAME);
            String netPaymentAccountLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT);
            String sourceDocumentNumberLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_CUST_PAYMENT_DOC_NBR);
            String invoiceNumberLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER);
            String purchaseOrderNumberLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER);
            String paymentDetailIdLabel = dataDictionaryService.getAttributeLabel(PaymentDetail.class, PdpPropertyConstants.PaymentDetail.PAYMENT_ID);

            body.append(payeeLabel + ": " + paymentGroup.getPayeeName() + " \n");
            body.append(netPaymentAccountLabel + ": " + pd.getNetPaymentAmount() + " \n");
            body.append(sourceDocumentNumberLabel + ": " + pd.getCustPaymentDocNbr() + " \n");
            body.append(invoiceNumberLabel + ": " + pd.getInvoiceNbr() + " \n");
            body.append(purchaseOrderNumberLabel + ": " + pd.getPurchaseOrderNbr() + " \n");
            body.append(paymentDetailIdLabel + ": " + pd.getId() + "\n");
        }

        body.append(MessageFormat.format(messageKey, new Object[]{null}) + " \n\n");

        String batchIdLabel = dataDictionaryService.getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.BATCH_ID);
        String chartMessageLabel = dataDictionaryService.getAttributeLabel(CustomerProfile.class, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_CHART_CODE);
        String organizationLabel = dataDictionaryService.getAttributeLabel(CustomerProfile.class, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_UNIT_CODE);
        String subUnitLabel = dataDictionaryService.getAttributeLabel(CustomerProfile.class, PdpPropertyConstants.CustomerProfile.CUSTOMER_PROFILE_SUB_UNIT_CODE);
        String creationDateLabel = dataDictionaryService.getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.FILE_CREATION_TIME);
        String paymentCountLabel = dataDictionaryService.getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.PAYMENT_COUNT);
        String paymentTotalLabel = dataDictionaryService.getAttributeLabel(Batch.class, PdpPropertyConstants.BatchConstants.PAYMENT_TOTAL_AMOUNT);

        body.append(batchIdLabel + ": " + paymentGroup.getBatch().getId() + " \n");
        body.append(chartMessageLabel + ": " + cp.getChartCode() + " \n");
        body.append(organizationLabel + ": " + cp.getUnitCode() + " \n");
        body.append(subUnitLabel + ": " + cp.getSubUnitCode() + " \n");
        body.append(creationDateLabel + ": " + paymentGroup.getBatch().getCustomerFileCreateTimestamp() + " \n\n");
        body.append(paymentCountLabel + ": " + paymentGroup.getBatch().getPaymentCount() + " \n\n");
        body.append(paymentTotalLabel + ": " + paymentGroup.getBatch().getPaymentTotalAmount() + " \n\n");

        message.setMessage(body.toString());

        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendErrorEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * Writes out payment file field labels and values to <code>StringBuffer</code>
     *
     * @param body <code>StringBuffer</code>
     */
    protected void addPaymentFieldsToBody(StringBuilder body, Integer batchId, String chart, String unit, String subUnit, Date createDate, int paymentCount, KualiDecimal paymentTotal) {
        String batchIdLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.BATCH_ID);
        body.append(batchIdLabel + ": " + batchId + "\n");

        String chartLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, KFSPropertyConstants.CHART);
        body.append(chartLabel + ": " + chart + "\n");

        String orgLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.UNIT);
        body.append(orgLabel + ": " + unit + "\n");

        String subUnitLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.SUB_UNIT);
        body.append(subUnitLabel + ": " + subUnit + "\n");

        String createDateLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.CREATION_DATE);
        body.append(createDateLabel + ": " + createDate + "\n");

        String paymentCountLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.PAYMENT_COUNT);
        body.append("\n" + paymentCountLabel + ": " + paymentCount + "\n");

        String paymentTotalLabel = dataDictionaryService.getAttributeLabel(PaymentFileLoad.class, PdpPropertyConstants.PAYMENT_TOTAL_AMOUNT);
        body.append(paymentTotalLabel + ": " + paymentTotal + "\n");
    }

    /**
     * Sends notification e-mail that an immediate extract Disbursement Voucher has been extracted
     *
     * @param paymentSource       the disbursement voucher which was immediately extracted
     * @param fromAddress         the address that the e-mail should be sent from
     * @param toAddresses         the addresses that the e-mail should be sent to
     */
    @Override
    public void sendPaymentSourceImmediateExtractEmail(PaymentSource paymentSource, String fromAddress, Collection<String> toAddresses) {
        BodyMailMessage message = new BodyMailMessage();

        final String disbursementVoucherDocumentLabel = dataDictionaryService.getDocumentLabelByTypeName(DisbursementVoucherConstants.DOCUMENT_TYPE_CODE);
        final String subject = getMessage(KFSKeyConstants.MESSAGE_PAYMENT_SOURCE_IMMEDIATE_EXTRACT_EMAIL_SUBJECT, disbursementVoucherDocumentLabel, paymentSource.getCampusCode());
        final String body = getMessage(KFSKeyConstants.MESSAGE_PAYMENT_SOURCE_IMMEDIATE_EXTRACT_EMAIL_BODY, disbursementVoucherDocumentLabel, paymentSource.getCampusCode(), paymentSource.getDocumentNumber());

        message.setFromAddress(fromAddress);
        for (String toAddress : toAddresses) {
            message.addToAddress(toAddress);
        }
        message.setSubject(subject);
        message.setMessage(body);
        try {
            emailService.sendMessage(message,false);
        } catch (Exception e) {
            LOG.error("sendPaymentSourceImmediateExtractEmail() Invalid email address. Message not sent", e);
        }
    }

    /**
     * Reads system parameter indicating whether to status emails should be sent
     *
     * @return true if email should be sent, false otherwise
     */
    @Override
    public boolean isPaymentEmailEnabled() {
        boolean sendEmail = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.SEND_ACH_EMAIL_NOTIFICATION);
        if (!sendEmail) {
            LOG.debug("isPaymentEmailEnabled() sending payment file email is disabled");
        }
        return sendEmail;
    }

    /**
     * Retrieves the email subject text from system parameter then checks environment code and prepends to message if not
     * production.
     *
     * @param subjectParmaterName name of parameter giving the subject text
     * @return subject text
     */
    protected String getEmailSubject(String subjectParmaterName) {
        return parameterService.getParameterValueAsString(LoadPaymentsStep.class, subjectParmaterName);
    }

    /**
     * Helper method to retrieve a message from resources and substitute place holder values
     *
     * @param messageKey        key of message in resource file
     * @param messageParameters parameter for message
     * @return <code>String</code> Message with substituted values
     */
    protected String getMessage(String messageKey, Object... messageParameters) {
        String message = kualiConfigurationService.getPropertyValueAsString(messageKey);
        return MessageFormat.format(message, messageParameters);
    }

    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setAchBankService(AchBankService achBankService) {
        this.achBankService = achBankService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
