/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.coa.service.impl;

import org.kuali.kfs.coa.businessobject.ResponsibilityCenter;
import org.kuali.kfs.coa.service.ResponsibilityCenterService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.krad.service.BusinessObjectService;

@NonTransactional
public class ResponsibilityCenterServiceImpl implements ResponsibilityCenterService{

    private BusinessObjectService businessObjectService;

    /**
     * This method retrieves a responsibility instance by its primary key (parameters passed in).
     *
     * @param responsibilityCenterCode
     * @return A ResponsibilityCenter instance.
     */
    @Override
    public ResponsibilityCenter getByPrimaryId(String responsibilityCenterCode) {
        return businessObjectService.findBySinglePrimaryKey(ResponsibilityCenter.class, responsibilityCenterCode);
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
