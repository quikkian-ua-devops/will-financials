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
package org.kuali.kfs.sys.context;

import org.kuali.kfs.kns.bo.Step;
import org.springframework.aop.ClassFilter;

import java.lang.reflect.Proxy;

/**
 * Class filter used to match classes that executes batch job/steps, used to configure batch transaction settings
 */
public class BatchStepClassFilter implements ClassFilter {

    /**
     * Matches on Step implementations
     *
     * @see org.springframework.aop.ClassFilter#matches(java.lang.Class)
     */
    public boolean matches(Class clazz) {
        if (Step.class.isAssignableFrom(clazz)) {
            if (!Proxy.isProxyClass(clazz) && !isCglibProxyClass(clazz)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether the specified class is a CGLIB-generated class.
     *
     * @param clazz the class to check
     */
    public static boolean isCglibProxyClass(Class clazz) {
        return (clazz != null && clazz.getName().indexOf("$$") != -1);
    }

}
