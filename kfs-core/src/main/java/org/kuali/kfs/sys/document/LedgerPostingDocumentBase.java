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
package org.kuali.kfs.sys.document;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.sql.Date;

/**
 * Base implementation for a ledger posting document.
 */
public class LedgerPostingDocumentBase extends FinancialSystemTransactionalDocumentBase implements LedgerPostingDocument {
    static protected transient DateTimeService dateTimeService;
    static protected transient AccountingPeriodService accountingPeriodService;
    static protected transient DataDictionaryService dataDictionaryService;

    protected AccountingPeriod accountingPeriod;
    protected Integer postingYear;
    protected String postingPeriodCode;
    protected boolean checkPostingYearForCopy;

    /**
     * Constructs a LedgerPostingDocumentBase.java.
     */
    public LedgerPostingDocumentBase() {
        super();
        if (SpringContext.isInitialized()) {
            createInitialAccountingPeriod();
        }
    }

    /**
     * Used during initialization to provide a base <code>{@link AccountingPeriod}</code>.<br/>
     * <p>
     * This is a hack right now because its intended to be set by the
     * <code>{@link org.kuali.kfs.coa.service.AccountingPeriodService}</code>
     *
     * @return AccountingPeriod
     */
    public void createInitialAccountingPeriod() {
        AccountingPeriod accountingPeriod = retrieveCurrentAccountingPeriod();
        setAccountingPeriod(accountingPeriod);
    }

    /**
     * Finds the accounting period for the current date
     *
     * @return the current accounting period
     */
    public AccountingPeriod retrieveCurrentAccountingPeriod() {
        // CSU 6702 BEGIN
        try {
            // CSU 6702 END
            Date date = getDateTimeService().getCurrentSqlDate();
            return getAccountingPeriodService().getByDate(date);
            // CSU 6702 BEGIN
        } catch (RuntimeException ex) {
            // catch and ignore - prevent blowup when called before services initialized
            return null;
        }
        // CSU 6702 END
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#getPostingYear()
     */
    public Integer getPostingYear() {
        return postingYear;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#setPostingYear(java.lang.Integer)
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#getPostingPeriodCode()
     */
    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#setPostingPeriodCode(java.lang.String)
     */
    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#getAccountingPeriod()
     */
    public AccountingPeriod getAccountingPeriod() {
        accountingPeriod = getAccountingPeriodService().getByPeriod(postingPeriodCode, postingYear);

        return accountingPeriod;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#setAccountingPeriod(AccountingPeriod)
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;

        if (ObjectUtils.isNotNull(accountingPeriod)) {
            this.setPostingYear(accountingPeriod.getUniversityFiscalYear());
            this.setPostingPeriodCode(accountingPeriod.getUniversityFiscalPeriodCode());
        }
    }

    /**
     * If we've copied, we need to update the posting period and year
     *
     * @see org.kuali.rice.krad.document.DocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException, IllegalStateException {
        super.toCopy();
        setAccountingPeriod(retrieveCurrentAccountingPeriod());
    }

    /**
     * Returns the financial document type code for the given document, using the DataDictionaryService
     *
     * @return the financial document type code for the given document
     */
    public String getFinancialDocumentTypeCode() {
        return getDataDictionaryService().getDocumentTypeNameByClass(this.getClass());
    }


    public static DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

    public static DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    public static AccountingPeriodService getAccountingPeriodService() {
        if (accountingPeriodService == null) {
            accountingPeriodService = SpringContext.getBean(AccountingPeriodService.class);
        }
        return accountingPeriodService;
    }

    // CSU 6702 BEGIN
    // rSmart-jkneal-KFSCSU-199-begin mod for selected accounting period

    /**
     * Composite of postingPeriodCode and postingYear
     *
     * @return Return a composite of postingPeriodCode and postingYear
     */
    public String getAccountingPeriodCompositeString() {
        return postingPeriodCode + postingYear;
    }

    /**
     * Set accountingPeriod based on incoming paramater.
     *
     * @param accountingPeriodString in the form of [period][year]
     */
    public void setAccountingPeriodCompositeString(String accountingPeriodString) {
        if (StringUtils.isNotBlank(accountingPeriodString)) {
            String period = StringUtils.left(accountingPeriodString, 2);
            Integer year = new Integer(StringUtils.right(accountingPeriodString, 4));
            AccountingPeriod accountingPeriod = getAccountingPeriodService().getByPeriod(period, year);
            setAccountingPeriod(accountingPeriod);
        }
    }
    // rSmart-jkneal-KFSCSU-199-end mod for selected accounting period
    // CSU 6702 END
}
