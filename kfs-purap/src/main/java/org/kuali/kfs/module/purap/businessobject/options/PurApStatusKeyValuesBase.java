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
package org.kuali.kfs.module.purap.businessobject.options;

import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.service.KeyValuesService;
import org.kuali.kfs.module.purap.businessobject.Status;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base Value Finder for Purchasing / Accounts Payable Statuses.
 */
public class PurApStatusKeyValuesBase extends KeyValuesBase {

    /**
     * Returns code/description pairs of all PurAp Statuses.
     *
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<Status> statuses = boService.findAll(getStatusClass());
        List labels = new ArrayList();
        for (Status status : statuses) {
            labels.add(new ConcreteKeyValue(status.getStatusCode(), status.getStatusDescription()));
        }
        return labels;
    }

    /**
     * Returns Status class for this Value Finder.
     */
    public Class getStatusClass() {
        // method must be overriden
        throw new RuntimeException("getStatusClass() method must be overridden to be used");
    }
}
