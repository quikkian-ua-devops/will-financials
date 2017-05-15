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
package org.kuali.kfs.module.cg.document.validation.impl;

import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.fixture.AgencyFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

/**
 * This class tests the AgencyRule validation Class
 */
@ConfigureContext(session = khuntley)
public class AgencyRuleTest extends MaintenanceRuleTestBase {

    private AgencyRule rule;
    private Agency agency;
    private AgencyAddress agencyAddress;
    private BusinessObjectService boService;
    private Long agencyNumber;

    private static final String AGENCY_ADDRESS_NAME = "Address Name";
    private static final String AGENCY_ADDRESS_COUNTRY_CODE_US = "US";
    private static final String AGENCY_ADDRESS_COUNTRY_CODE_RO = "RO";
    private static final String AGENCY_ADDRESS_STATE_CODE = "NY";
    private static final String AGENCY_ADDRESS_ZIP_CODE = "14850";
    private static final String AGENCY_ADDRESS_PROVINCE = "Iasi";

    public void setUp() throws Exception {
        super.setUp();
        agencyNumber = new Long(12500);
        boService = SpringContext.getBean(BusinessObjectService.class);
        agency = new Agency();
        agencyAddress = new AgencyAddress();
        rule = (AgencyRule) setupMaintDocRule(newMaintDoc(agency), AgencyRule.class);
    }


    public void testCheckAgencyReportsTo_True() {
        agency = (Agency) boService.findBySinglePrimaryKey(Agency.class, agencyNumber);
        assertTrue(rule.checkAgencyReportsTo(newMaintDoc(agency)));
    }

    public void testValidateAgencyType_True() {
        agency = AgencyFixture.CG_AGENCY1.createAgency();
        rule.newAgency = agency;
        assertTrue(rule.validateAgencyType(newMaintDoc(agency)));
    }

    public void testValidateAgencyReportingName_True() {
        agency = AgencyFixture.CG_AGENCY1.createAgency();
        rule.newAgency = agency;
        assertTrue(rule.validateAgencyReportingName(newMaintDoc(agency)));
    }


    /**
     * This method if checkAddressIsValid returns false when country code is US and state code and zip code are empty
     */
    public void testCheckAddressIsValid_CountryUS_False() {
        agencyAddress.setAgencyAddressName(AGENCY_ADDRESS_NAME);
        agencyAddress.setAgencyCountryCode(AGENCY_ADDRESS_COUNTRY_CODE_US);
        agencyAddress.setAgencyStateCode("");
        agencyAddress.setAgencyZipCode("");
        //To set customer exists value to "Create New Customer"
        agency.setCustomerCreationOptionCode(CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE);
        rule.newAgency = agency;
        AgencyRule rule = (AgencyRule) setupMaintDocRule(newMaintDoc(agency), AgencyRule.class);
        boolean result = rule.checkAddressIsValid(agencyAddress);
        assertEquals("When agency address has country code " + AGENCY_ADDRESS_COUNTRY_CODE_US + " and state code and zip code are empty checkAddressIsValid should return false. ", false, result);
    }


    /**
     * This method checks that checkAddressIsValid returns false when country code is not US and InternationalProvinceName and
     * InternationalMailCode are not set.
     */
    public void testCheckAddressIsValid_CountryNonUS_False() {
        agencyAddress.setAgencyAddressName(AGENCY_ADDRESS_NAME);
        agencyAddress.setAgencyCountryCode(AGENCY_ADDRESS_COUNTRY_CODE_RO);
        agencyAddress.setAgencyAddressInternationalProvinceName("");
        agencyAddress.setAgencyZipCode("");
        //To set customer exists value to "Create New Customer"
        agency.setCustomerCreationOptionCode(CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE);
        rule.newAgency = agency;
        AgencyRule rule = (AgencyRule) setupMaintDocRule(newMaintDoc(agency), AgencyRule.class);
        boolean result = rule.checkAddressIsValid(agencyAddress);
        assertEquals("When agency address has country code " + AGENCY_ADDRESS_COUNTRY_CODE_RO + " and province and International Mail Code are empty checkAddressIsValid should return false. ", false, result);
    }
}
