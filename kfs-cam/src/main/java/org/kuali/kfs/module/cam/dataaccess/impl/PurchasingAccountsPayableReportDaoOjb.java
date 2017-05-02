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
package org.kuali.kfs.module.cam.dataaccess.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cam.dataaccess.PurchasingAccountsPayableReportDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PurchasingAccountsPayableReportDaoOjb extends PlatformAwareDaoBaseOjb implements PurchasingAccountsPayableReportDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableReportDaoOjb.class);

    /**
     * @see PurchasingAccountsPayableReportDao#findPurchasingAccountsPayableDocuments(Map)
     */
    @Override
    public Collection findPurchasingAccountsPayableDocuments(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new PurchasingAccountsPayableDocument());
        QueryByCriteria query = QueryFactory.newQuery(PurchasingAccountsPayableDocument.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }


    /**
     * @see PurchasingAccountsPayableReportDao#findGeneralLedgers(Map)
     */
    @Override
    public Iterator findGeneralLedgers(Map fieldValues) {
        LOG.debug("findGeneralLedgers started...");
        ReportQueryByCriteria query = getGeneralLedgerReportQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * Generate Query object for GeneralLedgerEntry search.
     *
     * @param fieldValues
     * @return
     */
    protected ReportQueryByCriteria getGeneralLedgerReportQuery(Map fieldValues) {
        Collection docTypeCodes = getDocumentType(fieldValues);
        Collection activityStatusCodes = getActivityStatusCode(fieldValues);

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new GeneralLedgerEntry());

        // set document type code criteria
        if (!docTypeCodes.isEmpty()) {
            criteria.addIn(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE, docTypeCodes);
        }
        // set activity status code criteria
        if (!activityStatusCodes.isEmpty()) {
            criteria.addIn(CamsPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE, activityStatusCodes);
        }
        ReportQueryByCriteria query = QueryFactory.newReportQuery(GeneralLedgerEntry.class, criteria);

        List attributeList = buildAttributeList(false);

        // set the selection attributes
        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);
        return query;
    }


    /**
     * Get activity_statu_code
     *
     * @param fieldValues
     * @return
     */
    protected Collection getActivityStatusCode(Map fieldValues) {
        Collection activityStatusCodes = new ArrayList<String>();

        if (fieldValues.containsKey(CamsPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE)) {
            String fieldValue = (String) fieldValues.get(CamsPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
            if (KFSConstants.NON_ACTIVE_INDICATOR.equalsIgnoreCase(fieldValue)) {
                // when selected as 'N', search for active lines as 'M'-modified by CAB user,'N'- new
                activityStatusCodes.add(CamsConstants.ActivityStatusCode.NEW);
                activityStatusCodes.add(CamsConstants.ActivityStatusCode.MODIFIED);
            }
            fieldValues.remove(CamsPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        }
        return activityStatusCodes;
    }


    /**
     * Get Document type code selection
     *
     * @param fieldValues
     * @return
     */
    protected Collection getDocumentType(Map fieldValues) {
        Collection docTypeCodes = new ArrayList<String>();

        if (fieldValues.containsKey(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE)) {
            String fieldValue = (String) fieldValues.get(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
            if (StringUtils.isEmpty(fieldValue)) {
                docTypeCodes.add(CamsConstants.PREQ);
                docTypeCodes.add(CamsConstants.CM);
            } else {
                docTypeCodes.add(fieldValue);
            }
            // truncate the non-property filed
            fieldValues.remove(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
        }

        return docTypeCodes;
    }


    /**
     * Build attribute list for select clause.
     *
     * @param isExtended
     * @return
     */
    protected List<String> buildAttributeList(boolean isExtended) {
        List attributeList = new ArrayList();

        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_YEAR);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.UNIVERSITY_FISCAL_PERIOD_CODE);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.CHART_OF_ACCOUNTS_CODE);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.ACCOUNT_NUMBER);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_CODE);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.FINANCIAL_DOCUMENT_TYPE_CODE);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.TRANSACTION_DEBIT_CREDIT_CODE);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.REFERENCE_FINANCIAL_DOCUMENT_NUMBER);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.TRANSACTION_DATE);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.TRANSACTION_LEDGER_SUBMIT_AMOUNT);
        attributeList.add(CamsPropertyConstants.GeneralLedgerEntry.ACTIVITY_STATUS_CODE);
        return attributeList;
    }

}
