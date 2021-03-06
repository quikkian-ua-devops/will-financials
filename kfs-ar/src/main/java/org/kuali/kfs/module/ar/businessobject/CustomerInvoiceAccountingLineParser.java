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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;

import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_CODE;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_DESCRIPTION;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_QUANTITY;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_SERVICE_DATE;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_TAXABLE_INDICATOR;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE;
import static org.kuali.kfs.module.ar.ArPropertyConstants.CustomerInvoiceDocumentFields.UNIT_OF_MEASURE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ACCOUNT_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.ORGANIZATION_REFERENCE_ID;
import static org.kuali.kfs.sys.KFSPropertyConstants.PROJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.SUB_ACCOUNT_NUMBER;

/**
 * This class...
 */
public class CustomerInvoiceAccountingLineParser extends AccountingLineParserBase {

    protected static final String[] CUSTOMER_INVOICE_FORMAT = {CHART_OF_ACCOUNTS_CODE, ACCOUNT_NUMBER, SUB_ACCOUNT_NUMBER, FINANCIAL_OBJECT_CODE, FINANCIAL_SUB_OBJECT_CODE, PROJECT_CODE, ORGANIZATION_REFERENCE_ID, INVOICE_ITEM_CODE, INVOICE_ITEM_QUANTITY, INVOICE_ITEM_DESCRIPTION, INVOICE_ITEM_SERVICE_DATE, UNIT_OF_MEASURE_CODE, INVOICE_ITEM_UNIT_PRICE, INVOICE_ITEM_TAXABLE_INDICATOR};

    /**
     * @see org.kuali.rice.krad.bo.AccountingLineParserBase#getSourceAccountingLineFormat()
     */
    @Override
    public String[] getSourceAccountingLineFormat() {
        return removeChartFromFormatIfNeeded(CUSTOMER_INVOICE_FORMAT);
    }

}
