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
package org.kuali.kfs.pdp.batch.service.impl;

import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.batch.service.FormatCheckACHEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.service.impl.VelocityEmailServiceBase;

import java.util.Collection;

public class FormatCheckACHEmailServiceImpl extends VelocityEmailServiceBase
    implements FormatCheckACHEmailService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
        .getLogger(FormatCheckACHEmailServiceImpl.class);

    private String templateUrl;

    private String emailSubject;

    /*
     * (non-Javadoc)
     *
     * @see org.kuali.kfs.sys.service.VelocityEmailService#getEmailSubject()
     */
    @Override
    public String getEmailSubject() {
        return emailSubject;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.kuali.kfs.sys.service.impl.VelocityEmailServiceBase#getProdEmailReceivers
     * ()
     */
    @Override
    public Collection<String> getProdEmailReceivers() {
        Collection<String> emailReceivers = CoreFrameworkServiceLocator
            .getParameterService()
            .getParameterValuesAsString(
                KFSConstants.CoreModuleNamespaces.PDP,
                PdpConstants.FormatCheckACHParameters.PDP_FORMAT_CHECK_ACH_STEP,
                PdpConstants.FormatCheckACHParameters.FORMAT_SUMMARY_TO_EMAIL_ADDRESSES);
        return emailReceivers;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kuali.kfs.sys.service.VelocityEmailService#getTemplateUrl()
     */
    @Override
    public String getTemplateUrl() {
        return templateUrl;
    }

    /**
     * @param templateUrl the templateUrl to set
     */
    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    /**
     * @param emailSubject the emailSubject to set
     */
    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }


}
