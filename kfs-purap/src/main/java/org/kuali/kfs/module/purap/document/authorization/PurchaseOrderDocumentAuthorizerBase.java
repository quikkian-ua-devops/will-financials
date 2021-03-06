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
package org.kuali.kfs.module.purap.document.authorization;

import org.kuali.kfs.krad.document.Document;
import org.kuali.rice.kim.api.identity.Person;

public class PurchaseOrderDocumentAuthorizerBase extends PurchasingAccountsPayableTransactionalDocumentAuthorizerBase {

    @Override
    public boolean canEditDocumentOverview(Document document, Person user) {
        // According to the requirement in KFSMI-8056, for PurchaseOrderDocument, the way it should work is :
        // "any person who has edit ability should be able to edit the entire Document Overview tab".
        // So I'm going to return the same value that is returned by canEdit method of the superclass.
        return canEdit(document, user);
    }

}
