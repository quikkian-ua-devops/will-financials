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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.cg.businessobject.AgencyAddress;


/**
 * Fixture class for CG Agency Address
 */
public enum ARAgencyAddressFixture {


    CG_AGENCY_ADD1("11505", new Long(26), "P"), CG_AGENCY_ADD2("11505", new Long(26), "A");

    private String agencyNumber;
    private Long agencyAddressIdentifier;
    private String customerAddressTypeCode;

    private ARAgencyAddressFixture(String agencyNumber, Long agencyAddressIdentifier, String customerAddressTypeCode) {

        this.agencyNumber = agencyNumber;
        this.agencyAddressIdentifier = agencyAddressIdentifier;
        this.customerAddressTypeCode = customerAddressTypeCode;

    }

    public AgencyAddress createAgencyAddress() {
        AgencyAddress agencyAddress = new AgencyAddress();
        agencyAddress.setAgencyNumber(this.agencyNumber);
        agencyAddress.setAgencyAddressIdentifier(this.agencyAddressIdentifier);
        agencyAddress.setCustomerAddressTypeCode(this.customerAddressTypeCode);
        return agencyAddress;
    }
}
