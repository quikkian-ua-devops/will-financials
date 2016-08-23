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
package org.kuali.kfs.module.ar.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.Attachment;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.NoteService;
import org.kuali.kfs.krad.service.impl.MailServiceImpl;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.UpcomingMilestoneNotificationStep;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.service.AREmailService;
import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.mail.AttachmentMailMessage;
import org.kuali.kfs.sys.service.AttachmentMailService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.IOException;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Defines methods for sending AR emails.
 */
public class AREmailServiceImpl implements AREmailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AREmailServiceImpl.class);

    protected AttachmentMailService mailService;
    protected ParameterService parameterService;
    protected DataDictionaryService dataDictionaryService;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected NoteService noteService;
    protected KualiModuleService kualiModuleService;
    protected ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService;

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * This method gets the document service
     *
     * @return the document service
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * This method sets the document service
     *
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * This method is used to send emails to the agency
     *
     * @param invoices
     */
    @Override
    public boolean sendInvoicesViaEmail(Collection<ContractsGrantsInvoiceDocument> invoices) throws InvalidAddressException, MessagingException {
        LOG.debug("sendInvoicesViaEmail() starting.");

        boolean success = true;
        Properties props = getConfigProperties();

        // Get session
        Session session = Session.getInstance(props, null);
        for (ContractsGrantsInvoiceDocument invoice : invoices) {
            List<InvoiceAddressDetail> invoiceAddressDetails = invoice.getInvoiceAddressDetails();
            for (InvoiceAddressDetail invoiceAddressDetail : invoiceAddressDetails) {
                if (ArConstants.InvoiceTransmissionMethod.EMAIL.equals(invoiceAddressDetail.getInvoiceTransmissionMethodCode())) {
                    Note note = noteService.getNoteByNoteId(invoiceAddressDetail.getNoteId());

                    if (ObjectUtils.isNotNull(note)) {
                        AttachmentMailMessage message = new AttachmentMailMessage();

                        String sender = parameterService.getParameterValueAsString(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE, ArConstants.CONTRACTS_GRANTS_INVOICE_COMPONENT, ArConstants.FROM_EMAIL_ADDRESS);
                        message.setFromAddress(sender);

                        CustomerAddress customerAddress = invoiceAddressDetail.getCustomerAddress();
                        String recipients = invoiceAddressDetail.getCustomerEmailAddress();
                        if (StringUtils.isNotEmpty(recipients)) {
                            message.getToAddresses().add(recipients);
                        } else {
                            LOG.warn("No recipients indicated.");
                        }

                        String subject = getSubject(invoice);
                        message.setSubject(subject);
                        if (StringUtils.isEmpty(subject)) {
                            LOG.warn("Empty subject being sent.");
                        }

                        String bodyText = getMessageBody(invoice, customerAddress);
                        message.setMessage(bodyText);
                        if (StringUtils.isEmpty(bodyText)) {
                            LOG.warn("Empty bodyText being sent.");
                        }

                        Attachment attachment = note.getAttachment();
                        if (ObjectUtils.isNotNull(attachment)) {
                            try {
                                message.setContent(IOUtils.toByteArray(attachment.getAttachmentContents()));
                            } catch (IOException ex) {
                                LOG.error("Error setting attachment contents", ex);
                                throw new RuntimeException(ex);
                            }
                            message.setFileName(attachment.getAttachmentFileName());
                            message.setType(attachment.getAttachmentMimeTypeCode());
                        }

                        setupMailServiceForNonProductionInstance();
                        mailService.sendMessage(message);

                        invoiceAddressDetail.setInitialTransmissionDate(new Date(new java.util.Date().getTime()));
                        documentService.updateDocument(invoice);
                    } else {
                        success = false;
                    }
                }
            }
        }
        return success;
    }

    protected String getSubject(ContractsGrantsInvoiceDocument invoice) {
        String subject = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_EMAIL_SUBJECT);

        return MessageFormat.format(subject, invoice.getInvoiceGeneralDetail().getAward().getProposal().getGrantNumber(),
            invoice.getInvoiceGeneralDetail().getProposalNumber(),
            invoice.getDocumentNumber());
    }

    protected String getMessageBody(ContractsGrantsInvoiceDocument invoice, CustomerAddress customerAddress) {
        String message = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_EMAIL_BODY);

        String department = "";
        String[] orgCode = invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getFundManager().getPrimaryDepartmentCode().split("-");
        Map<String, Object> key = new HashMap<String, Object>();
        key.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, orgCode[0].trim());
        key.put(KFSPropertyConstants.ORGANIZATION_CODE, orgCode[1].trim());
        Organization org = businessObjectService.findByPrimaryKey(Organization.class, key);
        if (ObjectUtils.isNotNull(org)) {
            department = org.getOrganizationName();
        }

        return MessageFormat.format(message, customerAddress.getCustomer().getCustomerName(),
            customerAddress.getCustomerAddressName(),
            invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getFundManager().getName(),
            invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getProjectTitle(),
            department,
            invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getFundManager().getPhoneNumber(),
            invoice.getInvoiceGeneralDetail().getAward().getAwardPrimaryFundManager().getFundManager().getEmailAddress());
    }

    /**
     * Setup properties to handle mail messages in a non-production environment as appropriate.
     * <p>
     * NOTE: We should be setting up configuration properties for these values, and once that is done
     * this method can be removed.
     */
    public void setupMailServiceForNonProductionInstance() {
        if (!ConfigContext.getCurrentContextConfig().isProductionEnvironment()) {
            ((MailServiceImpl) mailService).setRealNotificationsEnabled(false);
            ((MailServiceImpl) mailService).setNonProductionNotificationMailingList(mailService.getBatchMailingList());
        }
    }

    /**
     * Sets the mailService attribute value.
     *
     * @param mailService The mailService to set.
     */
    public void setMailService(AttachmentMailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     *
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Retrieves the Properties used to configure the Mailer. The property names configured in the Workflow configuration should
     * match those of Java Mail.
     *
     * @return
     */
    protected Properties getConfigProperties() {
        return ConfigContext.getCurrentContextConfig().getProperties();
    }

    /**
     * This method sends out emails for upcoming milestones.
     *
     * @see org.kuali.kfs.module.ar.service.CGEmailService#sendEmail(java.util.List, org.kuali.kfs.module.ar.businessobject.Award)
     */
    @Override
    public void sendEmailNotificationsForMilestones(List<Milestone> milestones, ContractsAndGrantsBillingAward award) {
        LOG.debug("sendEmail() starting");

        MailMessage message = new MailMessage();

        message.setFromAddress(mailService.getBatchMailingList());
        message.setSubject(getEmailSubject(ArConstants.REMINDER_EMAIL_SUBJECT));
        message.getToAddresses().add(award.getAwardPrimaryFundManager().getFundManager().getEmailAddress());
        StringBuffer body = new StringBuffer();

        String messageKey = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_1);
        body.append(messageKey + "\n\n");

        for (Milestone milestone : milestones) {
            String proposalNumber = dataDictionaryService.getAttributeLabel(Milestone.class, KFSPropertyConstants.PROPOSAL_NUMBER);
            String milestoneNumber = dataDictionaryService.getAttributeLabel(Milestone.class, ArPropertyConstants.MilestoneFields.MILESTONE_NUMBER);
            String milestoneDescription = dataDictionaryService.getAttributeLabel(Milestone.class, ArPropertyConstants.MilestoneFields.MILESTONE_DESCRIPTION);
            String milestoneAmount = dataDictionaryService.getAttributeLabel(Milestone.class, ArPropertyConstants.MilestoneFields.MILESTONE_AMOUNT);
            String milestoneExpectedCompletionDate = dataDictionaryService.getAttributeLabel(Milestone.class, ArPropertyConstants.MilestoneFields.MILESTONE_EXPECTED_COMPLETION_DATE);

            body.append(proposalNumber + ": " + milestone.getProposalNumber() + " \n");
            body.append(milestoneNumber + ": " + milestone.getMilestoneNumber() + " \n");
            body.append(milestoneDescription + ": " + milestone.getMilestoneDescription() + " \n");
            body.append(milestoneAmount + ": " + milestone.getMilestoneAmount() + " \n");
            body.append(milestoneExpectedCompletionDate + ": " + milestone.getMilestoneExpectedCompletionDate() + " \n");

            body.append("\n\n");
        }
        body.append("\n\n");

        messageKey = kualiConfigurationService.getPropertyValueAsString(ArKeyConstants.MESSAGE_CG_UPCOMING_MILESTONES_EMAIL_LINE_2);
        body.append(MessageFormat.format(messageKey, new Object[]{null}) + "\n\n");

        message.setMessage(body.toString());

        try {
            mailService.sendMessage(message);
        } catch (InvalidAddressException | MessagingException ex) {
            LOG.error("Problems sending milestones e-mail", ex);
            throw new RuntimeException("Problems sending milestones e-mail", ex);
        }
    }

    /**
     * Retrieves the email subject text from system parameter then checks environment code and prepends to message if not
     * production.
     *
     * @param subjectParmaterName name of parameter giving the subject text
     * @return subject text
     */
    protected String getEmailSubject(String subjectParmaterName) {
        String subject = parameterService.getParameterValueAsString(UpcomingMilestoneNotificationStep.class, subjectParmaterName);

        String productionEnvironmentCode = kualiConfigurationService.getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);
        if (!StringUtils.equals(productionEnvironmentCode, environmentCode)) {
            subject = environmentCode + ": " + subject;
        }

        return subject;
    }

    /**
     * Sets the noteService attribute value.
     *
     * @param noteService The noteService to set.
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public ContractsGrantsBillingUtilityService getContractsGrantsBillingUtilityService() {
        return contractsGrantsBillingUtilityService;
    }

    public void setContractsGrantsBillingUtilityService(ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService) {
        this.contractsGrantsBillingUtilityService = contractsGrantsBillingUtilityService;
    }
}
