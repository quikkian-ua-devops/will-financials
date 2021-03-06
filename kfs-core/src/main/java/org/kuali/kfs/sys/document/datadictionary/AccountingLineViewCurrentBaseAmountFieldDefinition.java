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
package org.kuali.kfs.sys.document.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.kns.util.FieldUtils;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.web.ui.FieldBridge;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.web.AccountingLineViewCurrentBaseAmount;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * The definition for an amount field which reports both current and base amounts
 */
public class AccountingLineViewCurrentBaseAmountFieldDefinition extends MaintainableFieldDefinition implements AccountingLineViewRenderableElementDefinition {
    private String currentAmountPropertyName;
    private String baseAmountPropertyName;
    private boolean useShortLabels = false;

    /**
     * Creates a property initiated AccountingLineViewCurrentBaseAmount element
     *
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement(java.lang.Class)
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        AccountingLineViewCurrentBaseAmount layoutElement = new AccountingLineViewCurrentBaseAmount();

        layoutElement.setBaseAmountField(createFieldForPropertyName(baseAmountPropertyName, accountingLineClass));
        layoutElement.setBaseAmountFieldDefinition(createFieldDefinitionForProperty(baseAmountPropertyName));

        layoutElement.setCurrentAmountField(createFieldForPropertyName(currentAmountPropertyName, accountingLineClass));
        layoutElement.setCurrentAmountFieldDefinition(createFieldDefinitionForProperty(currentAmountPropertyName));

        layoutElement.setDefinition(this);

        return layoutElement;
    }

    /**
     * Creates a field for the given AccountingLine class and property name
     *
     * @param propertyName        the name of the property to create a Field for
     * @param accountingLineClass the Class of the AccountingLine we're planning on rendering
     * @return an appropriately created Field
     */
    protected Field createFieldForPropertyName(String propertyName, Class<? extends AccountingLine> accountingLineClass) {
        Field realField = FieldUtils.getPropertyField(accountingLineClass, propertyName, false);
        FieldBridge.setupField(realField, this, null);
        if (useShortLabels) {
            BusinessObjectEntry boEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(accountingLineClass.getName());
            realField.setFieldLabel(boEntry.getAttributeDefinition(propertyName).getShortLabel());
        }
        return realField;
    }

    /**
     * Creates an AccountingLineViewFieldDefinition for the given property name
     *
     * @param propertyName the name of the field property that we're creating a definition for
     * @return an appropriately created AccountingLineViewFieldDefinition
     */
    protected AccountingLineViewFieldDefinition createFieldDefinitionForProperty(String propertyName) {
        AccountingLineViewFieldDefinition fieldDefinition = SpringContext.getBean(AccountingLineRenderingService.class).createGenericAccountingLineViewFieldDefinition(this);
        fieldDefinition.setName(propertyName);
        return fieldDefinition;
    }

    /**
     * @see org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    @Override
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (StringUtils.isBlank(currentAmountPropertyName)) {
            throw new AttributeValidationException("The currentAmountPropertyName property must be specified on the AccountingLineView-CurentBaseAmountField definition for " + rootBusinessObjectClass.getName());
        }

        if (StringUtils.isBlank(baseAmountPropertyName)) {
            throw new AttributeValidationException("The baseAmountPropertyName property must be specified on the AccountingLineView-CurentBaseAmountField definition for " + rootBusinessObjectClass.getName());
        }

        if (!StringUtils.isBlank(getName())) {
            throw new AttributeValidationException("please do not specify name on the AccountingLineView-CurentBaseAmountField definition for " + rootBusinessObjectClass.getName());
        }

        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getCurrentAmountPropertyName())) {
            throw new AttributeValidationException("unable to find attribute or collection named '" + getCurrentAmountPropertyName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }

        if (!DataDictionary.isPropertyOf(rootBusinessObjectClass, getBaseAmountPropertyName())) {
            throw new AttributeValidationException("unable to find attribute or collection named '" + getBaseAmountPropertyName() + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + "" + ")");
        }

        if (defaultValueFinderClass != null && defaultValue != null) {
            throw new AttributeValidationException("Both defaultValue and defaultValueFinderClass can not be specified on attribute " + getName() + " in rootBusinessObjectClass " + rootBusinessObjectClass.getName());
        }
    }

    /**
     * Gets the baseAmountPropertyName attribute.
     *
     * @return Returns the baseAmountPropertyName.
     */
    public String getBaseAmountPropertyName() {
        return baseAmountPropertyName;
    }

    /**
     * Sets the baseAmountPropertyName attribute value.
     *
     * @param baseAmountPropertyName The baseAmountPropertyName to set.
     */
    public void setBaseAmountPropertyName(String baseAmountPropertyName) {
        this.baseAmountPropertyName = baseAmountPropertyName;
    }

    /**
     * Gets the currentAmountPropertyName attribute.
     *
     * @return Returns the currentAmountPropertyName.
     */
    public String getCurrentAmountPropertyName() {
        return currentAmountPropertyName;
    }

    /**
     * Sets the currentAmountPropertyName attribute value.
     *
     * @param currentAmountPropertyName The currentAmountPropertyName to set.
     */
    public void setCurrentAmountPropertyName(String currentAmountPropertyName) {
        this.currentAmountPropertyName = currentAmountPropertyName;
    }

    /**
     * Gets the useShortLabels attribute.
     *
     * @return Returns the useShortLabels.
     */
    public boolean isUseShortLabels() {
        return useShortLabels;
    }

    /**
     * Sets the useShortLabels attribute value.
     *
     * @param useShortLabels The useShortLabels to set.
     */
    public void setUseShortLabels(boolean useShortLabels) {
        this.useShortLabels = useShortLabels;
    }

}
