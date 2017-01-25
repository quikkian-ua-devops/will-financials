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
package org.kuali.kfs.module.cg.service.impl;

import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.cg.businessobject.SubContractor;
import org.kuali.kfs.module.cg.service.SubcontractorService;
import org.kuali.kfs.sys.KFSPropertyConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the Subcontractor service.
 */
public class SubcontractorServiceImpl implements SubcontractorService {

    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.service.SubcontractorService#getByPrimaryId(String)
     */
    @Override
    public SubContractor getByPrimaryId(String subcontractorNumber) {
        return businessObjectService.findByPrimaryKey(SubContractor.class, mapPrimaryKeys(subcontractorNumber));
    }

    protected Map<String, Object> mapPrimaryKeys(String subcontractorNumber) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.SUBCONTRACTOR_NUMBER, subcontractorNumber.trim());
        return primaryKeys;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
