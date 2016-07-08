package org.kuali.kfs.sys.batch.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.exception.InvalidAddressException;
import org.kuali.kfs.krad.service.MailService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.dataaccess.DetectDocumentsMissingPendingEntriesDao;
import org.kuali.kfs.sys.batch.service.DetectDocumentsMissingPendingEntriesService;
import org.kuali.kfs.sys.businessobject.DocumentHeaderData;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.mail.MailMessage;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class DetectDocumentsMissingPendingEntriesServiceImpl implements DetectDocumentsMissingPendingEntriesService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetectDocumentsMissingPendingEntriesServiceImpl.class);

    protected ParameterService parameterService;
    protected DetectDocumentsMissingPendingEntriesDao detectDocumentsMissingPendingEntriesDao;
    protected MailService mailService;
    protected ConfigurationService configurationService;

    @Override
    public List<DocumentHeaderData> discoverGeneralLedgerDocumentsWithoutPendingEntries(java.util.Date earliestProcessingDate) {
        LOG.debug("Running discoverLedgerDocumentsWithoutPendingEntries");
        return detectDocumentsMissingPendingEntriesDao.discoverLedgerDocumentsWithoutPendingEntries(earliestProcessingDate, getSearchByDocumentTypes());
    }

    protected List<String> getSearchByDocumentTypes() {
        List<String> searchByDocumentTypes = new ArrayList<>();
        final Collection<String> parameterDocumentTypesValues = parameterService.getParameterValuesAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.LEDGER_ENTRY_GENERATING_DOCUMENT_TYPES);
        if (!CollectionUtils.isEmpty(parameterDocumentTypesValues)) {
            searchByDocumentTypes.addAll(parameterDocumentTypesValues);
        }
        return searchByDocumentTypes;
    }

    @Override
    public void reportDocumentsWithoutPendingEntries(List<DocumentHeaderData> documentHeaders) {
        LOG.debug("Running reportDocumentsWithoutPendingEntries");
        final Collection<String> notificationEmailAddresses = parameterService.getParameterValuesAsString(KfsParameterConstants.FINANCIAL_SYSTEM_BATCH.class, KFSParameterKeyConstants.DetectDocumentsMissingPendingEntriesConstants.MISSING_PLES_NOTIFICATION_EMAIL_ADDRESSES);
        if (CollectionUtils.isEmpty(notificationEmailAddresses)) {
            logDocumentHeaders(documentHeaders);
        } else {
            emailDocumentHeaders(documentHeaders, notificationEmailAddresses);
        }
    }

    protected void logDocumentHeaders(List<DocumentHeaderData> documentHeaders) {
        LOG.warn("\n"+buildReportMessage(documentHeaders));
    }

    protected void emailDocumentHeaders(List<DocumentHeaderData> documentHeaders, Collection<String> notificationEmailAddresses) {
        try {
            mailService.sendMessage(buildNotificationMessage(documentHeaders, notificationEmailAddresses));
        }
        catch (InvalidAddressException | MessagingException e) {
            throw new RuntimeException("Could not send e-mail message for Detect Documents Missing PLEs Job", e);
        }
    }

    protected MailMessage buildNotificationMessage(List<DocumentHeaderData> documentHeaders, Collection<String> notificationEmailAddresses) {
        MailMessage message = new MailMessage();
        message.setSubject(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.EMAIL_SUBJECT));
        message.setToAddresses(buildNotificationEmailAddressesSet(notificationEmailAddresses));
        message.setFromAddress(mailService.getBatchMailingList());
        message.setMessage(buildReportMessage(documentHeaders));
        return message;
    }

    protected Set<String> buildNotificationEmailAddressesSet(Collection<String> notificationEmailAddresses) {
        Set<String> emailAddressesSet = new HashSet<>();
        emailAddressesSet.addAll(notificationEmailAddresses);
        return emailAddressesSet;
    }

    protected String buildReportMessage(List<DocumentHeaderData> documentHeaders) {
        StringBuilder reportMessage = new StringBuilder();
        reportMessage.append(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.FAILURE_HEADER));
        reportMessage.append("\n");
        final String documentHeadersReport =
                documentHeaders.stream()
                        .map(documentHeader -> MessageFormat.format(configurationService.getPropertyValueAsString(KFSKeyConstants.DetectMissingPendingEntriesMessages.FAILURE_ENTRY), documentHeader.getDocumentNumber(), documentHeader.getWorkflowDocumentTypeName()))
                        .collect(Collectors.joining("\n"));
        reportMessage.append(documentHeadersReport);
        return reportMessage.toString();
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public DetectDocumentsMissingPendingEntriesDao getDetectDocumentsMissingPendingEntriesDao() {
        return detectDocumentsMissingPendingEntriesDao;
    }

    public void setDetectDocumentsMissingPendingEntriesDao(DetectDocumentsMissingPendingEntriesDao detectDocumentsMissingPendingEntriesDao) {
        this.detectDocumentsMissingPendingEntriesDao = detectDocumentsMissingPendingEntriesDao;
    }

    public MailService getMailService() {
        return mailService;
    }

    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
