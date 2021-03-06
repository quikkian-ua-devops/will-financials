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
package org.kuali.kfs.module.ld.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.util.ObjectModification;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants.AccountingPeriodProperties;
import org.kuali.kfs.module.ld.businessobject.AccountStatusCurrentFunds;
import org.kuali.kfs.module.ld.businessobject.July1PositionFunding;
import org.kuali.kfs.module.ld.dataaccess.LaborDao;
import org.kuali.kfs.module.ld.util.ConsolidationUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * OJB Implementation of Labor Distribution DAO database queries.
 */
public class LaborDaoOjb extends PlatformAwareDaoBaseOjb implements LaborDao {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LaborDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborDao#getEncumbranceTotal(java.util.Map)
     */
    public Object getEncumbranceTotal(Map fieldValues) {

        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new AccountStatusCurrentFunds()));

        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountStatusCurrentFunds.class, criteria);

        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);

        query.setAttributes(new String[]{ConsolidationUtil.sum(LaborConstants.BalanceInquiries.ANNUAL_BALANCE) + " + " + ConsolidationUtil.sum(LaborConstants.BalanceInquiries.CONTRACT_GRANT_BB_AMOUNT)});
        query.addGroupBy(groupBy);

        Object[] encumbrances = null;

        Iterator<Object[]> accountStatusCurrentFunds = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (accountStatusCurrentFunds != null && accountStatusCurrentFunds.hasNext()) {
            encumbrances = accountStatusCurrentFunds.next();
        }
        KualiDecimal encumbranceTotal = KualiDecimal.ZERO;
        if (encumbrances != null)
            encumbranceTotal = new KualiDecimal(encumbrances[0].toString());
        return encumbranceTotal;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborDao#getJuly1(java.util.Map)
     */
    public Collection getJuly1(Map fieldValues) {
        Map fieldCriteria = new HashMap();
        fieldCriteria.putAll(fieldValues);
        fieldCriteria.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);

        ArrayList objectTypeCodes = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldCriteria, new July1PositionFunding()));
        QueryByCriteria query = QueryFactory.newQuery(July1PositionFunding.class, criteria);

        Collection<July1PositionFunding> july1PositionFundings = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return july1PositionFundings;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborDao#getCurrentFunds(java.util.Map)
     */
    public Iterator getCurrentFunds(Map fieldValues, boolean isConsolidated) {
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        return getAccountStatus(AccountStatusCurrentFunds.class, fieldValues, isConsolidated);
    }

    protected <T> Iterator getAccountStatus(Class<T> clazz, Map fieldValues, boolean isConsolidated) {
        Query query = getAccountStatusQuery(clazz, fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    // build the query for balance search
    protected <T> Query getAccountStatusQuery(Class<T> clazz, Map fieldValues, boolean isConsolidated) {
        LOG.debug("getAccountStatusQuery(Class<T>, Map, boolean) started");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Building criteria from map fields: " + fieldValues.entrySet());
        }

        Criteria criteria = new Criteria();
        try {
            criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, clazz.newInstance()));
        } catch (Exception e) {
            LOG.error("Could not add and criteria properly for " + clazz);
            throw new RuntimeException(e);
        }

        ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, criteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Built query: " + query);
        }

        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            ConsolidationUtil.buildConsolidatedQuery(query, ConsolidationUtil.sum(AccountingPeriodProperties.JULY.propertyName));
        }

        return query;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborDao#getJuly1PositionFunding(java.util.Map)
     */
    public Collection<July1PositionFunding> getJuly1PositionFunding(Map<String, String> fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new July1PositionFunding());

        criteria.addEqualToField(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        criteria.addEqualToField(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        criteria.addEqualToField(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        criteria.addNotNull(KFSPropertyConstants.LABOR_OBJECT + "." + KFSPropertyConstants.FINANCIAL_OBJECT_FRINGE_OR_SALARY_CODE);

        QueryByCriteria query = QueryFactory.newQuery(July1PositionFunding.class, criteria);
        OJBUtility.limitResultSize(query);

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborDao#insert(org.kuali.rice.krad.bo.BusinessObject)
     */
    public void insert(BusinessObject businessObject) {
        getPersistenceBroker(true).store(businessObject, ObjectModification.INSERT);
    }
}
