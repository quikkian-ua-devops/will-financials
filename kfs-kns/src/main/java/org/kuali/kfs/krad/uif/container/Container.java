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

import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.field.ErrorsField;
import org.kuali.kfs.krad.uif.field.HeaderField;
import org.kuali.kfs.krad.uif.field.MessageField;
import org.kuali.kfs.krad.uif.layout.LayoutManager;
import org.kuali.kfs.krad.uif.widget.Help;

import java.util.List;
import java.util.Set;

/**
 * Type of component that contains a collection of other components. All
 * templates for <code> Container</code> components must use a
 * <code>LayoutManager</code> to render the contained components.
 * <p>
 * Each container has the following parts in addition to the contained components:
 * <ul>
 * <li><code>HeaderField</code></li>
 * <li>Summary <code>MessageField</code></li>
 * <li>Help component</li>
 * <li>Errors container</li>
 * <li>Footer <code>Group</code></li>
 * </ul>
 * Container implementations are free to add additional content as needed.
 *
 * @see Component
 */
public interface Container extends Component {

    /**
     * <code>List</code> of <code>Component</code> instances that are held by
     * the container
     * <p>
     * <p>
     * Contained components are rendered within the section template by calling
     * the associated <code>LayoutManager</code>
     * </p>
     *
     * @return List component instances
     */
    public List<? extends Component> getItems();

    /**
     * Setter for the containers list of components
     *
     * @param items - list of components to set in container
     */
    public void setItems(List<? extends Component> items);

    /**
     * <code>Set</code> of <code>Component</code> classes that may be placed
     * into the container
     * <p>
     * <p>
     * If an empty or null list is returned, it is assumed the container
     * supports all components. The returned set will be used by dictionary
     * validators and allows renders to make assumptions about the contained
     * components
     * </p>
     *
     * @return Set component classes
     */
    public Set<Class<? extends Component>> getSupportedComponents();

    /**
     * <code>LayoutManager</code> that should be used to layout the components
     * in the container
     * <p>
     * <p>
     * The template associated with the layout manager will be invoked passing
     * in the List of components from the container. This list is exported under
     * the attribute name 'items'
     * </p>
     *
     * @return LayoutManager instance
     */
    public LayoutManager getLayoutManager();

    /**
     * Setter for the containers layout manager
     *
     * @param layoutManager
     */
    public void setLayoutManager(LayoutManager layoutManager);

    /**
     * <code>HeaderField</code> associated with the container
     * <p>
     * <p>
     * Header fields are generally rendered at the beginning of the container to
     * indicate a grouping, although this is determined by the template
     * associated with the container. The actual rendering configuration (style
     * and so on) is configured within the HeaderField instance
     * </p>
     * <p>
     * Header is only rendered if <code>Container#isRenderHeader</code> is true
     * and getHeader() is not null
     * </p>
     *
     * @return HeaderField instance or Null
     */
    public HeaderField getHeader();

    /**
     * Setter for the containers header field
     *
     * @param header
     */
    public void setHeader(HeaderField header);

    /**
     * Footer <code>Group</code> associated with the container
     * <p>
     * <p>
     * The footer is usually rendered at the end of the container. Often this is
     * a place to put actions (buttons) for the container.
     * </p>
     * <p>
     * Footer is only rendered if <code>Container#isRenderFooter</code> is true
     * and getFooter is not null
     * </p>
     *
     * @return Group footer instance or Null
     */
    public Group getFooter();

    /**
     * Setter for the containers footer
     *
     * @param footer
     */
    public void setFooter(Group footer);

    /**
     * Text for the container that provides a summary description or
     * instructions
     * <p>
     * <p>
     * Text is encapsulated in a <code>MessageField</code> that contains
     * rendering configuration.
     * </p>
     * <p>
     * Summary <code>MessageField</code> only rendered if this methods does not
     * return null
     * </p>
     *
     * @return MessageField instance or Null
     */
    public MessageField getInstructionalMessageField();

    /**
     * Setter for the containers summary message field
     *
     * @param summaryMessageField
     */
    public void setInstructionalMessageField(MessageField summaryMessageField);

    /**
     * Field that contains the error messages for the container
     * <p>
     * <p>
     * Containers can collect the errors for the contained component and display
     * either all the messages or counts. This <code>Field</code> is used to
     * render those messages. Styling and other configuration is done through
     * the <code>ErrorsField</code>
     * </p>
     *
     * @return ErrorsField holding the container errors
     */
    public ErrorsField getErrorsField();

    /**
     * Setter for the containers errors field
     *
     * @param errorsField
     */
    public void setErrorsField(ErrorsField errorsField);

    /**
     * Help configuration object for the container
     * <p>
     * <p>
     * External help information can be configured for the container. The
     * <code>Help</code> object can the configuration for rendering a link to
     * that help information.
     * </p>
     *
     * @return Help for container
     */
    public Help getHelp();

    /**
     * Setter for the containers help content
     *
     * @param help
     */
    public void setHelp(Help help);

    /**
     * This property is true if the container is used to display a group of fields that is visually a single
     * field - this has an effect on where errors will show up for these fields.
     *
     * @return the fieldContainer
     */
    public boolean isFieldContainer();

    /**
     * @param fieldContainer the fieldContainer to set
     */
    public void setFieldContainer(boolean fieldContainer);

}
