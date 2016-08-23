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
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsLookupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kew.impl.document.search.DocumentSearchCriteriaBo;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.kns.util.FieldUtils;
import org.kuali.kfs.kns.web.ui.Column;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.UrlFactory;

/**
 * Allows custom handling of Awards within the lookup framework.
 */
public class AwardLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected ContractsAndGrantsLookupService contractsAndGrantsLookupService;


    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getColumns()
     */
    @Override
    public List<Column> getColumns() {
        List<Column> columns =  super.getColumns();

        if (!getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            for(Iterator<Column> it = columns.iterator(); it.hasNext(); ) {
                Column column = it.next();
                if (getFieldsToIgnore().contains(column.getPropertyName())) {
                    it.remove();
                }
            }
        }

        return columns;
    }

    /**
     * Ignore fields that are specific to the Contracts & Grants Billing (CGB) enhancement
     * if CGB is disabled.
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
     */
    @Override
    protected void setRows() {
        List<String> lookupFieldNames = null;
        if (getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldNames = getBusinessObjectMetaDataService().getLookupableFieldNames(
                    getBusinessObjectClass());
        }
        if (lookupFieldNames == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }

        List<String> lookupFieldAttributeList = new ArrayList();
        for (String lookupFieldName: lookupFieldNames) {
            if (!getFieldsToIgnore().contains(lookupFieldName)) {
                lookupFieldAttributeList.add(lookupFieldName);
            }
        }

        // construct field object for each search attribute
        List fields = new ArrayList();

        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(),
                    getBusinessObjectClass());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }

        int numCols = getBusinessObjectDictionaryService().getLookupNumberOfColumns(this.getBusinessObjectClass());

        this.rows = FieldUtils.wrapFields(fields, numCols);
    }

    /**
     * If the Contracts & Grants Billing (CGB) enhancement is disabled, we don't want to
     * process sections only related to CGB.
     *
     * @return list of fields to ignore
     */
    protected List<String> getFieldsToIgnore() {
        List<String> fieldsToIgnore = new ArrayList<String>();

        if (!getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            fieldsToIgnore.add(CGPropertyConstants.LOOKUP_FUND_MGR_USER_ID_FIELD);
            fieldsToIgnore.add(CGPropertyConstants.AWARD_LOOKUP_PRIMARY_FUND_MGR_FUND_MGR_NAME);
            fieldsToIgnore.add(CGPropertyConstants.AwardFields.LAST_BILLED_DATE);
            fieldsToIgnore.add(CGPropertyConstants.AwardFields.BILLING_FREQUENCY_CODE);
            fieldsToIgnore.add(CGPropertyConstants.AwardFields.EXCLUDED_FROM_INVOICING);
            fieldsToIgnore.add(CGPropertyConstants.AwardFields.ADDITIONAL_FORMS_DESCRIPTION);
            fieldsToIgnore.add(CGPropertyConstants.AwardFields.ADDITIONAL_FORMS_REQUIRED_INDICATOR);
            fieldsToIgnore.add(CGPropertyConstants.AwardFields.MIN_INVOICE_AMOUNT);
            fieldsToIgnore.add(CGPropertyConstants.AwardFields.FUNDING_EXPIRATION_DATE);
        }

        return fieldsToIgnore;
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // perform the lookup on the project director and fund manager objects first
        if (contractsAndGrantsLookupService.setupSearchFields(fieldValues, CGPropertyConstants.LOOKUP_USER_ID_FIELD, CGPropertyConstants.AWARD_LOOKUP_UNIVERSAL_USER_ID_FIELD) &&
                contractsAndGrantsLookupService.setupSearchFields(fieldValues, CGPropertyConstants.LOOKUP_FUND_MGR_USER_ID_FIELD, CGPropertyConstants.AWARD_LOOKUP_FUND_MGR_UNIVERSAL_USER_ID_FIELD)) {
            return super.getSearchResultsHelper(fieldValues, unbounded);
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      List pkNames)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        if (allowsMaintenanceNewOrCopyAction()) {
            anchorHtmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
        }

        // only display invoice lookup URL if CGB is enabled
        if (getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            AnchorHtmlData invoiceUrl = getInvoicesLookupUrl(businessObject);
            anchorHtmlDataList.add(invoiceUrl);
        }

        return anchorHtmlDataList;
    }

    /**
     * This method adds a link to the look up FOR the invoices associated with a given Award.
     *
     * @param bo
     * @return
     */
    protected AnchorHtmlData getInvoicesLookupUrl(BusinessObject bo) {
        Award award = (Award) bo;
        Properties params = new Properties();
        params.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
        params.put(KFSConstants.DOC_FORM_KEY, "88888888");
        params.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, "false");
        params.put(KFSPropertyConstants.DOCUMENT_TYPE_NAME, getAccountsReceivableModuleBillingService().getContractsGrantsInvoiceDocumentType());
        params.put(CGPropertyConstants.AWARD_INVOICE_LINK_PROPOSAL_NUMBER_PATH, award.getProposalNumber().toString());
        params.put(KFSConstants.RETURN_LOCATION_PARAMETER, "portal.do");
        params.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, DocumentSearchCriteriaBo.class.getName());
        String url = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, params);
        return new AnchorHtmlData(url, KFSConstants.SEARCH_METHOD, "View Invoices");
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }

    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

    public ContractsAndGrantsLookupService getContractsAndGrantsLookupService() {
        return contractsAndGrantsLookupService;
    }

    public void setContractsAndGrantsLookupService(ContractsAndGrantsLookupService contractsAndGrantsLookupService) {
        this.contractsAndGrantsLookupService = contractsAndGrantsLookupService;
    }

}
