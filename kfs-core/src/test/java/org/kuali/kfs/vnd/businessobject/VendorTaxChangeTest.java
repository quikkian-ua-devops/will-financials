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
package org.kuali.kfs.vnd.businessobject;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

@ConfigureContext(session = khuntley)
public class VendorTaxChangeTest extends KualiTestBase {

    public void testToString() {
        VendorTaxChange vendorTaxChange = new VendorTaxChange();

        vendorTaxChange.setVendorTaxChangeGeneratedIdentifier(new Integer(1010101010));
        vendorTaxChange.setVendorPreviousTaxNumber("999999999");
        vendorTaxChange.setVendorPreviousTaxTypeCode("XXX");
        vendorTaxChange.setVendorTaxChangePersonIdentifier("username");

        String vendorTaxChangeToString = vendorTaxChange.toString();
        assertFalse("vendorPreviousTaxNumber should not show on toString", vendorTaxChangeToString.contains("vendorPreviousTaxNumber"));
        assertFalse("vendorPreviousTaxTypeCode should not show on toString", vendorTaxChangeToString.contains("vendorPreviousTaxTypeCode"));

    }

}
