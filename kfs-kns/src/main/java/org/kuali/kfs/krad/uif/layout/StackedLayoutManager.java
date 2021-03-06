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
package org.kuali.kfs.krad.uif.layout;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.DataBinding;
import org.kuali.kfs.krad.uif.component.KeepExpression;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.container.Container;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.field.ActionField;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.field.FieldGroup;
import org.kuali.kfs.krad.uif.util.ComponentUtils;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Layout manager that works with <code>CollectionGroup</code> containers and
 * renders the collection lines in a vertical row
 * <p>
 * <p>
 * For each line of the collection, a <code>Group</code> instance is created.
 * The group header contains a label for the line (summary information), the
 * group fields are the collection line fields, and the group footer contains
 * the line actions. All the groups are rendered using the
 * <code>BoxLayoutManager</code> with vertical orientation.
 * </p>
 * <p>
 * <p>
 * Modify the lineGroupPrototype to change header/footer styles or any other
 * customization for the line groups
 * </p>
 */
public class StackedLayoutManager extends LayoutManagerBase implements CollectionLayoutManager {
    private static final long serialVersionUID = 4602368505430238846L;

    @KeepExpression
    private String summaryTitle;
    private List<String> summaryFields;

    private Group addLineGroup;
    private Group lineGroupPrototype;
    private FieldGroup subCollectionFieldGroupPrototype;
    private Field selectFieldPrototype;
    private Group wrapperGroup;

    private List<Group> stackedGroups;

    public StackedLayoutManager() {
        super();

        summaryFields = new ArrayList<String>();
        stackedGroups = new ArrayList<Group>();
    }

    /**
     * The following actions are performed:
     * <p>
     * <ul>
     * <li>Initializes the prototypes</li>
     * </ul>
     *
     * @see BoxLayoutManager#performInitialization(View,
     * java.lang.Object, Container)
     */
    @Override
    public void performInitialization(View view, Object model, Container container) {
        super.performInitialization(view, model, container);

        stackedGroups = new ArrayList<Group>();

        if (addLineGroup != null) {
            view.getViewHelperService().performComponentInitialization(view, model, addLineGroup);
        }
        view.getViewHelperService().performComponentInitialization(view, model, lineGroupPrototype);
        view.getViewHelperService().performComponentInitialization(view, model, subCollectionFieldGroupPrototype);
        view.getViewHelperService().performComponentInitialization(view, model, selectFieldPrototype);
    }

    /**
     * The following actions are performed:
     * <p>
     * <ul>
     * <li>If wrapper group is specified, places the stacked groups into the wrapper</li>
     * </ul>
     *
     * @see BoxLayoutManager#performApplyModel(View,
     * java.lang.Object, Container)
     */
    @Override
    public void performApplyModel(View view, Object model, Container container) {
        super.performApplyModel(view, model, container);

        if (wrapperGroup != null) {
            wrapperGroup.setItems(stackedGroups);
        }
    }

    /**
     * Builds a <code>Group</code> instance for a collection line. The group is
     * built by first creating a copy of the configured prototype. Then the
     * header for the group is created using the configured summary fields on
     * the <code>CollectionGroup</code>. The line fields passed in are set as
     * the items for the group, and finally the actions are placed into the
     * group footer
     *
     * @see CollectionLayoutManager#buildLine(View,
     * java.lang.Object, CollectionGroup,
     * java.util.List, java.util.List, java.lang.String, java.util.List,
     * java.lang.String, java.lang.Object, int)
     */
    public void buildLine(View view, Object model, CollectionGroup collectionGroup, List<Field> lineFields,
                          List<FieldGroup> subCollectionFields, String bindingPath, List<ActionField> actions, String idSuffix,
                          Object currentLine, int lineIndex) {
        boolean isAddLine = lineIndex == -1;

        // construct new group
        Group lineGroup = null;
        if (isAddLine) {
            stackedGroups = new ArrayList<Group>();

            if (addLineGroup == null) {
                lineGroup = ComponentUtils.copy(lineGroupPrototype, idSuffix);
            } else {
                lineGroup = ComponentUtils.copy(getAddLineGroup(), idSuffix);
            }
        } else {
            lineGroup = ComponentUtils.copy(lineGroupPrototype, idSuffix);
        }

        ComponentUtils.updateContextForLine(lineGroup, currentLine, lineIndex);

        // build header text for group
        String headerText = "";
        if (isAddLine) {
            headerText = collectionGroup.getAddLineLabel();
        } else {
            // get the collection for this group from the model
            List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(model,
                ((DataBinding) collectionGroup).getBindingInfo().getBindingPath());

            headerText = buildLineHeaderText(modelCollection.get(lineIndex), lineGroup);
        }

        // don't set header if text is blank (could already be set by other means)
        if (StringUtils.isNotBlank(headerText) && lineGroup.getHeader() != null) {
            lineGroup.getHeader().setHeaderText(headerText);
        }

        // stack all fields (including sub-collections) for the group
        List<Field> groupFields = new ArrayList<Field>();
        groupFields.addAll(lineFields);
        groupFields.addAll(subCollectionFields);

        lineGroup.setItems(groupFields);

        // set line actions on group footer
        if (collectionGroup.isRenderLineActions() && !collectionGroup.isReadOnly() && (lineGroup.getFooter() != null)) {
            lineGroup.getFooter().setItems(actions);
        }

        stackedGroups.add(lineGroup);
    }

    /**
     * Builds the header text for the collection line
     * <p>
     * <p>
     * Header text is built up by first the collection label, either specified
     * on the collection definition or retrieved from the dictionary. Then for
     * each summary field defined, the value from the model is retrieved and
     * added to the header.
     * </p>
     * <p>
     * <p>
     * Note the {@link #getSummaryTitle()} field may have expressions defined, in which cause it will be copied to the
     * property expressions map to set the title for the line group (which will have the item context variable set)
     * </p>
     *
     * @param line      - Collection line containing data
     * @param lineGroup - Group instance for rendering the line and whose title should be built
     * @return String header text for line
     */
    protected String buildLineHeaderText(Object line, Group lineGroup) {
        // check for expression on summary title
        if (KRADServiceLocatorWeb.getExpressionEvaluatorService().containsElPlaceholder(summaryTitle)) {
            lineGroup.getPropertyExpressions().put("title", summaryTitle);
            return null;
        }

        // build up line summary from declared field values and fixed title
        String summaryFieldString = "";
        for (String summaryField : summaryFields) {
            Object summaryFieldValue = ObjectPropertyUtils.getPropertyValue(line, summaryField);
            if (StringUtils.isNotBlank(summaryFieldString)) {
                summaryFieldString += " - ";
            }

            if (summaryFieldValue != null) {
                summaryFieldString += summaryFieldValue;
            } else {
                summaryFieldString += "Null";
            }
        }

        String headerText = summaryTitle;
        if (StringUtils.isNotBlank(summaryFieldString)) {
            headerText += " ( " + summaryFieldString + " )";
        }

        return headerText;
    }

    /**
     * @see org.kuali.rice.krad.uif.layout.ContainerAware#getSupportedContainer()
     */
    @Override
    public Class<? extends Container> getSupportedContainer() {
        return CollectionGroup.class;
    }

    /**
     * @see LayoutManagerBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        if (wrapperGroup != null) {
            components.add(wrapperGroup);
        } else {
            components.addAll(stackedGroups);
        }

        return components;
    }

    /**
     * @see LayoutManager#getComponentPrototypes()
     */
    @Override
    public List<Component> getComponentPrototypes() {
        List<Component> components = super.getComponentPrototypes();

        components.add(addLineGroup);
        components.add(lineGroupPrototype);
        components.add(subCollectionFieldGroupPrototype);
        components.add(selectFieldPrototype);

        return components;
    }

    /**
     * Text to appears in the header for each collection lines Group. Used in
     * conjunction with {@link #getSummaryFields()} to build up the final header
     * text
     *
     * @return String summary title text
     */
    public String getSummaryTitle() {
        return this.summaryTitle;
    }

    /**
     * Setter for the summary title text
     *
     * @param summaryTitle
     */
    public void setSummaryTitle(String summaryTitle) {
        this.summaryTitle = summaryTitle;
    }

    /**
     * List of attribute names from the collection line class that should be
     * used to build the line summary. To build the summary the value for each
     * attribute is retrieved from the line instance. All the values are then
     * placed together with a separator.
     *
     * @return List<String> summary field names
     * @see #buildLineHeaderText(java.lang.Object)
     */
    public List<String> getSummaryFields() {
        return this.summaryFields;
    }

    /**
     * Setter for the summary field name list
     *
     * @param summaryFields
     */
    public void setSummaryFields(List<String> summaryFields) {
        this.summaryFields = summaryFields;
    }

    /**
     * Group instance that will be used for the add line
     * <p>
     * <p>
     * Add line fields and actions configured on the
     * <code>CollectionGroup</code> will be set onto the add line group (if add
     * line is enabled). If the add line group is not configured, a new instance
     * of the line group prototype will be used for the add line.
     * </p>
     *
     * @return Group add line group instance
     * @see #getAddLineGroup()
     */
    public Group getAddLineGroup() {
        return this.addLineGroup;
    }

    /**
     * Setter for the add line group
     *
     * @param addLineGroup
     */
    public void setAddLineGroup(Group addLineGroup) {
        this.addLineGroup = addLineGroup;
    }

    /**
     * Group instance that is used as a prototype for creating the collection
     * line groups. For each line a copy of the prototype is made and then
     * adjusted as necessary
     *
     * @return Group instance to use as prototype
     */
    public Group getLineGroupPrototype() {
        return this.lineGroupPrototype;
    }

    /**
     * Setter for the line group prototype
     *
     * @param lineGroupPrototype
     */
    public void setLineGroupPrototype(Group lineGroupPrototype) {
        this.lineGroupPrototype = lineGroupPrototype;
    }

    /**
     * @see CollectionLayoutManager#getSubCollectionFieldGroupPrototype()
     */
    public FieldGroup getSubCollectionFieldGroupPrototype() {
        return this.subCollectionFieldGroupPrototype;
    }

    /**
     * Setter for the sub-collection field group prototype
     *
     * @param subCollectionFieldGroupPrototype
     */
    public void setSubCollectionFieldGroupPrototype(FieldGroup subCollectionFieldGroupPrototype) {
        this.subCollectionFieldGroupPrototype = subCollectionFieldGroupPrototype;
    }

    /**
     * Field instance that serves as a prototype for creating the select field on each line when
     * {@link CollectionGroup#isRenderSelectField()} is true
     * <p>
     * <p>
     * This prototype can be used to set the control used for the select field (generally will be a checkbox control)
     * in addition to styling and other setting. The binding path will be formed with using the
     * {@link CollectionGroup#getSelectPropertyName()} or if not set the framework
     * will use {@link UifFormBase#getSelectedCollectionLines()}
     * </p>
     *
     * @return Field select field prototype instance
     */
    public Field getSelectFieldPrototype() {
        return selectFieldPrototype;
    }

    /**
     * Setter for the prototype instance for select fields
     *
     * @param selectFieldPrototype
     */
    public void setSelectFieldPrototype(Field selectFieldPrototype) {
        this.selectFieldPrototype = selectFieldPrototype;
    }

    /**
     * Group that will 'wrap' the generated collection lines so that they have a different layout from the general
     * stacked layout
     * <p>
     * <p>
     * By default (when the wrapper group is null), each collection line will become a group and the groups are
     * rendered one after another. If the wrapper group is configured, the generated groups will be inserted as the
     * items for the wrapper group, and the layout manager configured for the wrapper group will determine how they
     * are rendered. For example, the layout manager could be a grid layout configured for three columns, which would
     * layout the first three lines horizontally then break to a new row.
     * </p>
     *
     * @return Group instance whose items list should be populated with the generated groups, or null to use the
     * default layout
     */
    public Group getWrapperGroup() {
        return wrapperGroup;
    }

    /**
     * Setter for the wrapper group that will receive the generated line groups
     *
     * @param wrapperGroup
     */
    public void setWrapperGroup(Group wrapperGroup) {
        this.wrapperGroup = wrapperGroup;
    }

    /**
     * Final <code>List</code> of Groups to render for the collection
     *
     * @return List<Group> collection groups
     */
    public List<Group> getStackedGroups() {
        return this.stackedGroups;
    }

    /**
     * Setter for the collection groups
     *
     * @param stackedGroups
     */
    public void setStackedGroups(List<Group> stackedGroups) {
        this.stackedGroups = stackedGroups;
    }

}
