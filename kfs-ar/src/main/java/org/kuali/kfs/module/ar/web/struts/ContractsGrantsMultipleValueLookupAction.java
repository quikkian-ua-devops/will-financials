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
package org.kuali.kfs.module.ar.web.struts;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.kfs.kns.lookup.LookupResultsService;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.kns.web.struts.action.KualiMultipleValueLookupAction;
import org.kuali.kfs.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.kfs.kns.web.ui.ResultRow;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.UrlFactory;

/**
 * Parent class of C&G Billing multiple value lookups which have common attributes
 */
public abstract class ContractsGrantsMultipleValueLookupAction extends KualiMultipleValueLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsMultipleValueLookupAction.class);

    /**
     * This method performs the operations necessary for a multiple value lookup to select all of the results and rerender the page
     *
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    @Override
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }

        Map<String, String> selectedObjectIds = collectSelectedObjectIds(resultTable);

        multipleValueLookupForm.jumpToPage(multipleValueLookupForm.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        if (!StringUtils.isBlank(multipleValueLookupForm.getPreviouslySortedColumnIndex())) {
            multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        }
        multipleValueLookupForm.setCompositeObjectIdMap(selectedObjectIds);

        return resultTable;
    }

    /**
     * Collects a Map of selected object ids from the results of the lookup
     * @param resultTable the results of the lookup
     * @return a Map of the selected ids
     */
    protected abstract Map<String, String> collectSelectedObjectIds(List<ResultRow> resultTable);

    /**
     * This method does the processing necessary to return selected results and sends a redirect back to the lookup caller
     *
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward prepareToReturnSelectedResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        if (StringUtils.isBlank(multipleValueLookupForm.getLookupResultsSequenceNumber())) {
            // no search was executed
            return prepareToReturnNone(mapping, form, request, response);
        }

        Map<String, String> compositeObjectIdMap = LookupUtils.generateCompositeSelectedObjectIds(multipleValueLookupForm.getPreviouslySelectedObjectIdSet(), multipleValueLookupForm.getDisplayedObjectIdSet(), multipleValueLookupForm.getSelectedObjectIdSet());
        Set<String> compositeObjectIds = compositeObjectIdMap.keySet();

        if (!compositeObjectIds.isEmpty()) {
            prepareToReturnSelectedResultBOs(multipleValueLookupForm);

            // build the parameters for the refresh url
            Properties parameters = new Properties();
            parameters.put(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, multipleValueLookupForm.getLookupResultsSequenceNumber());
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, ArConstants.CUSTOMER_INVOICE_WRITEOFF_SUMMARY_ACTION);

            String returnResultUrl = UrlFactory.parameterizeUrl(getActionUrl(), parameters);
            return new ActionForward(returnResultUrl, true);
        }
        else {
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
    }

    /**
     * @return the base url of the concrete action class
     */
    protected abstract String getActionUrl();
}
