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
package org.kuali.kfs.sys.web.struts;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.DisplayMessageException;
import org.kuali.rice.core.api.config.property.ConfigurationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ErrorHandlerAction extends Action {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ErrorHandlerAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("execute() started");

        Exception exception = (Exception) request.getAttribute(Globals.EXCEPTION_KEY);

        if (exception instanceof DisplayMessageException) {
            request.setAttribute("message", exception.getMessage());
            return mapping.findForward("display");
        }

        ConfigurationService configurationService = SpringContext.getBean(ConfigurationService.class);
        String productionEnvironmentCode = configurationService.getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY);
        String environmentCode = configurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);

        if (productionEnvironmentCode.equals(environmentCode)) {
            return mapping.findForward("prd");
        } else {
            return mapping.findForward("tst");
        }
    }
}
