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

package org.kuali.kfs.module.cam.document.dataaccess.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciableAssetsDao;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DepreciableAssetsDaoOjb extends PlatformAwareDaoBaseOjb implements DepreciableAssetsDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DepreciableAssetsDaoOjb.class);

    private DepreciationBatchDao depreciationBatchDao;

    protected final static String[] REPORT_GROUP = {"*** BEFORE RUNNING DEPRECIATION PROCESS ****", "*** AFTER RUNNING DEPRECIATION PROCESS ****"};

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciableAssetsDao#generateStatistics(boolean, java.lang.String,
     * java.lang.Integer, java.lang.Integer, java.util.Calendar)
     */
    public List<String[]> generateStatistics(boolean beforeDepreciationReport, List<String> documentNumbers, Integer fiscalYear, Integer fiscalMonth, Calendar depreciationDate, String depreciationRunDate, Collection<AssetObjectCode> assetObjectCodes, int fiscalStartMonth, String errorMessage) {
        LOG.debug("generateStatistics() started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "generating statistics for report - " + (beforeDepreciationReport ? "Before part." : "After part"));

        List<String[]> reportLine = new ArrayList<>();

        NumberFormat usdFormat = NumberFormat.getCurrencyInstance(Locale.US);
        KualiDecimal amount;
        String[] columns = new String[2];


        columns[1] = "******************";
        if (beforeDepreciationReport)
            columns[0] = REPORT_GROUP[0];
        else
            columns[0] = REPORT_GROUP[1];

        reportLine.add(columns.clone());

        if (beforeDepreciationReport) {
            columns[0] = "Depreciation Run Date";
            columns[1] = depreciationRunDate;
            reportLine.add(columns.clone());


            columns[0] = "Fiscal Year";
            columns[1] = (fiscalYear.toString());
            reportLine.add(columns.clone());

            columns[0] = "Fiscal Month";
            columns[1] = (fiscalMonth.toString());
            reportLine.add(columns.clone());

            columns[0] = "Number of assets fully depreciated";
            columns[1] = depreciationBatchDao.getFullyDepreciatedAssetCount().toString();
            // columns[1] = getFullyDepreciatedAssetCount().toString();
            reportLine.add(columns.clone());
        }

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting DocumentHeader row count.");
        ReportQueryByCriteria q = QueryFactory.newReportQuery(DocumentHeader.class, new Criteria());
        q.setAttributes(new String[]{"count(*)"});
        Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);

        Object[] data = (Object[]) i.next();
        columns[0] = "Document header table - record count";
        columns[1] = (convertCountValueToString(data[0]));
        reportLine.add(columns.clone());

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting general ledger pending entry row count.");
        q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, new Criteria());
        q.setAttributes(new String[]{"count(*)"});
        i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        data = (Object[]) i.next();
        columns[0] = "General ledger pending entry table - record count";
        columns[1] = (convertCountValueToString(data[0]));
        reportLine.add(columns.clone());

        if (beforeDepreciationReport) {
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting assets row count.");
            q = QueryFactory.newReportQuery(Asset.class, new Criteria());
            q.setAttributes(new String[]{"count(*)"});
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();
            columns[0] = "Asset table - record count";
            columns[1] = (convertCountValueToString(data[0]));
            reportLine.add(columns.clone());
        }

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting assets payment row count, depreciation base amount, accumulated depreciation amount, and every months depreciation amount.");
        q = QueryFactory.newReportQuery(AssetPayment.class, new Criteria());
        q.setAttributes(new String[]{"count(*)", "SUM(" + CamsPropertyConstants.AssetPayment.PRIMARY_DEPRECIATION_BASE_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.ACCUMULATED_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PREVIOUS_YEAR_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_1_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_2_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_3_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_4_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_5_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_6_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_7_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_8_DEPRECIATION_AMOUNT + ")",
            "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_9_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_10_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_11_DEPRECIATION_AMOUNT + ")", "SUM(" + CamsPropertyConstants.AssetPayment.PERIOD_12_DEPRECIATION_AMOUNT + ")"});

        i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        data = new Object[16];
        if (i.hasNext()) {
            data = (Object[]) i.next();
        } else {
            for (int c = 0; c < 16; c++)
                data[c] = new KualiDecimal(0);
        }

        if (beforeDepreciationReport) {
            columns[0] = "Asset payment table - record count";
            columns[1] = (convertCountValueToString(data[0]));
            reportLine.add(columns.clone());
        }

        columns[0] = "Depreciation base amount";
        columns[1] = (usdFormat.format((KualiDecimal) data[1]));
        reportLine.add(columns.clone());

        columns[0] = "Current year - accumulated depreciation";
        columns[1] = (usdFormat.format((KualiDecimal) data[2]));
        reportLine.add(columns.clone());

        columns[0] = "Previous year - accumulated depreciation";
        columns[1] = (usdFormat.format((KualiDecimal) data[3]));
        reportLine.add(columns.clone());

        // *******************************************************************************************************************************

        // Adding monthly depreciation amounts
        KualiDecimal yearToDateDepreciationAmt = new KualiDecimal(0);

        boolean isJanuaryTheFirstFiscalMonth = (fiscalStartMonth == 1);
        int col = 4;
        int currentMonth = fiscalStartMonth - 1;
        for (int monthCounter = 1; monthCounter <= 12; monthCounter++, currentMonth++) {
            columns[0] = CamsConstants.MONTHS[currentMonth] + " depreciation amount";
            columns[1] = (usdFormat.format((KualiDecimal) data[col]));
            reportLine.add(columns.clone());

            yearToDateDepreciationAmt = yearToDateDepreciationAmt.add((KualiDecimal) data[col]);

            col++;

            if (!isJanuaryTheFirstFiscalMonth) {
                if (currentMonth == 11) {
                    currentMonth = -1;
                }
            }
        }

        columns[0] = "Year to date depreciation amount";
        columns[1] = (usdFormat.format(yearToDateDepreciationAmt));
        reportLine.add(columns.clone());

        if (beforeDepreciationReport) {
            int federallyOwnedAssetPaymentCount = Integer.valueOf(depreciationBatchDao.getFederallyOwnedAssetAndPaymentCount(fiscalYear, fiscalMonth, depreciationDate)[1].toString());
            int retiredAndTransferredAssetCount = depreciationBatchDao.getTransferDocLockedAssetCount() + depreciationBatchDao.getRetireDocLockedAssetCount();

            columns[0] = "Object code table - record count";
            columns[1] = (convertCountValueToString(this.getAssetObjectCodesCount(fiscalYear)));
            reportLine.add(columns.clone());

            columns[0] = "Plant fund account table - record count";
            columns[1] = (convertCountValueToString(this.getCOAsCount()));
            reportLine.add(columns.clone());

            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting asset payment row count , depreciation base amount, accumulated depreciation amount, and every months depreciation amount.");
            data = depreciationBatchDao.getAssetAndPaymentCount(fiscalYear, fiscalMonth, depreciationDate, true);
            int eligibleAssetPaymentCount = new Integer(data[1].toString());

            int totalAssetPayments = (eligibleAssetPaymentCount + federallyOwnedAssetPaymentCount);

            columns[0] = "Asset payments eligible for depreciation";
            columns[1] = totalAssetPayments + "";
            reportLine.add(columns.clone());

            columns[0] = "Number of assets with pending AR or AT documents";
            columns[1] = retiredAndTransferredAssetCount + "";
            reportLine.add(columns.clone());

            columns[0] = "Asset payments ineligible for depreciation (Federally owned assets)";
            columns[1] = federallyOwnedAssetPaymentCount + "";
            reportLine.add(columns.clone());

            columns[0] = "Asset payments eligible for depreciation - After excluding federally owned assets";
            columns[1] = eligibleAssetPaymentCount + "";
            reportLine.add(columns.clone());

            columns[0] = "Assets eligible for depreciation";
            columns[1] = data[0].toString();
            reportLine.add(columns.clone());

            Set<Long> transferDocPendingAssets = depreciationBatchDao.getTransferDocPendingAssets();
            if (transferDocPendingAssets.size() > 0) {
                columns[0] = "Assets with pending transfer documents";
                columns[1] = StringUtils.join(transferDocPendingAssets, ",");
                reportLine.add(columns.clone());
            }
        }


        if (!beforeDepreciationReport) {
            // Generating a list of depreciation expense object codes.
            List<String> depreExpObjCodes = this.getExpenseObjectCodes(assetObjectCodes);

            // Generating a list of accumulated depreciation object codes.
            List<String> accumulatedDepreciationObjCodes = this.getAccumulatedDepreciationObjectCodes(assetObjectCodes);

            KualiDecimal debits = new KualiDecimal(0);
            KualiDecimal credits = new KualiDecimal(0);

            // Document Number created
            columns[0] = "Document Number(s)";
            columns[1] = documentNumbers.toString();
            reportLine.add(columns.clone());

            // Expense Debit
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "calculating the debit amount for expense object codes.");
            Criteria criteria = new Criteria();
            criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, depreExpObjCodes);
            criteria.addEqualTo(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
            criteria.addIn(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumbers);

            q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
            q.setAttributes(new String[]{"SUM(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")"});
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();

            amount = (data[0] == null ? new KualiDecimal(0) : (KualiDecimal) data[0]);
            KualiDecimal deprAmtDebit = amount;
            columns[0] = "Debit - Depreciation Expense object codes: " + depreExpObjCodes.toString();
            columns[1] = (usdFormat.format(amount));
            reportLine.add(columns.clone());
            debits = debits.add(amount);

            // Accumulated Depreciation credit
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "calculating the credit amount for accumulated depreciation object codes.");
            criteria = new Criteria();
            criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accumulatedDepreciationObjCodes);
            criteria.addEqualTo(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_CREDIT_CODE);
            criteria.addIn(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumbers);

            q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
            q.setAttributes(new String[]{"SUM(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")"});
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();
            amount = (data[0] == null ? new KualiDecimal(0) : (KualiDecimal) data[0]);
            columns[0] = "Credit - Accumulated depreciation object codes: " + accumulatedDepreciationObjCodes.toString();
            columns[1] = (usdFormat.format(amount));
            reportLine.add(columns.clone());
            credits = credits.add(amount);
            // ***********************************************************************************************

            // Accumulated Depreciation debit
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "calculating the debit amount for accumulated depreciation object codes.");
            criteria = new Criteria();
            criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accumulatedDepreciationObjCodes);
            criteria.addEqualTo(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_DEBIT_CODE);
            criteria.addIn(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumbers);

            q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
            q.setAttributes(new String[]{"SUM(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")"});
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();
            amount = (data[0] == null ? new KualiDecimal(0) : (KualiDecimal) data[0]);

            columns[0] = "Debit - Accumulated depreciation object codes:" + accumulatedDepreciationObjCodes.toString();
            columns[1] = (usdFormat.format(amount));
            reportLine.add(columns.clone());
            debits = debits.add(amount);

            // Expense credit
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "calculating the credit amount for expense object codes.");
            criteria = new Criteria();
            criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, depreExpObjCodes);
            criteria.addEqualTo(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_CREDIT_CODE);
            criteria.addIn(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumbers);

            q = QueryFactory.newReportQuery(GeneralLedgerPendingEntry.class, criteria);
            q.setAttributes(new String[]{"SUM(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")"});
            i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
            data = (Object[]) i.next();
            amount = (data[0] == null ? new KualiDecimal(0) : (KualiDecimal) data[0]);
            KualiDecimal deprAmtCredit = amount;
            columns[0] = "Credit - Depreciation Expense object codes:" + depreExpObjCodes.toString();
            columns[1] = (usdFormat.format(amount));
            reportLine.add(columns.clone());
            credits = credits.add(amount);

            columns[0] = "Current Month";
            columns[1] = usdFormat.format(deprAmtDebit.subtract(deprAmtCredit));
            reportLine.add(columns.clone());

            columns[0] = "Total Debits";
            columns[1] = usdFormat.format(debits);
            reportLine.add(columns.clone());

            columns[0] = "Total Credits";
            columns[1] = usdFormat.format(credits);
            reportLine.add(columns.clone());

            columns[0] = "Total Debits - Total Credits";
            columns[1] = usdFormat.format(debits.subtract(credits));
            reportLine.add(columns.clone());
        }

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished generating statistics for report - " + (beforeDepreciationReport ? "Before part." : "After part"));
        return reportLine;
    }


    /**
     * This method returns the number of records found resulting from a join of the organization table and the account table
     *
     * @param fiscalYear
     * @return
     */
    protected Object getCOAsCount() {
        LOG.debug("getCOAsCount() started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting the number of campus plant fund accounts.");

        Criteria criteria = new Criteria();
        Object[] data;
        ReportQueryByCriteria q = QueryFactory.newReportQuery(Account.class, criteria);
        q.setAttributes(new String[]{"count(" + KFSPropertyConstants.ORGANIZATION + "." + KFSPropertyConstants.CAMPUS_PLANT_ACCOUNT_NUMBER + ")"});
        Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (!i.hasNext()) {
            data = new Object[1];
            data[0] = new BigDecimal(0);
        } else {
            data = (Object[]) i.next();
        }

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finised getting the number of campus plant fund accounts.");
        return data[0];
    }

    /**
     * This method gets a list of Expense object codes from the asset object code table for a particular fiscal year
     *
     * @param fiscalYear
     * @return a List<String>
     */
    protected List<String> getExpenseObjectCodes(Collection<AssetObjectCode> assetObjectCodesCollection) {
        LOG.debug("getExpenseObjectCodes() started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting expense object codes");

        List<String> depreExpObjCodes = new ArrayList<String>();

        // Creating a list of depreciation expense object codes.
        for (Iterator<AssetObjectCode> iterator = assetObjectCodesCollection.iterator(); iterator.hasNext(); ) {
            AssetObjectCode assetObjectCode = iterator.next();

            String objCode = assetObjectCode.getDepreciationExpenseFinancialObjectCode();
            if (objCode != null && !objCode.equals("") && !depreExpObjCodes.contains(objCode)) {
                depreExpObjCodes.add(objCode);
            }
        }
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting expense object codes which are:" + depreExpObjCodes.toString());
        return depreExpObjCodes;
    }

    /**
     * This method gets a list of Accumulated Depreciation Object Codes from the asset object code table for a particular fiscal
     * year.
     *
     * @param fiscalYear
     * @return List<String>
     */
    protected List<String> getAccumulatedDepreciationObjectCodes(Collection<AssetObjectCode> assetObjectCodesCollection) {
        LOG.debug("getAccumulatedDepreciationObjectCodes() started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting accum depreciation object codes");

        List<String> accumulatedDepreciationObjCodes = new ArrayList<>();

        // Creating a list of depreciation expense object codes.
        for (Iterator<AssetObjectCode> iterator = assetObjectCodesCollection.iterator(); iterator.hasNext(); ) {
            AssetObjectCode assetObjectCode = iterator.next();

            String objCode = assetObjectCode.getAccumulatedDepreciationFinancialObjectCode();
            if (objCode != null && !objCode.equals("") && !accumulatedDepreciationObjCodes.contains(objCode)) {
                accumulatedDepreciationObjCodes.add(objCode);
            }
        }
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting accum depreciation object codes which are:" + accumulatedDepreciationObjCodes.toString());

        return accumulatedDepreciationObjCodes;
    }

    /**
     * This method counts the number of assets that exist in both chart of accounts object code table and capital asset object code
     * tables
     *
     * @param fiscalYear
     * @return number of object codes found
     */
    protected Object getAssetObjectCodesCount(Integer fiscalYear) {
        LOG.debug("getAssetObjectCodesCount() started");

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Getting asset object code count.");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(CamsPropertyConstants.AssetObject.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(AssetObjectCode.class, criteria);
        q.setAttributes(new String[]{"count(" + KFSPropertyConstants.OBJECT_CODE + "." + KFSPropertyConstants.FINANCIAL_OBJECT_CODE + ")"});
        Iterator<Object> i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        Object[] data = (Object[]) i.next();

        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finisned getting asset object code count which is: " + data[0]);
        LOG.debug("DepreciableAssetsDAoOjb.getAssetObjectCodesCount() -  ended");
        return data[0];
    }

    /**
     * This method converts a variable of type object to BigDecimal or a Long type in order to return a string
     *
     * @param fieldValue
     * @return String
     */
    protected String convertCountValueToString(Object fieldValue) {
        if (fieldValue == null)
            return "0.0";

        if (fieldValue instanceof BigDecimal) {
            return ((BigDecimal) fieldValue).toString();
        } else {
            return ((Long) fieldValue).toString();
        }
    }

    public void setDepreciationBatchDao(DepreciationBatchDao depreciationBatchDao) {
        this.depreciationBatchDao = depreciationBatchDao;
    }
}
