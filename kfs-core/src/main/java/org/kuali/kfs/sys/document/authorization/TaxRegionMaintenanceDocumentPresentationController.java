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
package org.kuali.kfs.sys.document.authorization;

import org.kuali.kfs.kns.document.MaintenanceDocument;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;

import java.util.Date;
import java.util.Set;

public class TaxRegionMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        TaxRegion taxRegion = (TaxRegion) document.getNewMaintainableObject().getBusinessObject();
        if (taxRegion != null) {
            Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentDate();

            int index = 0;
            for (TaxRegionRate taxRegionRate : taxRegion.getTaxRegionRates()) {
                int comparison = taxRegionRate.getEffectiveDate().compareTo(currentDate);

                if (comparison <= 0) {
                    readOnlyPropertyNames.add("taxDistrictRates[" + index + "].taxRate");
                }

                index++;
            }
        }

        return readOnlyPropertyNames;
    }
}
