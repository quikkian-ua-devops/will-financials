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
package org.kuali.kfs.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.bo.DataObjectAuthorizer;
import org.kuali.kfs.krad.bo.DataObjectAuthorizerBase;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentSecurity;
import org.kuali.kfs.krad.uif.component.DataBinding;
import org.kuali.kfs.krad.uif.container.CollectionGroup;
import org.kuali.kfs.krad.uif.container.Group;
import org.kuali.kfs.krad.uif.field.ActionField;
import org.kuali.kfs.krad.uif.field.DataField;
import org.kuali.kfs.krad.uif.field.Field;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.widget.Widget;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of {@link ViewAuthorizer} that verifies authorization with KIM permission checks
 * <p>
 * <p>
 * Each permission goes through one of the isAuthorized methods provided by
 * {@link DataObjectAuthorizer}, these in turn call {@link #addPermissionDetails(Object, java.util.Map)}
 * and {@link #addRoleQualification(Object, java.util.Map)} for building the permission and role maps to send with
 * the permission check. Subclasses can override these methods to add additional attributes
 * </p>
 */
public class ViewAuthorizerBase extends DataObjectAuthorizerBase implements ViewAuthorizer {
    private static final long serialVersionUID = -2687378084630965412L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ViewAuthorizerBase.class);

    private ConfigurationService configurationService;

    /**
     * @see ViewAuthorizer#getActionFlags(View, ViewModel,
     * org.kuali.rice.kim.api.identity.Person, java.util.Set<java.lang.String>)
     */
    public Set<String> getActionFlags(View view, ViewModel model, Person user, Set<String> actions) {
        if (actions.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && !canEditView(view, model, user)) {
            actions.remove(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }

        return actions;
    }

    /**
     * @see ViewAuthorizer#getEditModes(View, ViewModel,
     * org.kuali.rice.kim.api.identity.Person, java.util.Set<java.lang.String>)
     */
    public Set<String> getEditModes(View view, ViewModel model, Person user, Set<String> editModes) {
        Set<String> unauthorizedEditModes = new HashSet<String>();

        Object dataObjectForContext = getDataObjectContext(view, model);

        for (String editMode : editModes) {
            Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
            additionalPermissionDetails.put(KimConstants.AttributeConstants.EDIT_MODE, editMode);
            if (permissionExistsByTemplate(dataObjectForContext, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.USE_TRANSACTIONAL_DOCUMENT, additionalPermissionDetails)
                && !isAuthorizedByTemplate(dataObjectForContext, KRADConstants.KNS_NAMESPACE,
                KimConstants.PermissionTemplateNames.USE_TRANSACTIONAL_DOCUMENT, user.getPrincipalId(),
                additionalPermissionDetails, null)) {
                unauthorizedEditModes.add(editMode);
            }
        }
        editModes.removeAll(unauthorizedEditModes);

        return editModes;
    }

    /**
     * Checks for an open view permission for the view id, and if found verifies the user has that permission
     *
     * @see ViewAuthorizer#canOpenView(View, ViewModel, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canOpenView(View view, ViewModel model, Person user) {
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, view.getViewNamespaceCode());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.VIEW_ID, model.getViewId());

        if (permissionExistsByTemplate(model, KRADConstants.KRAD_NAMESPACE,
            KimConstants.PermissionTemplateNames.OPEN_VIEW, additionalPermissionDetails)) {
            return isAuthorizedByTemplate(model, KRADConstants.KRAD_NAMESPACE,
                KimConstants.PermissionTemplateNames.OPEN_VIEW, user.getPrincipalId(), additionalPermissionDetails,
                null);
        }

        return true;
    }

    /**
     * Checks for an edit view permission for the view id, and if found verifies the user has that permission
     *
     * @see ViewAuthorizer#canEditView(View, ViewModel,
     * org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canEditView(View view, ViewModel model, Person user) {
        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, view.getViewNamespaceCode());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.VIEW_ID, model.getViewId());

        if (permissionExistsByTemplate(model, KRADConstants.KRAD_NAMESPACE,
            KimConstants.PermissionTemplateNames.EDIT_VIEW, additionalPermissionDetails)) {
            return isAuthorizedByTemplate(model, KRADConstants.KRAD_NAMESPACE,
                KimConstants.PermissionTemplateNames.EDIT_VIEW, user.getPrincipalId(), additionalPermissionDetails,
                null);
        }

        return true;
    }

    /**
     * @see ViewAuthorizer#canUnmaskField(View, ViewModel,
     * DataField, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canUnmaskField(View view, ViewModel model, DataField field, String propertyName, Person user) {
        // check mask authz flag is set
        AttributeSecurity attributeSecurity = field.getDataFieldSecurity().getAttributeSecurity();
        if (attributeSecurity == null || !attributeSecurity.isMask()) {
            return true;
        }

        // for non-production environments the ability to unmask can be disabled by a system parameter
        if (isNonProductionEnvAndUnmaskingTurnedOff()) {
            return false;
        }

        Object dataObjectForContext = getDataObjectContext(view, model);

        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails = KRADUtils.getNamespaceAndComponentSimpleName(dataObjectForContext.getClass());
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, propertyName);
        // TODO: check for namespace, component, attribute override on attribute security

        if (field.getComponentSecurity().getAdditionalPermissionDetails() != null) {
            permissionDetails.putAll(field.getComponentSecurity().getAdditionalPermissionDetails());
        }

        Map<String, String> roleQualifications = new HashMap<String, String>();
        if (field.getComponentSecurity().getAdditionalRoleQualifiers() != null) {
            roleQualifications.putAll(field.getComponentSecurity().getAdditionalRoleQualifiers());
        }

        return isAuthorizedByTemplate(dataObjectForContext, KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD, user.getPrincipalId(), permissionDetails,
            roleQualifications);
    }

    /**
     * @see ViewAuthorizer#canPartialUnmaskField(View, ViewModel,
     * DataField, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canPartialUnmaskField(View view, ViewModel model, DataField field, String propertyName,
                                         Person user) {
        // check partial mask authz flag is set
        AttributeSecurity attributeSecurity = field.getDataFieldSecurity().getAttributeSecurity();
        if (attributeSecurity == null || !attributeSecurity.isPartialMask()) {
            return true;
        }

        // for non-production environments the ability to unmask can be disabled by a system parameter
        if (isNonProductionEnvAndUnmaskingTurnedOff()) {
            return false;
        }

        Object dataObjectForContext = getDataObjectContext(view, model);

        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails = KRADUtils.getNamespaceAndComponentSimpleName(dataObjectForContext.getClass());
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, propertyName);
        // TODO: check for namespace, component, attribute override on attribute security

        if (field.getComponentSecurity().getAdditionalPermissionDetails() != null) {
            permissionDetails.putAll(field.getComponentSecurity().getAdditionalPermissionDetails());
        }

        Map<String, String> roleQualifications = new HashMap<String, String>();
        if (field.getComponentSecurity().getAdditionalRoleQualifiers() != null) {
            roleQualifications.putAll(field.getComponentSecurity().getAdditionalRoleQualifiers());
        }

        return isAuthorizedByTemplate(dataObjectForContext, KRADConstants.KNS_NAMESPACE,
            KimConstants.PermissionTemplateNames.PARTIAL_UNMASK_FIELD, user.getPrincipalId(), permissionDetails,
            roleQualifications);
    }

    /**
     * @see ViewAuthorizer#canEditField(View, ViewModel,
     * Field, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canEditField(View view, ViewModel model, Field field, String propertyName, Person user) {
        // check edit authz flag is set
        if (!field.getComponentSecurity().isEditAuthz()) {
            return true;
        }

        return isAuthorizedByTemplate(view, field, model, KimConstants.PermissionTemplateNames.EDIT_FIELD, user, null,
            null, false);
    }

    /**
     * @see ViewAuthorizer#canViewField(View, ViewModel,
     * Field, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canViewField(View view, ViewModel model, Field field, String propertyName, Person user) {
        // check view authz flag is set
        if (!field.getComponentSecurity().isViewAuthz()) {
            return true;
        }

        return isAuthorizedByTemplate(view, field, model, KimConstants.PermissionTemplateNames.VIEW_FIELD, user, null,
            null, false);
    }

    /**
     * @see ViewAuthorizer#canEditGroup(View, ViewModel,
     * Group, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canEditGroup(View view, ViewModel model, Group group, String groupId, Person user) {
        // check edit group authz flag is set
        if (!group.getComponentSecurity().isEditAuthz()) {
            return true;
        }

        return isAuthorizedByTemplate(view, group, model, KimConstants.PermissionTemplateNames.EDIT_GROUP, user, null,
            null, false);
    }

    /**
     * @see ViewAuthorizer#canViewGroup(View, ViewModel,
     * Group, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canViewGroup(View view, ViewModel model, Group group, String groupId, Person user) {
        // check view group authz flag is set
        if (!group.getComponentSecurity().isViewAuthz()) {
            return true;
        }

        return isAuthorizedByTemplate(view, group, model, KimConstants.PermissionTemplateNames.VIEW_GROUP, user, null,
            null, false);
    }

    /**
     * @see ViewAuthorizer#canEditWidget(View, ViewModel,
     * Widget, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canEditWidget(View view, ViewModel model, Widget widget, String widgetId, Person user) {
        // check edit widget authz flag is set
        if (!widget.getComponentSecurity().isViewAuthz()) {
            return true;
        }

        return isAuthorizedByTemplate(view, widget, model, KimConstants.PermissionTemplateNames.EDIT_WIDGET, user, null,
            null, false);
    }

    /**
     * @see ViewAuthorizer#canViewWidget(View, ViewModel,
     * Widget, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canViewWidget(View view, ViewModel model, Widget widget, String widgetId, Person user) {
        // check view widget authz flag is set
        if (!widget.getComponentSecurity().isViewAuthz()) {
            return true;
        }

        return isAuthorizedByTemplate(view, widget, model, KimConstants.PermissionTemplateNames.VIEW_WIDGET, user, null,
            null, false);
    }

    /**
     * @see ViewAuthorizer#canPerformAction(View, ViewModel,
     * ActionField, java.lang.String, java.lang.String, org.kuali.rice.kim.api.identity.Person)
     */
    public boolean canPerformAction(View view, ViewModel model, ActionField actionField, String actionEvent,
                                    String actionId, Person user) {
        // check action authz flag is set
        if (!actionField.getActionFieldSecurity().isPerformActionAuthz()) {
            return true;
        }

        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        if (StringUtils.isNotBlank(actionEvent)) {
            additionalPermissionDetails.put(KimConstants.AttributeConstants.ACTION_EVENT, actionEvent);
        }

        return isAuthorizedByTemplate(view, actionField, model, KimConstants.PermissionTemplateNames.PERFORM_ACTION,
            user, additionalPermissionDetails, null, false);
    }

    public boolean canEditLine(View view, ViewModel model, CollectionGroup collectionGroup,
                               String collectionPropertyName, Object line, Person user) {
        // check edit line authz flag is set
        if (!collectionGroup.getCollectionGroupSecurity().isEditLineAuthz()) {
            return true;
        }

        return isAuthorizedByTemplate(view, collectionGroup, model, KimConstants.PermissionTemplateNames.EDIT_LINE,
            user, null, null, false);
    }

    public boolean canViewLine(View view, ViewModel model, CollectionGroup collectionGroup,
                               String collectionPropertyName, Object line, Person user) {
        // check view line authz flag is set
        if (!collectionGroup.getCollectionGroupSecurity().isViewLineAuthz()) {
            return true;
        }

        return isAuthorizedByTemplate(view, collectionGroup, model, KimConstants.PermissionTemplateNames.VIEW_LINE,
            user, null, null, false);
    }

    public boolean canEditLineField(View view, ViewModel model, CollectionGroup collectionGroup,
                                    String collectionPropertyName, Object line, Field field, String propertyName, Person user) {
        // check edit line field authz flag is set
        if (!field.getFieldSecurity().isEditInLineAuthz()) {
            return true;
        }

        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.GROUP_ID, collectionGroup.getId());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.COLLECTION_PROPERTY_NAME,
            collectionGroup.getPropertyName());

        return isAuthorizedByTemplate(view, field, model,
            KimConstants.PermissionTemplateNames.EDIT_LINE_FIELD, user, additionalPermissionDetails, null, false);
    }

    public boolean canViewLineField(View view, ViewModel model, CollectionGroup collectionGroup,
                                    String collectionPropertyName, Object line, Field field, String propertyName, Person user) {
        // check view line field authz flag is set
        if (!field.getFieldSecurity().isViewInLineAuthz()) {
            return true;
        }

        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.GROUP_ID, collectionGroup.getId());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.COLLECTION_PROPERTY_NAME,
            collectionGroup.getPropertyName());

        return isAuthorizedByTemplate(view, field, model,
            KimConstants.PermissionTemplateNames.VIEW_LINE_FIELD, user, additionalPermissionDetails, null, false);
    }

    public boolean canPerformLineAction(View view, ViewModel model, CollectionGroup collectionGroup,
                                        String collectionPropertyName, Object line, ActionField actionField, String actionEvent, String actionId,
                                        Person user) {
        // check perform line action authz flag is set
        if (!actionField.getActionFieldSecurity().isPerformLineActionAuthz()) {
            return true;
        }

        Map<String, String> additionalPermissionDetails = new HashMap<String, String>();
        additionalPermissionDetails.put(KimConstants.AttributeConstants.GROUP_ID, collectionGroup.getId());
        additionalPermissionDetails.put(KimConstants.AttributeConstants.COLLECTION_PROPERTY_NAME,
            collectionGroup.getPropertyName());
        if (StringUtils.isNotBlank(actionEvent)) {
            additionalPermissionDetails.put(KimConstants.AttributeConstants.ACTION_EVENT, actionEvent);
        }

        return isAuthorizedByTemplate(view, actionField, model,
            KimConstants.PermissionTemplateNames.PERFORM_LINE_ACTION, user, additionalPermissionDetails, null,
            false);
    }

    /**
     * Retrieves the object from the model that is used as the context for permission checks
     * <p>
     * <p>
     * Used to derive namespace and component details. Subclasses can override to return the object to be used
     * </p>
     *
     * @param view  - view instance the permission checks are being done for
     * @param model - model object containing the data and from which the data object should be pulled
     * @return Object data object instance to use
     */
    protected Object getDataObjectContext(View view, ViewModel model) {
        Object dataObject = model;

        if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
            Object defaultObject = ObjectPropertyUtils.getPropertyValue(model, view.getDefaultBindingObjectPath());
            if (defaultObject != null) {
                dataObject = defaultObject;
            }
        }

        return dataObject;
    }

    /**
     * Builds the permission details map for a field which includes the component namespace, component name, and
     * field id, in addition to property name for data binding fields
     *
     * @param view       - view instance the field belongs to
     * @param dataObject - default object from the data model (used for subclasses to build details)
     * @param field      - field instance the details are being built for
     * @return Map<String, String> permission details for the field
     */
    protected Map<String, String> getFieldPermissionDetails(View view, Object dataObject, Field field) {
        Map<String, String> permissionDetails = new HashMap<String, String>();

        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, view.getViewNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.VIEW_ID, view.getId());
        permissionDetails.put(KimConstants.AttributeConstants.FIELD_ID, field.getId());

        if (field instanceof DataBinding) {
            permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME,
                ((DataBinding) field).getPropertyName());
        }

        return permissionDetails;
    }

    /**
     * Builds the permission details map for a group which includes the component namespace, component name, and
     * group id, in addition to property name for collection groups
     *
     * @param view       - view instance the group belongs to
     * @param dataObject - default object from the data model (used for subclasses to build details)
     * @param group      - group instance the details are being built for
     * @return Map<String, String> permission details for the group
     */
    protected Map<String, String> getGroupPermissionDetails(View view, Object dataObject, Group group) {
        Map<String, String> permissionDetails = new HashMap<String, String>();

        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, view.getViewNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.VIEW_ID, view.getId());
        permissionDetails.put(KimConstants.AttributeConstants.GROUP_ID, group.getId());

        if (group instanceof CollectionGroup) {
            permissionDetails.put(KimConstants.AttributeConstants.COLLECTION_PROPERTY_NAME,
                ((CollectionGroup) group).getPropertyName());
        }

        return permissionDetails;
    }

    /**
     * Builds the permission details map for a widget which includes the namespace, view id, and
     * widget id
     *
     * @param view       - view instance the widget belongs to
     * @param dataObject - default object from the data model (used for subclasses to build details)
     * @param widget     - group instance the details are being built for
     * @return Map<String, String> permission details for group
     */
    protected Map<String, String> getWidgetPermissionDetails(View view, Object dataObject, Widget widget) {
        Map<String, String> permissionDetails = new HashMap<String, String>();

        permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, view.getViewNamespaceCode());
        permissionDetails.put(KimConstants.AttributeConstants.VIEW_ID, view.getId());
        permissionDetails.put(KimConstants.AttributeConstants.WIDGET_ID, widget.getId());

        return permissionDetails;
    }

    /**
     * Performs a permission check for the given template name in the context of the given view and component
     * <p>
     * <p>
     * First standard permission details are added based on the type of component the permission check is being
     * done for.
     * Then the {@link ComponentSecurity} of the given component is used to pick up additional permission details and
     * role qualifiers.
     * </p>
     *
     * @param view                         - view instance the component belongs to
     * @param component                    - component instance the permission check is being done for
     * @param model                        - object containing the views data
     * @param permissionTemplateName       - template name for the permission to check
     * @param user                         - user to perform the authorization for
     * @param additionalPermissionDetails  - additional key/value pairs to pass with the permission details
     * @param additionalRoleQualifications - additional key/value paris to pass with the role qualifiers
     * @param checkPermissionExistence     - boolean indicating whether the existence of the permission should be checked
     *                                     before performing the authorization
     * @return boolean indicating whether the user has authorization, this will be the case if the user has been
     * granted the permission or checkPermissionExistence is true and the permission does not exist
     */
    protected boolean isAuthorizedByTemplate(View view, Component component, ViewModel model,
                                             String permissionTemplateName, Person user, Map<String, String> additionalPermissionDetails,
                                             Map<String, String> additionalRoleQualifications, boolean checkPermissionExistence) {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        Map<String, String> roleQualifications = new HashMap<String, String>();

        if (additionalPermissionDetails != null) {
            permissionDetails.putAll(additionalPermissionDetails);
        }

        if (additionalRoleQualifications != null) {
            roleQualifications.putAll(additionalRoleQualifications);
        }

        Object dataObjectForContext = getDataObjectContext(view, model);

        // add permission details depending on the type of component
        if (component instanceof Field) {
            permissionDetails.putAll(getFieldPermissionDetails(view, dataObjectForContext, (Field) component));
        } else if (component instanceof Group) {
            permissionDetails.putAll(getGroupPermissionDetails(view, dataObjectForContext, (Group) component));
        } else if (component instanceof Widget) {
            permissionDetails.putAll(getWidgetPermissionDetails(view, dataObjectForContext, (Widget) component));
        }

        // pick up additional attributes and overrides from component security
        ComponentSecurity componentSecurity = component.getComponentSecurity();

        // add configured overrides
        if (StringUtils.isNotBlank(componentSecurity.getNamespaceAttribute())) {
            permissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE,
                componentSecurity.getNamespaceAttribute());
        }
        if (StringUtils.isNotBlank(componentSecurity.getComponentAttribute())) {
            permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME,
                componentSecurity.getComponentAttribute());
        }
        if (StringUtils.isNotBlank(componentSecurity.getIdAttribute())) {
            if (component instanceof Field) {
                permissionDetails.put(KimConstants.AttributeConstants.FIELD_ID, componentSecurity.getIdAttribute());
            } else if (component instanceof Group) {
                permissionDetails.put(KimConstants.AttributeConstants.GROUP_ID, componentSecurity.getIdAttribute());
            } else if (component instanceof Widget) {
                permissionDetails.put(KimConstants.AttributeConstants.WIDGET_ID, componentSecurity.getIdAttribute());
            }
        }

        if (componentSecurity.getAdditionalPermissionDetails() != null) {
            permissionDetails.putAll(componentSecurity.getAdditionalPermissionDetails());
        }

        if (componentSecurity.getAdditionalRoleQualifiers() != null) {
            roleQualifications.putAll(componentSecurity.getAdditionalRoleQualifiers());
        }

        boolean result = true;
        if (!checkPermissionExistence || (checkPermissionExistence && permissionExistsByTemplate(dataObjectForContext,
            KRADConstants.KRAD_NAMESPACE, permissionTemplateName, permissionDetails))) {
            result = isAuthorizedByTemplate(dataObjectForContext, KRADConstants.KRAD_NAMESPACE, permissionTemplateName,
                user.getPrincipalId(), permissionDetails, roleQualifications);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Performed permission check for: " + permissionTemplateName + " and got result: " + result);
            }
        }

        return result;
    }

    /**
     * Indicates whether the environment is non production and unmasking is not enabled by system parameter
     *
     * @return boolean true if unmasking is turned off, false if unmasking is allowed
     */
    private boolean isNonProductionEnvAndUnmaskingTurnedOff() {
        return !getConfigurationService().getPropertyValueAsString(KRADConstants.PROD_ENVIRONMENT_CODE_KEY).
            equalsIgnoreCase(getConfigurationService().getPropertyValueAsString(KRADConstants.ENVIRONMENT_KEY))
            && !getConfigurationService().getPropertyValueAsBoolean(KRADConstants.ENABLE_NONPRODUCTION_UNMASKING);
    }

    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            return KRADServiceLocator.getKualiConfigurationService();
        }
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
