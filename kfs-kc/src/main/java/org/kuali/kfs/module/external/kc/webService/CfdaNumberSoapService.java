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
package org.kuali.kfs.module.external.kc.webService;
/*
 *
 */

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kra.external.Cfda.service.CfdaNumberService;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class was generated by Apache CXF 2.2.10
 * Wed Mar 02 08:02:23 HST 2011
 * Generated source version: 2.2.10
 */


@WebServiceClient(name = KcConstants.Cfda.SOAP_SERVICE_NAME,
    wsdlLocation = "http://test.kc.kuali.org:80/kc-trunk/remoting/cfdaNumberSoapService?wsdl",
    targetNamespace = KcConstants.KC_NAMESPACE_URI)
public class CfdaNumberSoapService extends KfsKcSoapService {

    public final static QName CfdaNumberServicePort = new QName(KcConstants.KC_NAMESPACE_URI, KcConstants.Cfda.SERVICE_PORT);

    static {
        try {
            getWsdl(KcConstants.Cfda.SERVICE);
        } catch (MalformedURLException e) {
            LOG.warn("Can not initialize the wsdl");
        }
    }

    public CfdaNumberSoapService() throws MalformedURLException {
        super(getWsdl(KcConstants.Cfda.SERVICE), KcConstants.Cfda.SERVICE);
    }


    /**
     * @return returns CfdaNumberService
     */
    @WebEndpoint(name = KcConstants.Cfda.SERVICE_PORT)
    public CfdaNumberService getCfdaNumberServicePort() {
        return super.getPort(CfdaNumberServicePort, CfdaNumberService.class);
    }

    /**
     * @param features A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return returns CfdaNumberService
     */
    @WebEndpoint(name = KcConstants.Cfda.SERVICE_PORT)
    public CfdaNumberService getCfdaNumberServicePort(WebServiceFeature... features) {
        return super.getPort(CfdaNumberServicePort, CfdaNumberService.class, features);
    }

    public URL getWsdl() throws MalformedURLException {
        return super.getWsdl(KcConstants.Cfda.SERVICE);
    }

}
