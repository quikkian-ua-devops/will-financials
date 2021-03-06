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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.B2BShoppingCartFixture;
import org.kuali.kfs.module.purap.fixture.B2BShoppingCartItemFixture;
import org.kuali.kfs.module.purap.util.cxml.B2BShoppingCart;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;

import java.math.BigDecimal;
import java.util.List;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

/**
 * Unit tests for B2BShoppingService.
 */
@ConfigureContext(session = parke)
public class B2BShoppingServiceTest extends KualiTestBase {

    /**
     * Tests creating a requisition from cxml using either vendor ID or DUNS depending on the parameter setting.
     */
    public void testCreateRequisitionsFromCxml() {
        // create b2b req using either vendorID or DUNS, depending on the parameter
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        boolean enableB2bByDuns = parameterService.getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_B2B_BY_VENDOR_DUNS_NUMBER_IND);
        RequisitionDocument req = createRequisitionFromCxml(enableB2bByDuns);
        B2BShoppingCartItemFixture b2bItem = null;

        // check vendor DUNS or ID
        if (enableB2bByDuns) {
            b2bItem = B2BShoppingCartItemFixture.B2B_ITEM_USING_VENDOR_DUNS;
            String duns = req.getVendorDetail().getVendorDunsNumber();
            assertEquals(duns, b2bItem.duns);
        } else {
            b2bItem = B2BShoppingCartItemFixture.B2B_ITEM_USING_VENDOR_ID;
            String vendorNumber = req.getVendorNumber();
            assertEquals(vendorNumber, b2bItem.externalSupplierId);
        }

        // check system supplier ID
        String supplierID = req.getExternalOrganizationB2bSupplierIdentifier();
        assertEquals(supplierID, b2bItem.systemSupplierID);

        // check items: only one item in the cart
        assertEquals(req.getItems().size(), 1);
        RequisitionItem reqItem = (RequisitionItem) req.getItem(0);
        assertNotNull(reqItem);

        // check item detail
        assertEquals(reqItem.getItemQuantity().compareTo(new KualiDecimal(b2bItem.quantity)), 0);
        assertEquals(reqItem.getItemCatalogNumber(), b2bItem.supplierPartId);
        assertEquals(reqItem.getItemAuxiliaryPartIdentifier(), b2bItem.supplierPartAuxiliaryId);
        assertEquals(reqItem.getItemUnitPrice().compareTo(new BigDecimal(b2bItem.unitPrice)), 0);
        assertEquals(reqItem.getItemDescription(), b2bItem.description);
        assertEquals(reqItem.getItemUnitOfMeasureCode(), b2bItem.unitOfMeasure);
        assertEquals(reqItem.getExternalOrganizationB2bProductTypeName(), b2bItem.productSource);
        assertEquals(reqItem.getExternalOrganizationB2bProductReferenceNumber(), b2bItem.systemProductID);
    }


    /**
     * Creates a requisition from cxml using vendor ID or DUNS, depending on whether DUNS is enabled.
     *
     * @param enableB2bByDunsNumber indicator of whether DUNS is enabled for B2B
     * @return Requisition document created.
     */
    protected RequisitionDocument createRequisitionFromCxml(boolean enableB2bByDunsNumber) {
        B2BShoppingCartFixture cartFixture = null;
        if (enableB2bByDunsNumber) {
            cartFixture = B2BShoppingCartFixture.B2B_CART_USING_VENDOR_DUNS;
        } else {
            cartFixture = B2BShoppingCartFixture.B2B_CART_USING_VENDOR_ID;
        }

        B2BShoppingCart cart = cartFixture.createB2BShoppingCart();
        Person user = GlobalVariables.getUserSession().getPerson();
        RequisitionDocument req = null;

        try {
            List reqs = SpringContext.getBean(B2BShoppingService.class).createRequisitionsFromCxml(cart, user);
            assertNotNull(reqs);

            // only one req created since there's only one vendor used in the cart
            assertEquals(reqs.size(), 1);
            req = (RequisitionDocument) reqs.get(0);
            assertNotNull(req);
        } catch (WorkflowException e) {
            fail(e.getMessage());
        }

        return req;
    }

}
