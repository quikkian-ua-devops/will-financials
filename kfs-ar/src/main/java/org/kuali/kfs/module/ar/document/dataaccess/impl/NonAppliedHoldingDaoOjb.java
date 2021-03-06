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
package org.kuali.kfs.module.ar.document.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.krad.util.OjbCollectionAware;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.dataaccess.NonAppliedHoldingDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NonAppliedHoldingDaoOjb extends PlatformAwareDaoBaseOjb implements NonAppliedHoldingDao, OjbCollectionAware {

    public Collection<NonAppliedHolding> getNonAppliedHoldingsByListOfDocumentNumbers(List<String> docNumbers) {
        Criteria criteria = new Criteria();
        criteria.addIn("referenceFinancialDocumentNumber", docNumbers);

        QueryByCriteria query = QueryFactory.newQuery(NonAppliedHolding.class, criteria);
        return new ArrayList<NonAppliedHolding>(this.getPersistenceBrokerTemplate().getCollectionByQuery(query));
    }

    public Collection<NonAppliedHolding> getNonAppliedHoldingsForCustomer(String customerNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("customerNumber", customerNumber);
        criteria.addEqualTo("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        Query query = QueryFactory.newQuery(NonAppliedHolding.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

}
