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
package org.kuali.kfs.module.ld.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.kns.web.ui.Column;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.kns.web.ui.Row;
import org.kuali.kfs.krad.exception.ValidationException;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.util.BeanPropertyComparator;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.LaborEnterpriseFeedStep;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class BenefitsCalculationLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

    private ParameterService parameterService;

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // remove hidden fields
        LookupUtils.removeHiddenCriteriaFields(getBusinessObjectClass(), fieldValues);

        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH));

        List searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, false);
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getColumns()
     */
    @Override
    public List<Column> getColumns() {
        List<Column> columns = super.getColumns();

        String offsetParmValue = parameterService.getParameterValueAsString(LaborEnterpriseFeedStep.class, LaborConstants.BenefitCalculation.LABOR_BENEFIT_CALCULATION_OFFSET_IND);

        if (offsetParmValue.equalsIgnoreCase("n")) {
            for (Iterator<Column> it = columns.iterator(); it.hasNext(); ) {
                Column column = (Column) it.next();
                if (column.getPropertyName().equalsIgnoreCase(LaborConstants.BenefitCalculation.ACCOUNT_CODE_OFFSET_PROPERTY_NAME) || column.getPropertyName().equalsIgnoreCase(LaborConstants.BenefitCalculation.OBJECT_CODE_OFFSET_PROPERTY_NAME)) {
                    it.remove();
                }
            }
        }

        return columns;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
     */
    @Override
    public List<Row> getRows() {
        List<Row> rows = super.getRows();

        String offsetParmValue = parameterService.getParameterValueAsString(LaborEnterpriseFeedStep.class, LaborConstants.BenefitCalculation.LABOR_BENEFIT_CALCULATION_OFFSET_IND);

        if (offsetParmValue.equalsIgnoreCase("n")) {
            for (Iterator<Row> it = rows.iterator(); it.hasNext(); ) {
                Row row = (Row) it.next();
                for (Field field : row.getFields()) {
                    if (field.getPropertyName().equalsIgnoreCase(LaborConstants.BenefitCalculation.ACCOUNT_CODE_OFFSET_PROPERTY_NAME) || field.getPropertyName().equalsIgnoreCase(LaborConstants.BenefitCalculation.OBJECT_CODE_OFFSET_PROPERTY_NAME)) {
                        it.remove();
                    }
                }
            }
        }

        return rows;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        HashMap<String, String> fieldsMap = new HashMap<String, String>();

        String accountNumber = (String) fieldValues.get("accountCodeOffset");
        String objectCode = (String) fieldValues.get("objectCodeOffset");

        // Validate the Account Number field is a valid Account Number in the DB
        if (StringUtils.isNotEmpty(accountNumber)) {
            fieldsMap.clear();
            fieldsMap.put(GeneralLedgerConstants.ColumnNames.ACCOUNT_NUMBER, accountNumber);
            List<Account> accountNums = (List<Account>) SpringContext.getBean(BusinessObjectService.class).findMatching(Account.class, fieldsMap);


            if (accountNums == null || accountNums.size() <= 0) {
                GlobalVariables.getMessageMap().putError("accountNumber", KFSKeyConstants.ERROR_CUSTOM, new String[]{"Invalid Account Number: " + accountNumber});
                throw new ValidationException("errors in search criteria");
            }
        }

        // Validate the Object Code field is a valid Object Code in the DB
        if (StringUtils.isNotEmpty(objectCode)) {
            fieldsMap.clear();
            fieldsMap.put(GeneralLedgerConstants.ColumnNames.OBJECT_CODE, objectCode);
            List<ObjectCode> objCodes = (List<ObjectCode>) SpringContext.getBean(BusinessObjectService.class).findMatching(ObjectCode.class, fieldsMap);


            if (objCodes == null || objCodes.size() <= 0) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.OBJECT_CODE, KFSKeyConstants.ERROR_CUSTOM, new String[]{"Invalid Object Code: " + objectCode});
                throw new ValidationException("errors in search criteria");
            }
        }
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
