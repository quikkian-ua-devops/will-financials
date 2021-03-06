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
package org.kuali.kfs.vnd.businessobject.inquiry;

import org.kuali.kfs.kns.inquiry.KualiInquirableImpl;
import org.kuali.kfs.vnd.businessobject.DebarredVendorMatch;
import org.kuali.kfs.vnd.businessobject.VendorAlias;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.List;
import java.util.Map;

public class VendorExclusionInquirable extends KualiInquirableImpl {

    /**
     * @see org.kuali.rice.kns.inquiry.KualiInquirableImpl#getBusinessObject(java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        DebarredVendorMatch match = (DebarredVendorMatch) super.getBusinessObject(fieldValues);
        List<VendorAlias> vendorAliases = match.getVendorDetail().getVendorAliases();
        StringBuffer concatenatedAliases = new StringBuffer();
        for (VendorAlias alias : vendorAliases) {
            concatenatedAliases.append(alias.getVendorAliasName());
            concatenatedAliases.append("/n");
        }
        match.setConcatenatedAliases(concatenatedAliases.toString());
        return match;
    }
}
