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
package org.kuali.kfs.krad.uif.component;

import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.service.ExpressionEvaluatorService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Configuration for replacing a property value based on a condition
 * <p>
 * <p>
 * A <code>Component</code> may be configured with one or more <code>PropertyReplacer</code> instances. Each defines
 * a condition to evaluate during the apply model phase, and if that condition succeeds the property on the component
 * given by {@link #getPropertyName()}, will be replaced with the value given by {@link #getReplacement()}. Conditions
 * are defined using an expression language and may reference any variables available in the component's context.
 * </p>
 * <p>
 * <p>
 * Property replacers can be used to change out an entire Component or List/Map of Components. For example, based on a
 * condition you might want to display a <code>TextControl</code> or <code>RadioControl</code> for an
 * <code>InputField</code>. You can define the field with a text control, then include a property replacer as
 * follows:
 * <pre>
 * <bean parent="PropertyReplacer" p:propertyName="control"
 * p:condition="field1 eq '10985'" p:replacement-ref="RadioControl"/>
 *
 * </pre>
 * <p>
 * Note <code>Component</code> contains a <code>List</code> or property replacers which will be evaluated in the order
 * contained within the list. So in the above example if we wanted to now add a further condition which sets the control
 * to a checkbox, we would just add another property replacer bean.
 * <pre>
 *   <property name="propertyReplacers">
 * <list>
 * <bean parent="PropertyReplacer" p:propertyName="control"
 * p:condition="field1 eq '10985'" p:replacement-ref="RadioControl"/>
 * <bean parent="PropertyReplacer" p:propertyName="control"
 * p:condition="field1 eq '11456'" p:replacement-ref="CheckboxControl"/>
 *     </list>
 *   </property>
 * </pre>
 * <p>
 * Property replacers may be used to substitute primitive properties as well, such as Strings
 * </p>
 */
public class PropertyReplacer extends ConfigurableBase implements Serializable {
    private static final long serialVersionUID = -8405429643299461398L;

    private String propertyName;
    private String condition;
    private Object replacement;

    public PropertyReplacer() {
        super();
    }

    /**
     * Returns a list of nested components
     * <p>
     * <p>
     * All nested components will be returned in the list. Current assumption is that
     * <code>PropertyReplacer</code> can only contain a <code>Component</code>, <code>List</code> or
     * <code>Map</code> for nested components
     * </p>
     *
     * @return List<Component> nested components
     */
    public List<Component> getNestedComponents() {
        ArrayList<Component> nestedComponents = new ArrayList<Component>();
        if (replacement instanceof Component) {
            nestedComponents.add(((Component) replacement));
        } else if (replacement instanceof List) {
            for (Object replacementItem : (List<?>) replacement) {
                if (replacementItem instanceof Component) {
                    nestedComponents.add((Component) replacementItem);
                }
            }
        } else if (replacement instanceof Map) {
            for (Object replacementItem : ((Map<?, ?>) replacement).values()) {
                if (replacementItem instanceof Component) {
                    nestedComponents.add((Component) replacementItem);
                }
            }
        }

        return nestedComponents;
    }

    /**
     * Name of the property on the Component the property replacer is associated with that
     * will be set when the condition for the replacer succeeds
     * <p>
     * <p>
     * Note the property name must be readable/writable on the component. The property name may
     * be nested, and include Map or List references.
     * </p>
     *
     * @return String property name to set
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * Setter for the property name that will be set
     *
     * @param propertyName
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Gives the expression that should be evaluated to determine whether or not
     * the property replacement should be made
     * <p>
     * <p>
     * Expression follows SPEL and may access any model data along with any variables
     * available in the context for the Component. The expression should evaluate to
     * a boolean. If the resulting boolean is true, the object given by {@link #getReplacement()}
     * will be set as the value for the associated property on the component. If the resulting
     * boolean is false, no action will take place
     * </p>
     * <p>
     * <p>
     * Note the value does not need to contain the expression placeholder @{}
     * </p>
     *
     * @return String expression that should be evaluated
     * @see ExpressionEvaluatorService
     * @see UifConstants.ContextVariableNames
     */
    public String getCondition() {
        return this.condition;
    }

    /**
     * Setter for the replacement condition
     *
     * @param condition
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Gives the Object that should be used to set the property value if the replacers condition
     * evaluates to true
     * <p>
     * <p>
     * Note the configured Object must be valid for the type given by the property on the Component. Standard
     * property editors will be used for setting the property value
     * </p>
     *
     * @return Object instance to set
     */
    public Object getReplacement() {
        return this.replacement;
    }

    /**
     * Setter for the replacement Object
     *
     * @param replacement
     */
    public void setReplacement(Object replacement) {
        this.replacement = replacement;
    }

}
