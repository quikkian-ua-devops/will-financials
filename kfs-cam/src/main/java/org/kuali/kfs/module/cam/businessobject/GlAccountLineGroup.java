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
package org.kuali.kfs.module.cam.businessobject;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.util.ArrayList;
import java.util.List;

/**
 * Accounting line grouped data for GL Line
 */
public class GlAccountLineGroup extends AccountLineGroup {
    private Entry targetEntry;
    private List<Entry> sourceEntries = new ArrayList<Entry>();
    private List<PurApAccountingLineBase> matchedPurApAcctLines = new ArrayList<PurApAccountingLineBase>();

    /**
     * Constructs a GlAccountLineGroup from a GL Line Entry
     *
     * @param entry GL Line
     */
    public GlAccountLineGroup(Entry entry) {
        setUniversityFiscalYear(entry.getUniversityFiscalYear());
        setChartOfAccountsCode(entry.getChartOfAccountsCode());
        setAccountNumber(entry.getAccountNumber());
        setSubAccountNumber(entry.getSubAccountNumber());
        setFinancialObjectCode(entry.getFinancialObjectCode());
        setFinancialSubObjectCode(entry.getFinancialSubObjectCode());
        setUniversityFiscalPeriodCode(entry.getUniversityFiscalPeriodCode());
        setDocumentNumber(entry.getDocumentNumber());
        setReferenceFinancialDocumentNumber(entry.getReferenceFinancialDocumentNumber());
        setOrganizationReferenceId(entry.getOrganizationReferenceId());
        setProjectCode(entry.getProjectCode());
        this.sourceEntries.add(entry);
        this.targetEntry = (Entry) ObjectUtils.deepCopy(entry);

        KualiDecimal amount = entry.getTransactionLedgerEntryAmount();
        if (KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode())) {
            // negate the amount
            setAmount(amount.negated());
        } else {
            setAmount(amount);
        }
    }

    /**
     * Returns true if input GL entry belongs to this account group
     *
     * @param entry Entry
     * @return true if Entry belongs to same account line group
     */
    public boolean isAccounted(Entry entry) {
        GlAccountLineGroup test = new GlAccountLineGroup(entry);
        return this.equals(test);
    }

    /**
     * This method will combine multiple GL entries for the same account line group, so that m:n association is prevented in the
     * database. This could be a rare case that we need to address. First GL is used as the final target and rest of the GL entries
     * are adjusted.
     *
     * @param entry
     */
    public void combineEntry(Entry newEntry) {
        this.sourceEntries.add(newEntry);
        KualiDecimal newAmt = newEntry.getTransactionLedgerEntryAmount();
        String newDebitCreditCode = newEntry.getTransactionDebitCreditCode();

        KualiDecimal targetAmount = this.targetEntry.getTransactionLedgerEntryAmount();
        String targetDebitCreditCode = this.targetEntry.getTransactionDebitCreditCode();

        // if debit/credit code is same then just add the amount
        if (targetDebitCreditCode.equals(newDebitCreditCode)) {
            targetAmount = targetAmount.add(newAmt);
        } else {
            // if debit/credit code is not the same and new amount is greater, toggle the debit/credit code
            if (newAmt.isGreaterThan(targetAmount)) {
                targetDebitCreditCode = newDebitCreditCode;
                targetAmount = newAmt.subtract(targetAmount);
            } else {
                // if debit/credit code is not the same and current amount is greater or equal
                targetAmount = targetAmount.subtract(newAmt);
            }
        }
        this.targetEntry.setTransactionDebitCreditCode(targetDebitCreditCode);
        this.targetEntry.setTransactionLedgerEntryAmount(targetAmount);
        // re-compute the absolute value of amount
        if (KFSConstants.GL_CREDIT_CODE.equals(targetDebitCreditCode)) {
            setAmount(targetAmount.negated());
        } else {
            setAmount(targetAmount);
        }
    }

    /**
     * Gets the targetEntry attribute.
     *
     * @return Returns the targetEntry
     */
    public Entry getTargetEntry() {
        return targetEntry;
    }

    /**
     * Sets the targetEntry attribute.
     *
     * @param targetEntry The targetEntry to set.
     */
    public void setTargetEntry(Entry targetGlEntry) {
        this.targetEntry = targetGlEntry;
    }

    /**
     * Gets the sourceEntries attribute.
     *
     * @return Returns the sourceEntries
     */
    public List<Entry> getSourceEntries() {
        return sourceEntries;
    }

    /**
     * Sets the sourceEntries attribute.
     *
     * @param sourceEntries The sourceEntries to set.
     */
    public void setSourceEntries(List<Entry> sourceGlEntries) {
        this.sourceEntries = sourceGlEntries;
    }

    /**
     * Gets the matchedPurApAcctLines attribute.
     *
     * @return Returns the matchedPurApAcctLines.
     */
    public List<PurApAccountingLineBase> getMatchedPurApAcctLines() {
        return matchedPurApAcctLines;
    }

    /**
     * Sets the matchedPurApAcctLines attribute value.
     *
     * @param matchedPurApAcctLines The matchedPurApAcctLines to set.
     */
    public void setMatchedPurApAcctLines(List<PurApAccountingLineBase> matchedPurApAcctLines) {
        this.matchedPurApAcctLines = matchedPurApAcctLines;
    }
}
