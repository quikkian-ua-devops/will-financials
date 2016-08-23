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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.util.ObjectUtils;

/**
 * A validation which uses parameters to determine if a value on an accounting line is valid.
 */
public class PurchasingAccountsPayableObjectCodeOverrideBranchingValidation extends BranchingValidation {
    protected String propertyPath;
    protected String parameterToCheckAgainst;
    protected ParameterService parameterService;
    protected String responsibleProperty;
    protected AccountingDocument accountingDocumentForValidation;
    protected AccountingLine accountingLineForValidation;

    protected final static String OBJECT_CODE_OVERRIDEN = "ObjectCodeOverriden";
    protected final static String OBJECT_CODE_NOT_OVERRIDEN = "ObjectCodeNotOverriden";

    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (!StringUtils.isBlank(propertyPath)) {
            refreshByPath(accountingLineForValidation);
        }

        boolean isTaxApproval = false;
        //if payment request, skip object code check when this is a tax approval,
        // or if this accounting line is from a Tax Charge line.
        if (accountingDocumentForValidation instanceof PaymentRequestDocument) {
            PaymentRequestDocument preq = (PaymentRequestDocument)accountingDocumentForValidation;
            PurApAccountingLine purapAccountingLine = (PurApAccountingLine)accountingLineForValidation;
            PurApItem item = purapAccountingLine.getPurapItem();

            if (StringUtils.equals(PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW, preq.getApplicationDocumentStatus())){
                isTaxApproval = true;
            }else if(StringUtils.equals(PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED, preq.getApplicationDocumentStatus()) &&
                     (ObjectUtils.isNotNull(item) && item.getItemType().getIsTaxCharge()) ){
                isTaxApproval = true;
            }
        }

        if (isTaxApproval) {
            return null;
        } else if (isAccountingLineValueAllowed(accountingDocumentForValidation.getClass(), accountingLineForValidation, parameterToCheckAgainst, propertyPath, (responsibleProperty != null ? responsibleProperty : propertyPath))){
            return OBJECT_CODE_OVERRIDEN;
        } else {
            return OBJECT_CODE_NOT_OVERRIDEN;
        }
    }

    /**
     * Checks that a value on an accounting line is valid, based on parameters, for a document of the given class
     * @param documentClass the class of the document to check
     * @param accountingLine the accounting line to check
     * @param parameterName the name of the parameter to check
     * @param propertyName the name of the property to check
     * @param userEnteredPropertyName the value the user entered on the line
     * @return true if this passes validation, false otherwise
     */
    protected boolean isAccountingLineValueAllowed(Class documentClass, AccountingLine accountingLine, String parameterName, String propertyName, String userEnteredPropertyName) {
        boolean isAllowed = false;
        String exceptionMessage = "Invalid property name provided to PurchasingAccountsPayableObjectCodeOverrideBranchingValidation isAccountingLineValueAllowed method: " + propertyName;
        try {
            String propertyValue = (String) PropertyUtils.getProperty(accountingLine, propertyName);
            if (getParameterService().parameterExists(documentClass, parameterName)) {
                isAllowed = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(documentClass, parameterName, propertyValue).evaluationSucceeds();
            }
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(exceptionMessage, e);
        }
        return isAllowed;
    }

    /**
     * Refreshes a value on the accounting line, using the propertyPath to decided what to refresh
     * @param line the accounting line to refresh a property on
     */
    public void refreshByPath(AccountingLine line) {
        refreshByQueue(line, convertPathToQueue(propertyPath));
    }

    /**
     * Creates a Queue which represents a FIFO path of what properties to visit, based on the given property path
     * @param path the path to convert to a Queue
     * @return a Queue representing the path
     */
    protected Queue<String> convertPathToQueue(String path) {
        Queue<String> pathQueue = new LinkedList<String>();
        for (String property: path.split("\\.")) {
            pathQueue.add(property);
        }
        return pathQueue;
    }

    /**
     * Recursively refreshes a property given by the queue path
     * @param bo the business object to refresh
     * @param path the path, in Queue form, of properties to refresh
     */
    protected void refreshByQueue(PersistableBusinessObject bo, Queue<String> path) {
        if (path.size() > 1) { // we know that the last thing on our list is a code. why refresh that?
            String currentProperty = path.remove();
            bo.refreshReferenceObject(currentProperty);
            PersistableBusinessObject childBO = (PersistableBusinessObject)ObjectUtils.getPropertyValue(bo, currentProperty);
            if (!ObjectUtils.isNull(childBO)) {
                refreshByQueue(childBO, path);
            }
        }
    }

    /**
     * Gets the propertyPath attribute. This is the path to the value to check, e. g. "accountNumber.subFundGroup.fundGroupCode"
     * @return Returns the propertyPath.
     */
    public String getPropertyPath() {
        return propertyPath;
    }

    /**
     * Sets the propertyPath attribute value. This is the path to the value to check, e. g. "accountNumber.subFundGroup.fundGroupCode"
     * @param propertyPath The propertyPath to set.
     */
    public void setPropertyPath(String refreshPath) {
        this.propertyPath = refreshPath;
    }

    /**
     * Gets the parameterService attribute.
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the parameterToCheckAgainst attribute. This is the name of the parameter which has the values to validate against.
     * @return Returns the parameterToCheckAgainst.
     */
    public String getParameterToCheckAgainst() {
        return parameterToCheckAgainst;
    }

    /**
     * Sets the parameterToCheckAgainst attribute value.  This is the name of the parameter which has the values to validate against.
     * @param parameterToCheckAgainst The parameterToCheckAgainst to set.
     */
    public void setParameterToCheckAgainst(String parameterToCheckAgainst) {
        this.parameterToCheckAgainst = parameterToCheckAgainst;
    }

    /**
     * Gets the responsibleProperty attribute. This is the property on the accounting line to show the error on.
     * @return Returns the responsibleProperty.
     */
    public String getResponsibleProperty() {
        return responsibleProperty;
    }

    /**
     * Sets the responsibleProperty attribute value. This is the property on the accounting line to show the error on.
     * @param responsibleProperty The responsibleProperty to set.
     */
    public void setResponsibleProperty(String responsibleProperty) {
        this.responsibleProperty = responsibleProperty;
    }

    /**
     * Gets the accountingDocumentForValidation attribute.
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingLineForValidation attribute.
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

}
