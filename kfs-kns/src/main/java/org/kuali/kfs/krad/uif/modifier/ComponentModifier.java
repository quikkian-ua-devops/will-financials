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
package org.kuali.kfs.krad.uif.modifier;

import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.Configurable;
import org.kuali.kfs.krad.uif.component.Ordered;
import org.kuali.kfs.krad.uif.service.ViewHelperService;
import org.kuali.kfs.krad.uif.view.View;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Provides modification functionality for a <code>Component</code>
 * <p>
 * <p>
 * <code>ComponentModifier</code> instances are configured by the component's
 * dictionary definition. They can be used to provide dynamic initialization
 * behavior for a certain type of component or all components based on the
 * getComponentsForLifecycle method. In addition they can do dynamic generation of
 * new <code>Component</code> instances, or replacement of the components or
 * their properties.
 * </p>
 * <p>
 * <p>
 * Modifiers provide for more usability and flexibility of component
 * configuration. For instance if a <code>Group</code> definition is already
 * configured that is close to what the developer needs, but they need to make
 * global changes of the group, then can invoke or create a
 * <code>ComponentModifier</code> for the group to apply those changes. The
 * configuration can then inherit the exiting group definition and then specify
 * the modifier to run with the component's componentModifiers property.
 * </p>
 */
public interface ComponentModifier extends Configurable, Serializable, Ordered {

    /**
     * Should be called to initialize the ComponentModifier
     * <p>
     * <p>
     * This is where component modifiers can set defaults and setup other necessary
     * state. The initialize method should only be called once per layout
     * manager lifecycle and is invoked within the initialize phase of the view
     * lifecylce.
     * </p>
     * <p>
     * <p>
     * Note if the component modifier holds nested components, they should be initialized
     * in this method by calling the view helper service
     * </p>
     *
     * @param view      - View instance the component modifier is a part of
     * @param component - Component the modifier is configured on
     * @parma model - object instance containing the view data
     * @see ViewHelperService#performInitialization
     */
    public void performInitialization(View view, Object model, Component component);

    /**
     * Invoked within the configured phase of the component lifecycle. This is
     * where the <code>ComponentModifier</code> should perform its work against
     * the given <code>Component</code> instance
     *
     * @param view      - the view instance to which the component belongs
     * @param model     - top level object containing the view data
     * @param component - the component instance to modify
     * @see ComponentModifier#performModification
     * (View, Object, Component)
     */
    public void performModification(View view, Object model, Component component);

    /**
     * <code>Set</code> of <code>Component</code> classes that may be sent to
     * the modifier
     * <p>
     * <p>
     * If an empty or null list is returned, it is assumed the modifier supports
     * all components. The returned set will be used by the dictionary
     * validation
     * </p>
     *
     * @return Set component classes
     */
    public Set<Class<? extends Component>> getSupportedComponents();

    /**
     * List of components that are maintained by the modifier as prototypes for creating other component instances
     * <p>
     * <p>
     * Prototypes are held for configuring how a component should be created during the lifecycle. An example of this
     * are the fields in a collection group that are created for each collection record. They only participate in the
     * initialize phase.
     * </p>
     *
     * @return List<Component> child component prototypes
     */
    public List<Component> getComponentPrototypes();

    /**
     * Indicates what phase of the component lifecycle the
     * <code>ComponentModifier</code> should be invoked in (INITIALIZE,
     * APPLY_MODEL, or FINALIZE)
     *
     * @return String view lifecycle phase
     * @see UifConstants.ViewPhases
     */
    public String getRunPhase();

    /**
     * Conditional expression to evaluate for determining whether the component
     * modifier should be run. If the expression evaluates to true the modifier
     * will be executed, otherwise it will not be executed
     *
     * @return String el expression that should evaluate to boolean
     */
    public String getRunCondition();

    /**
     * @see org.springframework.core.Ordered#getOrder()
     */
    public int getOrder();

    /**
     * @see Ordered#setOrder(int)
     */
    public void setOrder(int order);

}
