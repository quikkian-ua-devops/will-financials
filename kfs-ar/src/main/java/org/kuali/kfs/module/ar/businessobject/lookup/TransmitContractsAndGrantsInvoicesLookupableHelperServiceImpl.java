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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.TransmitContractsAndGrantsInvoicesLookupDataHolder;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.report.service.TransmitContractsAndGrantsInvoicesService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.kns.web.comparator.CellComparatorHelper;
import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.kfs.kns.web.ui.Column;
import org.kuali.kfs.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;

/**
 * LookupableHelperService class for Collection Activity Report.
 */
public class TransmitContractsAndGrantsInvoicesLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransmitContractsAndGrantsInvoicesLookupableHelperServiceImpl.class);

    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected PersonService personService;
    protected TransmitContractsAndGrantsInvoicesService transmitContractsAndGrantsInvoicesService;

    /**
     * Get the search results that meet the input search criteria.
     *
     * @param fieldValues - Map containing prop name keys and search values
     * @return a List of found business objects
     */
    @Override
    public List getSearchResultsUnbounded(Map fieldValues) {
        List<TransmitContractsAndGrantsInvoicesLookupDataHolder> results = new ArrayList<TransmitContractsAndGrantsInvoicesLookupDataHolder>();
        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        String invoiceTransmissionMethodCode = (String) fieldValues.get(ArPropertyConstants.INVOICE_TRANSMISSION_METHOD_CODE);

        // Fetch the invoices with the input parameters
        Collection<ContractsGrantsInvoiceDocument> list;
        try {
            list = transmitContractsAndGrantsInvoicesService.getInvoicesByParametersFromRequest(fieldValues);
        }
        catch (WorkflowException | ParseException ex) {
            LOG.error("Problem searching for invoices ready to transmit.", ex);
            throw new RuntimeException(ex);
        }
        if (ObjectUtils.isNotNull(list)) {
            for (ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument:list) {
                TransmitContractsAndGrantsInvoicesLookupDataHolder result = setupResultRecord(contractsGrantsInvoiceDocument, invoiceTransmissionMethodCode);
                results.add(result);
            }
        }

        return new CollectionIncomplete<TransmitContractsAndGrantsInvoicesLookupDataHolder>(results, (long) results.size());
    }


    /**
     *
     * @param contractsGrantsInvoiceDocument
     * @return
     */
    private TransmitContractsAndGrantsInvoicesLookupDataHolder setupResultRecord(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, String invoiceTransmissionMethodCode) {
        TransmitContractsAndGrantsInvoicesLookupDataHolder result = new TransmitContractsAndGrantsInvoicesLookupDataHolder();
        result.setBillByChartOfAccountCode(contractsGrantsInvoiceDocument.getBillByChartOfAccountCode());
        result.setBilledByOrganizationCode(contractsGrantsInvoiceDocument.getBilledByOrganizationCode());
        result.setInvoiceInitiatorPrincipalName(contractsGrantsInvoiceDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        result.setProposalNumber(contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getProposalNumber());
        result.setDocumentNumber(contractsGrantsInvoiceDocument.getDocumentNumber());
        result.setInvoiceInitiatorPrincipalName(personService.getPerson(contractsGrantsInvoiceDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPrincipalName());
        result.setInvoiceAmount(contractsGrantsInvoiceDocument.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount().toString());
        result.setInvoiceTransmissionMethodCode(invoiceTransmissionMethodCode);
        return result;
    }


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
        Collection displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        // MJM get resultTable populated here
        HashMap<String, Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (Object aDisplayList : displayList) {
            BusinessObject element = (BusinessObject) aDisplayList;

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            if (ObjectUtils.isNotNull(getColumns())) {
                List<Column> columns = getColumns();
                for (Object column : columns) {

                    Column col = (Column) column;
                    Formatter formatter = col.getFormatter();

                    // pick off result column from result list, do formatting
                    Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());

                    String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                    Class propClass = getPropertyClass(element, col.getPropertyName());

                    // formatters
                    if (ObjectUtils.isNotNull(prop)) {
                        propValue = getContractsGrantsReportHelperService().formatByType(prop, formatter);
                    }

                    // comparator
                    col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                    col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                    propValue = super.maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                    col.setPropertyValue(propValue);

                    // Add url when property is invoiceNumber
                    if (col.getPropertyName().equals(KFSPropertyConstants.DOCUMENT_NUMBER)) {
                        String url = contractsGrantsReportHelperService.getDocSearchUrl(propValue);

                        Map<String, String> fieldList = new HashMap<String, String>();
                        fieldList.put(ArPropertyConstants.INVOICE_NUMBER, propValue);
                        AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                        a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                        col.setColumnAnchor(a);
                    }
                }

                ResultRow row = new ResultRow(columns, KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING);
                if (element instanceof PersistableBusinessObject) {
                    row.setObjectId(((PersistableBusinessObject) element).getObjectId());
                }

                boolean rowReturnable = isResultReturnable(element);
                row.setRowReturnable(rowReturnable);
                if (rowReturnable) {
                    hasReturnableRow = true;
                }

                resultTable.add(row);
            }

            lookupForm.setHasReturnableRow(hasReturnableRow);
        }

        return displayList;
    }

    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);
        transmitContractsAndGrantsInvoicesService.validateSearchParameters(fieldValues);
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }


    public PersonService getPersonService() {
        return personService;
    }


    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }


    public TransmitContractsAndGrantsInvoicesService getTransmitContractsAndGrantsInvoicesService() {
        return transmitContractsAndGrantsInvoicesService;
    }


    public void setTransmitContractsAndGrantsInvoicesService(TransmitContractsAndGrantsInvoicesService transmitContractsAndGrantsInvoicesService) {
        this.transmitContractsAndGrantsInvoicesService = transmitContractsAndGrantsInvoicesService;
    }
}
