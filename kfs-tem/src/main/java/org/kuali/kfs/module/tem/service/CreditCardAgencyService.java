/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service;

import java.util.List;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;

public interface CreditCardAgencyService {

    /**
     * Get a list of the credit card agency of type Corp Card
     *
     * @return
     */
    public List<CreditCardAgency> getCorpCreditCardAgencyList();

    /**
     * Get a list of the credit card agency of type Corp Card and its credit card agency code
     *
     * @return
     */
    public List<String> getCorpCreditCardAgencyCodeList();

    /**
     * Get Credit Card Agency by credit card agency code
     *
     * @return
     */
    public CreditCardAgency getCreditCardAgencyByCode(String code);

}
