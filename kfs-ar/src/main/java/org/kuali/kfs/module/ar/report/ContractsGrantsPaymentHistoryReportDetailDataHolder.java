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

package org.kuali.kfs.module.ar.report;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Defines a data holder for the Payment History Report.
 */
public class ContractsGrantsPaymentHistoryReportDetailDataHolder {

    private String paymentNumber;
    private Date paymentDate;
    private String customerNumber;
    private String customerName;
    private BigDecimal paymentAmount;
    private String invoiceNumber;
    private BigDecimal invoiceAmount;
    private String awardNumber;
    private String reversedIndicator;
    private String appliedIndicator;
    private String sortedFieldValue;
    private BigDecimal invoiceSubTotal;
    private BigDecimal paymentSubTotal;
    public boolean displaySubtotal;

    /**
     * Gets the paymentNumber attribute.
     *
     * @return Returns the paymentNumber
     */
    public String getPaymentNumber() {
        return paymentNumber;
    }

    /**
     * Sets the paymentNumber attribute value.
     *
     * @param paymentNumber The paymentNumber to set.
     */
    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    /**
     * Gets the paymentDate attribute.
     *
     * @return Returns the paymentDate
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the paymentDate attribute value.
     *
     * @param paymentDate The paymentDate to set.
     */
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     *
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the customerName attribute.
     *
     * @return Returns the customerName
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute value.
     *
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the paymentAmount attribute.
     *
     * @return Returns the paymentAmount
     */
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the paymentAmount attribute value.
     *
     * @param paymentAmount The paymentAmount to set.
     */
    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * Gets the invoiceNumber attribute.
     *
     * @return Returns the invoiceNumber
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute value.
     *
     * @param invoiceNumber The invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the invoiceAmount attribute.
     *
     * @return Returns the invoiceAmount
     */
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute value.
     *
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the awardNumber attribute.
     *
     * @return Returns the awardNumber
     */
    public String getAwardNumber() {
        return awardNumber;
    }

    /**
     * Sets the awardNumber attribute value.
     *
     * @param awardNumber The awardNumber to set.
     */
    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }

    /**
     * Gets the reversedIndicator attribute.
     *
     * @return Returns the reversedIndicator
     */
    public String getReversedIndicator() {
        return reversedIndicator;
    }

    /**
     * Sets the reversedIndicator attribute value.
     *
     * @param reversedIndicator The reversedIndicator to set.
     */
    public void setReversedIndicator(String reversedIndicator) {
        this.reversedIndicator = reversedIndicator;
    }

    /**
     * Gets the appliedIndicator attribute.
     *
     * @return Returns the appliedIndicator
     */
    public String getAppliedIndicator() {
        return appliedIndicator;
    }

    /**
     * Sets the appliedIndicator attribute value.
     *
     * @param appliedIndicator The appliedIndicator to set.
     */
    public void setAppliedIndicator(String appliedIndicator) {
        this.appliedIndicator = appliedIndicator;
    }

    /**
     * Gets the sortedFieldValue attribute.
     *
     * @return Returns the sortedFieldValue
     */
    public String getSortedFieldValue() {
        return sortedFieldValue;
    }

    /**
     * Sets the sortedFieldValue attribute value.
     *
     * @param sortedFieldValue The sortedFieldValue to set.
     */
    public void setSortedFieldValue(String sortedFieldValue) {
        this.sortedFieldValue = sortedFieldValue;
    }

    /**
     * Gets the invoiceSubTotal attribute.
     *
     * @return Returns the invoiceSubTotal
     */
    public BigDecimal getInvoiceSubTotal() {
        return invoiceSubTotal;
    }

    /**
     * Sets the invoiceSubTotal attribute value.
     *
     * @param invoiceSubTotal The invoiceSubTotal to set.
     */
    public void setInvoiceSubTotal(BigDecimal invoiceSubTotal) {
        this.invoiceSubTotal = invoiceSubTotal;
    }

    /**
     * Gets the paymentSubTotal attribute.
     *
     * @return Returns the paymentSubTotal
     */
    public BigDecimal getPaymentSubTotal() {
        return paymentSubTotal;
    }

    /**
     * Sets the paymentSubTotal attribute value.
     *
     * @param paymentSubTotal The paymentSubTotal to set.
     */
    public void setPaymentSubTotal(BigDecimal paymentSubTotal) {
        this.paymentSubTotal = paymentSubTotal;
    }

    /**
     * Gets the displaySubtotal attribute.
     *
     * @return Returns the displaySubtotal
     */
    public boolean isDisplaySubtotal() {
        return displaySubtotal;
    }

    /**
     * Sets the displaySubtotal attribute value.
     *
     * @param displaySubtotal The displaySubtotal to set.
     */
    public void setDisplaySubtotal(boolean displaySubtotal) {
        this.displaySubtotal = displaySubtotal;
    }
}
