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
package org.kuali.kfs.kns.web.comparator;

import org.displaytag.model.Cell;

import java.io.Serializable;
import java.util.Comparator;

public class BooleanCellComparator implements Comparator, Serializable {

    static final long serialVersionUID = 1525781435762831055L;

    public int compare(Object o1, Object o2) {

        // null guard. non-null value is greater. equal if both are null
        if (null == o1 || null == o2) {

            return null == o1 && null == o2 ? 0 : null == o1 ? -1 : 1;

        }

        String s1 = CellComparatorHelper.getSanitizedStaticValue((Cell) o1);
        String s2 = CellComparatorHelper.getSanitizedStaticValue((Cell) o2);

        int compared = 0;

        Boolean b1 = Boolean.valueOf(s1);
        Boolean b2 = Boolean.valueOf(s2);

        if (!b1.equals(b2)) {
            if (b1.equals(Boolean.FALSE)) {
                compared = -1;
            } else {
                compared = 1;
            }
        }

        return compared;
    }

}
