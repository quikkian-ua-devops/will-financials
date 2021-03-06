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

package org.kuali.kfs.sys.batch;

import org.apache.commons.lang3.StringUtils;

public class DataDictionaryFilteredField {
    private String businessObjectSimpleName;
    private String propertyName;
    private boolean pdf;
    private boolean workflow;
    private boolean kim;
    private boolean audit;
    private boolean unused;

    public DataDictionaryFilteredField() {}

    public DataDictionaryFilteredField(String fieldName) {
        setFilteredField(fieldName);
    }

    public void setFilteredField(String fieldName) {
        final String[] args = fieldName.split("\\.");
        setBusinessObjectSimpleName(args[0]);
        setPropertyName(args[1]);
    }

    public String getBusinessObjectSimpleName() {
        return businessObjectSimpleName;
    }

    public void setBusinessObjectSimpleName(String businessObjectSimpleName) {
        this.businessObjectSimpleName = businessObjectSimpleName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public boolean matches(String tableName, String fieldName) {
        return  ((StringUtils.equals(tableName, businessObjectSimpleName) || matchesAnyClass())
                && StringUtils.equals(fieldName, propertyName));
    }

    public boolean matchesAnyClass() {
        return StringUtils.equals(businessObjectSimpleName, "*");
    }

    public boolean isPdf() {
        return pdf;
    }

    public void setPdf(boolean pdf) {
        this.pdf = pdf;
    }

    public boolean isWorkflow() {
        return workflow;
    }

    public void setWorkflow(boolean workflow) {
        this.workflow = workflow;
    }

    public boolean isKim() {
        return kim;
    }

    public void setKim(boolean kim) {
        this.kim = kim;
    }

    public boolean isAudit() {
        return audit;
    }

    public void setAudit(boolean audit) {
        this.audit = audit;
    }

    public boolean isUnused() {
        return unused;
    }

    public void setUnused(boolean unused) {
        this.unused = unused;
    }
}
