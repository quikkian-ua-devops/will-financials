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
package org.kuali.kfs.krad.workflow.attribute;

import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.document.DocumentWithContent;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.framework.document.attribute.SearchableAttribute;
import org.kuali.rice.kew.framework.document.search.DocumentSearchCustomizer;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultSetConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues;
import org.kuali.rice.kew.framework.document.search.NullDocumentSearchCustomizer;

import java.util.List;

public class DataDictionaryDocumentSearchCustomizer implements SearchableAttribute, DocumentSearchCustomizer {

    private SearchableAttribute searchableAttribute;
    private DocumentSearchCustomizer documentSearchCustomizer;

    public DataDictionaryDocumentSearchCustomizer() {
        this(new DataDictionarySearchableAttribute(), new NullDocumentSearchCustomizer());
    }

    public DataDictionaryDocumentSearchCustomizer(SearchableAttribute searchableAttribute,
                                                  DocumentSearchCustomizer documentSearchCustomizer) {
        this.searchableAttribute = searchableAttribute;
        this.documentSearchCustomizer = documentSearchCustomizer;
    }

    @Override
    public final String generateSearchContent(ExtensionDefinition extensionDefinition,
                                              String documentTypeName,
                                              WorkflowAttributeDefinition attributeDefinition) {
        return getSearchableAttribute().generateSearchContent(extensionDefinition, documentTypeName,
            attributeDefinition);
    }

    @Override
    public final List<DocumentAttribute> extractDocumentAttributes(ExtensionDefinition extensionDefinition,
                                                                   DocumentWithContent documentWithContent) {
        return getSearchableAttribute().extractDocumentAttributes(extensionDefinition, documentWithContent);
    }

    @Override
    public final List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition,
                                                               String documentTypeName) {
        return getSearchableAttribute().getSearchFields(extensionDefinition, documentTypeName);
    }

    @Override
    public final List<RemotableAttributeError> validateDocumentAttributeCriteria(ExtensionDefinition extensionDefinition,
                                                                                 DocumentSearchCriteria documentSearchCriteria) {
        return getSearchableAttribute().validateDocumentAttributeCriteria(extensionDefinition, documentSearchCriteria);
    }

    @Override
    public final DocumentSearchCriteria customizeCriteria(DocumentSearchCriteria documentSearchCriteria) {
        return getDocumentSearchCustomizer().customizeCriteria(documentSearchCriteria);
    }

    @Override
    public final DocumentSearchCriteria customizeClearCriteria(DocumentSearchCriteria documentSearchCriteria) {
        return getDocumentSearchCustomizer().customizeClearCriteria(documentSearchCriteria);
    }

    @Override
    public final DocumentSearchResultValues customizeResults(DocumentSearchCriteria documentSearchCriteria,
                                                             List<DocumentSearchResult> defaultResults) {
        return getDocumentSearchCustomizer().customizeResults(documentSearchCriteria, defaultResults);
    }

    @Override
    public DocumentSearchResultSetConfiguration customizeResultSetConfiguration(
        DocumentSearchCriteria documentSearchCriteria) {
        return getDocumentSearchCustomizer().customizeResultSetConfiguration(documentSearchCriteria);
    }

    @Override
    public final boolean isCustomizeCriteriaEnabled(String documentTypeName) {
        return getDocumentSearchCustomizer().isCustomizeCriteriaEnabled(documentTypeName);
    }

    @Override
    public final boolean isCustomizeClearCriteriaEnabled(String documentTypeName) {
        return getDocumentSearchCustomizer().isCustomizeClearCriteriaEnabled(documentTypeName);
    }

    @Override
    public final boolean isCustomizeResultsEnabled(String documentTypeName) {
        return getDocumentSearchCustomizer().isCustomizeResultsEnabled(documentTypeName);
    }

    @Override
    public final boolean isCustomizeResultSetFieldsEnabled(String documentTypeName) {
        return getDocumentSearchCustomizer().isCustomizeResultSetFieldsEnabled(documentTypeName);
    }

    protected SearchableAttribute getSearchableAttribute() {
        return this.searchableAttribute;
    }

    public void setSearchableAttribute(SearchableAttribute searchableAttribute) {
        this.searchableAttribute = searchableAttribute;
    }

    protected DocumentSearchCustomizer getDocumentSearchCustomizer() {
        return this.documentSearchCustomizer;
    }

    public void setDocumentSearchCustomizer(DocumentSearchCustomizer documentSearchCustomizer) {
        this.documentSearchCustomizer = documentSearchCustomizer;
    }


}
