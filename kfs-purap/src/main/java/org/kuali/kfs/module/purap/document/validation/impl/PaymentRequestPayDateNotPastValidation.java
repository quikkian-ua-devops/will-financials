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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.PersistenceService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.api.WorkflowDocument;

import java.util.Map;

public class PaymentRequestPayDateNotPastValidation extends GenericValidation {

    private PurapService purapService;
    private PersistenceService persistenceService;
    private BusinessObjectService businessObjectService;

    /**
     * Validates that the payment request date does not occur in the past.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PaymentRequestDocument document = (PaymentRequestDocument) event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        java.sql.Date paymentRequestPayDate = document.getPaymentRequestPayDate();
        if (ObjectUtils.isNotNull(paymentRequestPayDate) && purapService.isDateInPast(paymentRequestPayDate)) {
            // the pay date is in the past, now we need to check whether given the state of the document to determine whether a past pay date is allowed
            WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
            if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
                // past pay dates are not allowed if the document has never been routed (i.e. in saved or initiated state)
                // (note that this block will be run when a document is being routed, or re-saved after being routed
                valid &= false;
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, PurapKeyConstants.ERROR_INVALID_PAY_DATE);
            } else {
                // otherwise, this document has already been routed
                // it's an error if the pay date has been changed from the pay date in the database and the new pay date is in the past
                // retrieve doc from DB, and compare the dates
                PaymentRequestDocument paymentRequestDocumentFromDatabase = retrievePaymentRequestDocumentFromDatabase(document);

                if (ObjectUtils.isNull(paymentRequestDocumentFromDatabase)) {
                    // this definitely should not happen
                    throw new NullPointerException("Unable to find payment request document " + document.getDocumentNumber() + " from database");
                }

                java.sql.Date paymentRequestPayDateFromDatabase = paymentRequestDocumentFromDatabase.getPaymentRequestPayDate();
                if (ObjectUtils.isNull(paymentRequestPayDateFromDatabase) || !paymentRequestPayDateFromDatabase.equals(paymentRequestPayDate)) {
                    valid &= false;
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, PurapKeyConstants.ERROR_INVALID_PAY_DATE);
                }
            }
        }

        GlobalVariables.getMessageMap().clearErrorPath();

        return valid;
    }

    /**
     * Retrieves the payment request document from the database.  Note that the instance returned
     *
     * @param document the document to look in the database for
     * @return an instance representing what's stored in the database for this instance
     */
    protected PaymentRequestDocument retrievePaymentRequestDocumentFromDatabase(PaymentRequestDocument document) {
        Map primaryKeyValues = persistenceService.getPrimaryKeyFieldValues(document);
        return (PaymentRequestDocument) businessObjectService.findByPrimaryKey(document.getClass(), primaryKeyValues);
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
