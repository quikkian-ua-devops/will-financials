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
package org.kuali.kfs.module.ar.document.authorization;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

import java.util.Collection;
import java.util.Map;

public class CustomerInvoiceDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        CustomerInvoiceDocument document = (CustomerInvoiceDocument) businessObject;
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection<CustomerInvoiceDetail> invoiceDetails = invoiceDocService.getCustomerInvoiceDetailsForCustomerInvoiceDocumentWithCaching(document);

        for (CustomerInvoiceDetail invoiceDetail : invoiceDetails) {
            if (StringUtils.isNotBlank(invoiceDetail.getChartOfAccountsCode()) && StringUtils.isNotBlank(invoiceDetail.getAccountNumber())) {
                attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, invoiceDetail.getChartOfAccountsCode());
                attributes.put(KfsKimAttributes.ACCOUNT_NUMBER, invoiceDetail.getAccountNumber());
            }
        }

        // add profile principal id - hopefully someday, TEM can simply do this for us if it is turned on
        final String currentUserPrincipalId = GlobalVariables.getUserSession().getPrincipalId();
        attributes.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, currentUserPrincipalId);
    }

}
