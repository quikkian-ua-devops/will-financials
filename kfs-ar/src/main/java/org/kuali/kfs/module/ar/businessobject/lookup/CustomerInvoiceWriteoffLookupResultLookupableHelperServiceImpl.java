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
package org.kuali.kfs.module.ar.businessobject.lookup;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.web.comparator.CellComparatorHelper;
import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.kns.web.ui.Column;
import org.kuali.kfs.kns.web.ui.ResultRow;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.krad.util.BeanPropertyComparator;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsLookupResultRow;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.BusinessObject;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomerInvoiceWriteoffLookupResultLookupableHelperServiceImpl extends AccountsReceivableLookupableHelperServiceImplBase {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceWriteoffLookupResultLookupableHelperServiceImpl.class);
    protected CustomerInvoiceWriteoffDocumentService customerInvoiceWriteoffDocumentService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return KRAD Conversion: Lookupable performs customization of the display results.
     * <p>
     * No use of data dictionary.
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection<BusinessObject> displayList = (Collection<BusinessObject>) getSearchResults(lookupForm.getFieldsForLookup());

        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        List returnKeys = getReturnKeys();
        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (BusinessObject element : displayList) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Doing lookup for " + element.getClass());
            }

            CustomerInvoiceWriteoffLookupResult result = ((CustomerInvoiceWriteoffLookupResult) element);
            List<String> customerInvoiceDocumentAttributesForDisplay = result.getCustomerInvoiceDocumentAttributesForDisplay();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(result, user);
            //add list of customer invoice document sub rows
            List<ResultRow> subResultRows = new ArrayList<ResultRow>();
            for (CustomerInvoiceDocument customerInvoiceDocument : result.getCustomerInvoiceDocuments()) {

                List<Column> subResultColumns = new ArrayList<Column>();

                for (String propertyName : customerInvoiceDocumentAttributesForDisplay) {
                    subResultColumns.add(setupResultsColumn(customerInvoiceDocument, propertyName, businessObjectRestrictions));
                }

                ResultRow subResultRow = new ResultRow(subResultColumns, "", "");
                subResultRow.setObjectId(customerInvoiceDocument.getObjectId());
                subResultRows.add(subResultRow);
            }

            //create main customer header row
            Collection<Column> columns = getColumns(element, businessObjectRestrictions);
            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);
            ContractsGrantsLookupResultRow row = new ContractsGrantsLookupResultRow((List<Column>) columns, subResultRows,
                returnUrl.constructCompleteHtmlTag(), getActionUrls(element, pkNames, businessObjectRestrictions));
            resultTable.add(row);
        }

        return displayList;
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        Collection searchResultsCollection = customerInvoiceWriteoffDocumentService.getCustomerInvoiceDocumentsForInvoiceWriteoffLookup(fieldValues);

        return this.buildSearchResultList(searchResultsCollection, new Long(searchResultsCollection.size()));
    }

    /**
     * build the search result list from the given collection and the number of all qualified search results
     *
     * @param searchResultsCollection the given search results, which may be a subset of the qualified search results
     * @param actualSize              the number of all qualified search results
     * @return the serach result list with the given results and actual size
     */
    protected List buildSearchResultList(Collection searchResultsCollection, Long actualSize) {
        CollectionIncomplete results = new CollectionIncomplete(searchResultsCollection, actualSize);

        // sort list if default sort column given
        List searchResults = results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    /**
     * @param element
     * @param attributeName
     * @return Column
     * <p>
     * KRAD Conversion: setup up the results column in the display results set.
     * <p>
     * No use of data dictionary.
     */
    protected Column setupResultsColumn(BusinessObject element, String attributeName, BusinessObjectRestrictions businessObjectRestrictions) {
        Column col = new Column();

        col.setPropertyName(attributeName);


        String columnTitle = getDataDictionaryService().getAttributeLabel(element.getClass(), attributeName);
        if (StringUtils.isBlank(columnTitle)) {
            columnTitle = getDataDictionaryService().getCollectionLabel(element.getClass(), attributeName);
        }
        col.setColumnTitle(columnTitle);
        col.setMaxLength(getDataDictionaryService().getAttributeMaxLength(element.getClass(), attributeName));

        try {
            Class formatterClass = getDataDictionaryService().getAttributeFormatter(element.getClass(), attributeName);
            Formatter formatter = null;
            if (formatterClass != null) {
                formatter = (Formatter) formatterClass.newInstance();
                col.setFormatter(formatter);
            }

            // pick off result column from result list, do formatting
            String propValue = KFSConstants.EMPTY_STRING;
            Object prop = ObjectUtils.getPropertyValue(element, attributeName);

            // set comparator and formatter based on property type
            Class propClass = null;
            PropertyDescriptor propDescriptor = PropertyUtils.getPropertyDescriptor(element, col.getPropertyName());
            if (propDescriptor != null) {
                propClass = propDescriptor.getPropertyType();
            }

            // formatters
            if (prop != null) {
                propValue = getContractsGrantsReportHelperService().formatByType(prop, formatter);
            }

            // comparator
            col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
            col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));
            propValue = super.maskValueIfNecessary(element, col.getPropertyName(), propValue, businessObjectRestrictions);
            col.setPropertyValue(propValue);

            if (StringUtils.isNotBlank(propValue)) {
                col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
            }
        } catch (InstantiationException ie) {
            throw new RuntimeException("Unable to get new instance of formatter class for property " + col.getPropertyName(), ie);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", ex);
        }
        return col;
    }


    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     *
     * @param bo
     * @return Collection<Column>
     * <p>
     * KRAD Conversion: Gets column names.
     * <p>
     * Data dictionary is using the bo to look up the attribute names for the columns.
     */
    protected Collection<Column> getColumns(BusinessObject bo, BusinessObjectRestrictions businessObjectRestrictions) {
        Collection<Column> columns = new ArrayList<Column>();

        for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(bo.getClass())) {
            columns.add(setupResultsColumn(bo, attributeName, businessObjectRestrictions));
        }
        return columns;
    }

    public CustomerInvoiceWriteoffDocumentService getCustomerInvoiceWriteoffDocumentService() {
        return customerInvoiceWriteoffDocumentService;
    }

    public void setCustomerInvoiceWriteoffDocumentService(CustomerInvoiceWriteoffDocumentService customerInvoiceWriteoffDocumentService) {
        this.customerInvoiceWriteoffDocumentService = customerInvoiceWriteoffDocumentService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}
