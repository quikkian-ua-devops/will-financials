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
package org.kuali.kfs.krad.uif.container;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.component.DataBinding;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.field.FieldGroup;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.widget.Disclosure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Container that holds a list of <code>Field</code> or other <code>Group</code>
 * instances
 * <p>
 * <p>
 * Groups can exist at different levels of the <code>View</code>, providing
 * conceptual groupings such as the page, section, and group. In addition, other
 * group types can be created to add behavior like collection support
 * </p>
 * <p>
 * <p>
 * <code>Group</code> implementation has properties for defaulting the binding
 * information (such as the parent object path and a binding prefix) for the
 * fields it contains. During the phase these properties (if given) are set on
 * the fields contained in the <code>Group</code> that implement
 * <code>DataBinding</code>, unless they have already been set on the field.
 * </p>
 */
public class Group extends ContainerBase {
    private static final long serialVersionUID = 7953641325356535509L;

    private String fieldBindByNamePrefix;
    private String fieldBindingObjectPath;

    private Disclosure disclosure;

    private List<? extends Component> items;

    /**
     * Default Constructor
     */
    public Group() {
        items = new ArrayList<Component>();
    }

    /**
     * The following actions are performed:
     * <p>
     * <ul>
     * <li>Sets the bindByNamePrefix if blank on any InputField and
     * FieldGroup instances within the items List</li>
     * </ul>
     *
     * @see ComponentBase#performInitialization(View, java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        for (Component component : getItems()) {
            // append group's field bind by name prefix (if set) to each
            // attribute field's binding prefix
            if (component instanceof DataBinding) {
                DataBinding dataBinding = (DataBinding) component;

                if (StringUtils.isNotBlank(getFieldBindByNamePrefix())) {
                    String bindByNamePrefixToSet = getFieldBindByNamePrefix();

                    if (StringUtils.isNotBlank(dataBinding.getBindingInfo().getBindByNamePrefix())) {
                        bindByNamePrefixToSet += "." + dataBinding.getBindingInfo().getBindByNamePrefix();
                    }
                    dataBinding.getBindingInfo().setBindByNamePrefix(bindByNamePrefixToSet);
                }

                if (StringUtils.isNotBlank(fieldBindingObjectPath) &&
                    StringUtils.isBlank(dataBinding.getBindingInfo().getBindingObjectPath())) {
                    dataBinding.getBindingInfo().setBindingObjectPath(fieldBindingObjectPath);
                }
            }
            // set on FieldGroup's group to recursively set AttributeFields
            else if (component instanceof FieldGroup) {
                FieldGroup fieldGroup = (FieldGroup) component;

                if (fieldGroup.getGroup() != null) {
                    if (StringUtils.isBlank(fieldGroup.getGroup().getFieldBindByNamePrefix())) {
                        fieldGroup.getGroup().setFieldBindByNamePrefix(fieldBindByNamePrefix);
                    }
                    if (StringUtils.isBlank(fieldGroup.getGroup().getFieldBindingObjectPath())) {
                        fieldGroup.getGroup().setFieldBindingObjectPath(fieldBindingObjectPath);
                    }
                }
            } else if (component instanceof Group) {
                Group subGroup = (Group) component;
                if (StringUtils.isNotBlank(getFieldBindByNamePrefix())) {
                    if (StringUtils.isNotBlank(subGroup.getFieldBindByNamePrefix())) {
                        subGroup.setFieldBindByNamePrefix(
                            getFieldBindByNamePrefix() + "." + subGroup.getFieldBindByNamePrefix());
                    } else {
                        subGroup.setFieldBindByNamePrefix(getFieldBindByNamePrefix());
                    }
                }
                if (StringUtils.isNotBlank(getFieldBindingObjectPath())) {
                    if (StringUtils.isNotBlank(subGroup.getFieldBindingObjectPath())) {
                        subGroup.setFieldBindingObjectPath(
                            getFieldBindingObjectPath() + "." + subGroup.getFieldBindingObjectPath());
                    } else {
                        subGroup.setFieldBindingObjectPath(getFieldBindingObjectPath());
                    }
                }
            }
        }
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(disclosure);

        return components;
    }

    /**
     * @see org.kuali.rice.krad.web.view.container.ContainerBase#getSupportedComponents()
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> supportedComponents = new HashSet<Class<? extends Component>>();
        supportedComponents.add(Field.class);
        supportedComponents.add(Group.class);

        return supportedComponents;
    }

    /**
     * @see Component#getComponentTypeName()
     */
    @Override
    public final String getComponentTypeName() {
        return "group";
    }

    /**
     * Binding prefix string to set on each of the groups <code>DataField</code> instances
     * <p>
     * <p>
     * As opposed to setting the bindingPrefix on each attribute field instance,
     * it can be set here for the group. During initialize the string will then
     * be set on each attribute field instance if the bindingPrefix is blank and
     * not a form field
     * </p>
     *
     * @return String binding prefix to set
     */
    public String getFieldBindByNamePrefix() {
        return this.fieldBindByNamePrefix;
    }

    /**
     * Setter for the field binding prefix
     *
     * @param fieldBindByNamePrefix
     */
    public void setFieldBindByNamePrefix(String fieldBindByNamePrefix) {
        this.fieldBindByNamePrefix = fieldBindByNamePrefix;
    }

    /**
     * Object binding path to set on each of the group's
     * <code>InputField</code> instances
     * <p>
     * <p>
     * When the attributes of the group belong to a object whose path is
     * different from the default then this property can be given to set each of
     * the attributes instead of setting the model path on each one. The object
     * path can be overridden at the attribute level. The object path is set to
     * the fieldBindingObjectPath during the initialize phase.
     * </p>
     *
     * @return String model path to set
     * @see org.kuali.rice.krad.uif.BindingInfo.getBindingObjectPath()
     */
    public String getFieldBindingObjectPath() {
        return this.fieldBindingObjectPath;
    }

    /**
     * Setter for the field object binding path
     *
     * @param fieldBindingObjectPath
     */
    public void setFieldBindingObjectPath(String fieldBindingObjectPath) {
        this.fieldBindingObjectPath = fieldBindingObjectPath;
    }

    /**
     * Disclosure widget that provides collapse/expand functionality for the
     * group
     *
     * @return Accordion instance
     */
    public Disclosure getDisclosure() {
        return this.disclosure;
    }

    /**
     * Setter for the group's disclosure instance
     *
     * @param disclosure
     */
    public void setDisclosure(Disclosure disclosure) {
        this.disclosure = disclosure;
    }

    /**
     * @see ContainerBase#getItems()
     */
    @Override
    public List<? extends Component> getItems() {
        return this.items;
    }

    /**
     * Setter for the Group's list of components
     *
     * @param items
     */
    @Override
    public void setItems(List<? extends Component> items) {
        this.items = items;
    }

}
