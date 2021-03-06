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
package org.kuali.kfs.gl.businessobject.lookup;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.TransientBalanceInquiryAttributes;
import org.kuali.kfs.gl.businessobject.inquiry.AccountBalanceByLevelInquirableImpl;
import org.kuali.kfs.gl.service.AccountBalanceService;
import org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * An extension of KualiLookupableImpl to support the account balance by level inquiry screen
 */
public class AccountBalanceByLevelLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceByLevelLookupableHelperServiceImpl.class);

    private AccountBalanceService accountBalanceService;

    public void setAccountBalanceService(AccountBalanceService abs) {
        accountBalanceService = abs;
    }

    /**
     * Returns the inquiry url for a field if one exist.
     *
     * @param bo           the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new AccountBalanceByLevelInquirableImpl()).getInquiryUrl(bo, propertyName);
    }

    /**
     * Uses Lookup Service to provide a basic search.
     *
     * @param fieldValues - Map containing prop name keys and search values
     * @return List found business objects
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        LOG.debug("getSearchResults() started");

        setBackLocation((String) fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey((String) fieldValues.get(KFSConstants.DOC_FORM_KEY));

        BusinessObjectFieldConverter.escapeSingleQuote(fieldValues);

        String costShareOption = (String) fieldValues.get(GeneralLedgerConstants.DummyBusinessObject.COST_SHARE_OPTION);
        String pendingEntryOption = (String) fieldValues.get(GeneralLedgerConstants.DummyBusinessObject.PENDING_ENTRY_OPTION);
        String consolidationOption = (String) fieldValues.get(GeneralLedgerConstants.DummyBusinessObject.CONSOLIDATION_OPTION);
        boolean isCostShareExcluded = Constant.COST_SHARE_EXCLUDE.equals(costShareOption);
        int pendingEntryCode = AccountBalanceService.PENDING_NONE;
        if (GeneralLedgerConstants.PendingEntryOptions.APPROVED.equals(pendingEntryOption)) {
            pendingEntryCode = AccountBalanceService.PENDING_APPROVED;
        } else if (GeneralLedgerConstants.PendingEntryOptions.ALL.equals(pendingEntryOption)) {
            pendingEntryCode = AccountBalanceService.PENDING_ALL;
        }
        boolean isConsolidated = Constant.CONSOLIDATION.equals(consolidationOption);

        String chartOfAccountsCode = (String) fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String accountNumber = (String) fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        String subAccountNumber = (String) fieldValues.get(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        String financialConsolidationObjectCode = (String) fieldValues.get(GeneralLedgerConstants.BalanceInquiryDrillDowns.CONSOLIDATION_OBJECT_CODE);

        // Dashes means no sub account number
        if (KFSConstants.getDashSubAccountNumber().equals(subAccountNumber)) {
            subAccountNumber = "";
        }

        String ufy = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);

        // TODO Deal with invalid numbers
        Integer universityFiscalYear = new Integer(Integer.parseInt(ufy));

        List results = accountBalanceService.findAccountBalanceByLevel(universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber, financialConsolidationObjectCode, isCostShareExcluded, isConsolidated, pendingEntryCode);

        // Put the search related stuff in the objects
        for (Iterator iter = results.iterator(); iter.hasNext(); ) {
            AccountBalance ab = (AccountBalance) iter.next();

            TransientBalanceInquiryAttributes dbo = ab.getDummyBusinessObject();
            dbo.setConsolidationOption(consolidationOption);
            dbo.setCostShareOption(costShareOption);
            dbo.setPendingEntryOption(pendingEntryOption);
            dbo.setLinkButtonOption(Constant.LOOKUP_BUTTON_VALUE);
        }
        return new CollectionIncomplete(results, new Long(results.size()));
    }
}
