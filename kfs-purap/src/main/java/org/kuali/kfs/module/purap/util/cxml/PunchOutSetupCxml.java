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
/*
 * Created on Mar 10, 2005
 *
 */
package org.kuali.kfs.module.purap.util.cxml;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.B2BInformation;
import org.kuali.kfs.module.purap.util.PurApDateFormatUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PunchOutSetupCxml {
    private Person user;
    private B2BInformation b2bInformation;

    public PunchOutSetupCxml(Person u, B2BInformation b) {
        user = u;
        b2bInformation = b;
    }

    /**
     * Get cxml punch out request xml
     *
     * @return xml for punch out request
     * Note: this method is moved into B2BShoppingServiceImpl for easier institution customization
     */
    @Deprecated
    public String getPunchOutSetupRequestMessage() {
        StringBuffer cxml = new StringBuffer();
        Date d = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        SimpleDateFormat date = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.CXML_SIMPLE_DATE_FORMAT);
        SimpleDateFormat time = PurApDateFormatUtils.getSimpleDateFormat(PurapConstants.NamedDateFormats.CXML_SIMPLE_TIME_FORMAT);

        // doing as two parts b/c they want a T instead of space
        // between them, and SimpleDateFormat doesn't allow putting the
        // constant "T" in the string

        cxml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        cxml.append("<!DOCTYPE cXML SYSTEM \"cXML.dtd\">\n");
        cxml.append("<cXML payloadID=\"irrelevant\" xml:lang=\"en-US\" timestamp=\"").append(date.format(d)).append("T")
            .append(time.format(d)).append("-05:00").append("\">\n");

        // note that timezone is hard coded b/c this is the format
        // they wanted, but SimpleDateFormat returns -0500, so rather than
        // parse it just hard-coded

        cxml.append("  <Header>\n");
        cxml.append("    <From>\n");
        cxml.append("      <Credential domain=\"NetworkId\">\n");
        cxml.append("        <Identity>").append(b2bInformation.getIdentity()).append("</Identity>\n");
        cxml.append("      </Credential>\n");
        cxml.append("    </From>\n");
        cxml.append("    <To>\n");
        cxml.append("      <Credential domain=\"DUNS\">\n");
        cxml.append("        <Identity>").append(b2bInformation.getIdentity()).append("</Identity>\n");
        cxml.append("      </Credential>\n");
        cxml.append("      <Credential domain=\"internalsupplierid\">\n");
        cxml.append("        <Identity>1016</Identity>\n");
        cxml.append("      </Credential>\n");
        cxml.append("    </To>\n");
        cxml.append("    <Sender>\n");
        cxml.append("      <Credential domain=\"TOPSNetworkUserId\">\n");
        cxml.append("        <Identity>").append(user.getPrincipalName().toUpperCase()).append("</Identity>\n");
        cxml.append("        <SharedSecret>").append(b2bInformation.getPassword()).append("</SharedSecret>\n");
        cxml.append("      </Credential>\n");
        cxml.append("      <UserAgent>").append(b2bInformation.getUserAgent()).append("</UserAgent>\n");
        cxml.append("    </Sender>\n");
        cxml.append("  </Header>\n");
        cxml.append("  <Request deploymentMode=\"").append(b2bInformation.getEnvironment()).append("\">\n");
        cxml.append("    <PunchOutSetupRequest operation=\"create\">\n");
        cxml.append("      <BuyerCookie>").append(user.getPrincipalName().toUpperCase()).append("</BuyerCookie>\n");
        //cxml.append(" <Extrinsic
        // name=\"UserEmail\">jdoe@TOPS.com</Extrinsic>\n"); // we can't reliably
        // get the e-mail address, so we're leaving it out
        cxml.append("      <Extrinsic name=\"UniqueName\">").append(user.getPrincipalName().toUpperCase()).append("</Extrinsic>\n");
        cxml.append("      <Extrinsic name=\"Department\">IU").append(user.getCampusCode()).append(user.getPrimaryDepartmentCode()).append("</Extrinsic>\n");
        cxml.append("      <Extrinsic name=\"Campus\">").append(user.getCampusCode()).append("</Extrinsic>\n");
        cxml.append("      <BrowserFormPost>\n");
        cxml.append("        <URL>").append(b2bInformation.getPunchbackURL()).append("</URL>\n");
        cxml.append("      </BrowserFormPost>\n");
        cxml.append("      <Contact role=\"endUser\">\n");
        cxml.append("        <Name xml:lang=\"en\">").append(user.getName()).append("</Name>\n");
        //cxml.append(" <Email>jdoe@TOPS.com</Email>\n"); // again, we can't
        // reliably get this, so we're leaving it out
        cxml.append("      </Contact>\n");
        cxml.append("      <SupplierSetup>\n");
        cxml.append("        <URL>").append(b2bInformation.getPunchoutURL()).append("</URL>\n");
        cxml.append("      </SupplierSetup>\n");
        cxml.append("    </PunchOutSetupRequest>\n");
        cxml.append("  </Request>\n");
        cxml.append("</cXML>\n");

        return cxml.toString();
    }

}

