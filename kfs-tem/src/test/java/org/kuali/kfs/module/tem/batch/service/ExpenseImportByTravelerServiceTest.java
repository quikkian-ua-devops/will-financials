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
package org.kuali.kfs.module.tem.batch.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.kfs.coreservice.api.parameter.Parameter;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.businessobject.defaultvalue.NextAgencyStagingDataIdFinder;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.SequenceAccessorService;
import org.kuali.kfs.krad.util.ErrorMessage;

@ConfigureContext(session = khuntley)
public class ExpenseImportByTravelerServiceTest extends KualiTestBase {

    private ExpenseImportByTravelerService expenseImportByTravelerService;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    private SequenceAccessorService sas;
    private ParameterService parameterService;

    private final static String EMPLOYEE_ID = "123456789";
    private final static String CUSTOMER_NUM = "ABC1234";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        expenseImportByTravelerService = SpringContext.getBean(ExpenseImportByTravelerService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        sas = SpringContext.getBean(SequenceAccessorService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
    }


    /**
     *
     * This method tests {@link ExpenseImportByTravelerService#validateAccountingInfo(TemProfile, AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testValidateAccountingInfo() {
        AgencyStagingData agency = createAgencyStagingData();
        TemProfile profile = createTemProfile();
        // parameter is defaulted to 6000, but there are no valid combos that
        // will work with 6000. Set it to 5000 for testing purposes.
        Parameter param = parameterService.getParameter(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_AIRFARE_OBJECT_CODE);
        Parameter.Builder builder = Parameter.Builder.create(param);
        builder.setValue("5000");
        parameterService.updateParameter(builder.build());

        // success case
        expenseImportByTravelerService.validateAccountingInfo(agency);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR));
        assertTrue(agency.getTripAccountingInformation().size()==1);
        TripAccountingInformation accountingInfo = agency.getTripAccountingInformation().get(0);
        assertTrue(StringUtils.equals(accountingInfo.getTripAccountNumber(), profile.getDefaultAccount()));
        assertTrue(StringUtils.equals(accountingInfo.getTripSubAccountNumber(), profile.getDefaultSubAccount()));
        assertTrue(StringUtils.equals(accountingInfo.getProjectCode(), profile.getDefaultProjectCode()));
    }

    /**
     *
     * This method tests {@link ExpenseImportByTravelerService#validateTraveler(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testValidateTraveler() {
        TemProfile employee = createTemProfile();
        employee.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        employee.setEmployeeId(EMPLOYEE_ID);
        businessObjectService.save(employee);

        TemProfile customer = createTemProfile();
        customer.setTravelerTypeCode(TemConstants.NONEMP_TRAVELER_TYP_CD);
        customer.setCustomerNumber(CUSTOMER_NUM);
        businessObjectService.save(customer);

        AgencyStagingData agency = createAgencyStagingData();
        agency.setTravelerId("987654321");
        List<ErrorMessage> errors = expenseImportByTravelerService.validateTraveler(agency);

        assertTrue(!errors.isEmpty());
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_TRAVELER));

        agency.setTravelerId(EMPLOYEE_ID);
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        TemProfile empProfile = expenseImportByTravelerService.getTraveler(agency);
        assertTrue(empProfile.getEmployeeId().equals(agency.getTravelerId()));
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR));

        agency = createAgencyStagingData();
        agency.setTravelerId(CUSTOMER_NUM);
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        TemProfile custProfile = expenseImportByTravelerService.getTraveler(agency);
        assertTrue(custProfile.getCustomerNumber().equals(agency.getTravelerId()));
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR));

    }

    /**
     *
     * This method tests {@link ExpenseImportByTravelerService#isDuplicate(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testIsDuplicate() {
        AgencyStagingData dbData = createAgencyStagingData();
        businessObjectService.save(dbData);

        // duplicate entry test
        AgencyStagingData importData = createAgencyStagingData();
        List<ErrorMessage> errors = expenseImportByTravelerService.validateDuplicateData(importData);
        assertTrue(!errors.isEmpty());

        // not a duplicate
        importData.setTravelerId(CUSTOMER_NUM);
        errors = expenseImportByTravelerService.validateDuplicateData(importData);
        assertTrue(errors.isEmpty());
    }

    /**
     *
     * This method tests {@link ExpenseImportByTravelerService#areMandatoryFieldsPresent(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testAreMandatoryFieldsPresent() {
        AgencyStagingData agency = createAgencyStagingData();

        List<ErrorMessage> errors = expenseImportByTravelerService.validateMandatoryFieldsPresent(agency);

        // all fields present
        assertTrue(errors.isEmpty());

        // missing fields, testing in reverse order of the if block to hit all possible checks
        agency.setTripInvoiceNumber(null);
        errors = expenseImportByTravelerService.validateMandatoryFieldsPresent(agency);
        assertFalse(errors.isEmpty());
        agency.setTripExpenseAmount("");
        errors = expenseImportByTravelerService.validateMandatoryFieldsPresent(agency);
        assertFalse(errors.isEmpty());
        agency.setTransactionPostingDate(null);
        errors = expenseImportByTravelerService.validateMandatoryFieldsPresent(agency);
        assertFalse(errors.isEmpty());
        agency.setAgency(null);
        errors = expenseImportByTravelerService.validateMandatoryFieldsPresent(agency);
        assertFalse(errors.isEmpty());
        agency.setAirTicketNumber("");
        errors = expenseImportByTravelerService.validateMandatoryFieldsPresent(agency);
        assertFalse(errors.isEmpty());
        agency.setTravelerId(null);
        errors = expenseImportByTravelerService.validateMandatoryFieldsPresent(agency);
        assertFalse(errors.isEmpty());
        agency.setCreditCardOrAgencyCode(null);
        errors = expenseImportByTravelerService.validateMandatoryFieldsPresent(agency);
        assertFalse(errors.isEmpty());
    }

    protected TemProfile createTemProfile() {
        TemProfile profile = new TemProfile();
        Integer newProfileId = sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_SEQ_NAME).intValue();
        profile.setProfileId(newProfileId);
        profile.getTemProfileAddress().setProfileId(newProfileId);
        profile.setDefaultChartCode("BL");
        profile.setDefaultAccount("1031400");
        profile.setDefaultSubAccount("ADV");
        profile.setDefaultProjectCode("KUL");
        profile.setDateOfBirth(dateTimeService.getCurrentSqlDate());
        profile.setGender("M");
        profile.setHomeDeptOrgCode("BL");
        profile.setHomeDeptChartOfAccountsCode("BL");
        return profile;
    }

    protected AgencyStagingData createAgencyStagingData() {
        AgencyStagingData agency = new AgencyStagingData();
        NextAgencyStagingDataIdFinder idFinder = new NextAgencyStagingDataIdFinder();
        // mandatory fields

        agency.setId(Integer.valueOf(idFinder.getValue()));
        agency.setImportBy(ExpenseImportTypes.IMPORT_BY_TRAVELLER);
        agency.setCreditCardOrAgencyCode("1234");
        agency.setTravelerName("Traveler Bob");
        agency.setTravelerId(EMPLOYEE_ID);
        agency.setAirTicketNumber("12345678");
        agency.setLodgingItineraryNumber("12345");
        agency.setRentalCarItineraryNumber("54321");
        agency.setAgency("agency name");
        agency.setTransactionPostingDate(dateTimeService.getCurrentSqlDate());
        agency.setTripExpenseAmount(new KualiDecimal(123.45));
        agency.setTripInvoiceNumber("invoice12345");

        TripAccountingInformation account = new TripAccountingInformation();
        account.setTripChartCode("BL");
        account.setTripAccountNumber("1031400");
        account.setTripSubAccountNumber("ADV");
        account.setProjectCode("KUL");
        agency.addTripAccountingInformation(account);

        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        return agency;
    }
}
