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
package org.kuali.kfs.kns.service;

import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.document.Document;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * A service that logs access to restricted information
 * 
 */
public interface SecurityLoggingService {

    /**
     * Logs an attempt to fully unmask a secure field
     * 
     * @param bo the business object containing the secure field
     * @param propertyName the property being fully unmasked
     * @param document the document being operated upon, if the user is viewing a document (can be null)
     * @param accessGranted whether the access to fully unmask the field is successful
     * @param alternateBusinessObjectIdentifier an optional ID string to print in the logs to represent the primary key of the BO.
     *                                           By default, the implementation will print the object id of the bo.
     *                                           This is particularly useful if the bo is of type @{@link org.kuali.kfs.krad.bo.TransientBusinessObjectBase} which has no object ID
     */
    public void logFullUnmask(BusinessObject bo, String propertyName, Document document, boolean accessGranted, String alternateBusinessObjectIdentifier);
    
    /**
     * Logs an attempt to partially unmask a secure field
     * 
     * @param bo the business object containing the secure field
     * @param propertyName the property being partially unmasked
     * @param document the document being operated upon, if the user is viewing a document (can be null)
     * @param accessGranted whether the access to partially unmask the field is successful
     * @param alternateBusinessObjectIdentifier an optional ID string to print in the logs to represent the primary key of the BO.
     *                                           By default, the implementation will print the object id of the bo.
     *                                           This is particularly useful if the bo is of type @{@link org.kuali.kfs.krad.bo.TransientBusinessObjectBase} which has no object ID
     */
    public void logPartialUnmask(BusinessObject bo, String propertyName, Document document, boolean accessGranted, String alternateBusinessObjectIdentifier);
    
    /**
     * Logs an attempt to view a secure field
     * 
     * @param bo the business object containing the secure field
     * @param propertyName the property being viewed
     * @param document the document being operated upon, if the user is viewing a document (can be null)
     * @param accessGranted whether the access to view the field is successful
     * @param alternateBusinessObjectIdentifier an optional ID string to print in the logs to represent the primary key of the BO.
     *                                           By default, the implementation will print the object id of the bo.
     *                                           This is particularly useful if the bo is of type @{@link org.kuali.kfs.krad.bo.TransientBusinessObjectBase} which has no object ID
     */
    public void logView(BusinessObject bo, String propertyName, Document document, boolean accessGranted, String alternateBusinessObjectIdentifier);
    
    /**
     * Convenience method to log fully unmask/partial unmask/view access all in one call
     * 
     * @param bo the business object containing the secure field
     * @param propertyName the property being viewed
     * @param document the document being operated upon, if the user is viewing a document (can be null)
     * @param businessObjectRestrictions an object containing the results 
     * @param logViewAccess
     * @param alternateBusinessObjectIdentifier an optional ID string to print in the logs to represent the primary key of the BO.
     *                                           By default, the implementation will print the object id of the bo.
     *                                           This is particularly useful if the bo is of type @{@link org.kuali.kfs.krad.bo.TransientBusinessObjectBase} which has no object ID
     */
    public void logFieldAccess(BusinessObject bo, String propertyName, Document document, BusinessObjectRestrictions businessObjectRestrictions, boolean logViewAccess, String alternateBusinessObjectIdentifier);
    
    /**
     * Logs a custom security event
     * 
     * @param message a custom message
     */
    public void logCustomString(String message);
}
