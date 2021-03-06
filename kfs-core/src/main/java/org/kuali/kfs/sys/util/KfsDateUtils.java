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
package org.kuali.kfs.sys.util;

import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSConstants;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility methods for comparing dates
 */
public class KfsDateUtils {
    /**
     * Adds null-safety to commons.DateUtils isSameDay method.
     *
     * @return true if both dates are null or represent the same day
     */
    public static boolean isSameDay(Date date1, Date date2) {
        boolean same = false;

        if ((date1 == null) && (date2 == null)) {
            same = true;
        } else if ((date1 != null) && (date2 != null)) {
            return org.apache.commons.lang.time.DateUtils.isSameDay(date1, date2);
        } else {
            same = false;
        }

        return same;
    }

    /**
     * Adds null-safety to commons.DateUtils isSameDay method.
     *
     * @return true if both calendars are null or represent the same day
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        boolean same = false;

        if ((cal1 == null) && (cal2 == null)) {
            same = true;
        } else if ((cal1 != null) && (cal2 != null)) {
            return org.apache.commons.lang.time.DateUtils.isSameDay(cal1, cal2);
        } else {
            same = false;
        }

        return same;
    }

    /**
     * Converts the given java.util.Date into an equivalent java.sql.Date
     *
     * @param date
     * @return java.sql.Date constructed from the given java.util.Date
     */
    public static java.sql.Date convertToSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }


    /**
     * Convert the given java.sql.date into a java.sql.date of which all the time fields are set to 0.
     *
     * @param date
     * @return
     */
    public static java.sql.Date clearTimeFields(java.sql.Date date) {
        Calendar timelessCal = new GregorianCalendar();
        timelessCal.setTime(date);
        timelessCal.set(Calendar.HOUR_OF_DAY, 0);
        timelessCal.set(Calendar.MINUTE, 0);
        timelessCal.set(Calendar.SECOND, 0);
        timelessCal.set(Calendar.MILLISECOND, 0);

        return new java.sql.Date(timelessCal.getTimeInMillis());
    }


    /**
     * Convert the given java.util.date into a java.util.date of which all the time fields are set to 0.
     *
     * @param date
     * @return
     */
    public static java.util.Date clearTimeFields(java.util.Date date) {
        Calendar timelessCal = new GregorianCalendar();
        timelessCal.setTime(date);
        timelessCal.set(Calendar.HOUR_OF_DAY, 0);
        timelessCal.set(Calendar.MINUTE, 0);
        timelessCal.set(Calendar.SECOND, 0);
        timelessCal.set(Calendar.MILLISECOND, 0);

        return new java.util.Date(timelessCal.getTimeInMillis());
    }

    /**
     * @param startDateTime
     * @param endDateTime
     * @return the difference in days between the second timestamp and first
     */
    public static double getDifferenceInDays(Timestamp startDateTime, Timestamp endDateTime) {
        int difference = 0;

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDateTime);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDateTime);

        // First, get difference in whole days
        Calendar startCompare = Calendar.getInstance();
        startCompare.setTime(startDateTime);
        startCompare.set(Calendar.HOUR_OF_DAY, 0);
        startCompare.set(Calendar.MINUTE, 0);
        startCompare.set(Calendar.SECOND, 0);
        startCompare.set(Calendar.MILLISECOND, 0);

        Calendar endCompare = Calendar.getInstance();
        endCompare.setTime(endDateTime);
        endCompare.set(Calendar.HOUR_OF_DAY, 0);
        endCompare.set(Calendar.MINUTE, 0);
        endCompare.set(Calendar.SECOND, 0);
        endCompare.set(Calendar.MILLISECOND, 0);

        return (endCompare.getTimeInMillis() - startCompare.getTimeInMillis()) / ((double) KFSConstants.MILLSECONDS_PER_DAY);
    }

    /**
     * @param startDateTime
     * @param endDateTime
     * @return the difference in hours between the second timestamp and first
     */
    public static double getDifferenceInHours(Timestamp startDateTime, Timestamp endDateTime) {
        int difference = 0;

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDateTime);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDateTime);

        // First, get difference in whole days
        Calendar startCompare = Calendar.getInstance();
        startCompare.setTime(startDateTime);
        startCompare.set(Calendar.HOUR_OF_DAY, 0);
        startCompare.set(Calendar.MINUTE, 0);

        Calendar endCompare = Calendar.getInstance();
        endCompare.setTime(endDateTime);
        endCompare.set(Calendar.HOUR_OF_DAY, 0);
        endCompare.set(Calendar.MINUTE, 0);

        return (endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis()) / (60.0000 * 60.0000 * 1000.0000);
    }

    /**
     * This method is a utility method to create a new java.sql.Date in one line.
     *
     * @param year
     * @param month
     * @param day
     * @return a populated java.sql.Date with the year, month, and day specified, and no values for hour, minute, second,
     * millisecond
     */
    public static java.sql.Date newDate(Integer year, Integer month, Integer day) {

        // test for null arguments
        if (year == null) {
            throw new IllegalArgumentException("Argument 'year' passed in was null.");
        }
        if (month == null) {
            throw new IllegalArgumentException("Argument 'month' passed in was null.");
        }
        if (day == null) {
            throw new IllegalArgumentException("Argument 'day' passed in was null.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * This method is a utility method to create a new java.sql.Date in one line.
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return a populated java.sql.Date with the year, month, hour, minute, and second populated, with no value for millisecond.
     */
    public static java.sql.Date newDate(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer second) {

        // test for null arguments
        if (year == null) {
            throw new IllegalArgumentException("Argument 'year' passed in was null.");
        }
        if (month == null) {
            throw new IllegalArgumentException("Argument 'month' passed in was null.");
        }
        if (day == null) {
            throw new IllegalArgumentException("Argument 'day' passed in was null.");
        }
        if (hour == null) {
            throw new IllegalArgumentException("Argument 'hour' passed in was null.");
        }
        if (minute == null) {
            throw new IllegalArgumentException("Argument 'minute' passed in was null.");
        }
        if (second == null) {
            throw new IllegalArgumentException("Argument 'second' passed in was null.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.clear(Calendar.MILLISECOND);

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    /**
     * Determines if the given date d1 is on the same day or an earlier day than the given date d2.
     *
     * @param d1 a date
     * @param d2 another date, to compare the first date to
     * @return true if d1 is earlier or the same day as d2, false otherwise or if either value is null
     */
    public static boolean isSameDayOrEarlier(Date d1, Date d2) {
        if (ObjectUtils.isNull(d1) || ObjectUtils.isNull(d2)) {
            return false;
        }
        if (isSameDay(d1, d2)) {
            return true;
        }
        return d1.compareTo(d2) < 0;
    }

    /**
     * Determines if the given date d1 is on the same day or a later day than the given date d2.
     *
     * @param d1 a date
     * @param d2 another date, to compare the first date to
     * @return true if d1 is later or the same day as d2, false otherwise or if either value is null
     */
    public static boolean isSameDayOrLater(Date d1, Date d2) {
        if (ObjectUtils.isNull(d1) || ObjectUtils.isNull(d2)) {
            return false; // we can't compare against nulls
        }
        if (isSameDay(d1, d2)) {
            return true;
        }
        return d1.compareTo(d2) > 0;
    }

    /**
     * Determines if the given date d1 is on the same day or an earlier day than the given date d2.
     *
     * @param d1 a date
     * @param d2 another date, to compare the first date to
     * @return true if d1 is earlier or the same day as d2, false otherwise or if either value is null
     */
    public static boolean isEarlierDay(Date d1, Date d2) {
        if (ObjectUtils.isNull(d1) || ObjectUtils.isNull(d2)) {
            return false;
        }
        if (isSameDay(d1, d2)) {
            return false;
        }
        return d1.compareTo(d2) < 0;
    }
}
