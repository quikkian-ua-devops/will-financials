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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;

public class RecalculateCustomerInvoiceDetailEvent extends AttributedDocumentEventBase {

    private final CustomerInvoiceDetail customerInvoiceDetail;

    public RecalculateCustomerInvoiceDetailEvent(String errorPathPrefix, Document document, CustomerInvoiceDetail customerInvoiceDetail) {
        super("Recalculating customer invoice detail for document " + getDocumentId(document), errorPathPrefix, document);
        this.customerInvoiceDetail = customerInvoiceDetail;
    }

    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }

    /**
     * Convenience getter, for standard names (which will make this event a bit more reusable)
     *
     * @return returns the encapsulated CustomerInvoiceDetail
     */
    public AccountingLine getAccountingLine() {
        return customerInvoiceDetail;
    }

}
