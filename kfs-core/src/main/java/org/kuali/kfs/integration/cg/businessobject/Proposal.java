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

package org.kuali.kfs.integration.cg.businessobject;

import org.kuali.kfs.integration.cg.ContractAndGrantsProposal;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * See functional documentation.
 */
public class Proposal implements ContractAndGrantsProposal {

    private String proposalNumber;
    private Date proposalBeginningDate;
    private Date proposalEndingDate;

    private KualiDecimal proposalDirectCostAmount;
    private KualiDecimal proposalIndirectCostAmount;
    private Date proposalRejectedDate;
    private Timestamp proposalLastUpdateDate;
    private Date proposalDueDate;
    private KualiDecimal proposalTotalProjectAmount;
    private Date proposalSubmissionDate;
    private boolean proposalFederalPassThroughIndicator;
    private String oldProposalNumber;
    private String grantNumber;
    private Date proposalClosingDate;
    private String proposalAwardTypeCode;
    private String agencyNumber;
    private String proposalStatusCode;
    private String federalPassThroughAgencyNumber;
    private String cfdaNumber;
    private String proposalFellowName;
    private String proposalPurposeCode;
    private String proposalProjectTitle;
    private boolean active;

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber
     */
    @Override
    public String getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the proposalBeginningDate attribute.
     *
     * @return Returns the proposalBeginningDate
     */
    @Override
    public Date getProposalBeginningDate() {
        return proposalBeginningDate;
    }

    /**
     * Sets the proposalBeginningDate attribute.
     *
     * @param proposalBeginningDate The proposalBeginningDate to set.
     */
    public void setProposalBeginningDate(Date proposalBeginningDate) {
        this.proposalBeginningDate = proposalBeginningDate;
    }

    /**
     * Gets the proposalEndingDate attribute.
     *
     * @return Returns the proposalEndingDate
     */
    @Override
    public Date getProposalEndingDate() {
        return proposalEndingDate;
    }

    /**
     * Sets the proposalEndingDate attribute.
     *
     * @param proposalEndingDate The proposalEndingDate to set.
     */
    public void setProposalEndingDate(Date proposalEndingDate) {
        this.proposalEndingDate = proposalEndingDate;
    }

    /**
     * Gets the proposalTotalAmount attribute.
     *
     * @return Returns the proposalTotalAmount
     */
    @Override
    public KualiDecimal getProposalTotalAmount() {
        KualiDecimal direct = getProposalDirectCostAmount();
        KualiDecimal indirect = getProposalIndirectCostAmount();
        return ObjectUtils.isNull(direct) || ObjectUtils.isNull(indirect) ? null : direct.add(indirect);
    }

    /**
     * Gets the proposalDirectCostAmount attribute.
     *
     * @return Returns the proposalDirectCostAmount
     */
    @Override
    public KualiDecimal getProposalDirectCostAmount() {
        return proposalDirectCostAmount;
    }

    /**
     * Sets the proposalDirectCostAmount attribute.
     *
     * @param proposalDirectCostAmount The proposalDirectCostAmount to set.
     */
    public void setProposalDirectCostAmount(KualiDecimal proposalDirectCostAmount) {
        this.proposalDirectCostAmount = proposalDirectCostAmount;
    }

    /**
     * Gets the proposalIndirectCostAmount attribute.
     *
     * @return Returns the proposalIndirectCostAmount
     */
    @Override
    public KualiDecimal getProposalIndirectCostAmount() {
        return proposalIndirectCostAmount;
    }

    /**
     * Sets the proposalIndirectCostAmount attribute.
     *
     * @param proposalIndirectCostAmount The proposalIndirectCostAmount to set.
     */
    public void setProposalIndirectCostAmount(KualiDecimal proposalIndirectCostAmount) {
        this.proposalIndirectCostAmount = proposalIndirectCostAmount;
    }

    /**
     * Gets the proposalRejectedDate attribute.
     *
     * @return Returns the proposalRejectedDate
     */
    @Override
    public Date getProposalRejectedDate() {
        return proposalRejectedDate;
    }

    /**
     * Sets the proposalRejectedDate attribute.
     *
     * @param proposalRejectedDate The proposalRejectedDate to set.
     */
    public void setProposalRejectedDate(Date proposalRejectedDate) {
        this.proposalRejectedDate = proposalRejectedDate;
    }

    /**
     * Gets the proposalLastUpdateDate attribute.
     *
     * @return Returns the proposalLastUpdateDate
     */
    @Override
    public Timestamp getProposalLastUpdateDate() {
        return proposalLastUpdateDate;
    }

    /**
     * Sets the proposalLastUpdateDate attribute.
     *
     * @param proposalLastUpdateDate The proposalLastUpdateDate to set.
     */
    public void setProposalLastUpdateDate(Timestamp proposalLastUpdateDate) {
        this.proposalLastUpdateDate = proposalLastUpdateDate;
    }

    /**
     * Gets the proposalDueDate attribute.
     *
     * @return Returns the proposalDueDate
     */
    @Override
    public Date getProposalDueDate() {
        return proposalDueDate;
    }

    /**
     * Sets the proposalDueDate attribute.
     *
     * @param proposalDueDate The proposalDueDate to set.
     */
    public void setProposalDueDate(Date proposalDueDate) {
        this.proposalDueDate = proposalDueDate;
    }

    /**
     * Gets the proposalTotalProjectAmount attribute.
     *
     * @return Returns the proposalTotalProjectAmount
     */
    @Override
    public KualiDecimal getProposalTotalProjectAmount() {
        return proposalTotalProjectAmount;
    }

    /**
     * Sets the proposalTotalProjectAmount attribute.
     *
     * @param proposalTotalProjectAmount The proposalTotalProjectAmount to set.
     */
    public void setProposalTotalProjectAmount(KualiDecimal proposalTotalProjectAmount) {
        this.proposalTotalProjectAmount = proposalTotalProjectAmount;
    }

    /**
     * Gets the proposalSubmissionDate attribute.
     *
     * @return Returns the proposalSubmissionDate
     */
    @Override
    public Date getProposalSubmissionDate() {
        return proposalSubmissionDate;
    }

    /**
     * Sets the proposalSubmissionDate attribute.
     *
     * @param proposalSubmissionDate The proposalSubmissionDate to set.
     */
    public void setProposalSubmissionDate(Date proposalSubmissionDate) {
        this.proposalSubmissionDate = proposalSubmissionDate;
    }

    /**
     * Gets the proposalFederalPassThroughIndicator attribute.
     *
     * @return Returns the proposalFederalPassThroughIndicator
     */
    @Override
    public boolean getProposalFederalPassThroughIndicator() {
        return proposalFederalPassThroughIndicator;
    }

    /**
     * Sets the proposalFederalPassThroughIndicator attribute.
     *
     * @param proposalFederalPassThroughIndicator The proposalFederalPassThroughIndicator to set.
     */
    public void setProposalFederalPassThroughIndicator(boolean proposalFederalPassThroughIndicator) {
        this.proposalFederalPassThroughIndicator = proposalFederalPassThroughIndicator;
    }

    /**
     * Gets the oldProposalNumber attribute.
     *
     * @return Returns the oldProposalNumber
     */
    @Override
    public String getOldProposalNumber() {
        return oldProposalNumber;
    }

    /**
     * Sets the oldProposalNumber attribute.
     *
     * @param oldProposalNumber The oldProposalNumber to set.
     */
    public void setOldProposalNumber(String oldProposalNumber) {
        this.oldProposalNumber = oldProposalNumber;
    }

    /**
     * Gets the grantNumber attribute.
     *
     * @return Returns the grantNumber
     */
    @Override
    public String getGrantNumber() {
        return grantNumber;
    }

    /**
     * Sets the grantNumber attribute.
     *
     * @param grantNumber The grantNumber to set.
     */
    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }

    /**
     * Gets the proposalClosingDate attribute.
     *
     * @return Returns the proposalClosingDate
     */
    @Override
    public Date getProposalClosingDate() {
        return proposalClosingDate;
    }

    /**
     * Sets the proposalClosingDate attribute.
     *
     * @param proposalClosingDate The proposalClosingDate to set.
     */
    public void setProposalClosingDate(Date proposalClosingDate) {
        this.proposalClosingDate = proposalClosingDate;
    }

    /**
     * Gets the proposalAwardTypeCode attribute.
     *
     * @return Returns the proposalAwardTypeCode
     */
    @Override
    public String getProposalAwardTypeCode() {
        return proposalAwardTypeCode;
    }

    /**
     * Sets the proposalAwardTypeCode attribute.
     *
     * @param proposalAwardTypeCode The proposalAwardTypeCode to set.
     */
    public void setProposalAwardTypeCode(String proposalAwardTypeCode) {
        this.proposalAwardTypeCode = proposalAwardTypeCode;
    }

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber
     */
    @Override
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     *
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the proposalStatusCode attribute.
     *
     * @return Returns the proposalStatusCode
     */
    @Override
    public String getProposalStatusCode() {
        return proposalStatusCode;
    }

    /**
     * Sets the proposalStatusCode attribute.
     *
     * @param proposalStatusCode The proposalStatusCode to set.
     */
    public void setProposalStatusCode(String proposalStatusCode) {
        this.proposalStatusCode = proposalStatusCode;
    }

    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     *
     * @return Returns the federalPassThroughAgencyNumber
     */
    @Override
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }

    /**
     * Sets the federalPassThroughAgencyNumber attribute.
     *
     * @param federalPassThroughAgencyNumber The federalPassThroughAgencyNumber to set.
     */
    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }

    /**
     * Gets the cfdaNumber attribute.
     *
     * @return Returns the cfdaNumber
     */
    @Override
    public String getCfdaNumber() {
        return cfdaNumber;
    }

    /**
     * Sets the cfdaNumber attribute.
     *
     * @param cfdaNumber The cfdaNumber to set.
     */
    public void setCfdaNumber(String cfdaNumber) {
        this.cfdaNumber = cfdaNumber;
    }

    /**
     * Gets the proposalFellowName attribute.
     *
     * @return Returns the proposalFellowName
     */
    @Override
    public String getProposalFellowName() {
        return proposalFellowName;
    }

    /**
     * Sets the proposalFellowName attribute.
     *
     * @param proposalFellowName The proposalFellowName to set.
     */
    public void setProposalFellowName(String proposalFellowName) {
        this.proposalFellowName = proposalFellowName;
    }

    /**
     * Gets the proposalPurposeCode attribute.
     *
     * @return Returns the proposalPurposeCode
     */
    @Override
    public String getProposalPurposeCode() {
        return proposalPurposeCode;
    }

    /**
     * Sets the proposalPurposeCode attribute.
     *
     * @param proposalPurposeCode The proposalPurposeCode to set.
     */
    public void setProposalPurposeCode(String proposalPurposeCode) {
        this.proposalPurposeCode = proposalPurposeCode;
    }

    /**
     * Gets the proposalProjectTitle attribute.
     *
     * @return Returns the proposalProjectTitle
     */
    @Override
    public String getProposalProjectTitle() {
        return proposalProjectTitle;
    }

    /**
     * Sets the proposalProjectTitle attribute.
     *
     * @param proposalProjectTitle The proposalProjectTitle to set.
     */
    public void setProposalProjectTitle(String proposalProjectTitle) {
        this.proposalProjectTitle = proposalProjectTitle;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void refresh() {
    }

    @Override
    public ContractsAndGrantsAward getAward() {
        return null;
    }
}

