/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.kns.datadictionary.exporter;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.datadictionary.control.ButtonControlDefinition;
import org.kuali.kfs.kns.datadictionary.control.CurrencyControlDefinition;
import org.kuali.kfs.kns.datadictionary.control.LinkControlDefinition;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;
import org.kuali.kfs.krad.datadictionary.exporter.ExportMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * AttributesMapBuilder
 */
@Deprecated
public class AttributesMapBuilder {

    /**
     * Default constructor
     */
    public AttributesMapBuilder() {
    }


    /**
     * @param entry
     * @return ExportMap containing the standard entries for the entry's AttributesDefinition
     */
    public ExportMap buildAttributesMap(DataDictionaryEntryBase entry) {
        ExportMap attributesMap = new ExportMap("attributes");

        for (AttributeDefinition attribute : entry.getAttributes()) {
            attributesMap.set(buildAttributeMap(attribute, entry.getFullClassName()));
        }

        return attributesMap;
    }

    public ExportMap buildAttributeMap(AttributeDefinition attribute, String fullClassName) {
        ExportMap attributeMap = new ExportMap(attribute.getName());

        // simple properties
        attributeMap.set("name", attribute.getName());
        attributeMap.set("forceUppercase", attribute.getForceUppercase().toString());
        attributeMap.set("label", attribute.getLabel());
        attributeMap.set("shortLabel", attribute.getShortLabel());

        //KULRICE-9144 remove maxLength non null assumption
        Integer maxLength = attribute.getMaxLength();
        if (maxLength != null) {
            attributeMap.set("maxLength", maxLength.toString());
        }
        String exclusiveMin = attribute.getExclusiveMin();
        if (exclusiveMin != null) {
            attributeMap.set("exclusiveMin", exclusiveMin/*.toString()*/);
        }
        String exclusiveMax = attribute.getInclusiveMax();
        if (exclusiveMax != null) {
            attributeMap.set("exclusiveMax", exclusiveMax/*.toString()*/);
        }

        attributeMap.set("required", attribute.isRequired().toString());
        if (attribute.getSummary() != null) {
            attributeMap.set("summary", attribute.getSummary());
        }
        if (attribute.getDescription() != null) {
            attributeMap.set("description", attribute.getDescription());
        }
        if (attribute.hasFormatterClass()) {
            attributeMap.set("formatterClass", attribute.getFormatterClass());
        }

        // complex properties
        if (attribute.hasValidationPattern()) {
            attributeMap.set(attribute.getValidationPattern().buildExportMap("validationPattern"));
        }

        if (attribute.hasAttributeSecurity()) {
            attributeMap.set("attributeSecurityMask", String.valueOf(attribute.getAttributeSecurity().isMask()));
            attributeMap.set("attributeSecurityPartialMask", String.valueOf(attribute.getAttributeSecurity().isPartialMask()));
            attributeMap.set("attributeSecurityHide", String.valueOf(attribute.getAttributeSecurity().isHide()));
            attributeMap.set("attributeSecurityReadOnly", String.valueOf(attribute.getAttributeSecurity().isReadOnly()));

            // TODO: consider whether to export class names from the attribute security
        }

        attributeMap.set(buildControlMap(attribute));
        if (attribute.getOptionsFinder() != null) {
            attributeMap.set(buildKeyLabelMap(attribute));
        }
        if (StringUtils.isNotBlank(fullClassName)) {
            attributeMap.set("fullClassName", fullClassName);
        }

        return attributeMap;
    }

    private ExportMap buildKeyLabelMap(AttributeDefinition attribute) {

        ExportMap keyLabelMap = new ExportMap("keyLabelMap");

        List<Map.Entry<String, String>> keyLabelList = new ArrayList<Map.Entry<String, String>>(attribute.getOptionsFinder().getKeyLabelMap().entrySet());
        Collections.sort(keyLabelList, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        for (Map.Entry<String, String> entry : keyLabelList) {
            keyLabelMap.set(entry.getKey(), entry.getValue());
        }
        return keyLabelMap;
    }

    private ExportMap buildControlMap(AttributeDefinition attribute) {
        ControlDefinition control = attribute.getControl();
        ExportMap controlMap = new ExportMap("control");

        if (control.isCheckbox()) {
            controlMap.set("checkbox", "true");
        } else if (control.isHidden()) {
            controlMap.set("hidden", "true");
        } else if (control.isKualiUser()) {
            controlMap.set("kualiUser", "true");
        } else if (control.isRadio()) {
            controlMap.set("radio", "true");
            if (control.getValuesFinderClass() != null) {
                controlMap.set("valuesFinder", control.getValuesFinderClass());
            }
            if (control.getBusinessObjectClass() != null) {
                controlMap.set("businessObject", control.getBusinessObjectClass());
            }
            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
                controlMap.set("keyAttribute", control.getKeyAttribute());
            }
            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
                controlMap.set("labelAttribute", control.getLabelAttribute());
            }
            if (control.getIncludeKeyInLabel() != null) {
                controlMap.set("includeKeyInLabel", control.getIncludeKeyInLabel().toString());
            }
        } else if (control.isSelect()) {
            controlMap.set("select", "true");
            if (control.getValuesFinderClass() != null) {
                controlMap.set("valuesFinder", control.getValuesFinderClass());
            }
            if (control.getBusinessObjectClass() != null) {
                controlMap.set("businessObject", control.getBusinessObjectClass());
            }
            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
                controlMap.set("keyAttribute", control.getKeyAttribute());
            }
            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
                controlMap.set("labelAttribute", control.getLabelAttribute());
            }
            if (control.getIncludeBlankRow() != null) {
                controlMap.set("includeBlankRow", control.getIncludeBlankRow().toString());
            }
            if (control.getIncludeKeyInLabel() != null) {
                controlMap.set("includeKeyInLabel", control.getIncludeKeyInLabel().toString());
            }
        } else if (control.isMultiselect()) {
            controlMap.set("multiselect", "true");
            if (control.getValuesFinderClass() != null) {
                controlMap.set("valuesFinder", control.getValuesFinderClass());
            }
            if (control.getBusinessObjectClass() != null) {
                controlMap.set("businessObject", control.getBusinessObjectClass());
            }
            if (StringUtils.isNotEmpty(control.getKeyAttribute())) {
                controlMap.set("keyAttribute", control.getKeyAttribute());
            }
            if (StringUtils.isNotEmpty(control.getLabelAttribute())) {
                controlMap.set("labelAttribute", control.getLabelAttribute());
            }
            if (control.getIncludeKeyInLabel() != null) {
                controlMap.set("includeKeyInLabel", control.getIncludeKeyInLabel().toString());
            }
            if (control.getSize() != null) {
                controlMap.set("size", control.getSize().toString());
            }
        } else if (control.isText()) {
            controlMap.set("text", "true");
            if (control.getSize() != null) {
                controlMap.set("size", control.getSize().toString());
            }
            controlMap.set("datePicker", Boolean.valueOf(control.isDatePicker()).toString());
            controlMap.set("ranged", Boolean.valueOf(control.isRanged()).toString());
        } else if (control.isTextarea()) {
            controlMap.set("textarea", "true");
            controlMap.set("rows", control.getRows().toString());
            controlMap.set("cols", control.getCols().toString());
            controlMap.set("expandedTextArea", Boolean.valueOf(control.isExpandedTextArea()).toString());
        } else if (control.isCurrency()) {
            controlMap.set("currency", "true");
            if (control.getSize() != null) {
                controlMap.set("size", control.getSize().toString());
            }
            controlMap.set("formattedMaxLength", ((CurrencyControlDefinition) control).getFormattedMaxLength().toString());
        } else if (control.isLookupHidden()) {
            controlMap.set("lookupHidden", "true");
        } else if (control.isLookupReadonly()) {
            controlMap.set("lookupReadonly", "true");
        } else if (control.isButton()) {
            controlMap.set("button", "true");
            if (StringUtils.isNotEmpty(((ButtonControlDefinition) control).getImageSrc())) {
                controlMap.set("imageSrc", ((ButtonControlDefinition) control).getImageSrc());
            }
            if (StringUtils.isNotEmpty(((ButtonControlDefinition) control).getStyleClass())) {
                controlMap.set("styleClass", ((ButtonControlDefinition) control).getStyleClass());
            }
        } else if (control.isLink()) {
            controlMap.set("link", "true");
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getTarget())) {
                controlMap.set("target", ((LinkControlDefinition) control).getTarget());
            }
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getStyleClass())) {
                controlMap.set("styleClass", ((LinkControlDefinition) control).getStyleClass());
            }
            if (StringUtils.isNotEmpty(((LinkControlDefinition) control).getHrefText())) {
                controlMap.set("hrefText", ((LinkControlDefinition) control).getHrefText());
            }
        }

        return controlMap;
    }
}
