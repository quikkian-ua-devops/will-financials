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
package org.kuali.kfs.module.ld.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.inquiry.EntryInquirableImpl;
import org.kuali.kfs.gl.businessobject.inquiry.InquirableFinancialDocument;
import org.kuali.kfs.integration.ld.businessobject.inquiry.AbstractPositionDataDetailsInquirableImpl;
import org.kuali.kfs.kns.lookup.AbstractLookupableHelperServiceImpl;
import org.kuali.kfs.kns.lookup.HtmlData;
import org.kuali.kfs.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.kfs.krad.lookup.CollectionIncomplete;
import org.kuali.kfs.krad.util.BeanPropertyComparator;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.businessobject.inquiry.PositionDataDetailsInquirableImpl;
import org.kuali.kfs.module.ld.service.LaborInquiryOptionsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.kfs.module.ld.LaborConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21;

/**
 * The class is the front-end for all Ledger Entry inquiry processing.
 *
 * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry
 */
public class LedgerEntryLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerEntryLookupableHelperServiceImpl.class);

    private LaborInquiryOptionsService laborInquiryOptionsService;

    /**
     * @see org.kuali.rice.kns.lookup.Lookupable#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String propertyName) {
        if (KFSPropertyConstants.DOCUMENT_NUMBER.equals(propertyName)) {
            if (businessObject instanceof LedgerEntry) {
                LedgerEntry entry = (LedgerEntry) businessObject;
                return new AnchorHtmlData(new InquirableFinancialDocument().getInquirableDocumentUrl(entry), KFSConstants.EMPTY_STRING, "view ledger entry");
            }
        } else if (KFSPropertyConstants.POSITION_NUMBER.equals(propertyName)) {
            LedgerEntry entry = (LedgerEntry) businessObject;
            AbstractPositionDataDetailsInquirableImpl positionDataDetailsInquirable = new PositionDataDetailsInquirableImpl();

            Map<String, String> fieldValues = new HashMap<String, String>();
            fieldValues.put(propertyName, entry.getPositionNumber());

            BusinessObject positionData = positionDataDetailsInquirable.getBusinessObject(fieldValues);

            return positionData == null ? new AnchorHtmlData(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING) : positionDataDetailsInquirable.getInquiryUrl(positionData, propertyName);
        }
        return (new EntryInquirableImpl()).getInquiryUrl(businessObject, propertyName);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        setBackLocation(fieldValues.get(KFSConstants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(KFSConstants.DOC_FORM_KEY));

        // get the pending entry option. This method must be prior to the get search results
        String pendingEntryOption = laborInquiryOptionsService.getSelectedPendingEntryOption(fieldValues);

        // get the input balance type code
        String balanceTypeCode = fieldValues.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        boolean isA21Balance = StringUtils.isNotEmpty(balanceTypeCode) && BALANCE_TYPE_AC_AND_A21.equals(balanceTypeCode.trim());

        if (isA21Balance) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        }

        Collection<LedgerEntry> ledgerEntries = getLookupService().findCollectionBySearch(LedgerEntry.class, fieldValues);
        laborInquiryOptionsService.updateLedgerEntryByPendingLedgerEntry(ledgerEntries, fieldValues, pendingEntryOption);

        // add the ledger entries into the search results if the searching balance type code is A21
        if (isA21Balance) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
            Collection<LedgerEntry> effortLedgerEntries = getLookupService().findCollectionBySearch(LedgerEntry.class, fieldValues);
            laborInquiryOptionsService.updateLedgerEntryByPendingLedgerEntry(effortLedgerEntries, fieldValues, pendingEntryOption);

            ledgerEntries.addAll(effortLedgerEntries);
        }

        // get the actual size of all qualified search results
        Long actualSize = new Long(ledgerEntries.size());
        return this.buildSearchResultList(ledgerEntries, actualSize);
    }

    /**
     * build the serach result list from the given collection and the number of all qualified search results
     *
     * @param searchResultsCollection the given search results, which may be a subset of the qualified search results
     * @param actualSize              the number of all qualified search results
     * @return the serach result list with the given results and actual size
     */
    protected List buildSearchResultList(Collection searchResultsCollection, Long actualSize) {
        CollectionIncomplete results = new CollectionIncomplete(searchResultsCollection, actualSize);

        // sort list if default sort column given
        List searchResults = results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    /**
     * Sets the laborInquiryOptionsService attribute value.
     *
     * @param laborInquiryOptionsService The laborInquiryOptionsService to set.
     */
    public void setLaborInquiryOptionsService(LaborInquiryOptionsService laborInquiryOptionsService) {
        this.laborInquiryOptionsService = laborInquiryOptionsService;
    }
}
