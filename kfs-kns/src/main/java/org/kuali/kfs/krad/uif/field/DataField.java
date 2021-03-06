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
import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.bo.KualiCode;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatter;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.uif.component.BindingInfo;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.component.ComponentBase;
import org.kuali.kfs.krad.uif.component.ComponentSecurity;
import org.kuali.kfs.krad.uif.component.DataBinding;
import org.kuali.kfs.krad.uif.util.ObjectPropertyUtils;
import org.kuali.kfs.krad.uif.util.ViewModelUtils;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.uif.widget.Inquiry;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.krad.valuefinder.ValueFinder;
import org.kuali.rice.core.api.exception.RiceRuntimeException;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

/**
 * Field that renders data from the application, such as the value of a data object property
 */
public class DataField extends FieldBase implements DataBinding {
    private static final long serialVersionUID = -4129678891948564724L;

    // binding
    private String propertyName;
    private BindingInfo bindingInfo;

    private String dictionaryAttributeName;
    private String dictionaryObjectEntry;

    // value props
    private String defaultValue;
    private Class<? extends ValueFinder> defaultValueFinderClass;

    private PropertyEditor propertyEditor;

    private boolean readOnlyHidden;

    // alternate and additional display properties
    protected String alternateDisplayPropertyName;
    protected String additionalDisplayPropertyName;

    private String alternateDisplayValue;
    private String additionalDisplayValue;

    private boolean applyValueMask;
    private MaskFormatter maskFormatter;

    private List<String> hiddenPropertyNames;
    private List<String> informationalDisplayPropertyNames;

    private boolean escapeHtmlInPropertyValue = true;

    private String helpSummary;
    private String helpDescription;

    // widgets
    private Inquiry fieldInquiry;

    public DataField() {
        super();

        readOnlyHidden = false;
        applyValueMask = false;

        hiddenPropertyNames = new ArrayList<String>();
        informationalDisplayPropertyNames = new ArrayList<String>();
    }

    /**
     * The following initialization is performed:
     * <p>
     * <ul>
     * <li>Set defaults for binding</li>
     * <li>Default the model path if not set</li>
     * </ul>
     *
     * @see ComponentBase#performInitialization(View,
     * java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        if (bindingInfo != null) {
            bindingInfo.setDefaults(view, getPropertyName());
        }
    }

    /**
     * The following updates are done here:
     * <p>
     * <ul>
     * <li>If readOnlyHidden set to true, set field to readonly and add to hidden property names</li>
     * </ul>
     */
    public void performApplyModel(View view, Object model, Component parent) {
        super.performApplyModel(view, model, parent);

        if (isReadOnlyHidden()) {
            setReadOnly(true);
            getHiddenPropertyNames().add(getPropertyName());
        }
    }

    /**
     * The following actions are performed:
     * <p>
     * <ul>
     * <li>Set the ids for the various attribute components</li>
     * <li>Sets up the client side validation for constraints on this field. In
     * addition, it sets up the messages applied to this field</li>
     * </ul>
     *
     * @see ComponentBase#performFinalize(View,
     * java.lang.Object, Component)
     */
    @Override
    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        // adjust the path for hidden fields
        // TODO: should this check the view#readOnly?
        List<String> hiddenPropertyPaths = new ArrayList<String>();
        for (String hiddenPropertyName : getHiddenPropertyNames()) {
            String hiddenPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(hiddenPropertyName);
            hiddenPropertyPaths.add(hiddenPropertyPath);
        }
        this.hiddenPropertyNames = hiddenPropertyPaths;

        // adjust paths on informational property names
        List<String> informationalPropertyPaths = new ArrayList<String>();
        for (String infoPropertyName : getInformationalDisplayPropertyNames()) {
            String infoPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(infoPropertyName);
            informationalPropertyPaths.add(infoPropertyPath);
        }
        this.informationalDisplayPropertyNames = informationalPropertyPaths;

        // Additional and Alternate display value
        setAlternateAndAdditionalDisplayValue(view, model);
    }

    /**
     * Sets alternate and additional property value for this field.
     * <p>
     * <p>
     * If <code>AttributeSecurity</code> present in this field, make sure the current user has permission to view the
     * field value. If user doesn't have permission to view the value, mask the value as configured and set it
     * as alternate value for display. If security doesn't exists for this field but
     * <code>alternateDisplayPropertyName</code> present, get its value and format it based on that
     * fields formatting and set for display.
     * </p>
     * <p>
     * <p>
     * For additional display value, if <code>AttributeSecurity</code> not present, sets the value if
     * <code>additionalDisplayPropertyName</code> present. If not present, check whether this field is a
     * <code>KualiCode</code> and get the relationship configured in the datadictionary file and set the name
     * additional display value which will be displayed along with the code. If additional display property not
     * present, check whether this field is has <code>MultiValueControlBase</code>. If yes, get the Label
     * for the value and set it as additional display value.
     * </p>
     *
     * @param view  - the current view instance
     * @param model - model instance
     */
    protected void setAlternateAndAdditionalDisplayValue(View view, Object model) {
        // if alternate or additional display values set don't use property names
        if (StringUtils.isNotBlank(alternateDisplayValue) || StringUtils.isNotBlank(additionalDisplayValue)) {
            return;
        }

        // check whether field value needs to be masked, and if so apply masking as alternateDisplayValue
        if (isApplyValueMask()) {
            Object fieldValue = ObjectPropertyUtils.getPropertyValue(model, getBindingInfo().getBindingPath());
            alternateDisplayValue = getMaskFormatter().maskValue(fieldValue);

            // mask values are forced to be readonly
            setReadOnly(true);
            return;
        }

        // if not read only, return without trying to set alternate and additional values
        if (!isReadOnly()) {
            return;
        }

        // if field is not secure, check for alternate and additional display properties
        if (StringUtils.isNotBlank(getAlternateDisplayPropertyName())) {
            String alternateDisplayPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(
                getAlternateDisplayPropertyName());

            Object alternateFieldValue = ObjectPropertyUtils.getPropertyValue(model, alternateDisplayPropertyPath);
            if (alternateFieldValue != null) {
                // TODO: format by type
                alternateDisplayValue = alternateFieldValue.toString();
            }
        }

        // perform automatic translation for code references if enabled on view
        if (StringUtils.isBlank(getAdditionalDisplayPropertyName()) && view.isTranslateCodes()) {
            // check for any relationship present for this field and it's of type KualiCode
            Class<?> parentObjectClass = ViewModelUtils.getParentObjectClassForMetadata(view, model, this);
            DataObjectRelationship relationship =
                KRADServiceLocatorWeb.getDataObjectMetaDataService().getDataObjectRelationship(null,
                    parentObjectClass, getBindingInfo().getBindingName(), "", true, false, false);

            if (relationship != null
                && getPropertyName().startsWith(relationship.getParentAttributeName())
                && KualiCode.class.isAssignableFrom(relationship.getRelatedClass())) {
                additionalDisplayPropertyName =
                    relationship.getParentAttributeName() + "." + KRADPropertyConstants.NAME;
            }
        }

        // now check for an additional display property and if set get the value
        if (StringUtils.isNotBlank(getAdditionalDisplayPropertyName())) {
            String additionalDisplayPropertyPath = getBindingInfo().getPropertyAdjustedBindingPath(
                getAdditionalDisplayPropertyName());

            Object additionalFieldValue = ObjectPropertyUtils.getPropertyValue(model, additionalDisplayPropertyPath);
            if (additionalFieldValue != null) {
                // TODO: format by type
                additionalDisplayValue = additionalFieldValue.toString();
            }
        }
    }

    /**
     * Defaults the properties of the <code>DataField</code> to the
     * corresponding properties of its <code>AttributeDefinition</code>
     * retrieved from the dictionary (if such an entry exists). If the field
     * already contains a value for a property, the definitions value is not
     * used.
     *
     * @param view                - view instance the field belongs to
     * @param attributeDefinition - AttributeDefinition instance the property values should be
     *                            copied from
     */
    public void copyFromAttributeDefinition(View view, AttributeDefinition attributeDefinition) {
        // label
        if (StringUtils.isEmpty(getLabel())) {
            setLabel(attributeDefinition.getLabel());
        }

        // short label
        if (StringUtils.isEmpty(getShortLabel())) {
            setShortLabel(attributeDefinition.getShortLabel());
        }

        // summary
        if (StringUtils.isEmpty(getHelpSummary())) {
            setHelpSummary(attributeDefinition.getSummary());
        }

        // description
        if (StringUtils.isEmpty(getHelpDescription())) {
            setHelpDescription(attributeDefinition.getDescription());
        }

        // security
        if (getDataFieldSecurity().getAttributeSecurity() == null) {
            getDataFieldSecurity().setAttributeSecurity(attributeDefinition.getAttributeSecurity());
        }

        // alternate property name
        if (getAlternateDisplayPropertyName() == null && StringUtils.isNotBlank(
            attributeDefinition.getAlternateDisplayAttributeName())) {
            setAlternateDisplayPropertyName(attributeDefinition.getAlternateDisplayAttributeName());
        }

        // additional property display name
        if (getAdditionalDisplayPropertyName() == null && StringUtils.isNotBlank(
            attributeDefinition.getAdditionalDisplayAttributeName())) {
            setAdditionalDisplayPropertyName(attributeDefinition.getAdditionalDisplayAttributeName());
        }

        // property editor
        if (getPropertyEditor() == null) {
            setPropertyEditor(attributeDefinition.getPropertyEditor());
        }
    }

    /**
     * @see ComponentBase#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(fieldInquiry);

        return components;
    }

    /**
     * Indicates whether the data field instance allows input, subclasses should override and set to
     * true if input is allowed
     *
     * @return boolean true if input is allowed, false if read only
     */
    public boolean isInputAllowed() {
        return false;
    }

    /**
     * @see DataBinding#getPropertyName()
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * Setter for the component's property name
     *
     * @param propertyName
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Performs formatting of the field value for display and then converting the value back to its
     * expected type from a string
     * <p>
     * <p>
     * Note property editors exist and are already registered for the basic Java types and the
     * common Kuali types such as [@link KualiDecimal}. Registration with this property is only
     * needed for custom property editors
     * </p>
     *
     * @return PropertyEditor property editor instance to use for this field
     */
    public PropertyEditor getPropertyEditor() {
        return propertyEditor;
    }

    /**
     * Setter for the custom property editor to use for the field
     *
     * @param propertyEditor
     */
    public void setPropertyEditor(PropertyEditor propertyEditor) {
        this.propertyEditor = propertyEditor;
    }

    /**
     * Convenience setter for configuring a property editor by class
     *
     * @param propertyEditorClass
     */
    public void setPropertyEditorClass(Class<? extends PropertyEditor> propertyEditorClass) {
        this.propertyEditor = ObjectUtils.newInstance(propertyEditorClass);
    }

    /**
     * @see DataBinding#getBindingInfo()
     */
    public BindingInfo getBindingInfo() {
        return this.bindingInfo;
    }

    /**
     * Setter for the field's binding info
     *
     * @param bindingInfo
     */
    public void setBindingInfo(BindingInfo bindingInfo) {
        this.bindingInfo = bindingInfo;
    }

    /**
     * Name of the attribute within the data dictionary the attribute field is
     * associated with
     * <p>
     * <p>
     * During the initialize phase for the <code>View</code>, properties for
     * attribute fields are defaulted from a corresponding
     * <code>AttributeDefinition</code> in the data dictionary. Based on the
     * propertyName and parent object class the framework attempts will
     * determine the attribute definition that is associated with the field and
     * set this property. However this property can also be set in the fields
     * configuration to use another dictionary attribute.
     * </p>
     * <p>
     * <p>
     * The attribute name is used along with the dictionary object entry to find
     * the <code>AttributeDefinition</code>
     * </p>
     *
     * @return String attribute name
     */
    public String getDictionaryAttributeName() {
        return this.dictionaryAttributeName;
    }

    /**
     * Setter for the dictionary attribute name
     *
     * @param dictionaryAttributeName
     */
    public void setDictionaryAttributeName(String dictionaryAttributeName) {
        this.dictionaryAttributeName = dictionaryAttributeName;
    }

    /**
     * Object entry name in the data dictionary the associated attribute is
     * apart of
     * <p>
     * <p>
     * During the initialize phase for the <code>View</code>, properties for
     * attribute fields are defaulted from a corresponding
     * <code>AttributeDefinition</code> in the data dictionary. Based on the
     * parent object class the framework will determine the object entry for the
     * associated attribute. However the object entry can be set in the field's
     * configuration to use another object entry for the attribute
     * </p>
     * <p>
     * <p>
     * The attribute name is used along with the dictionary object entry to find
     * the <code>AttributeDefinition</code>
     * </p>
     *
     * @return
     */
    public String getDictionaryObjectEntry() {
        return this.dictionaryObjectEntry;
    }

    /**
     * Setter for the dictionary object entry
     *
     * @param dictionaryObjectEntry
     */
    public void setDictionaryObjectEntry(String dictionaryObjectEntry) {
        this.dictionaryObjectEntry = dictionaryObjectEntry;
    }

    /**
     * Default value for the model property the field points to
     * <p>
     * <p>
     * When a new <code>View</code> instance is requested, the corresponding
     * model will be newly created. During this initialization process the value
     * for the model property will be set to the given default value (if set)
     * </p>
     *
     * @return String default value
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Setter for the fields default value
     *
     * @param defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gives Class that should be invoked to produce the default value for the
     * field
     *
     * @return Class<? extends ValueFinder> default value finder class
     */
    public Class<? extends ValueFinder> getDefaultValueFinderClass() {
        return this.defaultValueFinderClass;
    }

    /**
     * Setter for the default value finder class
     *
     * @param defaultValueFinderClass
     */
    public void setDefaultValueFinderClass(Class<? extends ValueFinder> defaultValueFinderClass) {
        this.defaultValueFinderClass = defaultValueFinderClass;
    }

    /**
     * Summary help text for the field
     *
     * @return String summary help text
     */
    public String getHelpSummary() {
        return helpSummary;
    }

    /**
     * Setter for the summary help text
     *
     * @param helpSummary
     */
    public void setHelpSummary(String helpSummary) {
        this.helpSummary = helpSummary;
    }

    /**
     * Full help information text for the field
     *
     * @return String help description text
     */
    public String getHelpDescription() {
        return this.helpDescription;
    }

    /**
     * Setter for the help description text
     *
     * @param helpDescription
     */
    public void setHelpDescription(String helpDescription) {
        this.helpDescription = helpDescription;
    }

    /**
     * Data Field Security object that indicates what authorization (permissions) exist for the field
     *
     * @return DataFieldSecurity instance
     */
    public DataFieldSecurity getDataFieldSecurity() {
        return (DataFieldSecurity) super.getComponentSecurity();
    }

    /**
     * Override to assert a {@link DataFieldSecurity} instance is set
     *
     * @param componentSecurity - instance of DataFieldSecurity
     */
    @Override
    public void setComponentSecurity(ComponentSecurity componentSecurity) {
        if (!(componentSecurity instanceof DataFieldSecurity)) {
            throw new RiceRuntimeException("Component security for DataField should be instance of DataFieldSecurity");
        }

        super.setComponentSecurity(componentSecurity);
    }

    @Override
    protected Class<? extends ComponentSecurity> getComponentSecurityClass() {
        return DataFieldSecurity.class;
    }

    /**
     * Indicates the field should be read-only but also a hidden should be generated for the field
     * <p>
     * <p>
     * Useful for when a value is just displayed but is needed by script
     * </p>
     *
     * @return boolean true if field should be readOnly hidden, false if not
     */
    public boolean isReadOnlyHidden() {
        return readOnlyHidden;
    }

    /**
     * Setter for the read-only hidden indicator
     *
     * @param readOnlyHidden
     */
    public void setReadOnlyHidden(boolean readOnlyHidden) {
        this.readOnlyHidden = readOnlyHidden;
    }

    /**
     * Inquiry widget for the field
     * <p>
     * <p>
     * The inquiry widget will render a link for the field value when read-only
     * that points to the associated inquiry view for the field. The inquiry can
     * be configured to point to a certain <code>InquiryView</code>, or the
     * framework will attempt to associate the field with a inquiry based on its
     * metadata (in particular its relationships in the model)
     * </p>
     *
     * @return Inquiry field inquiry
     */
    public Inquiry getFieldInquiry() {
        return this.fieldInquiry;
    }

    /**
     * Setter for the inquiry widget
     *
     * @param fieldInquiry
     */
    public void setFieldInquiry(Inquiry fieldInquiry) {
        this.fieldInquiry = fieldInquiry;
    }

    /**
     * Additional display attribute name, which will be displayed next to the actual field value
     * when the field is readonly with hypen inbetween like PropertyValue - AdditionalPropertyValue
     *
     * @param additionalDisplayPropertyName - Name of the additional display property
     */
    public void setAdditionalDisplayPropertyName(String additionalDisplayPropertyName) {
        this.additionalDisplayPropertyName = additionalDisplayPropertyName;
    }

    /**
     * Returns the additional display attribute name to be displayed when the field is readonly
     *
     * @return Additional Display Attribute Name
     */
    public String getAdditionalDisplayPropertyName() {
        return this.additionalDisplayPropertyName;
    }

    /**
     * Sets the alternate display attribute name to be displayed when the field is readonly.
     * This properties value will be displayed instead of actual fields value when the field is readonly.
     *
     * @param alternateDisplayPropertyName - alternate display property name
     */
    public void setAlternateDisplayPropertyName(String alternateDisplayPropertyName) {
        this.alternateDisplayPropertyName = alternateDisplayPropertyName;
    }

    /**
     * Returns the alternate display attribute name to be displayed when the field is readonly.
     *
     * @return alternate Display Property Name
     */
    public String getAlternateDisplayPropertyName() {
        return this.alternateDisplayPropertyName;
    }

    /**
     * Returns the alternate display value
     *
     * @return the alternate display value set for this field
     */
    public String getAlternateDisplayValue() {
        return alternateDisplayValue;
    }

    /**
     * Setter for the alternative display value
     *
     * @param value
     */
    public void setAlternateDisplayValue(String value) {
        this.alternateDisplayValue = value;
    }

    /**
     * Returns the additional display value.
     *
     * @return the additional display value set for this field
     */
    public String getAdditionalDisplayValue() {
        return additionalDisplayValue;
    }

    /**
     * Setter for the additional display value
     *
     * @param value
     */
    public void setAdditionalDisplayValue(String value) {
        this.additionalDisplayValue = value;
    }

    /**
     * Indicates whether the value for the field should be masked (or partially masked) on display
     * <p>
     * <p>
     * If set to true, the field value will be masked by applying the configured {@link #getMaskFormatter()}
     * </p>
     * <p>
     * <p>
     * If a KIM permission exists that should be checked to determine whether the value should be masked or not,
     * this value should not be set but instead the mask or partialMask property on {@link #getComponentSecurity()}
     * should be set to true. This indicates there is a mask permission that should be consulted. If the user
     * does not have the permission, this flag will be set to true by the framework and the value masked using
     * the mask formatter configured on the security object
     * </p>
     *
     * @return boolean true if the field value should be masked, false if not
     */
    public boolean isApplyValueMask() {
        return applyValueMask;
    }

    /**
     * Setter for the apply value mask flag
     *
     * @param applyValueMask
     */
    public void setApplyValueMask(boolean applyValueMask) {
        this.applyValueMask = applyValueMask;
    }

    /**
     * MaskFormatter instance that will be used to mask the field value when {@link #isApplyValueMask()} is true
     * <p>
     * <p>
     * Note in cases where the mask is applied due to security (KIM permissions), the mask or partial mask formatter
     * configured on {@link #getComponentSecurity()} will be used instead of this mask formatter
     * </p>
     *
     * @return MaskFormatter instance
     */
    public MaskFormatter getMaskFormatter() {
        return maskFormatter;
    }

    /**
     * Setter for the MaskFormatter instance to apply when the value is masked
     *
     * @param maskFormatter
     */
    public void setMaskFormatter(MaskFormatter maskFormatter) {
        this.maskFormatter = maskFormatter;
    }

    /**
     * Allows specifying hidden property names without having to specify as a
     * field in the group config (that might impact layout)
     *
     * @return List<String> hidden property names
     */
    public List<String> getHiddenPropertyNames() {
        return hiddenPropertyNames;
    }

    /**
     * Setter for the hidden property names
     *
     * @param hiddenPropertyNames
     */
    public void setHiddenPropertyNames(List<String> hiddenPropertyNames) {
        this.hiddenPropertyNames = hiddenPropertyNames;
    }

    /**
     * List of property names whose values should be displayed read-only under this field
     * <p>
     * <p>
     * In the attribute field template for each information property name given its values is
     * outputted read-only. Informational property values can also be updated dynamically with
     * the use of field attribute query
     * </p>
     * <p>
     * <p>
     * Simple property names can be given if the property has the same binding parent as this
     * field, in which case the binding path will be adjusted by the framework. If the property
     * names starts with UifConstants#NO_BIND_ADJUST_PREFIX, no binding
     * prefix will be added.
     * </p>
     *
     * @return List<String> informational property names
     */
    public List<String> getInformationalDisplayPropertyNames() {
        return informationalDisplayPropertyNames;
    }

    /**
     * Setter for the list of informational property names
     *
     * @param informationalDisplayPropertyNames
     */
    public void setInformationalDisplayPropertyNames(List<String> informationalDisplayPropertyNames) {
        this.informationalDisplayPropertyNames = informationalDisplayPropertyNames;
    }

    /**
     * Sets HTML escaping for this property value. HTML escaping will be handled in alternate and additional fields
     * also.
     */
    public void setEscapeHtmlInPropertyValue(boolean escapeHtmlInPropertyValue) {
        this.escapeHtmlInPropertyValue = escapeHtmlInPropertyValue;
    }

    /**
     * Returns true if HTML escape allowed for this field
     *
     * @return true if escaping allowed
     */
    public boolean isEscapeHtmlInPropertyValue() {
        return this.escapeHtmlInPropertyValue;
    }

    /**
     * Indicates whether the value for the field is secure
     * <p>
     * <p>
     * A value will be secured if masking has been applied (by configuration or a failed KIM permission) or the field
     * has been marked as hidden due to an authorization check
     * </p>
     *
     * @return boolean true if value is secure, false if not
     */
    public boolean hasSecureValue() {
        return isApplyValueMask() || ((getComponentSecurity().isViewAuthz()
            || getDataFieldSecurity().isViewInLineAuthz()
            || ((getDataFieldSecurity().getAttributeSecurity() != null) && getDataFieldSecurity()
            .getAttributeSecurity().isHide())) && isHidden());
    }

}
