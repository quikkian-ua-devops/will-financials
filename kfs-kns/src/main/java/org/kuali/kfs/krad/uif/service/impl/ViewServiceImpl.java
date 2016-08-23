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
package org.kuali.kfs.krad.uif.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifConstants.ViewStatus;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.service.ViewHelperService;
import org.kuali.kfs.krad.uif.service.ViewService;
import org.kuali.kfs.krad.uif.service.ViewTypeService;
import org.kuali.kfs.krad.uif.UifConstants.ViewType;
import org.kuali.kfs.krad.web.form.UifFormBase;

/**
 * Implementation of <code>ViewService</code>
 *
 * <p>
 * Provides methods for retrieving View instances and carrying out the View
 * lifecycle methods. Interacts with the configured <code>ViewHelperService</code>
 * during the view lifecycle
 * </p>
 *
 *
 */
public class ViewServiceImpl implements ViewService {
    private static final Logger LOG = Logger.getLogger(ViewServiceImpl.class);

    private DataDictionaryService dataDictionaryService;

    // TODO: remove once we can get beans by type from spring
    private List<ViewTypeService> viewTypeServices;

    /**
     * @see ViewService#getViewById(java.lang.String)
     */
    public View getViewById(String viewId) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrieving view instance for id: " + viewId);
        }

        View view = dataDictionaryService.getViewById(viewId);
        if (view == null) {
            LOG.warn("View not found for id: " + viewId);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Updating view status to CREATED for view: " + view.getId());
            }
            view.setViewStatus(ViewStatus.CREATED);
        }

        return view;
    }

    /**
     * Retrieves the <code>ViewTypeService</code> for the given view type, then builds up the index based
     * on the supported view type parameters and queries the dictionary service to retrieve the view
     * based on its type and index
     *
     * @see ViewService#getViewByType(UifConstants.ViewType,
     *      java.util.Map<java.lang.String,java.lang.String>)
     */
    public View getViewByType(ViewType viewType, Map<String, String> parameters) {
        ViewTypeService typeService = getViewTypeService(viewType);
        if (typeService == null) {
            throw new RuntimeException("Unable to find view type service for view type name: " + viewType);
        }

        Map<String, String> typeParameters = typeService.getParametersFromRequest(parameters);

        Map<String, String> indexKey = new HashMap<String, String>();
        for (Map.Entry<String, String> parameter : typeParameters.entrySet()) {
            indexKey.put(parameter.getKey(), parameter.getValue());
        }

        View view = dataDictionaryService.getViewByTypeIndex(viewType, indexKey);
        if (view == null) {
            LOG.warn("View not found for type: " + viewType);
        } else {
            LOG.debug("Updating view status to CREATED for view: " + view.getId());
            view.setViewStatus(ViewStatus.CREATED);
        }

        return view;
    }

    /**
     * @see ViewService#buildView(View, java.lang.Object,
     * java.util.Map<java.lang.String,java.lang.String>)
     */
    public void buildView(View view, Object model, Map<String, String> parameters) {
        // get the configured helper service for the view
        ViewHelperService helperService = view.getViewHelperService();

        // populate view from request parameters
        helperService.populateViewFromRequestParameters(view, parameters);

        // backup view request parameters on form for recreating lost views (session timeout)
        ((UifFormBase) model).setViewRequestParameters(view.getViewRequestParameters());

        // run view lifecycle
        performViewLifecycle(view, model, parameters);
    }

    /**
     * Initializes a newly created <code>View</code> instance. Each component of the tree is invoked
     * to perform setup based on its configuration. In addition helper service methods are invoked to
     * perform custom initialization
     *
     * @param view - view instance to initialize
     * @param model - object instance containing the view data
     * @param parameters - Map of key values pairs that provide configuration for the <code>View</code>, this
     * is generally comes from the request and can be the request parameter Map itself. Any parameters
     * not valid for the View will be filtered out
     */
    protected void performViewLifecycle(View view, Object model, Map<String, String> parameters) {
        // get the configured helper service for the view
        ViewHelperService helperService = view.getViewHelperService();

        // invoke initialize phase on the views helper service
        // Heavily called method showed up on profile as a hotspot.  Putting log statements in checks cuts execution time by ~75%
        if (LOG.isEnabledFor(Priority.INFO)) {
            LOG.info("performing initialize phase for view: " + view.getId());
        }
        helperService.performInitialization(view, model);

        // do indexing
        if (LOG.isDebugEnabled()) {
            LOG.debug("processing indexing for view: " + view.getId());
        }
        view.index();

        // update status on view
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating view status to INITIALIZED for view: " + view.getId());
        }
        view.setViewStatus(ViewStatus.INITIALIZED);

        // Apply Model Phase
        if (LOG.isEnabledFor(Priority.INFO)) {
            LOG.info("performing apply model phase for view: " + view.getId());
        }
        helperService.performApplyModel(view, model);

        // do indexing
        if (LOG.isEnabledFor(Priority.INFO)) {
            LOG.info("reindexing after apply model for view: " + view.getId());
        }
        view.index();

        // Finalize Phase
        if (LOG.isEnabledFor(Priority.INFO)) {
            LOG.info("performing finalize phase for view: " + view.getId());
        }
        helperService.performFinalize(view, model);

        // do indexing
        if (LOG.isEnabledFor(Priority.INFO)) {
            LOG.info("processing final indexing for view: " + view.getId());
        }
        view.index();

        // update status on view
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating view status to FINAL for view: " + view.getId());
        }
        view.setViewStatus(ViewStatus.FINAL);
    }

    public ViewTypeService getViewTypeService(UifConstants.ViewType viewType) {
        if (viewTypeServices != null) {
            for (ViewTypeService typeService : viewTypeServices) {
                if (viewType.equals(typeService.getViewTypeName())) {
                    return typeService;
                }
            }
        }

        return null;
    }

    public List<ViewTypeService> getViewTypeServices() {
        return this.viewTypeServices;
    }

    public void setViewTypeServices(List<ViewTypeService> viewTypeServices) {
        this.viewTypeServices = viewTypeServices;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
