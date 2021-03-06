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
package org.kuali.kfs.krad.datadictionary;

import java.util.List;


/**
 * This is a description of what this class does - mpham don't forget to fill this in.
 */
public class SearchingTypeDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = -8779609937539520677L;

    private SearchingAttribute searchingAttribute;
    private List<String> paths;

    /**
     * @return the searchingAttribute
     */
    public SearchingAttribute getSearchingAttribute() {
        return this.searchingAttribute;
    }

    /**
     * @return the documentValues
     */
    public List<String> getDocumentValues() {
        return this.paths;
    }

    /**
     * @param searchingAttribute the searchingAttribute to set
     */
    public void setSearchingAttribute(SearchingAttribute searchingAttribute) {
        this.searchingAttribute = searchingAttribute;
    }

    /**
     * @param documentValues the documentValues to set
     */
    public void setDocumentValues(List<String> paths) {
        this.paths = paths;
    }

    /**
     * This overridden method ...
     *
     * @see DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Class)
     */
    public void completeValidation(Class rootBusinessObjectClass,
                                   Class otherBusinessObjectClass) {

    }
}
