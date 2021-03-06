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
package org.kuali.kfs.module.purap.util;

import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.exception.ItemParserException;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.util.AutoPopulatingList;

import java.math.BigDecimal;

import static org.kuali.kfs.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_INVALID_NUMERIC_VALUE;
import static org.kuali.kfs.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_ITEM_PROPERTY;
import static org.kuali.kfs.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER;

/**
 * Test class for testing <code>{@link ItemParser}</code>
 */
@ConfigureContext(session = UserNameFixture.parke)
public class ItemParserTest extends KualiTestBase {
    PurchasingDocument purDoc;
    ItemParser parser;
    Class<? extends PurApItem> itemClass;
    String documentNumber;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        purDoc = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getNewDocument(RequisitionDocument.class);
        parser = purDoc.getItemParser();
        itemClass = purDoc.getItemClass();
        documentNumber = purDoc.getDocumentNumber();
    }

    /**
     * Asserts true if the specified ItemParserException reports the appropriate error message
     * with the expected parameters upon wrong number of input item properties.
     *
     * @param e              the specified ItemParserException
     * @param propertyNumber the wrong number of input item properties
     */
    private static void assertWrongPropertyNumber(ItemParserException e, int propertyNumber) {
        assertEquals(e.getErrorKey(), ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER);
        assertEquals(e.getErrorParameters()[1], "" + propertyNumber);
    }

    /**
     * Asserts true if the specified ItemParserException reports the appropriate error message
     * with the expected parameters upon empty input item property value.
     *
     * @param e the specified ItemParserException
     * @param propertyName the property name for the empty property value
     *
    private static void assertEmptyPropertyValue( ItemParserException e, String propertyName) {
    assertEquals(e.getErrorKey(), ERROR_ITEMPARSER_ITEM_PROPERTY);
    String errorPath = PurapConstants.ITEM_TAB_ERRORS;
    String errorKey = ERROR_ITEMPARSER_EMPTY_PROPERTY_VALUE;
    assertTrue(GlobalVariables.getMessageMap().containsMessageKey(errorKey));
    ArrayList params = GlobalVariables.getMessageMap().getMessages(errorPath);
    for (int i=0; i<params.size(); i++) {
    ErrorMessage errmsg = (ErrorMessage)params.get(i);
    if (errmsg.getErrorKey().equals(errorKey)) {
    assertEquals(errmsg.getMessageParameters()[0], propertyName);
    }
    }
    }
     */

    /**
     * Asserts true if the specified ItemParserException reports the appropriate error message
     * with the expected parameters upon invalid numeric value for input item property.
     *
     * @param e             the specified ItemParserException
     * @param propertyName  the property name for the invalid property value
     * @param propertyValue the invalid property value
     */
    private static void assertInvalidNumericValue(ItemParserException e, String propertyName, String propertyValue) {
        assertEquals(e.getErrorKey(), ERROR_ITEMPARSER_ITEM_PROPERTY);
        String errorPath = PurapConstants.ITEM_TAB_ERRORS;
        String errorKey = ERROR_ITEMPARSER_INVALID_NUMERIC_VALUE;
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(errorKey));
        AutoPopulatingList<ErrorMessage> params = GlobalVariables.getMessageMap().getMessages(errorPath);
        for (int i = 0; i < params.size(); i++) {
            ErrorMessage errmsg = (ErrorMessage) params.get(i);
            if (errmsg.getErrorKey().equals(errorKey)) {
                assertEquals(errmsg.getMessageParameters()[0], propertyValue);
                assertEquals(errmsg.getMessageParameters()[1], propertyName);
            }
        }
    }

    /**
     * Tests whether parseItem returns successfully with valid quantity-driven Requisition item line as input.
     */
    public void testParseQuantityReqItem() {
        String itemLine = "3,BX,123,,paper,6";
        try {
            PurApItem item = parser.parseItem(itemLine, itemClass, documentNumber);
            assertEquals(item.getItemQuantity().compareTo(new KualiDecimal(3)), 0);
            assertEquals(item.getItemUnitOfMeasureCode(), "BX");
            assertEquals(item.getItemCatalogNumber(), "123");
            assertEquals(item.getItemDescription(), "paper");
            assertEquals(item.getItemUnitPrice().compareTo(new BigDecimal(6)), 0);
            assertEquals(item.getItemTypeCode(), PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
            assertTrue(item instanceof RequisitionItem);
            assertFalse(((RequisitionItem) item).isItemRestrictedIndicator());
        } catch (ItemParserException e) {
            fail("Caught ItemParserException with valid quantity-driven requisition item.");
        }
    }

    /**
     * Tests whether parseItem returns successfully with valid nonquantity-driven PurchaseOrder item line as input.
     */
    public void testParseNonQuantityPOItem() throws Exception {
        purDoc = (PurchaseOrderDocument) SpringContext.getBean(DocumentService.class).getNewDocument(PurchaseOrderDocument.class);
        parser = purDoc.getItemParser();
        itemClass = purDoc.getItemClass();
        documentNumber = purDoc.getDocumentNumber();
        String itemLine = ",,100,,cleaning service,50";
        try {
            PurApItem item = parser.parseItem(itemLine, itemClass, documentNumber);
            assertEquals(item.getItemQuantity(), null);
            assertEquals(item.getItemUnitOfMeasureCode(), null);
            assertEquals(item.getItemCatalogNumber(), "100");
            assertEquals(item.getItemDescription(), "cleaning service");
            assertEquals(item.getItemUnitPrice().compareTo(new BigDecimal(50)), 0);
            assertEquals(item.getItemTypeCode(), PurapConstants.ItemTypeCodes.ITEM_TYPE_SERVICE_CODE);
            assertTrue(item instanceof PurchaseOrderItem);
            assertEquals(((PurchaseOrderItem) item).getDocumentNumber(), documentNumber);
        } catch (ItemParserException e) {
            fail("Caught ItemParserException with valid nonquantity-driven purchase order item.");
        }
    }

    /**
     * Tests whether parseItem catches exception upon wrong number of properties in the input item line.
     */
    public void testParseWrongPropertyNumberItem() {
        try {
            String itemLine = "2.5,BX,123,,paper,5.99,blahblah";
            PurApItem item = parser.parseItem(itemLine, itemClass, documentNumber);
            fail("Fail to throw ItemParserException with extra item property fields.");
        } catch (ItemParserException e) {
            assertWrongPropertyNumber(e, 7);
        }
        try {
            String itemLine = "BX,123,paper,,5.99";
            PurApItem item = parser.parseItem(itemLine, itemClass, documentNumber);
            fail("Fail to throw ItemParserException with missing item property fields.");
        } catch (ItemParserException e) {
            assertWrongPropertyNumber(e, 5);
        }
    }

    /**
     * Tests whether parseItem catches exceptions upon empty properties in the input item line.
     *
     public void testParseEmptyPropertyItem() {
     String itemLine = "2.5,BX,123,,5.99";
     try {
     PurApItem item = parser.parseItem(itemLine, itemClass, documentNumber);
     fail("Fail to throw ItemParserException with empty item property fields.");
     }
     catch(ItemParserException e) {
     assertEmptyPropertyValue(e, parser.getItemFormat()[3]);
     }
     }
     */

    /**
     * Tests whether parseItem catches exceptions upon invalid numeric properties values in the input item line.
     */
    public void testParseInvalidNumericItem() {
        String itemLine = "2.5,BX,123,,paper,blahblah";
        try {
            PurApItem item = parser.parseItem(itemLine, itemClass, documentNumber);
            fail("Fail to throw ItemParserException with invalid numeric values for item property fields.");
        } catch (ItemParserException e) {
            assertInvalidNumericValue(e, parser.getItemFormat()[5], "blahblah");
        }
    }
}

