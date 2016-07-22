/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.web.form;

import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Form class for incident reports
 * 
 * 
 */
public class IncidentReportForm extends UifFormBase {

    private static final long serialVersionUID = -6677581167041430694L;

    protected String errorMessage = "The system has encountered an error and is unable to complete your request at this time. Please provide more information regarding this error by completing this Incident Report.";

    protected Exception exception;
    protected String exceptionMessage;
    protected String exceptionStackTrace;

    protected String userInput;
    protected String incidentDocId;
    protected String incidentViewId;
    protected String controller;
    protected String userName;
    protected String userId;
    protected String userEmail;

    protected boolean devMode;

    public IncidentReportForm() {
        super();
        this.setRenderFullView(true);
        setViewTypeName(ViewType.INCIDENT);
    }

    /**
     * Creates the email message from the exception, form and user data.
     * 
     * @return the email message
     */
    public String createEmailMessage() {
        String format = "Document Id: %s%n" + "View Id: %s%n" + "Handler: %s%n%n" + "User Email: %s%n"
                + "Person User Identifier: %s%n" + "Person Name: %s%n" + "User Input: %s%n%n" + "errorMessage: %n"
                + "%s";
        String message = String.format(format, (incidentDocId == null) ? "" : incidentDocId, (incidentViewId == null) ? "" : incidentViewId,
                (controller == null) ? "" : controller, (userEmail == null) ? "" : userEmail, (userId == null) ? ""
                        : userId, (userName == null) ? "" : userName, (userInput == null) ? "" : userInput,
                (exceptionStackTrace == null) ? "" : exceptionStackTrace);

        return message;

    }

    /**
     * Creates the email subject containing the mode, view id and the exception message.
     * 
     * @return the email subject
     */
    public String createEmailSubject() {
        String app = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString("application.id");
        String env = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString("environment");
        String format = "%s:%s:%s:%s";
        String subject = String.format(format, app, env, (incidentViewId == null) ? "" : incidentViewId,
                truncateString(exceptionMessage, 180));
        return subject;
    }

    /**
     * Truncate the string to specified length.
     * 
     * @param str
     *            the string to truncate
     * @param maxLength
     *            the max length
     * @return the truncated string
     */
    protected String truncateString(String str, int maxLength) {
        if (str != null && str.length() > maxLength)
            str = str.substring(0, maxLength);
        return str;
    }

    /**
     * Gets the stack trace from an exception.
     * 
     * @param t
     *            the throwable to get the stack trace from
     * @return the stack trace
     */
    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * @param errorMessage
     *            the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the exceptionMessage
     */
    public String getExceptionMessage() {
        return this.exceptionMessage;
    }

    /**
     * @param exceptionMessage
     *            the exceptionMessage to set
     */
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * @return the exceptionStackTrace
     */
    public String getExceptionStackTrace() {
        return this.exceptionStackTrace;
    }

    /**
     * @param exceptionStackTrace
     *            the exceptionStackTrace to set
     */
    public void setExceptionStackTrace(String exceptionStackTrace) {
        this.exceptionStackTrace = exceptionStackTrace;
    }

    /**
     * @return the userInput
     */
    public String getUserInput() {
        return this.userInput;
    }

    /**
     * @param userInput
     *            the userInput to set
     */
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    /**
     * @return the devMode
     */
    public boolean isDevMode() {
        return this.devMode;
    }

    /**
     * @param devMode
     *            the devMode to set
     */
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    /**
     * @param incidentDocId
     *            the incidentDocId to set
     */
    public void setIncidentDocId(String incidentDocId) {
        this.incidentDocId = incidentDocId;
    }

    /**
     * @return the incidentDocId
     */
    public String getIncidentDocId() {
        return incidentDocId;
    }

    /**
     * @param incidentViewId
     *            the incidentViewId to set
     */
    public void setIncidentViewId(String incidentViewId) {
        this.incidentViewId = incidentViewId;
    }

    /**
     * @return the incidentViewId
     */
    public String getIncidentViewId() {
        return incidentViewId;
    }

    /**
     * @param exception
     *            the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
        setExceptionStackTrace(getStackTrace(exception));
        setExceptionMessage(exception.getMessage());
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userEmail
     *            the userEmail to set
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * @return the userEmail
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * @param controller
     *            the controller to set
     */
    public void setController(String controller) {
        this.controller = controller;
    }

    /**
     * @return the controller
     */
    public String getController() {
        return controller;
    }

}
