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
package org.kuali.kfs.module.cam.document.service;

import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.document.service.AssetLocationService.LocationField;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.HashMap;
import java.util.Map;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

@ConfigureContext(session = khuntley)
public class AssetLocationServiceTest extends KualiTestBase {
    private AssetLocationService assetLocationService;
    private AssetGlobalDetail onCampusObject;
    private AssetGlobalDetail offcampusObject;
    private static final Map<LocationField, String> fieldMap = new HashMap<LocationField, String>();

    static {
        fieldMap.put(LocationField.CAMPUS_CODE, CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE);
        fieldMap.put(LocationField.BUILDING_CODE, CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE);
        fieldMap.put(LocationField.ROOM_NUMBER, CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER);
        fieldMap.put(LocationField.SUB_ROOM_NUMBER, CamsPropertyConstants.AssetGlobalDetail.BUILDING_SUB_ROOM_NUMBER);
        fieldMap.put(LocationField.CONTACT_NAME, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_NAME);
        fieldMap.put(LocationField.STREET_ADDRESS, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_ADDRESS);
        fieldMap.put(LocationField.CITY_NAME, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_CITY_NAME);
        fieldMap.put(LocationField.STATE_CODE, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_STATE_CODE);
        fieldMap.put(LocationField.ZIP_CODE, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_ZIP_CODE);
        fieldMap.put(LocationField.COUNTRY_CODE, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_COUNTRY_CODE);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.assetLocationService = SpringContext.getBean(AssetLocationService.class);
        onCampusObject = new AssetGlobalDetail();
        onCampusObject.setCampusCode("BL");
        onCampusObject.setBuildingCode("BL001");
        onCampusObject.setBuildingRoomNumber("B009");
        onCampusObject.setBuildingSubRoomNumber("23");

        offcampusObject = new AssetGlobalDetail();
        offcampusObject.setOffCampusName("eddsdsd");
        offcampusObject.setOffCampusAddress("Addreed");
        offcampusObject.setOffCampusCityName("City");
        offcampusObject.setOffCampusStateCode("IN");
        offcampusObject.setOffCampusZipCode("47401");
        offcampusObject.setOffCampusCountryCode("US");

    }

    public void testValidateLocation_OnCampus() throws Exception {

        AssetType assetType = new AssetType();
        assetType.setMovingIndicator(true);
        // when conditions are valid
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when building code is null
        onCampusObject.setBuildingCode(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when room number is null
        onCampusObject.setBuildingCode("BL001");
        onCampusObject.setBuildingRoomNumber(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();


        // when sub room number is optional
        onCampusObject.setBuildingRoomNumber("B034F");
        onCampusObject.setBuildingSubRoomNumber(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // test condition when building required indicator is true
        assetType.setMovingIndicator(false);
        assetType.setRequiredBuildingIndicator(true);
        onCampusObject.setBuildingRoomNumber(null);
        onCampusObject.setBuildingSubRoomNumber(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertFalse(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when building code is null
        onCampusObject.setBuildingCode(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when room number is not null
        onCampusObject.setBuildingCode("BL001");
        onCampusObject.setBuildingRoomNumber("YYGBJGJH");
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();


        // when sub room number is not null
        onCampusObject.setBuildingRoomNumber(null);
        onCampusObject.setBuildingSubRoomNumber("HGBJHNGBJH");
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();
    }

    public void testValidateLocation_OffCampus() throws Exception {
        AssetType assetType = new AssetType();
        assetType.setMovingIndicator(false);
        assetType.setRequiredBuildingIndicator(false);
        // when conditions are valid
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertFalse(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when contact name is null
        this.offcampusObject.setOffCampusName(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when street address is null
        this.offcampusObject.setOffCampusName("me");
        this.offcampusObject.setOffCampusAddress(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when city name is null
        this.offcampusObject.setOffCampusAddress("Street");
        this.offcampusObject.setOffCampusCityName(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when state code is null
        this.offcampusObject.setOffCampusCityName("City");
        this.offcampusObject.setOffCampusStateCode(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when zip code is null
        this.offcampusObject.setOffCampusStateCode("MI");
        this.offcampusObject.setOffCampusZipCode(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when country code is null
        this.offcampusObject.setOffCampusZipCode("34343");
        this.offcampusObject.setOffCampusCountryCode(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

    }

    public void testValidateLocation_CapitalAsset() throws Exception {

        // when asset type is not defined
        AssetType assetType = null;
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // required building and offcampus
        assetType = new AssetType();
        assetType.setRequiredBuildingIndicator(true);
        assetType.setMovingIndicator(false);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // req bldg and moving indicators value false, oncampus not allowed
        assetType.setRequiredBuildingIndicator(false);
        assetType.setMovingIndicator(false);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when no information is available for capital asset
        assetType.setRequiredBuildingIndicator(true);
        AssetGlobalDetail blankObject = new AssetGlobalDetail();
        this.assetLocationService.validateLocation(fieldMap, blankObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when both are available
        this.onCampusObject.setOffCampusAddress("Street");
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, true, assetType);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();
    }

    public void testValidateLocation_NonCapitalAsset() throws Exception {
        // when no information is available for capital asset
        AssetGlobalDetail blankObject = new AssetGlobalDetail();
        this.assetLocationService.validateLocation(fieldMap, blankObject, false, null);
        assertFalse(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when on-campus, all fields are optional
        this.onCampusObject.setBuildingCode(null);
        this.assetLocationService.validateLocation(fieldMap, onCampusObject, false, null);
        assertFalse(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when off-campus, validate location
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, false, null);
        assertFalse(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

        // when off-campus, validate location
        this.offcampusObject.setOffCampusCountryCode(null);
        this.assetLocationService.validateLocation(fieldMap, offcampusObject, false, null);
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        GlobalVariables.getMessageMap().clearErrorMessages();

    }
}

