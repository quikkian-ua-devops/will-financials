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
package org.kuali.kfs.module.cam.batch.service;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cam.batch.ExtractProcessLog;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Declares the service methods used by CAB batch program
 */
public interface BatchExtractService {

    /**
     * This method is the entry point into the Batch Extract.
     *
     * @param processLog
     */
    public void performExtract(ExtractProcessLog processLog);


    /**
     * Allocate additional charges during batch.
     *
     * @param purApDocuments
     */
    void allocateAdditionalCharges(HashSet<PurchasingAccountsPayableDocument> purApDocuments);

    /**
     * Returns the list of CAB eligible GL entries, filter parameters are pre-configured
     *
     * @param process log
     * @return Eligible GL Entries meeting batch parameters configured under parameter group KFS-CAM:Batch
     */
    Collection<Entry> findElgibleGLEntries(ExtractProcessLog processLog);


    /**
     * Saves financial transaction lines which dont have Purchase Order number associated with it
     *
     * @param fpLines    Financial transaction lines
     * @param processLog Process Log
     */
    void saveFPLines(List<Entry> fpLines, ExtractProcessLog processLog);

    /**
     * Saved purchasing line transactions, this method implementation internally uses
     * {@link org.kuali.kfs.gl.batch.service.ReconciliationService} to QA the data before saving
     *
     * @param poLines    Eligible GL Lines
     * @param processLog Process Log
     */
    HashSet<PurchasingAccountsPayableDocument> savePOLines(List<Entry> poLines, ExtractProcessLog processLog);

    /**
     * Separates out transaction lines associated with purchase order from the rest
     *
     * @param fpLines          Non-purchasing lines
     * @param purapLines       Purchasing lines
     * @param elgibleGLEntries Full list of eligible GL entries
     */
    void separatePOLines(List<Entry> fpLines, List<Entry> purapLines, Collection<Entry> elgibleGLEntries);

    /**
     * Updates the last extract time stamp system parameter, usually done when a batch process is finished successfully.
     *
     * @param time Last extract start time
     */
    void updateLastExtractTime(Timestamp time);


    /**
     * This method collects account line history using batch parameters
     *
     * @return Collection Purchasing Accounts Payable Account Line History
     */
    Collection<PurApAccountingLineBase> findPurapAccountRevisions();


    /**
     * Implementation will retrieve all eligible Purchase Order account lines from a Purchase order that matches criteria required
     * by pre-asset tagging, using these account lines, batch process can identify the eligible purchase order line items to be
     * saved for pre-tagging screen
     *
     * @return Pre-taggable PO Account lines
     */
    Collection<PurchaseOrderAccount> findPreTaggablePOAccounts();

    /**
     * Implementation will identify eligible purchase oder line items eligible for pre-tagging screen
     *
     * @param preTaggablePOAccounts List of pre-taggable account lines
     */
    void savePreTagLines(Collection<PurchaseOrderAccount> preTaggablePOAccounts);

    /**
     * Updates the last extract date parameter for Pre Asset Tagging Step
     *
     * @param date Date value
     */
    void updateLastExtractDate(Date date);
}
