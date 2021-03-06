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
package org.kuali.kfs.module.ar.web.struts;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.util.WebUtils;
import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerInvoiceWriteoffLookupUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomerInvoiceWriteoffLookupSummaryForm extends KualiForm {

    private String lookupResultsSequenceNumber;
    private Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults;
    private boolean sentToBatch;

    public CustomerInvoiceWriteoffLookupSummaryForm() {
        customerInvoiceWriteoffLookupResults = new ArrayList<CustomerInvoiceWriteoffLookupResult>();
        sentToBatch = false;
    }

    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    public Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceWriteoffLookupResults() {
        return customerInvoiceWriteoffLookupResults;
    }

    public void setCustomerInvoiceWriteoffLookupResults(Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults) {
        this.customerInvoiceWriteoffLookupResults = customerInvoiceWriteoffLookupResults;
    }

    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    public CustomerInvoiceWriteoffLookupResult getCustomerInvoiceWriteoffLookupResult(int index) {
        CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult = ((List<CustomerInvoiceWriteoffLookupResult>) getCustomerInvoiceWriteoffLookupResults()).get(index);
        return customerInvoiceWriteoffLookupResult;
    }

    public boolean isSentToBatch() {
        return sentToBatch;
    }

    public void setSentToBatch(boolean sentToBatch) {
        this.sentToBatch = sentToBatch;
    }

    @Override
    public void populate(HttpServletRequest request) {
        UserSession userSession = GlobalVariables.getUserSession();
        Person person = userSession.getPerson();
        String lookupResultsSequenceNumber = (String) userSession.getObjectMap().get(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
        Map params = request.getParameterMap();
        if (!StringUtils.isEmpty(lookupResultsSequenceNumber)) {
            userSession.removeObject(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
            Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults = CustomerInvoiceWriteoffLookupUtil.getCustomerInvoiceWriteoffResutlsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, person.getPrincipalId());
            this.setCustomerInvoiceWriteoffLookupResults(customerInvoiceWriteoffLookupResults);
            this.setLookupResultsSequenceNumber(lookupResultsSequenceNumber);

        }
        if (!WebUtils.parseMethodToCall(this, request).equals(KFSConstants.MAPPING_CANCEL)) {
            super.populate(request);
        }
    }
}
