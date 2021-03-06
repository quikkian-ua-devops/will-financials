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
package org.kuali.kfs.krad.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.UserSessionUtils;
import org.kuali.kfs.krad.bo.SessionDocument;
import org.kuali.kfs.krad.dao.SessionDocumentDao;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocatorWeb;
import org.kuali.kfs.krad.service.SessionDocumentService;
import org.kuali.kfs.krad.web.form.DocumentFormBase;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Implementation of <code>SessionDocumentService</code> that persists the document form
 * contents to the underlying database
 */
@Transactional
public class SessionDocumentServiceImpl implements SessionDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SessionDocumentServiceImpl.class);

    protected static final String IP_ADDRESS = "ipAddress";
    protected static final String PRINCIPAL_ID = "principalId";
    protected static final String DOCUMENT_NUMBER = "documentNumber";
    protected static final String SESSION_ID = "sessionId";

    private EncryptionService encryptionService;

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private SessionDocumentDao sessionDocumentDao;

    @Override
    public DocumentFormBase getDocumentForm(String documentNumber, String docFormKey, UserSession userSession,
                                            String ipAddress) {
        DocumentFormBase documentForm = null;

        LOG.debug("getDocumentForm DocumentFormBase from db");
        try {
            // re-create the DocumentFormBase object
            documentForm = (DocumentFormBase) retrieveDocumentForm(userSession, docFormKey, documentNumber, ipAddress);

            //re-store workFlowDocument into session
            WorkflowDocument workflowDocument =
                documentForm.getDocument().getDocumentHeader().getWorkflowDocument();
            UserSessionUtils.addWorkflowDocument(userSession, workflowDocument);
        } catch (Exception e) {
            LOG.error("getDocumentForm failed for SessId/DocNum/PrinId/IP:" + userSession.getKualiSessionId() + "/" +
                documentNumber + "/" + userSession.getPrincipalId() + "/" + ipAddress, e);
        }

        return documentForm;
    }

    protected Object retrieveDocumentForm(UserSession userSession, String sessionId, String documentNumber,
                                          String ipAddress) throws Exception {
        HashMap<String, String> primaryKeys = new HashMap<String, String>(4);
        primaryKeys.put(SESSION_ID, sessionId);
        if (documentNumber != null) {
            primaryKeys.put(DOCUMENT_NUMBER, documentNumber);
        }
        primaryKeys.put(PRINCIPAL_ID, userSession.getPrincipalId());
        primaryKeys.put(IP_ADDRESS, ipAddress);

        SessionDocument sessionDoc = getBusinessObjectService().findByPrimaryKey(SessionDocument.class, primaryKeys);
        if (sessionDoc != null) {
            byte[] formAsBytes = sessionDoc.getSerializedDocumentForm();
            if (sessionDoc.isEncrypted()) {
                formAsBytes = getEncryptionService().decryptBytes(formAsBytes);
            }
            ByteArrayInputStream baip = new ByteArrayInputStream(formAsBytes);
            ObjectInputStream ois = new ObjectInputStream(baip);

            return ois.readObject();
        }

        return null;
    }

    @Override
    public WorkflowDocument getDocumentFromSession(UserSession userSession, String docId) {
        return UserSessionUtils.getWorkflowDocument(userSession, docId);
    }

    /**
     * @see SessionDocumentService#addDocumentToUserSession(UserSession,
     * org.kuali.rice.kew.api.WorkflowDocument)
     */
    @Override
    public void addDocumentToUserSession(UserSession userSession, WorkflowDocument document) {
        UserSessionUtils.addWorkflowDocument(userSession, document);
    }

    /**
     * @see SessionDocumentService#purgeDocumentForm(String, String,
     * UserSession, String)
     */
    @Override
    public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress) {
        synchronized (userSession) {

            LOG.debug("purge document form from session");
            userSession.removeObject(docFormKey);
            try {
                LOG.debug("purge document form from database");
                HashMap<String, String> primaryKeys = new HashMap<String, String>(4);
                primaryKeys.put(SESSION_ID, userSession.getKualiSessionId());
                primaryKeys.put(DOCUMENT_NUMBER, documentNumber);
                primaryKeys.put(PRINCIPAL_ID, userSession.getPrincipalId());
                primaryKeys.put(IP_ADDRESS, ipAddress);
                getBusinessObjectService().deleteMatching(SessionDocument.class, primaryKeys);
            } catch (Exception e) {
                LOG.error("purgeDocumentForm failed for SessId/DocNum/PrinId/IP:" + userSession.getKualiSessionId() +
                    "/" + documentNumber + "/" + userSession.getPrincipalId() + "/" + ipAddress, e);
            }
        }
    }

    @Override
    public void setDocumentForm(DocumentFormBase form, UserSession userSession, String ipAddress) {
        synchronized (userSession) {
            //formKey was set in KualiDocumentActionBase execute method
            String formKey = form.getFormKey();
            String key = userSession.getKualiSessionId() + "-" + formKey;

            String documentNumber = form.getDocument().getDocumentNumber();
            if (StringUtils.isNotBlank(formKey)) {
                //FIXME: Currently using formKey for sessionId
                persistDocumentForm(form, userSession, ipAddress, formKey, documentNumber);
            } else {
                LOG.warn("documentNumber is null on form's document: " + form);
            }
        }
    }

    protected void persistDocumentForm(DocumentFormBase form, UserSession userSession, String ipAddress,
                                       String sessionId, String documentNumber) {
        try {
            LOG.debug("set Document Form into database");

            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(form);

            // serialize the DocumentFormBase object into a byte array
            byte[] formAsBytes = baos.toByteArray();
            boolean encryptContent = false;
            DocumentEntry documentEntry =
                getDataDictionaryService().getDataDictionary().getDocumentEntry(form.getDocTypeName());
            if (documentEntry != null) {
                encryptContent = documentEntry.isEncryptDocumentDataInPersistentSessionStorage();
            }

            if (encryptContent) {
                formAsBytes = getEncryptionService().encryptBytes(formAsBytes);
            }

            // check if a record is already there in the database
            // this may only happen under jMeter testing, but there is no way to be sure
            HashMap<String, String> primaryKeys = new HashMap<String, String>(4);
            primaryKeys.put(SESSION_ID, sessionId);
            primaryKeys.put(DOCUMENT_NUMBER, documentNumber);
            primaryKeys.put(PRINCIPAL_ID, userSession.getPrincipalId());
            primaryKeys.put(IP_ADDRESS, ipAddress);

            SessionDocument sessionDocument =
                getBusinessObjectService().findByPrimaryKey(SessionDocument.class, primaryKeys);
            if (sessionDocument == null) {
                sessionDocument = new SessionDocument();
                sessionDocument.setSessionId(sessionId);
                sessionDocument.setDocumentNumber(documentNumber);
                sessionDocument.setPrincipalId(userSession.getPrincipalId());
                sessionDocument.setIpAddress(ipAddress);
            }
            sessionDocument.setSerializedDocumentForm(formAsBytes);
            sessionDocument.setEncrypted(encryptContent);
            sessionDocument.setLastUpdatedDate(currentTime);

            businessObjectService.save(sessionDocument);
        } catch (Exception e) {
            final String className = form != null ? form.getClass().getName() : "null";
            LOG.error("setDocumentForm failed for SessId/DocNum/PrinId/IP/class:" + userSession.getKualiSessionId() +
                "/" + documentNumber + "/" + userSession.getPrincipalId() + "/" + ipAddress + "/" + className, e);
        }
    }

    /**
     * @see SessionDocumentService#purgeAllSessionDocuments(java.sql.Timestamp)
     */
    @Override
    public void purgeAllSessionDocuments(Timestamp expirationDate) {
        sessionDocumentDao.purgeAllSessionDocuments(expirationDate);
    }

    protected SessionDocumentDao getSessionDocumentDao() {
        return this.sessionDocumentDao;
    }

    public void setSessionDocumentDao(SessionDocumentDao sessionDocumentDao) {
        this.sessionDocumentDao = sessionDocumentDao;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return this.businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected EncryptionService getEncryptionService() {
        if (encryptionService == null) {
            encryptionService = CoreApiServiceLocator.getEncryptionService();
        }
        return encryptionService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }
}
