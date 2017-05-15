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
package org.kuali.kfs.module.purap.document.authorization;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRetransmitDocument;

import java.util.Set;


public class PurchaseOrderRetransmitDocumentPresentationController extends PurchaseOrderDocumentPresentationController {

    @Override
    public boolean canSave(Document document) {
        return false;
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        PurchaseOrderRetransmitDocument poDocument = (PurchaseOrderRetransmitDocument) document;
        if (poDocument.isShouldDisplayRetransmitTab()) {
            editModes.add(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB);
        }
        return editModes;
    }

    @Override
    protected boolean canPreviewPrintPo(PurchaseOrderDocument poDocument) {

        return false;
    }
}
