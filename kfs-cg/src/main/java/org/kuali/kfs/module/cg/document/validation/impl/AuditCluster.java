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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * KRA Audit Cluster; container for related set of audit errors.
 */
public class AuditCluster {

    private String label;
    private List auditErrorList;
    private boolean softAuditsIndicator;

    public AuditCluster() {
        this.auditErrorList = new ArrayList();
    }

    public AuditCluster(String label, List auditErrorList) {
        this.label = label;
        this.auditErrorList = auditErrorList;
    }

    public AuditCluster(String label, List auditErrorList, boolean softAuditsIndicator) {
        this(label, auditErrorList);
        this.softAuditsIndicator = softAuditsIndicator;
    }

    /**
     * Gets the label attribute.
     *
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label attribute value.
     *
     * @param label The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the auditErrorList attribute.
     *
     * @return Returns the auditErrorList.
     */
    public List getAuditErrorList() {
        return auditErrorList;
    }

    /**
     * Sets the auditErrorList attribute value.
     *
     * @param auditErrorList The auditErrorList to set.
     */
    public void setAuditErrorList(List auditErrorList) {
        this.auditErrorList = auditErrorList;
    }

    /**
     * Gets the softAuditsIndicator attribute.
     *
     * @return Returns the softAuditsIndicator.
     */
    public boolean isSoftAuditsIndicator() {
        return softAuditsIndicator;
    }

    /**
     * Sets the softAuditsIndicator attribute value.
     *
     * @param softAuditsIndicator The softAuditsIndicator to set.
     */
    public void setSoftAuditsIndicator(boolean softAuditsIndicator) {
        this.softAuditsIndicator = softAuditsIndicator;
    }

    /**
     * Returns the number of audit errors in the cluster.
     *
     * @return int size
     */
    public int getSize() {
        return this.getAuditErrorList().size();
    }
}
