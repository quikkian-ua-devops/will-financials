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
package org.kuali.kfs.module.cam.batch;

import org.kuali.kfs.gl.businessobject.Entry;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtractProcessLog {
    private Timestamp startTime;
    private Timestamp finishTime;
    private Timestamp lastExtractTime;
    private List<Entry> ignoredGLEntries;
    private List<Entry> duplicateGLEntries;
    private List<Entry> mismatchedGLEntries;
    private String errorMessage;
    private boolean success = true;
    private Integer totalGlCount = 0;
    private Integer nonPurApGlCount = 0;
    private Integer purApGlCount = 0;
    private String statusMessage;

    /**
     * Gets the ignoredGLEntries attribute.
     *
     * @return Returns the ignoredGLEntries.
     */
    public List<Entry> getIgnoredGLEntries() {
        return ignoredGLEntries;
    }

    /**
     * Sets the ignoredGLEntries attribute value.
     *
     * @param ignoredGLEntries The ignoredGLEntries to set.
     */
    public void setIgnoredGLEntries(List<Entry> ignoredGLEntries) {
        this.ignoredGLEntries = ignoredGLEntries;
    }

    /**
     * Gets the duplicateGLEntries attribute.
     *
     * @return Returns the duplicateGLEntries.
     */
    public List<Entry> getDuplicateGLEntries() {
        return duplicateGLEntries;
    }

    /**
     * Sets the duplicateGLEntries attribute value.
     *
     * @param duplicateGLEntries The duplicateGLEntries to set.
     */
    public void setDuplicateGLEntries(List<Entry> duplicateGLEntries) {
        this.duplicateGLEntries = duplicateGLEntries;
    }

    /**
     * Gets the mismatchedGLEntries attribute.
     *
     * @return Returns the mismatchedGLEntries.
     */
    public List<Entry> getMismatchedGLEntries() {
        return mismatchedGLEntries;
    }

    /**
     * Sets the mismatchedGLEntries attribute value.
     *
     * @param mismatchedGLEntries The mismatchedGLEntries to set.
     */
    public void setMismatchedGLEntries(List<Entry> mismatchedGLEntries) {
        this.mismatchedGLEntries = mismatchedGLEntries;
    }

    /**
     * Adds a collection of entries to ignoredGLEntries
     *
     * @param add ignoredGLEntries
     */
    public void addIgnoredGLEntries(Collection<Entry> add) {
        if (this.ignoredGLEntries == null) {
            this.ignoredGLEntries = new ArrayList<Entry>();
        }
        this.ignoredGLEntries.addAll(add);
    }

    /**
     * Adds a collection of entries to duplicateGLEntries
     *
     * @param add duplicateGLEntries
     */
    public void addDuplicateGLEntries(Collection<Entry> add) {
        if (this.duplicateGLEntries == null) {
            this.duplicateGLEntries = new ArrayList<Entry>();
        }
        this.duplicateGLEntries.addAll(add);
    }

    /**
     * Adds a collection of entries to mismatchedGLEntries
     *
     * @param add mismatchedGLEntries
     */
    public void addMismatchedGLEntries(Collection<Entry> add) {
        if (this.mismatchedGLEntries == null) {
            this.mismatchedGLEntries = new ArrayList<Entry>();
        }
        this.mismatchedGLEntries.addAll(add);
    }

    /**
     * Add a GL entry to ignoredGLEntries
     *
     * @param add Entry
     */
    public void addIgnoredGLEntry(Entry add) {
        if (this.ignoredGLEntries == null) {
            this.ignoredGLEntries = new ArrayList<Entry>();
        }
        this.ignoredGLEntries.add(add);
    }

    /**
     * Add a GL entry to duplicateGLEntries
     *
     * @param add Entry
     */
    public void addDuplicateGLEntry(Entry add) {
        if (this.duplicateGLEntries == null) {
            this.duplicateGLEntries = new ArrayList<Entry>();
        }
        this.duplicateGLEntries.add(add);
    }

    /**
     * Add a GL entry to mismatchedGLEntries
     *
     * @param add Entry
     */
    public void addMismatchedGLEntry(Entry add) {
        if (this.mismatchedGLEntries == null) {
            this.mismatchedGLEntries = new ArrayList<Entry>();
        }
        this.mismatchedGLEntries.add(add);
    }

    /**
     * Gets the startTime attribute.
     *
     * @return Returns the startTime.
     */
    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * Sets the startTime attribute value.
     *
     * @param startTime The startTime to set.
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the lastExtractTime attribute.
     *
     * @return Returns the lastExtractTime.
     */
    public Timestamp getLastExtractTime() {
        return lastExtractTime;
    }

    /**
     * Sets the lastExtractTime attribute value.
     *
     * @param lastExtractTime The lastExtractTime to set.
     */
    public void setLastExtractTime(Timestamp lastExtractTime) {
        this.lastExtractTime = lastExtractTime;
    }

    /**
     * Gets the success attribute.
     *
     * @return Returns the success.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success attribute value.
     *
     * @param success The success to set.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets the finishTime attribute.
     *
     * @return Returns the finishTime.
     */
    public Timestamp getFinishTime() {
        return finishTime;
    }

    /**
     * Sets the finishTime attribute value.
     *
     * @param finishTime The finishTime to set.
     */
    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * Gets the errorMessage attribute.
     *
     * @return Returns the errorMessage.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the errorMessage attribute value.
     *
     * @param errorMessage The errorMessage to set.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the totalGlCount attribute.
     *
     * @return Returns the totalGlCount.
     */
    public Integer getTotalGlCount() {
        return totalGlCount;
    }

    /**
     * Sets the totalGlCount attribute value.
     *
     * @param totalGlCount The totalGlCount to set.
     */
    public void setTotalGlCount(Integer totalGlCount) {
        this.totalGlCount = totalGlCount;
    }

    /**
     * Gets the nonPurApGlCount attribute.
     *
     * @return Returns the nonPurApGlCount.
     */
    public Integer getNonPurApGlCount() {
        return nonPurApGlCount;
    }

    /**
     * Sets the nonPurApGlCount attribute value.
     *
     * @param nonPurApGlCount The nonPurApGlCount to set.
     */
    public void setNonPurApGlCount(Integer nonPurApGlCount) {
        this.nonPurApGlCount = nonPurApGlCount;
    }

    /**
     * Gets the purApGlCount attribute.
     *
     * @return Returns the purApGlCount.
     */
    public Integer getPurApGlCount() {
        return purApGlCount;
    }

    /**
     * Sets the purApGlCount attribute value.
     *
     * @param purApGlCount The purApGlCount to set.
     */
    public void setPurApGlCount(Integer purApGlCount) {
        this.purApGlCount = purApGlCount;
    }

    /**
     * Gets the statusMessage attribute.
     *
     * @return Returns the statusMessage.
     */
    public String getStatusMessage() {
        if (this.statusMessage == null) {
            return success ? "Success" : this.errorMessage == null ? "" : this.errorMessage;
        }
        return statusMessage;
    }

    /**
     * Sets the statusMessage attribute value.
     *
     * @param statusMessage The statusMessage to set.
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }


}
