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
package org.kuali.kfs.module.external.kc.service;

import org.kuali.kfs.module.external.kc.KcConstants;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


@WebService(name = KcConstants.RiceApplicationConfigurationService.WEB_SERVICE_NAME,
    targetNamespace = KcConstants.KFS_NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
    parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)

public interface KcRiceApplicationConfigurationService {
    /**
     * @see org.kuali.rice.krad.service.RiceApplicationConfigurationService#getBaseInquiryUrl(java.lang.String)
     */
    public String getBaseInquiryUrl(String businessObjectClassName);

    /**
     * @see org.kuali.rice.krad.service.RiceApplicationConfigurationService#getBaseLookupUrl(java.lang.String)
     */
    public String getBaseLookupUrl(String businessObjectClassName);


    public String getBaseHelpUrl(String businessObjectClassName);

}
