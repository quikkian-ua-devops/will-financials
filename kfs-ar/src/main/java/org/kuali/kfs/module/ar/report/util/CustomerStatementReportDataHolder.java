/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.ar.report.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * To group and hold the data presented to working reports of extract process
 */
public class CustomerStatementReportDataHolder {
    private Map<String, String> invoice;
    private Map<String, String> customer;
    private Map<String, String> sysinfo;
    private List<CustomerStatementDetailReportDataHolder> details;

    private Map<String, Object> reportData;

    public final static String KEY_OF_INVOICE_ENTRY = "invoice";
    public final static String KEY_OF_CUSTOMER_ENTRY = "customer";
    public final static String KEY_OF_SYSINFO_ENTRY = "sysinfo";
    public final static String KEY_OF_DETAILS_ENTRY = "details";

    /**
     * Constructs a ExtractProcessReportDataHolder.java.
     */
    public CustomerStatementReportDataHolder() {
        //this(null);

        this.invoice = new HashMap<String, String>();
        this.customer = new HashMap<String, String>();
        this.sysinfo = new HashMap<String, String>();
        this.details = new ArrayList<CustomerStatementDetailReportDataHolder>();

        this.reportData = new HashMap<String, Object>();
    }

    /**
     * Constructs a ExtractProcessReportDataHolder.java.
     *
     * @param reportDefinition
     */
//    public CustomerCreditMemoReportDataHolder(CustomerCreditMemoReportDefinition reportDefinition) {
//        super();
//        this.reportDefinition = reportDefinition;
//        this.creditmemo = new HashMap<String, String>();
//        this.invoice = new HashMap<String, String>();
//        this.customer = new HashMap<String, String>();
//        this.sysinfo = new HashMap<String, String>();
//
//        this.reportData = new HashMap<String, Object>();
//    }


    /**
     * Gets the invoice attribute.
     *
     * @return Returns the invoice.
     */
    public Map<String, String> getInvoice() {
        return invoice;
    }

    /**
     * Sets the invoice attribute value.
     *
     * @param invoice The invoice to set.
     */
    public void setInvoice(Map<String, String> invoice) {
        this.invoice = invoice;
    }

    /**
     * Gets the customer attribute.
     *
     * @return Returns the customer.
     */
    public Map<String, String> getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     *
     * @param customer The customer to set.
     */
    public void setCustomer(Map<String, String> customer) {
        this.customer = customer;
    }

    /**
     * Gets the sysinfo attribute.
     *
     * @return Returns the sysinfo.
     */
    public Map<String, String> getSysinfo() {
        return sysinfo;
    }

    /**
     * Sets the sysinfo attribute value.
     *
     * @param sysinfo The sysinfo to set.
     */
    public void setSysinfo(Map<String, String> sysinfo) {
        this.sysinfo = sysinfo;
    }

    /**
     * Gets the reportData attribute.
     *
     * @return Returns the reportData.
     */
    public Map<String, Object> getReportData() {

        reportData.put(KEY_OF_INVOICE_ENTRY, invoice);
        reportData.put(KEY_OF_CUSTOMER_ENTRY, customer);
        reportData.put(KEY_OF_SYSINFO_ENTRY, sysinfo);
        reportData.put(KEY_OF_DETAILS_ENTRY, details);
        //  reportData.put(arg0, arg1);

        return reportData;
    }


    /**
     * Sets the reportData attribute value.
     *
     * @param reportData The reportData to set.
     */
    public void setReportData(Map<String, Object> reportData) {
        this.reportData = reportData;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getReportData().toString();
    }

    /**
     * Gets the details attribute.
     *
     * @return Returns the details.
     */
    public List<CustomerStatementDetailReportDataHolder> getDetails() {
        return details;
    }

    /**
     * Sets the details attribute value.
     *
     * @param details The details to set.
     */
    public void setDetails(List<CustomerStatementDetailReportDataHolder> details) {
        this.details = details;
    }


}
