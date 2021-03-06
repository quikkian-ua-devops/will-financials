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
package org.kuali.kfs.gl.report;

import org.kuali.kfs.sys.KFSConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a summary amount used in reporst
 */
public class Summary implements Comparable {
    /**
     * This number is used by TransactionReport when sorting the list of Summary objects passed to
     * TransactionReport.generateReport(). Lowest number prints first.
     */
    public static final int TOTAL_RECORD_COUNT_SUMMARY_SORT_ORDER = 1;
    public static final int SELECTED_RECORD_COUNT_SUMMARY_SORT_ORDER = 2;
    public static final int SEQUENCE_RECORDS_WRITTEN_SUMMARY_SORT_ORDER = 3;
    private int sortOrder;

    /**
     * This is the description that prints for the summary line.
     */
    private String description;

    /**
     * This is the count that displays. FIXME: Make this documentation a bit more clear.
     */
    private long count;


    public Summary() {
        super();
    }


    /**
     * Constructs a Summary.java.
     *
     * @param sortOrder
     * @param description
     * @param count
     */
    public Summary(int sortOrder, String description, long count) {
        this.sortOrder = sortOrder;
        this.description = description;
        this.count = count;
    }

    /**
     * Constructs a Summary.java.
     *
     * @param sortOrder
     * @param description
     * @param count
     */
    public Summary(int sortOrder, String description, Integer count) {
        this.sortOrder = sortOrder;
        this.description = description;
        if (count == null) {
            this.count = 0;
        } else {
            this.count = count.longValue();
        }
    }

    /**
     * Compare this Summary object with another summary object
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        if (arg0 instanceof Summary) {
            Summary otherObject = (Summary) arg0;
            Integer otherSort = new Integer(otherObject.getSortOrder());
            Integer thisSort = new Integer(sortOrder);
            return thisSort.compareTo(otherSort);
        } else {
            return 0;
        }
    }

    /**
     * Returns true if the description of this summary object and the passed in summary object are the same
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (!(object instanceof Summary))
            return false;

        Summary that = (Summary) object;
        return this.description.equals(that.getDescription());
    }

    /**
     * Build a report summary list for labor general ledger posting
     *
     * @param destination   description of summary displayed
     * @param startingOrder order how information is displayed
     * @return a list of summary objects
     */
    public static List<Summary> buildDefualtReportSummary(String destination, int startingOrder) {
        List<Summary> reportSummary = new ArrayList<Summary>();
        updateReportSummary(reportSummary, destination, KFSConstants.OperationType.INSERT, 0, startingOrder++);
        updateReportSummary(reportSummary, destination, KFSConstants.OperationType.UPDATE, 0, startingOrder++);
        updateReportSummary(reportSummary, destination, KFSConstants.OperationType.DELETE, 0, startingOrder++);
        return reportSummary;
    }

    /**
     * Update the report summary with the given information
     *
     * @param reportSummary   list of summaries
     * @param destinationName description of summary displayed
     * @param operationType   description of what action is related to the summary (i.e. insert, updated, deleted)
     * @param count           count of how many "objects" are affected
     * @param order           order how information is displayed
     */
    public static void updateReportSummary(List<Summary> reportSummary, String destinationName, String operationType, int count, int order) {
        StringBuilder summaryDescription = buildSummaryDescription(destinationName, operationType);
        updateReportSummary(reportSummary, summaryDescription.toString(), count, order);
    }

    /**
     * Update the report summary with the given information
     *
     * @param reportSummary      list of summaries
     * @param summaryDescription description of summary displayed
     * @param count              count of how many "objects" are affected
     * @param order              order how information is displayed
     */
    public static void updateReportSummary(List<Summary> reportSummary, String summaryDescription, int count, int order) {
        Summary inputSummary = new Summary(order, summaryDescription, count);

        int index = reportSummary.indexOf(inputSummary);
        if (index >= 0) {
            Summary summary = reportSummary.get(index);
            summary.setCount(summary.getCount() + count);
        } else {
            reportSummary.add(inputSummary);
        }
    }

    /**
     * Build the description of summary with the given information
     *
     * @param destinationName description of summary displayed
     * @param operationType   description of what action is related to the summary (i.e. insert, updated, deleted)
     * @return
     */
    public static StringBuilder buildSummaryDescription(String destinationName, String operationType) {
        StringBuilder summaryDescription = new StringBuilder();
        summaryDescription.append("Number of ").append(destinationName).append(" records ").append(operationType).append(":");
        return summaryDescription;
    }


    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
