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
package org.kuali.kfs.sys.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentAdHocService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentDao;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentHeaderDao;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is a Financial System specific Document Service class to allow for the
 * {@link #findByDocumentHeaderStatusCode(Class, String)} method.
 */
@Transactional
public class FinancialSystemDocumentServiceImpl implements FinancialSystemDocumentService {
    protected FinancialSystemDocumentHeaderDao financialSystemDocumentHeaderDao;
    protected FinancialSystemDocumentDao financialSystemDocumentDao;
    protected DocumentService documentService;
    protected ParameterService parameterService;
    protected DocumentAdHocService documentAdHocService;
    protected BusinessObjectService businessObjectService;

    private static final int DEFAULT_FETCH_MORE_ITERATION_LIMIT = 10;

    public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemDocumentServiceImpl.class);


    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByDocumentHeaderStatusCode(java.lang.Class,
     * java.lang.String)
     */
    @Override
    public <T extends Document> Collection<T> findByDocumentHeaderStatusCode(Class<T> clazz, String statusCode) throws WorkflowException {
        Collection<T> foundDocuments = getFinancialSystemDocumentDao().findByDocumentHeaderStatusCode(clazz, statusCode);
        for (Document doc : foundDocuments) {
            documentAdHocService.addAdHocs(doc);
        }

        Collection<T> returnDocuments = new ArrayList<T>();
        for (Iterator<T> iter = foundDocuments.iterator(); iter.hasNext(); ) {
            Document doc = iter.next();
            returnDocuments.add((T) getDocumentService().getByDocumentHeaderId(doc.getDocumentNumber()));
        }
        return returnDocuments;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByWorkflowStatusCode(java.lang.Class, org.kuali.rice.kew.api.document.DocumentStatus)
     */
    @Override
    public <T extends Document> Collection<T> findByWorkflowStatusCode(Class<T> clazz, DocumentStatus docStatus) throws WorkflowException {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, docStatus.getCode());
        final Collection<T> docsWithoutWorfklowHeaders = getBusinessObjectService().findMatching(clazz, fieldValues);
        List<T> results = new ArrayList<T>();
        for (T doc : docsWithoutWorfklowHeaders) {
            final T docWithWorkflowHeader = (T) getDocumentService().getByDocumentHeaderId(doc.getDocumentNumber());
            results.add(docWithWorkflowHeader);
        }
        return results;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByWorkflowStatusCode(org.kuali.rice.kew.api.document.DocumentStatus)
     */
    @Override
    public Collection<FinancialSystemDocumentHeader> findByWorkflowStatusCode(DocumentStatus docStatus) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, docStatus.getCode());

        return this.getBusinessObjectService().findMatching(FinancialSystemDocumentHeader.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByApplicationDocumentStatus(java.lang.Class, java.lang.String)
     */
    @Override
    public <T extends Document> Collection<T> findByApplicationDocumentStatus(Class<T> clazz, String applicationDocumentStatus) throws WorkflowException {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER + "." + KFSPropertyConstants.APPLICATION_DOCUMENT_STATUS, applicationDocumentStatus);
        final Collection<T> docsWithoutWorfklowHeaders = getBusinessObjectService().findMatching(clazz, fieldValues);
        List<T> results = new ArrayList<T>();
        for (T doc : docsWithoutWorfklowHeaders) {
            final T docWithWorkflowHeader = (T) getDocumentService().getByDocumentHeaderId(doc.getDocumentNumber());
            results.add(docWithWorkflowHeader);
        }
        return results;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByApplicationDocumentStatus(java.lang.String)
     */
    @Override
    public Collection<FinancialSystemDocumentHeader> findByApplicationDocumentStatus(String applicationDocumentStatus) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.APPLICATION_DOCUMENT_STATUS, applicationDocumentStatus);

        return getBusinessObjectService().findMatching(FinancialSystemDocumentHeader.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByDocumentNumber(java.lang.String)
     */
    @Override
    public FinancialSystemDocumentHeader findByDocumentNumber(String documentNumber) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        return getBusinessObjectService().findByPrimaryKey(FinancialSystemDocumentHeader.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#getPendingDocumentStatuses()
     */
    @Override
    public Set<String> getPendingDocumentStatuses() {
        return getDocumentStatusesForCategory(DocumentStatusCategory.PENDING);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#getSuccessfulDocumentStatuses()
     */
    @Override
    public Set<String> getSuccessfulDocumentStatuses() {
        return getDocumentStatusesForCategory(DocumentStatusCategory.SUCCESSFUL);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#getUnsuccessfulDocumentStatuses()
     */
    @Override
    public Set<String> getUnsuccessfulDocumentStatuses() {
        return getDocumentStatusesForCategory(DocumentStatusCategory.UNSUCCESSFUL);
    }

    /**
     * Turns all of the Rice KEW DocumentStatus codes for the given category into a Set of String codes
     *
     * @param documentStatusCategory the category to get DocumentStatuses for
     * @return the Set of String DocumentStatus codes for the given category
     */
    protected Set<String> getDocumentStatusesForCategory(DocumentStatusCategory documentStatusCategory) {
        Set<String> statuses = new HashSet<String>();
        for (DocumentStatus docStatus : DocumentStatus.getStatusesForCategory(documentStatusCategory)) {
            statuses.add(docStatus.getCode());
        }
        return statuses;
    }

    /**
     * Defers to the DAO
     *
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#getCorrectingDocumentHeader(java.lang.String)
     */
    @Override
    public DocumentHeader getCorrectingDocumentHeader(String documentId) {
        return getFinancialSystemDocumentHeaderDao().getCorrectingDocumentHeader(documentId);
    }

    /**
     * Returns the maximum number of results that should be returned from the document search.
     *
     * @param criteria the criteria in which to check for a max results value
     * @return the maximum number of results that should be returned from a document search
     */
    @Override
    public int getMaxResultCap(DocumentSearchCriteria criteria) {
        int systemLimit = KewApiConstants.DOCUMENT_LOOKUP_DEFAULT_RESULT_CAP;
        String resultCapValue = getParameterService().getParameterValueAsString(KewApiConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KewApiConstants.DOC_SEARCH_RESULT_CAP);
        if (StringUtils.isNotBlank(resultCapValue)) {
            try {
                int configuredLimit = Integer.parseInt(resultCapValue);
                if (configuredLimit <= 0) {
                    LOG.warn(KewApiConstants.DOC_SEARCH_RESULT_CAP + " was less than or equal to zero.  Please use a positive integer.");
                } else {
                    systemLimit = configuredLimit;
                }
            } catch (NumberFormatException e) {
                LOG.warn(KewApiConstants.DOC_SEARCH_RESULT_CAP + " is not a valid number.  Value was " + resultCapValue + ".  Using default: " + KewApiConstants.DOCUMENT_LOOKUP_DEFAULT_RESULT_CAP);
            }
        }
        int maxResults = systemLimit;
        if (criteria.getMaxResults() != null) {
            int criteriaLimit = criteria.getMaxResults().intValue();
            if (criteriaLimit > systemLimit) {
                LOG.warn("Result set cap of " + criteriaLimit + " is greater than system value of " + systemLimit);
            } else {
                if (criteriaLimit < 0) {
                    LOG.warn("Criteria results limit was less than zero.");
                    criteriaLimit = 0;
                }
                maxResults = criteriaLimit;
            }
        }
        return maxResults;
    }

    @Override
    public int getFetchMoreIterationLimit() {
        int fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
        String fetchMoreLimitValue = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KewApiConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.DOCUMENT_SEARCH_DETAIL_TYPE, KewApiConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT);
        if (!StringUtils.isBlank(fetchMoreLimitValue)) {
            try {
                fetchMoreLimit = Integer.parseInt(fetchMoreLimitValue);
                if (fetchMoreLimit < 0) {
                    LOG.warn(KewApiConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT + " was less than zero.  Please use a value greater than or equal to zero.");
                    fetchMoreLimit = DEFAULT_FETCH_MORE_ITERATION_LIMIT;
                }
            } catch (NumberFormatException e) {
                LOG.warn(KewApiConstants.DOC_SEARCH_FETCH_MORE_ITERATION_LIMIT + " is not a valid number.  Value was " + fetchMoreLimitValue);
            }
        }
        return fetchMoreLimit;
    }

    @Override
    public void prepareToCopy(FinancialSystemDocumentHeader oldDocumentHeader, FinancialSystemTransactionalDocument document) {
        // This method serves as a plugin to add logic to the copy functionality, when needed.
    }

    public FinancialSystemDocumentDao getFinancialSystemDocumentDao() {
        return financialSystemDocumentDao;
    }

    public void setFinancialSystemDocumentDao(FinancialSystemDocumentDao financialSystemDocumentDao) {
        this.financialSystemDocumentDao = financialSystemDocumentDao;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDocumentAdHocService(DocumentAdHocService documentAdHocService) {
        this.documentAdHocService = documentAdHocService;
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public FinancialSystemDocumentHeaderDao getFinancialSystemDocumentHeaderDao() {
        return financialSystemDocumentHeaderDao;
    }

    public void setFinancialSystemDocumentHeaderDao(FinancialSystemDocumentHeaderDao financialSystemDocumentHeaderDao) {
        this.financialSystemDocumentHeaderDao = financialSystemDocumentHeaderDao;
    }
}
