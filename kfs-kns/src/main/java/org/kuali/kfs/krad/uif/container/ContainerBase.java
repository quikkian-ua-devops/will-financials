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
import org.kuali.kfs.krad.uif.field.ErrorsField;
import org.kuali.kfs.krad.uif.field.FieldGroup;
import org.kuali.kfs.krad.uif.field.HeaderField;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.field.MessageField;
import org.kuali.kfs.krad.uif.layout.LayoutManager;
import org.kuali.kfs.krad.uif.util.ComponentUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.widget.Help;

import java.util.ArrayList;
import java.util.List;

/**
 * Base <code>Container</code> implementation which container implementations
 * can extend
 * <p>
 * <p>
 * Provides properties for the basic <code>Container</code> functionality in
 * addition to default implementation of the lifecycle methods including some
 * setup of the header, items list, and layout manager
 * </p>
 */
public abstract class ContainerBase extends ComponentBase implements Container {
    private static final long serialVersionUID = -4182226230601746657L;

    private int itemOrderingSequence;

    private String additionalMessageKeys;
    private ErrorsField errorsField;

    private Help help;
    private LayoutManager layoutManager;

    private HeaderField header;
    private Group footer;

    private String instructionalText;
    private MessageField instructionalMessageField;

    private boolean fieldContainer;

    /**
     * Default Constructor
     */
    public ContainerBase() {
        itemOrderingSequence = 1;
    }

    /**
     * The following initialization is performed:
     * <p>
     * <ul>
     * <li>Sorts the containers list of components</li>
     * <li>Initializes LayoutManager</li>
     * </ul>
     *
     * @see ComponentBase#performInitialization(View, java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        // sort items list by the order property
        List<? extends Component> sortedItems = (List<? extends Component>) ComponentUtils.sort(getItems(),
            itemOrderingSequence);
        setItems(sortedItems);

        if (layoutManager != null) {
            layoutManager.performInitialization(view, model, this);
        }
    }

    /**
     * @see ComponentBase#performApplyModel(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (layoutManager != null) {
            layoutManager.performApplyModel(view, model, this);
        }
    }

    /**
     * The following finalization is performed:
     * <p>
     * <ul>
     * <li>Sets the headerText of the header Group if it is blank</li>
     * <li>Set the messageText of the summary MessageField if it is blank</li>
     * <li>Finalizes LayoutManager</li>
     * </ul>
     *
     * @see ComponentBase#performFinalize(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // if header title not given, use the container title
        if (header != null && StringUtils.isBlank(header.getHeaderText())) {
            header.setHeaderText(this.getTitle());
        }

        // setup summary message field if necessary
        if (instructionalMessageField != null && StringUtils.isBlank(instructionalMessageField.getMessageText())) {
            instructionalMessageField.setMessageText(instructionalText);
        }

        if (layoutManager != null) {
            layoutManager.performFinalize(view, model, this);
        }
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(header);
        components.add(footer);
        components.add(errorsField);
        components.add(help);
        components.add(instructionalMessageField);

        for (Component component : getItems()) {
            components.add(component);
        }

        if (layoutManager != null) {
            components.addAll(layoutManager.getComponentsForLifecycle());
        }

        return components;
    }

    /**
     * @see Component#getComponentPrototypes()
     */
    @Override
    public List<Component> getComponentPrototypes() {
        List<Component> components = super.getComponentPrototypes();

        if (layoutManager != null) {
            components.addAll(layoutManager.getComponentPrototypes());
        }

        return components;
    }

    /**
     * Additional keys that should be matching on when gathering errors or other
     * messages for the <code>Container</code>
     * <p>
     * <p>
     * Messages associated with the container will be displayed with the
     * container grouping in the user interface. Typically, these are a result
     * of problems with the containers fields or some other business logic
     * associated with the containers information. The framework will by default
     * include all the error keys for fields in the container, and also an
     * errors associated with the containers id. Keys given here will be matched
     * in addition to those defaults.
     * </p>
     * <p>
     * <p>
     * Multple keys can be given using the comma delimiter, the * wildcard is
     * also allowed in the message key
     * </p>
     *
     * @return String additional message key string
     */
    public String getAdditionalMessageKeys() {
        return this.additionalMessageKeys;
    }

    /**
     * Setter for the components additional message key string
     *
     * @param additionalMessageKeys
     */
    public void setAdditionalMessageKeys(String additionalMessageKeys) {
        this.additionalMessageKeys = additionalMessageKeys;
    }

    /**
     * @see Container#getErrorsField()
     */
    @Override
    public ErrorsField getErrorsField() {
        return this.errorsField;
    }

    /**
     * @see Container#setErrorsField(ErrorsField)
     */
    @Override
    public void setErrorsField(ErrorsField errorsField) {
        this.errorsField = errorsField;
    }

    /**
     * @see Container#getHelp()
     */
    @Override
    public Help getHelp() {
        return this.help;
    }

    /**
     * @see Container#setHelp(Help)
     */
    @Override
    public void setHelp(Help help) {
        this.help = help;
    }

    /**
     * @see Container#getItems()
     */
    @Override
    public abstract List<? extends Component> getItems();

    /**
     * Setter for the containers list of components
     *
     * @param items
     */
    public abstract void setItems(List<? extends Component> items);

    /**
     * For <code>Component</code> instances in the container's items list that
     * do not have an order set, a default order number will be assigned using
     * this property. The first component found in the list without an order
     * will be assigned the configured initial value, and incremented by one for
     * each component (without an order) found afterwards
     *
     * @return int order sequence
     */
    public int getItemOrderingSequence() {
        return this.itemOrderingSequence;
    }

    /**
     * Setter for the container's item ordering sequence number (initial value)
     *
     * @param itemOrderingSequence
     */
    public void setItemOrderingSequence(int itemOrderingSequence) {
        this.itemOrderingSequence = itemOrderingSequence;
    }

    /**
     * @see Container#getLayoutManager()
     */
    @Override
    public LayoutManager getLayoutManager() {
        return this.layoutManager;
    }

    /**
     * @see Container#setLayoutManager(LayoutManager)
     */
    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /**
     * @see Container#getHeader()
     */
    @Override
    public HeaderField getHeader() {
        return this.header;
    }

    /**
     * @see Container#setHeader(HeaderField)
     */
    @Override
    public void setHeader(HeaderField header) {
        this.header = header;
    }

    /**
     * @see Container#getFooter()
     */
    @Override
    public Group getFooter() {
        return this.footer;
    }

    /**
     * @see Container#setFooter(Group)
     */
    @Override
    public void setFooter(Group footer) {
        this.footer = footer;
    }

    /**
     * Convenience setter for configuration to turn rendering of the header
     * on/off
     * <p>
     * <p>
     * For nested groups (like Field Groups) it is often necessary to only show
     * the container body (the contained components). This method allows the
     * header to not be displayed
     * </p>
     *
     * @param renderHeader
     */
    public void setRenderHeader(boolean renderHeader) {
        if (header != null) {
            header.setRender(renderHeader);
        }
    }

    /**
     * Convenience setter for configuration to turn rendering of the footer
     * on/off
     * <p>
     * <p>
     * For nested groups it is often necessary to only show the container body
     * (the contained components). This method allows the footer to not be
     * displayed
     * </p>
     *
     * @param renderFooter
     */
    public void setRenderFooter(boolean renderFooter) {
        if (footer != null) {
            footer.setRender(renderFooter);
        }
    }

    /**
     * Text explaining how complete the group inputs, including things like what values should be selected
     * in certain cases, what fields should be completed and so on (instructions)
     *
     * @return String instructional message
     */
    public String getInstructionalText() {
        return this.instructionalText;
    }

    /**
     * Setter for the instructional message
     *
     * @param instructionalText
     */
    public void setInstructionalText(String instructionalText) {
        this.instructionalText = instructionalText;
    }

    /**
     * Message field that displays instructional text
     * <p>
     * <p>
     * This message field can be configured to for adjusting how the instructional text will display. Generally
     * the styleClasses property will be of most interest
     * </p>
     *
     * @return MessageField instructional message field
     */
    public MessageField getInstructionalMessageField() {
        return this.instructionalMessageField;
    }

    /**
     * Setter for the instructional text message field
     * <p>
     * <p>
     * Note this is the setter for the field that will render the instructional text. The actual text can be
     * set on the field but can also be set using {@link #setInstructionalText(String)}
     * </p>
     *
     * @param instructionalMessageField
     */
    public void setInstructionalMessageField(MessageField instructionalMessageField) {
        this.instructionalMessageField = instructionalMessageField;
    }

    /**
     * Gets only the data fields that are nested in this container.  This is a subset of
     * what getComponentsForLifecycle() returns
     *
     * @return
     */
    public List<InputField> getInputFields() {
        List<InputField> inputFields = new ArrayList<InputField>();
        for (Component c : this.getComponentsForLifecycle()) {
            if (c instanceof InputField) {
                inputFields.add((InputField) c);
            }
        }
        return inputFields;

    }

    /**
     * getAllInputFields gets all the input fields contained in this container, but also in
     * every sub-container that is a child of this container.  When called from the top level
     * View this will be every InputField across all pages.
     *
     * @return every InputField that is a child at any level of this container
     */
    public List<InputField> getAllInputFields() {
        List<InputField> inputFields = new ArrayList<InputField>();
        for (Component c : this.getComponentsForLifecycle()) {
            if (c instanceof InputField) {
                inputFields.add((InputField) c);
            } else if (c instanceof ContainerBase) {
                inputFields.addAll(((ContainerBase) c).getAllInputFields());
            } else if (c instanceof FieldGroup) {
                ContainerBase cb = ((FieldGroup) c).getGroup();
                inputFields.addAll(cb.getAllInputFields());
            }
        }
        return inputFields;
    }

    /**
     * This property is true if the container is used to display a group of fields that is visually a single
     * field.
     *
     * @return the fieldContainer
     */
    public boolean isFieldContainer() {
        return this.fieldContainer;
    }

    /**
     * @param fieldContainer the fieldContainer to set
     */
    public void setFieldContainer(boolean fieldContainer) {
        this.fieldContainer = fieldContainer;
    }

}
