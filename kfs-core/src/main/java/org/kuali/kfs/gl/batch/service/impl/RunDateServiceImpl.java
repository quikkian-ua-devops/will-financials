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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.batch.service.RunDateService;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * The default implementation of RunDateService
 */
public class RunDateServiceImpl implements RunDateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RunDateServiceImpl.class);

    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.gl.batch.service.RunDateService#calculateRunDate(java.util.Date)
     */
    public Date calculateRunDate(Date executionDate) {
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(executionDate);

        CutoffTime cutoffTime = parseCutoffTime(retrieveCutoffTimeValue());

        if (isCurrentDateBeforeCutoff(currentCal, cutoffTime)) {
            // time to set the date to the previous day's last minute/second
            currentCal.add(Calendar.DAY_OF_MONTH, -1);
            // per old COBOL code (see KULRNE-70),
            // the time is set to 23:59:59 (assuming 0 ms)
            currentCal.set(Calendar.HOUR_OF_DAY, 23);
            currentCal.set(Calendar.MINUTE, 59);
            currentCal.set(Calendar.SECOND, 59);
            currentCal.set(Calendar.MILLISECOND, 0);
            return new Date(currentCal.getTimeInMillis());
        }
        return new Date(executionDate.getTime());
    }

    /**
     * Determines if the given calendar time is before the given cutoff time
     *
     * @param currentCal the current time
     * @param cutoffTime the "start of the day" cut off time
     * @return true if the current time is before the cutoff, false otherwise
     */
    protected boolean isCurrentDateBeforeCutoff(Calendar currentCal, CutoffTime cutoffTime) {
        if (cutoffTime != null) {
            // if cutoff date is not properly defined
            // 24 hour clock (i.e. hour is 0 - 23)

            // clone the calendar so we get the same month, day, year
            // then change the hour, minute, second fields
            // then see if the cutoff is before or after
            Calendar cutoffCal = (Calendar) currentCal.clone();
            cutoffCal.setLenient(false);
            cutoffCal.set(Calendar.HOUR_OF_DAY, cutoffTime.hour);
            cutoffCal.set(Calendar.MINUTE, cutoffTime.minute);
            cutoffCal.set(Calendar.SECOND, cutoffTime.second);
            cutoffCal.set(Calendar.MILLISECOND, 0);

            return currentCal.before(cutoffCal);
        }
        // if cutoff date is not properly defined, then it is considered to be after the cutoff
        return false;
    }

    /**
     * Holds the hour, minute, and second of a given cut off time
     */
    protected class CutoffTime {
        /**
         * 24 hour time, from 0-23, inclusive
         */
        protected int hour;

        /**
         * From 0-59, inclusive
         */
        protected int minute;

        /**
         * From 0-59, inclusive
         */
        protected int second;

        /**
         * Constructs a RunDateServiceImpl instance
         *
         * @param hour   the cutoff hour
         * @param minute the cutoff minute
         * @param second the cutoff second
         */
        protected CutoffTime(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }
    }

    /**
     * Parses a String representation of the cutoff time
     *
     * @param cutoffTime the cutoff time String to parse
     * @return a record holding the cutoff time
     */
    protected CutoffTime parseCutoffTime(String cutoffTime) {
        if (StringUtils.isBlank(cutoffTime)) {
            return new CutoffTime(0, 0, 0);
        } else {
            cutoffTime = cutoffTime.trim();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cutoff time value found: " + cutoffTime);
            }
            StringTokenizer st = new StringTokenizer(cutoffTime, ":", false);

            try {
                String hourStr = st.nextToken();
                String minuteStr = st.nextToken();
                String secondStr = st.nextToken();

                int hourInt = Integer.parseInt(hourStr, 10);
                int minuteInt = Integer.parseInt(minuteStr, 10);
                int secondInt = Integer.parseInt(secondStr, 10);

                if (hourInt < 0 || hourInt > 23 || minuteInt < 0 || minuteInt > 59 || secondInt < 0 || secondInt > 59) {
                    throw new IllegalArgumentException("Cutoff time must be in the format \"HH:mm:ss\", where HH, mm, ss are defined in the java.text.SimpleDateFormat class.  In particular, 0 <= hour <= 23, 0 <= minute <= 59, and 0 <= second <= 59");
                }
                return new CutoffTime(hourInt, minuteInt, secondInt);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cutoff time should either be null, or in the format \"HH:mm:ss\", where HH, mm, ss are defined in the java.text.SimpleDateFormat class.");
            }
        }
    }

    /**
     * Retrieves the cutoff time from a repository.
     *
     * @return a time of day in the format "HH:mm:ss", where HH, mm, ss are defined in the java.text.SimpleDateFormat class. In
     * particular, 0 <= hour <= 23, 0 <= minute <= 59, and 0 <= second <= 59
     */
    protected String retrieveCutoffTimeValue() {
        String value = parameterService.getParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.SCRUBBER_CUTOFF_TIME);
        if (StringUtils.isBlank(value)) {
            LOG.error("Unable to retrieve parameter for GL process cutoff date.  Defaulting to no cutoff time (i.e. midnight)");
            value = null;
        }
        return value;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
