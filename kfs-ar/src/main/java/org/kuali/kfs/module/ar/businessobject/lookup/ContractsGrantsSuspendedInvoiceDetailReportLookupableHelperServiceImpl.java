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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceDetailReport;
import org.kuali.kfs.module.ar.businessobject.InvoiceSuspensionCategory;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.kns.web.comparator.CellComparatorHelper;
import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.kns.web.ui.Column;
import org.kuali.kfs.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;

/**
 * Defines a lookupable helper class for Suspended Invoice Detail Report.
 */
public class ContractsGrantsSuspendedInvoiceDetailReportLookupableHelperServiceImpl extends ContractsGrantsSuspendedInvoiceReportLookupableHelperServiceImplBase {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsSuspendedInvoiceDetailReportLookupableHelperServiceImpl.class);
    protected PersonService personService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        List<ContractsGrantsSuspendedInvoiceDetailReport> displayList = new ArrayList<ContractsGrantsSuspendedInvoiceDetailReport>();

        Map<String, String> invoiceDocumentCriteria = new HashMap<>();

        final String suspensionCategoryCode = (String)lookupFormFields.get(ArPropertyConstants.SuspensionCategoryReportFields.SUSPENSION_CATEGORY_CODE);
        if (StringUtils.isNotBlank(suspensionCategoryCode)) {
            invoiceDocumentCriteria.put(ArPropertyConstants.SuspensionCategoryReportFields.CONTRACTS_GRANTS_INVOICE_DOCUMENT_SUSPENSION_CATEGORY_CODE, suspensionCategoryCode);
        }

        final String documentNumber = (String)lookupFormFields.get(KFSPropertyConstants.DOCUMENT_NUMBER);

        final List<? extends ContractsAndGrantsAward> matchingAwards = lookupMatchingAwards(lookupFormFields);
        if (matchingAwards != null) { // null means that no award-based criteria were used in the search and therefore, these values should not be used for document selection
            if (matchingAwards.isEmpty()) { //we searched on awards, but didn't find any.  So we can't find any matching documents
                return displayList;
            }

            final String proposalNumbers = harvestIdsFromAwards(matchingAwards);
            if (!StringUtils.isBlank(proposalNumbers)) {
                invoiceDocumentCriteria.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, proposalNumbers);
            }
        }

        final String processingDocumentStatuses = buildProcessingDocumentStatusesForLookup();
        invoiceDocumentCriteria.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, processingDocumentStatuses);
        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments = getLookupService().findCollectionBySearchHelper(ContractsGrantsInvoiceDocument.class, invoiceDocumentCriteria, true);

        for (ContractsGrantsInvoiceDocument cgInvoiceDoc : cgInvoiceDocuments) {
            if (!ObjectUtils.isNull(cgInvoiceDoc.getInvoiceSuspensionCategories()) && !cgInvoiceDoc.getInvoiceSuspensionCategories().isEmpty()) { // only report on documents which have suspension categories associated
                if (StringUtils.isBlank(documentNumber) || StringUtils.equals(documentNumber, cgInvoiceDoc.getDocumentNumber())) {
                    for (InvoiceSuspensionCategory invoiceSuspensionCategory : cgInvoiceDoc.getInvoiceSuspensionCategories()) {
                        Pattern suspensionCategoryCodePattern = null;
                        if (!StringUtils.isBlank(suspensionCategoryCode)) {
                            suspensionCategoryCodePattern = Pattern.compile(suspensionCategoryCode.replace("*", ".*"), Pattern.CASE_INSENSITIVE);
                        }

                        if (StringUtils.isBlank(suspensionCategoryCode) ||
                                (suspensionCategoryCodePattern != null && suspensionCategoryCodePattern.matcher(invoiceSuspensionCategory.getSuspensionCategoryCode()).matches())) {
                            ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReport = new ContractsGrantsSuspendedInvoiceDetailReport();
                            cgSuspendedInvoiceDetailReport.setSuspensionCategoryCode(invoiceSuspensionCategory.getSuspensionCategoryCode());
                            cgSuspendedInvoiceDetailReport.setDocumentNumber(cgInvoiceDoc.getDocumentNumber());
                            cgSuspendedInvoiceDetailReport.setLetterOfCreditFundGroupCode(null);
                            if (ObjectUtils.isNotNull(cgInvoiceDoc.getInvoiceGeneralDetail().getAward())) {
                                if (ObjectUtils.isNotNull(cgInvoiceDoc.getInvoiceGeneralDetail().getAward().getLetterOfCreditFund())) {
                                    cgSuspendedInvoiceDetailReport.setLetterOfCreditFundGroupCode(cgInvoiceDoc.getInvoiceGeneralDetail().getAward().getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
                                }
                            }
                            ContractsAndGrantsBillingAward award = cgInvoiceDoc.getInvoiceGeneralDetail().getAward();
                            if (award != null) {
                                Person fundManager = award.getAwardPrimaryFundManager().getFundManager();
                                String fundManagerPrincipalName = fundManager.getPrincipalName();
                                Person projectDirector = award.getAwardPrimaryProjectDirector().getProjectDirector();
                                String projectDirectorPrincipalName = projectDirector.getPrincipalName();
                                cgSuspendedInvoiceDetailReport.setAwardFundManager(fundManager);
                                cgSuspendedInvoiceDetailReport.setAwardProjectDirector(projectDirector);
                                cgSuspendedInvoiceDetailReport.setProjectDirectorPrincipalName(projectDirectorPrincipalName);
                                cgSuspendedInvoiceDetailReport.setFundManagerPrincipalName(fundManagerPrincipalName);
                                cgSuspendedInvoiceDetailReport.setAwardTotal(award.getAwardTotalAmount());
                            }

                            if (!displayList.contains(cgSuspendedInvoiceDetailReport)) {
                                displayList.add(cgSuspendedInvoiceDetailReport);
                            }
                        }
                    }
                }
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    /**
     * Substitutes the fields on the lookup to match what the ORM is actually expecting
     * @param lookupFields the lookup fields to substitute column names for prior to the lookup
     */
    protected List<? extends ContractsAndGrantsAward> lookupMatchingAwards(@SuppressWarnings("rawtypes") Map lookupFields) {
        Map<String, String> awardLookupFields = new HashMap<String, String>();
        List<ContractsAndGrantsAward> filteredAwards = new ArrayList<ContractsAndGrantsAward>();

        // letterOfCreditFundGroupCode should be award.letterOfCreditFund.letterOfCreditFundGroupCode
        final String letterOfCreditFundGroupCode = (String)lookupFields.get(ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE);
        if (!StringUtils.isBlank(letterOfCreditFundGroupCode)) {
            awardLookupFields.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND+"."+ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE, letterOfCreditFundGroupCode);
        }

        // awardTotal should be award.awardTotalAmount
        final String awardTotal = (String)lookupFields.get(ArConstants.AWARD_TOTAL);
        if (!StringUtils.isBlank(awardTotal)) {
            awardLookupFields.put(ArConstants.AWARD_TOTAL, awardTotal);
        }

        // awardFundManager.principalName should be award.awardPrimaryFundManager.fundManager.principalId
        final String fundManagerPrincipalName = (String)lookupFields.get(ArConstants.AWARD_FUND_MANAGER+"."+KimConstants.UniqueKeyConstants.PRINCIPAL_NAME);
        final Set<String> fundManagerPrincipalIds = getContractsGrantsReportHelperService().lookupPrincipalIds(fundManagerPrincipalName);
        if (StringUtils.isNotBlank(fundManagerPrincipalName)) {
            if (fundManagerPrincipalIds.isEmpty()) {
                // no fund manager found, invalid name, return empty list to get no records found message
                return filteredAwards;
            } else {
                final String joinedFundManagerPrincipalIds = StringUtils.join(fundManagerPrincipalIds, SearchOperator.OR.op());
                awardLookupFields.put(ArConstants.AWARD_FUND_MANAGERS+"."+KFSPropertyConstants.PRINCIPAL_ID, joinedFundManagerPrincipalIds);
            }
        }

        // awardProjectDirector.principalName should be award.awardPrimaryProjectDirector.projectDirector.principalId
        final String projectDirectorPrincipalName = (String)lookupFields.get(ArConstants.AWARD_PROJECT_DIRECTOR+"."+KimConstants.UniqueKeyConstants.PRINCIPAL_NAME);
        final Set<String> projectDirectorPrincipalIds = getContractsGrantsReportHelperService().lookupPrincipalIds(projectDirectorPrincipalName);
        if (StringUtils.isNotBlank(projectDirectorPrincipalName)) {
            if (projectDirectorPrincipalIds.isEmpty()) {
                // no fund manager found, invalid name, return empty list to get no records found message
                return filteredAwards;
            } else {
                final String joinedProjectDirectorPrincipalIds = StringUtils.join(projectDirectorPrincipalIds, SearchOperator.OR.op());
                awardLookupFields.put(ArConstants.AWARD_PROJECT_DIRECTORS+"."+KFSPropertyConstants.PRINCIPAL_ID, joinedProjectDirectorPrincipalIds);
            }
        }

        if (awardLookupFields.isEmpty()) {
            return null; // nothing to lookup, so send back null to signal that we shouldn't even filter based on awards
        }

        // add active check
        awardLookupFields.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        final List<? extends ContractsAndGrantsAward> awards = getContractsAndGrantsModuleBillingService().lookupAwards(awardLookupFields, true);
        return awards;
    }

    /**
     * Harvests the proposal ids from the given awards and builds a lookup ready String of proposal id's
     * @param awards the awards to harvest ids from
     * @return a lookup ready String of proposal id's, or'd together
     */
    protected String harvestIdsFromAwards(List<? extends ContractsAndGrantsAward> awards) {
        if (awards.isEmpty()) {
            return KFSConstants.EMPTY_STRING;
        }

        Set<String> proposalIdsSet = new HashSet<String>();
        for (ContractsAndGrantsAward award : awards) {
            proposalIdsSet.add(award.getProposalNumber().toString());
        }
        final String proposalIdsForLookup = StringUtils.join(proposalIdsSet, SearchOperator.OR.op());
        return proposalIdsForLookup;
    }

    @Override
    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;
        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                col.setPropertyValue(propValue);

                // Add url when property is documentNumber
                if (col.getPropertyName().equals(KFSPropertyConstants.DOCUMENT_NUMBER)) {
                    String url = contractsGrantsReportHelperService.getDocSearchUrl(propValue);

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.DOCUMENT_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(ContractsGrantsInvoiceReport.class), ContractsGrantsInvoiceReport.class, fieldList));

                    col.setColumnAnchor(a);
                }
            }

            ResultRow row = new ResultRow(columns, "", ACTION_URLS_EMPTY);

            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            boolean isRowReturnable = isResultReturnable(element);
            row.setRowReturnable(isRowReturnable);
            if (isRowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }
}
