/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.batch.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.fp.batch.ProcurementCardAutoApproveDocumentsStep;
import org.kuali.kfs.fp.batch.ProcurementCardCreateDocumentsStep;
import org.kuali.kfs.fp.batch.ProcurementCardLoadStep;
import org.kuali.kfs.fp.batch.ProcurementCardReportType;
import org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.ProcurementCardDefault;
import org.kuali.kfs.fp.businessobject.ProcurementCardHolder;
import org.kuali.kfs.fp.businessobject.ProcurementCardSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransaction;
import org.kuali.kfs.fp.businessobject.ProcurementCardTransactionDetail;
import org.kuali.kfs.fp.businessobject.ProcurementCardVendor;
import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.mail.VelocityMailMessage;
import org.kuali.kfs.sys.service.EmailService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.AUTO_APPROVE_DOCUMENTS_IND;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.AUTO_APPROVE_NUMBER_OF_DAYS;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_ACCOUNT_PARM_NM;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_CHART_CODE_PARM_NM;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.DEFAULT_TRANS_OBJECT_CODE_PARM_NM;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.ERROR_TRANS_ACCOUNT_PARM_NM;
import static org.kuali.kfs.fp.document.validation.impl.ProcurementCardDocumentRuleConstants.SINGLE_TRANSACTION_IND_PARM_NM;
import static org.kuali.kfs.sys.KFSConstants.FinancialDocumentTypeCodes.PROCUREMENT_CARD;
import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;

public class ProcurementCardCreateDocumentServiceImpl implements ProcurementCardCreateDocumentService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardCreateDocumentServiceImpl.class);

    protected ParameterService parameterService;
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected WorkflowDocumentService workflowDocumentService;
    protected DataDictionaryService dataDictionaryService;
    protected DateTimeService dateTimeService;
    protected AccountingLineRuleHelperService accountingLineRuleUtil;
    protected CapitalAssetManagementModuleService capitalAssetManagementModuleService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected EmailService emailService;
    protected String emailTemplateUrl;

    public static final String DOCUMENT_DESCRIPTION_PATTERN = "{0}-{1}-{2}-{3}";

    /**
     * This method retrieves a collection of credit card transactions and traverses through this list, creating
     * ProcurementCardDocuments for each card.
     *
     * @return True if the procurement card documents were created successfully.  If any problem occur while creating the
     * documents, a runtime exception will be thrown.
     * @see org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService#createProcurementCardDocuments()
     */
    @Override
    @SuppressWarnings("rawtypes")
    @Transactional
    public boolean createProcurementCardDocuments() {
        LOG.debug("createProcurementCardDocuments() started");

        List documents = new ArrayList();
        List cardTransactions = retrieveTransactions();

        // iterate through card transaction list and create documents
        for (Iterator iter = cardTransactions.iterator(); iter.hasNext(); ) {
            documents.add(createProcurementCardDocument((List) iter.next()));
        }

        // now store all the documents
        for (Iterator iter = documents.iterator(); iter.hasNext(); ) {
            ProcurementCardDocument pcardDocument = (ProcurementCardDocument) iter.next();
            try {
                documentService.saveDocument(pcardDocument, DocumentSystemSaveEvent.class);
                LOG.info("createProcurementCardDocuments() Saved Procurement Card document: " + pcardDocument.getDocumentNumber());
            } catch (Exception e) {
                LOG.error("createProcurementCardDocuments() Error persisting document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(), e);
                throw new RuntimeException("Error persisting document # " + pcardDocument.getDocumentHeader().getDocumentNumber() + " " + e.getMessage(), e);
            }
        }

        // send email notification
        sendEmailNotification(documents);

        return true;
    }

    /**
     * Sending email notification with initializing template variable details in report.
     *
     * @param documents
     */
    protected void sendEmailNotification(List<ProcurementCardDocument> documents) {
        List<ProcurementCardReportType> summaryList = getSortedReportSummaryList(documents);
        int totalBatchTransactions = getBatchTotalTransactionCnt(summaryList);
        DateFormat dateFormat = getDateFormat(KFSConstants.CoreModuleNamespaces.FINANCIAL, KFSConstants.ProcurementCardParameters.PCARD_BATCH_CREATE_DOC_STEP, KFSConstants.ProcurementCardParameters.BATCH_SUMMARY_RUNNING_TIMESTAMP_FORMAT, KFSConstants.ProcurementCardEmailTimeFormat);

        // Create formatter for payment amounts and batch run time
        String batchRunTime = dateFormat.format(dateTimeService.getCurrentDate());

        final Map<String, Object> templateVariables = new HashMap<>();

        templateVariables.put(KFSConstants.ProcurementCardEmailVariableTemplate.DOC_CREATE_DATE, batchRunTime);
        templateVariables.put(KFSConstants.ProcurementCardEmailVariableTemplate.TRANSACTION_COUNTER, totalBatchTransactions);
        templateVariables.put(KFSConstants.ProcurementCardEmailVariableTemplate.TRANSACTION_SUMMARY_LIST, summaryList);

        // Handle for email sending exception
        VelocityMailMessage msg = new VelocityMailMessage();
        msg.setFromAddress(emailService.getDefaultFromAddress());
        getProdEmailReceivers().stream().forEach(r -> msg.addToAddress(r));
        msg.setSubject("KFS Pcard Load Summary");
        msg.setTemplateUrl(emailTemplateUrl);
        msg.setTemplateVariables(templateVariables);
        emailService.sendMessage(msg,false);
    }

    protected Collection<String> getProdEmailReceivers() {
        return parameterService.getParameterValuesAsString(KFSConstants.CoreModuleNamespaces.FINANCIAL, KFSConstants.ProcurementCardParameters.PCARD_BATCH_CREATE_DOC_STEP, KFSConstants.ProcurementCardParameters.PCARD_BATCH_SUMMARY_TO_EMAIL_ADDRESSES);
    }

    /**
     * Retrieve date formatter used to format batch running timestamp in report. System parameter has precedence over system default String.
     *
     * @return
     */
    protected DateFormat getDateFormat(String namespaceCode, String componentCode, String parameterName, String defaultValue) {
        DateFormat dateFormat = new SimpleDateFormat(defaultValue);
        if (parameterService.parameterExists(namespaceCode, componentCode, parameterName)) {
            String formatParmVal = parameterService.getParameterValueAsString(namespaceCode, componentCode, parameterName, defaultValue);
            if (StringUtils.isNotBlank(formatParmVal)) {
                try {
                    dateFormat = new SimpleDateFormat(formatParmVal);
                } catch (IllegalArgumentException e) {
                    LOG.error("Parameter PCARD_BATCH_SUMMARY_RUNNING_TIMESTAMP_FORMAT used by ProcurementCardCreateDocumentsStep does not set up properly. Use system default timestamp format instead for generating report.");
                    dateFormat = new SimpleDateFormat(defaultValue);
                }
            }
        }

        dateFormat.setLenient(false);
        return dateFormat;
    }

    /**
     * Get the total number of transactions processed by this batch run
     *
     * @param summaryList
     * @return
     */
    protected int getBatchTotalTransactionCnt(List<ProcurementCardReportType> summaryList) {
        int totalBatchTransactionCnt = 0;

        for (ProcurementCardReportType procurementCardReportType : summaryList) {
            totalBatchTransactionCnt += procurementCardReportType.getTotalTranNumber();
        }
        return totalBatchTransactionCnt;
    }

    /**
     * Build the sorted batch report summary list
     *
     * @param documents
     * @return
     */
    protected List<ProcurementCardReportType> getSortedReportSummaryList(List<ProcurementCardDocument> documents) {
        KualiDecimal totalAmount;
        int totalTransactionCounter;

        HashMap<String, String> totalDocMap;

        Map<Date, HashMap<String, String>> docMapByPstDt = new HashMap<>();
        Map<Date, Integer> transctionMapByPstDt = new HashMap<>();
        Map<Date, KualiDecimal> totalAmountMapByPstDt = new HashMap<>();
        Iterator iter = null;
        Date keyDate = null;

        // walk through each doc and calculate the total amount per bank transaction posting date.
        for (ProcurementCardDocument pDoc : documents) {
            iter = pDoc.getTransactionEntries().iterator();
            while (iter.hasNext()) {
                ProcurementCardTransactionDetail pCardDetail = (ProcurementCardTransactionDetail) iter.next();

                keyDate = pCardDetail.getTransactionPostingDate();

                if (docMapByPstDt.containsKey(keyDate)) {
                    totalDocMap = docMapByPstDt.get(keyDate);
                    totalTransactionCounter = transctionMapByPstDt.get(keyDate).intValue();
                    totalAmount = totalAmountMapByPstDt.get(keyDate);
                } else {
                    totalDocMap = new HashMap<>();
                    totalTransactionCounter = 0;
                    totalAmount = KualiDecimal.ZERO;
                }
                // update number of transactions
                totalTransactionCounter++;
                // update number of eDocs
                if (!totalDocMap.containsKey(pDoc.getDocumentNumber())) {
                    totalDocMap.put(pDoc.getDocumentNumber(), pDoc.getDocumentNumber());
                }
                // update transaction total
                totalAmount = totalAmount.add(pCardDetail.getTransactionTotalAmount());

                docMapByPstDt.put(keyDate, totalDocMap);
                transctionMapByPstDt.put(keyDate, totalTransactionCounter);
                totalAmountMapByPstDt.put(keyDate, totalAmount);
            }
        }

        List<ProcurementCardReportType> summaryList = buildBatchReportSummary(docMapByPstDt, transctionMapByPstDt, totalAmountMapByPstDt);

        sortingSummaryList(summaryList);

        return summaryList;
    }

    /**
     * Build Procurement Card report object.
     *
     * @param docMapByPstDt
     * @param transctionMapByPstDt
     * @param totalAmountMapByPstDt
     * @return
     */
    protected List<ProcurementCardReportType> buildBatchReportSummary(Map<Date, HashMap<String, String>> docMapByPstDt, Map<Date, Integer> transctionMapByPstDt, Map<Date, KualiDecimal> totalAmountMapByPstDt) {
        List<ProcurementCardReportType> summaryList = new ArrayList<>();
        ProcurementCardReportType reportEntry;

        Formatter currencyFormatter = new CurrencyFormatter();
        DateFormat dateFormatter = getDateFormat(KFSConstants.CoreModuleNamespaces.FINANCIAL, KFSConstants.ProcurementCardParameters.PCARD_BATCH_CREATE_DOC_STEP, KFSConstants.ProcurementCardParameters.BATCH_SUMMARY_POSTING_DATE_FORMAT, KFSConstants.ProcurementCardTransactionTimeFormat);

        for (Date keyDate : docMapByPstDt.keySet()) {
            reportEntry = new ProcurementCardReportType();
            reportEntry.setTransactionPostingDate(keyDate);

            reportEntry.setFormattedPostingDate(dateFormatter.format(keyDate));
            reportEntry.setTotalDocNumber(docMapByPstDt.get(keyDate).keySet().isEmpty() ? 0 : docMapByPstDt.get(keyDate).keySet().size());
            reportEntry.setTotalTranNumber(transctionMapByPstDt.get(keyDate));
            reportEntry.setTotalAmount(currencyFormatter.formatForPresentation(totalAmountMapByPstDt.get(keyDate)).toString());
            summaryList.add(reportEntry);
        }
        return summaryList;
    }


    /**
     * Sorting the report summary list by transaction posting date
     *
     * @param summaryList
     */
    protected void sortingSummaryList(List<ProcurementCardReportType> summaryList) {
        Comparator<ProcurementCardReportType> comparator = new Comparator<ProcurementCardReportType>() {

            @Override
            public int compare(ProcurementCardReportType o1, ProcurementCardReportType o2) {

                if (o1 == null && o1 == null) {
                    return 0;
                }

                if (o1 == null || o2 == null) {
                    return o1 == null ? -1 : 1;
                }

                return o1.getTransactionPostingDate().compareTo(o2.getTransactionPostingDate());
            }

        };

        // sort by posting date
        Collections.sort(summaryList, comparator);
    }

    /**
     * This method retrieves all the procurement card documents with a status of 'I' and routes them to the next step in the
     * routing path.
     *
     * @return True if the routing was performed successfully.  A runtime exception will be thrown if any errors occur while routing.
     */
    @Override
    public boolean routeProcurementCardDocuments() {
        LOG.debug("routeProcurementCardDocuments() started");

        Collection<ProcurementCardDocument> procurementCardDocumentList = retrieveProcurementCardDocumentsToRoute(KewApiConstants.ROUTE_HEADER_SAVED_CD);

        LOG.info("routeProcurementCardDocuments() Number of PCards to Route: " + procurementCardDocumentList.size());

        for (ProcurementCardDocument pcardDocument : procurementCardDocumentList) {
            try {
                LOG.info("routeProcurementCardDocuments() Routing PCDO document # " + pcardDocument.getDocumentNumber() + ".");

                documentService.prepareWorkflowDocument(pcardDocument);

                //** NOTE
                //
                //     Calling workflow service to BYPASS business rule checks
                //
                //** NOTE
                workflowDocumentService.route(pcardDocument.getDocumentHeader().getWorkflowDocument(), "", null);
            } catch (WorkflowException e) {
                LOG.error("Error routing document # " + pcardDocument.getDocumentNumber() + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return true;
    }

    /**
     * Returns a list of all initiated but not yet routed procurement card documents, using the KualiWorkflowInfo service.
     *
     * @return a list of procurement card documents to route
     */
    protected Collection<ProcurementCardDocument> retrieveProcurementCardDocumentsToRoute(String statusCode) {

        try {
            return financialSystemDocumentService.findByWorkflowStatusCode(ProcurementCardDocument.class, DocumentStatus.fromCode(statusCode));
        } catch (WorkflowException e) {
            LOG.error("Error searching for enroute procurement card documents " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * This method determines if procurement card documents can be auto approved.  A document can be auto approved if
     * the grace period for allowing auto approval of a procurement card document has passed.  The grace period is defined
     * by a parameter in the parameters table.  The create date of the document is then compared against the current date and
     * if the difference is larger than the grace period defined, then the document is auto approved.
     *
     * @return This method always returns true.
     * @see org.kuali.kfs.fp.batch.service.ProcurementCardCreateDocumentService#autoApproveProcurementCardDocuments()
     */
    @Override
    public boolean autoApproveProcurementCardDocuments() {
        // check if auto approve is turned on
        boolean autoApproveOn = parameterService.getParameterValueAsBoolean(ProcurementCardAutoApproveDocumentsStep.class, AUTO_APPROVE_DOCUMENTS_IND);

        if (!autoApproveOn) { // no auto approve?  then skip out of here...
            return true;
        }

        Collection<ProcurementCardDocument> pcardDocumentList = retrieveProcurementCardDocumentsToRoute(KewApiConstants.ROUTE_HEADER_ENROUTE_CD);

        // get number of days and type for auto approve
        int autoApproveNumberDays = Integer.parseInt(parameterService.getParameterValueAsString(ProcurementCardAutoApproveDocumentsStep.class, AUTO_APPROVE_NUMBER_OF_DAYS));

        Timestamp currentDate = dateTimeService.getCurrentTimestamp();
        for (ProcurementCardDocument pcardDocument : pcardDocumentList) {
            try {

                // prevent PCard documents from auto approving if they have capital asset info to collect
                if (capitalAssetManagementModuleService.hasCapitalAssetObjectSubType(pcardDocument)) {
                    continue;
                }

                // if number of days in route is passed the allowed number, call doc service for super user approve
                Timestamp docCreateDate = new Timestamp(pcardDocument.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());
                if (KfsDateUtils.getDifferenceInDays(docCreateDate, currentDate) > autoApproveNumberDays) {
                    // update document description to reflect the auto approval
                    pcardDocument.getDocumentHeader().setDocumentDescription("Auto Approved On " + dateTimeService.toDateTimeString(currentDate) + ".");

                    LOG.info("autoApproveProcurementCardDocuments() Auto approving document # " + pcardDocument.getDocumentHeader().getDocumentNumber());

                    pcardDocument.setAutoApprovedIndicator(true);
                    documentService.superUserApproveDocument(pcardDocument, "");
                }
            } catch (WorkflowException e) {
                LOG.error("autoApproveProcurementCardDocuments() Error auto approving document # " + pcardDocument.getDocumentNumber() + " " + e.getMessage(), e);
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return true;
    }

    /**
     * This method retrieves a list of transactions from a temporary table, and groups them into document lists, based on
     * single transaction indicator or a grouping by card.
     *
     * @return List containing transactions for document.
     */
    @SuppressWarnings("rawtypes")
    protected List retrieveTransactions() {
        List groupedTransactions = new ArrayList();

        // retrieve records from transaction table order by card number
        List transactions = (List) businessObjectService.findMatchingOrderBy(ProcurementCardTransaction.class, new HashMap(), KFSPropertyConstants.TRANSACTION_CREDIT_CARD_NUMBER, true);

        // check apc for single transaction documents or multiple by card
        boolean singleTransaction = parameterService.getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, SINGLE_TRANSACTION_IND_PARM_NM);

        List documentTransactions = new ArrayList();
        if (singleTransaction) {
            for (Iterator iter = transactions.iterator(); iter.hasNext(); ) {
                documentTransactions.add(iter.next());
                groupedTransactions.add(documentTransactions);
                documentTransactions = new ArrayList();
            }
        } else {
            Map cardTransactionsMap = new HashMap();
            for (Iterator iter = transactions.iterator(); iter.hasNext(); ) {
                ProcurementCardTransaction transaction = (ProcurementCardTransaction) iter.next();
                if (!cardTransactionsMap.containsKey(transaction.getTransactionCreditCardNumber())) {
                    cardTransactionsMap.put(transaction.getTransactionCreditCardNumber(), new ArrayList());
                }
                ((List) cardTransactionsMap.get(transaction.getTransactionCreditCardNumber())).add(transaction);
            }

            for (Iterator iter = cardTransactionsMap.values().iterator(); iter.hasNext(); ) {
                groupedTransactions.add(iter.next());

            }
        }

        return groupedTransactions;
    }

    /**
     * Creates a ProcurementCardDocument from the List of transactions given.
     *
     * @param transactions List of ProcurementCardTransaction objects to be used for creating the document.
     * @return A ProcurementCardDocument populated with the transactions provided.
     */
    protected ProcurementCardDocument createProcurementCardDocument(List transactions) {
        ProcurementCardDocument pcardDocument = null;

        try {
            // get new document from doc service
            pcardDocument = (ProcurementCardDocument) SpringContext.getBean(DocumentService.class).getNewDocument(PROCUREMENT_CARD);

            List<CapitalAssetInformation> capitalAssets = pcardDocument.getCapitalAssetInformation();
            for (CapitalAssetInformation capitalAsset : capitalAssets) {
                if (ObjectUtils.isNotNull(capitalAsset) && ObjectUtils.isNotNull(capitalAsset.getCapitalAssetInformationDetails())) {
                    capitalAsset.setDocumentNumber(pcardDocument.getDocumentNumber());
                }
            }

            ProcurementCardTransaction trans = (ProcurementCardTransaction) transactions.get(0);
            String errors = validateTransaction(trans);
            createCardHolderRecord(pcardDocument, trans);

            // for each transaction, create transaction detail object and then acct lines for the detail
            int transactionLineNumber = 1;
            KualiDecimal documentTotalAmount = KualiDecimal.ZERO;
            String errorText = "";
            for (Iterator iter = transactions.iterator(); iter.hasNext(); ) {
                ProcurementCardTransaction transaction = (ProcurementCardTransaction) iter.next();

                // create transaction detail record with accounting lines
                errorText += createTransactionDetailRecord(pcardDocument, transaction, transactionLineNumber);

                // update document total
                documentTotalAmount = documentTotalAmount.add(transaction.getFinancialDocumentTotalAmount());

                transactionLineNumber++;
            }

            pcardDocument.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(documentTotalAmount);
            // PCDO Default Description
            //pcardDocument.getDocumentHeader().setDocumentDescription("SYSTEM Generated");
            setupDocumentDescription(pcardDocument);

            // Remove duplicate messages from errorText
            String messages[] = StringUtils.split(errorText, ".");
            for (int i = 0; i < messages.length; i++) {
                int countMatches = StringUtils.countMatches(errorText, messages[i]) - 1;
                errorText = StringUtils.replace(errorText, messages[i] + ".", "", countMatches);
            }
            // In case errorText is still too long, truncate it and indicate so.
            Integer documentExplanationMaxLength = dataDictionaryService.getAttributeMaxLength(DocumentHeader.class.getName(), KFSPropertyConstants.EXPLANATION);
            if (documentExplanationMaxLength != null && errorText.length() > documentExplanationMaxLength.intValue()) {
                String truncatedMessage = " ... TRUNCATED.";
                errorText = errorText.substring(0, documentExplanationMaxLength - truncatedMessage.length()) + truncatedMessage;
            }
            pcardDocument.getDocumentHeader().setExplanation(errorText);
        } catch (WorkflowException e) {
            LOG.error("Error creating pcdo documents: " + e.getMessage(), e);
            throw new RuntimeException("Error creating pcdo documents: " + e.getMessage(), e);
        }

        return pcardDocument;
    }

    /**
     * Set up PCDO document description as "cardholder name-card number (which is 4 digits)-chartOfAccountsCode-default account"
     *
     * @param pcardDocument
     */
    protected void setupDocumentDescription(ProcurementCardDocument pcardDocument) {
        ProcurementCardHolder cardHolder = pcardDocument.getProcurementCardHolder();

        if (ObjectUtils.isNotNull(cardHolder)) {
            String cardHolderName = StringUtils.left(cardHolder.getCardHolderName(), 23);
            String lastFourDigits = StringUtils.right(cardHolder.getTransactionCreditCardNumber(), 4);
            String chartOfAccountsCode = cardHolder.getChartOfAccountsCode();
            String accountNumber = cardHolder.getAccountNumber();

            String description = MessageFormat.format(DOCUMENT_DESCRIPTION_PATTERN, cardHolderName, lastFourDigits, chartOfAccountsCode, accountNumber);
            pcardDocument.getDocumentHeader().setDocumentDescription(description);
        }
    }

    /**
     * Creates card holder record and sets that record to the document given.
     *
     * @param pcardDocument Procurement card document to place the record in.
     * @param transaction   The transaction to set the card holder record fields from.
     */
    protected void createCardHolderRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction) {
        ProcurementCardHolder cardHolder = new ProcurementCardHolder();

        cardHolder.setDocumentNumber(pcardDocument.getDocumentNumber());
        cardHolder.setTransactionCreditCardNumber(transaction.getTransactionCreditCardNumber());

        final ProcurementCardDefault procurementCardDefault = retrieveProcurementCardDefault(transaction.getTransactionCreditCardNumber());
        if (procurementCardDefault != null) {
            if (parameterService.getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_CARD_HOLDER_DEFAULT_PARAMETER_NAME)) {
                cardHolder.setCardCycleAmountLimit(procurementCardDefault.getCardCycleAmountLimit());
                cardHolder.setCardCycleVolumeLimit(procurementCardDefault.getCardCycleVolumeLimit());
                cardHolder.setCardHolderAlternateName(procurementCardDefault.getCardHolderAlternateName());
                cardHolder.setCardHolderCityName(procurementCardDefault.getCardHolderCityName());
                cardHolder.setCardHolderLine1Address(procurementCardDefault.getCardHolderLine1Address());
                cardHolder.setCardHolderLine2Address(procurementCardDefault.getCardHolderLine2Address());
                cardHolder.setCardHolderName(procurementCardDefault.getCardHolderName());
                cardHolder.setCardHolderStateCode(procurementCardDefault.getCardHolderStateCode());
                cardHolder.setCardHolderWorkPhoneNumber(procurementCardDefault.getCardHolderWorkPhoneNumber());
                cardHolder.setCardHolderZipCode(procurementCardDefault.getCardHolderZipCode());
                cardHolder.setCardLimit(procurementCardDefault.getCardLimit());
                cardHolder.setCardNoteText(procurementCardDefault.getCardNoteText());
                cardHolder.setCardStatusCode(procurementCardDefault.getCardStatusCode());
            }
            if (parameterService.getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME)) {
                cardHolder.setChartOfAccountsCode(procurementCardDefault.getChartOfAccountsCode());
                cardHolder.setAccountNumber(procurementCardDefault.getAccountNumber());
                cardHolder.setSubAccountNumber(procurementCardDefault.getSubAccountNumber());
            }
        }

        if (StringUtils.isEmpty(cardHolder.getAccountNumber())) {
            cardHolder.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
            cardHolder.setAccountNumber(transaction.getAccountNumber());
            cardHolder.setSubAccountNumber(transaction.getSubAccountNumber());
        }
        if (StringUtils.isEmpty(cardHolder.getCardHolderName())) {
            cardHolder.setCardCycleAmountLimit(transaction.getCardCycleAmountLimit());
            cardHolder.setCardCycleVolumeLimit(transaction.getCardCycleVolumeLimit());
            cardHolder.setCardHolderAlternateName(transaction.getCardHolderAlternateName());
            cardHolder.setCardHolderCityName(transaction.getCardHolderCityName());
            cardHolder.setCardHolderLine1Address(transaction.getCardHolderLine1Address());
            cardHolder.setCardHolderLine2Address(transaction.getCardHolderLine2Address());
            cardHolder.setCardHolderName(transaction.getCardHolderName());
            cardHolder.setCardHolderStateCode(transaction.getCardHolderStateCode());
            cardHolder.setCardHolderWorkPhoneNumber(transaction.getCardHolderWorkPhoneNumber());
            cardHolder.setCardHolderZipCode(transaction.getCardHolderZipCode());
            cardHolder.setCardLimit(transaction.getCardLimit());
            cardHolder.setCardNoteText(transaction.getCardNoteText());
            cardHolder.setCardStatusCode(transaction.getCardStatusCode());
        }

        pcardDocument.setProcurementCardHolder(cardHolder);
    }

    /**
     * Creates a transaction detail record and adds that record to the document provided.
     *
     * @param pcardDocument         Document to place record in.
     * @param transaction           Transaction to set fields from.
     * @param transactionLineNumber Line number of the new transaction detail record within the procurement card document.
     * @return The error text that was generated from the creation of the detail records.  If the text is empty, no errors were encountered.
     */
    protected String createTransactionDetailRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, Integer transactionLineNumber) {
        ProcurementCardTransactionDetail transactionDetail = new ProcurementCardTransactionDetail();

        // set the document transaction detail fields from the loaded transaction record
        transactionDetail.setDocumentNumber(pcardDocument.getDocumentNumber());
        transactionDetail.setFinancialDocumentTransactionLineNumber(transactionLineNumber);
        transactionDetail.setTransactionDate(transaction.getTransactionDate());
        transactionDetail.setTransactionReferenceNumber(transaction.getTransactionReferenceNumber());
        transactionDetail.setTransactionBillingCurrencyCode(transaction.getTransactionBillingCurrencyCode());
        transactionDetail.setTransactionCurrencyExchangeRate(transaction.getTransactionCurrencyExchangeRate());
        transactionDetail.setTransactionDate(transaction.getTransactionDate());
        transactionDetail.setTransactionOriginalCurrencyAmount(transaction.getTransactionOriginalCurrencyAmount());
        transactionDetail.setTransactionOriginalCurrencyCode(transaction.getTransactionOriginalCurrencyCode());
        transactionDetail.setTransactionPointOfSaleCode(transaction.getTransactionPointOfSaleCode());
        transactionDetail.setTransactionPostingDate(transaction.getTransactionPostingDate());
        transactionDetail.setTransactionPurchaseIdentifierDescription(transaction.getTransactionPurchaseIdentifierDescription());
        transactionDetail.setTransactionPurchaseIdentifierIndicator(transaction.getTransactionPurchaseIdentifierIndicator());
        transactionDetail.setTransactionSalesTaxAmount(transaction.getTransactionSalesTaxAmount());
        transactionDetail.setTransactionSettlementAmount(transaction.getTransactionSettlementAmount());
        transactionDetail.setTransactionTaxExemptIndicator(transaction.getTransactionTaxExemptIndicator());
        transactionDetail.setTransactionTravelAuthorizationCode(transaction.getTransactionTravelAuthorizationCode());
        transactionDetail.setTransactionUnitContactName(transaction.getTransactionUnitContactName());

        if (GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
            transactionDetail.setTransactionTotalAmount(transaction.getFinancialDocumentTotalAmount().negated());
        } else {
            transactionDetail.setTransactionTotalAmount(transaction.getFinancialDocumentTotalAmount());
        }

        // create transaction vendor record
        createTransactionVendorRecord(pcardDocument, transaction, transactionDetail);

        // add transaction detail to document
        pcardDocument.getTransactionEntries().add(transactionDetail);

        // now create the initial source and target lines for this transaction
        return createAndValidateAccountingLines(pcardDocument, transaction, transactionDetail);
    }


    /**
     * Creates a transaction vendor detail record and adds it to the transaction detail.
     *
     * @param pcardDocument     The procurement card document to retrieve values from.
     * @param transaction       Transaction to set fields from.
     * @param transactionDetail The transaction detail to set the vendor record on.
     */
    protected void createTransactionVendorRecord(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail transactionDetail) {
        ProcurementCardVendor transactionVendor = new ProcurementCardVendor();

        transactionVendor.setDocumentNumber(pcardDocument.getDocumentNumber());
        transactionVendor.setFinancialDocumentTransactionLineNumber(transactionDetail.getFinancialDocumentTransactionLineNumber());
        transactionVendor.setTransactionMerchantCategoryCode(transaction.getTransactionMerchantCategoryCode());
        transactionVendor.setVendorCityName(transaction.getVendorCityName());
        transactionVendor.setVendorLine1Address(transaction.getVendorLine1Address());
        transactionVendor.setVendorLine2Address(transaction.getVendorLine2Address());
        transactionVendor.setVendorName(transaction.getVendorName());
        transactionVendor.setVendorOrderNumber(transaction.getVendorOrderNumber());
        transactionVendor.setVendorStateCode(transaction.getVendorStateCode());
        transactionVendor.setVendorZipCode(transaction.getVendorZipCode());
        transactionVendor.setVisaVendorIdentifier(transaction.getVisaVendorIdentifier());

        transactionDetail.setProcurementCardVendor(transactionVendor);
    }

    /**
     * From the transaction accounting attributes, creates source and target accounting lines. Attributes are validated first, and
     * replaced with default and error values if needed. There will be 1 source and 1 target line generated.
     *
     * @param pcardDocument        The procurement card document to add the new accounting lines to.
     * @param transaction          The transaction to process into account lines.
     * @param docTransactionDetail The transaction detail to create source and target accounting lines from.
     * @return String containing any error messages.
     */
    protected String createAndValidateAccountingLines(ProcurementCardDocument pcardDocument, ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        // build source lines
        ProcurementCardSourceAccountingLine sourceLine = createSourceAccountingLine(transaction, docTransactionDetail);
        sourceLine.setPostingYear(pcardDocument.getPostingYear());

        // add line to transaction through document since document contains the next sequence number fields
        pcardDocument.addSourceAccountingLine(sourceLine);

        // build target lines
        ProcurementCardTargetAccountingLine targetLine = createTargetAccountingLine(transaction, docTransactionDetail);
        targetLine.setPostingYear(pcardDocument.getPostingYear());

        // add line to transaction through document since document contains the next sequence number fields
        pcardDocument.addTargetAccountingLine(targetLine);

        return validateTargetAccountingLine(targetLine);
    }

    /**
     * Creates the to record for the transaction. The chart of account attributes from the transaction are used to create
     * the accounting line.
     *
     * @param transaction          The transaction to pull information from to create the accounting line.
     * @param docTransactionDetail The transaction detail to pull information from to populate the accounting line.
     * @return The target accounting line fully populated with values from the parameters passed in.
     */
    protected ProcurementCardTargetAccountingLine createTargetAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardTargetAccountingLine targetLine = new ProcurementCardTargetAccountingLine();
        targetLine.setDocumentNumber(docTransactionDetail.getDocumentNumber());
        targetLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        targetLine.setChartOfAccountsCode(transaction.getChartOfAccountsCode());
        targetLine.setAccountNumber(transaction.getAccountNumber());
        targetLine.setFinancialObjectCode(transaction.getFinancialObjectCode());
        targetLine.setSubAccountNumber(transaction.getSubAccountNumber());
        targetLine.setFinancialSubObjectCode(transaction.getFinancialSubObjectCode());
        targetLine.setProjectCode(transaction.getProjectCode());

        if (parameterService.getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME)) {
            final ProcurementCardDefault procurementCardDefault = retrieveProcurementCardDefault(transaction.getTransactionCreditCardNumber());
            if (procurementCardDefault != null) {
                targetLine.setChartOfAccountsCode(procurementCardDefault.getChartOfAccountsCode());
                targetLine.setAccountNumber(procurementCardDefault.getAccountNumber());
                targetLine.setFinancialObjectCode(procurementCardDefault.getFinancialObjectCode());
                targetLine.setSubAccountNumber(procurementCardDefault.getSubAccountNumber());
                targetLine.setFinancialSubObjectCode(procurementCardDefault.getFinancialSubObjectCode());
                targetLine.setProjectCode(procurementCardDefault.getProjectCode());
            }
        }

        if (GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
            targetLine.setAmount(transaction.getFinancialDocumentTotalAmount().negated());
        } else {
            targetLine.setAmount(transaction.getFinancialDocumentTotalAmount());
        }

        return targetLine;
    }

    /**
     * Creates the from record for the transaction. The clearing chart, account, and object code is used for creating the line.
     *
     * @param transaction          The transaction to pull information from to create the accounting line.
     * @param docTransactionDetail The transaction detail to pull information from to populate the accounting line.
     * @return The source accounting line fully populated with values from the parameters passed in.
     */
    protected ProcurementCardSourceAccountingLine createSourceAccountingLine(ProcurementCardTransaction transaction, ProcurementCardTransactionDetail docTransactionDetail) {
        ProcurementCardSourceAccountingLine sourceLine = new ProcurementCardSourceAccountingLine();

        sourceLine.setDocumentNumber(docTransactionDetail.getDocumentNumber());
        sourceLine.setFinancialDocumentTransactionLineNumber(docTransactionDetail.getFinancialDocumentTransactionLineNumber());
        sourceLine.setChartOfAccountsCode(getDefaultChartCode());
        sourceLine.setAccountNumber(getDefaultAccountNumber());
        sourceLine.setFinancialObjectCode(getDefaultObjectCode());

        if (GL_CREDIT_CODE.equals(transaction.getTransactionDebitCreditCode())) {
            sourceLine.setAmount(transaction.getFinancialDocumentTotalAmount().negated());
        } else {
            sourceLine.setAmount(transaction.getFinancialDocumentTotalAmount());
        }

        return sourceLine;
    }

    /**
     * Validates the chart of account attributes for existence and active indicator. Will substitute for defined
     * default parameters or set fields to empty that if they have errors.
     *
     * @param targetLine The target accounting line to be validated.
     * @return String with error messages discovered during validation.  An empty string indicates no validation errors were found.
     */
    protected String validateTargetAccountingLine(ProcurementCardTargetAccountingLine targetLine) {
        String errorText = "";

        targetLine.refresh();
        final String lineNumber = targetLine.getSequenceNumber() == null ? "new" : targetLine.getSequenceNumber().toString();

        if (!accountingLineRuleUtil.isValidChart("", targetLine.getChart(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Target Accounting Line " + lineNumber + " Chart " + targetLine.getChartOfAccountsCode() + " is invalid; using error Chart Code.";
            if (LOG.isInfoEnabled()) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setChartOfAccountsCode(getErrorChartCode());
            targetLine.refresh();
        }

        if (!accountingLineRuleUtil.isValidAccount("", targetLine.getAccount(), dataDictionaryService.getDataDictionary()) || targetLine.getAccount().isExpired()) {
            String tempErrorText = "Target Accounting Line " + lineNumber + " Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " is invalid; using error account.";
            if (LOG.isInfoEnabled()) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setChartOfAccountsCode(getErrorChartCode());
            targetLine.setAccountNumber(getErrorAccountNumber());
            targetLine.refresh();
        }

        if (!accountingLineRuleUtil.isValidObjectCode("", targetLine.getObjectCode(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Target Accounting Line " + lineNumber + " Chart " + targetLine.getChartOfAccountsCode() + " Object Code " + targetLine.getFinancialObjectCode() + " is invalid; using default Object Code.";
            if (LOG.isInfoEnabled()) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setFinancialObjectCode(getDefaultObjectCode());
            targetLine.refresh();
        }

        if (StringUtils.isNotBlank(targetLine.getSubAccountNumber()) && !accountingLineRuleUtil.isValidSubAccount("", targetLine.getSubAccount(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Target Accounting Line " + lineNumber + " Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " Sub Account " + targetLine.getSubAccountNumber() + " is invalid; Setting Sub Account to blank.";
            if (LOG.isInfoEnabled()) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setSubAccountNumber("");
        }

        if (StringUtils.isNotBlank(targetLine.getFinancialSubObjectCode()) && !accountingLineRuleUtil.isValidSubObjectCode("", targetLine.getSubObjectCode(), dataDictionaryService.getDataDictionary())) {
            String tempErrorText = "Target Accounting Line " + lineNumber + " Chart " + targetLine.getChartOfAccountsCode() + " Account " + targetLine.getAccountNumber() + " Object Code " + targetLine.getFinancialObjectCode() + " Sub Object Code " + targetLine.getFinancialSubObjectCode() + " is invalid; setting Sub Object to blank.";
            if (LOG.isInfoEnabled()) {
                LOG.info(tempErrorText);
            }
            errorText += " " + tempErrorText;

            targetLine.setFinancialSubObjectCode("");
        }

        if (StringUtils.isNotBlank(targetLine.getProjectCode()) && !accountingLineRuleUtil.isValidProjectCode("", targetLine.getProject(), dataDictionaryService.getDataDictionary())) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Target Accounting Line " + lineNumber + " Project Code " + targetLine.getProjectCode() + " is invalid; setting to blank.");
            }
            errorText += " Target Accounting Line " + lineNumber + " Project Code " + targetLine.getProjectCode() + " is invalid; setting to blank.";

            targetLine.setProjectCode("");
        }

        // clear out GlobalVariable message map, since we have taken care of the errors
        GlobalVariables.setMessageMap(new MessageMap());

        return errorText;
    }

    /**
     * Validates the chart of account attributes for existence and active indicator. Will substitute for defined
     * default parameters or set fields to empty that if they have errors.
     *
     * @param transaction The transaction to be validated.
     * @return String with error messages discovered during validation.  An empty string indicates no validation errors were found.
     */
    protected String validateTransaction(ProcurementCardTransaction transaction) {
        String errorText = "";
        final String lineNumber = transaction.getTransactionSequenceRowNumber() == null ? "new" : transaction.getTransactionSequenceRowNumber().toString();

        if (parameterService.getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME)) {
            final ProcurementCardDefault procurementCardDefault = retrieveProcurementCardDefault(transaction.getTransactionCreditCardNumber());
            if (ObjectUtils.isNull(procurementCardDefault)) {
                final String tempErrorText = "Procurement Card Accounting Line Defaults are turned on but no Procurement Card Default record could be retrieved for transaction: " + transaction.getTransactionReferenceNumber() + " by card number.";
                if (LOG.isInfoEnabled()) {
                    LOG.info(tempErrorText);
                }
                errorText += " " + tempErrorText;
            }
        } else {
            transaction.refresh();

            final ChartService chartService = SpringContext.getBean(ChartService.class);
            if (transaction.getChartOfAccountsCode() == null || ObjectUtils.isNull(chartService.getByPrimaryId(transaction.getChartOfAccountsCode()))) {
                String tempErrorText = "Transaction " + lineNumber + " Chart " + transaction.getChartOfAccountsCode() + " is invalid; using error Chart Code.";
                if (LOG.isInfoEnabled()) {
                    LOG.info(tempErrorText);
                }
                errorText += " " + tempErrorText;
                transaction.setChartOfAccountsCode(getErrorChartCode());
                transaction.refresh();
            }

            final AccountService accountService = SpringContext.getBean(AccountService.class);
            if (transaction.getAccountNumber() == null || ObjectUtils.isNull(accountService.getByPrimaryIdWithCaching(transaction.getChartOfAccountsCode(), transaction.getAccountNumber())) || accountService.getByPrimaryIdWithCaching(transaction.getChartOfAccountsCode(), transaction.getAccountNumber()).isExpired()) {
                String tempErrorText = "Transaction " + lineNumber + " Chart " + transaction.getChartOfAccountsCode() + " Account " + transaction.getAccountNumber() + " is invalid; using error account.";
                if (LOG.isInfoEnabled()) {
                    LOG.info(tempErrorText);
                }
                errorText += " " + tempErrorText;
                transaction.setChartOfAccountsCode(getErrorChartCode());
                transaction.setAccountNumber(getErrorAccountNumber());
                transaction.refresh();
            }

            final UniversityDateService uds = SpringContext.getBean(UniversityDateService.class);
            final ObjectCodeService ocs = SpringContext.getBean(ObjectCodeService.class);
            if (transaction.getFinancialObjectCode() == null || ObjectUtils.isNull(ocs.getByPrimaryIdWithCaching(uds.getCurrentFiscalYear(), transaction.getChartOfAccountsCode(), transaction.getFinancialObjectCode()))) {
                String tempErrorText = "Transaction " + lineNumber + " Chart " + transaction.getChartOfAccountsCode() + " Object Code " + transaction.getFinancialObjectCode() + " is invalid; using default Object Code.";
                if (LOG.isInfoEnabled()) {
                    LOG.info(tempErrorText);
                }
                errorText += " " + tempErrorText;

                transaction.setFinancialObjectCode(getDefaultObjectCode());
                transaction.refresh();
            }

            if (StringUtils.isNotBlank(transaction.getSubAccountNumber())) {
                SubAccountService sas = SpringContext.getBean(SubAccountService.class);
                SubAccount subAccount = sas.getByPrimaryIdWithCaching(transaction.getChartOfAccountsCode(), transaction.getAccountNumber(), transaction.getSubAccountNumber());

                if (ObjectUtils.isNull(subAccount)) {
                    String tempErrorText = "Transaction " + lineNumber + " Chart " + transaction.getChartOfAccountsCode() + " Account " + transaction.getAccountNumber() + " Sub Account " + transaction.getSubAccountNumber() + " is invalid; Setting Sub Account to blank.";
                    if (LOG.isInfoEnabled()) {
                        LOG.info(tempErrorText);
                    }
                    errorText += " " + tempErrorText;

                    transaction.setSubAccountNumber("");
                }
            }

            if (StringUtils.isNotBlank(transaction.getFinancialSubObjectCode())) {

                SubObjectCodeService socs = SpringContext.getBean(SubObjectCodeService.class);
                SubObjectCode soc = socs.getByPrimaryIdForCurrentYear(transaction.getChartOfAccountsCode(), transaction.getAccountNumber(), transaction.getFinancialObjectCode(), transaction.getFinancialSubObjectCode());

                if (ObjectUtils.isNull(soc)) {
                    String tempErrorText = "Transaction " + lineNumber + " Chart " + transaction.getChartOfAccountsCode() + " Account " + transaction.getAccountNumber() + " Object Code " + transaction.getFinancialObjectCode() + " Sub Object Code " + transaction.getFinancialSubObjectCode() + " is invalid; setting Sub Object to blank.";
                    if (LOG.isInfoEnabled()) {
                        LOG.info(tempErrorText);
                    }
                    errorText += " " + tempErrorText;

                    transaction.setFinancialSubObjectCode("");
                }
            }

            if (StringUtils.isNotBlank(transaction.getProjectCode())) {

                ProjectCodeService pcs = SpringContext.getBean(ProjectCodeService.class);
                ProjectCode pc = pcs.getByPrimaryId(transaction.getProjectCode());

                if (ObjectUtils.isNull(pc)) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Transaction " + lineNumber + " Project Code " + transaction.getProjectCode() + " is invalid; setting to blank.");
                    }
                    errorText += " Transaction " + lineNumber + " Project Code " + transaction.getProjectCode() + " is invalid; setting to blank.";

                    transaction.setProjectCode("");
                }
            }
        }

        // clear out GlobalVariable message map, since we have taken care of the errors
        GlobalVariables.setMessageMap(new MessageMap());

        return errorText;
    }

    protected String getErrorChartCode() {
        return parameterService.getParameterValueAsString(ProcurementCardCreateDocumentsStep.class, ProcurementCardDocumentRuleConstants.ERROR_TRANS_CHART_CODE_PARM_NM);
    }

    protected String getErrorAccountNumber() {
        return parameterService.getParameterValueAsString(ProcurementCardCreateDocumentsStep.class, ERROR_TRANS_ACCOUNT_PARM_NM);
    }

    /**
     * Gets the default Chart Code, Account from the custom Procurement Cardholder table.
     */
    protected ProcurementCardDefault retrieveProcurementCardDefault(String creditCardNumber) {
        Map<String, String> fieldValues = new HashMap<>();
        fieldValues.put(KFSPropertyConstants.CREDIT_CARD_NUMBER, creditCardNumber);
        List<ProcurementCardDefault> matchingPcardDefaults = (List<ProcurementCardDefault>) businessObjectService.findMatching(ProcurementCardDefault.class, fieldValues);
        ProcurementCardDefault procurementCardDefault = null;
        if (!matchingPcardDefaults.isEmpty()) {
            procurementCardDefault = matchingPcardDefaults.get(0);
        }

        return procurementCardDefault;
    }

    protected String getDefaultChartCode() {
        return parameterService.getParameterValueAsString(ProcurementCardLoadStep.class, DEFAULT_TRANS_CHART_CODE_PARM_NM);
    }

    protected String getDefaultAccountNumber() {
        return parameterService.getParameterValueAsString(ProcurementCardLoadStep.class, DEFAULT_TRANS_ACCOUNT_PARM_NM);
    }

    protected String getDefaultObjectCode() {
        return parameterService.getParameterValueAsString(ProcurementCardLoadStep.class, DEFAULT_TRANS_OBJECT_CODE_PARM_NM);
    }

    /**
     * Calls businessObjectService to remove all the procurement card transaction rows from the transaction load table.
     */
    protected void cleanTransactionsTable() {
        businessObjectService.deleteMatching(ProcurementCardTransaction.class, new HashMap());
    }

    /**
     * Loads all the parsed XML transactions into the temp transaction table.
     *
     * @param transactions List of ProcurementCardTransactions to load.
     */
    protected void loadTransactions(List transactions) {
        businessObjectService.save(transactions);
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setAccountingLineRuleUtil(AccountingLineRuleHelperService accountingLineRuleUtil) {
        this.accountingLineRuleUtil = accountingLineRuleUtil;
    }

    public void setCapitalAssetManagementModuleService(CapitalAssetManagementModuleService capitalAssetManagementModuleService) {
        this.capitalAssetManagementModuleService = capitalAssetManagementModuleService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void setEmailTemplateUrl(String emailTemplateUrl) {
        this.emailTemplateUrl = emailTemplateUrl;
    }
}
