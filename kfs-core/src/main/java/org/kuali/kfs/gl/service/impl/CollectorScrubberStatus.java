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
package org.kuali.kfs.gl.service.impl;


/**
 * This class represents the status for origin entries through the different origin entry groups (i.e. input, valid, error, and expired)
 */
public class CollectorScrubberStatus {
    private String inputFileName;
    private String validFileName;
    private String errorFileName;
    private String expiredFileName;

    /**
     * Gets the errorFileName attribute.
     *
     * @return Returns the errorFileName.
     */
    public String getErrorFileName() {
        return errorFileName;
    }

    /**
     * Sets the errorFileName attribute value.
     *
     * @param errorFileName The errorFileName to set.
     */
    public void setErrorFileName(String errorFileName) {
        this.errorFileName = errorFileName;
    }

    /**
     * Gets the expiredFileName attribute.
     *
     * @return Returns the expiredFileName.
     */
    public String getExpiredFileName() {
        return expiredFileName;
    }

    /**
     * Sets the expiredFileName attribute value.
     *
     * @param expiredFileName The expiredFileName to set.
     */
    public void setExpiredFileName(String expiredFileName) {
        this.expiredFileName = expiredFileName;
    }

    /**
     * Gets the inputFileName attribute.
     *
     * @return Returns the inputFileName.
     */
    public String getInputFileName() {
        return inputFileName;
    }

    /**
     * Sets the inputFileName attribute value.
     *
     * @param inputFileName The inputFileName to set.
     */
    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    /**
     * Gets the validFileName attribute.
     *
     * @return Returns the validFileName.
     */
    public String getValidFileName() {
        return validFileName;
    }

    /**
     * Sets the validFileName attribute value.
     *
     * @param validFileName The validFileName to set.
     */
    public void setValidFileName(String validFileName) {
        this.validFileName = validFileName;
    }
}
