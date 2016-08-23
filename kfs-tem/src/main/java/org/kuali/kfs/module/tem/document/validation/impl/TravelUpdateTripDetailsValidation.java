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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.krad.service.DictionaryValidationService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

import java.util.Date;

import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_DELINQUENT_MSG_REQUIRED;
import static org.kuali.kfs.module.tem.TemKeyConstants.ERROR_TA_AUTH_END_DATE_BEFORE_BEGIN;

/**
 * Validation that is run when a user modifies the trip detail information in a {@link TravelDocument}
 */
public class TravelUpdateTripDetailsValidation extends GenericValidation {
    protected DictionaryValidationService dictionaryValidationService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        TravelDocument doc = (TravelDocument) event.getDocument();
        Date beginDate = doc.getTripBegin();
        Date endDate = doc.getTripEnd();

        boolean valid = true;

        int preCount = GlobalVariables.getMessageMap().getErrorCount();
        MessageMap errors = GlobalVariables.getMessageMap();
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        int postCount = GlobalVariables.getMessageMap().getErrorCount();
        if (postCount > preCount) {
            valid = false;
        }

        if (endDate == null || beginDate == null) {
            getDictionaryValidationService().validateDocument(doc);
        }

        if (endDate != null && beginDate != null && endDate.compareTo(beginDate) < 0) {
            valid = false;
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRIP_BEGIN_DT, ERROR_TA_AUTH_END_DATE_BEFORE_BEGIN);
        }

        if (doc.getDocumentHeader().getWorkflowDocument().getDocumentTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)) {
            TravelReimbursementDocument tr = (TravelReimbursementDocument) doc;
            if (tr.getDelinquentAction() != null) {
                if (tr.getDelinquentAction().equals(TemConstants.DELINQUENT_STOP) && !tr.getDelinquentTRException()) {
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TRIP_END_DT, KFSKeyConstants.ERROR_CUSTOM, "Travel Reimbursement cannot be initiated because of delinquency.");
                }

                if (tr.getDocumentHeader().getExplanation() == null || tr.getDocumentHeader().getExplanation().trim().length() == 0) {
                    GlobalVariables.getMessageMap().putError("document.documentHeader.documentDescription", ERROR_DELINQUENT_MSG_REQUIRED);
                }
            }
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);


        if (doc.getTripType() != null && !doc.getTripType().getUsePerDiem() && doc.getPerDiemExpenses() != null && !doc.getPerDiemExpenses().isEmpty()) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.PER_DIEM_EXPENSES, KFSKeyConstants.ERROR_CUSTOM, "Per Diem entry is not allowed for this Trip Type [" + doc.getTripType().getCode() + "].");
            valid = false;
        }

        if (doc.getPrimaryDestinationName() == null || doc.getPrimaryDestinationName().trim().length() == 0) {
            GlobalVariables.getMessageMap().putError("document.primaryDestinationName", KFSKeyConstants.ERROR_CUSTOM, "Primary Destination is a required field.");
            valid = false;
        }

        return valid;
    }

    public DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    public void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }
}
