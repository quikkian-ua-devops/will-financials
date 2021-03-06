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

import org.kuali.kfs.kns.rules.PromptBeforeValidationBase;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.HashMap;
import java.util.Iterator;

public class BulkReceivingDocumentPreRules extends PromptBeforeValidationBase {

    public boolean doPrompts(Document document) {

        BulkReceivingDocument bulkReceivingDocument = (BulkReceivingDocument) document;

        HashMap<String, String> duplicateMessages = SpringContext.getBean(BulkReceivingService.class).bulkReceivingDuplicateMessages(bulkReceivingDocument);

        if (duplicateMessages != null && !duplicateMessages.isEmpty()) {
            Iterator iterator = duplicateMessages.values().iterator();
            StringBuffer msg = new StringBuffer();
            while (iterator.hasNext()) {
                msg.append(iterator.next());
            }
            boolean proceed = super.askOrAnalyzeYesNoQuestion(PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION, msg.toString());
            if (!proceed) {
                abortRulesCheck();
            }
        }

        return true;
    }


}
