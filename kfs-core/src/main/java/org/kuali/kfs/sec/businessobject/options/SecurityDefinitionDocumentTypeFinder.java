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
package org.kuali.kfs.sec.businessobject.options;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.keyvalues.KeyValuesBase;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Returns list of valid document type names for security definition
 */
public class SecurityDefinitionDocumentTypeFinder extends KeyValuesBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityDefinitionDocumentTypeFinder.class);

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> activeLabels = new ArrayList<KeyValue>();

        // add option to include all document types
        activeLabels.add(new ConcreteKeyValue(SecConstants.ALL_DOCUMENT_TYPE_NAME, SecConstants.ALL_DOCUMENT_TYPE_NAME));

        Collection<String> documentTypes = SpringContext.getBean(ParameterService.class).getParameterValuesAsString(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ACCESS_SECURITY_DOCUMENT_TYPES);

        // copy list so it can be sorted (since it is unmodifiable)
        List<String> sortedDocumentTypes = new ArrayList<String>(documentTypes);
        Collections.sort(sortedDocumentTypes);

        for (String documentTypeName : sortedDocumentTypes) {
            DocumentType documentType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeName);

            if (documentType != null) {
                activeLabels.add(new ConcreteKeyValue(documentTypeName, documentType.getLabel()));
            } else {
                LOG.warn("Unknown document type in " + SecConstants.SecurityParameterNames.ACCESS_SECURITY_DOCUMENT_TYPES + " parameter: " + documentTypeName);
            }
        }

        return activeLabels;
    }
}
