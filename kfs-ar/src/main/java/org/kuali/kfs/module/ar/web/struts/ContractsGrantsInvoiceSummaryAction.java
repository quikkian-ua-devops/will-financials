/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceLookupResult;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsLookupResultRow;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.SegmentedLookupResultsService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.kns.util.KNSGlobalVariables;
import org.kuali.kfs.kns.web.ui.Column;
import org.kuali.kfs.kns.web.ui.ResultRow;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;

/**
 * Action class for Contracts & Grants Invoice Summary.
 */
public class ContractsGrantsInvoiceSummaryAction extends ContractsGrantsBillingSummaryActionBase {
    private static volatile ContractsGrantsInvoiceReportService contractsGrantsInvoiceReportService;
    private static volatile SegmentedLookupResultsService segmentedLookupResultsService;

    /**
     * 1. This method passes the control from Contracts & Grants Invoice lookup to the Contracts & Grants Invoice
     * Summary page. 2. Retrieves the list of selected awards by agency for creating invoices.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward viewSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContractsGrantsInvoiceSummaryForm contractsGrantsInvoiceSummaryForm = (ContractsGrantsInvoiceSummaryForm) form;
        String lookupResultsSequenceNumber = contractsGrantsInvoiceSummaryForm.getLookupResultsSequenceNumber();
        if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
            String personId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
            Collection<ContractsGrantsInvoiceLookupResult> contractsGrantsInvoiceLookupResults = getContractsGrantsInvoiceResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);

            contractsGrantsInvoiceSummaryForm.setContractsGrantsInvoiceLookupResults(contractsGrantsInvoiceLookupResults);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method would create invoices for the list of awards. It calls the batch process to reuse the functionality to create the
     * invoices.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward createInvoices(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ContractsGrantsInvoiceSummaryForm contractsGrantsInvoiceSummaryForm = (ContractsGrantsInvoiceSummaryForm) form;
        ContractsGrantsInvoiceCreateDocumentService cgInvoiceDocumentCreateService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        Person person = GlobalVariables.getUserSession().getPerson();
        String lookupResultsSequenceNumber = "";
        String parameterName = (String) request.getAttribute(KRADConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            lookupResultsSequenceNumber = StringUtils.substringBetween(parameterName, ".number", ".");
        }

        Collection<ContractsGrantsInvoiceLookupResult> lookupResults = getContractsGrantsInvoiceResultsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        // To retrieve the batch file directory name as "reports/cg"
        ModuleConfiguration systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-AR").getModuleConfiguration();

        String destinationFolderPath = StringUtils.EMPTY;
        List<String> batchFileDirectories = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories();

        if ( CollectionUtils.isNotEmpty(batchFileDirectories)){
            destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);
        }

        String runtimeStamp = getDateTimeService().toDateTimeStringForFilename(new java.util.Date());
        contractsGrantsInvoiceSummaryForm.setAwardInvoiced(true);
        int validationErrors = 0;
        int validAwards = 0;

        // Create Invoices from list of Awards.
        List<ErrorMessage> errorMessages = null;
        for (ContractsGrantsInvoiceLookupResult contractsGrantsInvoiceLookupResult : lookupResults) {
            Collection<ContractsAndGrantsBillingAward> awards = contractsGrantsInvoiceLookupResult.getAwards();
            Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();
            awards = cgInvoiceDocumentCreateService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());
            validationErrors += contractsGrantsInvoiceDocumentErrorLogs.size();
            validAwards += awards.size();
            if (awards.size() > 0) {
                errorMessages = cgInvoiceDocumentCreateService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);
            }
        }
        if (validationErrors > 0) {
            // At a minimum, show users a message that errors occurred, check report for details.
            KNSGlobalVariables.getMessageList().add(ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_AWARDS_INVALID);
        }
        if (validAwards > 0) {
            KNSGlobalVariables.getMessageList().add(ArKeyConstants.ContractsGrantsInvoiceConstants.MESSAGE_CONTRACTS_GRANTS_INVOICE_BATCH_SENT);
        }

        if (ObjectUtils.isNotNull(errorMessages)) {
            KNSGlobalVariables.getMessageList().addAll(errorMessages);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     *
     * @param lookupResultsSequenceNumber
     * @param personId
     * @return
     * @throws Exception
     */
    protected Collection<ContractsGrantsInvoiceLookupResult> getContractsGrantsInvoiceResultsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) throws Exception {
        return getContractsGrantsInvoiceReportService().getPopulatedContractsGrantsInvoiceLookupResults(getAwardsFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId));
    }

    /**
     * Get the Awards based on what the user selected in the lookup results.
     *
     * @param lookupResultsSequenceNumber sequence number used to retrieve the lookup results
     * @param personId person who performed the lookup
     * @return Collection of ContractsAndGrantsBillingAwards
     * @throws Exception if there was a problem getting the selected proposal numbers from the lookup results
     */
    protected Collection<ContractsAndGrantsBillingAward> getAwardsFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) throws Exception {
        KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        Collection<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        List<String> selectedProposalNumbers = getSelectedProposalNumbersFromLookupResultsSequenceNumber(lookupResultsSequenceNumber, personId);
        ContractsAndGrantsBillingAward award = null;

        for (String selectedProposalNumber: selectedProposalNumbers) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, selectedProposalNumber);
            award = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
            if (ObjectUtils.isNotNull(award)) {
                awards.add(award);
            }
        }

        return awards;
    }

    /**
     * Get the selected proposal numbers to be used as keys to retrieve the awards by retrieving the selected object ids
     * and then matching them against the results from the lookup.
     *
     * @param lookupResultsSequenceNumber sequence number used to retrieve the lookup results and selected object ids
     * @param personId person who performed the lookup
     * @return List of proposalNumber Strings that the user selected
     * @throws Exception if there was a problem getting the selected object ids or the lookup results
     */
    protected List getSelectedProposalNumbersFromLookupResultsSequenceNumber(String lookupResultsSequenceNumber, String personId) throws Exception {
        List<String> selectedProposalNumbers = new ArrayList<String>();
        Set<String> selectedIds = getSegmentedLookupResultsService().retrieveSetOfSelectedObjectIds(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        if (CollectionUtils.isNotEmpty(selectedIds)) {
            List<ResultRow> results = getLookupResultsService().retrieveResultsTable(lookupResultsSequenceNumber, personId);
            for (ResultRow result:results) {
                List<Column> columns = result.getColumns();
                if (result instanceof ContractsGrantsLookupResultRow) {
                    for (ResultRow subResultRow : ((ContractsGrantsLookupResultRow) result).getSubResultRows()) {
                        String objId = subResultRow.getObjectId();
                        if (selectedIds.contains(objId)) {
                            // This is somewhat brittle - it depends on the fact that the Proposal Number is one of
                            // the columns in the sub result rows. If that changes, this will no longer work and will
                            // need to be changed.
                            for (Column column: subResultRow.getColumns()) {
                                if (StringUtils.equals(column.getPropertyName(), KFSPropertyConstants.PROPOSAL_NUMBER)) {
                                    selectedProposalNumbers.add(subResultRow.getColumns().get(0).getPropertyValue());
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }

        return selectedProposalNumbers;
    }

    /**
     * To cancel the document, invoices are not created when the cancel method is called.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_CANCEL);
    }

    public static ContractsGrantsInvoiceReportService getContractsGrantsInvoiceReportService() {
        if (contractsGrantsInvoiceReportService == null) {
            contractsGrantsInvoiceReportService = SpringContext.getBean(ContractsGrantsInvoiceReportService.class);
        }
        return contractsGrantsInvoiceReportService;
    }

    public static SegmentedLookupResultsService getSegmentedLookupResultsService() {
        if (segmentedLookupResultsService == null) {
            segmentedLookupResultsService = SpringContext.getBean(SegmentedLookupResultsService.class);
        }
        return segmentedLookupResultsService;
    }
}
