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
package org.kuali.kfs.gl.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;


/**
 * Interface for EntryHistory used by GL and Labor
 */
public interface LedgerEntryHistory extends BusinessObject {
    public Integer getUniversityFiscalYear();

    public String getChartOfAccountsCode();

    public String getFinancialObjectCode();

    public String getFinancialBalanceTypeCode();

    public String getUniversityFiscalPeriodCode();

    public String getTransactionDebitCreditCode();

    public KualiDecimal getTransactionLedgerEntryAmount();

    public Integer getRowCount();
}
