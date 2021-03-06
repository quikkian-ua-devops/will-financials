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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.GECSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.GECTargetAccountingLine;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.document.Copyable;
import org.kuali.kfs.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.kfs.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;

import static org.kuali.kfs.sys.KFSConstants.FROM;
import static org.kuali.kfs.sys.KFSConstants.TO;


/**
 * This is the business object that represents the GeneralErrorCorrectionDocument in Kuali. This is a transactional document that
 * will eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines:
 * from and to. From lines are the source lines, to lines are the target lines.
 */
public class GeneralErrorCorrectionDocument extends CapitalAccountingLinesDocumentBase implements Copyable, Correctable, AmountTotaling, CapitalAssetEditable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralErrorCorrectionDocument.class);

    protected transient CapitalAssetManagementModuleService capitalAssetManagementModuleService;

    /**
     * Initializes the array lists and some basic info.
     */
    public GeneralErrorCorrectionDocument() {
        super();
    }

    /**
     * Overrides the base implementation to return "From".
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return TO;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return GECSourceAccountingLine.class;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    @Override
    public Class getTargetAccountingLineClass() {
        return GECTargetAccountingLine.class;
    }

    /**
     * Customizes a GLPE by setting financial document number, financial system origination code and document type code to null
     *
     * @param transactionalDocument submitted accounting document
     * @param accountingLine        accounting line in document
     * @param explicitEntry         general ledger pending entry
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     * GeneralLedgerPendingEntry)
     */
    @Override
    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
        explicitEntry.setTransactionLedgerEntryDescription(buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(postable));

        // Clearing fields that are already handled by the parent algorithm - we don't actually want
        // these to copy over from the accounting lines b/c they don't belong in the GLPEs
        // if the aren't nulled, then GECs fail to post
        explicitEntry.setReferenceFinancialDocumentNumber(null);
        explicitEntry.setReferenceFinancialSystemOriginationCode(null);
        explicitEntry.setReferenceFinancialDocumentTypeCode(null);
    }

    /**
     * Builds an appropriately formatted string to be used for the <code>transactionLedgerEntryDescription</code>. It is built
     * using information from the <code>{@link AccountingLine}</code>. Format is "01-12345: blah blah blah".
     *
     * @param line                  accounting line
     * @param transactionalDocument submitted accounting document
     * @return String formatted string to be used for transaction ledger entry description
     */
    protected String buildTransactionLedgerEntryDescriptionUsingRefOriginAndRefDocNumber(GeneralLedgerPendingEntrySourceDetail line) {
        String description = "";
        description = line.getReferenceOriginCode() + "-" + line.getReferenceNumber();

        if (StringUtils.isNotBlank(line.getFinancialDocumentLineDescription())) {
            description += ": " + line.getFinancialDocumentLineDescription();
        } else {
            description += ": " + getDocumentHeader().getDocumentDescription();
        }

        if (description.length() > GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH) {
            description = description.substring(0, GENERAL_LEDGER_PENDING_ENTRY_CODE.GLPE_DESCRIPTION_MAX_LENGTH - 3) + "...";
        }

        return description;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this);
    }


    /**
     * @see org.kuali.rice.krad.document.DocumentBase#postProcessSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this, documentTypeName);
        }
    }

    /**
     * @return CapitalAssetManagementModuleService
     */
    public CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        if (capitalAssetManagementModuleService == null) {
            capitalAssetManagementModuleService = SpringContext.getBean(CapitalAssetManagementModuleService.class);
        }
        return capitalAssetManagementModuleService;
    }
}
