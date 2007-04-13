/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.budget.web.struts.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.module.budget.web.struts.form.MonthlyBudgetForm;
import org.kuali.rice.KNSServiceLocator;

public class MonthlyBudgetAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MonthlyBudgetAction.class);
            
    /**
     * added for testing - remove if not needed
     * @see org.kuali.module.budget.web.struts.action.BudgetConstructionAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        
        //TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        //since BC sets locks up front, but need to verify this


        //TODO should probably use service locator and call
        //DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer("<BCDoctype>");
        BudgetConstructionDocumentAuthorizer budgetConstructionDocumentAuthorizer = new BudgetConstructionDocumentAuthorizer();
        monthlyBudgetForm.populateAuthorizationFields(budgetConstructionDocumentAuthorizer);
/*
// TODO from KualiDocumentActionBase remove when ready
        // populates authorization-related fields in KualiDocumentFormBase instances, which are derived from
        // information which is contained in the form but which may be unavailable until this point
        if (form instanceof KualiDocumentFormBase) {
            KualiDocumentFormBase formBase = (KualiDocumentFormBase) form;
            Document document = formBase.getDocument();
            DocumentAuthorizer documentAuthorizer = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(document);
            formBase.populateAuthorizationFields(documentAuthorizer);

            // set returnToActionList flag, if needed
            if ("displayActionListView".equals(formBase.getCommand())) {
                formBase.setReturnToActionList(true);
            }

 */        
        return forward;
    }
    
    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {

        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());
        if ( !KNSServiceLocator.getKualiModuleService().isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), bcAuthorizationType ) ){
            LOG.error("User not authorized to use this action: " + this.getClass().getName() );
            throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), bcAuthorizationType, getKualiModuleService().getResponsibleModule(this.getClass()) );
        }
/*
//TODO from KualiAction - remove when ready
        AuthorizationType defaultAuthorizationType = new AuthorizationType.Default(this.getClass());
        if ( !KNSServiceLocator.getKualiModuleService().isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), defaultAuthorizationType ) ) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName() );
            throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), defaultAuthorizationType, getKualiModuleService().getResponsibleModule(((KualiDocumentFormBase)form).getDocument().getClass()) );
        }

//TODO from KualiDocumentActionBase - remove when ready            
            AuthorizationType documentAuthorizationType = new AuthorizationType.Document(((KualiDocumentFormBase)form).getDocument().getClass(), ((KualiDocumentFormBase)form).getDocument());
            if ( !KNSServiceLocator.getKualiModuleService().isAuthorized( GlobalVariables.getUserSession().getUniversalUser(), documentAuthorizationType ) ) {
                LOG.error("User not authorized to use this action: " + ((KualiDocumentFormBase)form).getDocument().getClass().getName() );
                throw new ModuleAuthorizationException( GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), documentAuthorizationType, getKualiModuleService().getResponsibleModule(((KualiDocumentFormBase)form).getDocument().getClass()) );
            }

 */
    }

    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        
        // use the passed url parms to get the record from DB 
        Map fieldValues = new HashMap();
        fieldValues.put("documentNumber", monthlyBudgetForm.getDocumentNumber());
        fieldValues.put("universityFiscalYear", monthlyBudgetForm.getUniversityFiscalYear());
        fieldValues.put("chartOfAccountsCode", monthlyBudgetForm.getChartOfAccountsCode());
        fieldValues.put("accountNumber", monthlyBudgetForm.getAccountNumber());
        fieldValues.put("subAccountNumber", monthlyBudgetForm.getSubAccountNumber());
        fieldValues.put("financialObjectCode", monthlyBudgetForm.getFinancialObjectCode());
        fieldValues.put("financialSubObjectCode", monthlyBudgetForm.getFinancialSubObjectCode());
        fieldValues.put("financialBalanceTypeCode", monthlyBudgetForm.getFinancialBalanceTypeCode());
        fieldValues.put("financialObjectTypeCode", monthlyBudgetForm.getFinancialObjectTypeCode());
        BudgetConstructionMonthly budgetConstructionMonthly = (BudgetConstructionMonthly) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BudgetConstructionMonthly.class,fieldValues);
        if (budgetConstructionMonthly == null){
            budgetConstructionMonthly = new BudgetConstructionMonthly();
            budgetConstructionMonthly.setDocumentNumber(monthlyBudgetForm.getDocumentNumber());
            budgetConstructionMonthly.setUniversityFiscalYear(monthlyBudgetForm.getUniversityFiscalYear());
            budgetConstructionMonthly.setChartOfAccountsCode(monthlyBudgetForm.getChartOfAccountsCode());
            budgetConstructionMonthly.setAccountNumber(monthlyBudgetForm.getAccountNumber());
            budgetConstructionMonthly.setSubAccountNumber(monthlyBudgetForm.getSubAccountNumber());
            budgetConstructionMonthly.setFinancialObjectCode(monthlyBudgetForm.getFinancialObjectCode());
            budgetConstructionMonthly.setFinancialSubObjectCode(monthlyBudgetForm.getFinancialSubObjectCode());
            budgetConstructionMonthly.setFinancialBalanceTypeCode(monthlyBudgetForm.getFinancialBalanceTypeCode());
            budgetConstructionMonthly.setFinancialObjectTypeCode(monthlyBudgetForm.getFinancialObjectTypeCode());
            budgetConstructionMonthly.refreshReferenceObject("pendingBudgetConstructionGeneralLedger");
        }
        monthlyBudgetForm.setBudgetConstructionMonthly(budgetConstructionMonthly);


        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly budgetConstructionMonthly = monthlyBudgetForm.getBudgetConstructionMonthly();
        
        // TODO validate and store monthly changes

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_REFRESH_METHOD);
        parameters.put(Constants.DOC_FORM_KEY, monthlyBudgetForm.getReturnFormKey());
        parameters.put(Constants.ANCHOR, monthlyBudgetForm.getReturnAnchor());
        parameters.put(Constants.REFRESH_CALLER, BCConstants.MONTHLY_BUDGET_REFRESH_CALLER);
        
        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.BC_DOCUMENT_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * This action changes the value of the hide field in the user interface so that when the page is rendered,
     * the UI knows to show all of the descriptions and labels for each of the pbgl line values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MonthlyBudgetForm tForm = (MonthlyBudgetForm) form;
        tForm.setHideDetails(false);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * This action toggles the value of the hide field in the user interface to "hide" so that when the page is rendered,
     * the UI displays values without all of the descriptions and labels for each of the pbgl lines.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MonthlyBudgetForm tForm = (MonthlyBudgetForm) form;
        tForm.setHideDetails(true);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}
