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
package org.kuali.kfs.module.cab.document.service.impl;

import org.kuali.kfs.krad.exception.ValidationException;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCapitalAssetItemsFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionItemFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

import java.util.List;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

@ConfigureContext(session = khuntley)
public class CapitalAssetPurchasingServiceTest extends KualiTestBase {

    public void testSetupCapitalAssetItems() {
        RequisitionDocument requisition = RequisitionDocumentWithCapitalAssetItemsFixture.REQ_VALID_IND_NEW_CAPITAL_ASSET_ITEM.createRequisitionDocument();

        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);
        List<PurchasingCapitalAssetItem> afterFirstCall = requisition.getPurchasingCapitalAssetItems();

        RequisitionItem item2 = (RequisitionItem) ObjectUtils.deepCopy(requisition.getItem(0));
        item2.setItemIdentifier(null);
        requisition.addItem(item2);

        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);
        List<PurchasingCapitalAssetItem> afterSecondCall = requisition.getPurchasingCapitalAssetItems();
        assertTrue(afterSecondCall.size() == 2);

        for (PurchasingCapitalAssetItem camsItem : afterSecondCall) {
            assertTrue(camsItem.getPurchasingCapitalAssetSystem() != null);
        }

    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testDeleteCapitalAssetItems() {
        RequisitionDocument requisition = RequisitionDocumentWithCapitalAssetItemsFixture.REQ_VALID_IND_NEW_CAPITAL_ASSET_ITEM.createRequisitionDocument();
        requisition.getDocumentHeader().setDocumentDescription("From PurchasingServiceTest)");

        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);

        PurchasingItem item2 = RequisitionItemFixture.REQ_ITEM_VALID_CAPITAL_ASSET.createRequisitionItemForCapitalAsset();

        requisition.addItem(item2);

        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);

        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(requisition);

        //now do the deletion
        SpringContext.getBean(PurchasingService.class).deleteCapitalAssetItems(requisition, requisition.getItem(0).getItemIdentifier());

        List<PurchasingCapitalAssetItem> afterDeletion = requisition.getPurchasingCapitalAssetItems();

        try {
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(requisition);
        } catch (ValidationException ex) {
            fail("Validation error when saving document without validation: " + dumpMessageMapErrors());
        }
        assertEquals(1, afterDeletion.size());

        for (PurchasingCapitalAssetItem camsItem : afterDeletion) {
            assertNotNull("PurchasingCapitalAssetSystem on " + camsItem + " should not have been null", camsItem.getPurchasingCapitalAssetSystem());
        }
    }

}
