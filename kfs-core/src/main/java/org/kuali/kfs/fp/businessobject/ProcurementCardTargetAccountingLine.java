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
package org.kuali.kfs.fp.businessobject;

import org.kuali.kfs.sys.businessobject.TargetAccountingLine;


/**
 * This class is used to represent a procurement card target accounting line.
 */
public class ProcurementCardTargetAccountingLine extends TargetAccountingLine {
    private Integer financialDocumentTransactionLineNumber;
    protected int transactionContainerIndex;

    /**
     * Gets the transactionContainerIndex attribute.
     *
     * @return Returns the transactionContainerIndex
     */

    public int getTransactionContainerIndex() {
        return transactionContainerIndex;
    }

    /**
     * Sets the transactionContainerIndex attribute.
     *
     * @param transactionContainerIndex The transactionContainerIndex to set.
     */
    public void setTransactionContainerIndex(int transactionContainerIndex) {
        this.transactionContainerIndex = transactionContainerIndex;
    }

    /**
     * @return Returns the financialDocumentTransactionLineNumber.
     */
    public Integer getFinancialDocumentTransactionLineNumber() {
        return financialDocumentTransactionLineNumber;
    }

    /**
     * @param financialDocumentTransactionLineNumber The financialDocumentTransactionLineNumber to set.
     */
    public void setFinancialDocumentTransactionLineNumber(Integer financialDocumentTransactionLineNumber) {
        this.financialDocumentTransactionLineNumber = financialDocumentTransactionLineNumber;
    }
}
