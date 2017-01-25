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
package org.kuali.kfs.module.tem.batch.service;

import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.module.tem.businessobject.CreditCardImportData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.sys.batch.BatchInputFileType;

import java.util.List;

public interface CreditCardDataImportService {
    public boolean importCreditCardData();

    public boolean importCreditCardDataFile(String dataFileName, BatchInputFileType inputFileType);

    public boolean isDuplicate(CreditCardStagingData creditCardData, List<ErrorMessage> errorMessages);

    public TemProfileAccount findTraveler(CreditCardStagingData creditCardData);

    public List<CreditCardStagingData> validateCreditCardData(CreditCardImportData creditCardList, String dataFileName);

    public boolean validateAndSetCreditCardAgency(CreditCardStagingData creditCardData);

    public boolean moveCreditCardDataToHistoricalExpenseTable();
}
