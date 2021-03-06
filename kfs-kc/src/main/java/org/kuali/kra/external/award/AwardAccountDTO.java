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
package org.kuali.kra.external.award;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is the DTO to be sent over to the financial system
 * with information required for effort reporting and
 * award account.
 */
public class AwardAccountDTO implements Serializable {


    private static final long serialVersionUID = 1L;
    private String proposalFederalPassThroughAgencyNumber;
    private String errorMessage;
    private String grantNumber;
    private String projectDirector;
    private String awardNumber;
    private String sponsorName;
    private boolean isFederalSponsor;
    private Long awardId;
    private String sponsorCode;
    private Long institutionalproposalId;
    private String awardTitle;
    private String primeSponsorCode;
    private String primeSponsorName;
    private String primeSponsorTypeCode;
    private String sponsorTypeCode;

    private String chartOfAcccountsCode;
    private String accountNumber;
    private boolean finalBill;
    private Date lastBilledDate;
    private Date previousLastBilledDate;

    public String getPrimeSponsorTypeCode() {
        return primeSponsorTypeCode;
    }

    public void setPrimeSponsorTypeCode(String primeSponsorTypeCode) {
        this.primeSponsorTypeCode = primeSponsorTypeCode;
    }

    public String getSponsorTypeCode() {
        return sponsorTypeCode;
    }

    public void setSponsorTypeCode(String sponsorTypeCode) {
        this.sponsorTypeCode = sponsorTypeCode;
    }

    public String getAwardTitle() {
        return awardTitle;
    }

    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }

    public String getPrimeSponsorCode() {
        return primeSponsorCode;
    }

    public void setPrimeSponsorCode(String primeSponsorCode) {
        this.primeSponsorCode = primeSponsorCode;
    }

    public String getPrimeSponsorName() {
        return primeSponsorName;
    }

    public void setPrimeSponsorName(String primeSponsorName) {
        this.primeSponsorName = primeSponsorName;
    }

    public String getProposalFederalPassThroughAgencyNumber() {
        return proposalFederalPassThroughAgencyNumber;
    }

    public void setProposalFederalPassThroughAgencyNumber(String proposalFederalPassThroughAgencyNumber) {
        this.proposalFederalPassThroughAgencyNumber = proposalFederalPassThroughAgencyNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }

    public String getProjectDirector() {
        return projectDirector;
    }

    public void setProjectDirector(String projectDirector) {
        this.projectDirector = projectDirector;
    }

    public String getAwardNumber() {
        return awardNumber;
    }

    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public boolean isFederalSponsor() {
        return isFederalSponsor;
    }

    public void setFederalSponsor(boolean isFederalSponsor) {
        this.isFederalSponsor = isFederalSponsor;
    }

    public Long getAwardId() {
        return awardId;
    }

    public void setAwardId(Long long1) {
        this.awardId = long1;
    }

    public String getSponsorCode() {
        return sponsorCode;
    }

    public void setSponsorCode(String sponsorCode) {
        this.sponsorCode = sponsorCode;
    }

    public Long getInstitutionalproposalId() {
        return institutionalproposalId;
    }

    public void setInstitutionalproposalId(Long long1) {
        this.institutionalproposalId = long1;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getChartOfAcccountsCode() {
        return chartOfAcccountsCode;
    }

    public void setChartOfAcccountsCode(String chartOfAcccountsCode) {
        this.chartOfAcccountsCode = chartOfAcccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isFinalBill() {
        return finalBill;
    }

    public void setFinalBill(boolean finalBill) {
        this.finalBill = finalBill;
    }

    public Date getLastBilledDate() {
        return lastBilledDate;
    }

    public void setLastBilledDate(Date lastBilledDate) {
        this.lastBilledDate = lastBilledDate;
    }

    public Date getPreviousLastBilledDate() {
        return previousLastBilledDate;
    }

    public void setPreviousLastBilledDate(Date previousLastBilledDate) {
        this.previousLastBilledDate = previousLastBilledDate;
    }
}
