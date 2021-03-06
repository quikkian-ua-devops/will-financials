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
package org.kuali.kfs.coa.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.SubObjectTrickleDownInactivationService;
import org.kuali.kfs.kns.maintenance.Maintainable;
import org.kuali.kfs.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.dao.MaintenanceDocumentDao;
import org.kuali.kfs.krad.maintenance.MaintenanceLock;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentHeaderService;
import org.kuali.kfs.krad.service.NoteService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Transactional
public class SubObjectTrickleDownInactivationServiceImpl implements SubObjectTrickleDownInactivationService {

    private static final int NO_OF_SUB_OBJECTS_PER_NOTE = 15;

    private static final Logger LOG = Logger.getLogger(SubObjectTrickleDownInactivationServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    protected MaintenanceDocumentDao maintenanceDocumentDao;
    protected NoteService noteService;
    protected ConfigurationService kualiConfigurationService;
    protected UniversityDateService universityDateService;
    protected DocumentHeaderService documentHeaderService;

    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(Account inactivatedAccount, String documentNumber) {
        Collection<SubObjectCode> subObjects = getAssociatedSubObjects(inactivatedAccount);
        List<MaintenanceLock> maintenanceLocks = generateTrickleDownMaintenanceLocks(subObjects, documentNumber);
        return maintenanceLocks;
    }

    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(ObjectCode inactivatedObjectCode, String documentNumber) {
        Collection<SubObjectCode> subObjects = getAssociatedSubObjects(inactivatedObjectCode);
        List<MaintenanceLock> maintenanceLocks = generateTrickleDownMaintenanceLocks(subObjects, documentNumber);
        return maintenanceLocks;
    }

    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(Collection<SubObjectCode> subObjects, String documentNumber) {
        Maintainable subObjectMaintainable = getSubObjectMaintainable(documentNumber);
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        for (SubObjectCode subObjCd : subObjects) {
            subObjectMaintainable.setBusinessObject(subObjCd);
            maintenanceLocks.addAll(subObjectMaintainable.generateMaintenanceLocks());
        }
        return maintenanceLocks;
    }

    protected class TrickleDownInactivationStatus {
        public List<SubObjectCode> inactivatedSubObjCds;
        public Map<SubObjectCode, String> alreadyLockedSubObjCds;
        public List<SubObjectCode> errorPersistingSubObjCds;

        public TrickleDownInactivationStatus() {
            inactivatedSubObjCds = new ArrayList<SubObjectCode>();
            alreadyLockedSubObjCds = new HashMap<SubObjectCode, String>();
            errorPersistingSubObjCds = new ArrayList<SubObjectCode>();
        }
    }

    public void trickleDownInactivateSubObjects(Account inactivatedAccount, String documentNumber) {
        Collection<SubObjectCode> subObjects = getAssociatedSubObjects(inactivatedAccount);
        TrickleDownInactivationStatus trickleDownInactivationStatus = trickleDownInactivate(subObjects, documentNumber);
        addNotesToDocument(trickleDownInactivationStatus, documentNumber);
    }

    public void trickleDownInactivateSubObjects(ObjectCode inactivatedObject, String documentNumber) {
        Collection<SubObjectCode> subObjects = getAssociatedSubObjects(inactivatedObject);
        TrickleDownInactivationStatus trickleDownInactivationStatus = trickleDownInactivate(subObjects, documentNumber);
        addNotesToDocument(trickleDownInactivationStatus, documentNumber);
    }

    protected TrickleDownInactivationStatus trickleDownInactivate(Collection<SubObjectCode> subObjects, String documentNumber) {
        TrickleDownInactivationStatus trickleDownInactivationStatus = new TrickleDownInactivationStatus();

        if (subObjects != null && !subObjects.isEmpty()) {
            Maintainable subObjectMaintainable = getSubObjectMaintainable(documentNumber);
            for (Iterator<SubObjectCode> i = subObjects.iterator(); i.hasNext(); ) {
                SubObjectCode subObjCd = i.next();
                if (subObjCd.isActive()) {
                    subObjectMaintainable.setBusinessObject(subObjCd);
                    List<MaintenanceLock> subAccountLocks = subObjectMaintainable.generateMaintenanceLocks();

                    MaintenanceLock failedLock = verifyAllLocksFromThisDocument(subAccountLocks, documentNumber);
                    if (failedLock != null) {
                        // another document has locked this sub account, so we don't try to inactivate the account
                        trickleDownInactivationStatus.alreadyLockedSubObjCds.put(subObjCd, failedLock.getDocumentNumber());
                    } else {
                        // no locks other than our own (but there may have been no locks at all), just go ahead and try to update
                        subObjCd.setActive(false);

                        try {
                            subObjectMaintainable.saveBusinessObject();
                            trickleDownInactivationStatus.inactivatedSubObjCds.add(subObjCd);
                        } catch (RuntimeException e) {
                            LOG.error("Unable to trickle-down inactivate sub-account " + subObjCd.toString(), e);
                            trickleDownInactivationStatus.errorPersistingSubObjCds.add(subObjCd);
                        }
                    }
                }
            }
        }

        return trickleDownInactivationStatus;
    }

    protected void addNotesToDocument(TrickleDownInactivationStatus trickleDownInactivationStatus, String documentNumber) {
        if (trickleDownInactivationStatus.inactivatedSubObjCds.isEmpty() && trickleDownInactivationStatus.alreadyLockedSubObjCds.isEmpty() && trickleDownInactivationStatus.errorPersistingSubObjCds.isEmpty()) {
            // if we didn't try to inactivate any sub-objects, then don't bother
            return;
        }
        DocumentHeader noteParent = documentHeaderService.getDocumentHeaderById(documentNumber);
        Note newNote = new Note();

        addNotes(documentNumber, trickleDownInactivationStatus.inactivatedSubObjCds, KFSKeyConstants.SUB_OBJECT_TRICKLE_DOWN_INACTIVATION, noteParent, newNote);
        addNotes(documentNumber, trickleDownInactivationStatus.errorPersistingSubObjCds, KFSKeyConstants.SUB_OBJECT_TRICKLE_DOWN_INACTIVATION_ERROR_DURING_PERSISTENCE, noteParent, newNote);
        addMaintenanceLockedNotes(documentNumber, trickleDownInactivationStatus.alreadyLockedSubObjCds, KFSKeyConstants.SUB_OBJECT_TRICKLE_DOWN_INACTIVATION_RECORD_ALREADY_MAINTENANCE_LOCKED, noteParent, newNote);
    }

    protected MaintenanceLock verifyAllLocksFromThisDocument(List<MaintenanceLock> maintenanceLocks, String documentNumber) {
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            String lockingDocNumber = maintenanceDocumentDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(), documentNumber);
            if (StringUtils.isNotBlank(lockingDocNumber)) {
                return maintenanceLock;
            }
        }
        return null;
    }

    protected Maintainable getSubObjectMaintainable(String documentNumber) {
        Maintainable subObjectMaintainable;
        try {
            subObjectMaintainable = (Maintainable) maintenanceDocumentDictionaryService.getMaintainableClass(SubObjectCode.class.getName()).newInstance();
            subObjectMaintainable.setBoClass(SubObjectCode.class);
            subObjectMaintainable.setDocumentNumber(documentNumber);
        } catch (Exception e) {
            LOG.error("Unable to instantiate SubObject Maintainable", e);
            throw new RuntimeException("Unable to instantiate SubObject Maintainable", e);
        }
        return subObjectMaintainable;
    }

    protected Collection<SubObjectCode> getAssociatedSubObjects(Account account) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityDateService.getCurrentFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        return businessObjectService.findMatching(SubObjectCode.class, fieldValues);
    }

    protected Collection<SubObjectCode> getAssociatedSubObjects(ObjectCode objectCode) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, objectCode.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        return businessObjectService.findMatching(SubObjectCode.class, fieldValues);
    }

    protected void addNotes(String documentNumber, List<SubObjectCode> listOfSubObjects, String messageKey, PersistableBusinessObject noteParent, Note noteTemplate) {
        for (int i = 0; i < listOfSubObjects.size(); i += getNumSubObjectsPerNote()) {
            try {
                String subAccountString = createSubObjectChunk(listOfSubObjects, i, i + getNumSubObjectsPerNote());
                if (StringUtils.isNotBlank(subAccountString)) {
                    String noteTextTemplate = kualiConfigurationService.getPropertyValueAsString(messageKey);
                    String noteText = MessageFormat.format(noteTextTemplate, subAccountString);
                    Note note = noteService.createNote(noteTemplate, noteParent, GlobalVariables.getUserSession().getPrincipalId());
                    note.setNoteText(noteText);
                    note.setNotePostedTimestampToCurrent();
                    noteService.save(note);
                }
            } catch (Exception e) {
                LOG.error("Unable to create/save notes for document " + documentNumber, e);
                throw new RuntimeException("Unable to create/save notes for document " + documentNumber, e);
            }
        }
    }

    protected void addMaintenanceLockedNotes(String documentNumber, Map<SubObjectCode, String> lockedSubObjects, String messageKey, PersistableBusinessObject noteParent, Note noteTemplate) {
        for (Map.Entry<SubObjectCode, String> entry : lockedSubObjects.entrySet()) {
            try {
                SubObjectCode subObjCd = entry.getKey();
                String subObjectString = subObjCd.getUniversityFiscalYear() + " - " + subObjCd.getChartOfAccountsCode() + " - " + subObjCd.getAccountNumber() + " - " + subObjCd.getFinancialObjectCode() + " - " + subObjCd.getFinancialSubObjectCode();
                if (StringUtils.isNotBlank(subObjectString)) {
                    String noteTextTemplate = kualiConfigurationService.getPropertyValueAsString(messageKey);
                    String noteText = MessageFormat.format(noteTextTemplate, subObjectString, entry.getValue());
                    Note note = noteService.createNote(noteTemplate, noteParent, GlobalVariables.getUserSession().getPrincipalId());
                    note.setNoteText(noteText);
                    note.setNotePostedTimestampToCurrent();
                    noteService.save(note);
                }
            } catch (Exception e) {
                LOG.error("Unable to create/save notes for document " + documentNumber, e);
                throw new RuntimeException("Unable to create/save notes for document " + documentNumber, e);
            }
        }
    }

    protected String createSubObjectChunk(List<SubObjectCode> listOfSubObjects, int startIndex, int endIndex) {
        StringBuilder buf = new StringBuilder();
        for (int i = startIndex; i < endIndex && i < listOfSubObjects.size(); i++) {
            SubObjectCode subObjCd = listOfSubObjects.get(i);
            buf.append(subObjCd.getUniversityFiscalYear()).append(" - ").append(subObjCd.getChartOfAccountsCode()).append(" - ")
                .append(subObjCd.getAccountNumber()).append(" - ").append(subObjCd.getFinancialObjectCode())
                .append(" - ").append(subObjCd.getFinancialSubObjectCode());
            if (i + 1 < endIndex && i + 1 < listOfSubObjects.size()) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }

    protected int getNumSubObjectsPerNote() {
        //Account Document in Exception reduced the no of sub objects
        //per note from 20 to 15 to reduce the note text length
        return NO_OF_SUB_OBJECTS_PER_NOTE;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    public void setMaintenanceDocumentDao(MaintenanceDocumentDao maintenanceDocumentDao) {
        this.maintenanceDocumentDao = maintenanceDocumentDao;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setDocumentHeaderService(DocumentHeaderService documentHeaderService) {
        this.documentHeaderService = documentHeaderService;
    }
}
