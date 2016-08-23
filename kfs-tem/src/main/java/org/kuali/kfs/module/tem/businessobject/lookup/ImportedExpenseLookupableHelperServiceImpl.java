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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.text.StrBuilder;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.InputHtmlData;
import org.kuali.kfs.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.krad.util.GlobalVariables;

@SuppressWarnings("deprecation")
public class ImportedExpenseLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    ParameterService parameterService;

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        Integer profileID = (Integer) GlobalVariables.getUserSession().retrieveObject(TemPropertyConstants.TemProfileProperties.PROFILE_ID);

        List<String> lookupableProfileId = new ArrayList<String>();
        lookupableProfileId.add(profileID.toString());

        boolean includeTravelerExpense = parameterService.getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INCLUDE_ARRANGER_EXPENSE_IN_IMPORTED_EXPENSE_IND);
        //also use the traveler's profile ID to get the result
        if (includeTravelerExpense){
            Integer travelerProfileID = (Integer) GlobalVariables.getUserSession().retrieveObject(TemPropertyConstants.ARRANGER_PROFILE_ID);
            if (travelerProfileID != null){
                lookupableProfileId.add(travelerProfileID.toString());
            }
        }
        StrBuilder builder = new StrBuilder();
        builder.appendWithSeparators(lookupableProfileId, "|");

        fieldValues.put(TemPropertyConstants.TemProfileProperties.PROFILE_ID, builder.toString());
        String value = GlobalVariables.getUserSession().retrieveObject(KFSPropertyConstants.DOCUMENT_TYPE_CODE).toString();

        List<HistoricalTravelExpense> results = (List<HistoricalTravelExpense>) super.getSearchResultsHelper(fieldValues, true);
        List<HistoricalTravelExpense> newResults = new ArrayList<HistoricalTravelExpense>();
        Iterator<HistoricalTravelExpense> it = results.iterator();
        while (it.hasNext()){
            HistoricalTravelExpense historicalTravelExpense = it.next();
            newResults.add(historicalTravelExpense);
        }
        CollectionIncomplete collection = null;
        Integer limit = LookupUtils.getSearchResultsLimit(HistoricalTravelExpense.class);
        if (newResults.size() > limit.intValue()){
            collection = new CollectionIncomplete(newResults.subList(0, limit), (long) newResults.size());
        }
        else{
            collection = new CollectionIncomplete(newResults, (long) 0);
        }

        return collection;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnInputHtmlData(org.kuali.rice.kns.bo.BusinessObject, java.util.Properties, org.kuali.rice.kns.web.struts.form.LookupForm, java.util.List, org.kuali.rice.kns.authorization.BusinessObjectRestrictions)
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected HtmlData getReturnInputHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        HistoricalTravelExpense expense = (HistoricalTravelExpense)businessObject;
        if (!expense.getAssigned()
                && (expense.getReconciled() == null || expense.getReconciled().equals(TemConstants.ReconciledCodes.UNRECONCILED))){
            InputHtmlData input = (InputHtmlData) super.getReturnInputHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);
            input.setAppendDisplayText("</div>");
            input.setPrependDisplayText("<div align=\"center\">");
            return input;
        }
        else{
            InputHtmlData input = new InputHtmlData("","hidden");
            return input;
        }

    }

    @Override
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
