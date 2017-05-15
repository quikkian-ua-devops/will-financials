/**
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2017 Kuali, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.document.validation.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ObjectConsService;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.dataaccess.BalanceDao;
import org.kuali.kfs.gl.dataaccess.impl.BalanceDaoOjb;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.GeneralLedgerPendingEntryDao;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class ObjectCodeRuleTest {
    private ObjectLevelService objectLevelService;
    private ObjectCodeService objectCodeService;
    private ObjectConsService objectConsService;
    private BalanceDao balanceDao;
    private ConfigurationService configService;
    private ChartService chartService;
    private BusinessObjectService businessObjectService;
    private UniversityDateService universityDateService;
    private GeneralLedgerPendingEntryDao generalLedgerPendingEntryDao;

    @Before
    public void setup() {
        PowerMock.mockStatic(SpringContext.class);

        objectLevelService = EasyMock.createMock(ObjectLevelService.class);
        objectCodeService = EasyMock.createMock(ObjectCodeService.class);
        objectConsService = EasyMock.createMock(ObjectConsService.class);
        balanceDao = EasyMock.createMock(BalanceDao.class);
        configService = EasyMock.createMock(ConfigurationService.class);
        chartService = EasyMock.createMock(ChartService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        universityDateService = EasyMock.createMock(UniversityDateService.class);
        generalLedgerPendingEntryDao = EasyMock.createMock(GeneralLedgerPendingEntryDao.class);

        EasyMock.expect(SpringContext.getBean(ConfigurationService.class)).andReturn(configService).anyTimes();
        EasyMock.expect(SpringContext.getBean(ObjectLevelService.class)).andReturn(objectLevelService).anyTimes();
        EasyMock.expect(SpringContext.getBean(ObjectCodeService.class)).andReturn(objectCodeService).anyTimes();
        EasyMock.expect(SpringContext.getBean(ChartService.class)).andReturn(chartService).anyTimes();
        EasyMock.expect(SpringContext.getBean(ObjectConsService.class)).andReturn(objectConsService).anyTimes();
        EasyMock.expect(SpringContext.getBean(BalanceDao.class)).andReturn(balanceDao).anyTimes();
        EasyMock.expect(SpringContext.getBean(BusinessObjectService.class)).andReturn(businessObjectService).anyTimes();
        EasyMock.expect(SpringContext.getBean(UniversityDateService.class)).andReturn(universityDateService).anyTimes();
        EasyMock.expect(SpringContext.getBean(GeneralLedgerPendingEntryDao.class)).andReturn(generalLedgerPendingEntryDao).anyTimes();
        EasyMock.expect(chartService.getReportsToHierarchy()).andReturn(null).anyTimes();
    }

    @Test
    @PrepareForTest({SpringContext.class})
    public void testCheckForBlockingGLBalances_None() {
        KualiDecimal balances = KualiDecimal.ZERO;
        KualiDecimal generalLedgerPendingEntries = KualiDecimal.ZERO;
        int currentFiscalYear = 2017;

        configureCheckForBlockingGLBalances(balances, generalLedgerPendingEntries, currentFiscalYear);

        ObjectCodeRule rule = new ObjectCodeRule();

        ObjectCode oc = new ObjectCode();
        oc.setUniversityFiscalYear(currentFiscalYear);
        oc.setFinancialObjectCode("1130");

        Assert.assertTrue(rule.checkForBlockingGLBalances(oc));

        verifyAll();
    }

    @Test
    @PrepareForTest({SpringContext.class})
    public void testCheckForBlockingGLBalances_GLEntries() {
        int currentFiscalYear = 2017;
        String objectCode = "1130";

        KualiDecimal balances = new KualiDecimal(100);
        KualiDecimal generalLedgerPendingEntries = KualiDecimal.ZERO;

        configureCheckForBlockingGLBalances(balances, generalLedgerPendingEntries, currentFiscalYear);

        ObjectCodeRule rule = new ObjectCodeRule();

        ObjectCode oc = new ObjectCode();
        oc.setUniversityFiscalYear(currentFiscalYear);
        oc.setFinancialObjectCode(objectCode);

        boolean result = rule.checkForBlockingGLBalances(oc);

        verifyAll();

        Assert.assertFalse(result);

        PowerMock.reset(SpringContext.class);
    }

    @Test
    @PrepareForTest({SpringContext.class})
    public void testCheckForBlockingGLBalances_GLPEntries() {
        int fiscalYear = 2017;
        String objectCode = "1130";

        KualiDecimal balances = KualiDecimal.ZERO;
        KualiDecimal generalLedgerPendingEntries = new KualiDecimal(100);

        configureCheckForBlockingGLBalances(balances, generalLedgerPendingEntries, fiscalYear);

        ObjectCodeRule rule = new ObjectCodeRule();

        ObjectCode oc = new ObjectCode();
        oc.setUniversityFiscalYear(fiscalYear);
        oc.setFinancialObjectCode(objectCode);

        Assert.assertFalse(rule.checkForBlockingGLBalances(oc));

        verifyAll();
    }

    @Test
    @PrepareForTest({SpringContext.class})
    public void testCheckForBlockingGLBalances_GLandGLPEntries() {
        int fiscalYear = 2017;
        String objectCode = "1130";

        KualiDecimal balances = new KualiDecimal(100);
        KualiDecimal generalLedgerPendingEntries = new KualiDecimal(100);

        configureCheckForBlockingGLBalances(balances, generalLedgerPendingEntries, fiscalYear);

        ObjectCodeRule rule = new ObjectCodeRule();

        ObjectCode oc = new ObjectCode();
        oc.setUniversityFiscalYear(fiscalYear);
        oc.setFinancialObjectCode(objectCode);

        Assert.assertFalse(rule.checkForBlockingGLBalances(oc));

        verifyAll();
    }

    @Test
    @PrepareForTest({SpringContext.class})
    public void testCheckForBlockingGLBalances_GLandGLPEntries_CancelledOut() {
        int fiscalYear = 2017;
        String objectCode = "1130";

        KualiDecimal balances = new KualiDecimal(100);
        KualiDecimal generalLedgerPendingEntries = new KualiDecimal(-100);

        configureCheckForBlockingGLBalances(balances, generalLedgerPendingEntries, fiscalYear);

        ObjectCodeRule rule = new ObjectCodeRule();

        ObjectCode oc = new ObjectCode();
        oc.setUniversityFiscalYear(fiscalYear);
        oc.setFinancialObjectCode(objectCode);

        Assert.assertTrue(rule.checkForBlockingGLBalances(oc));

        verifyAll();
    }

    private void configureCheckForBlockingGLBalances(KualiDecimal balancesTotal,
            KualiDecimal generalLedgerPendingEntriesTotal,
            int currentFiscalYear) {
        EasyMock.expect(universityDateService.getCurrentFiscalYear()).andReturn(currentFiscalYear).anyTimes();
        EasyMock.expect(balanceDao.findBalancesTotal(EasyMock.anyObject(Map.class))).andReturn(balancesTotal).times(1);
        EasyMock.expect(generalLedgerPendingEntryDao.getPendingEntriesTotalLedgerEntryAmount(EasyMock.anyObject(Map.class))).andReturn(generalLedgerPendingEntriesTotal).times(1);

        EasyMock.replay(configService, objectLevelService, objectCodeService, chartService, objectConsService, balanceDao, businessObjectService, universityDateService, generalLedgerPendingEntryDao);
        PowerMock.replay(SpringContext.class);
    }

    private void verifyAll() {
        EasyMock.verify(configService, objectLevelService, objectCodeService, chartService, objectConsService, balanceDao, businessObjectService, universityDateService, generalLedgerPendingEntryDao);
        PowerMock.verify(SpringContext.class);
    }
}
