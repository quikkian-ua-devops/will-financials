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

package org.kuali.kfs.module.cg.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsProjectDirector;
import org.kuali.kfs.krad.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;

import java.util.LinkedHashMap;

/**
 * This class represents an association between an award and a project director. It's like a reference to the project director from
 * the award. This way an award can maintain a collection of these references instead of owning project directors directly.
 */
public class AwardProjectDirector extends PersistableBusinessObjectBase implements Primaryable, CGProjectDirector, MutableInactivatable, ContractsAndGrantsProjectDirector {

    private String principalId;
    private String proposalNumber;
    private boolean awardPrimaryProjectDirectorIndicator;
    private String awardProjectDirectorProjectTitle;
    private boolean active = true;

    private Person projectDirector;

    private final String userLookupRoleNamespaceCode = KFSConstants.ParameterNamespaces.KFS;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.CONTRACTS_AND_GRANTS_PROJECT_DIRECTOR;

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getPrincipalId()
     */
    @Override
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setPrincipalId(java.lang.String)
     */
    @Override
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getProposalNumber()
     */
    @Override
    public String getProposalNumber() {
        return proposalNumber;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setProposalNumber(java.lang.String)
     */
    @Override
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the awardPrimaryProjectDirectorIndicator attribute.
     *
     * @return Returns the awardPrimaryProjectDirectorIndicator
     */
    public boolean isAwardPrimaryProjectDirectorIndicator() {
        return awardPrimaryProjectDirectorIndicator;
    }


    /**
     * Sets the awardPrimaryProjectDirectorIndicator attribute.
     *
     * @param awardPrimaryProjectDirectorIndicator The awardPrimaryProjectDirectorIndicator to set.
     */
    public void setAwardPrimaryProjectDirectorIndicator(boolean awardPrimaryProjectDirectorIndicator) {
        this.awardPrimaryProjectDirectorIndicator = awardPrimaryProjectDirectorIndicator;
    }


    /**
     * Gets the awardProjectDirectorProjectTitle attribute.
     *
     * @return Returns the awardProjectDirectorProjectTitle
     */
    public String getAwardProjectDirectorProjectTitle() {
        return awardProjectDirectorProjectTitle;
    }

    /**
     * Sets the awardProjectDirectorProjectTitle attribute.
     *
     * @param awardProjectDirectorProjectTitle The awardProjectDirectorProjectTitle to set.
     */
    public void setAwardProjectDirectorProjectTitle(String awardProjectDirectorProjectTitle) {
        this.awardProjectDirectorProjectTitle = awardProjectDirectorProjectTitle;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#getProjectDirector()
     */
    @Override
    public Person getProjectDirector() {
        projectDirector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, projectDirector);
        return projectDirector;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGProjectDirector#setProjectDirector(org.kuali.kfs.module.cg.businessobject.ProjectDirector)
     */
    @Override
    public void setProjectDirector(Person projectDirector) {
        this.projectDirector = projectDirector;
    }

    /**
     * @see Primaryable#isPrimary()
     */
    @Override
    public boolean isPrimary() {
        return isAwardPrimaryProjectDirectorIndicator();
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * @param userLookupRoleNamespaceCode
     */
    public void setUserLookupRoleNamespaceCode(String userLookupRoleNamespaceCode) {
    }

    /**
     * @return
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * s
     *
     * @param userLookupRoleName
     */
    public void setUserLookupRoleName(String userLookupRoleName) {
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, this.principalId);
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber);
        }
        return m;
    }

}
