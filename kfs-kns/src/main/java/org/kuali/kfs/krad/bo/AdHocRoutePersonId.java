/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.bo;

import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * This Compound Primary Class has been generated by the rice ojb2jpa Groovy script.  Please
 * note that there are no setter methods, only getters.  This is done purposefully as cpk classes
 * can not change after they have been created.  Also note they require a public no-arg constructor.
 * TODO: Implement the equals() and hashCode() methods. 
 */
public class AdHocRoutePersonId implements Serializable {

    private static final long serialVersionUID = -2030160650786561367L;
    
	@Column(name="RECIP_TYP_CD")
    private Integer type;
    @Id
    @Column(name="ACTN_RQST_CD")
    private String actionRequested;
    @Id
    @Column(name="ACTN_RQST_RECIP_ID")
    private String id;

    public AdHocRoutePersonId() {}
    
    public AdHocRoutePersonId(Integer type, String actionRequested, String id) {
    	this.type = type;
    	this.actionRequested = actionRequested;
    	this.id = id;
    }

    public Integer getType() {
        return this.type;
    }

    public String getActionRequested() {
        return this.actionRequested;
    }

    public String getId() {
        return this.id;
    }
        
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AdHocRoutePersonId)) return false;
        if (o == null) return false;
        AdHocRoutePersonId pk = (AdHocRoutePersonId) o;
        return getType() != null && getActionRequested() != null && getId() != null && getType().equals(pk.getType()) && getActionRequested().equals(pk.getActionRequested()) && getId().equals(pk.getId());        
    }

    public int hashCode() {
    	return new HashCodeBuilder().append(type).append(actionRequested).append(id).toHashCode();
    }

}
