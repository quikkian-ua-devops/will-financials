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
package org.kuali.kfs.gl.batch.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.batch.EnterpriseFeedStep;
import org.kuali.kfs.gl.batch.service.EnterpriseFeederNotificationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.mail.BodyMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The base implementation of EnterpriseFeederNotificationService; performs email-based notifications
 */
public class EnterpriseFeederNotificationServiceImpl implements EnterpriseFeederNotificationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EnterpriseFeederNotificationServiceImpl.class);

    private ParameterService parameterService;
    private ConfigurationService configurationService;
    private EmailService emailService;

    /**
     * Performs notification about the status of the upload (i.e. feeding) of a single file set (i.e. done file, data file, and
     * recon file).
     *
     * @param feederProcessName The name of the feeder process; this may correspond to the name of the Spring definition of the
     *                          feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *                          value.
     * @param doneFile          The done file
     * @param dataFile          The data file
     * @param reconFile         The recon file
     * @param errorMessages     Any error messages for which to provide notification
     */
    @Override
    public void notifyFileFeedStatus(String feederProcessName, EnterpriseFeederStatus status, File doneFile, File dataFile, File reconFile, List<Message> errorMessages) {
        LOG.debug("notifyFileFeedStatus() started");

        String doneFileDescription = doneFile == null ? "Done file missing" : doneFile.getAbsolutePath();
        String dataFileDescription = dataFile == null ? "Data file missing" : dataFile.getAbsolutePath();
        String reconFileDescription = reconFile == null ? "Recon file missing" : reconFile.getAbsolutePath();

        // this implementation does not use the contents of the file, so we don't bother opening up the files
        notifyFileFeedStatus(feederProcessName, status, doneFileDescription, null, dataFileDescription, null, reconFileDescription, null, errorMessages);
    }

    /**
     * Performs notifications
     *
     * @param feederProcessName    The name of the feeder process; this may correspond to the name of the Spring definition of the
     *                             feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *                             value.
     * @param status               The event/status of the upload of the file set
     * @param doneFileDescription  The file name
     * @param doneFileContents     Not used; can be set to null
     * @param dataFileDescription  The file name
     * @param dataFileContents     Not used; can be set to null
     * @param reconFileDescription The file name
     * @param reconFileContents    Not used; can be set to null
     * @param errorMessages        Any error messages for which to provide notification
     */
    @Override
    public void notifyFileFeedStatus(String feederProcessName, EnterpriseFeederStatus status, String doneFileDescription, InputStream doneFileContents, String dataFileDescription, InputStream dataFileContents, String reconFileDescription, InputStream reconFileContents, List<Message> errorMessages) {
        LOG.debug("notifyFileFeedStatus() started");

        try {
            if (isStatusNotifiable(feederProcessName, status, doneFileDescription, dataFileDescription, reconFileDescription, errorMessages)) {
                Set<String> toEmailAddresses = generateToEmailAddresses(feederProcessName, status, doneFileDescription, dataFileDescription, reconFileDescription, errorMessages);

                BodyMailMessage mailMessage = new BodyMailMessage();
                String returnAddress = parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.FROM_EMAIL_ADDRESS_PARM_NM);
                if (StringUtils.isEmpty(returnAddress)) {
                    returnAddress = emailService.getDefaultFromAddress();
                }
                mailMessage.setFromAddress(returnAddress);
                mailMessage.setToAddresses(toEmailAddresses);
                mailMessage.setSubject(getSubjectLine(doneFileDescription, dataFileDescription, reconFileDescription, errorMessages, feederProcessName, status));
                mailMessage.setMessage(buildFileFeedStatusMessage(doneFileDescription, dataFileDescription, reconFileDescription, errorMessages, feederProcessName, status));

                emailService.sendMessage(mailMessage,false);
            }
        } catch (Exception e) {
            // Have to try to prevent notification exceptions from breaking control flow in the caller
            // log and swallow the exception
            LOG.error("Error occured trying to send notifications.", e);
        }
    }

    /**
     * Generates the status message that would be generated by a call to notifyFileFeedStatus with the same parameters.
     *
     * @param feederProcessName The name of the feeder process; this may correspond to the name of the Spring definition of the
     *                          feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *                          value.
     * @param doneFile          The done file
     * @param dataFile          The data file
     * @param reconFile         The recon file
     * @param errorMessages     Any error messages for which to provide notification
     */
    @Override
    public String getFileFeedStatusMessage(String feederProcessName, EnterpriseFeederStatus status, File doneFile, File dataFile, File reconFile, List<Message> errorMessages) {
        LOG.debug("getFileFeedStatusMessage() started");

        String doneFileDescription = doneFile.getAbsolutePath();
        String dataFileDescription = dataFile.getAbsolutePath();
        String reconFileDescription = reconFile.getAbsolutePath();

        return buildFileFeedStatusMessage(doneFileDescription, dataFileDescription, reconFileDescription, errorMessages, feederProcessName, status);
    }

    @Override
    public String getFileFeedStatusMessage(String feederProcessName, EnterpriseFeederStatus status, String doneFileDescription, InputStream doneFileContents, String dataFileDescription, InputStream dataFileContents, String reconFileDescription, InputStream reconFileContents, List<Message> errorMessages) {
        LOG.debug("getFileFeedStatusMessage() started");

        return buildFileFeedStatusMessage(doneFileDescription, dataFileDescription, reconFileDescription, errorMessages, feederProcessName, status);
    }

    /**
     * Generates the destination address(s) for the email notifications, possibly depending on the parameter values
     *
     * @param feederProcessName    The name of the feeder process; this may correspond to the name of the Spring definition of the
     *                             feeder step, but each implementation may define how to use the value of this parameter and/or restrictions on its
     *                             value.
     * @param status               The event/status of the upload of the file set
     * @param doneFileDescription  The file name
     * @param dataFileDescription  The file name
     * @param reconFileDescription The file name
     * @param errorMessages        Any error messages for which to provide notification
     * @return the destination addresses
     */
    protected Set<String> generateToEmailAddresses(String feederProcessName, EnterpriseFeederStatus status, String doneFileDescription, String dataFileDescription, String reconFileDescription, List<Message> errorMessages) {
        Set<String> addresses = new HashSet<>();
        Collection<String> addressesArray = parameterService.getParameterValuesAsString(EnterpriseFeedStep.class, KFSConstants.EnterpriseFeederApplicationParameterKeys.TO_ADDRESS);
        for (String address : addressesArray) {
            addresses.add(address);
        }
        return addresses;
    }

    /**
     * Builds the status message for the status of a feed.
     *
     * @param doneFileDescription  the name of the done file
     * @param dataFileDescription  the name of the file to read data from
     * @param reconFileDescription the name of the reconciliation file
     * @param errorMessages        a List of error messages
     * @param feederProcessName    the name of the feeder process
     * @return the String of the subject line
     */
    protected String getSubjectLine(String doneFileDescription, String dataFileDescription, String reconFileDescription, List<Message> errorMessages, String feederProcessName, EnterpriseFeederStatus status) {
        String subject = configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_ENTERPRISE_FEEDER_RECONCILIATION_OR_LOADING_ERROR);
        if (subject == null) {
            return "ERROR in reconciling or loading GL origin entries from file.";
        }
        return subject;
    }

    /**
     * Builds the status message for the status of a feed.
     *
     * @param doneFileName      the name of the done file
     * @param dataFileName      the name of the file to get data from
     * @param reconFileName     the reconciliation file
     * @param errorMessages     a List of error messages generated during the process
     * @param feederProcessName the name of the feeder process
     * @return a String with the status message
     */
    protected String buildFileFeedStatusMessage(String doneFileName, String dataFileName, String reconFileName, List<Message> errorMessages, String feederProcessName, EnterpriseFeederStatus status) {
        StringBuilder buf = new StringBuilder();

        buf.append("Data file: ").append(dataFileName).append("\n");
        buf.append("Reconciliation File: ").append(reconFileName).append("\n");
        buf.append("Done file: ").append(doneFileName).append("\n\n\n");

        buf.append("Status: ").append(status.getStatusDescription()).append("\n\n\n");

        if (status.isErrorEvent()) {
            buf.append("The done file has been removed and ");
            if (StringUtils.isNotBlank(feederProcessName)) {
                buf.append(feederProcessName);
            } else {
                buf.append("<process name unavailable>");
            }
            buf.append(" will continue without processing this set of files (see below).");

            buf.append("  Please correct and resend the files for the next day's batch.");
        }

        buf.append("\n\n");

        if (!errorMessages.isEmpty()) {
            buf.append("Error/warning messages:\n");
            for (Message message : errorMessages) {
                if (message.getType() == Message.TYPE_FATAL) {
                    buf.append("ERROR: ");
                }
                if (message.getType() == Message.TYPE_WARNING) {
                    buf.append("WARNING: ");
                }
                buf.append(message.getMessage()).append("\n");
            }
        }

        return buf.toString();
    }

    /**
     * Returns whether a notification is necessary given the values of the parameters
     *
     * @param feederProcessName    the name of the process that invoked the feeder
     * @param status               the status of the feed
     * @param doneFileDescription  the done file description
     * @param dataFileDescription  the data file description
     * @param reconFileDescription the recon file description
     * @param errorMessages        a list of error messages
     * @return whether to notify
     */
    protected boolean isStatusNotifiable(String feederProcessName, EnterpriseFeederStatus status, String doneFileDescription, String dataFileDescription, String reconFileDescription, List<Message> errorMessages) {
        if (status instanceof FileReconOkLoadOkStatus) {
            return false;
        }
        return true;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
