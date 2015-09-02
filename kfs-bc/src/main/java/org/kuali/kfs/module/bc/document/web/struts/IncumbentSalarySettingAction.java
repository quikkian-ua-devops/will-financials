/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.web.struts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.kns.util.KNSGlobalVariables;
import org.kuali.kfs.kns.util.MessageList;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.MessageMap;

/**
 * the struts action for the salary setting for incumbent
 */
public class IncumbentSalarySettingAction extends DetailSalarySettingAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncumbentSalarySettingAction.class);

    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private BudgetConstructionIntendedIncumbentService budgetConstructionIntendedIncumbentService = SpringContext.getBean(BudgetConstructionIntendedIncumbentService.class);

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#loadExpansionScreen(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncumbentSalarySettingForm incumbentSalarySettingForm = (IncumbentSalarySettingForm) form;
        MessageMap errorMap;
        if (incumbentSalarySettingForm.isBudgetByAccountMode()){
            errorMap = incumbentSalarySettingForm.getCallBackErrors();
        }
        else {
            errorMap = GlobalVariables.getMessageMap();
        }

        // update incumbent record if required
        if (incumbentSalarySettingForm.isRefreshIncumbentBeforeSalarySetting()) {
            budgetConstructionIntendedIncumbentService.refreshIncumbentFromExternal(incumbentSalarySettingForm.getEmplid());
        }

        // use the passed url parms to get the record from DB
        Map<String, Object> fieldValues = incumbentSalarySettingForm.getKeyMapOfSalarySettingItem();
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, fieldValues);

        if (budgetConstructionIntendedIncumbent == null) {
            String emplid = (String) fieldValues.get(KFSPropertyConstants.EMPLID);
            
            errorMap.putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_INCUMBENT_NOT_FOUND, emplid);
            if (incumbentSalarySettingForm.isBudgetByAccountMode()){
                return this.returnToCaller(mapping, form, request, response);
            }
            else {
                this.cleanupAnySessionForm(mapping, request);
                return mapping.findForward(BCConstants.MAPPING_ORGANIZATION_SALARY_SETTING_RETURNING);
            }
        }

        incumbentSalarySettingForm.setBudgetConstructionIntendedIncumbent(budgetConstructionIntendedIncumbent);
        if (incumbentSalarySettingForm.isSingleAccountMode()) {
            incumbentSalarySettingForm.pickAppointmentFundingsForSingleAccount();
        }

        // acquire position and funding locks for the associated funding lines
        if (!incumbentSalarySettingForm.isViewOnlyEntry()) {
            incumbentSalarySettingForm.postProcessBCAFLines();
            incumbentSalarySettingForm.setNewBCAFLine(incumbentSalarySettingForm.createNewAppointmentFundingLine());
            
            boolean accessModeUpdated = incumbentSalarySettingForm.updateAccessMode(errorMap);
            if (!accessModeUpdated) {
                if (incumbentSalarySettingForm.isBudgetByAccountMode()){
                    return this.returnToCaller(mapping, form, request, response);
                }
                else {
                    this.cleanupAnySessionForm(mapping, request);
                    return mapping.findForward(BCConstants.MAPPING_ORGANIZATION_SALARY_SETTING_RETURNING);
                }
            }

            boolean gotLocks = incumbentSalarySettingForm.acquirePositionAndFundingLocks(errorMap);
            if (!gotLocks) {
                if (incumbentSalarySettingForm.isBudgetByAccountMode()){
                    return this.returnToCaller(mapping, form, request, response);
                }
                else {
                    this.cleanupAnySessionForm(mapping, request);
                    return mapping.findForward(BCConstants.MAPPING_ORGANIZATION_SALARY_SETTING_RETURNING);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.DetailSalarySettingAction#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward saveAction =  super.save(mapping, form, request, response);

        IncumbentSalarySettingForm incumbentSalarySettingForm = (IncumbentSalarySettingForm) form;
        this.sendWarnings(incumbentSalarySettingForm, KNSGlobalVariables.getMessageList());
        
        return saveAction;
    }

    /**
     * send warning messsages back to the caller
     * 
     * @param incumbentSalarySettingForm
     * @param warnings
     */
    public void sendWarnings(IncumbentSalarySettingForm incumbentSalarySettingForm, MessageList warnings){
        List<PendingBudgetConstructionAppointmentFunding> activeAppointmentFundings = incumbentSalarySettingForm.getActiveFundingLines();
        if (activeAppointmentFundings == null || activeAppointmentFundings.isEmpty()) {
            return;
        }
        BigDecimal requestedFteQuantityTotal = incumbentSalarySettingForm.getAppointmentRequestedFteQuantityTotal();
        
        // check for an active line that is LWPA or LWPF
        boolean hasFundingLineInvolvedLeaveWithoutPay = this.hasFundingLineInvolvedLeaveWithoutPay(activeAppointmentFundings);

        if (!hasFundingLineInvolvedLeaveWithoutPay && requestedFteQuantityTotal.compareTo(BigDecimal.ONE) != 0) {
            warnings.add(BCKeyConstants.WARNING_FTE_NOT_ONE);
        }

    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#getFundingAwareObjectName()
     */
    protected String getFundingAwareObjectName() {
        return BCPropertyConstants.BUDGET_CONSTRUCTION_INTENDED_INCUMBENT;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.DetailSalarySettingAction#getSynchronizationCheckType()
     */
    @Override
    public SynchronizationCheckType getSynchronizationCheckType() {
        return SynchronizationCheckType.POSN;
    }
}
