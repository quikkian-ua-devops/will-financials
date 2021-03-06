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
package org.kuali.kfs.module.ec.util;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import static org.kuali.kfs.sys.KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT;

/**
 * To hold the payroll amount and percent
 */
public class PayrollAmountHolder {

    private KualiDecimal payrollAmount;
    private Integer payrollPercent;

    private KualiDecimal totalAmount;

    private KualiDecimal accumulatedAmount;
    private Integer accumulatedPercent;

    /**
     * Constructs a PayrollAmountHolder.java.
     *
     * @param totalAmount        the total payroll amount
     * @param accumulatedAmount  the accumulated payroll amount
     * @param accumulatedPercent the accumulated payroll percent
     */
    public PayrollAmountHolder(KualiDecimal totalAmount, KualiDecimal accumulatedAmount, Integer accumulatedPercent) {
        super();
        this.totalAmount = totalAmount;
        this.accumulatedAmount = accumulatedAmount;
        this.accumulatedPercent = accumulatedPercent;
    }

    /**
     * Gets the payrollAmount attribute.
     *
     * @return Returns the payrollAmount.
     */
    public KualiDecimal getPayrollAmount() {
        return payrollAmount;
    }

    /**
     * Sets the payrollAmount attribute value.
     *
     * @param payrollAmount The payrollAmount to set.
     */
    public void setPayrollAmount(KualiDecimal payrollAmount) {
        this.payrollAmount = payrollAmount;
    }

    /**
     * Gets the payrollPercent attribute.
     *
     * @return Returns the payrollPercent.
     */
    public Integer getPayrollPercent() {
        return payrollPercent;
    }

    /**
     * Sets the payrollPercent attribute value.
     *
     * @param payrollPercent The payrollPercent to set.
     */
    public void setPayrollPercent(Integer payrollPercent) {
        this.payrollPercent = payrollPercent;
    }

    /**
     * Gets the totalAmount attribute.
     *
     * @return Returns the totalAmount.
     */
    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the totalAmount attribute value.
     *
     * @param totalAmount The totalAmount to set.
     */
    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets the accumulatedAmount attribute.
     *
     * @return Returns the accumulatedAmount.
     */
    public KualiDecimal getAccumulatedAmount() {
        return accumulatedAmount;
    }

    /**
     * Sets the accumulatedAmount attribute value.
     *
     * @param accumulatedAmount The accumulatedAmount to set.
     */
    public void setAccumulatedAmount(KualiDecimal accumulatedAmount) {
        this.accumulatedAmount = accumulatedAmount;
    }

    /**
     * Gets the accumulatedPercent attribute.
     *
     * @return Returns the accumulatedPercent.
     */
    public Integer getAccumulatedPercent() {
        return accumulatedPercent;
    }

    /**
     * Sets the accumulatedPercent attribute value.
     *
     * @param accumulatedPercent The accumulatedPercent to set.
     */
    public void setAccumulatedPercent(Integer accumulatedPercent) {
        this.accumulatedPercent = accumulatedPercent;
    }

    /**
     * calculate the payroll percentage based on the given information in payroll amount holder
     *
     * @param payrollAmountHolder the given payroll amount holder containing relating information
     */
    public static void calculatePayrollPercent(PayrollAmountHolder payrollAmountHolder) {
        KualiDecimal totalAmount = payrollAmountHolder.getTotalAmount();
        if (totalAmount.isZero()) {
            return;
        }

        KualiDecimal payrollAmount = payrollAmountHolder.getPayrollAmount();
        KualiDecimal accumulatedAmount = payrollAmountHolder.getAccumulatedAmount();
        accumulatedAmount = accumulatedAmount.add(payrollAmount);

        int accumulatedPercent = payrollAmountHolder.getAccumulatedPercent();
        int quotientOne = Math.round(payrollAmount.multiply(HUNDRED_DOLLAR_AMOUNT).divide(totalAmount).floatValue());
        accumulatedPercent = accumulatedPercent + quotientOne;

        int quotientTwo = Math.round(accumulatedAmount.multiply(HUNDRED_DOLLAR_AMOUNT).divide(totalAmount).floatValue());
        quotientTwo = quotientTwo - accumulatedPercent;

        payrollAmountHolder.setAccumulatedAmount(accumulatedAmount);
        payrollAmountHolder.setAccumulatedPercent(accumulatedPercent + quotientTwo);
        payrollAmountHolder.setPayrollPercent(quotientOne + quotientTwo);
    }

    /**
     * recalculate the payroll amount based on the given total amount and effort percent
     *
     * @param totalPayrollAmount the given total amount
     * @param effortPercent      the given effort percent
     * @return the payroll amount calculated from the given total amount and effort percent
     */
    public static KualiDecimal recalculatePayrollAmount(KualiDecimal totalPayrollAmount, Integer effortPercent) {
        double amount = totalPayrollAmount.doubleValue() * effortPercent / HUNDRED_DOLLAR_AMOUNT.doubleValue();

        return new KualiDecimal(amount);
    }

    /**
     * recalculate the effort percent based on the given total amount and payroll amount
     *
     * @param totalPayrollAmount the given total amount
     * @param payrollAmount      the given payroll amount
     * @return the effort percent calculated from the given total amount and payroll amount
     */
    public static Double recalculateEffortPercent(KualiDecimal totalPayrollAmount, KualiDecimal payrollAmount) {
        double percent = payrollAmount.doubleValue() * HUNDRED_DOLLAR_AMOUNT.doubleValue() / totalPayrollAmount.doubleValue();

        return percent;
    }

    /**
     * recalculate the effort percent based on the given total amount and payroll amount and return it as of type String
     *
     * @param totalPayrollAmount the given total amount
     * @param payrollAmount      the given payroll amount
     * @return the effort percent as String calculated from the given total amount and payroll amount
     */
    public static String recalculateEffortPercentAsString(KualiDecimal totalPayrollAmount, KualiDecimal payrollAmount) {
        double actualPercentAsDouble = 0;
        if (totalPayrollAmount.isNonZero()) {
            actualPercentAsDouble = recalculateEffortPercent(totalPayrollAmount, payrollAmount);
        }

        return String.format("%.4f%s", actualPercentAsDouble, KFSConstants.PERCENTAGE_SIGN);
    }
}
