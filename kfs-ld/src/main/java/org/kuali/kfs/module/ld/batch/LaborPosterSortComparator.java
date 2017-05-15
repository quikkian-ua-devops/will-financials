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
package org.kuali.kfs.module.ld.batch;

import org.kuali.kfs.module.ld.businessobject.LaborOriginEntryFieldUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;

import java.util.Comparator;
import java.util.Map;

public class LaborPosterSortComparator implements Comparator<String> {

    LaborOriginEntryFieldUtil loefu = new LaborOriginEntryFieldUtil();
    Map<String, Integer> pMap = loefu.getFieldBeginningPositionMap();

    private class Range {
        public Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int start;
        public int end;
    }

    Range[] compareRanges;

    {
        compareRanges = new Range[8];
        compareRanges[0] = new Range(pMap.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR), pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE));
        compareRanges[1] = new Range(pMap.get(KFSPropertyConstants.PROJECT_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC));
        compareRanges[2] = new Range(pMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID), pMap.get(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE));
        compareRanges[3] = new Range(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));
        compareRanges[4] = new Range(pMap.get(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER), pMap.get(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID));
        compareRanges[5] = new Range(pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER), pMap.get(KFSPropertyConstants.POSITION_NUMBER));
        compareRanges[6] = new Range(pMap.get(KFSPropertyConstants.POSITION_NUMBER), pMap.get(KFSPropertyConstants.PROJECT_CODE));
        compareRanges[7] = new Range(pMap.get(KFSPropertyConstants.EMPLID), pMap.get(KFSPropertyConstants.EARN_CODE));
    }


    public int compare(String string1, String string2) {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(string1.substring(compareRanges[0].start, compareRanges[0].end));
        sb1.append(string1.substring(compareRanges[1].start, compareRanges[1].end));
        sb1.append(string1.substring(compareRanges[2].start, compareRanges[2].end));
        sb1.append(string1.substring(compareRanges[3].start, compareRanges[3].end));
        sb1.append(string1.substring(compareRanges[4].start, compareRanges[4].end));
        sb1.append(string1.substring(compareRanges[5].start, compareRanges[5].end));
        sb1.append(string1.substring(compareRanges[6].start, compareRanges[6].end));
        sb1.append(string1.substring(compareRanges[7].start, compareRanges[7].end));

        StringBuilder sb2 = new StringBuilder();
        sb2.append(string2.substring(compareRanges[0].start, compareRanges[0].end));
        sb2.append(string2.substring(compareRanges[1].start, compareRanges[1].end));
        sb2.append(string2.substring(compareRanges[2].start, compareRanges[2].end));
        sb2.append(string2.substring(compareRanges[3].start, compareRanges[3].end));
        sb2.append(string2.substring(compareRanges[4].start, compareRanges[4].end));
        sb2.append(string2.substring(compareRanges[5].start, compareRanges[5].end));
        sb2.append(string2.substring(compareRanges[6].start, compareRanges[6].end));
        sb2.append(string2.substring(compareRanges[7].start, compareRanges[7].end));

        return sb1.toString().compareTo(sb2.toString());
    }
}
