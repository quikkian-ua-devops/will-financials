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
package org.kuali.kfs.kns.service;

import org.kuali.kfs.kns.inquiry.InquiryPresentationController;
import org.kuali.kfs.kns.inquiry.InquiryAuthorizer;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.kfs.krad.valuefinder.ValueFinder;

import java.util.List;


/**
 * This interface defines the API for the interacting with the data dictionary.
 */
@Deprecated
public interface BusinessObjectDictionaryService {
	public <T extends BusinessObject> InquiryPresentationController getInquiryPresentationController(Class<T> businessObjectClass);

	public <T extends BusinessObject> InquiryAuthorizer getInquiryAuthorizer(Class<T> businessObjectClass);

    /**
     * the list of business object class names being maintained
     */
    public List getBusinessObjectClassnames();


    /**
     * indicates whether business object has lookup defined
     */
    public Boolean isLookupable(Class businessObjectClass);


    /**
     * indicates whether business object has inquiry defined
     */
    public Boolean isInquirable(Class businessObjectClass);


    /**
     * indicates whether business object has maintainable defined
     */
    public Boolean isMaintainable(Class businessObjectClass);


    /**
     * indicates whether business object has an exporter defined
     */
    public Boolean isExportable(Class businessObjectClass);

    /**
     * the list defined as lookup fields for the business object.
     */
    public List getLookupFieldNames(Class businessObjectClass);


    /**
     * the text to be displayed for the title of business object lookup.
     */
    public String getLookupTitle(Class businessObjectClass);


    /**
     * menu bar html defined for the business object.
     */
    public String getLookupMenuBar(Class businessObjectClass);


    /**
     * source for optional extra button
     */
    public String getExtraButtonSource(Class businessObjectClass);


    /**
     * return parameters for optional extra button
     */
    public String getExtraButtonParams(Class businessObjectClass);


    /**
     * the property names of the bo used to sort the initial result set
     */
    public List getLookupDefaultSortFieldNames(Class businessObjectClass);


    /**
     * the list defined as lookup result fields for the business object.
     */
    public List<String> getLookupResultFieldNames(Class businessObjectClass);

    /**
     * This method returns the maximum display length of the value of the given field in the lookup results.  While the actual value may
     * be longer than the specified length, this value specifies the maximum length substring that should be displayed.
     * It is up to the UI layer to intepret the results of the field
     *
     * @param businessObjectClass
     * @param resultFieldName
     * @return the maximum length of the lookup results field that should be displayed.  Returns null
     * if this value has not been defined.  If negative, denotes that the is maximum length is unlimited.
     */
    public Integer getLookupResultFieldMaxLength(Class businessObjectClass, String resultFieldName);

    /**
     * returns boolean indicating whether lookup result field marked to force an inquiry
     */
    public Boolean forceLookupResultFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup result field marked to not do an inquiry
     */
    public Boolean noLookupResultFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup search field marked to force a lookup
     */
    public Boolean forceLookupFieldLookup(Class businessObjectClass, String attributeName);

    /**
     * returns boolean indicating whether lookup search field marked to force an inquiry
     */
    public Boolean forceInquiryFieldLookup(Class businessObjectClass, String attributeName);

    /**
     * returns boolean indicating whether lookup search field marked to not do a lookup
     */
    public Boolean noLookupFieldLookup(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup search field marked to not do a direct inquiry
     */
    public Boolean noDirectInquiryFieldLookup(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether inquiry result field marked to force an inquiry
     */
    public Boolean forceInquiryFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether inquiry result field marked to not do an inquiry
     */
    public Boolean noInquiryFieldInquiry(Class businessObjectClass, String attributeName);

    /**
     * returns boolean indicating whether lookup result field to use shortLabel
     */
    public Boolean getLookupResultFieldUseShortLabel(Class businessObjectClass, String attributeName);

    /**
     * returns boolean indicating whether lookup result field should be totaled
     */
    public Boolean getLookupResultFieldTotal(Class businessObjectClass, String attributeName);

    /**
     * returns String indicating the default search value for the lookup field
     */
    public String getLookupFieldDefaultValue(Class businessObjectClass, String attributeName);


    /**
     * returns Class used to generate a lookup field default value
     */
    public Class getLookupFieldDefaultValueFinderClass(Class businessObjectClass, String attributeName);

    /**
     * See {@link FieldDefinition#getQuickfinderParameterString()}.
     * returns String indicating the default search value for the lookup field.
     */
    public String getLookupFieldQuickfinderParameterString(Class businessObjectClass, String attributeName);


    /**
     * returns Class used to generate quickfinder lookup field default values.
     * See {@link FieldDefinition#getQuickfinderParameterStringBuilderClass()}.
     */
    public Class<? extends ValueFinder> getLookupFieldQuickfinderParameterStringBuilderClass(Class businessObjectClass, String attributeName);


    /**
     * returns String indicating the result set limit for the lookup
     */
    public Integer getLookupResultSetLimit(Class businessObjectClass);

    /**
     * returns Integer indicating the result set limit for a multiple values lookup
     */
    public Integer getMultipleValueLookupResultSetLimit(Class businessObjectClass);

    /**
     * @return number of search columns configured for the lookup associated with the class
     */
    public Integer getLookupNumberOfColumns(Class businessObjectClass);

    /**
     * returns String indicating the location of the lookup icon.
     */
    public String getSearchIconOverride(Class businessObjectClass);

    /**
     * indicates whether a field is required for a lookup
     */
    public Boolean getLookupAttributeRequired(Class businessObjectClass, String attributeName);

    /**
     * indicates whether a field is read only for a lookup
     */
    public Boolean getLookupAttributeReadOnly(Class businessObjectClass, String attributeName);


    /**
     * the list defined as inquiry fields for the business object and inquiry section.
     */
    public List getInquiryFieldNames(Class businessObjectClass, String sectionTitle);


    /**
     * the list defined as inquiry sections for the business object.
     */
    public List getInquirySections(Class businessObjectClass);


    /**
     * the text to be displayed for the title of business object inquiry.
     */
    public String getInquiryTitle(Class businessObjectClass);


    /**
     * the class to be used for building inquiry pages.
     */
    public Class getInquirableClass(Class businessObjectClass);

    /**
     * the text to be displayed for the title of business object maintenance document.
     */
    public String getMaintainableLabel(Class businessObjectClass);


    /**
     * the attribute to be associated with for object level markings
     */
    public String getTitleAttribute(Class businessObjectClass);


    /**
     * the Lookupable implementation id for the associated Lookup, if one has been specified
     */
    public String getLookupableID(Class businessObjectClass);


    /**
     * This method takes any business object and recursively walks through it checking to see if any attributes need to be forced to
     * uppercase based on settings in the data dictionary
     *
     * @param bo
     */
    public void performForceUppercase(BusinessObject bo);

    /**
     * returns whether on a lookup, field/attribute values with wildcards and operators should treat them as literal characters
     *
     * @param businessObjectClass
     * @param attributeName
     * @return
     */
    public boolean isLookupFieldTreatWildcardsAndOperatorsAsLiteral(Class businessObjectClass, String attributeName);

    /**
     * returns String giving alternate display attribute name for lookup field if configured, or null
     */
    public String getLookupFieldAlternateDisplayAttributeName(Class businessObjectClass, String attributeName);

    /**
     * returns String giving alternate display attribute name for inquiry field if configured, or null
     */
    public String getInquiryFieldAlternateDisplayAttributeName(Class businessObjectClass, String attributeName);

    /**
     * returns String giving additional display attribute name for lookup field if configured, or null
     */

    public String getLookupFieldAdditionalDisplayAttributeName(Class businessObjectClass, String attributeName);

    /**
     * returns String giving additional display attribute name for inquiry field if configured, or null
     */
    public String getInquiryFieldAdditionalDisplayAttributeName(Class businessObjectClass, String attributeName);

     /**
     * @param businessObjectClass - business object class for lookup definition
     * @return Boolean indicating whether translating of codes is configured to true in lookup definition
     */
    public Boolean tranlateCodesInLookup(Class businessObjectClass);

    /**
     * @param businessObjectClass - business object class for inquiry definition
     * @return Boolean indicating whether translating of codes is configured to true in inquiry definition
     */
    public Boolean tranlateCodesInInquiry(Class businessObjectClass);

    /**
     * Indicates whether a lookup field has been configured to trigger on value change
     *
     * @param businessObjectClass - Class for business object to lookup
     * @param attributeName - name of attribute in the business object to check configuration for
     * @return true if field is configured to trigger on value change, false if not
     */
    public boolean isLookupFieldTriggerOnChange(Class businessObjectClass, String attributeName);

	/**
	 * Indicates whether the search and clear buttons should be disabled based on the data
	 * dictionary configuration
	 *
	 * @param businessObjectClass
	 *            - business object class for lookup definition
	 * @return Boolean indicating whether disable search buttons is configured to true in lookup
	 *         definition
	 */
	public boolean disableSearchButtonsInLookup(Class businessObjectClass);


}
