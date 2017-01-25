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
package org.kuali.kfs.module.ar.report;

import org.kuali.kfs.module.ar.businessobject.CollectionActivityReport;

import java.util.Date;

/**
 * Data holder class for Collection Activity Report.
 */
public class CollectionActivityReportDetailDataHolder {

    private Date followupDate;
    private String activityType;
    private String activityComment;
    private String proposalNumber;
    private String agencyNumber;

    private String accountNumber;
    private String invoiceNumber;
    private Date activityDate;
    private String agencyName;
    private Date completedDate;
    private String userPrincipalId;
    private String chartOfAccountsCode;

    /**
     * Default constructor.
     */
    public CollectionActivityReportDetailDataHolder() {
    }

    /**
     * Constructor to initialize other values from given object.
     *
     * @param cr ColletionActivityReport object from which values to be set in data holder object.
     */
    public CollectionActivityReportDetailDataHolder(CollectionActivityReport cr) {
        this.accountNumber = cr.getAccountNumber();
        this.invoiceNumber = cr.getInvoiceNumber();
        this.activityDate = cr.getActivityDate();
        this.followupDate = cr.getFollowupDate();
        this.activityType = cr.getActivityType();
        this.activityComment = cr.getActivityComment();
        this.completedDate = cr.getCompletedDate();
        this.userPrincipalId = cr.getUserPrincipalId();
        this.chartOfAccountsCode = cr.getChartOfAccountsCode();
        this.agencyNumber = cr.getAgencyNumber();
        this.proposalNumber = cr.getProposalNumber();
    }

    /**
     * Gets the followupDate attribute.
     *
     * @return Returns the followup date.
     */
    public Date getFollowupDate() {
        return followupDate;
    }

    /**
     * Sets the followupDate attribute.
     *
     * @param followupDate The followup date to set.
     */
    public void setFollowupDate(Date followupDate) {
        this.followupDate = followupDate;
    }

    /**
     * Gets the activityType attribute.
     *
     * @return Returns the activityType.
     */
    public String getActivityType() {
        return activityType;
    }

    /**
     * Sets the activityType attribute.
     *
     * @param activityType The activityType to set.
     */
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    /**
     * Gets the activityComment attribute.
     *
     * @return Returns the activityComment.
     */
    public String getActivityComment() {
        return activityComment;
    }

    /**
     * Sets the activityComment attribute.
     *
     * @param activityComment The activityComment to set.
     */
    public void setActivityComment(String activityComment) {
        this.activityComment = activityComment;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the activityDate attribute.
     *
     * @return Returns the activityDate.
     */
    public Date getActivityDate() {
        return activityDate;
    }

    /**
     * Sets the activityDate attribute.
     *
     * @param activityDate The activityDate to set.
     */
    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    /**
     * Gets the completedDate attribute.
     *
     * @return Returns the completedDate.
     */
    public Date getCompletedDate() {
        return completedDate;
    }

    /**
     * Sets the completedDate attribute.
     *
     * @param completedDate The completedDate to set.
     */
    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * Gets the userPrincipalId attribute.
     *
     * @return Returns the userPrincipalId.
     */
    public String getUserPrincipalId() {
        return userPrincipalId;
    }

    /**
     * Sets the userPrincipalId attribute.
     *
     * @param userPrincipalId The userPrincipalId to set.
     */
    public void setUserPrincipalId(String userPrincipalId) {
        this.userPrincipalId = userPrincipalId;
    }

    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposal number.
     */
    public String getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     *
     * @param proposalNumber The proposal number to set.
     */
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agency number.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     *
     * @param agencyNumber The agency number to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the invoiceNumber attribute.
     *
     * @return Returns the invoice number.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute.
     *
     * @param invoiceNumber The invoice number to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the agencyName attribute.
     *
     * @return Returns tha agency name.
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Sets the agencyName attribute.
     *
     * @param agencyName The agency name to set.
     */
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

}
