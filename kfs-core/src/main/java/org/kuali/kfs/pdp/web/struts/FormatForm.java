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
package org.kuali.kfs.pdp.web.struts;

import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.web.struts.form.KualiForm;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberFormatter;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.FormatProcessSummary;
import org.kuali.rice.core.web.format.CurrencyFormatter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Struts Action Form for Format Checks/ACH
 */
public class FormatForm extends KualiForm {

    protected String campus;
    protected String paymentDate;
    protected String paymentTypes;
    protected String initiatorEmail;

    protected FormatProcessSummary formatProcessSummary;

    protected List<CustomerProfile> customers;
    protected List<DisbursementNumberRange> ranges;

    /**
     * Constructs a FormatForm.
     */
    public FormatForm() {
        super();
        customers = new ArrayList<CustomerProfile>();
        ranges = new ArrayList<DisbursementNumberRange>();

        this.setFormatterType("range.lastAssignedDisbNbr", DisbursementNumberFormatter.class);
    }

    /**
     * This method gets campus
     *
     * @return campus
     */
    public String getCampus() {
        return campus;
    }

    /**
     * This method sets campus
     *
     * @param campus
     */
    public void setCampus(String campus) {
        this.campus = campus;
    }

    /**
     * This method gets payment types
     *
     * @return paymentTypes
     */
    public String getPaymentTypes() {
        return paymentTypes;
    }

    /**
     * This method sets paymentTypes
     *
     * @param paymentTypes
     */
    public void setPaymentTypes(String paymentTypes) {
        this.paymentTypes = paymentTypes;
    }

    public String getInitiatorEmail() {
        return initiatorEmail;
    }

    public void setInitiatorEmail(String initiatorEmail) {
        this.initiatorEmail = initiatorEmail;
    }

    /**
     * This method gets customers
     *
     * @return customers
     */
    public List<CustomerProfile> getCustomers() {
        return customers;
    }

    /**
     * This method sets customers
     *
     * @param customers
     */
    public void setCustomers(List<CustomerProfile> customers) {
        this.customers = customers;
    }

    /**
     * This method retrieves a specific customer profile from the list, by index
     *
     * @param index the index of the customers to retrieve the customer profile from
     * @return a CustomerProfile
     */
    public CustomerProfile getCustomer(int index) {
        if (index >= customers.size()) {
            for (int i = customers.size(); i <= index; i++) {
                customers.add(new CustomerProfile());
            }
        }
        return customers.get(index);
    }

    /**
     * This method sets a customer profile.
     *
     * @param key   the index of the value
     * @param value the new value
     */
    public void setCustomer(int key, CustomerProfile value) {
        customers.set(key, value);
    }


    /**
     * This method gets the ranges.
     *
     * @return ranges list
     */
    public List<DisbursementNumberRange> getRanges() {
        return ranges;
    }

    /**
     * This method sets ranges list.
     *
     * @param ranges
     */
    public void setRanges(List<DisbursementNumberRange> ranges) {
        this.ranges = ranges;
    }

    /**
     * This method retrieves a specific disbursement number range from the list, by index
     *
     * @param index the index of the ranges to retrieve the disbursement number range from
     * @return a DisbursementNumberRange
     */
    public DisbursementNumberRange getRange(int index) {
        if (index >= ranges.size()) {
            for (int i = ranges.size(); i <= index; i++) {
                ranges.add(new DisbursementNumberRange());
            }
        }
        return ranges.get(index);
    }

    /**
     * This method gets the currency formated value of the total amount.
     *
     * @return the currency formated value of the total amount
     */
    public String getCurrencyFormattedTotalAmount() {
        return (String) new CurrencyFormatter().format(formatProcessSummary.getTotalAmount());
    }

    /**
     * This method gets the payment date.
     *
     * @return paymentDate
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * This method sets the payment date.
     *
     * @param paymentDate
     */
    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * This method gets the format process summary.
     *
     * @return formatProcessSummary
     */
    public FormatProcessSummary getFormatProcessSummary() {
        return formatProcessSummary;
    }

    /**
     * This method sets the format process summary.
     *
     * @param formatProcessSummary
     */
    public void setFormatProcessSummary(FormatProcessSummary formatProcessSummary) {
        this.formatProcessSummary = formatProcessSummary;
    }

    /**
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);

        for (CustomerProfile customer : customers) {
            customer.setSelectedForFormat(false);
        }
    }
}
