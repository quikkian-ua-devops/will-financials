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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.UifPropertyPaths;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.field.DataField;
import org.kuali.kfs.krad.uif.field.HeaderField;
import org.kuali.kfs.krad.uif.util.ComponentUtils;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates <code>Field</code> instances to produce a comparison view among
 * objects of the same type
 * <p>
 * <p>
 * Modifier is initialized with a List of <code>ComparableInfo</code> instances.
 * For each comparable info, a copy of the configured group field is made and
 * adjusted to the binding object path for the comparable. The comparison fields
 * are ordered based on the configured order property of the comparable. In
 * addition, a <code>HeaderField<code> can be generated to label each group
 * of comparison fields.
 * </p>
 */
public class CompareFieldCreateModifier extends ComponentModifierBase {
    private static final Logger LOG = Logger.getLogger(CompareFieldCreateModifier.class);

    private static final long serialVersionUID = -6285531580512330188L;

    private int defaultOrderSequence;
    private boolean generateCompareHeaders;

    private HeaderField headerFieldPrototype;
    private List<ComparableInfo> comparables;

    public CompareFieldCreateModifier() {
        defaultOrderSequence = 1;
        generateCompareHeaders = true;

        comparables = new ArrayList<ComparableInfo>();
    }

    /**
     * Calls <code>ViewHelperService</code> to initialize the header field prototype
     *
     * @see ComponentModifier#performInitialization(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performInitialization(View view, Object model, Component component) {
        super.performInitialization(view, model, component);

        if (headerFieldPrototype != null) {
            view.getViewHelperService().performComponentInitialization(view, model, headerFieldPrototype);
        }
    }

    /**
     * Generates the comparison fields
     * <p>
     * <p>
     * First the configured List of <code>ComparableInfo</code> instances are
     * sorted based on their order property. Then if generateCompareHeaders is
     * set to true, a <code>HeaderField</code> is created for each comparable
     * using the headerFieldPrototype and the headerText given by the
     * comparable. Finally for each field configured on the <code>Group</code>,
     * a corresponding comparison field is generated for each comparable and
     * adjusted to the binding object path given by the comparable in addition
     * to suffixing the id and setting the readOnly property
     * </p>
     *
     * @see ComponentModifier#performModification(View,
     * java.lang.Object, Component)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void performModification(View view, Object model, Component component) {
        if ((component != null) && !(component instanceof Group)) {
            throw new IllegalArgumentException(
                "Compare field initializer only support Group components, found type: " + component.getClass());
        }

        if (component == null) {
            return;
        }

        // list to hold the generated compare items
        List<Component> comparisonItems = new ArrayList<Component>();

        // sort comparables by their order property
        List<ComparableInfo> groupComparables = (List<ComparableInfo>) ComponentUtils.sort(comparables,
            defaultOrderSequence);

        // evaluate expressions on comparables
        Map<String, Object> context = new HashMap<String, Object>();
        context.putAll(view.getContext());
        context.put(UifConstants.ContextVariableNames.COMPONENT, component);

        for (ComparableInfo comparable : groupComparables) {
            KRADServiceLocatorWeb.getExpressionEvaluatorService().evaluateObjectExpressions(comparable, model,
                context);
        }

        // generate compare header
        if (isGenerateCompareHeaders()) {
            for (ComparableInfo comparable : groupComparables) {
                HeaderField compareHeaderField = ComponentUtils.copy(headerFieldPrototype, comparable.getIdSuffix());
                compareHeaderField.setHeaderText(comparable.getHeaderText());

                comparisonItems.add(compareHeaderField);
            }
        }

        // find the comparable to use for comparing value changes (if
        // configured)
        boolean performValueChangeComparison = false;
        String compareValueObjectBindingPath = null;
        for (ComparableInfo comparable : groupComparables) {
            if (comparable.isCompareToForValueChange()) {
                performValueChangeComparison = true;
                compareValueObjectBindingPath = comparable.getBindingObjectPath();
            }
        }

        // generate the compare items from the configured group
        Group group = (Group) component;
        for (Component item : group.getItems()) {
            int defaultSuffix = 0;
            for (ComparableInfo comparable : groupComparables) {
                String idSuffix = comparable.getIdSuffix();
                if (StringUtils.isBlank(idSuffix)) {
                    idSuffix = UifConstants.IdSuffixes.COMPARE + defaultSuffix;
                }

                Component compareItem = ComponentUtils.copy(item, idSuffix);

                ComponentUtils.setComponentPropertyDeep(compareItem, UifPropertyPaths.BIND_OBJECT_PATH,
                    comparable.getBindingObjectPath());
                if (comparable.isReadOnly()) {
                    compareItem.setReadOnly(true);
                    if (compareItem.getPropertyExpressions().containsKey("readOnly")) {
                        compareItem.getPropertyExpressions().remove("readOnly");
                    }
                }

                // do value comparison
                if (performValueChangeComparison && comparable.isHighlightValueChange() && !comparable
                    .isCompareToForValueChange()) {
                    performValueComparison(group, compareItem, model, compareValueObjectBindingPath);
                }

                comparisonItems.add(compareItem);
                defaultSuffix++;
            }
        }

        // update the group's list of components
        group.setItems(comparisonItems);
    }

    /**
     * For each attribute field in the compare item, retrieves the field value and compares against the value for the
     * main comparable. If the value is different, adds script to the field on ready event to add the change icon to
     * the field and the containing group header
     *
     * @param group                         - group that contains the item and whose header will be highlighted for changes
     * @param compareItem                   - the compare item being generated and to pull attribute fields from
     * @param model                         - object containing the data
     * @param compareValueObjectBindingPath - object path for the comparison item
     */
    protected void performValueComparison(Group group, Component compareItem, Object model,
                                          String compareValueObjectBindingPath) {
        // get any attribute fields for the item so we can compare the values
        List<DataField> itemFields = ComponentUtils.getComponentsOfTypeDeep(compareItem, DataField.class);
        for (DataField field : itemFields) {
            String fieldBindingPath = field.getBindingInfo().getBindingPath();
            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, fieldBindingPath);

            String compareBindingPath = StringUtils.replaceOnce(fieldBindingPath,
                field.getBindingInfo().getBindingObjectPath(), compareValueObjectBindingPath);
            Object compareValue = ObjectPropertyUtils.getPropertyValue(model, compareBindingPath);

            boolean valueChanged = false;
            if (!((fieldValue == null) && (compareValue == null))) {
                // if one is null then value changed
                if ((fieldValue == null) || (compareValue == null)) {
                    valueChanged = true;
                } else {
                    // both not null, compare values
                    valueChanged = !fieldValue.equals(compareValue);
                }
            }

            // add script to show change icon
            if (valueChanged) {
                String onReadyScript = "showChangeIcon('" + field.getId() + "');";

                // add icon to group header
                Component headerField = group.getHeader();
                onReadyScript += "showChangeIconOnHeader('" + headerField.getId() + "');";

                field.setOnDocumentReadyScript(onReadyScript);
            }

            // TODO: add script for value changed?
        }
    }

    /**
     * Generates an id suffix for the comparable item
     * <p>
     * <p>
     * If the idSuffix to use if configured on the <code>ComparableInfo</code>
     * it will be used, else the given integer index will be used with an
     * underscore
     * </p>
     *
     * @param comparable - comparable info to check for id suffix
     * @param index      - sequence integer
     * @return String id suffix
     * @see ComparableInfo.getIdSuffix()
     */
    protected String getIdSuffix(ComparableInfo comparable, int index) {
        String idSuffix = comparable.getIdSuffix();
        if (StringUtils.isBlank(idSuffix)) {
            idSuffix = "_" + index;
        }

        return idSuffix;
    }

    /**
     * @see ComponentModifier#getSupportedComponents()
     */
    @Override
    public Set<Class<? extends Component>> getSupportedComponents() {
        Set<Class<? extends Component>> components = new HashSet<Class<? extends Component>>();
        components.add(Group.class);

        return components;
    }

    /**
     * @see ComponentModifierBase#getComponentPrototypes()
     */
    public List<Component> getComponentPrototypes() {
        List<Component> components = new ArrayList<Component>();

        components.add(headerFieldPrototype);

        return components;
    }

    /**
     * Indicates the starting integer sequence value to use for
     * <code>ComparableInfo</code> instances that do not have the order property
     * set
     *
     * @return int default sequence starting value
     */
    public int getDefaultOrderSequence() {
        return this.defaultOrderSequence;
    }

    /**
     * Setter for the default sequence starting value
     *
     * @param defaultOrderSequence
     */
    public void setDefaultOrderSequence(int defaultOrderSequence) {
        this.defaultOrderSequence = defaultOrderSequence;
    }

    /**
     * Indicates whether a <code>HeaderField</code> should be created for each
     * group of comparison fields
     * <p>
     * <p>
     * If set to true, for each group of comparison fields a header field will
     * be created using the headerFieldPrototype configured on the modifier with
     * the headerText property of the comparable
     * </p>
     *
     * @return boolean true if the headers should be created, false if no
     * headers should be created
     */
    public boolean isGenerateCompareHeaders() {
        return this.generateCompareHeaders;
    }

    /**
     * Setter for the generate comparison headers indicator
     *
     * @param generateCompareHeaders
     */
    public void setGenerateCompareHeaders(boolean generateCompareHeaders) {
        this.generateCompareHeaders = generateCompareHeaders;
    }

    /**
     * Prototype instance to use for creating the <code>HeaderField</code> for
     * each group of comparison fields (if generateCompareHeaders is true)
     *
     * @return HeaderField header field prototype
     */
    public HeaderField getHeaderFieldPrototype() {
        return this.headerFieldPrototype;
    }

    /**
     * Setter for the header field prototype
     *
     * @param headerFieldPrototype
     */
    public void setHeaderFieldPrototype(HeaderField headerFieldPrototype) {
        this.headerFieldPrototype = headerFieldPrototype;
    }

    /**
     * List of <code>ComparableInfo</code> instances the compare fields should
     * be generated for
     * <p>
     * <p>
     * For each comparable, a copy of the fields configured for the
     * <code>Group</code> will be created for the comparison view
     * </p>
     *
     * @return List<ComparableInfo> comparables to generate fields for
     */
    public List<ComparableInfo> getComparables() {
        return this.comparables;
    }

    /**
     * Setter for the list of comparable info instances
     *
     * @param comparables
     */
    public void setComparables(List<ComparableInfo> comparables) {
        this.comparables = comparables;
    }

}
