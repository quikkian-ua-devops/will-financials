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
package org.kuali.kfs.module.purap.document.web.struts;

import org.kuali.kfs.kns.document.authorization.DocumentAuthorizer;
import org.kuali.kfs.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Struts Action for Contract Manager Assignment document.
 */
public class ContractManagerAssignmentAction extends FinancialSystemTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractManagerAssignmentAction.class);

    /**
     * Do initialization for a new <code>ContractManagerAssignmentDocument</code>.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ContractManagerAssignmentDocument acmDocument = (ContractManagerAssignmentDocument) kualiDocumentFormBase.getDocument();
        acmDocument.getDocumentHeader().setDocumentDescription(PurapConstants.ASSIGN_CONTRACT_MANAGER_DEFAULT_DESC);
        acmDocument.populateDocumentWithRequisitions();
    }

    /**
     * Overrides the method in KualiDocumentActionBase to fetch a List of requisition documents for the
     * ContractManagerAssignmentDocument from documentService, because we need the workflowDocument to get the
     * createDate. If we don't fetch the requisition documents from the documentService, the workflowDocument
     * in the requisition's documentHeader would be null and would cause the transient flexDoc is null error.
     * That's the reason we need this override.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        ContractManagerAssignmentDocument document = (ContractManagerAssignmentDocument) kualiDocumentFormBase.getDocument();
        List<String> documentHeaderIds = new ArrayList();
        Map<String, ContractManagerAssignmentDetail> documentHeaderIdsAndDetails = new HashMap();

        //Compose a Map in which the keys are the document header ids of each requisition in this acm document and the values are the
        //corresponding ContractManagerAssignmentDetail object.
        for (ContractManagerAssignmentDetail detail : (List<ContractManagerAssignmentDetail>) document.getContractManagerAssignmentDetails()) {
            documentHeaderIdsAndDetails.put(detail.getRequisition().getDocumentNumber(), detail);
        }
        //Add all of the document header ids (which are the keys of the documentHeaderIdsAndDetails  map) to the
        //documentHeaderIds List.
        documentHeaderIds.addAll(documentHeaderIdsAndDetails.keySet());

        //Get a List of requisition documents from documentService so that we can have the workflowDocument as well
        List<Document> requisitionDocumentsFromDocService = new ArrayList();
        try {
            if (documentHeaderIds.size() > 0)
                requisitionDocumentsFromDocService = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(RequisitionDocument.class, documentHeaderIds);
        } catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught: " + we.getLocalizedMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }

        //Set the documentHeader of the requisition of each of the ContractManagerAssignmentDetail to the documentHeader of
        //the requisitions resulted from the documentService, so that we'll have workflowDocument in the documentHeader.
        for (Document req : requisitionDocumentsFromDocService) {
            ContractManagerAssignmentDetail detail = (ContractManagerAssignmentDetail) documentHeaderIdsAndDetails.get(req.getDocumentNumber());
            detail.getRequisition().setDocumentHeader(req.getDocumentHeader());
        }
    }

    @Override
    protected void populateAdHocActionRequestCodes(KualiDocumentFormBase formBase) {
        Document document = formBase.getDocument();
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        Map<String, String> adHocActionRequestCodes = new HashMap<String, String>();

        if (documentAuthorizer.canSendAdHocRequests(document, KewApiConstants.ACTION_REQUEST_FYI_REQ, GlobalVariables.getUserSession().getPerson())) {
            adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_FYI_REQ, KewApiConstants.ACTION_REQUEST_FYI_REQ_LABEL);
        }
        if ((document.getDocumentHeader().getWorkflowDocument().isInitiated()
            || document.getDocumentHeader().getWorkflowDocument().isSaved()
            || document.getDocumentHeader().getWorkflowDocument().isEnroute()
        ) && documentAuthorizer.canSendAdHocRequests(document, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, GlobalVariables.getUserSession().getPerson())) {
            adHocActionRequestCodes.put(KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
        }
        formBase.setAdHocActionRequestCodes(adHocActionRequestCodes);

    }

}
