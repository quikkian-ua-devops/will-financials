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
package org.kuali.kfs.krad.uif.field;

/**
 * Field that produces only a space
 * <p>
 * <p>
 * Can be used to aid in the layout of other fields, for instance in a grid. For
 * example in a totals row generally the rows that are not totaled are blank in
 * the total row.
 * </p>
 */
public class BlankField extends FieldBase {
    private static final long serialVersionUID = -4740343801872334348L;

    public BlankField() {
        super();
    }

}
