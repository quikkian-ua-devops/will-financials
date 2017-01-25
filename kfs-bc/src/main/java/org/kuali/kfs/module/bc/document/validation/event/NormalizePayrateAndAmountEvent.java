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
package org.kuali.kfs.module.bc.document.validation.event;

import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;

/**
 * Event triggered when normalizing the pay rate and salary amount.
 */
public class NormalizePayrateAndAmountEvent extends SalarySettingBaseEvent {
    PendingBudgetConstructionAppointmentFunding appointmentFunding;

    /**
     * Constructs a PayrateAndAmountNormalizationEvent.java.
     *
     * @param errorPathPrefix    the specified error path prefix
     * @param appointmentFunding the given appointment funding
     */
    public NormalizePayrateAndAmountEvent(String description, String errorPathPrefix, BudgetConstructionDocument document, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        super(description, errorPathPrefix, document);
        this.appointmentFunding = appointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent#invokeExpansionRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    @Override
    public boolean invokeExpansionRuleMethod(BusinessRule rule) {
        return ((SalarySettingRule) rule).processNormalizePayrateAndAmount(appointmentFunding);
    }
}
