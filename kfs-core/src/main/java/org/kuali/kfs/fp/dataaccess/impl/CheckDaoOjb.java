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
package org.kuali.kfs.fp.dataaccess.impl;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.dataaccess.CheckDao;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.dao.DataAccessException;

import java.util.Collection;

/**
 * This class is the OJB implementation of the AccountingLineDao interface.
 */

public class CheckDaoOjb extends PlatformAwareDaoBaseOjb implements CheckDao {
    private static final Logger LOG = Logger.getLogger(CheckDaoOjb.class);

    /**
     * @param line
     * @throws DataAccessException
     */
    public void deleteCheck(Check check) throws DataAccessException {
        getPersistenceBrokerTemplate().delete(check);
    }

    /**
     * Retrieves accounting lines associate with a given document header ID using OJB.
     *
     * @param classname
     * @param id
     * @return
     */
    public Collection<CheckBase> findByDocumentHeaderId(String documentHeaderId) throws DataAccessException {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("FDOC_NBR", documentHeaderId);

        QueryByCriteria query = QueryFactory.newQuery(CheckBase.class, criteria);

        Collection<CheckBase> lines = getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return lines;
    }
}
