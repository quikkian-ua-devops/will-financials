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

package org.kuali.kfs.coa.businessobject;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.util.LinkedHashMap;

/**
 * IndirectCostRecoveryAccount for A21SubAccount
 */
public class PriorYearIndirectCostRecoveryAccount extends IndirectCostRecoveryAccount {
    private static Logger LOG = Logger.getLogger(PriorYearIndirectCostRecoveryAccount.class);

    private Integer priorYearIndirectCostRecoveryAccountGeneratedIdentifier;

    /**
     * Default constructor.
     */
    public PriorYearIndirectCostRecoveryAccount() {
    }

    public PriorYearIndirectCostRecoveryAccount(IndirectCostRecoveryAccount icr) {
        BeanUtils.copyProperties(icr, this);
    }

    public Integer getPriorYearIndirectCostRecoveryAccountGeneratedIdentifier() {
        return priorYearIndirectCostRecoveryAccountGeneratedIdentifier;
    }

    public void setPriorYearIndirectCostRecoveryAccountGeneratedIdentifier(Integer priorYearIndirectCostRecoveryAccountGeneratedIdentifier) {
        this.priorYearIndirectCostRecoveryAccountGeneratedIdentifier = priorYearIndirectCostRecoveryAccountGeneratedIdentifier;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.priorYearIndirectCostRecoveryAccountGeneratedIdentifier != null) {
            m.put("priorYearIndirectCostRecoveryAccountGeneratedIdentifier", this.priorYearIndirectCostRecoveryAccountGeneratedIdentifier.toString());
        }
        return m;
    }

}
