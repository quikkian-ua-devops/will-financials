/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.service;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.exception.BudgetPositionAlreadyExistsException;

/**
 * Provides methods to retrieve and populate budget construction position records.
 */
public interface BudgetConstructionPositionService {

    /**
     * Retrieves a new position record from an external system using <code>HumanResourcesPayrollService</code> then populates the
     * record in the budget position table.
     * 
     * @param universityFiscalYear budget fiscal year for the position
     * @param positionNumber position number for the record
     * @exception BudgetPositionAlreadyExistsException thrown when position is already in the budget table
     */
    public void pullNewPositionFromExternal(Integer universityFiscalYear, String positionNumber) throws BudgetPositionAlreadyExistsException;

    /**
     * Refreshes a position record from an external system using <code>HumanResourcesPayrollService</code> then updates the record
     * in the budget position table.
     * 
     * @param universityFiscalYear budget fiscal year for the position
     * @param positionNumber position number for the record
     */
    public void refreshPositionFromExternal(Integer universityFiscalYear, String positionNumber);

    /**
     * retrieve a Budget Construction Position object by its primary key.
     * 
     * @param fiscalYear the given fiscal year
     * @param positionNumber the given position number
     * @return a Budget Construction Position object retrived by its primary key
     */
    public BudgetConstructionPosition getByPrimaryId(String fiscalYear, String positionNumber);
    
    /**
     * retrieve a Budget Construction Position object by its primary key, which is locked by the specified user.
     * 
     * @param fiscalYear the given fiscal year
     * @param positionNumber the given position number
     * @param positionLockUserIdentifier the user id who owns a lock on the position
     * @return a Budget Construction Position object retrived by its primary key, which is locked by the specified user.
     */
    public BudgetConstructionPosition getLockedPositionByPrimaryId(Integer fiscalYear, String positionNumber, String positionLockUserIdentifier);
}
