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
package org.kuali.kfs.module.ec.service.impl;

import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.service.EffortCertificationDetailBuildService;
import org.kuali.kfs.module.ec.service.EffortCertificationDocumentBuildService;
import org.kuali.kfs.module.ec.util.LedgerBalanceConsolidationHelper;
import org.kuali.kfs.module.ec.util.PayrollAmountHolder;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class Provide the facility used to generate documents (build) from the labor ledger balances
 */
@Transactional
public class EffortCertificationDocumentBuildServiceImpl implements EffortCertificationDocumentBuildService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentBuildServiceImpl.class);

    protected EffortCertificationDetailBuildService effortCertificationDetailBuildService;
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentBuildService#removeExistingDocumentBuild(java.util.Map)
     */
    public void removeExistingDocumentBuild(Map<String, String> fieldValues) {
        Collection<EffortCertificationDocumentBuild> documents = businessObjectService.findMatching(EffortCertificationDocumentBuild.class, fieldValues);
        businessObjectService.delete(new ArrayList<EffortCertificationDocumentBuild>(documents));
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentBuildService#generateDocumentBuild(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition,
     * java.util.List, java.util.Map)
     */
    public List<EffortCertificationDocumentBuild> generateDocumentBuildList(Integer postingYear, EffortCertificationReportDefinition reportDefinition, List<LaborLedgerBalance> ledgerBalances) {
        List<EffortCertificationDocumentBuild> documentList = new ArrayList<EffortCertificationDocumentBuild>();

        Map<String, List<LaborLedgerBalance>> ledgerBalanceGroups = buildLedgerBalanceGroups(ledgerBalances);
        for (String key : ledgerBalanceGroups.keySet()) {
            List<LaborLedgerBalance> balanceList = ledgerBalanceGroups.get(key);

            EffortCertificationDocumentBuild document = this.generateDocumentBuild(postingYear, reportDefinition, balanceList);
            documentList.add(document);
        }

        return documentList;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentBuildService#generateDocumentBuild(org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition,
     * java.util.List, java.util.Map)
     */
    public EffortCertificationDocumentBuild generateDocumentBuild(Integer postingYear, EffortCertificationReportDefinition reportDefinition, List<LaborLedgerBalance> ledgerBalances) {
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();

        KualiDecimal totalAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalances, reportPeriods);
        PayrollAmountHolder payrollAmountHolder = new PayrollAmountHolder(totalAmount, KualiDecimal.ZERO, 0);

        LaborLedgerBalance headOfBalanceList = ledgerBalances.get(0);
        EffortCertificationDocumentBuild document = populateDocument(reportDefinition, headOfBalanceList);
        List<EffortCertificationDetailBuild> detailLineList = document.getEffortCertificationDetailLinesBuild();

        for (LaborLedgerBalance balance : ledgerBalances) {
            EffortCertificationDetailBuild detailLine = effortCertificationDetailBuildService.generateDetailBuild(postingYear, balance, reportDefinition);
            detailLine.setEffortCertificationBuildNumber(document.getEffortCertificationBuildNumber());

            payrollAmountHolder.setPayrollAmount(detailLine.getEffortCertificationPayrollAmount());
            PayrollAmountHolder.calculatePayrollPercent(payrollAmountHolder);

            detailLine.setEffortCertificationCalculatedOverallPercent(payrollAmountHolder.getPayrollPercent());
            detailLine.setEffortCertificationUpdatedOverallPercent(payrollAmountHolder.getPayrollPercent());

            this.updateDetailLineList(detailLineList, detailLine);
        }

        return document;
    }

    /**
     * populate a document build object through the given information
     *
     * @param reportDefinition the given report definition
     * @param ledgerBalance    the given ledger balance
     * @return a dument build object populated with the given information
     */
    protected static EffortCertificationDocumentBuild populateDocument(EffortCertificationReportDefinition reportDefinition, LaborLedgerBalance ledgerBalance) {
        EffortCertificationDocumentBuild document = new EffortCertificationDocumentBuild();

        document.setEffortCertificationBuildNumber(null);
        document.setEffortCertificationDocumentCode(false);

        document.setEffortCertificationReportNumber(reportDefinition.getEffortCertificationReportNumber());
        document.setUniversityFiscalYear(reportDefinition.getUniversityFiscalYear());
        document.setEmplid(ledgerBalance.getEmplid());

        return document;
    }

    /**
     * group the given ledger balances according to the combination of the values in the specified fields
     *
     * @param ledgerBalances the given ledger balances
     * @return the map holding ledger balance groups
     */
    protected Map<String, List<LaborLedgerBalance>> buildLedgerBalanceGroups(List<LaborLedgerBalance> ledgerBalances) {
        Map<String, List<LaborLedgerBalance>> ledgerBalanceGroups = new HashMap<String, List<LaborLedgerBalance>>();

        for (LaborLedgerBalance balance : ledgerBalances) {
            String consolidationKeys = balance.getEmplid();
            LedgerBalanceConsolidationHelper.groupLedgerBalancesByKeys(ledgerBalanceGroups, balance, consolidationKeys);
        }
        return ledgerBalanceGroups;
    }


    /**
     * update the given detail line if the given detail line is in the list; otherwise, add the given line into the list
     *
     * @param detailLineList the given list of detail lines
     * @param detailLine     the given detail line
     */
    protected void updateDetailLineList(List<EffortCertificationDetailBuild> detailLineList, EffortCertificationDetailBuild detailLine) {
        int index = detailLineList.indexOf(detailLine);
        if (index >= 0) {
            EffortCertificationDetailBuild existingDetailLine = detailLineList.get(index);

            int calculatedOverallPercent = existingDetailLine.getEffortCertificationCalculatedOverallPercent() + detailLine.getEffortCertificationCalculatedOverallPercent();
            existingDetailLine.setEffortCertificationCalculatedOverallPercent(calculatedOverallPercent);

            int updatedOverallPercent = existingDetailLine.getEffortCertificationUpdatedOverallPercent() + detailLine.getEffortCertificationUpdatedOverallPercent();
            existingDetailLine.setEffortCertificationUpdatedOverallPercent(updatedOverallPercent);

            KualiDecimal originalPayrollAmount = existingDetailLine.getEffortCertificationOriginalPayrollAmount().add(detailLine.getEffortCertificationOriginalPayrollAmount());
            existingDetailLine.setEffortCertificationOriginalPayrollAmount(originalPayrollAmount);

            KualiDecimal payrollAmount = existingDetailLine.getEffortCertificationPayrollAmount().add(detailLine.getEffortCertificationPayrollAmount());
            existingDetailLine.setEffortCertificationPayrollAmount(payrollAmount);

        } else {
            detailLineList.add(detailLine);
        }
    }

    /**
     * Sets the effortCertificationDetailBuildService attribute value.
     *
     * @param effortCertificationDetailBuildService The effortCertificationDetailBuildService to set.
     */
    public void setEffortCertificationDetailBuildService(EffortCertificationDetailBuildService effortCertificationDetailBuildService) {
        this.effortCertificationDetailBuildService = effortCertificationDetailBuildService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
