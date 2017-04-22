/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.kfs.kns.service;

import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.document.Document;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * A service that logs access to restricted information
 * 
 * @author wliang
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
