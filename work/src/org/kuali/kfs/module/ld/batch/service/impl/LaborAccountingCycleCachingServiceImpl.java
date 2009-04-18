/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.batch.service.impl;

import org.kuali.kfs.gl.batch.service.impl.AccountingCycleCachingServiceImpl;
import org.kuali.kfs.module.ld.batch.dataaccess.LedgerPreparedStatementCachingDao;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;

public class LaborAccountingCycleCachingServiceImpl extends AccountingCycleCachingServiceImpl implements LaborAccountingCycleCachingService {
    protected LedgerPreparedStatementCachingDao ledgerDao;
    
    protected void initialize() {
        super.initialize();
        previousValueCache.put(LedgerBalance.class, new PreviousValueReference<LedgerBalance>());
    }

    public LaborObject getLaborObject(final Integer fiscalYear, final String chartCode, final String objectCode) {
        return new ReferenceValueRetriever<LaborObject>() {
            @Override
            protected LaborObject useDao() {
                return ledgerDao.getLaborObject(fiscalYear, chartCode, objectCode);
            }
            @Override
            protected void retrieveReferences(LaborObject laborObject) {}
        }.get(LaborObject.class, fiscalYear, chartCode, objectCode);
    }

    public int getMaxLaborSequenceNumber(LedgerEntry t) {
        return ledgerDao.getMaxLaborSequenceNumber(t);
    }

    public LedgerBalance getLedgerBalance(final LedgerBalance ledgerBalance) {
        return new PreviousValueRetriever<LedgerBalance>() {
            @Override
            protected LedgerBalance useDao() {
                return ledgerDao.getLedgerBalance(ledgerBalance);
            }            
        }.get(LedgerBalance.class, ledgerBalance.getUniversityFiscalYear(), ledgerBalance.getChartOfAccountsCode(), ledgerBalance.getAccountNumber(), ledgerBalance.getSubAccountNumber(), ledgerBalance.getFinancialObjectCode(), ledgerBalance.getFinancialSubObjectCode(), ledgerBalance.getFinancialBalanceTypeCode(), ledgerBalance.getFinancialObjectTypeCode(), ledgerBalance.getPositionNumber(), ledgerBalance.getEmplid());
    }

    public void insertLedgerBalance(LedgerBalance ledgerBalance) {
        ledgerDao.insertLedgerBalance(ledgerBalance);
        previousValueCache.get(LedgerBalance.class).update(ledgerBalance, ledgerBalance.getUniversityFiscalYear(), ledgerBalance.getChartOfAccountsCode(), ledgerBalance.getAccountNumber(), ledgerBalance.getSubAccountNumber(), ledgerBalance.getFinancialObjectCode(), ledgerBalance.getFinancialSubObjectCode(), ledgerBalance.getFinancialBalanceTypeCode(), ledgerBalance.getFinancialObjectTypeCode(), ledgerBalance.getPositionNumber(), ledgerBalance.getEmplid());
    }

    public void updateLedgerBalance(LedgerBalance ledgerBalance) {
        ledgerDao.updateLedgerBalance(ledgerBalance);
        previousValueCache.get(LedgerBalance.class).update(ledgerBalance, ledgerBalance.getUniversityFiscalYear(), ledgerBalance.getChartOfAccountsCode(), ledgerBalance.getAccountNumber(), ledgerBalance.getSubAccountNumber(), ledgerBalance.getFinancialObjectCode(), ledgerBalance.getFinancialSubObjectCode(), ledgerBalance.getFinancialBalanceTypeCode(), ledgerBalance.getFinancialObjectTypeCode(), ledgerBalance.getPositionNumber(), ledgerBalance.getEmplid());
    }

    public void insertLedgerEntry(LedgerEntry ledgerEntry) {
        ledgerDao.insertLedgerEntry(ledgerEntry);
    }

    public void setLedgerDao(LedgerPreparedStatementCachingDao ledgerDao) {
        this.ledgerDao = ledgerDao;
    }
}
