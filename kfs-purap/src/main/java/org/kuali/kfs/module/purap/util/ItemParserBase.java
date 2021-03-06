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

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.exception.InfrastructureException;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.exception.ItemParserException;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.web.format.FormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.kuali.kfs.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_INVALID_FILE_FORMAT;
import static org.kuali.kfs.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_INVALID_NUMERIC_VALUE;
import static org.kuali.kfs.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_ITEM_LINE;
import static org.kuali.kfs.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_ITEM_PROPERTY;
import static org.kuali.kfs.module.purap.PurapKeyConstants.ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER;
import static org.kuali.kfs.module.purap.PurapPropertyConstants.ITEM_CATALOG_NUMBER;
import static org.kuali.kfs.module.purap.PurapPropertyConstants.ITEM_COMMODITY_CODE;
import static org.kuali.kfs.module.purap.PurapPropertyConstants.ITEM_DESCRIPTION;
import static org.kuali.kfs.module.purap.PurapPropertyConstants.ITEM_QUANTITY;
import static org.kuali.kfs.module.purap.PurapPropertyConstants.ITEM_UNIT_PRICE;

public class ItemParserBase implements ItemParser {

    /**
     * The default format defines the expected item property names and their order in the import file.
     * Please update this if the import file format changes (i.e. adding/deleting item properties, changing their order).
     */
    protected static final String[] DEFAULT_FORMAT = {ITEM_QUANTITY, KFSPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, ITEM_CATALOG_NUMBER, ITEM_COMMODITY_CODE, ITEM_DESCRIPTION, ITEM_UNIT_PRICE};
    protected static final String[] COMMODITY_CODE_DISABLED_FORMAT = {ITEM_QUANTITY, KFSPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE, ITEM_CATALOG_NUMBER, ITEM_DESCRIPTION, ITEM_UNIT_PRICE};

    private Integer lineNo = 0;

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#getItemFormat()
     */
    public String[] getItemFormat() {
        //Check the ENABLE_COMMODITY_CODE_IND system parameter. If it's Y then
        //we should return the DEFAULT_FORMAT, otherwise
        //we should return the COMMODITY_CODE_DISABLED_FORMAT
        boolean enableCommodityCode = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KfsParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.ENABLE_COMMODITY_CODE_IND);
        if (enableCommodityCode) {
            return DEFAULT_FORMAT;
        }
        return COMMODITY_CODE_DISABLED_FORMAT;
    }

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#getExpectedItemFormatAsString(java.lang.Class)
     */
    public String getExpectedItemFormatAsString(Class<? extends PurApItem> itemClass) {
        checkItemClass(itemClass);
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String attributeName : getItemFormat()) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(getAttributeLabel(itemClass, attributeName));
        }
        return sb.toString();
    }

    /**
     * Retrieves the attribute label for the specified attribute.
     *
     * @param clazz         the class in which the specified attribute is defined
     * @param attributeName the name of the specified attribute
     * @return the attribute label for the specified attribute
     */
    @SuppressWarnings("rawtypes")
    protected String getAttributeLabel(Class clazz, String attributeName) {
        String label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(clazz, attributeName);
        if (StringUtils.isBlank(label)) {
            label = attributeName;
        }
        return label;
    }

    /**
     * Checks whether the specified item class is a subclass of PurApItem;
     * throws exceptions if not.
     *
     * @param itemClass the specified item class
     */
    protected void checkItemClass(Class<? extends PurApItem> itemClass) {
        if (!PurApItem.class.isAssignableFrom(itemClass)) {
            throw new IllegalArgumentException("unknown item class: " + itemClass);
        }
    }

    /**
     * Checks whether the specified item import file is not null and of a valid format;
     * throws exceptions if conditions not satisfied.
     *
     * @param itemClass the specified item import file
     */
    protected void checkItemFile(FormFile itemFile) {
        if (itemFile == null) {
            throw new ItemParserException("invalid (null) item import file", KFSKeyConstants.ERROR_UPLOADFILE_NULL);
        }
        String fileName = itemFile.getFileName();
        if (StringUtils.isNotBlank(fileName) && !StringUtils.lowerCase(fileName).endsWith(".csv") && !StringUtils.lowerCase(fileName).endsWith(".xls")) {
            throw new ItemParserException("unsupported item import file format: " + fileName, ERROR_ITEMPARSER_INVALID_FILE_FORMAT, fileName);
        }
    }

    /**
     * Parses a line of item data from a csv file and retrieves the attributes as key-value string pairs into a map.
     *
     * @param itemLine a string read from a line in the item import file
     * @return a map containing item attribute name-value string pairs
     */
    protected Map<String, String> retrieveItemAttributes(String itemLine) {
        String[] attributeNames = getItemFormat();
        String[] attributeValues = StringUtils.splitPreserveAllTokens(itemLine, ',');
        if (attributeNames.length != attributeValues.length) {
            String[] errorParams = {"" + attributeNames.length, "" + attributeValues.length, "" + lineNo};
            GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERRORS, ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER, errorParams);
            throw new ItemParserException("wrong number of item properties: " + attributeValues.length + " exist, " + attributeNames.length + " expected (line " + lineNo + ")", ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER, errorParams);
        }

        Map<String, String> itemMap = new HashMap<String, String>();
        for (int i = 0; i < attributeNames.length; i++) {
            itemMap.put(attributeNames[i], attributeValues[i]);
        }
        return itemMap;
    }

    /**
     * Generates an item instance and populates it with the specified attribute map.
     *
     * @param itemMap   the specified attribute map from which attributes are populated
     * @param itemClass the class of which the new item instance shall be created
     * @return the populated item
     */
    protected PurApItem genItemWithRetrievedAttributes(Map<String, String> itemMap, Class<? extends PurApItem> itemClass) {
        PurApItem item;
        try {
            item = itemClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new InfrastructureException("unable to complete item line population.", e);
        } catch (InstantiationException e) {
            throw new InfrastructureException("unable to complete item line population.", e);
        }

        boolean failed = false;
        for (Entry<String, String> entry : itemMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                /* removing this part as the checking are done in rule class later
                if ((key.equals(ITEM_DESCRIPTION) || key.equals(ITEM_UNIT_PRICE)) && value.equals("")) {
                    String[] errorParams = { key, "" + lineNo };
                    throw new ItemParserException("empty property value for " + key + " (line " + lineNo + ")", ERROR_ITEMPARSER_EMPTY_PROPERTY_VALUE, errorParams);
                }
                else */
                if (key.equals(KFSPropertyConstants.ITEM_UNIT_OF_MEASURE_CODE)) {
                    value = value.toUpperCase(); // force UOM code to uppercase
                }
                try {
                    ObjectUtils.setObjectProperty(item, key, value);
                } catch (FormatException e) {
                    String[] errorParams = {value, key, "" + lineNo};
                    throw new ItemParserException("invalid numeric property value: " + key + " = " + value + " (line " + lineNo + ")", ERROR_ITEMPARSER_INVALID_NUMERIC_VALUE, errorParams);
                }
            } catch (ItemParserException e) {
                // continue to parse the rest of the item properties after the current property fails
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERRORS, e.getErrorKey(), e.getErrorParameters());
                failed = true;
            } catch (IllegalAccessException e) {
                throw new InfrastructureException("unable to complete item line population.", e);
            } catch (NoSuchMethodException e) {
                throw new InfrastructureException("unable to complete item line population.", e);
            } catch (InvocationTargetException e) {
                throw new InfrastructureException("unable to complete item line population.", e);
            }
        }

        if (failed) {
            throw new ItemParserException("empty or invalid item properties in line " + lineNo + ")", ERROR_ITEMPARSER_ITEM_PROPERTY, "" + lineNo);
        }
        return item;
    }

    /**
     * Populates extra item attributes not contained in the imported item data to default values.
     *
     * @param item           the item to be populated
     * @param documentNumber the number of the docment that contains the item
     */
    protected void populateExtraAttributes(PurApItem item, String documentNumber) {
        if (item.getItemQuantity() != null) {
            String paramName = PurapParameterConstants.DEFAULT_QUANTITY_ITEM_TYPE;
            String itemTypeCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "Document", paramName);
            item.setItemTypeCode(itemTypeCode);
        } else {
            String paramName = PurapParameterConstants.DEFAULT_NON_QUANTITY_ITEM_TYPE;
            String itemTypeCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PurapConstants.PURAP_NAMESPACE, "Document", paramName);
            item.setItemTypeCode(itemTypeCode);
        }
        if (item instanceof RequisitionItem)
            ((RequisitionItem) item).setItemRestrictedIndicator(false);
        if (item instanceof PurchaseOrderItem)
            ((PurchaseOrderItem) item).setDocumentNumber(documentNumber);
    }

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#parseItem(java.lang.String, java.lang.Class, java.lang.String)
     */
    public PurApItem parseItem(String itemLine, Class<? extends PurApItem> itemClass, String documentNumber) {
        Map<String, String> itemMap = retrieveItemAttributes(itemLine);
        PurApItem item = genItemWithRetrievedAttributes(itemMap, itemClass);
        populateExtraAttributes(item, documentNumber);
        item.refresh();
        return item;
    }

    /**
     * @see org.kuali.kfs.module.purap.util.ItemParser#parseItem(org.apache.struts.upload.FormFile, java.lang.Class, java.lang.String)
     */
    public List<PurApItem> importItems(FormFile itemFile, Class<? extends PurApItem> itemClass, String documentNumber) {
        // check input parameters
        try {
            checkItemClass(itemClass);
            checkItemFile(itemFile);
        } catch (IllegalArgumentException e) {
            throw new InfrastructureException("unable to import items.", e);
        }

        // open input stream
        List<PurApItem> importedItems = new ArrayList<PurApItem>();
        InputStream is;
        BufferedReader br;
        try {
            is = itemFile.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
        } catch (IOException e) {
            throw new InfrastructureException("unable to open import file in ItemParserBase.", e);
        }

        // parse items line by line
        lineNo = 0;
        boolean failed = false;
        String itemLine = null;
        try {
            while ((itemLine = br.readLine()) != null) {
                lineNo++;

                if (StringUtils.isBlank(StringUtils.remove(StringUtils.deleteWhitespace(itemLine), KFSConstants.COMMA))) {
                    continue;
                }

                try {
                    PurApItem item = parseItem(itemLine, itemClass, documentNumber);
                    importedItems.add(item);
                } catch (ItemParserException e) {
                    // continue to parse the rest of the items after the current item fails
                    // error messages are already dealt with inside parseItem, so no need to do anything here
                    failed = true;
                }
            }

            if (failed) {
                throw new ItemParserException("errors in parsing item lines in file " + itemFile.getFileName(), ERROR_ITEMPARSER_ITEM_LINE, itemFile.getFileName());
            }
        } catch (IOException e) {
            throw new InfrastructureException("unable to read line from BufferReader in ItemParserBase", e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                throw new InfrastructureException("unable to close BufferReader in ItemParserBase", e);
            }
        }

        return importedItems;
    }

}
