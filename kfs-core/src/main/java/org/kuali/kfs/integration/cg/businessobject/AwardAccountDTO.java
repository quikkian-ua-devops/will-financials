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

import org.kuali.kfs.integration.cg.ContractsAndGrantsAwardAccount;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * <p>Java class for awardAccountDTO complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="awardAccountDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="awardId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="awardTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="federalSponsor" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="grantNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="institutionalproposalId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="primeSponsorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="primeSponsorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="projectDirector" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proposalFederalPassThroughAgencyNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="proposalNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sponsorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sponsorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "awardAccountDTO", propOrder = {
    "awardId",
    "awardTitle",
    "errorMessage",
    "federalSponsor",
    "grantNumber",
    "institutionalproposalId",
    "primeSponsorCode",
    "primeSponsorName",
    "projectDirector",
    "proposalFederalPassThroughAgencyNumber",
    "proposalNumber",
    "sponsorCode",
    "sponsorName"
})

public class AwardAccountDTO implements ContractsAndGrantsAwardAccount, Serializable {

    private long awardId;
    private String awardTitle;
    private String errorMessage;
    private boolean federalSponsor;
    private String grantNumber;
    private long institutionalproposalId;
    private String primeSponsorCode;
    private String primeSponsorName;
    private String projectDirector;
    private String proposalFederalPassThroughAgencyNumber;
    private String proposalNumber;
    private String sponsorCode;
    private String sponsorName;

    @Override
    public long getAwardId() {
        return awardId;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean getFederalSponsor() {
        return federalSponsor;
    }

    @Override
    public String getGrantNumber() {
        return grantNumber;
    }

    @Override
    public long getInstitutionalproposalId() {
        return institutionalproposalId;
    }

    @Override
    public String getProjectDirector() {
        return projectDirector;
    }

    @Override
    public String getProposalFederalPassThroughAgencyNumber() {
        return proposalFederalPassThroughAgencyNumber;
    }

    @Override
    public String getProposalNumber() {
        return proposalNumber;
    }

    @Override
    public String getSponsorCode() {
        return sponsorCode;
    }

    @Override
    public String getSponsorName() {
        return sponsorName;
    }

    @Override
    public void refresh() {
    }


    @Override
    public String getAwardTitle() {
        return awardTitle;
    }


    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }


    @Override
    public String getPrimeSponsorCode() {
        return primeSponsorCode;
    }


    public void setPrimeSponsorCode(String primeSponsorCode) {
        this.primeSponsorCode = primeSponsorCode;
    }


    @Override
    public String getPrimeSponsorName() {
        return primeSponsorName;
    }


    public void setPrimeSponsorName(String primeSponsorName) {
        this.primeSponsorName = primeSponsorName;
    }

}
