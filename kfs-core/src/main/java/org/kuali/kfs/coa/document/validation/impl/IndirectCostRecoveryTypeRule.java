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
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.List;

public class IndirectCostRecoveryTypeRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Account.class);

    protected IndirectCostRecoveryType indirectCostRecoveryType;
    protected List indirectCostRecoveryExclusionTypeDetails;
    protected ChartService chartService;


    public IndirectCostRecoveryTypeRule() {
        chartService = SpringContext.getBean(ChartService.class);
    }

    public void setupConvenienceObjects() {
        indirectCostRecoveryType = (IndirectCostRecoveryType) super.getNewBo();
        indirectCostRecoveryExclusionTypeDetails = indirectCostRecoveryType.getIndirectCostRecoveryExclusionTypeDetails();
    }

    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean isValid = true;
        isValid &= itemIsValid((IndirectCostRecoveryExclusionType) line);
        return isValid;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = true;
        GlobalVariables.getMessageMap().addToErrorPath(MAINTAINABLE_ERROR_PATH);
        isValid &= checkCollectionItems();
        GlobalVariables.getMessageMap().removeFromErrorPath(MAINTAINABLE_ERROR_PATH);
        return isValid;
    }

    public boolean checkCollectionItems() {
        boolean isValid = true;
        for (int i = 0; i < indirectCostRecoveryExclusionTypeDetails.size(); i++) {
            String collectionElementPath = "indirectCostRecoveryExclusionTypeDetails[" + i + "]";
            GlobalVariables.getMessageMap().addToErrorPath(collectionElementPath);
            isValid &= itemIsValid((IndirectCostRecoveryExclusionType) indirectCostRecoveryExclusionTypeDetails.get(i));
            GlobalVariables.getMessageMap().removeFromErrorPath(collectionElementPath);
        }
        return isValid;
    }

    public boolean itemIsValid(IndirectCostRecoveryExclusionType item) {
        boolean isValid = true;
        if (ObjectUtils.isNotNull(chartService.getUniversityChart().getChartOfAccountsCode())) {
            if (StringUtils.isBlank(item.getChartOfAccountsCode())) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_EXSISTENCE_CHART_CODE, item.getChartOfAccountsCode());
                isValid = false;
            } else if (item.isActive()) {
                item.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE_CURRENT);
                if (ObjectUtils.isNull(item.getObjectCodeCurrent())) {
                    if (item.isNewCollectionRecord()) {
                        GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_EXSISTENCE_OBJECT_CODE_DELETE, item.getChartOfAccountsCode(), item.getFinancialObjectCode());
                    } else {
                        GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_EXSISTENCE_OBJECT_CODE, item.getChartOfAccountsCode(), item.getFinancialObjectCode());
                    }
                    isValid = false;
                }
            }

        } else {
            LOG.info("The Chart Service found no Charts of University type.");
        }
        return isValid;
    }

}
