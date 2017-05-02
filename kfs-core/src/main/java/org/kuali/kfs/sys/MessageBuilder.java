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
package org.kuali.kfs.sys;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.BusinessObject;

import java.text.MessageFormat;
import java.util.List;

/**
 * This class provides a set of utilities that can be used to build error message
 */
public class MessageBuilder {
    private static ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
    private static DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
    private static DataDictionary dataDictionary = dataDictionaryService.getDataDictionary();

    /**
     * add the given message into the given message list
     *
     * @param messageList the given message list
     * @param message     the given message
     */
    public static void addMessageIntoList(List<Message> messageList, Message message) {
        if (message != null) {
            messageList.add(message);
        }
    }

    /**
     * Build the error message with the message body and error type
     */
    public static Message buildMessage(String errorMessageKey, int errorType) {
        return MessageBuilder.buildMessage(errorMessageKey, null, errorType);
    }

    /**
     * Build the message for a fatal error with the message body and invalid value
     */
    public static Message buildMessage(String errorMessageKey, String invalidValue) {
        return buildMessage(errorMessageKey, invalidValue, Message.TYPE_FATAL);
    }

    /**
     * Build the error message with the message body, invalid value and error type
     */
    public static Message buildMessage(String errorMessageKey, String invalidValue, int errorType) {
        String errorMessageBody = getPropertyValueAsString(errorMessageKey);
        String errorMessage = formatMessageBody(errorMessageBody, invalidValue);
        return new Message(errorMessage, errorType);
    }

    /**
     * Format the error message body based on the given error message and invalid value
     */
    public static String formatMessageBody(String errorMessageBody, String invalidValue) {
        String value = StringUtils.isBlank(invalidValue) ? "" : "[" + invalidValue + "]";
        return errorMessageBody + value;
    }

    /**
     * Build the error message with the message body, invalid value and error type. The message body contains place holders.
     */
    public static Message buildMessageWithPlaceHolder(String errorMessageKey, int errorType, Object... invalidValues) {
        String errorMessageBody = getPropertyValueAsString(errorMessageKey);
        String errorMessage = MessageFormat.format(errorMessageBody, invalidValues);
        return new Message(errorMessage, errorType);
    }

    /**
     * Build the message for a fatal error with the message body and invalid value. The message body contains place holders.
     */
    public static Message buildMessageWithPlaceHolder(String errorMessageKey, Object... invalidValues) {
        return buildMessageWithPlaceHolder(errorMessageKey, Message.TYPE_FATAL, invalidValues);
    }

    /**
     * get the message from application resource properties with the given key
     *
     * @param messageKey the given message key
     * @return the message from application resource properties with the given key
     */
    public static String getPropertyValueAsString(String messageKey) {
        return kualiConfigurationService.getPropertyValueAsString(messageKey);
    }

    /**
     * build the error message with the given label and current value
     *
     * @param label        the given label
     * @param currentValue the given current value
     * @return the error message built from the given label and current value
     */
    public static String buildErrorMessageWithDataDictionary(Class<? extends BusinessObject> businessObjectClass, String attributeName, String currentValue) {
        String label = getShortLabel(businessObjectClass, attributeName);

        return label + ":" + currentValue;
    }

    /**
     * get the label of the specified attribute of the given business object
     *
     * @param businessObjectClass the given business object
     * @param attributeName       the specified attribute name
     * @return the label of the specified attribute of the given business object
     */
    public static String getShortLabel(Class<? extends BusinessObject> businessObjectClass, String attributeName) {
        return dataDictionary.getBusinessObjectEntry(businessObjectClass.getName()).getAttributeDefinition(attributeName).getShortLabel();
    }
}
