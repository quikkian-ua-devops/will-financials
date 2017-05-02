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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.view.View;

import java.util.List;

/**
 * Field that contains a nested <code>Group</code>. Can be used to group
 * together fields by providing a group without header and footer, or simply to
 * nest full groups. The items getter/setter provided is for convenience and
 * will set the items <code>List</code> in the nested <code>Group</code>
 */
public class FieldGroup extends FieldBase {
    private static final long serialVersionUID = -505654043702442196L;

    private Group group;

    public FieldGroup() {
        super();
    }

    /**
     * The following initialization is performed:
     * <p>
     * <ul>
     * <li>Set the align on group if empty and the align has been set on the
     * field</li>
     * </ul>
     *
     * @see ComponentBase#performInitialization(View, java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        if (StringUtils.isNotBlank(getAlign()) && group != null) {
            group.setAlign(getAlign());
        }
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(group);

        return components;
    }

    /**
     * <code>Group</code> instance that is contained within in the field
     *
     * @return Group instance
     */
    public Group getGroup() {
        return this.group;
    }

    /**
     * Setter for the field's nested group
     *
     * @param group
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * List of <code>Component</code> instances contained in the nested group
     * <p>
     * <p>
     * Convenience method for configuration to get the items List from the
     * field's nested group
     * </p>
     *
     * @return List<? extends Component> items
     */
    public List<? extends Component> getItems() {
        if (group != null) {
            return group.getItems();
        }

        return null;
    }

    /**
     * Setter for the field's nested group items
     * <p>
     * <p>
     * Convenience method for configuration to set the items List for the
     * field's nested group
     * </p>
     *
     * @param items
     */
    public void setItems(List<? extends Component> items) {
        if (group != null) {
            group.setItems(items);
        }
    }

}
