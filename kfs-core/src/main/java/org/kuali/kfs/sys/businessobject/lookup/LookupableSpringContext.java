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
package org.kuali.kfs.sys.businessobject.lookup;

import org.kuali.kfs.kns.lookup.Lookupable;
import org.kuali.kfs.kns.lookup.LookupableHelperService;
import org.kuali.kfs.sys.context.SpringContext;

public class LookupableSpringContext {
    public static Lookupable getLookupable(String beanId) {
        try {
            return SpringContext.getBean(Lookupable.class, beanId);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public static LookupableHelperService getLookupableHelperService(String beanId) {
        try {
            return SpringContext.getBean(LookupableHelperService.class, beanId);
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
