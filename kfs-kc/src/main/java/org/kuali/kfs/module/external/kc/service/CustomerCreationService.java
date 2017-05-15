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
package org.kuali.kfs.module.external.kc.service;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.CustomerCreationStatusDto;
import org.kuali.kfs.module.external.kc.dto.CustomerTypeDto;
import org.kuali.kra.external.sponsor.SponsorDTO;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

@WebService(name = KcConstants.CustomerCreationService.WEB_SERVICE_NAME,
    targetNamespace = KcConstants.KFS_NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL,
    parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CustomerCreationService {

    public CustomerCreationStatusDto createCustomer(@WebParam(name = "sponsor") SponsorDTO sponsor,
                                                    @WebParam(name = "initiatedByPrincipalName") String initiatedByPrincipalName);

    public List<CustomerTypeDto> getCustomerTypes();

    public boolean isValidCustomer(@WebParam(name = "customerNumber") String customerNumber);
}
