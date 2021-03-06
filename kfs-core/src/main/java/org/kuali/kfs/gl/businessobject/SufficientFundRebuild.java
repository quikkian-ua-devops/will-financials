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

package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;

import java.util.LinkedHashMap;


/**
 * This class represents a sufficient fund rebuild
 */
public class SufficientFundRebuild extends PersistableBusinessObjectBase {

    public static final String REBUILD_ACCOUNT = "A";
    public static final String REBUILD_OBJECT = "O";

    private String chartOfAccountsCode;
    private String accountFinancialObjectTypeCode;
    private String accountNumberFinancialObjectCode;
    private Chart chart;

    /**
     * Default constructor.
     */
    public SufficientFundRebuild() {

    }

    public SufficientFundRebuild(String line) {
        setFromTextFile(line);
    }

    /**
     * This method sets this object's attributes from a line
     *
     * @param line with object's attributes
     */
    public void setFromTextFile(String line) {

        // Just in case
        line = line + "                   ";

        setChartOfAccountsCode(line.substring(0, 2).trim());
        setAccountFinancialObjectTypeCode(line.substring(2, 3));
        setAccountNumberFinancialObjectCode(line.substring(3, 10).trim());
    }

    /**
     * This method returns a String representation of this object
     *
     * @return String representation of this object
     */
    public String getLine() {
        StringBuffer sb = new StringBuffer();
        sb.append(getField(2, chartOfAccountsCode));
        sb.append(getField(1, accountFinancialObjectTypeCode));
        sb.append(getField(7, accountNumberFinancialObjectCode));
        return sb.toString();
    }

    private static String SPACES = "          ";

    /**
     * This method returns the value passed in with additional spaces if need be.
     *
     * @param size
     * @param value
     * @return
     */
    private String getField(int size, String value) {
        if (value == null) {
            return SPACES.substring(0, size);
        } else {
            if (value.length() < size) {
                return value + SPACES.substring(0, size - value.length());
            } else {
                return value;
            }
        }
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountFinancialObjectTypeCode attribute.
     *
     * @return Returns the accountFinancialObjectTypeCode
     */
    public String getAccountFinancialObjectTypeCode() {
        return accountFinancialObjectTypeCode;
    }

    /**
     * Sets the accountFinancialObjectTypeCode attribute.
     *
     * @param accountFinancialObjectTypeCode The accountFinancialObjectTypeCode to set.
     */
    public void setAccountFinancialObjectTypeCode(String accountFinancialObjectTypeCode) {
        this.accountFinancialObjectTypeCode = accountFinancialObjectTypeCode;
    }


    /**
     * Gets the accountNumberFinancialObjectCode attribute.
     *
     * @return Returns the accountNumberFinancialObjectCode
     */
    public String getAccountNumberFinancialObjectCode() {
        return accountNumberFinancialObjectCode;
    }

    /**
     * Sets the accountNumberFinancialObjectCode attribute.
     *
     * @param accountNumberFinancialObjectCode The accountNumberFinancialObjectCode to set.
     */
    public void setAccountNumberFinancialObjectCode(String accountNumberFinancialObjectCode) {
        this.accountNumberFinancialObjectCode = accountNumberFinancialObjectCode;
    }


    /**
     * Gets the chart attribute.
     *
     * @return Returns the chart
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * Sets the chart attribute.
     *
     * @param chart The chart to set.
     * @deprecated
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountFinancialObjectTypeCode", this.accountFinancialObjectTypeCode);
        m.put("accountNumberFinancialObjectCode", this.accountNumberFinancialObjectCode);
        return m;
    }
}
