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
package org.kuali.kfs.krad.uif.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.inquiry.Inquirable;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.component.BindingInfo;
import org.kuali.kfs.krad.uif.component.ClientSideState;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentSecurity;
import org.kuali.kfs.krad.uif.component.DataBinding;
import org.kuali.kfs.krad.uif.component.PropertyReplacer;
import org.kuali.kfs.krad.uif.component.RequestParameter;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.container.CollectionGroupBuilder;
import org.kuali.kfs.krad.uif.container.Container;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.control.Control;
import org.kuali.kfs.krad.uif.field.ActionField;
import org.kuali.kfs.krad.uif.field.DataField;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.field.FieldGroup;
import org.kuali.kfs.krad.uif.field.InputField;
import org.kuali.kfs.krad.uif.field.RemoteFieldsHolder;
import org.kuali.kfs.krad.uif.layout.LayoutManager;
import org.kuali.kfs.krad.uif.modifier.ComponentModifier;
import org.kuali.kfs.krad.uif.service.ExpressionEvaluatorService;
import org.kuali.kfs.krad.uif.service.ViewDictionaryService;
import org.kuali.kfs.krad.uif.service.ViewHelperService;
import org.kuali.kfs.krad.uif.util.BooleanMap;
import org.kuali.kfs.krad.uif.util.CloneUtils;
import org.kuali.kfs.krad.uif.util.ComponentFactory;
import org.kuali.kfs.krad.uif.util.ComponentUtils;
import org.kuali.kfs.krad.uif.util.ExpressionUtils;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.util.ScriptUtils;
import org.kuali.kfs.krad.uif.util.ViewCleaner;
import org.kuali.kfs.krad.uif.util.ViewModelUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.view.ViewAuthorizer;
import org.kuali.kfs.krad.uif.view.ViewModel;
import org.kuali.kfs.krad.uif.view.ViewPresentationController;
import org.kuali.kfs.krad.uif.widget.Inquiry;
import org.kuali.kfs.krad.uif.widget.Widget;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.valuefinder.ValueFinder;
import org.kuali.kfs.krad.web.form.UifFormBase;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Default Implementation of <code>ViewHelperService</code>
 */
public class ViewHelperServiceImpl implements ViewHelperService, Serializable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ViewHelperServiceImpl.class);

    private transient DataDictionaryService dataDictionaryService;
    private transient ExpressionEvaluatorService expressionEvaluatorService;
    private transient ViewDictionaryService viewDictionaryService;

    /**
     * Uses reflection to find all fields defined on the <code>View</code> instance that have
     * the <code>RequestParameter</code> annotation (which indicates the field may be populated by the request).
     * <p>
     * <p>
     * For each field found, if there is a corresponding key/value pair in the request parameters,
     * the value is used to populate the field. In addition, any conditional properties of
     * <code>PropertyReplacers</code> configured for the field are cleared so that the request parameter
     * value does not get overridden by the dictionary conditional logic
     * </p>
     *
     * @see ViewHelperService#populateViewFromRequestParameters(View,
     * java.util.Map)
     */
    @Override
    public void populateViewFromRequestParameters(View view, Map<String, String> parameters) {
        // build Map of property replacers by property name so that we can remove them
        // if the property was set by a request parameter
        Map<String, Set<PropertyReplacer>> viewPropertyReplacers = new HashMap<String, Set<PropertyReplacer>>();
        for (PropertyReplacer replacer : view.getPropertyReplacers()) {
            Set<PropertyReplacer> propertyReplacers = new HashSet<PropertyReplacer>();
            if (viewPropertyReplacers.containsKey(replacer.getPropertyName())) {
                propertyReplacers = viewPropertyReplacers.get(replacer.getPropertyName());
            }
            propertyReplacers.add(replacer);

            viewPropertyReplacers.put(replacer.getPropertyName(), propertyReplacers);
        }

        Map<String, Annotation> annotatedFields = CloneUtils.getFieldsWithAnnotation(view.getClass(),
            RequestParameter.class);

        // for each request parameter allowed on the view, if the request contains a value use
        // to set on View, and clear and conditional expressions or property replacers for that field
        Map<String, String> viewRequestParameters = new HashMap<String, String>();
        for (String fieldToPopulate : annotatedFields.keySet()) {
            RequestParameter requestParameter = (RequestParameter) annotatedFields.get(fieldToPopulate);

            // use specified parameter name if given, else use field name to retrieve parameter value
            String requestParameterName = requestParameter.parameterName();
            if (StringUtils.isBlank(requestParameterName)) {
                requestParameterName = fieldToPopulate;
            }

            if (!parameters.containsKey(requestParameterName)) {
                continue;
            }

            String fieldValue = parameters.get(requestParameterName);
            if (StringUtils.isNotBlank(fieldValue)) {
                viewRequestParameters.put(requestParameterName, fieldValue);
                ObjectPropertyUtils.setPropertyValue(view, fieldToPopulate, fieldValue);

                // remove any conditional configuration so value is not
                // overridden later during the apply model phase
                if (view.getPropertyExpressions().containsKey(fieldToPopulate)) {
                    view.getPropertyExpressions().remove(fieldToPopulate);
                }

                if (viewPropertyReplacers.containsKey(fieldToPopulate)) {
                    Set<PropertyReplacer> propertyReplacers = viewPropertyReplacers.get(fieldToPopulate);
                    for (PropertyReplacer replacer : propertyReplacers) {
                        view.getPropertyReplacers().remove(replacer);
                    }
                }
            }
        }

        view.setViewRequestParameters(viewRequestParameters);
    }

    /**
     * @see ViewHelperService#performInitialization(View,
     * java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        view.assignComponentIds(view);
        performComponentInitialization(view, model, view);
    }

    /**
     * Performs the complete component lifecycle on the component passed in, in this order:
     * performComponentInitialization, performComponentApplyModel, and performComponentFinalize.
     *
     * @see {@link ViewHelperService#performComponentLifecycle(
     *View, java.lang.Object, Component,
     * java.lang.String)
     * @see {@link #performComponentInitialization(View, Object,
     * Component)}
     * @see {@link #performComponentApplyModel(View, Component, Object)}
     * @see {@link #performComponentFinalize(View, Component, Object, Component, Map)}
     */
    public void performComponentLifecycle(View view, Object model, Component component, String origId) {
        Component origComponent = view.getViewIndex().getComponentById(origId);

        // run through and assign any ids starting with the id for the refreshed component (this might be
        // necessary if we are getting a new component instance from the bean factory)
        Integer currentSequenceVal = view.getIdSequence();
        Integer startingSequenceVal = view.getViewIndex().getIdSequenceSnapshot().get(component.getId());
        view.setIdSequence(startingSequenceVal);

        view.assignComponentIds(component);

        // now set back from the ending view sequence so IDs for any dynamically created (newly) will not stomp
        // on existing components
        view.setIdSequence(currentSequenceVal);

        Component parent = (Component) origComponent.getContext().get(UifConstants.ContextVariableNames.PARENT);
        component.pushAllToContext(origComponent.getContext());

        // adjust IDs for suffixes that might have been added by a parent component during the full view lifecycle
        String suffix = StringUtils.replaceOnce(origComponent.getId(), origComponent.getFactoryId(), "");

        // remove attribute suffix since that gets added in lifecycle
        if (suffix.endsWith(UifConstants.IdSuffixes.ATTRIBUTE)) {
            suffix = StringUtils.removeEnd(suffix, UifConstants.IdSuffixes.ATTRIBUTE);
        }
        ComponentUtils.updateIdWithSuffix(component, suffix);

        // binding path should stay the same
        if (component instanceof DataBinding) {
            ((DataBinding) component).setBindingInfo(((DataBinding) origComponent).getBindingInfo());
            ((DataBinding) component).getBindingInfo().setBindingPath(
                ((DataBinding) origComponent).getBindingInfo().getBindingPath());
        }

        // copy properties that are set by parent components in the full view lifecycle
        if (component instanceof Field) {
            ((Field) component).setLabelFieldRendered(((Field) origComponent).isLabelFieldRendered());
        } else if (component instanceof CollectionGroup) {
            ((CollectionGroup) component).setSubCollectionSuffix(
                ((CollectionGroup) origComponent).getSubCollectionSuffix());
        }

        if (origComponent.isRefreshedByAction()) {
            component.setRefreshedByAction(true);
        }

        // reset data if needed
        if (component.isResetDataOnRefresh()) {
            // TODO: this should handle groups as well, going through nested data fields
            if (component instanceof DataField) {
                // TODO: should check default value

                // clear value
                ObjectPropertyUtils.initializeProperty(model,
                    ((DataField) component).getBindingInfo().getBindingPath());
            }
        }

        performComponentInitialization(view, model, component);
        view.getViewIndex().indexComponent(component);

        performComponentApplyModel(view, component, model);
        view.getViewIndex().indexComponent(component);

        // make sure id, binding, and label settings stay the same as initial
        if (component instanceof Group || component instanceof FieldGroup) {
            List<Component> nestedComponents = ComponentUtils.getAllNestedComponents(component);
            for (Component nestedComponent : nestedComponents) {
                Component origNestedComponent = null;
                if (nestedComponent instanceof DataField) {
                    origNestedComponent = view.getViewIndex().getComponentById(
                        nestedComponent.getId() + suffix + UifConstants.IdSuffixes.ATTRIBUTE);
                } else {
                    origNestedComponent = view.getViewIndex().getComponentById(nestedComponent.getId() + suffix);
                }

                if (origNestedComponent != null) {
                    // update binding
                    if (nestedComponent instanceof DataBinding) {
                        ((DataBinding) nestedComponent).setBindingInfo(
                            ((DataBinding) origNestedComponent).getBindingInfo());
                        ((DataBinding) nestedComponent).getBindingInfo().setBindingPath(
                            ((DataBinding) origNestedComponent).getBindingInfo().getBindingPath());
                    }

                    // update label rendered flag
                    if (nestedComponent instanceof Field) {
                        ((Field) nestedComponent).setLabelFieldRendered(
                            ((Field) origNestedComponent).isLabelFieldRendered());
                    }

                    if (origNestedComponent.isRefreshedByAction()) {
                        nestedComponent.setRefreshedByAction(true);
                    }

                    // update id
                    ComponentUtils.updateIdWithSuffix(nestedComponent, suffix);
                }
            }
        }

        // TODO: need to handle updating client state for component refresh
        Map<String, Object> clientState = new HashMap<String, Object>();
        performComponentFinalize(view, component, model, parent, clientState);

        // get client state for component and build update script for on load
        String clientStateScript = buildClientSideStateScript(view, clientState, true);
        String onLoadScript = component.getOnLoadScript();
        if (StringUtils.isNotBlank(onLoadScript)) {
            clientStateScript = onLoadScript + clientStateScript;
        }
        component.setOnLoadScript(clientStateScript);

        view.getViewIndex().indexComponent(component);
    }

    /**
     * Performs initialization of a component by these steps:
     * <p>
     * <ul>
     * <li>For <code>DataField</code> instances, set defaults from the data
     * dictionary.</li>
     * <li>Invoke the initialize method on the component. Here the component can
     * setup defaults and do other initialization that is specific to that
     * component.</li>
     * <li>Invoke any configured <code>ComponentModifier</code> instances for
     * the component.</li>
     * <li>Call the component to get the List of components that are nested
     * within and recursively call this method to initialize those components.</li>
     * <li>Call custom initialize hook for service overrides</li>
     * </ul>
     * <p>
     * <p>
     * Note the order various initialize points are called, this can sometimes
     * be an important factor to consider when initializing a component
     * </p>
     *
     * @throws RiceRuntimeException if the component id or factoryId is not specified
     * @see ViewHelperService#performComponentInitialization(View,
     * java.lang.Object, Component)
     */
    public void performComponentInitialization(View view, Object model, Component component) {
        if (component == null) {
            return;
        }

        if (StringUtils.isBlank(component.getId())) {
            throw new RiceRuntimeException("Id is not set, this should not happen unless a component is misconfigured");
        }

        // TODO: duplicate ID check

        LOG.debug("Initializing component: " + component.getId() + " with type: " + component.getClass());

        // add initial state to the view index for component refreshes
        if (!(component instanceof View)) {
            view.getViewIndex().addInitialComponentStateIfNeeded(component);
        }

        // invoke component to initialize itself after properties have been set
        component.performInitialization(view, model);

        // for attribute fields, set defaults from dictionary entry
        if (component instanceof DataField) {
            initializeDataFieldFromDataDictionary(view, (DataField) component);
        }

        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            // invoke hook point for adding components through code
            addCustomContainerComponents(view, model, (Container) component);

            // process any remote fields holder that might be in the containers items, collection items will get
            // processed as the lines are being built
            if (!(component instanceof CollectionGroup)) {
                processAnyRemoteFieldsHolder(view, model, (Container) component);
            }
        }

        // for collection groups set defaults from dictionary entry
        if (component instanceof CollectionGroup) {
            // TODO: initialize from dictionary
        }

        // invoke component modifiers setup to run in the initialize phase
        runComponentModifiers(view, component, null, UifConstants.ViewPhases.INITIALIZE);

        // initialize nested components
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            performComponentInitialization(view, model, nestedComponent);
        }

        // initialize nested components in property replacements
        for (PropertyReplacer replacer : component.getPropertyReplacers()) {
            for (Component replacerComponent : replacer.getNestedComponents()) {
                performComponentInitialization(view, model, replacerComponent);
            }
        }

        // invoke initialize service hook
        performCustomInitialization(view, component);
    }

    /**
     * Iterates through the containers configured items checking for <code>RemotableFieldsHolder</code>, if found
     * the holder is invoked to retrieved the remotable fields and translate to attribute fields. The translated list
     * is then inserted into the container item list at the position of the holder
     *
     * @param view      - view instance containing the container
     * @param model     - object instance containing the view data
     * @param container - container instance to check for any remotable fields holder
     */
    protected void processAnyRemoteFieldsHolder(View view, Object model, Container container) {
        List<Component> processedItems = new ArrayList<Component>();

        // check for holders and invoke to retrieve the remotable fields and translate
        // translated fields are placed into the container item list at the position of the holder
        for (Component item : container.getItems()) {
            if (item instanceof RemoteFieldsHolder) {
                List<InputField> translatedFields = ((RemoteFieldsHolder) item).fetchAndTranslateRemoteFields(view,
                    model, container);
                processedItems.addAll(translatedFields);
            } else {
                processedItems.add(item);
            }
        }

        // updated container items
        container.setItems(processedItems);
    }

    /**
     * Sets properties of the <code>InputField</code> (if blank) to the
     * corresponding attribute entry in the data dictionary
     *
     * @param view  - view instance containing the field
     * @param field - data field instance to initialize
     */
    protected void initializeDataFieldFromDataDictionary(View view, DataField field) {
        AttributeDefinition attributeDefinition = null;

        String dictionaryAttributeName = field.getDictionaryAttributeName();
        String dictionaryObjectEntry = field.getDictionaryObjectEntry();

        // if entry given but not attribute name, use field name as attribute
        // name
        if (StringUtils.isNotBlank(dictionaryObjectEntry) && StringUtils.isBlank(dictionaryAttributeName)) {
            dictionaryAttributeName = field.getPropertyName();
        }

        // if dictionary entry and attribute set, attempt to find definition
        if (StringUtils.isNotBlank(dictionaryAttributeName) && StringUtils.isNotBlank(dictionaryObjectEntry)) {
            attributeDefinition = getDataDictionaryService().getAttributeDefinition(dictionaryObjectEntry,
                dictionaryAttributeName);
        }

        // if definition not found, recurse through path
        if (attributeDefinition == null) {
            String propertyPath = field.getBindingInfo().getBindingPath();
            if (StringUtils.isNotBlank(field.getBindingInfo().getCollectionPath())) {
                propertyPath = field.getBindingInfo().getCollectionPath();
                if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
                    propertyPath += "." + field.getBindingInfo().getBindByNamePrefix();
                }
                propertyPath += "." + field.getBindingInfo().getBindingName();
            }

            attributeDefinition = findNestedDictionaryAttribute(view, field, null, propertyPath);
        }

        // if a definition was found, initialize field from definition
        if (attributeDefinition != null) {
            field.copyFromAttributeDefinition(view, attributeDefinition);
        }

        // if control still null, assign default
        if (field instanceof InputField) {
            InputField inputField = (InputField) field;
            if (inputField.getControl() == null) {
                Control control = ComponentFactory.getTextControl();
                control.setId(view.getNextId());
                control.setFactoryId(control.getId());

                inputField.setControl(control);
            }
        }
    }

    /**
     * Recursively drills down the property path (if nested) to find an
     * AttributeDefinition, the first attribute definition found will be
     * returned
     * <p>
     * <p>
     * e.g. suppose parentPath is 'document' and propertyPath is
     * 'account.subAccount.name', first the property type for document will be
     * retrieved using the view metadata and used as the dictionary entry, with
     * the propertyPath as the dictionary attribute, if an attribute definition
     * exists it will be returned. Else, the first part of the property path is
     * added to the parent, making the parentPath 'document.account' and the
     * propertyPath 'subAccount.name', the method is then called again to
     * perform the process with those parameters. The recursion continues until
     * an attribute field is found, or the propertyPath is no longer nested
     * </p>
     *
     * @param view         - view instance containing the field
     * @param field        - field we are attempting to find a supporting attribute
     *                     definition for
     * @param parentPath   - parent path to use for getting the dictionary entry
     * @param propertyPath - path of the property relative to the parent, to use as
     *                     dictionary attribute and to drill down on
     * @return AttributeDefinition if found, or Null
     */
    protected AttributeDefinition findNestedDictionaryAttribute(View view, DataField field, String parentPath,
                                                                String propertyPath) {
        AttributeDefinition attributeDefinition = null;

        // attempt to find definition for parent and property
        String dictionaryAttributeName = propertyPath;
        String dictionaryObjectEntry = null;

        if (field.getBindingInfo().isBindToMap()) {
            parentPath = "";
            if (!field.getBindingInfo().isBindToForm() && StringUtils.isNotBlank(
                field.getBindingInfo().getBindingObjectPath())) {
                parentPath = field.getBindingInfo().getBindingObjectPath();
            }
            if (StringUtils.isNotBlank(field.getBindingInfo().getBindByNamePrefix())) {
                if (StringUtils.isNotBlank(parentPath)) {
                    parentPath += "." + field.getBindingInfo().getBindByNamePrefix();
                } else {
                    parentPath = field.getBindingInfo().getBindByNamePrefix();
                }
            }

            dictionaryAttributeName = field.getBindingInfo().getBindingName();
        }

        if (StringUtils.isNotBlank(parentPath)) {
            Class<?> dictionaryModelClass = ViewModelUtils.getPropertyTypeByClassAndView(view, parentPath);
            if (dictionaryModelClass != null) {
                dictionaryObjectEntry = dictionaryModelClass.getName();

                attributeDefinition = getDataDictionaryService().getAttributeDefinition(dictionaryObjectEntry,
                    dictionaryAttributeName);
            }
        }

        // if definition not found and property is still nested, recurse down
        // one level
        if ((attributeDefinition == null) && StringUtils.contains(propertyPath, ".")) {
            String nextParentPath = StringUtils.substringBefore(propertyPath, ".");
            if (StringUtils.isNotBlank(parentPath)) {
                nextParentPath = parentPath + "." + nextParentPath;
            }
            String nextPropertyPath = StringUtils.substringAfter(propertyPath, ".");

            return findNestedDictionaryAttribute(view, field, nextParentPath, nextPropertyPath);
        }

        // if a definition was found, update the fields dictionary properties
        if (attributeDefinition != null) {
            field.setDictionaryAttributeName(dictionaryAttributeName);
            field.setDictionaryObjectEntry(dictionaryObjectEntry);
        }

        return attributeDefinition;
    }

    /**
     * @see ViewHelperService#performApplyModel(View,
     * java.lang.Object)
     */
    @Override
    public void performApplyModel(View view, Object model) {
        // get action flag and edit modes from authorizer/presentation controller
        retrieveEditModesAndActionFlags(view, (UifFormBase) model);

        // set view context for conditional expressions
        setViewContext(view, model);

        performComponentApplyModel(view, view, model);
    }

    /**
     * Invokes the configured <code>PresentationController</code> and
     * </code>Authorizer</code> for the view to get the exported action flags
     * and edit modes that can be used in conditional logic
     *
     * @param view  - view instance that is being built and presentation/authorizer pulled for
     * @param model - Object that contains the model data
     */
    protected void retrieveEditModesAndActionFlags(View view, UifFormBase model) {
        ViewPresentationController presentationController = view.getPresentationController();
        ViewAuthorizer authorizer = view.getAuthorizer();

        Person user = GlobalVariables.getUserSession().getPerson();

        Set<String> actionFlags = presentationController.getActionFlags(view, model);
        actionFlags = authorizer.getActionFlags(view, model, user, actionFlags);

        view.setActionFlags(new BooleanMap(actionFlags));

        Set<String> editModes = presentationController.getEditModes(view, model);
        editModes = authorizer.getEditModes(view, model, user, editModes);

        view.setEditModes(new BooleanMap(editModes));
    }

    /**
     * Sets up the view context which will be available to other components
     * through their context for conditional logic evaluation
     *
     * @param view  - view instance to set context for
     * @param model - object containing the view data
     */
    protected void setViewContext(View view, Object model) {
        view.pushAllToContext(getPreModelContext(view));

        // evaluate view expressions for further context
        for (Entry<String, String> variableExpression : view.getExpressionVariables().entrySet()) {
            String variableName = variableExpression.getKey();
            Object value = getExpressionEvaluatorService().evaluateExpression(model, view.getContext(),
                variableExpression.getValue());
            view.pushObjectToContext(variableName, value);
        }
    }

    /**
     * Returns the general context that is available before the apply model
     * phase (during the initialize phase)
     *
     * @param view - view instance for context
     * @return Map<String, Object> context map
     */
    protected Map<String, Object> getPreModelContext(View view) {
        Map<String, Object> context = new HashMap<String, Object>();

        context.put(UifConstants.ContextVariableNames.VIEW, view);
        context.put(UifConstants.ContextVariableNames.VIEW_HELPER, this);

        Map<String, String> properties = KRADServiceLocator.getKualiConfigurationService().getAllProperties();
        context.put(UifConstants.ContextVariableNames.CONFIG_PROPERTIES, properties);
        context.put(UifConstants.ContextVariableNames.CONSTANTS, KRADConstants.class);
        context.put(UifConstants.ContextVariableNames.UIF_CONSTANTS, UifConstants.class);

        return context;
    }

    /**
     * Applies the model data to a component of the View instance
     * <p>
     * <p>
     * The component is invoked to to apply the model data. Here the component
     * can generate any additional fields needed or alter the configured fields.
     * After the component is invoked a hook for custom helper service
     * processing is invoked. Finally the method is recursively called for all
     * the component children
     * </p>
     *
     * @param view      - view instance the component belongs to
     * @param component - the component instance the model should be applied to
     * @param model     - top level object containing the data
     */
    protected void performComponentApplyModel(View view, Component component, Object model) {
        if (component == null) {
            return;
        }

        // evaluate expressions on component properties
        component.pushAllToContext(getCommonContext(view, component));
        ExpressionUtils.adjustPropertyExpressions(view, component);
        getExpressionEvaluatorService().evaluateObjectExpressions(component, model, component.getContext());

        // evaluate expressions on component security
        ComponentSecurity componentSecurity = component.getComponentSecurity();
        ExpressionUtils.adjustPropertyExpressions(view, componentSecurity);
        getExpressionEvaluatorService().evaluateObjectExpressions(componentSecurity, model, component.getContext());

        // evaluate expressions on the binding info object
        if (component instanceof DataBinding) {
            BindingInfo bindingInfo = ((DataBinding) component).getBindingInfo();
            ExpressionUtils.adjustPropertyExpressions(view, bindingInfo);
            getExpressionEvaluatorService().evaluateObjectExpressions(bindingInfo, model, component.getContext());
        }

        // evaluate expressions on the layout manager
        if (component instanceof Container) {
            LayoutManager layoutManager = ((Container) component).getLayoutManager();

            if (layoutManager != null) {
                layoutManager.getContext().putAll(getCommonContext(view, component));
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, component);
                layoutManager.pushObjectToContext(UifConstants.ContextVariableNames.MANAGER, layoutManager);

                ExpressionUtils.adjustPropertyExpressions(view, layoutManager);
                getExpressionEvaluatorService().evaluateObjectExpressions(layoutManager, model,
                    layoutManager.getContext());
            }
        }

        // sync the component with previous client side state
        syncClientSideStateForComponent(component, ((ViewModel) model).getClientStateForSyncing());

        // invoke authorizer and presentation controller to set component state
        applyAuthorizationAndPresentationLogic(view, component, (ViewModel) model);

        // invoke component to perform its conditional logic
        Component parent = (Component) component.getContext().get(UifConstants.ContextVariableNames.PARENT);
        component.performApplyModel(view, model, parent);

        // invoke service override hook
        performCustomApplyModel(view, component, model);

        // invoke component modifiers configured to run in the apply model phase
        runComponentModifiers(view, component, model, UifConstants.ViewPhases.APPLY_MODEL);

        // get children and recursively perform conditional logic
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            if (nestedComponent != null) {
                nestedComponent.pushObjectToContext(UifConstants.ContextVariableNames.PARENT, component);
            }

            performComponentApplyModel(view, nestedComponent, model);
        }
    }

    /**
     * Invokes the view's configured {@link ViewAuthorizer} and {@link ViewPresentationController} to set state of
     * the component
     * <p>
     * <p>
     * The following authorization is done here:
     * Fields: edit, view, required, mask, and partial mask
     * Groups: edit and view
     * Actions: take action
     * </p>
     * <p>
     * <p>
     * Note additional checks are also done for fields that are part of a collection group. This authorization is
     * found in {@link CollectionGroupBuilder}
     * </p>
     *
     * @param view      - view instance the component belongs to and from which the authorizer and presentation controller
     *                  will be pulled
     * @param component - component instance to authorize
     * @param model     - model object containing the data for the view
     */
    protected void applyAuthorizationAndPresentationLogic(View view, Component component, ViewModel model) {
        ViewPresentationController presentationController = view.getPresentationController();
        ViewAuthorizer authorizer = view.getAuthorizer();

        Person user = GlobalVariables.getUserSession().getPerson();

        // if component not flagged for render no need to check auth and controller logic
        if (!component.isRender()) {
            return;
        }

        // check top level view edit authorization
        if (component instanceof View) {
            if (!view.isReadOnly()) {
                boolean canEditView = authorizer.canEditView(view, model, user);
                if (canEditView) {
                    canEditView = presentationController.canEditView(view, model);
                }
                view.setReadOnly(!canEditView);
            }
        }

        // perform group authorization and presentation logic
        else if (component instanceof Group) {
            Group group = (Group) component;

            // if group is not hidden, do authorization for viewing the group
            if (!group.isHidden()) {
                boolean canViewGroup = authorizer.canViewGroup(view, model, group, group.getId(), user);
                if (canViewGroup) {
                    canViewGroup = presentationController.canViewGroup(view, model, group, group.getId());
                }
                group.setHidden(!canViewGroup);
                group.setRender(canViewGroup);
            }

            // if group is editable, do authorization for editing the group
            if (!group.isReadOnly()) {
                boolean canEditGroup = authorizer.canEditGroup(view, model, group, group.getId(), user);
                if (canEditGroup) {
                    canEditGroup = presentationController.canEditGroup(view, model, group, group.getId());
                }
                group.setReadOnly(!canEditGroup);
            }
        }

        // perform field authorization and presentation logic
        else if (component instanceof Field) {
            Field field = (Field) component;

            String propertyName = null;
            if (field instanceof DataBinding) {
                propertyName = ((DataBinding) field).getPropertyName();
            }

            // if field is not hidden, do authorization for viewing the field
            if (!field.isHidden()) {
                boolean canViewField = authorizer.canViewField(view, model, field, propertyName, user);
                if (canViewField) {
                    canViewField = presentationController.canViewField(view, model, field, propertyName);
                }
                field.setHidden(!canViewField);
                field.setRender(canViewField);
            }

            // if field is not readOnly, check edit authorization
            if (!field.isReadOnly()) {
                // check field edit authorization
                boolean canEditField = authorizer.canEditField(view, model, field, propertyName, user);
                if (canEditField) {
                    canEditField = presentationController.canEditField(view, model, field, propertyName);
                }
                field.setReadOnly(!canEditField);
            }

            // if field is not already required, invoke presentation logic to determine if it should be
            if ((field.getRequired() == null) || !field.getRequired().booleanValue()) {
                boolean fieldIsRequired = presentationController.fieldIsRequired(view, model, field, propertyName);
            }

            if (field instanceof DataField) {
                DataField dataField = (DataField) field;

                // check mask authorization
                boolean canUnmaskValue = authorizer.canUnmaskField(view, model, dataField, dataField.getPropertyName(),
                    user);
                if (!canUnmaskValue) {
                    dataField.setApplyValueMask(true);
                    dataField.setMaskFormatter(dataField.getDataFieldSecurity().getAttributeSecurity().
                        getMaskFormatter());
                } else {
                    // check partial mask authorization
                    boolean canPartiallyUnmaskValue = authorizer.canPartialUnmaskField(view, model, dataField,
                        dataField.getPropertyName(), user);
                    if (!canPartiallyUnmaskValue) {
                        dataField.setApplyValueMask(true);
                        dataField.setMaskFormatter(
                            dataField.getDataFieldSecurity().getAttributeSecurity().getPartialMaskFormatter());
                    }
                }
            }

            // check authorization for actions
            if (field instanceof ActionField) {
                ActionField actionField = (ActionField) field;

                boolean canTakeAction = authorizer.canPerformAction(view, model, actionField,
                    actionField.getActionEvent(), actionField.getId(), user);
                if (canTakeAction) {
                    canTakeAction = presentationController.canPerformAction(view, model, actionField,
                        actionField.getActionEvent(), actionField.getId());
                }
                actionField.setRender(canTakeAction);
            }
        }

        // perform widget authorization and presentation logic
        else if (component instanceof Widget) {
            Widget widget = (Widget) component;

            // if widget is not hidden, do authorization for viewing the widget
            if (!widget.isHidden()) {
                boolean canViewWidget = authorizer.canViewWidget(view, model, widget, widget.getId(), user);
                if (canViewWidget) {
                    canViewWidget = presentationController.canViewWidget(view, model, widget, widget.getId());
                }
                widget.setHidden(!canViewWidget);
                widget.setRender(canViewWidget);
            }

            // if widget is not readOnly, check edit authorization
            if (!widget.isReadOnly()) {
                boolean canEditWidget = authorizer.canEditWidget(view, model, widget, widget.getId(), user);
                if (canEditWidget) {
                    canEditWidget = presentationController.canEditWidget(view, model, widget, widget.getId());
                }
                widget.setReadOnly(!canEditWidget);
            }
        }
    }

    /**
     * Runs any configured <code>ComponentModifiers</code> for the given
     * component that match the given run phase and who run condition evaluation
     * succeeds
     * <p>
     * <p>
     * If called during the initialize phase, the performInitialization method will be invoked on
     * the <code>ComponentModifier</code> before running
     * </p>
     *
     * @param view      - view instance for context
     * @param component - component instance whose modifiers should be run
     * @param model     - model object for context
     * @param runPhase  - current phase to match on
     */
    protected void runComponentModifiers(View view, Component component, Object model, String runPhase) {
        for (ComponentModifier modifier : component.getComponentModifiers()) {
            // if run phase is initialize, invoke initialize method on modifier first
            if (StringUtils.equals(runPhase, UifConstants.ViewPhases.INITIALIZE)) {
                modifier.performInitialization(view, model, component);
            }

            // check run phase matches
            if (StringUtils.equals(modifier.getRunPhase(), runPhase)) {
                // check condition (if set) evaluates to true
                boolean runModifier = true;
                if (StringUtils.isNotBlank(modifier.getRunCondition())) {
                    Map<String, Object> context = new HashMap<String, Object>();
                    context.put(UifConstants.ContextVariableNames.COMPONENT, component);
                    context.put(UifConstants.ContextVariableNames.VIEW, view);

                    String conditionEvaluation = getExpressionEvaluatorService().evaluateExpressionTemplate(model,
                        context, modifier.getRunCondition());
                    runModifier = Boolean.parseBoolean(conditionEvaluation);
                }

                if (runModifier) {
                    modifier.performModification(view, model, component);
                }
            }
        }
    }

    /**
     * Gets global objects for the context map and pushes them to the context
     * for the component
     *
     * @param view      - view instance for component
     * @param component - component instance to push context to
     */
    protected Map<String, Object> getCommonContext(View view, Component component) {
        Map<String, Object> context = new HashMap<String, Object>();

        context.putAll(view.getContext());
        context.put(UifConstants.ContextVariableNames.COMPONENT, component);

        return context;
    }

    /**
     * @see ViewHelperService#performFinalize(View,
     * java.lang.Object)
     */
    @Override
    public void performFinalize(View view, Object model) {
        Map<String, Object> clientState = new HashMap<String, Object>();
        performComponentFinalize(view, view, model, null, clientState);

        String clientStateScript = buildClientSideStateScript(view, clientState, false);
        String viewPreLoadScript = view.getPreLoadScript();
        if (StringUtils.isNotBlank(viewPreLoadScript)) {
            clientStateScript = viewPreLoadScript + clientStateScript;
        }
        view.setPreLoadScript(clientStateScript);

        // apply default values if they have not been applied yet
        if (!((ViewModel) model).isDefaultsApplied()) {
            applyDefaultValues(view, view, model);
            ((ViewModel) model).setDefaultsApplied(true);
        }
    }

    /**
     * Builds script that will initialize configuration parameters and component state on the client
     * <p>
     * <p>
     * Here client side state is initialized along with configuration variables that need exposed to script
     * </p>
     *
     * @param view            - view instance that is being built
     * @param clientSideState - map of key/value pairs that should be exposed as client side state
     * @param updateOnly      - boolean that indicates whether we are just updating a component (true), or the full view
     */
    protected String buildClientSideStateScript(View view, Map<String, Object> clientSideState, boolean updateOnly) {
        // merge any additional client side state added to the view during processing
        // state from view will override in all cases except when both values are maps, in which the maps
        // be combined for the new value
        for (Entry<String, Object> additionalState : view.getClientSideState().entrySet()) {
            if (!clientSideState.containsKey(additionalState.getKey())) {
                clientSideState.put(additionalState.getKey(), additionalState.getValue());
            } else {
                Object state = clientSideState.get(additionalState.getKey());
                Object mergeState = additionalState.getValue();
                if ((state instanceof Map) && (mergeState instanceof Map)) {
                    ((Map) state).putAll((Map) mergeState);
                } else {
                    clientSideState.put(additionalState.getKey(), additionalState.getValue());
                }
            }
        }

        // script for initializing client side state on load
        String clientStateScript = "";
        if (!clientSideState.isEmpty()) {
            if (updateOnly) {
                clientStateScript = "updateViewState({";
            } else {
                clientStateScript = "initializeViewState({";
            }

            for (Entry<String, Object> stateEntry : clientSideState.entrySet()) {
                clientStateScript += "'" + stateEntry.getKey() + "':";
                clientStateScript += ScriptUtils.translateValue(stateEntry.getValue());
                clientStateScript += ",";
            }
            clientStateScript = StringUtils.removeEnd(clientStateScript, ",");
            clientStateScript += "});";
        }

        // add necessary configuration parameters
        if (!updateOnly) {
            String kradImageLocation = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                "krad.externalizable.images.url");
            clientStateScript += "setConfigParam('"
                + UifConstants.ClientSideVariables.KRAD_IMAGE_LOCATION
                + "','"
                + kradImageLocation
                + "');";

            String kradURL = KRADServiceLocator.getKualiConfigurationService().getPropertyValueAsString("krad.url");
            clientStateScript += "setConfigParam('"
                + UifConstants.ClientSideVariables.KRAD_URL
                + "','"
                + kradURL
                + "');";
        }

        return clientStateScript;
    }

    /**
     * Update state of the given component and does final preparation for
     * rendering
     *
     * @param view            - view instance the component belongs to
     * @param component       - the component instance that should be updated
     * @param model           - top level object containing the data
     * @param parent          - Parent component for the component being finalized
     * @param clientSideState - map to add client state to
     */
    protected void performComponentFinalize(View view, Component component, Object model, Component parent,
                                            Map<String, Object> clientSideState) {
        if (component == null) {
            return;
        }

        // implement readonly request overrides
        ViewModel viewModel = (ViewModel) model;
        if ((component instanceof DataBinding) && view.isSupportsReadOnlyFieldsOverride() && !viewModel
            .getReadOnlyFieldsList().isEmpty()) {
            String propertyName = ((DataBinding) component).getPropertyName();
            if (viewModel.getReadOnlyFieldsList().contains(propertyName)) {
                component.setReadOnly(true);
            }
        }

        // invoke configured method finalizers
        invokeMethodFinalizer(view, component, model);

        // invoke component to update its state
        component.performFinalize(view, model, parent);

        // add client side state for annotated component properties
        addClientSideStateForComponent(component, clientSideState);

        // invoke service override hook
        performCustomFinalize(view, component, model, parent);

        // invoke component modifiers setup to run in the finalize phase
        runComponentModifiers(view, component, model, UifConstants.ViewPhases.FINALIZE);

        // get components children and recursively update state
        for (Component nestedComponent : component.getComponentsForLifecycle()) {
            performComponentFinalize(view, nestedComponent, model, component, clientSideState);
        }
    }

    /**
     * Reflects the class for the given component to find any fields that are annotated with
     * <code>ClientSideState</code> and adds the corresponding property name/value pair to the client side state
     * map
     * <p>
     * <p>
     * Note if the component is the <code>View</code, state is added directly to the client side state map, while
     * for other components a nested Map is created to hold the state, which is then placed into the client side
     * state map with the component id as the key
     * </p>
     *
     * @param component       - component instance to get client state for
     * @param clientSideState - map to add client side variable name/values to
     */
    protected void addClientSideStateForComponent(Component component, Map<String, Object> clientSideState) {
        Map<String, Annotation> annotatedFields = CloneUtils.getFieldsWithAnnotation(component.getClass(),
            ClientSideState.class);

        if (!annotatedFields.isEmpty()) {
            Map<String, Object> componentClientState = null;
            if (component instanceof View) {
                componentClientState = clientSideState;
            } else {
                if (clientSideState.containsKey(component.getId())) {
                    componentClientState = (Map<String, Object>) clientSideState.get(component.getId());
                } else {
                    componentClientState = new HashMap<String, Object>();
                    clientSideState.put(component.getId(), componentClientState);
                }
            }

            for (Entry<String, Annotation> annotatedField : annotatedFields.entrySet()) {
                ClientSideState clientSideStateAnnot = (ClientSideState) annotatedField.getValue();

                String variableName = clientSideStateAnnot.variableName();
                if (StringUtils.isBlank(variableName)) {
                    variableName = annotatedField.getKey();
                }

                Object value = ObjectPropertyUtils.getPropertyValue(component, annotatedField.getKey());
                componentClientState.put(variableName, value);
            }
        }
    }

    /**
     * Updates the properties of the given component instance with the value found from the corresponding map of
     * client state (if found)
     *
     * @param component       - component instance to update
     * @param clientSideState - map of state to sync with
     */
    protected void syncClientSideStateForComponent(Component component, Map<String, Object> clientSideState) {
        // find the map of state that was sent for component (if any)
        Map<String, Object> componentState = null;
        if (component instanceof View) {
            componentState = clientSideState;
        } else {
            if (clientSideState.containsKey(component.getId())) {
                componentState = (Map<String, Object>) clientSideState.get(component.getId());
            }
        }

        // if state was sent, match with fields on the component that are annotated to have client state
        if ((componentState != null) && (!componentState.isEmpty())) {
            Map<String, Annotation> annotatedFields = CloneUtils.getFieldsWithAnnotation(component.getClass(),
                ClientSideState.class);

            for (Entry<String, Annotation> annotatedField : annotatedFields.entrySet()) {
                ClientSideState clientSideStateAnnot = (ClientSideState) annotatedField.getValue();

                String variableName = clientSideStateAnnot.variableName();
                if (StringUtils.isBlank(variableName)) {
                    variableName = annotatedField.getKey();
                }

                if (componentState.containsKey(variableName)) {
                    Object value = componentState.get(variableName);
                    ObjectPropertyUtils.setPropertyValue(component, annotatedField.getKey(), value);
                }
            }
        }
    }

    /**
     * Invokes the finalize method for the component (if configured) and sets
     * the render output for the component to the returned method string (if
     * method is not a void type)
     *
     * @param view      - view instance that contains the component
     * @param component - component to run finalize method for
     * @param model     - top level object containing the data
     */
    protected void invokeMethodFinalizer(View view, Component component, Object model) {
        String finalizeMethodToCall = component.getFinalizeMethodToCall();
        MethodInvoker finalizeMethodInvoker = component.getFinalizeMethodInvoker();

        if (StringUtils.isBlank(finalizeMethodToCall) && (finalizeMethodInvoker == null)) {
            return;
        }

        if (finalizeMethodInvoker == null) {
            finalizeMethodInvoker = new MethodInvoker();
        }

        // if method not set on invoker, use renderingMethodToCall, note staticMethod could be set(don't know since
        // there is not a getter), if so it will override the target method in prepare
        if (StringUtils.isBlank(finalizeMethodInvoker.getTargetMethod())) {
            finalizeMethodInvoker.setTargetMethod(finalizeMethodToCall);
        }

        // if target class or object not set, use view helper service
        if ((finalizeMethodInvoker.getTargetClass() == null) && (finalizeMethodInvoker.getTargetObject() == null)) {
            finalizeMethodInvoker.setTargetObject(view.getViewHelperService());
        }

        // setup arguments for method
        List<Object> additionalArguments = component.getFinalizeMethodAdditionalArguments();
        if (additionalArguments == null) {
            additionalArguments = new ArrayList<Object>();
        }

        Object[] arguments = new Object[2 + additionalArguments.size()];
        arguments[0] = component;
        arguments[1] = model;

        int argumentIndex = 1;
        for (Object argument : additionalArguments) {
            argumentIndex++;
            arguments[argumentIndex] = argument;
        }
        finalizeMethodInvoker.setArguments(arguments);

        // invoke method and get render output
        try {
            LOG.debug("Invoking render method: "
                + finalizeMethodInvoker.getTargetMethod()
                + " for component: "
                + component.getId());
            finalizeMethodInvoker.prepare();

            Class<?> methodReturnType = finalizeMethodInvoker.getPreparedMethod().getReturnType();
            if (StringUtils.equals("void", methodReturnType.getName())) {
                finalizeMethodInvoker.invoke();
            } else {
                String renderOutput = (String) finalizeMethodInvoker.invoke();

                component.setSelfRendered(true);
                component.setRenderOutput(renderOutput);
            }
        } catch (Exception e) {
            LOG.error("Error invoking rendering method for component: " + component.getId(), e);
            throw new RuntimeException("Error invoking rendering method for component: " + component.getId(), e);
        }
    }

    /**
     * @see ViewHelperService#cleanViewAfterRender(View)
     */
    @Override
    public void cleanViewAfterRender(View view) {
        ViewCleaner.cleanView(view);
    }

    /**
     * @see ViewHelperService#processCollectionAddLine(View,
     * java.lang.Object, java.lang.String)
     */
    @Override
    public void processCollectionAddLine(View view, Object model, String collectionPath) {
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // now get the new line we need to add
        String addLinePath = collectionGroup.getAddLineBindingInfo().getBindingPath();
        Object addLine = ObjectPropertyUtils.getPropertyValue(model, addLinePath);
        if (addLine == null) {
            logAndThrowRuntime("Add line instance not found for path: " + addLinePath);
        }

        processBeforeAddLine(view, collectionGroup, model, addLine);

        // validate the line to make sure it is ok to add
        boolean isValidLine = performAddLineValidation(view, collectionGroup, model, addLine);
        if (isValidLine) {
            // TODO: should check to see if there is an add line method on the
            // collection parent and if so call that instead of just adding to
            // the collection (so that sequence can be set)
            addLine(collection, addLine);

            // make a new instance for the add line
            collectionGroup.initializeNewCollectionLine(view, model, collectionGroup, true);
        }

        processAfterAddLine(view, collectionGroup, model, addLine);
    }

    /**
     * Add addLine to collection while giving derived classes an opportunity to override
     * for things like sorting.
     *
     * @param collection - the Collection to add the given addLine to
     * @param addLine    - the line to add to the given collection
     */
    protected void addLine(Collection<Object> collection, Object addLine) {
        if (collection instanceof List) {
            ((List) collection).add(0, addLine);
        } else {
            collection.add(addLine);
        }
    }


    /**
     * Performs validation on the new collection line before it is added to the
     * corresponding collection
     *
     * @param view            - view instance that the action was taken on
     * @param collectionGroup - collection group component for the collection
     * @param addLine         - new line instance to validate
     * @param model           - object instance that contain's the views data
     * @return boolean true if the line is valid and it should be added to the
     * collection, false if it was not valid and should not be added to
     * the collection
     */
    protected boolean performAddLineValidation(View view, CollectionGroup collectionGroup, Object model,
                                               Object addLine) {
        boolean isValid = true;

        // TODO: this should invoke rules, subclasses like the document view
        // should create the document add line event

        return isValid;
    }

    /**
     * @see ViewHelperService#processCollectionDeleteLine(View,
     * java.lang.Object, java.lang.String, int)
     */
    public void processCollectionDeleteLine(View view, Object model, String collectionPath, int lineIndex) {
        // get the collection group from the view
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            logAndThrowRuntime("Unable to get collection group component for path: " + collectionPath);
        }

        // get the collection instance for adding the new line
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model, collectionPath);
        if (collection == null) {
            logAndThrowRuntime("Unable to get collection property from model for path: " + collectionPath);
        }

        // TODO: look into other ways of identifying a line so we can deal with
        // unordered collections
        if (collection instanceof List) {
            Object deleteLine = ((List<Object>) collection).get(lineIndex);

            // validate the delete action is allowed for this line
            boolean isValid = performDeleteLineValidation(view, collectionGroup, deleteLine);
            if (isValid) {
                ((List<Object>) collection).remove(lineIndex);
                processAfterDeleteLine(view, collectionGroup, model, lineIndex);
            }
        } else {
            logAndThrowRuntime("Only List collection implementations are supported for the delete by index method");
        }
    }

    /**
     * Performs validation on the collection line before it is removed from the
     * corresponding collection
     *
     * @param view            - view instance that the action was taken on
     * @param collectionGroup - collection group component for the collection
     * @param deleteLine      - line that will be removed
     * @return boolean true if the action is allowed and the line should be
     * removed, false if the line should not be removed
     */
    protected boolean performDeleteLineValidation(View view, CollectionGroup collectionGroup, Object deleteLine) {
        boolean isValid = true;

        // TODO: this should invoke rules, sublclasses like the document view
        // should create the document delete line event

        return isValid;
    }

    /**
     * @see ViewHelperServiceImpl#processMultipleValueLookupResults
     */
    public void processMultipleValueLookupResults(View view, Object model, String collectionPath,
                                                  String lookupResultValues) {
        // if no line values returned, no population is needed
        if (StringUtils.isBlank(lookupResultValues)) {
            return;
        }

        // retrieve the collection group so we can get the collection class and collection lookup
        CollectionGroup collectionGroup = view.getViewIndex().getCollectionGroupByPath(collectionPath);
        if (collectionGroup == null) {
            throw new RuntimeException("Unable to find collection group for path: " + collectionPath);
        }

        Class<?> collectionObjectClass = collectionGroup.getCollectionObjectClass();
        Collection<Object> collection = ObjectPropertyUtils.getPropertyValue(model,
            collectionGroup.getBindingInfo().getBindingPath());
        if (collection == null) {
            Class<?> collectionClass = ObjectPropertyUtils.getPropertyType(model,
                collectionGroup.getBindingInfo().getBindingPath());
            collection = (Collection<Object>) ObjectUtils.newInstance(collectionClass);
            ObjectPropertyUtils.setPropertyValue(model, collectionGroup.getBindingInfo().getBindingPath(), collection);
        }

        Map<String, String> fieldConversions = collectionGroup.getCollectionLookup().getFieldConversions();
        List<String> toFieldNamesColl = new ArrayList(fieldConversions.values());
        Collections.sort(toFieldNamesColl);
        String[] toFieldNames = new String[toFieldNamesColl.size()];
        toFieldNamesColl.toArray(toFieldNames);

        // first split to get the line value sets
        String[] lineValues = StringUtils.split(lookupResultValues, ",");

        // for each returned set create a new instance of collection class and populate with returned line values
        for (String lineValue : lineValues) {
            Object lineDataObject = null;

            // TODO: need to put this in data object service so logic can be reused
            ModuleService moduleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(
                collectionObjectClass);
            if (moduleService != null && moduleService.isExternalizable(collectionObjectClass)) {
                lineDataObject = moduleService.createNewObjectFromExternalizableClass(collectionObjectClass.asSubclass(
                    ExternalizableBusinessObject.class));
            } else {
                lineDataObject = ObjectUtils.newInstance(collectionObjectClass);
            }

            // apply default values to new line
            applyDefaultValuesForCollectionLine(view, model, collectionGroup, lineDataObject);

            String[] fieldValues = StringUtils.split(lineValue, ":");
            if (fieldValues.length != toFieldNames.length) {
                throw new RuntimeException(
                    "Value count passed back from multi-value lookup does not match field conversion count");
            }

            // set each field value on the line
            for (int i = 0; i < fieldValues.length; i++) {
                String fieldName = toFieldNames[i];
                ObjectPropertyUtils.setPropertyValue(lineDataObject, fieldName, fieldValues[i]);
            }

            // TODO: duplicate identifier check

            collection.add(lineDataObject);
        }
    }

    /**
     * Finds the <code>Inquirable</code> configured for the given data object
     * class and delegates to it for building the inquiry URL
     *
     * @see ViewHelperService#buildInquiryLink(java.lang.Object,
     * java.lang.String, Inquiry)
     */
    public void buildInquiryLink(Object dataObject, String propertyName, Inquiry inquiry) {
        Inquirable inquirable = getViewDictionaryService().getInquirable(dataObject.getClass(), inquiry.getViewName());
        if (inquirable != null) {
            inquirable.buildInquirableLink(dataObject, propertyName, inquiry);
        } else {
            // inquirable not found, no inquiry link can be set
            inquiry.setRender(false);
        }
    }

    /**
     * @see ViewHelperService#applyDefaultValuesForCollectionLine(View,
     * java.lang.Object, CollectionGroup,
     * java.lang.Object)
     */
    public void applyDefaultValuesForCollectionLine(View view, Object model, CollectionGroup collectionGroup,
                                                    Object line) {
        // retrieve all data fields for the collection line
        List<DataField> dataFields = ComponentUtils.getComponentsOfTypeDeep(collectionGroup.getAddLineFields(),
            DataField.class);
        for (DataField dataField : dataFields) {
            String bindingPath = "";
            if (StringUtils.isNotBlank(dataField.getBindingInfo().getBindByNamePrefix())) {
                bindingPath = dataField.getBindingInfo().getBindByNamePrefix() + ".";
            }
            bindingPath += dataField.getBindingInfo().getBindingName();

            populateDefaultValueForField(view, line, dataField, bindingPath);
        }
    }

    /**
     * Iterates through the view components picking up data fields and applying an default value configured
     *
     * @param view      - view instance we are applying default values for
     * @param component - component that should be checked for default values
     * @param model     - model object that values should be set on
     */
    protected void applyDefaultValues(View view, Component component, Object model) {
        if (component == null) {
            return;
        }

        // if component is a data field apply default value
        if (component instanceof DataField) {
            DataField dataField = ((DataField) component);

            // need to make sure binding is initialized since this could be on a page we have not initialized yet
            dataField.getBindingInfo().setDefaults(view, dataField.getPropertyName());

            populateDefaultValueForField(view, model, dataField, dataField.getBindingInfo().getBindingPath());
        }

        List<Component> nestedComponents = component.getComponentsForLifecycle();

        // if view, need to add all pages since only one will be on the lifecycle
        if (component instanceof View) {
            nestedComponents.addAll(((View) component).getItems());
        }

        for (Component nested : nestedComponents) {
            applyDefaultValues(view, nested, model);
        }
    }

    /**
     * Applies the default value configured for the given field (if any) to the
     * line given object property that is determined by the given binding path
     * <p>
     * <p>
     * Checks for a configured default value or default value class for the
     * field. If both are given, the configured static default value will win.
     * In addition, if the default value contains an el expression it is
     * evaluated against the initial context
     * </p>
     *
     * @param view        - view instance the field belongs to
     * @param object      - object that should be populated
     * @param dataField   - field to check for configured default value
     * @param bindingPath - path to the property on the object that should be populated
     */
    protected void populateDefaultValueForField(View view, Object object, DataField dataField, String bindingPath) {
        // check for configured default value
        String defaultValue = dataField.getDefaultValue();
        if (StringUtils.isBlank(defaultValue) && (dataField.getDefaultValueFinderClass() != null)) {
            ValueFinder defaultValueFinder = ObjectUtils.newInstance(dataField.getDefaultValueFinderClass());
            defaultValue = defaultValueFinder.getValue();
        }

        // populate default value if given and path is valid
        if (StringUtils.isNotBlank(defaultValue) && ObjectPropertyUtils.isWritableProperty(object, bindingPath)) {
            if (getExpressionEvaluatorService().containsElPlaceholder(defaultValue)) {
                Map<String, Object> context = getPreModelContext(view);
                defaultValue = getExpressionEvaluatorService().evaluateExpressionTemplate(null, context, defaultValue);
            }

            // TODO: this should go through our formatters
            // Skip nullable non-null non-empty objects when setting default
            Object currentValue = ObjectPropertyUtils.getPropertyValue(object, bindingPath);
            Class currentClazz = ObjectPropertyUtils.getPropertyType(object, bindingPath);
            if (currentValue == null || StringUtils.isBlank(currentValue.toString()) || ClassUtils.isPrimitiveOrWrapper(currentClazz)) {
                ObjectPropertyUtils.setPropertyValue(object, bindingPath, defaultValue);
            }
        }
    }

    /**
     * Hook for creating new components with code and adding them to a container
     * <p>
     * <p>
     * Subclasses can override this method to check for one or more containers by id and then adding components
     * created in code. This is invoked before the initialize method on the container component, so the full
     * lifecycle will be run on the components returned.
     * </p>
     * <p>
     * <p>
     * New components instances can be retrieved using {@link ComponentFactory}
     * </p>
     *
     * @param view      - view instance the container belongs to
     * @param model     - object containing the view data
     * @param container - container instance to add components to
     */
    protected void addCustomContainerComponents(View view, Object model, Container container) {

    }

    /**
     * Hook for service overrides to perform custom initialization on the
     * component
     *
     * @param view      - view instance containing the component
     * @param component - component instance to initialize
     */
    protected void performCustomInitialization(View view, Component component) {

    }

    /**
     * Hook for service overrides to perform custom apply model logic on the
     * component
     *
     * @param view      - view instance containing the component
     * @param component - component instance to apply model to
     * @param model     - Top level object containing the data (could be the form or a
     *                  top level business object, dto)
     */
    protected void performCustomApplyModel(View view, Component component, Object model) {

    }

    /**
     * Hook for service overrides to perform custom component finalization
     *
     * @param view      - view instance containing the component
     * @param component - component instance to update
     * @param model     - Top level object containing the data
     * @param parent    - Parent component for the component being finalized
     */
    protected void performCustomFinalize(View view, Component component, Object model, Component parent) {

    }

    /**
     * Hook for service overrides to process the new collection line before it
     * is added to the collection
     *
     * @param view            - view instance that is being presented (the action was taken
     *                        on)
     * @param collectionGroup - collection group component for the collection the line will
     *                        be added to
     * @param model           - object instance that contain's the views data
     * @param addLine         - the new line instance to be processed
     */
    protected void processBeforeAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

    }

    /**
     * Hook for service overrides to process the new collection line after it
     * has been added to the collection
     *
     * @param view            - view instance that is being presented (the action was taken
     *                        on)
     * @param collectionGroup - collection group component for the collection the line that
     *                        was added
     * @param model           - object instance that contain's the views data
     * @param addLine         - the new line that was added
     */
    protected void processAfterAddLine(View view, CollectionGroup collectionGroup, Object model, Object addLine) {

    }

    /**
     * Hook for service overrides to process the collection line after it has been deleted
     *
     * @param view            - view instance that is being presented (the action was taken on)
     * @param collectionGroup - collection group component for the collection the line that
     *                        was added
     * @param model           - object instance that contains the views data
     * @param lineIndex       - index of the line that was deleted
     */
    protected void processAfterDeleteLine(View view, CollectionGroup collectionGroup, Object model, int lineIndex) {

    }

    protected void logAndThrowRuntime(String message) {
        LOG.error(message);
        throw new RuntimeException(message);
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (this.dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }

        return this.dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected ExpressionEvaluatorService getExpressionEvaluatorService() {
        if (this.expressionEvaluatorService == null) {
            this.expressionEvaluatorService = KRADServiceLocatorWeb.getExpressionEvaluatorService();
        }

        return this.expressionEvaluatorService;
    }

    public void setExpressionEvaluatorService(ExpressionEvaluatorService expressionEvaluatorService) {
        this.expressionEvaluatorService = expressionEvaluatorService;
    }

    public ViewDictionaryService getViewDictionaryService() {
        if (this.viewDictionaryService == null) {
            this.viewDictionaryService = KRADServiceLocatorWeb.getViewDictionaryService();
        }
        return this.viewDictionaryService;
    }

    public void setViewDictionaryService(ViewDictionaryService viewDictionaryService) {
        this.viewDictionaryService = viewDictionaryService;
    }
}
