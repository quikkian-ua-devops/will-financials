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
package org.kuali.kfs.coa.businessobject.options;

import org.kuali.kfs.coa.businessobject.MandatoryTransferEliminationCode;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.krad.service.KeyValuesService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link MandatoryTransferEliminationCode}s)
 */
public class MandatoryTransferEliminationCodeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link MandatoryTransferEliminationCode}s using their code as their key, and their code "-" name as the
     * display value
     *
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all Mandatory Transfer Elimination Codes
        List<MandatoryTransferEliminationCode> codes = (List<MandatoryTransferEliminationCode>) SpringContext.getBean(KeyValuesService.class).findAll(MandatoryTransferEliminationCode.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if (codes == null) {
            codes = new ArrayList<MandatoryTransferEliminationCode>(0);
        } else {
            codes = new ArrayList<MandatoryTransferEliminationCode>(codes);
        }

        // sort using comparator.
        Collections.sort(codes, new MandatoryTransferEliminationCodeComparator());

        // create a new list (code, descriptive-name)
        List<KeyValue> labels = new ArrayList<KeyValue>();

        for (MandatoryTransferEliminationCode mteCode : codes) {
            if (mteCode.isActive()) {
                labels.add(new ConcreteKeyValue(mteCode.getCode(), mteCode.getCodeAndDescription()));
            }
        }

        return labels;
    }

}
