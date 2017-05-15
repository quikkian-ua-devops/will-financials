/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.krad.service;

import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Defines service methods for module services
 */
public interface ModuleService extends InitializingBean, ApplicationContextAware {

    /**
     * This method returns the module configuration.
     *
     * @return
     */
    public ModuleConfiguration getModuleConfiguration();

    /**
     * This method determines whether this service is responsible for the business object class passed in, or not.
     *
     * @param businessObjectClass
     * @return
     */
    public boolean isResponsibleFor(Class businessObjectClass);

    /**
     * This method determines whether this service is responsible for the given jobname, or not.
     *
     * @param jobName
     * @return
     */
    public boolean isResponsibleForJob(String jobName);

    /**
     * This method returns the list of primary keys for the EBO.
     *
     * @param businessObjectInterfaceClass
     * @return
     */
    public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass);

    /**
     * This method returns a list of alternate primary keys.  This is used when the "real" primary
     * key is not the only one that can be used.  For example, documentType has "documentTypeId"
     * as its primary key, but the "name" could also be used.
     * A List of Lists is returned because because there can be component keys:
     * Ex:
     * {name, date}
     * {department, personId}
     *
     * @param businessObjectInterfaceClass
     * @return List of List of Strings.
     */
    public List<List<String>> listAlternatePrimaryKeyFieldNames(Class businessObjectInterfaceClass);

    /**
     * This method gets the business object dictionary entry for the passed in externalizable business object class.
     *
     * @param businessObjectInterfaceClass
     * @return
     */
    public BusinessObjectEntry getExternalizableBusinessObjectDictionaryEntry(Class businessObjectInterfaceClass);

    /**
     * This method gets the externalizable business object, given its type and a map of primary keys and values
     *
     * @param businessObjectClass
     * @param fieldValues
     * @return
     */
    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass,
                                                                                      Map<String, Object> fieldValues);

    /**
     * This method gets the list of externalizable business objects, given its type and a map of primary keys and
     * values.
     *
     * @param businessObjectClass
     * @param fieldValues
     * @return
     */
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(
        Class<T> businessObjectClass, Map<String, Object> fieldValues);

    /**
     * This method gets the list of externalizable business objects for lookup, given its type and a map of primary keys
     * and values.
     *
     * @param <T>
     * @param businessObjectClass
     * @param fieldValues
     * @param unbounded
     * @return
     */
    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(
        Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded);

    /**
     * Returns a URL so that the inquiry framework may redirect a user to the appropriate (possibly
     * external) website at which to view inquiry information
     *
     * @param inquiryDataObjectClass - a {@link ExternalizableBusinessObject} managed by this module
     * @param parameters             - any inquiry parameters, and the primary key values of the inquiry class would be
     *                               in here
     * @return String URL where externalizable object information may be viewed
     */
    public String getExternalizableDataObjectInquiryUrl(Class<?> inquiryDataObjectClass, Properties parameters);

    /**
     * Returns a URL so that the lookup framework may redirect a user to the appropriate (possibly
     * external) website at which to the data for the object may be searched
     *
     * @param inquiryDataObjectClass - a {@link ExternalizableBusinessObject} managed by this module
     * @param parameters             - any parameters for the lookup call
     * @return String URL where externalizable object information may be searched
     */
    public String getExternalizableDataObjectLookupUrl(Class<?> inquiryDataObjectClass, Properties parameters);

    /**
     * This method returns a URL so that the inquiry framework may redirect a user to the appropriate (possibly
     * external) website
     * at which to view inquiry information.
     *
     * @param inquiryBusinessObjectClass a {@link ExternalizableBusinessObject} managed by this module
     * @param parameters                 any inquiry parameters, and the primary key values of the inquiryBusinessObjectClass would be
     *                                   in here
     * @return a URL where externalizable business object information may be viewed.
     * @deprecated legacy KNS call, replaced by {@link #getExternalizableDataObjectInquiryUrl(Class, java.util.Properties)}
     * in KRAD
     */
    @Deprecated
    public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass,
                                                            Map<String, String[]> parameters);

    /**
     * This method gets the lookup url for the given externalizable business object properties.
     *
     * @param parameters
     * @return
     * @deprecated legacy KNS call, replaced by {@link #getExternalizableDataObjectLookupUrl(Class, java.util.Properties)}
     * in KRAD
     */
    @Deprecated
    public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass,
                                                           Map<String, String> parameters);

    /**
     * This method retrieves the externalizable business object, if it is not already populated
     * with the matching primary key values.
     *
     * @param businessObject
     * @param currentInstanceExternalizableBO
     * @param externalizableRelationshipName
     * @return
     */
    public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(
        BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName);

    /**
     * This method retrieves a list of externalizable business objects given a business object,
     * name of the relationship between the business object and the externalizable business object, and
     * the externalizable business object class.
     *
     * @param businessObject
     * @param externalizableRelationshipName
     * @param externalizableClazz
     * @return
     */
    public <T extends ExternalizableBusinessObject> List<T> retrieveExternalizableBusinessObjectsList(
        BusinessObject businessObject, String externalizableRelationshipName, Class<T> externalizableClazz);

    /**
     * This method determines whether or not a bo class is externalizable.
     *
     * @param boClass
     * @return
     */
    public boolean isExternalizable(Class boClass);

    /**
     * @param boClass
     * @return
     */
    public boolean isExternalizableBusinessObjectLookupable(Class boClass);

    /**
     * @param boClass
     * @return
     */
    public boolean isExternalizableBusinessObjectInquirable(Class boClass);

    /**
     * Determines whether an EBO's implementation is external.
     *
     * @param boClass
     * @return
     */
    public boolean isExternal(Class boClass);

    /**
     * @param <T>
     * @param boClass
     * @return
     */
    public <T extends ExternalizableBusinessObject> T createNewObjectFromExternalizableClass(Class<T> boClass);

    /**
     * For a given ExternalizableBusinessObject interface, return the implementation class provided by this module.
     */
    public <E extends ExternalizableBusinessObject> Class<E> getExternalizableBusinessObjectImplementation(
        Class<E> externalizableBusinessObjectInterface);

    /**
     * This method determines whether or not this module is currently locked
     */
    public boolean isLocked();

    /**
     * This method determines whether or not the central rice server should be used for lookups.
     *
     * @return
     */
    public boolean goToCentralRiceForInquiry();
}

