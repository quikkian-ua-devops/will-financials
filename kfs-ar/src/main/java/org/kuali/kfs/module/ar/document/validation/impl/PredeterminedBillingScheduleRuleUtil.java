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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Rules for the Predetermined Billing Schedule maintenance document.
 */
public class PredeterminedBillingScheduleRuleUtil {
    /**
     * Determines if a award has bills defined
     *
     * @param predeterminedSchedule to check the award for
     * @return true if the award has bills defined
     */
    public static boolean checkIfBillsExist(PredeterminedBillingSchedule predeterminedBillingSchedule) {
        if (ObjectUtils.isNull(predeterminedBillingSchedule)) {
            return false;
        }

        PredeterminedBillingSchedule result = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(PredeterminedBillingSchedule.class, predeterminedBillingSchedule.getProposalNumber());

        // Make sure it exists and is not the same object
        return ObjectUtils.isNotNull(result) && !StringUtils.equals(predeterminedBillingSchedule.getObjectId(), result.getObjectId());
    }
}
