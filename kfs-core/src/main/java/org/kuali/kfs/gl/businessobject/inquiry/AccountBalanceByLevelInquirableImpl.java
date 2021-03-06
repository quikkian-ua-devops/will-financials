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
package org.kuali.kfs.gl.businessobject.inquiry;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.AccountBalanceByLevel;
import org.kuali.kfs.gl.businessobject.AccountBalanceByObject;
import org.kuali.kfs.kns.service.BusinessObjectDictionaryService;
import org.kuali.kfs.krad.service.LookupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class is used to generate the URL for the user-defined attributes for the account balace by level screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class AccountBalanceByLevelInquirableImpl extends AbstractGeneralLedgerInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceByLevelInquirableImpl.class);

    private BusinessObjectDictionaryService dataDictionary;
    private LookupService lookupService;
    private Class businessObjectClass;

    /**
     * Builds the keys for this inquiry.
     *
     * @return a List of Strings, holding the keys of this inquiry
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();

        keys.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(GeneralLedgerConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE);
        keys.add(GeneralLedgerConstants.BalanceInquiryDrillDowns.REPORTING_SORT_CODE);
        keys.add(Constant.COST_SHARE_OPTION);
        keys.add(Constant.CONSOLIDATION_OPTION);
        keys.add(Constant.PENDING_ENTRY_OPTION);

        return keys;
    }

    /**
     * The addition of the link button
     *
     * @return a Map of user defined attributes
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        userDefinedAttributeMap.put(GeneralLedgerConstants.DummyBusinessObject.LINK_BUTTON_OPTION, "");
        return userDefinedAttributeMap;
    }

    /**
     * Changes the name of attributes on the fly...in this case, turns the link button to display its name as object code
     *
     * @param attributeName the attribute to rename
     * @return a String with the new attribute name
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        if (attributeName.equals(GeneralLedgerConstants.DummyBusinessObject.LINK_BUTTON_OPTION)) {
            attributeName = KFSPropertyConstants.OBJECT_CODE;
        }
        return attributeName;
    }

    /**
     * If the key name sent in represents an "exclusive field", returns "" as the key value
     *
     * @param keyName  the name of the key that may be changed
     * @param keyValue the value of the key that may be changed
     * @return an Object with the perhaps modified value for the key
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = "";
        }
        return keyValue;
    }

    /**
     * Justs returns the key name given
     *
     * @param keyName a key name
     * @return the key name given
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * Return a Spring bean for the lookup
     *
     * @return the name of the Spring bean of the lookup
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return Constant.GL_LOOKUPABLE_ACCOUNT_BALANCE_BY_OBJECT;
    }

    /**
     * Return the page name of this lookup
     *
     * @return the page name for all GL lookups
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * Retrieves the business class to use as the basis of an inquiry for the given attribute
     *
     * @param attributeName the name to build the inquiry link to
     * @return the Class of the business object that should be inquired on
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        Class c = null;
        /*
         * if("financialObject.financialObjectLevel.financialConsolidationObjectCode".equals(attributeName)) { c =
         * AccountBalanceByConsolidation.class; } else
         */
        if (GeneralLedgerConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE.equals(attributeName)) {
            c = AccountBalance.class;
        } else if (KFSPropertyConstants.OBJECT_CODE.equals(attributeName)) {
            c = AccountBalanceByObject.class;
        } else {
            c = AccountBalanceByLevel.class;
        }

        return c;
    }

    /**
     * Addes the lookup impl attribute to the parameters
     *
     * @param parameter     the parameters used in the lookup
     * @param attributeName the attribute name that an inquiry URL is being built for
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());
    }
}
