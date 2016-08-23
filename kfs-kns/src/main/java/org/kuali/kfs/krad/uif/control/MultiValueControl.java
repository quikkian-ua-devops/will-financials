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
package org.kuali.kfs.krad.uif.control;

import org.kuali.rice.core.api.util.KeyValue;

import java.util.List;

/**
 * Indicates <code>Control</code> types that can hold more than one value for selection
 *
 *
 */
public interface MultiValueControl {

    /**
     * <code>List</code> of values the control can accept. Each value consists
     * of a key and a label. The key is the what will be submitted back if the
     * user selects the choice, the label is what will be displayed to the user
     * for the choice.
     * <p>
     * <code>KeyLabelPair</code> instances are usually generated by the
     * <code>KeyValueFinder</code> associated with the <code>Field</code> for
     * which the control belongs
     * </p>
     *
     * @return List of KeyLabelPair instances
     */
    public List<KeyValue> getOptions();

    /**
     * Sets the List of <code>KeyValue</code> pairs that make up the options for the control
     *
     * @param options
     */
    public void setOptions(List<KeyValue> options);
}
