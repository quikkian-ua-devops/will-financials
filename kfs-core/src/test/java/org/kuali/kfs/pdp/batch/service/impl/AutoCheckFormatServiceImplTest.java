package org.kuali.kfs.pdp.batch.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.pdp.businessobject.AutoCheckFormat;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.DisbursementNumberRange;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.location.api.campus.Campus;
import org.kuali.rice.location.api.campus.CampusService;

public class AutoCheckFormatServiceImplTest {

    @TestSubject
    private AutoCheckFormatServiceImpl autoCheckFormatServ = new AutoCheckFormatServiceImpl();
    
    @Mock
    private static FormatSelection fs = new FormatSelection();
    
    @BeforeClass
    public static void setUp() throws Exception{
        fs = EasyMock.createMock(FormatSelection.class);
    }
    
    @Test
    public void testNullCustomerListInFormatSelectionWhenGeneratingFomatReadyCustomerList() throws Exception {
        EasyMock.expect(fs.getCustomerList()).andReturn(null);
        EasyMock.replay(fs);
        
        assertNotNull(autoCheckFormatServ.generateListOfCustomerProfilesReadyForFormat(fs));
        EasyMock.reset(fs);
    }
    
    @Test
    public void testGeneratingFomatReadyCustomersListWithFormatSelectionCustomersWithWrongCampus() throws Exception {
        CustomerProfile cp1 = new CustomerProfile();
        cp1.setDefaultPhysicalCampusProcessingCode("IR");
        
        ArrayList<CustomerProfile> customerList = new ArrayList<CustomerProfile>();
        customerList.add(cp1);
        
        EasyMock.expect(fs.getCustomerList()).andReturn(customerList);
        EasyMock.expect(fs.getCampus()).andReturn("BL");
        EasyMock.replay(fs);
        
        //there is only only item in the list and it is marked for format as FALSE
        assertFalse(autoCheckFormatServ.generateListOfCustomerProfilesReadyForFormat(fs).get(0).isSelectedForFormat());
        
        EasyMock.reset(fs);
    }
    
    @Test
    public void testGeneratingFomatReadyCustomersListWithFormatSelectionCustomersWithMatchingCampuses() throws Exception {
        CustomerProfile cp1 = new CustomerProfile();
        cp1.setDefaultPhysicalCampusProcessingCode("BL");
        
        ArrayList<CustomerProfile> customerList = new ArrayList<CustomerProfile>();
        customerList.add(cp1);
        
        EasyMock.expect(fs.getCustomerList()).andReturn(customerList);
        EasyMock.expect(fs.getCampus()).andReturn("BL");
        EasyMock.replay(fs);
        
        //there is only only item in the list and it is marked for format as TRUE
        assertTrue(autoCheckFormatServ.generateListOfCustomerProfilesReadyForFormat(fs).get(0).isSelectedForFormat());
        
        EasyMock.reset(fs);
    }
    
    @Test
    public void testCreationOfAutoCheckFormatWhenFormatSelectionHasStartDate() throws Exception {
        //set format selection to have date already set
        EasyMock.expect(fs.getStartDate()).andReturn(new Date()).times(3);
        EasyMock.replay(fs);
        
        DateTimeService dtService = EasyMock.createMock(DateTimeService.class);
        EasyMock.expect(dtService.toDateTimeString(fs.getStartDate())).andReturn("date time");
        EasyMock.replay(dtService);
        autoCheckFormatServ.setDateTimeService(dtService);
        
        // the method should return a null
        assertTrue(ObjectUtils.isNull(autoCheckFormatServ.createAutoCheckFormat(fs)));
        
        EasyMock.reset(fs);
    }
    
    @Test
	public void testProcessChecksByCustomerProfileWhenProfileIDisNull() throws Exception {
    	CampusService cs = EasyMock.createMock(CampusService.class);
    	EasyMock.expect(cs.findAllCampuses()).andReturn(new ArrayList<Campus>());
    	EasyMock.replay(cs);
    	
    	autoCheckFormatServ.setCampusService(cs);
    	
    	boolean results = autoCheckFormatServ.processChecksByCustomerProfile(null);
		
		assertTrue(results);
	}

    @Test
	public void testProcessChecksByCustomerProfileWhenNoCustomerProfileExist() throws Exception {
    	String profileId = "10001";

    	BusinessObjectService bos = EasyMock.createMock(BusinessObjectService.class);
		EasyMock.expect(bos.findBySinglePrimaryKey(CustomerProfile.class, profileId)).andReturn(null);
    	EasyMock.replay(bos);
    	
    	autoCheckFormatServ.setBusinessObjectService(bos);
    	
    	boolean results = autoCheckFormatServ.processChecksByCustomerProfile(profileId);
    	
		assertFalse(results);
	}
    
    @Test
    public void testCreateAutoCheckFormatHasSetFormatSelectionData() throws Exception {
        // start date is null, so can proceed
        EasyMock.expect(fs.getStartDate()).andReturn(null);
        EasyMock.expect(fs.getCampus()).andReturn("BL");
        
        DateTimeService dtService = EasyMock.createMock(DateTimeService.class);
        long currentTimeMillis = System.currentTimeMillis();
        EasyMock.expect(dtService.getCurrentTimestamp()).andReturn(new Timestamp(currentTimeMillis));
        EasyMock.expect(dtService.toDateString(new Timestamp(currentTimeMillis))).andReturn(new Date().toString());
        EasyMock.replay(dtService);
        autoCheckFormatServ.setDateTimeService(dtService);
        
        EasyMock.expect(fs.getRangeList()).andReturn(new ArrayList<DisbursementNumberRange>());
        EasyMock.expect(fs.getCustomerList()).andReturn(null);
        EasyMock.replay(fs);
        
        AutoCheckFormat autoCheckFormat = autoCheckFormatServ.createAutoCheckFormat(fs);
        
        assertEquals(autoCheckFormat.getCampus(), "BL");
        assertEquals(autoCheckFormat.getPaymentTypes(), "all");
        assertEquals(autoCheckFormat.getRanges().size(), 0);
        assertEquals(autoCheckFormat.getCustomers().size(), 0);
        
        EasyMock.reset(fs);
    }

    
}
