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
package org.kuali.kfs.module.ar.web.struts;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.kfs.kns.web.ui.ResultRow;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsLookupResultRow;
import org.kuali.rice.core.api.util.RiceConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Action class for the Generate Dunning Letters Lookup.
 */
public class GenerateDunningLettersLookupAction extends ContractsGrantsMultipleValueLookupAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenerateDunningLettersLookupAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction#search(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.search(mapping, form, request, response);
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;

        this.selectAll(multipleValueLookupForm, getMaxRowsPerPage(multipleValueLookupForm));

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method performs the operations necessary for a multiple value lookup to select all of the results and rerender the page
     *
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    @Override
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        List<ResultRow> resultTable = super.selectAll(multipleValueLookupForm, maxRowsPerPage);
        if (multipleValueLookupForm.getPreviouslySortedColumnIndex() != null) {
            multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        }
        return resultTable;
    }

    /**
     * Collects ids from the lookup that are selected; checks children rows for DunningLetterDistributionResultRows
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsMultipleValueLookupAction#collectSelectedObjectIds(java.util.List)
     */
    @Override
    protected Map<String, String> collectSelectedObjectIds(List<ResultRow> resultTable) {
        Map<String, String> selectedObjectIds = new HashMap<String, String>();

        for (ResultRow row : resultTable) {
            // actual object ids are on sub result rows, not on parent rows
            if (row instanceof ContractsGrantsLookupResultRow) {
                for (ResultRow subResultRow : ((ContractsGrantsLookupResultRow) row).getSubResultRows()) {
                    String objId = subResultRow.getObjectId();
                    selectedObjectIds.put(objId, objId);
                }
            } else {
                String objId = row.getObjectId();
                selectedObjectIds.put(objId, objId);
            }
        }
        return selectedObjectIds;
    }

    /**
     * Returns "arDunningLetterDistributionSummary.do"
     *
     * @see org.kuali.kfs.module.ar.web.struts.ContractsGrantsMultipleValueLookupAction#getActionUrl()
     */
    @Override
    protected String getActionUrl() {
        return ArConstants.MultipleValueReturnActions.GENERATE_DUNNING_LETTERS_SUMMARY;
    }
}
