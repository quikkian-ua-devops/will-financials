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
package org.kuali.kfs.module.cg.businessobject.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.service.KeyValuesService;
import org.kuali.kfs.module.cg.businessobject.ProposalStatus;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Gets a custom-formatted list of {@link ProposalStatus} values.
 */
public class ProposalStatusValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        Collection<ProposalStatus> codes = SpringContext.getBean(KeyValuesService.class).findAll(ProposalStatus.class);

        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));

        for (ProposalStatus proposalStatus : codes) {
            if (proposalStatus.isActive()) {
                labels.add(new ConcreteKeyValue(proposalStatus.getProposalStatusCode(), proposalStatus.getProposalStatusCode() + "-" + proposalStatus.getProposalStatusDescription()));
            }
        }

        return labels;
    }
}
