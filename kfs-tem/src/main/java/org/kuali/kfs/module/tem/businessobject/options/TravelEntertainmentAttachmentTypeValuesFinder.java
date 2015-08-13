/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.tem.TemConstants.AttachmentTypeCodes;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;

public class TravelEntertainmentAttachmentTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_ATTENDEE_LIST, AttachmentTypeCodes.ATTACHMENT_TYPE_ATTENDEE_LIST));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_ENT_HOST_CERT, AttachmentTypeCodes.ATTACHMENT_TYPE_ENT_HOST_CERT));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.ATTACHMENT_TYPE_RECEIPT, AttachmentTypeCodes.ATTACHMENT_TYPE_RECEIPT));
        keyValues.add(new ConcreteKeyValue(AttachmentTypeCodes.NON_EMPLOYEE_FORM, AttachmentTypeCodes.NON_EMPLOYEE_FORM));

        return keyValues;
    }
}
