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
package org.kuali.kfs.gl.batch.service.impl;

import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.CollectorScrubberProcess;
import org.kuali.kfs.gl.batch.service.CollectorScrubberService;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;
import org.kuali.kfs.krad.service.PersistenceService;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Collection;

/**
 * An implementation of CollectorScrubberService
 */
@Transactional
public class CollectorScrubberServiceImpl implements CollectorScrubberService {
    private DateTimeService dateTimeService;
    private UniversityDateDao universityDateDao;
    private ConfigurationService kualiConfigurationService;
    private PersistenceService persistenceService;
    private ScrubberService scrubberService;
    private String batchFileDirectoryName;

    /**
     * uns the scrubber on the origin entries in the batch. Any OEs edits/removals result of the scrub and demerger are removed
     * from the batch, and the same changes are reflected in the details in the same batch.
     *
     * @param batch               the data read in by the Collector
     * @param collectorReportData statistics generated by the scrub run on the Collector data
     * @return an object with the collector scrubber status.
     * @see org.kuali.kfs.gl.batch.service.CollectorScrubberService#scrub(org.kuali.kfs.gl.batch.CollectorBatch,
     * org.kuali.kfs.gl.report.CollectorReportData)
     */
    public CollectorScrubberStatus scrub(CollectorBatch batch, CollectorReportData collectorReportData, String collectorFileDirectoryName) {
        CollectorScrubberProcess collectorScrubberProcess = new CollectorScrubberProcess(batch, kualiConfigurationService, persistenceService, scrubberService, collectorReportData, dateTimeService, batchFileDirectoryName);
        return collectorScrubberProcess.scrub();

    }

    /**
     * Removes any temporarily created origin entries and origin entry groups so that they won't be persisted after the transaction
     * is committed.
     *
     * @param allStatusObjectsFromCollectorExecution a Collection of ScrubberStatus records to help find bad Collector data
     * @see org.kuali.kfs.gl.batch.service.CollectorScrubberService#removeTempGroups(java.util.Collection)
     */

    //TODO: need to delete files
    public void removeTempGroups(Collection<CollectorScrubberStatus> allStatusObjectsFromCollectorExecution) {
    }

    /**
     * Finds the run date of the current Collector scrubber process
     *
     * @return the date of the process
     */
    protected Date calculateRunDate() {
        return dateTimeService.getCurrentSqlDate();
    }

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the kualiConfigurationService attribute.
     *
     * @return Returns the kualiConfigurationService.
     */
    public ConfigurationService getConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     *
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Sets the universityDateDao attribute value.
     *
     * @param universityDateDao The universityDateDao to set.
     */
    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    /**
     * Gets the persistenceService attribute.
     *
     * @return Returns the persistenceService.
     */
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * Sets the persistenceService attribute value.
     *
     * @param persistenceService The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Gets the universityDateDao attribute.
     *
     * @return Returns the universityDateDao.
     */
    public UniversityDateDao getUniversityDateDao() {
        return universityDateDao;
    }

    /**
     * Sets the scrubberService attribute value.
     *
     * @param scrubberService The scrubberService to set.
     */
    public void setScrubberService(ScrubberService scrubberService) {
        this.scrubberService = scrubberService;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     *
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }


}
