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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class RequisitionCommodityCodeBranchingValidation extends BranchingValidation {

    public static final String COMMODITY_CODE_IS_REQUIRED = "commodityCodeIsRequiredValidation";

    private ParameterService parameterService;

    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (parameterService.getParameterValueAsBoolean(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND)) {
            return COMMODITY_CODE_IS_REQUIRED;
        } else {
            return null;
        }
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


}
