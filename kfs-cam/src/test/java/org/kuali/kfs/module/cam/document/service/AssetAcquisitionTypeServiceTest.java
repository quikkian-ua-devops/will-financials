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
package org.kuali.kfs.module.cam.document.service;

import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.fixture.AssetAcquisitionTypeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

public class AssetAcquisitionTypeServiceTest extends KualiTestBase {

    private AssetAcquisitionTypeService assetAcquisitionTypeService;

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        assetAcquisitionTypeService = SpringContext.getBean(AssetAcquisitionTypeService.class);
    }


    /**
     * Test hasIncomeAssetObjectCode
     *
     * @throws Exception
     */
    @ConfigureContext(session = khuntley, shouldCommitTransactions = false)
    public void testHasIncomeAssetObjectCode_Success() throws Exception {
        // set up the data
        AssetAcquisitionType assetAcquisitionType = AssetAcquisitionTypeFixture.WITH_INCOME_ASSET_OBJECT_CODE.newAssetAcquisitionType();
        assertTrue(assetAcquisitionTypeService.hasIncomeAssetObjectCode(assetAcquisitionType.getAcquisitionTypeCode()));

        assetAcquisitionType = AssetAcquisitionTypeFixture.WITHOUT_INCOME_ASSET_OBJECT_CODE.newAssetAcquisitionType();
        assertFalse(assetAcquisitionTypeService.hasIncomeAssetObjectCode(assetAcquisitionType.getAcquisitionTypeCode()));
    }
}

