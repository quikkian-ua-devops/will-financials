/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.document.DocumentAuthorizerBase;
import org.kuali.kfs.krad.document.DocumentPresentationController;
import org.kuali.kfs.krad.document.DocumentPresentationControllerBase;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.kfs.krad.maintenance.MaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.krad.maintenance.MaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;

/**
 * Implementation of <code>DocumentDictionaryService</code> which reads configuration
 * from the data dictionary
 *
 * 
 */
public class DocumentDictionaryServiceImpl implements DocumentDictionaryService {
    private DataDictionaryService dataDictionaryService;

    /**
     * @see DocumentDictionaryService#getLabel
     */
    @Override
    public String getLabel(String documentTypeName) {
        String label = null;

        DocumentType docType = getDocumentType(documentTypeName);
        if (docType != null) {
            label = docType.getLabel();
        }

        return label;
    }

    /**
     * @see DocumentDictionaryService#getMaintenanceDocumentTypeName
     */
    @Override
    public String getMaintenanceDocumentTypeName(Class dataObjectClass) {
        String documentTypeName = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(dataObjectClass);
        if (entry != null) {
            documentTypeName = entry.getDocumentTypeName();
        }

        return documentTypeName;
    }

    /**
     * @see DocumentDictionaryService#getDescription
     */
    @Override
    public String getDescription(String documentTypeName) {
        String description = null;

        DocumentType docType = getDocumentType(documentTypeName);
        if (docType != null) {
            description = docType.getDescription();
        }

        return description;
    }

    /**
     * @see DocumentDictionaryService#getDefaultExistenceChecks
     */
    @Override
    public Collection getDefaultExistenceChecks(Class dataObjectClass) {
        return getDefaultExistenceChecks(getMaintenanceDocumentTypeName(dataObjectClass));
    }

    /**
     * @see DocumentDictionaryService#getDefaultExistenceChecks
     */
    @Override
    public Collection getDefaultExistenceChecks(Document document) {
        return getDefaultExistenceChecks(getDocumentEntry(document).getDocumentTypeName());
    }

    /**
     * @see DocumentDictionaryService#getDefaultExistenceChecks
     */
    @Override
    public Collection getDefaultExistenceChecks(String docTypeName) {
        Collection defaultExistenceChecks = null;

        DocumentEntry entry = getDocumentEntry(docTypeName);
        if (entry != null) {
            defaultExistenceChecks = entry.getDefaultExistenceChecks();
        }

        return defaultExistenceChecks;
    }

    /**
     * @see DocumentDictionaryService#getMaintenanceDataObjectClass
     */
    @Override
    public Class<?> getMaintenanceDataObjectClass(String docTypeName) {
        Class dataObjectClass = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            dataObjectClass = entry.getDataObjectClass();
        }

        return dataObjectClass;
    }

    /**
     * @see org.kuali.rice.krad.service.impl.DocumentDictionaryService#getMaintainableClass
     */
    @Override
    public Class<? extends Maintainable> getMaintainableClass(String docTypeName) {
        Class maintainableClass = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            maintainableClass = entry.getMaintainableClass();
        }

        return maintainableClass;
    }

    /**
     * @see DocumentDictionaryService#getBusinessRulesClass
     */
    @Override
    public Class<? extends BusinessRule> getBusinessRulesClass(Document document) {
        Class<? extends BusinessRule> businessRulesClass = null;

        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        DocumentEntry entry = getDocumentEntry(docTypeName);
        if (entry != null) {
            businessRulesClass = entry.getBusinessRulesClass();
        }

        return businessRulesClass;
    }

    /**
     * @see DocumentDictionaryService#getAllowsCopy
     */
    @Override
    public Boolean getAllowsCopy(Document document) {
        Boolean allowsCopy = Boolean.FALSE;

        if (document == null) {
            return allowsCopy;
        }

        DocumentEntry entry = null;
        if (document instanceof MaintenanceDocument) {
            MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
            if (maintenanceDocument.getNewMaintainableObject() != null) {
                entry = getMaintenanceDocumentEntry(
                        maintenanceDocument.getNewMaintainableObject().getDataObjectClass());
            }
        } else {
            entry = getDocumentEntry(document);
        }

        if (entry != null) {
            allowsCopy = Boolean.valueOf(entry.getAllowsCopy());
        }

        return allowsCopy;
    }

    /**
     * @see DocumentDictionaryService#getAllowsNewOrCopy
     */
    @Override
    public Boolean getAllowsNewOrCopy(String docTypeName) {
        Boolean allowsNewOrCopy = Boolean.FALSE;

        if (docTypeName != null) {
            MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
            if (entry != null) {
                allowsNewOrCopy = Boolean.valueOf(entry.getAllowsNewOrCopy());
            }
        }

        return allowsNewOrCopy;
    }

    /**
     * @see DocumentDictionaryService#getDocumentEntry(java.lang.String)
     */
    @Override
    public DocumentEntry getDocumentEntry(String documentTypeName) {
        if (documentTypeName == null) {
            throw new IllegalArgumentException("invalid (null) document type name");
        }

        DocumentEntry entry = getDataDictionary().getDocumentEntry(documentTypeName);

        return entry;
    }

    /**
     * @see DocumentDictionaryService#getDocumentEntryByClass(java.lang.Class<? extends
     *      Document >)
     */
    @Override
    public DocumentEntry getDocumentEntryByClass(Class<? extends Document> documentClass) {
        DocumentEntry entry = null;

        String documentTypeName = getDocumentTypeByClass(documentClass);
        if (StringUtils.isNotBlank(documentTypeName)) {
            entry = getDocumentEntry(documentTypeName);
        }

        return entry;
    }

    /**
     * @see DocumentDictionaryService#getMaintenanceDocumentEntry
     */
    @Override
    public MaintenanceDocumentEntry getMaintenanceDocumentEntry(String docTypeName) {
        if (StringUtils.isBlank(docTypeName)) {
            throw new IllegalArgumentException("invalid (blank) docTypeName");
        }

        MaintenanceDocumentEntry entry = (MaintenanceDocumentEntry) getDataDictionary().getDocumentEntry(docTypeName);
        return entry;
    }

    /**
     * @see DocumentDictionaryService#getDocumentClassByName
     */
    @Override
    public Class<?> getDocumentClassByName(String documentTypeName) {
        Class documentClass = null;

        DocumentEntry entry = getDocumentEntry(documentTypeName);
        if (entry != null) {
            documentClass = entry.getDocumentClass();
        }

        return documentClass;
    }

    /**
     * @see DocumentDictionaryService#getDocumentTypeByClass(java.lang.Class<? extends Document >)
     */
    @Override
    public String getDocumentTypeByClass(Class<? extends Document> documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) document class");
        }

        DocumentEntry entry = getDataDictionary().getDocumentEntry(documentClass.getName());
        if (entry != null) {
            return entry.getDocumentTypeName();
        }

        return null;
    }

    /**
     * @see DocumentDictionaryService#getAllowsRecordDeletion
     */
    @Override
    public Boolean getAllowsRecordDeletion(Class dataObjectClass) {
        Boolean allowsRecordDeletion = Boolean.FALSE;

        MaintenanceDocumentEntry docEntry = getMaintenanceDocumentEntry(dataObjectClass);

        if (docEntry != null) {
            allowsRecordDeletion = Boolean.valueOf(docEntry.getAllowsRecordDeletion());
        }

        return allowsRecordDeletion;
    }

    /**
     * @see DocumentDictionaryService#getAllowsRecordDeletion
     */
    @Override
    public Boolean getAllowsRecordDeletion(MaintenanceDocument document) {
        return document != null ?
                this.getAllowsRecordDeletion(document.getNewMaintainableObject().getDataObjectClass()) : Boolean.FALSE;
    }

    /**
     * @see DocumentDictionaryService#getLockingKeys
     */
    @Override
    public List<String> getLockingKeys(String docTypeName) {
        List lockingKeys = null;

        MaintenanceDocumentEntry entry = getMaintenanceDocumentEntry(docTypeName);
        if (entry != null) {
            lockingKeys = entry.getLockingKeyFieldNames();
        }

        return lockingKeys;
    }

    /**
     * @see DocumentDictionaryService#getPreserveLockingKeysOnCopy
     */
    @Override
    public boolean getPreserveLockingKeysOnCopy(Class dataObjectClass) {
        boolean preserveLockingKeysOnCopy = false;

        MaintenanceDocumentEntry docEntry = getMaintenanceDocumentEntry(dataObjectClass);

        if (docEntry != null) {
            preserveLockingKeysOnCopy = docEntry.getPreserveLockingKeysOnCopy();
        }

        return preserveLockingKeysOnCopy;
    }

    /**
     * @see DocumentDictionaryService#getDocumentAuthorizer(java.lang.String)
     */
    public DocumentAuthorizer getDocumentAuthorizer(String documentType) {
        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();

        if (StringUtils.isBlank(documentType)) {
            throw new IllegalArgumentException("invalid (blank) documentType");
        }

        DocumentEntry documentEntry = dataDictionary.getDocumentEntry(documentType);
        if (documentEntry == null) {
            throw new IllegalArgumentException("unknown documentType '" + documentType + "'");
        }

        Class<? extends DocumentAuthorizer> documentAuthorizerClass = documentEntry.getDocumentAuthorizerClass();

        DocumentAuthorizer documentAuthorizer = null;
        try {
            if (documentAuthorizerClass != null) {
                documentAuthorizer = documentAuthorizerClass.newInstance();
            } else if (documentEntry instanceof MaintenanceDocumentEntry) {
                documentAuthorizer = new MaintenanceDocumentAuthorizerBase();
            } else {
                documentAuthorizer = new DocumentAuthorizerBase();
            }
        } catch (Exception e) {
            throw new RuntimeException("unable to instantiate documentAuthorizer '"
                    + documentAuthorizerClass.getName()
                    + "' for doctype '"
                    + documentType
                    + "'", e);
        }

        return documentAuthorizer;
    }

    /**
     * @see DocumentDictionaryService#getDocumentAuthorizer(java.lang.String)
     */
    public DocumentAuthorizer getDocumentAuthorizer(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        } else if (document.getDocumentHeader() == null) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader");
        } else if (!document.getDocumentHeader().hasWorkflowDocument()) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader.workflowDocument");
        }

        String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        DocumentAuthorizer documentAuthorizer = getDocumentAuthorizer(documentType);

        return documentAuthorizer;
    }

    /**
     * @see DocumentDictionaryService#getDocumentPresentationController(java.lang.String)
     */
    public DocumentPresentationController getDocumentPresentationController(String documentType) {
        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();

        if (StringUtils.isBlank(documentType)) {
            throw new IllegalArgumentException("invalid (blank) documentType");
        }

        DocumentEntry documentEntry = dataDictionary.getDocumentEntry(documentType);
        if (documentEntry == null) {
            throw new IllegalArgumentException("unknown documentType '" + documentType + "'");
        }

        Class<? extends DocumentPresentationController> documentPresentationControllerClass =
                documentEntry.getDocumentPresentationControllerClass();

        DocumentPresentationController documentPresentationController = null;
        try {
            if (documentPresentationControllerClass != null) {
                documentPresentationController = documentPresentationControllerClass.newInstance();
            } else if (documentEntry instanceof MaintenanceDocumentEntry) {
                documentPresentationController = new MaintenanceDocumentPresentationControllerBase();
            } else {
                documentPresentationController = new DocumentPresentationControllerBase();
            }
        } catch (Exception e) {
            throw new RuntimeException("unable to instantiate documentAuthorizer '"
                    + documentPresentationControllerClass.getName()
                    + "' for doctype '"
                    + documentType
                    + "'", e);
        }

        return documentPresentationController;
    }

    /**
     * @see DocumentDictionaryService#getDocumentPresentationController(java.lang.String)
     */
    public DocumentPresentationController getDocumentPresentationController(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        } else if (document.getDocumentHeader() == null) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader");
        } else if (!document.getDocumentHeader().hasWorkflowDocument()) {
            throw new IllegalArgumentException("invalid (null) document.documentHeader.workflowDocument");
        }

        String documentType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        DocumentPresentationController documentPresentationController = getDocumentPresentationController(documentType);

        return documentPresentationController;
    }

    /**
     * Retrieves the maintenance document entry associated with the given data object class
     *
     * @param dataObjectClass - data object class to retrieve maintenance document entry for
     * @return MaintenanceDocumentEntry for associated data object class
     */
    protected MaintenanceDocumentEntry getMaintenanceDocumentEntry(Class dataObjectClass) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("invalid (blank) dataObjectClass");
        }

        MaintenanceDocumentEntry entry =
                getDataDictionary().getMaintenanceDocumentEntryForBusinessObjectClass(dataObjectClass);
        return entry;
    }

    /**
     * Retrieves the document entry for the document type of the given document instance
     *
     * @param document - document instance to retrieve document entry for
     * @return DocumentEntry instance found for document type
     */
    protected DocumentEntry getDocumentEntry(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }

        DocumentEntry entry = getDataDictionary().getDocumentEntry(document.getClass().getName());

        return entry;
    }

    /**
     * Gets the workflow document type dto for the given documentTypeName
     *
     * @param documentTypeName - document type name to retrieve document type dto
     * @return DocumentType for given document type name
     */
    protected DocumentType getDocumentType(String documentTypeName) {
        return KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeName);
    }

    protected DataDictionary getDataDictionary() {
        return getDataDictionaryService().getDataDictionary();
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            this.dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
