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
package org.kuali.kfs.krad.web.controller;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.bo.PersistableAttachment;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.exception.UnknownDocumentIdException;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.maintenance.MaintenanceUtils;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.MaintenanceDocumentService;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifParameters;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.web.form.DocumentFormBase;
import org.kuali.kfs.krad.web.form.InitiatedDocumentInfoForm;
import org.kuali.kfs.krad.web.form.MaintenanceForm;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

/**
 * Controller for <code>MaintenanceView</code> screens which operate on
 * <code>MaintenanceDocument</code> instances
 */
@Controller
@RequestMapping(value = "/maintenance")
public class MaintenanceDocumentController extends DocumentControllerBase {
    protected static final Logger LOG = Logger.getLogger(MaintenanceDocumentController.class);

    /**
     * @see UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected MaintenanceForm createInitialForm(HttpServletRequest request) {
        return new MaintenanceForm();
    }

    /**
     * After the document is loaded calls method to setup the maintenance object
     */
    @Override
    @RequestMapping(params = "methodToCall=docHandler")
    public ModelAndView docHandler(@ModelAttribute("KualiForm") DocumentFormBase formBase, BindingResult result,
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {

        // TODO getting double view if we call base, not sure how to handle
        // so pasting in superclass code
        // super.docHandler(formBase, request, response);
        // * begin copy/paste from the base
        MaintenanceForm form = (MaintenanceForm) formBase;

        // in all of the following cases we want to load the document
        if (ArrayUtils.contains(DOCUMENT_LOAD_COMMANDS, form.getCommand()) && form.getDocId() != null) {
            try {
                loadDocument(form);
            } catch (UnknownDocumentIdException udie) {
                ConfigurationService kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
                StringBuffer sb = new StringBuffer();
                sb.append(kualiConfigurationService.getPropertyValueAsString(KRADConstants.KRAD_URL_KEY));
                sb.append(kualiConfigurationService.getPropertyValueAsString(KRADConstants.KRAD_INITIATED_DOCUMENT_URL_KEY));
                Properties props = new Properties();
                props.put(UifParameters.METHOD_TO_CALL, UifConstants.MethodToCallNames.START);
                GlobalVariables.getUifFormManager().removeForm(form);
                return performRedirect(new InitiatedDocumentInfoForm(), sb.toString(), props);
            }
        } else if (KewApiConstants.INITIATE_COMMAND.equals(form.getCommand())) {
            createDocument(form);
        } else {
            LOG.error("docHandler called with invalid parameters");
            throw new IllegalArgumentException("docHandler called with invalid parameters");
        }
        // * end copy/paste from the base

        if (KewApiConstants.ACTIONLIST_COMMAND.equals(form.getCommand()) ||
            KewApiConstants.DOCSEARCH_COMMAND.equals(form.getCommand()) ||
            KewApiConstants.SUPERUSER_COMMAND.equals(form.getCommand()) ||
            KewApiConstants.HELPDESK_ACTIONLIST_COMMAND.equals(form.getCommand()) && form.getDocId() != null) {
            // TODO: set state in view
            // form.setReadOnly(true);
            form.setMaintenanceAction((form.getDocument()).getNewMaintainableObject().getMaintenanceAction());

            // Retrieving the FileName from BO table
            Maintainable tmpMaintainable = form.getDocument().getNewMaintainableObject();
            if (tmpMaintainable.getDataObject() instanceof PersistableAttachment) {
                PersistableAttachment bo = (PersistableAttachment) getBusinessObjectService()
                    .retrieve((PersistableBusinessObject) tmpMaintainable.getDataObject());
                if (bo != null) {
                    request.setAttribute("fileName", bo.getFileName());
                }
            }
        } else if (KewApiConstants.INITIATE_COMMAND.equals(form.getCommand())) {
            // form.setReadOnly(false);
            setupMaintenance(form, request, KRADConstants.MAINTENANCE_NEW_ACTION);
        } else {
            LOG.error("We should never have gotten to here");
            throw new IllegalArgumentException("docHandler called with invalid parameters");
        }

        return getUIFModelAndView(form);
    }

    /**
     * Default method for controller that setups a new
     * <code>MaintenanceView</code> with the default new action
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_NEW)
    @Override
    public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
                              HttpServletRequest request, HttpServletResponse response) {
        MaintenanceForm maintenanceForm = (MaintenanceForm) form;

        setupMaintenance(maintenanceForm, request, KRADConstants.MAINTENANCE_NEW_ACTION);

        return getUIFModelAndView(maintenanceForm);
    }

    /**
     * Setups a new <code>MaintenanceView</code> with the edit maintenance
     * action
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_EDIT)
    public ModelAndView maintenanceEdit(@ModelAttribute("KualiForm") MaintenanceForm form, BindingResult result,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {

        setupMaintenance(form, request, KRADConstants.MAINTENANCE_EDIT_ACTION);

        return getUIFModelAndView(form);
    }

    /**
     * Setups a new <code>MaintenanceView</code> with the copy maintenance
     * action
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_COPY)
    public ModelAndView maintenanceCopy(@ModelAttribute("KualiForm") MaintenanceForm form, BindingResult result,
                                        HttpServletRequest request, HttpServletResponse response) throws Exception {

        setupMaintenance(form, request, KRADConstants.MAINTENANCE_COPY_ACTION);

        return getUIFModelAndView(form);
    }

    /**
     * Setups a new <code>MaintenanceView</code> with the new with existing
     * maintenance action
     */
    @RequestMapping(params = "methodToCall=" + KRADConstants.Maintenance.METHOD_TO_CALL_NEW_WITH_EXISTING)
    public ModelAndView maintenanceNewWithExisting(@ModelAttribute("KualiForm") MaintenanceForm form,
                                                   BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {

        setupMaintenance(form, request, KRADConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);

        return getUIFModelAndView(form);
    }

    /**
     * Sets up the <code>MaintenanceDocument</code> on initial get request
     * <p>
     * <p>
     * First step is to create a new document instance based on the query
     * parameters (document type name or object class name). Then call the
     * <code>Maintainable</code> to do setup on the object being maintained.
     * </p>
     *
     * @param form              - <code>MaintenanceForm</code> instance
     * @param request           - HTTP request object
     * @param maintenanceAction - the maintenance action (new, new from existing, edit, copy)
     *                          being request
     * @throws Exception
     */
    protected void setupMaintenance(MaintenanceForm form, HttpServletRequest request, String maintenanceAction) {
        MaintenanceDocument document = form.getDocument();

        // create a new document object, if required
        if (document == null) {
            document = getMaintenanceDocumentService()
                .setupNewMaintenanceDocument(form.getDataObjectClassName(), form.getDocTypeName(),
                    maintenanceAction);

            form.setDocument(document);
            form.setDocTypeName(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        }

        // set action on form
        form.setMaintenanceAction(maintenanceAction);

        // invoke maintenance document service to setup the object for maintenance
        getMaintenanceDocumentService().setupMaintenanceObject(document, maintenanceAction, request.getParameterMap());

        // for new maintainable check if a maintenance lock exists and if so
        // warn the user
        if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction)) {
            MaintenanceUtils.checkForLockingDocument(document, false);
        }

        // Retrieve notes topic display flag from data dictionary and add to
        // document
        // TODO: should be in the view as permission
        DocumentEntry entry = KRADServiceLocatorWeb.getDocumentDictionaryService()
            .getMaintenanceDocumentEntry(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
        document.setDisplayTopicFieldInNotes(entry.getDisplayTopicFieldInNotes());
    }

    /**
     * Override route to retrieve the maintenance object if it is an attachment
     *
     * @see DocumentControllerBase.route
     * (DocumentFormBase, HttpServletRequest, HttpServletResponse)
     */
    @Override
    @RequestMapping(params = "methodToCall=route")
    public ModelAndView route(@ModelAttribute("KualiForm") DocumentFormBase form, BindingResult result,
                              HttpServletRequest request, HttpServletResponse response) {

        ModelAndView modelAndView;

        modelAndView = super.route(form, result, request, response);

        MaintenanceDocument document = (MaintenanceDocument) form.getDocument();
        if (document.getNewMaintainableObject().getDataObject() instanceof PersistableAttachment) {
            PersistableAttachment bo = (PersistableAttachment) getBusinessObjectService()
                .retrieve((PersistableBusinessObject) document.getNewMaintainableObject().getDataObject());
            request.setAttribute("fileName", bo.getFileName());
        }

        modelAndView = getUIFModelAndView(form);

        return modelAndView;
    }

    protected MaintenanceDocumentService getMaintenanceDocumentService() {
        return KRADServiceLocatorWeb.getMaintenanceDocumentService();
    }

}
