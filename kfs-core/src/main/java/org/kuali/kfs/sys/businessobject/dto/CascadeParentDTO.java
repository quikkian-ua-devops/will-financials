package org.kuali.kfs.sys.businessobject.dto;

public class CascadeParentDTO {

    private String tableCode;
    private String fieldCode;

    public CascadeParentDTO() {
    }

    public CascadeParentDTO(String tableCode, String fieldCode) {
        this.tableCode = tableCode;
        this.fieldCode = fieldCode;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }
}
