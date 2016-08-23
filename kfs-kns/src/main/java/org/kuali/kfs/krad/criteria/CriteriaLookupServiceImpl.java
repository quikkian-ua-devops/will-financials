/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.krad.criteria;

import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;

public class CriteriaLookupServiceImpl implements CriteriaLookupService {
    CriteriaLookupDao criteriaLookupDao;


    public void setCriteriaLookupDao(CriteriaLookupDao criteriaLookupDao) {
        this.criteriaLookupDao = criteriaLookupDao;
    }

    @Override
    public <T> GenericQueryResults<T> lookup(Class<T> queryClass, QueryByCriteria criteria) {
        return criteriaLookupDao.lookup(queryClass, criteria);
    }

    @Override
    public <T> GenericQueryResults<T> lookup(Class<T> queryClass, QueryByCriteria criteria,
            LookupCustomizer<T> customizer) {
        return criteriaLookupDao.lookup(queryClass, criteria, customizer);
    }
}
