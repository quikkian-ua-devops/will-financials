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
package org.kuali.kfs.module.ar.document;

import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;

import java.util.List;
import java.util.Map;

/**
 * Maintainable class for Dunning Campaign.
 */
public class DunningCampaignMaintainableImpl extends FinancialSystemMaintainable {

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> requestParameters) {

        DunningCampaign dunningCampaign = (DunningCampaign) getBusinessObject();
        List<DunningLetterDistribution> dunningLetterDistributions = dunningCampaign.getDunningLetterDistributions();

        for (DunningLetterDistribution dunningLetterDistribution : dunningLetterDistributions) {
            dunningLetterDistribution.setDunningLetterDistributionID(null);
        }

        super.processAfterCopy(document, requestParameters);
    }


}
