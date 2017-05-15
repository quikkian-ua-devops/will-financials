/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.batch;

import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import static org.quartz.CronScheduleBuilder.cronSchedule;


public class CronTriggerDescriptor extends TriggerDescriptor {
    private String cronExpression;

    protected Trigger completeTriggerDescription(TriggerBuilder triggerBuilder) {
        // prevent setting of the trigger information in test mode
        triggerBuilder.withSchedule(cronSchedule(cronExpression).inTimeZone(getDateTimeService().getCurrentCalendar().getTimeZone()));
        if (!isTestMode()) {
            triggerBuilder.withSchedule(cronSchedule(cronExpression).inTimeZone(getDateTimeService().getCurrentCalendar().getTimeZone()));
        } else {
            triggerBuilder.withSchedule(cronSchedule("0 59 23 31 12 ? 2099").inTimeZone(getDateTimeService().getCurrentCalendar().getTimeZone()));
        }

        return triggerBuilder.build();
    }

    /**
     * Sets the cronExpression attribute value.
     *
     * @param cronExpression The cronExpression to set.
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
