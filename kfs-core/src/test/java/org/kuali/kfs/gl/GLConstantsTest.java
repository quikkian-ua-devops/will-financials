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
package org.kuali.kfs.gl;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

/**
 * A test to cover GeneralLedgerConstants
 */
@ConfigureContext
public class GLConstantsTest extends KualiTestBase {
    /**
     * Tests that none of the space constants in GeneralLedgerConstants return null.
     */
    public void testDDSpaceConstants() {
        assertNotNull(GeneralLedgerConstants.getSpaceDebitCreditCode());
        assertNotNull(GeneralLedgerConstants.getSpaceUniversityFiscalPeriodCode());
        assertNotNull(GeneralLedgerConstants.getSpaceBalanceTypeCode());
        assertNotNull(GeneralLedgerConstants.getSpaceFinancialSystemOriginationCode());
        assertNotNull(GeneralLedgerConstants.getSpaceFinancialObjectCode());
        assertNotNull(GeneralLedgerConstants.getSpaceTransactionDate());
        assertNotNull(GeneralLedgerConstants.getSpaceUniversityFiscalYear());
        assertNotNull(GeneralLedgerConstants.getSpaceTransactionEntrySequenceNumber());
        assertNotNull(GeneralLedgerConstants.getSpaceTransactionLedgetEntryDescription());
        assertNotNull(GeneralLedgerConstants.getSpaceSubAccountTypeCode());
    }
}
