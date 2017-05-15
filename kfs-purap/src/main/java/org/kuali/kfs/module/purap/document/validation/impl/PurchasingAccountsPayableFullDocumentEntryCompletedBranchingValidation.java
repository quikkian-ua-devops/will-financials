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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchasingAccountsPayableFullDocumentEntryCompletedBranchingValidation extends BranchingValidation {

    public static final String FULL_DOCUMENT_ENTRY = "fullDocumentEntry";
    public static final String NOT_FULL_DOCUMENT_ENTRY = "notFullDocumentEntry";
    private PurapService purapService;

    @Override
    protected String determineBranch(AttributedDocumentEvent event) {

        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) event.getDocument();

        if (purapService.isFullDocumentEntryCompleted(purapDocument)) {
            return FULL_DOCUMENT_ENTRY;
        } else {
            return NOT_FULL_DOCUMENT_ENTRY;
        }
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }
}
