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

import org.kuali.kfs.kns.rules.DocumentRuleBase;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurapEnterableItem;
import org.kuali.kfs.module.purap.businessobject.ReceivingItem;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.List;

public class CorrectionReceivingDocumentRule extends DocumentRuleBase {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean valid = true;
        CorrectionReceivingDocument correctionReceivingDocument = (CorrectionReceivingDocument) document;

        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        valid &= super.processCustomRouteDocumentBusinessRules(document);
        valid &= canCreateCorrectionReceivingDocument(correctionReceivingDocument);
        valid &= isAtLeastOneItemEntered(correctionReceivingDocument);

        return valid;
    }


    protected boolean isAtLeastOneItemEntered(ReceivingDocument receivingDocument) {
        for (ReceivingItem item : (List<ReceivingItem>) receivingDocument.getItems()) {
            if (((PurapEnterableItem) item).isConsideredEntered()) {
                //if any item is entered return true
                return true;
            }
        }
        //if no items are entered return false
        GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_RECEIVING_LINEITEM_REQUIRED);
        return false;

    }

    /**
     * Determines if it is valid to create a receiving correction document.  Only one
     * receiving correction document can be active at any time per receiving line document.
     *
     * @param receivingCorrectionDocument
     * @return
     */
    protected boolean canCreateCorrectionReceivingDocument(CorrectionReceivingDocument correctionReceivingDocument) {

        boolean valid = true;

        if (SpringContext.getBean(ReceivingService.class).canCreateCorrectionReceivingDocument(correctionReceivingDocument.getLineItemReceivingDocument(), correctionReceivingDocument.getDocumentNumber()) == false) {
            valid &= false;
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.LINE_ITEM_RECEIVING_DOCUMENT_NUMBER, PurapKeyConstants.ERROR_RECEIVING_CORRECTION_DOCUMENT_ACTIVE_FOR_RCV_LINE, correctionReceivingDocument.getDocumentNumber(), correctionReceivingDocument.getLineItemReceivingDocumentNumber());
        }

        return valid;
    }

}
