/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
/**
 * Copyright 2005-2015 The Kuali Foundation
 * <p>
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.opensource.org/licenses/ecl2.php
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.core.framework.persistence.ojb.conversion;

import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

import java.math.BigDecimal;


public class OjbKualiIntegerPercentageFieldConversion extends OjbDecimalPercentageFieldConversion {

    /**
     * Convert percentages to decimals for proper storage.
     *
     * @see FieldConversion#javaToSql(Object)
     */
    public Object javaToSql(Object source) {
        if (source != null && source instanceof KualiInteger) {
            return super.javaToSql(((KualiInteger) source).bigDecimalValue());
        } else {
            return null;
        }
    }

    /**
     * Convert database decimals to 'visual' percentages for use in our business objects.
     *
     * @see FieldConversion#sqlToJava(Object)
     */
    public Object sqlToJava(Object source) {
        if (source != null && source instanceof BigDecimal) {
            return new KualiInteger(((KualiDecimal) (super.sqlToJava(source))).bigDecimalValue());
        } else {
            return null;
        }
    }

}
