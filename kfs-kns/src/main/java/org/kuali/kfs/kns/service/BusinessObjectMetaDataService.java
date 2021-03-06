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
package org.kuali.kfs.kns.service;

import org.kuali.kfs.kns.datadictionary.FieldDefinition;
import org.kuali.kfs.krad.bo.DataObjectRelationship;
import org.kuali.kfs.krad.datadictionary.RelationshipDefinition;
import org.kuali.kfs.krad.service.DataObjectMetaDataService;
import org.kuali.kfs.krad.valuefinder.ValueFinder;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Provides Metadata about a specific BusinessObject. Depending on the circumstance or type
 * of BO it will retrieve the data it needs from either the DataDictionary or through the
 * PersistenceStructureService
 */
public interface BusinessObjectMetaDataService extends DataObjectMetaDataService {

    public DataObjectRelationship getBusinessObjectRelationship(RelationshipDefinition ddReference,
                                                                BusinessObject bo, Class boClass, String attributeName, String attributePrefix, boolean keysOnly);

    public RelationshipDefinition getBusinessObjectRelationshipDefinition(Class c, String attributeName);

    public RelationshipDefinition getBusinessObjectRelationshipDefinition(BusinessObject bo, String attributeName);

    /**
     * This method returns a list of inquirable field names
     *
     * @param bo
     * @return a collection of inquirable field names
     */
    public Collection<String> getInquirableFieldNames(Class boClass, String sectionTitle);

    /**
     * This method returns a list of lookupable fields
     *
     * @param bo
     * @return a collection of lookupable fields
     */
    public List<String> getLookupableFieldNames(Class boClass);

    /**
     * This method looks up the default value for a given attribute and returns
     * it
     *
     * @param businessObjectClass
     * @param attributeName
     * @return default value for an attribute
     */
    public String getLookupFieldDefaultValue(Class businessObjectClass, String attributeName);

    /**
     * This method returns the value finder class for a given attribute
     *
     * @param businessObjectClass
     * @param attributeName
     * @return value finder class
     */
    public Class getLookupFieldDefaultValueFinderClass(Class businessObjectClass, String attributeName);

    /**
     * This method looks up the quickfinder parameter string for a given
     * attribute and returns it. See
     * {@link FieldDefinition#getQuickfinderParameterString()}.
     *
     * @param businessObjectClass
     * @param attributeName
     * @return default values for attributes
     */
    public String getLookupFieldQuickfinderParameterString(Class businessObjectClass, String attributeName);

    /**
     * This method returns the quickfinder parameter string builder class for a
     * given attribute. See
     * {@link FieldDefinition#getQuickfinderParameterStringBuilderClass()}.
     *
     * @param businessObjectClass
     * @param attributeName
     * @return value finder class
     */
    public Class<? extends ValueFinder> getLookupFieldQuickfinderParameterStringBuilderClass(Class businessObjectClass,
                                                                                             String attributeName);

    /**
     * This method returns a list of collection names a business object contains
     *
     * @param bo
     * @return
     */
    public Collection<String> getCollectionNames(BusinessObject bo);

    /**
     * This method determines if a given field(attribute) is inquirable or not
     * This handles both nested and non-nested attributes
     *
     * @param bo
     * @param attributeName
     * @param sectionTitle
     * @return true if field is inquirable
     */
    public boolean isAttributeInquirable(Class boClass, String attributeName, String sectionTitle);

    /**
     * This method determines if a given business object is inquirable
     *
     * @param bo
     * @return true if bo is inquirable
     */
    public boolean isInquirable(Class boClass);

    /**
     * This method determines if a given field(attribute) is lookupable or not
     * This handles both nested and non-nested attributes
     *
     * @param bo
     * @param attributeName
     * @return true if field is lookupable
     */
    public boolean isAttributeLookupable(Class boClass, String attributeName);

    /**
     * This method determines if a given business object is lookupable
     *
     * @param bo
     * @return true if bo is lookupable
     */
    public boolean isLookupable(Class boClass);

    /**
     * This method will return a class that is related to the parent BO (either
     * through the DataDictionary or through the PersistenceStructureService)
     *
     * @param bo
     * @param attributes
     * @return related class
     */
    @Deprecated
    public DataObjectRelationship getBusinessObjectRelationship(BusinessObject bo, String attributeName);

    @Deprecated
    public DataObjectRelationship getBusinessObjectRelationship(BusinessObject bo, Class boClass,
                                                                String attributeName, String attributePrefix, boolean keysOnly);


    /**
     * Get all the business object relationships for the given business object.
     * These relationships may be defined at the ORM-layer or within the data
     * dictionary.
     */
    @Deprecated
    public List<DataObjectRelationship> getBusinessObjectRelationships(BusinessObject bo);

    /**
     * Get all the business object relationships for the given class. These
     * relationships may be defined at the ORM-layer or within the data
     * dictionary.
     */
    @Deprecated
    public List<DataObjectRelationship> getBusinessObjectRelationships(Class<? extends BusinessObject> boClass);

    /**
     * This method accepts a business object and one of its foreign key
     * attribute names. It returns a map that has a foreign key attribute name
     * as a key and its respective related class as value. If the passed in
     * attributeName is not a foreign key, this method will return an empty map.
     *
     * @param BusinessObject businessObject
     * @param String         attributeName
     * @return Map<String, Class>
     */
    @Deprecated
    public Map<String, Class> getReferencesForForeignKey(BusinessObject businessObject, String attributeName);

    /**
     * This method ...
     *
     * @param businessObjectClass
     * @param attributeName
     * @param targetName
     * @return
     */
    @Deprecated
    public String getForeignKeyFieldName(Class businessObjectClass, String attributeName, String targetName);
}
