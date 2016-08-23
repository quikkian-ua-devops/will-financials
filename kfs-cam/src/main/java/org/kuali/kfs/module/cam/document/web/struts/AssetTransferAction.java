/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.web.struts;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.kns.util.KNSGlobalVariables;
import org.kuali.kfs.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.MessageMap;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kim.api.identity.Person;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.kuali.kfs.module.cam.CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;

public class AssetTransferAction extends FinancialSystemTransactionalDocumentActionBase {
    protected static final Logger LOG = Logger.getLogger(AssetTransferAction.class);

    /**
     * This method had to override because asset information has to be refreshed before display
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward docHandlerForward = super.docHandler(mapping, form, request, response);

        // refresh asset information
        AssetTransferForm assetTransferForm = (AssetTransferForm) form;
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) assetTransferForm.getDocument();
        handleRequestFromLookup(request, assetTransferForm, assetTransferDocument);
        handleRequestFromWorkflow(assetTransferForm, assetTransferDocument);
        Asset asset = assetTransferDocument.getAsset();
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_LOCATIONS);
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
        SpringContext.getBean(AssetLocationService.class).setOffCampusLocation(asset);
        SpringContext.getBean(PaymentSummaryService.class).calculateAndSetPaymentSummary(asset);

        // populate old asset fields for historic retaining on document
        String command = assetTransferForm.getCommand();
        if (KewApiConstants.INITIATE_COMMAND.equals(command)) {
            assetTransferDocument.setOldOrganizationOwnerChartOfAccountsCode(asset.getOrganizationOwnerChartOfAccountsCode());
            assetTransferDocument.setOldOrganizationOwnerAccountNumber(asset.getOrganizationOwnerAccountNumber());
        }

        this.refresh(mapping, form, request, response);

        return docHandlerForward;
    }

    /**
     * This method handles when request is from a work flow document search
     *
     * @param assetTransferForm     Form
     * @param assetTransferDocument Document
     * @param service               BusinessObjectService
     * @return Asset
     */
    protected void handleRequestFromWorkflow(AssetTransferForm assetTransferForm, AssetTransferDocument assetTransferDocument) {
        LOG.debug("Start- Handle request from workflow");
        if (assetTransferForm.getDocId() != null) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
            org.kuali.rice.kim.api.identity.PersonService personService = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class);
            Person person = personService.getPerson(assetTransferDocument.getRepresentativeUniversalIdentifier());
            if (person != null) {
                assetTransferDocument.setAssetRepresentative(person);
            } else {
                LOG.error("org.kuali.rice.kim.api.identity.PersonService returned null for uuid " + assetTransferDocument.getRepresentativeUniversalIdentifier());
            }
        }
    }

    /**
     * This method handles the request coming from asset lookup screen
     *
     * @param request               Request
     * @param assetTransferForm     Current form
     * @param assetTransferDocument Document
     * @param service               Business Object Service
     * @param asset                 Asset
     * @return Asset
     */
    protected void handleRequestFromLookup(HttpServletRequest request, AssetTransferForm assetTransferForm, AssetTransferDocument assetTransferDocument) {
        LOG.debug("Start - Handle request from asset lookup screen");
        if (assetTransferForm.getDocId() == null) {
            String capitalAssetNumber = request.getParameter(CAPITAL_ASSET_NUMBER);
            assetTransferDocument.setCapitalAssetNumber(Long.valueOf(capitalAssetNumber));
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
        }
    }


    /**
     * Since the organization fields are view only we need to make sure they are in sync with the data entry fields.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ((KualiDocumentFormBase) form).setMessageMapFromPreviousRequest(new MessageMap());
        ActionForward actionForward = super.refresh(mapping, form, request, response);

        AssetTransferDocument assetTransferDocument = ((AssetTransferForm) form).getAssetTransferDocument();

        assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
        assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.OLD_ORGANIZATION_OWNER_ACCOUNT);

        return actionForward;
    }


    /**
     * Route the document
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.route(mapping, form, request, response);

        allPaymentsFederalOwnedMessage(form);

        return actionForward;
    }

    protected void allPaymentsFederalOwnedMessage(ActionForm form) {
        boolean allPaymentsFederalOwned = true;

        AssetTransferDocument assetTransferDocument = ((AssetTransferForm) form).getAssetTransferDocument();

        for (AssetPayment assetPayment : assetTransferDocument.getAsset().getAssetPayments()) {
            if (!getAssetPaymentService().isPaymentFederalOwned(assetPayment)) {
                allPaymentsFederalOwned = false;
            }
        }

        // display a message for asset not generating ledger entries when it is federally owned
        if (allPaymentsFederalOwned) {
            KNSGlobalVariables.getMessageList().add(0, new ErrorMessage(CamsKeyConstants.Transfer.MESSAGE_NO_LEDGER_ENTRY_REQUIRED_TRANSFER));
        }
    }

    protected AssetPaymentService getAssetPaymentService() {
        return SpringContext.getBean(AssetPaymentService.class);
    }

}
