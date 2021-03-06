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
package org.kuali.kfs.fp.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;

import java.util.HashMap;
import java.util.Map;

public class CapitalAssetInformationDetail extends PersistableBusinessObjectBase {

    //primary key fields..
    protected String documentNumber;
    protected Integer capitalAssetLineNumber;
    protected Integer itemLineNumber;
    protected String campusCode;
    protected String buildingCode;
    protected String buildingRoomNumber;
    protected String buildingSubRoomNumber;
    protected String capitalAssetTagNumber;
    protected String capitalAssetSerialNumber;

    protected CampusEbo campus;
    protected Building building;
    protected Room room;
    protected CapitalAssetInformation capitalAssetInformation;


    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the itemLineNumber attribute.
     *
     * @return Returns the itemLineNumber.
     */
    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    /**
     * Sets the itemLineNumber attribute value.
     *
     * @param itemLineNumber The itemLineNumber to set.
     */
    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    /**
     * Gets the campusCode attribute.
     *
     * @return Returns the campusCode.
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute value.
     *
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    /**
     * Gets the buildingCode attribute.
     *
     * @return Returns the buildingCode.
     */
    public String getBuildingCode() {
        return buildingCode;
    }

    /**
     * Sets the buildingCode attribute value.
     *
     * @param buildingCode The buildingCode to set.
     */
    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    /**
     * Gets the buildingRoomNumber attribute.
     *
     * @return Returns the buildingRoomNumber.
     */
    public String getBuildingRoomNumber() {
        return buildingRoomNumber;
    }

    /**
     * Sets the buildingRoomNumber attribute value.
     *
     * @param buildingRoomNumber The buildingRoomNumber to set.
     */
    public void setBuildingRoomNumber(String buildingRoomNumber) {
        this.buildingRoomNumber = buildingRoomNumber;
    }

    /**
     * Gets the capitalAssetTagNumber attribute.
     *
     * @return Returns the capitalAssetTagNumber.
     */
    public String getCapitalAssetTagNumber() {
        return capitalAssetTagNumber;
    }

    /**
     * Sets the capitalAssetTagNumber attribute value.
     *
     * @param capitalAssetTagNumber The capitalAssetTagNumber to set.
     */
    public void setCapitalAssetTagNumber(String capitalAssetTagNumber) {
        this.capitalAssetTagNumber = capitalAssetTagNumber;
    }

    /**
     * Gets the capitalAssetSerialNumber attribute.
     *
     * @return Returns the capitalAssetSerialNumber.
     */
    public String getCapitalAssetSerialNumber() {
        return capitalAssetSerialNumber;
    }

    /**
     * Sets the capitalAssetSerialNumber attribute value.
     *
     * @param capitalAssetSerialNumber The capitalAssetSerialNumber to set.
     */
    public void setCapitalAssetSerialNumber(String capitalAssetSerialNumber) {
        this.capitalAssetSerialNumber = capitalAssetSerialNumber;
    }

    /**
     * Gets the campus attribute.
     *
     * @return Returns the campus.
     */
    public CampusEbo getCampus() {
        if (StringUtils.isBlank(campusCode)) {
            campus = null;
        } else {
            if (campus == null || !StringUtils.equals(campus.getCode(), campusCode)) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CampusEbo.class);
                if (moduleService != null) {
                    Map<String, Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, campusCode);
                    campus = moduleService.getExternalizableBusinessObject(CampusEbo.class, keys);
                } else {
                    throw new RuntimeException("CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed.");
                }
            }
        }

        return campus;
    }

    /**
     * Sets the campus attribute value.
     *
     * @param campus The campus to set.
     */
    public void setCampus(CampusEbo campus) {
        this.campus = campus;
    }

    /**
     * Gets the building attribute.
     *
     * @return Returns the building.
     */
    public Building getBuilding() {
        return building;
    }

    /**
     * Sets the building attribute value.
     *
     * @param building The building to set.
     */
    public void setBuilding(Building building) {
        this.building = building;
    }

    /**
     * Gets the room attribute.
     *
     * @return Returns the room.
     */
    public Room getRoom() {
        return room;
    }

    /**
     * Sets the room attribute value.
     *
     * @param room The room to set.
     */
    public void setRoom(Room room) {
        this.room = room;
    }

    /**
     * Gets the capitalAssetInformation attribute.
     *
     * @return Returns the capitalAssetInformation.
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * Sets the capitalAssetInformation attribute value.
     *
     * @param capitalAssetInformation The capitalAssetInformation to set.
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }

    /**
     * Gets the buildingSubRoomNumber attribute.
     *
     * @return Returns the buildingSubRoomNumber.
     */
    public String getBuildingSubRoomNumber() {
        return buildingSubRoomNumber;
    }

    /**
     * Sets the buildingSubRoomNumber attribute value.
     *
     * @param buildingSubRoomNumber The buildingSubRoomNumber to set.
     */
    public void setBuildingSubRoomNumber(String buildingSubRoomNumber) {
        this.buildingSubRoomNumber = buildingSubRoomNumber;
    }

    /**
     * Gets the capitalAssetLineNumber attribute.
     *
     * @return Returns the capitalAssetLineNumber.
     */
    public Integer getCapitalAssetLineNumber() {
        return capitalAssetLineNumber;
    }

    /**
     * Sets the capitalAssetLineNumber attribute value.
     *
     * @param capitalAssetLineNumber The capitalAssetLineNumber to set.
     */
    public void setCapitalAssetLineNumber(Integer capitalAssetLineNumber) {
        this.capitalAssetLineNumber = capitalAssetLineNumber;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     *
     * @return Map a map with the primitive field names as the key and the primitive values as the map value.
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = new HashMap<String, Object>();

        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_LINE_NUMBER, this.getCapitalAssetLineNumber());
        simpleValues.put(KFSPropertyConstants.ITEM_LINE_NUMBER, this.getItemLineNumber());
        simpleValues.put(KFSPropertyConstants.CAMPUS_CODE, this.getCampusCode());
        simpleValues.put(KFSPropertyConstants.BUILDING_CODE, this.getBuildingCode());
        simpleValues.put(KFSPropertyConstants.BUILDING_ROOM_NUMBER, this.getBuildingRoomNumber());

        return simpleValues;
    }
}
