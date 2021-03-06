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
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO jawbenne don't forget to fill this in.
 */
public class ModuleLockingHandlerInterceptor implements HandlerInterceptor {

    private static final Logger LOG = Logger.getLogger(ModuleLockingHandlerInterceptor.class);


    private KualiModuleService kualiModuleService;
    private String moduleLockedMapping;

    /**
     * @return the moduleLockedMapping
     */
    public String getModuleLockedMapping() {
        return this.moduleLockedMapping;
    }

    /**
     * @param moduleLockedMapping the moduleLockedMapping to set
     */
    public void setModuleLockedMapping(String moduleLockedMapping) {
        this.moduleLockedMapping = moduleLockedMapping;
    }

    /**
     * @param kualiModuleService the kualiModuleService to set
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * This overridden method ...
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) throws Exception {
        // do nothing
    }

    /**
     * This overridden method ...
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndview)
        throws Exception {
        // do nothing
    }

    /**
     * This overridden method ...
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (isModuleLocked(request)) {
            response.sendRedirect(this.getModuleLockedMapping() + "?" + ModuleLockedController.MODULE_PARAMETER + "=" + getModuleService(request).getModuleConfiguration().getNamespaceCode());
        }
        return true;
    }

    private ModuleService getModuleService(HttpServletRequest request) {
        String boClass = request.getParameter(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE);
        if (StringUtils.isBlank(boClass)) {
            boClass = request.getParameter(KRADConstants.DATA_OBJECT_CLASS_ATTRIBUTE);
        }
        ModuleService moduleService = null;
        if (StringUtils.isNotBlank(boClass)) {
            try {
                moduleService = getKualiModuleService().getResponsibleModuleService(Class.forName(boClass));
            } catch (ClassNotFoundException classNotFoundException) {
                LOG.warn("BO class not found: " + boClass, classNotFoundException);
            }
        } else {
            moduleService = getKualiModuleService().getResponsibleModuleService(this.getClass());
        }
        return moduleService;
    }

    protected boolean isModuleLocked(HttpServletRequest request) {
        ModuleService moduleService = getModuleService(request);
        if (moduleService != null && moduleService.isLocked()) {
            String principalId = GlobalVariables.getUserSession().getPrincipalId();
            String namespaceCode = KRADConstants.KUALI_RICE_SYSTEM_NAMESPACE;
            String permissionName = KimConstants.PermissionNames.ACCESS_LOCKED_MODULE;
            Map<String, String> permissionDetails = new HashMap<String, String>();
            Map<String, String> qualification = new HashMap<String, String>();
            if (!KimApiServiceLocator.getPermissionService().isAuthorized(principalId, namespaceCode, permissionName, qualification)) {
                return true;
            }
        }
        return false;
    }

    protected KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }
}
