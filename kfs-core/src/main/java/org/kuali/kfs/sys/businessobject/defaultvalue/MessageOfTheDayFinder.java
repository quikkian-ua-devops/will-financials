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
package org.kuali.kfs.sys.businessobject.defaultvalue;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.MessageOfTheDay;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.valuefinder.ValueFinder;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.Collection;

public class MessageOfTheDayFinder implements ValueFinder {
    //    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/MessageOfTheDayFinder";
//
//    @Cacheable(value=CACHE_NAME)
    @Override
    public String getValue() {
        try {
            Collection<MessageOfTheDay> collection = SpringContext.getBean(BusinessObjectService.class).findAll(MessageOfTheDay.class);
            if (collection != null && !collection.isEmpty()) {
                return collection.iterator().next().getFinancialSystemMessageOfTheDayText();
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass()).error("Unable to retrieve the message of the day", ex);
        }
        return "unable to retrieve message of the day";
    }

}
