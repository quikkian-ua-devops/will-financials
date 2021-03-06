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
package org.kuali.kfs.fp.document;

import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.document.Copyable;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.kfs.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the business object that represents the AdvanceDeposit document in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow. Since an Advance Deposit document is a one sided
 * transactional document, only accepting funds into the university, the accounting line data will be held in the source accounting
 * line data structure only.
 */
public class AdvanceDepositDocument extends CashReceiptFamilyBase implements Copyable, AmountTotaling, Correctable {
    public static final String ADVANCE_DEPOSIT_DOCUMENT_TYPE_CODE = "AD";

    // holds details about each advance deposit
    protected List<AdvanceDepositDetail> advanceDeposits = new ArrayList<AdvanceDepositDetail>();

    // incrementers for detail lines
    protected Integer nextAdvanceDepositLineNumber = 1;

    // monetary attributes
    protected KualiDecimal totalAdvanceDepositAmount = KualiDecimal.ZERO;

    /**
     * Default constructor that calls super.
     */
    public AdvanceDepositDocument() {
        super();
    }

    /**
     * Gets the total advance deposit amount.
     *
     * @return KualiDecimal
     */
    public KualiDecimal getTotalAdvanceDepositAmount() {
        return totalAdvanceDepositAmount;
    }

    /**
     * This method returns the advance deposit total amount as a currency formatted string.
     *
     * @return String
     */
    public String getCurrencyFormattedTotalAdvanceDepositAmount() {
        return (String) new CurrencyFormatter().format(totalAdvanceDepositAmount);
    }

    /**
     * Sets the total advance deposit amount which is the sum of all advance deposits on this document.
     *
     * @param advanceDepositAmount
     */
    public void setTotalAdvanceDepositAmount(KualiDecimal advanceDepositAmount) {
        this.totalAdvanceDepositAmount = advanceDepositAmount;
    }

    /**
     * Gets the list of advance deposits which is a list of AdvanceDepositDetail business objects.
     *
     * @return List
     */
    public List<AdvanceDepositDetail> getAdvanceDeposits() {
        return advanceDeposits;
    }

    /**
     * Sets the advance deposits list.
     *
     * @param advanceDeposits
     */
    public void setAdvanceDeposits(List<AdvanceDepositDetail> advanceDeposits) {
        this.advanceDeposits = advanceDeposits;
    }

    /**
     * Adds a new advance deposit to the list.
     *
     * @param advanceDepositDetail
     */
    public void addAdvanceDeposit(AdvanceDepositDetail advanceDepositDetail) {
        // these three make up the primary key for an advance deposit detail record
        prepareNewAdvanceDeposit(advanceDepositDetail);

        // add the new detail record to the list
        this.advanceDeposits.add(advanceDepositDetail);

        // increment line number
        this.nextAdvanceDepositLineNumber++;

        // update the overall amount
        this.totalAdvanceDepositAmount = this.totalAdvanceDepositAmount.add(advanceDepositDetail.getFinancialDocumentAdvanceDepositAmount());
    }

    /**
     * This is a helper method that automatically populates document specfic information into the advance deposit
     * (AdvanceDepositDetail) instance.
     *
     * @param advanceDepositDetail
     */
    public final void prepareNewAdvanceDeposit(AdvanceDepositDetail advanceDepositDetail) {
        advanceDepositDetail.setFinancialDocumentLineNumber(this.nextAdvanceDepositLineNumber);
        advanceDepositDetail.setDocumentNumber(this.getDocumentNumber());
        advanceDepositDetail.setFinancialDocumentTypeCode(SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass()));
    }

    /**
     * Retrieve a particular advance deposit at a given index in the list of advance deposits.
     *
     * @param index
     * @return AdvanceDepositDetail
     */
    public AdvanceDepositDetail getAdvanceDepositDetail(int index) {
        while (this.advanceDeposits.size() <= index) {
            advanceDeposits.add(new AdvanceDepositDetail());
        }
        return advanceDeposits.get(index);
    }

    /**
     * This method removes an advance deposit from the list and updates the total appropriately.
     *
     * @param index
     */
    public void removeAdvanceDeposit(int index) {
        AdvanceDepositDetail advanceDepositDetail = advanceDeposits.remove(index);
        this.totalAdvanceDepositAmount = this.totalAdvanceDepositAmount.subtract(advanceDepositDetail.getFinancialDocumentAdvanceDepositAmount());
    }

    /**
     * @return Integer
     */
    public Integer getNextAdvanceDepositLineNumber() {
        return nextAdvanceDepositLineNumber;
    }

    /**
     * @param nextAdvanceDepositLineNumber
     */
    public void setNextAdvanceDepositLineNumber(Integer nextAdvanceDepositLineNumber) {
        this.nextAdvanceDepositLineNumber = nextAdvanceDepositLineNumber;
    }

    /**
     * This method returns the overall total of the document - the advance deposit total.
     *
     * @return KualiDecimal
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTotalDollarAmount()
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return this.totalAdvanceDepositAmount;
    }

    /**
     * This method defers to its parent's version of handleRouteStatusChange, but then, if the document is processed, it creates
     * ElectronicPaymentClaim records for any qualifying accountings lines in the document.
     *
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            SpringContext.getBean(ElectronicPaymentClaimingService.class).generateElectronicPaymentClaimRecords(this);
        }
        this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this);
    }


    /**
     * Overrides super to call super and then also add in the new list of advance deposits that have to be managed.
     *
     * @see org.kuali.rice.krad.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getAdvanceDeposits());

        return managedLists;
    }

    /**
     * Generates bank offset GLPEs for deposits, if enabled.
     *
     * @param financialDocument submitted financial document
     * @param sequenceHelper    helper class which will allows us to increment a reference without using an Integer
     * @return true if there are no issues creating GLPE's
     * @see org.kuali.rice.krad.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.rice.krad.document.FinancialDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);

        if (SpringContext.getBean(BankService.class).isBankSpecificationEnabled()) {
            int displayedDepositNumber = 1;
            for (AdvanceDepositDetail detail : getAdvanceDeposits()) {
                detail.refreshReferenceObject(KFSPropertyConstants.BANK);

                GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
                if (!glpeService.populateBankOffsetGeneralLedgerPendingEntry(detail.getBank(), detail.getFinancialDocumentAdvanceDepositAmount(), this, getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.ADVANCE_DEPOSITS_LINE_ERRORS)) {
                    success = false;
                    continue; // An unsuccessfully populated bank offset entry may contain invalid relations, so don't add it
                }

                AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
                bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.AdvanceDeposit.DESCRIPTION_GLPE_BANK_OFFSET, displayedDepositNumber++));
                addPendingEntry(bankOffsetEntry);
                sequenceHelper.increment();

                GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
                success &= glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
                addPendingEntry(offsetEntry);
                sequenceHelper.increment();
            }
        }

        return success;
    }

    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this, documentTypeName);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        correctAdvanceDepositDetails();
        correctCapitalAccountingLines();
    }

    /**
     * Upon error correction, negates amount in each advance deposit detail, and updates the documentNumber to point to the new document.
     */
    protected void correctAdvanceDepositDetails() {
        for (AdvanceDepositDetail deposit : advanceDeposits) {
            deposit.setVersionNumber(new Long(1));
            deposit.setDocumentNumber(documentNumber);
            deposit.setFinancialDocumentAdvanceDepositAmount(deposit.getFinancialDocumentAdvanceDepositAmount().negated());
        }
    }

}
