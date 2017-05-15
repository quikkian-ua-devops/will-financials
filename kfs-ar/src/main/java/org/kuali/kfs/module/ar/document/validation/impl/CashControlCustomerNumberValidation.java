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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

import java.util.HashMap;
import java.util.Map;

public class CashControlCustomerNumberValidation extends GenericValidation {

    private CashControlDocument cashControlDocument;
    private CashControlDetail cashControlDetail;

    @Override
    public boolean validate(AttributedDocumentEvent event) {

        boolean isValid = true;
        String customerNumber = cashControlDetail.getCustomerNumber();

        if (customerNumber != null && !customerNumber.equals("")) {

            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, customerNumber);

            Customer customer = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Customer.class, criteria);

            if (customer == null) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CustomerFields.CUSTOMER_NUMBER, ArKeyConstants.ERROR_CUSTOMER_NUMBER_IS_NOT_VALID);
                isValid = false;
            }
        }
        return isValid;

    }

    public CashControlDocument getCashControlDocument() {
        return cashControlDocument;
    }

    public void setCashControlDocument(CashControlDocument cashControlDocument) {
        this.cashControlDocument = cashControlDocument;
    }

    public CashControlDetail getCashControlDetail() {
        return cashControlDetail;
    }

    public void setCashControlDetail(CashControlDetail cashControlDetail) {
        this.cashControlDetail = cashControlDetail;
    }

}
