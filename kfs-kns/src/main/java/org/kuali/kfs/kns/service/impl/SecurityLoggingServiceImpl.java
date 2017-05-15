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
package org.kuali.kfs.kns.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.document.authorization.FieldRestriction;
import org.kuali.kfs.kns.service.KNSServiceLocator;
import org.kuali.kfs.kns.service.SecurityLoggingService;
import org.kuali.kfs.kns.util.KNSGlobalVariables;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This default implementation of the SecurityLoggingService writes to a log4j appender
 */
public class SecurityLoggingServiceImpl implements SecurityLoggingService {
    private static final Logger SECURITY_LOG = Logger.getLogger("kfsSecurityLog");
    private DataDictionaryService dataDictionaryService;
    
    @Override
    public void logFullUnmask(BusinessObject bo, String propertyName, Document document, boolean accessGranted, String alternateBusinessObjectIdentifier) {
        logFieldAccessRequested(bo, propertyName, "FullUnmask", document, accessGranted, alternateBusinessObjectIdentifier);
    }

    @Override
    public void logPartialUnmask(BusinessObject bo, String propertyName, Document document, boolean accessGranted, String alternateBusinessObjectIdentifier) {
        logFieldAccessRequested(bo, propertyName, "PartialUnmask", document, accessGranted, alternateBusinessObjectIdentifier);
    }

    @Override
    public void logView(BusinessObject bo, String propertyName, Document document, boolean accessGranted, String alternateBusinessObjectIdentifier) {
        logFieldAccessRequested(bo, propertyName, "View", document, accessGranted, alternateBusinessObjectIdentifier);
    }

    @Override
    public void logFieldAccess(BusinessObject bo, String propertyName, Document document, BusinessObjectRestrictions businessObjectRestrictions, boolean logViewAccess, String alternateBusinessObjectIdentifier) {
        boolean shouldLogUnmask = false;
        boolean shouldLogPartialUnmask = false;
        boolean shouldLogView = false;
        boolean unmaskResult = false;
        boolean partialUnmaskResult = false;
        boolean viewResult = false;

        AttributeSecurity attributeSecurity = getDataDictionaryService().getAttributeSecurity(bo.getClass().getName(), propertyName);
        if (attributeSecurity != null) {
            shouldLogUnmask = attributeSecurity.isMask();
            shouldLogPartialUnmask = attributeSecurity.isPartialMask();
            shouldLogView = attributeSecurity.isHide();

            if (businessObjectRestrictions == null) {
                unmaskResult = attributeSecurity.isMask();
                partialUnmaskResult = attributeSecurity.isPartialMask();
                shouldLogView = attributeSecurity.isHide();
            }
            else {
                FieldRestriction fieldRestriction = businessObjectRestrictions.getFieldRestriction(propertyName);
                if (fieldRestriction == null) {
                    unmaskResult = attributeSecurity.isMask();
                    partialUnmaskResult = attributeSecurity.isPartialMask();
                    viewResult = attributeSecurity.isHide();
                }
                else {
                    unmaskResult = attributeSecurity.isMask() && !fieldRestriction.isMasked();
                    partialUnmaskResult = attributeSecurity.isPartialMask() && !fieldRestriction.isPartiallyMasked();
                    viewResult = attributeSecurity.isHide() && !fieldRestriction.isViewable();
                }
            }
        }
        if (shouldLogUnmask) {
            logFullUnmask(bo, propertyName, document, unmaskResult, alternateBusinessObjectIdentifier);
        }
        if (shouldLogPartialUnmask) {
            logPartialUnmask(bo, propertyName, document, partialUnmaskResult, alternateBusinessObjectIdentifier);
        }
        if (logViewAccess && shouldLogView) {
            logView(bo, propertyName, document, viewResult, alternateBusinessObjectIdentifier);
        }
    }

    @Override
    public void logCustomString(String message) {
        StringBuilder buf = new StringBuilder(300);
        appendSecurityLogPrefix(buf);
        buf.append(message);
        writeStringToLog(buf.toString());
    }

    protected void logFieldAccessRequested(BusinessObject bo, String propertyName, String action, Document document, boolean accessGranted, String alternateBusinessObjectIdentifier) {
        Class clazz = ObjectUtils.materializeClassForProxiedObject(bo);
        String className = null;
        if (clazz != null) {
            className = clazz.getSimpleName();
        }

        StringBuilder buf = new StringBuilder(120);
        appendSecurityLogPrefix(buf);
        appendFieldAccessMessage(buf, action, className, propertyName, accessGranted, bo, document, alternateBusinessObjectIdentifier);
        writeStringToLog(buf.toString());
    }

    protected void appendSecurityLogPrefix(StringBuilder buf) {
        buf.append(KNSGlobalVariables.getRemoteIpAddress()).append(",").append(GlobalVariables.getUserSession().getPrincipalName()).append(",");
    }

    protected void appendFieldAccessMessage(StringBuilder buf, String action, String className, String propertyName, boolean accessGranted, BusinessObject bo, Document document, String alternateBusinessObjectIdentifier) {
        buf.append(action).append(",").append(className).append(".").append(propertyName);
        if (accessGranted) {
            buf.append(",SUCCESS");
        }
        else {
            buf.append(",DENY");
        }
        if (ObjectUtils.isNotNull(bo)) {
            if (bo instanceof PersistableBusinessObject) {
                buf.append(",objectId=").append(((PersistableBusinessObject) bo).getObjectId());
            }
            else {
                buf.append(",TransientBusinessObject");
            }
        }
        else {
            buf.append(",null");
        }
        if (StringUtils.isNotBlank(alternateBusinessObjectIdentifier)) {
            buf.append(",").append(alternateBusinessObjectIdentifier);
        }
        if (ObjectUtils.isNull(document)) {
            buf.append(",null");
        }
        else {
            buf.append(",docNbr=").append(document.getDocumentNumber());
        }
    }

    /**
     * Writes the message to the underlying logging mechanism
     * 
     * @param logMessage
     */
    protected void writeStringToLog(String logMessage) {
        SECURITY_LOG.info(logMessage);
    }
    
    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        }
        return dataDictionaryService;
    }
}
