/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.document.validation.impl;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.BenefitsCalculation;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Business rule(s) applicable to Benefit Calculation Documents.
 */
public class BenefitsCalculationDocumentRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BenefitsCalculationDocumentRule.class);
    protected BenefitsCalculation oldBenefitsCalculation;
    protected BenefitsCalculation newBenefitsCalculation;

    /**
     * Constructs a BenefitsCalculationDocumentRule.java.
     */
    public BenefitsCalculationDocumentRule() {
        super();
    }

    /**
     * Processes the rules
     *
     * @param document MaintenanceDocument type of document to be processed.
     * @return boolean true when success
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.debug("Entering processCustomApproveDocumentBusinessRules()");

        // process rules
        checkRules(document);

        return true;
    }

    /**
     * Processes the rules
     *
     * @param document MaintenanceDocument type of document to be processed.
     * @return boolean true when success
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomRouteDocumentBusinessRules()");

        // process rules
        success &= checkRules(document);

        return success;
    }

    /**
     * Processes the rules
     *
     * @param document MaintenanceDocument type of document to be processed.
     * @return boolean true when success
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomSaveDocumentBusinessRules()");

        // process rules
        success &= checkRules(document);

        return success;
    }

    /**
     * Checks the fringe benefit percentage cannot be equal to or over 100%
     *
     * @param document MaintenanceDocument type
     * @return boolean false when the fringe benefit percentage cannot be equal to or over 100%
     */
    protected boolean checkRules(MaintenanceDocument document) {

        boolean success = true;

        /* The fringe benefit percentage cannot be equal to or over 100% */
        if (ObjectUtils.isNotNull(newBenefitsCalculation.getPositionFringeBenefitPercent())) {
            if (newBenefitsCalculation.getPositionFringeBenefitPercent().isGreaterEqual(new KualiDecimal(100))) {
                putFieldError("positionFringeBenefitPercent", LaborKeyConstants.ERROR_FRINGE_BENEFIT_PERCENTAGE_INVALID);
                success = false;
            }
        }
        success &= checkLaborBenefitRateCategory();
        return success;
    }

    private boolean checkLaborBenefitRateCategory() {
        boolean success = true;
        //make sure the system parameter exists
        if (SpringContext.getBean(ParameterService.class).parameterExists(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, LaborConstants.BenefitCalculation.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_PARAMETER)) {
            //check the system param to see if the labor benefit rate category should be filled in
            String sysParam = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, LaborConstants.BenefitCalculation.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_PARAMETER);
            LOG.debug("sysParam: " + sysParam);
            //if sysParam == Y then Labor Benefit Rate Category Code must be filled in
            if (sysParam.equalsIgnoreCase("Y")) {
                //check to see if the labor benefit category code is empty
                if (ObjectUtils.isNull(newBenefitsCalculation.getLaborBenefitRateCategoryCode())) {
                    putFieldError("laborBenefitRateCategoryCode", KFSKeyConstants.ERROR_EMPTY_LABOR_BENEFIT_CATEGORY_CODE);
                    success = false;
                } else {
                    newBenefitsCalculation.refreshReferenceObject("laborBenefitRateCategory");
                    if (newBenefitsCalculation.getLaborBenefitRateCategory() == null) {
                        putFieldError("laborBenefitRateCategoryCode", KFSKeyConstants.ERROR_LABOR_BENEFIT_CATEGORY_CODE);
                        success &= false;
                    }

                }
            }
        }
        return success;
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     *
     * @param document - the maintenanceDocument being evaluated
     * @return none
     */
    @Override
    public void setupConvenienceObjects() {

        // setup oldBenefitsCalculation convenience objects, make sure all possible sub-objects are populated
        oldBenefitsCalculation = (BenefitsCalculation) super.getOldBo();

        // setup newBenefitsCalculation convenience objects, make sure all possible sub-objects are populated
        newBenefitsCalculation = (BenefitsCalculation) super.getNewBo();
    }

}
