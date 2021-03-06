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
package org.kuali.kfs.krad.web.controller;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.krad.lookup.Lookupable;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.uif.UifPropertyPaths;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.krad.web.form.LookupForm;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

/**
 * Controller that handles requests coming from a <code>LookupView</code>
 */
@Controller
@RequestMapping(value = "/lookup")
public class LookupController extends UifControllerBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupController.class);

    /**
     * @see UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected LookupForm createInitialForm(HttpServletRequest request) {
        return new LookupForm();
    }

    /**
     * Invoked to request an lookup view for a data object class
     * <p>
     * <p>
     * Checks if the data object is externalizable and we need to redirect to the appropriate lookup URL, else
     * continues with the lookup view display
     * </p>
     */
    @RequestMapping(params = "methodToCall=start")
    @Override
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
                              HttpServletRequest request, HttpServletResponse response) {
        LookupForm lookupForm = (LookupForm) form;

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }
        lookupable.initSuppressAction(lookupForm);

        // if request is not a redirect, determine if we need to redirect for an externalizable object lookup
        if (!lookupForm.isRedirectedLookup()) {
            Class lookupObjectClass = null;
            try {
                lookupObjectClass = Class.forName(lookupForm.getDataObjectClassName());
            } catch (ClassNotFoundException e) {
                throw new RiceRuntimeException("Unable to get class for name: " + lookupForm.getDataObjectClassName());
            }

            ModuleService responsibleModuleService =
                KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(lookupObjectClass);
            if (responsibleModuleService != null && responsibleModuleService.isExternalizable(lookupObjectClass)) {
                String lookupUrl = responsibleModuleService.getExternalizableDataObjectLookupUrl(lookupObjectClass,
                    KRADUtils.convertRequestMapToProperties(request.getParameterMap()));

                Properties redirectUrlProps = new Properties();
                redirectUrlProps.put(UifParameters.REDIRECTED_LOOKUP, "true");

                // clear current form from session
                GlobalVariables.getUifFormManager().removeForm(form);

                return performRedirect(form, lookupUrl, redirectUrlProps);
            }
        }

        return super.start(lookupForm, result, request, response);
    }

    /**
     * Just returns as if return with no value was selected
     */
    @Override
    @RequestMapping(params = "methodToCall=cancel")
    public ModelAndView cancel(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
                               HttpServletRequest request, HttpServletResponse response) {
        LookupForm lookupForm = (LookupForm) form;

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }
        lookupable.initSuppressAction(lookupForm);

        Properties props = new Properties();
        props.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.REFRESH);
        if (StringUtils.isNotBlank(lookupForm.getReturnFormKey())) {
            props.put(UifParameters.FORM_KEY, lookupForm.getReturnFormKey());
        }
        if (StringUtils.isNotBlank(lookupForm.getDocNum())) {
            props.put(UifParameters.DOC_NUM, lookupForm.getDocNum());
        }

        // clear current form from session
        GlobalVariables.getUifFormManager().removeForm(form);

        return performRedirect(lookupForm, lookupForm.getReturnLocation(), props);
    }

    /**
     * clearValues - clears the values of all the fields on the jsp.
     */
    @RequestMapping(params = "methodToCall=clearValues")
    public ModelAndView clearValues(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result,
                                    HttpServletRequest request, HttpServletResponse response) {

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }
        lookupable.initSuppressAction(lookupForm);
        lookupForm.setCriteriaFields(lookupable.performClear(lookupForm, lookupForm.getCriteriaFields()));

        return getUIFModelAndView(lookupForm);
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the
     * results.
     */
    @RequestMapping(params = "methodToCall=search")
    public ModelAndView search(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result,
                               HttpServletRequest request, HttpServletResponse response) {

        Lookupable lookupable = lookupForm.getLookupable();
        if (lookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }
        lookupable.initSuppressAction(lookupForm);

        // validate search parameters
        lookupable.validateSearchParameters(lookupForm, lookupForm.getCriteriaFields());

        Collection<?> displayList =
            lookupable.performSearch(lookupForm, lookupForm.getCriteriaFields(), true);

        if (displayList instanceof CollectionIncomplete<?>) {
            request.setAttribute("reqSearchResultsActualSize",
                ((CollectionIncomplete<?>) displayList).getActualSizeIfTruncated());
        } else {
            request.setAttribute("reqSearchResultsActualSize", new Integer(displayList.size()));
        }

        lookupForm.setSearchResults(displayList);

        return getUIFModelAndView(lookupForm);
    }

    /**
     * Invoked from the UI to return the selected lookup results lines, parameters are collected to build a URL to
     * the caller and then a redirect is performed
     *
     * @param lookupForm - lookup form instance containing the selected results and lookup configuration
     */
    @RequestMapping(params = "methodToCall=returnSelected")
    public ModelAndView returnSelected(@ModelAttribute("KualiForm") LookupForm lookupForm, BindingResult result,
                                       HttpServletRequest request, HttpServletResponse response) {
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);

        if (StringUtils.isNotBlank(lookupForm.getReturnFormKey())) {
            parameters.put(UifParameters.FORM_KEY, lookupForm.getReturnFormKey());
        }

        parameters.put(KRADConstants.REFRESH_CALLER, lookupForm.getView().getId());
        parameters.put(KRADConstants.REFRESH_CALLER_TYPE, UifConstants.RefreshCallerTypes.MULTI_VALUE_LOOKUP);
        parameters.put(KRADConstants.REFRESH_DATA_OBJECT_CLASS, lookupForm.getDataObjectClassName());

        if (StringUtils.isNotBlank(lookupForm.getDocNum())) {
            parameters.put(UifParameters.DOC_NUM, lookupForm.getDocNum());
        }

        if (StringUtils.isNotBlank(lookupForm.getLookupCollectionName())) {
            parameters.put(UifParameters.LOOKUP_COLLECTION_NAME, lookupForm.getLookupCollectionName());
        }

        if (StringUtils.isNotBlank(lookupForm.getReferencesToRefresh())) {
            parameters.put(KRADConstants.REFERENCES_TO_REFRESH, lookupForm.getReferencesToRefresh());
        }

        // build string of select line identifiers
        String selectedLineValues = "";
        Set<String> selectedLines = lookupForm.getSelectedCollectionLines().get(UifPropertyPaths.SEARCH_RESULTS);
        if (selectedLines != null) {
            for (String selectedLine : selectedLines) {
                selectedLineValues += selectedLine + ",";
            }
            selectedLineValues = StringUtils.removeEnd(selectedLineValues, ",");
        }

        parameters.put(UifParameters.SELECTED_LINE_VALUES, selectedLineValues);

        // clear current form from session
        GlobalVariables.getUifFormManager().removeForm(lookupForm);

        return performRedirect(lookupForm, lookupForm.getReturnLocation(), parameters);
    }
}
