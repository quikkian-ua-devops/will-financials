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
package org.kuali.kfs.module.external.kc.webService;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kra.external.awardpayment.AwardPaymentWebService;

/**
 * This class was generated by Apache CXF 2.2.10
 * Thu Sep 30 15:50:58 HST 2010
 * Generated source version: 2.2.10
 *
 */


@WebServiceClient(name = KcConstants.Award.SOAP_SERVICE_NAME,
                  wsdlLocation = "http://test.kc.kuali.org/kc-trunk/remoting/awardPaymentWebSoapService?wsdl",
                  targetNamespace = KcConstants.KC_NAMESPACE_URI)
public class AwardPaymentWebSoapService extends KfsKcSoapService {

    public final static QName AwardPaymentWebServicePort = new QName(KcConstants.KC_NAMESPACE_URI, KcConstants.AwardPayment.SERVICE_PORT);
    static {
        try {
           getWsdl(KcConstants.AwardPayment.SERVICE);
         } catch (MalformedURLException e) {
             LOG.warn("Can not initialize the wsdl");
         }
    }

    public AwardPaymentWebSoapService() throws MalformedURLException {
        super(getWsdl(KcConstants.AwardPayment.SERVICE), KcConstants.AwardPayment.SERVICE);
    }


    /**
     *
     * @return
     *     returns AwardPaymentWebService
     */
    @WebEndpoint(name = KcConstants.AwardPayment.SERVICE_PORT)
    public AwardPaymentWebService getAwardPaymentWebServicePort() {
        return super.getPort(AwardPaymentWebServicePort, AwardPaymentWebService.class);
    }

    /**
     *
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns AwardPaymentWebService
     */
    @WebEndpoint(name = KcConstants.Award.SERVICE_PORT)
    public AwardPaymentWebService getAwardPaymentWebServicePort(WebServiceFeature... features) {
        return super.getPort(AwardPaymentWebServicePort, AwardPaymentWebService.class, features);
    }

    @Override
    public URL getWsdl() throws MalformedURLException {
        return super.getWsdl(KcConstants.AwardPayment.SERVICE);
    }

}
