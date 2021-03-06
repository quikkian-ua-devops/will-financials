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
package org.kuali.kfs.module.ld.document.validation.impl;

import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * Validates that an accounting line has salary object code
 */
public class SalaryExpenseTransferSalaryObjectCodeValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;

    /**
     * Validates that an accounting line does not have a salary object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     *
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        AccountingLine accountingLine = getAccountingLineForValidation();

        if (!isSalaryObjectCode(accountingLine)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.OBJECT_CODE, LaborKeyConstants.INVALID_SALARY_OBJECT_CODE_ERROR);
            result = false;
        }
        return result;
    }

    /**
     * Checks whether the given AccountingLine's Object Code is a salary object code.
     *
     * @param accountingLine The accounting line the salary object code will be retrieved from.
     * @return True if the given accounting line's object code is a salary object code, false otherwise.
     */
    protected boolean isSalaryObjectCode(AccountingLine accountingLine) {
        boolean salaryObjectCode = true;

        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;
        expenseTransferAccountingLine.refreshReferenceObject(KFSPropertyConstants.LABOR_OBJECT);

        LaborObject laborObject = expenseTransferAccountingLine.getLaborObject();
        if (ObjectUtils.isNull(laborObject)) {
            return false;
        }

        boolean isItSalaryObjectCode = LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE.equals(laborObject.getFinancialObjectFringeOrSalaryCode());
        if (!isItSalaryObjectCode) {
            salaryObjectCode = false;
        }

        return salaryObjectCode;
    }

    /**
     * Gets the accountingLineForValidation attribute.
     *
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     *
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
