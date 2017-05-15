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
package org.kuali.kfs.module.cam.batch.service;

import org.apache.commons.lang3.time.DateUtils;
import org.kuali.kfs.coreservice.api.parameter.Parameter;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.batch.ExtractProcessLog;
import org.kuali.kfs.module.cam.batch.service.impl.BatchExtractServiceImpl;
import org.kuali.kfs.module.cam.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cam.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Default implementation
 */
public class BatchExtractServiceTest extends BatchTestBase {

    private BatchExtractService batchExtractService;
    private BusinessObjectService boService;

    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        BatchExtractServiceImpl batchExtractServiceImpl = (BatchExtractServiceImpl) ProxyUtils.getTargetIfProxied(SpringContext.getBean(BatchExtractService.class));
        batchExtractServiceImpl.setFinancialSystemDocumentService(new BatchTestBaseFinancialSystemDocumentService());
        BatchTestBasePurApInfoService purApInfoService = new BatchTestBasePurApInfoService();
        purApInfoService.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        batchExtractServiceImpl.setPurApInfoService(purApInfoService);
        batchExtractService = batchExtractServiceImpl; // hide implementation details for the sake of the test
        boService = SpringContext.getBean(BusinessObjectService.class);
    }

    public void testBatchProcess() throws Exception {
        // TODO - fix with better mocks and/or fixtures
        ExtractProcessLog processLog = new ExtractProcessLog();
        Collection<Entry> glEntries = batchExtractService.findElgibleGLEntries(processLog);
        assertNotNull(glEntries);
        assertEquals(13, glEntries.size());
        List<Entry> fpLines = new ArrayList<Entry>();
        List<Entry> purapLines = new ArrayList<Entry>();
        // Test separation of lines
        batchExtractService.separatePOLines(fpLines, purapLines, glEntries);
        assertEquals(11, purapLines.size());
        assertEquals(2, fpLines.size());
        // Test saving of FP lines
        batchExtractService.saveFPLines(fpLines, processLog);
        Collection<GeneralLedgerEntry> fpGls = boService.findAll(GeneralLedgerEntry.class);
        assertEquals(2, fpGls.size());
        Timestamp ts = new Timestamp(DateUtils.parseDate("01/01/1970 23:59:59", new String[]{CamsConstants.DateFormats.MONTH_DAY_YEAR + " " + CamsConstants.DateFormats.MILITARY_TIME}).getTime());
        // Test updating the last extract time stamp
        batchExtractService.updateLastExtractTime(ts);
        Parameter parameter = findCabExtractTimeParam();
        assertEquals("01/01/1970 23:59:59", parameter.getValue());

        // Test saving PO lines
        batchExtractService.savePOLines(purapLines, processLog);

        // After saving PO lines asset count of records
        Collection<GeneralLedgerEntry> gls = boService.findAll(GeneralLedgerEntry.class);
        assertEquals(13, gls.size());
        Collection<PurchasingAccountsPayableDocument> allCabDocs = boService.findAll(PurchasingAccountsPayableDocument.class);
        assertEquals(7, allCabDocs.size());

        Collection<PurchasingAccountsPayableItemAsset> allCabItems = boService.findAll(PurchasingAccountsPayableItemAsset.class);
        assertEquals(14, allCabItems.size());

        Collection<PurchasingAccountsPayableLineAssetAccount> allCabAccts = boService.findAll(PurchasingAccountsPayableLineAssetAccount.class);
        assertEquals(17, allCabAccts.size());
    }


    public void testFindPreTaggablePOAccounts() throws Exception {
        // TODO - fix with better mocks and/or fixtures
        Collection<PurchaseOrderAccount> preTaggablePOAccounts = batchExtractService.findPreTaggablePOAccounts();
        assertEquals(6, preTaggablePOAccounts.size());
    }
}
