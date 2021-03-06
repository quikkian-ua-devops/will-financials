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

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.batch.AssetDepreciationStep;
import org.kuali.kfs.module.cam.batch.AssetPaymentInfo;
import org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DepreciationBatchDaoJdbc extends PlatformAwareDaoBaseJdbc implements DepreciationBatchDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DepreciationBatchDaoJdbc.class);

    protected UniversityDateDao universityDateDao;
    protected ParameterService parameterService;

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#resetPeriodValuesWhenFirstFiscalPeriod(java.lang.Integer)
     */
    @Override
    public void resetPeriodValuesWhenFirstFiscalPeriod(Integer fiscalMonth) throws Exception {
        LOG.debug("resetPeriodValuesWhenFirstFiscalPeriod() started");

        if (fiscalMonth == 1) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Starting resetPeriodValuesWhenFirstFiscalPeriod");
            }
            // update previous year depreciation amount with sum of all periodic values for all asset payments
            getJdbcTemplate().update("UPDATE CM_AST_PAYMENT_T SET AST_PRVYRDEPR1_AMT = (COALESCE(AST_PRD1_DEPR1_AMT, 0) + COALESCE(AST_PRD2_DEPR1_AMT, 0) + COALESCE(AST_PRD3_DEPR1_AMT, 0) + COALESCE(AST_PRD4_DEPR1_AMT, 0) + COALESCE(AST_PRD5_DEPR1_AMT, 0) + COALESCE(AST_PRD6_DEPR1_AMT, 0) + COALESCE(AST_PRD7_DEPR1_AMT, 0) + COALESCE(AST_PRD8_DEPR1_AMT, 0) + COALESCE(AST_PRD9_DEPR1_AMT, 0) + COALESCE(AST_PRD10DEPR1_AMT, 0) + COALESCE(AST_PRD11DEPR1_AMT, 0) + COALESCE(AST_PRD12DEPR1_AMT, 0))");
            // reset periodic columns with zero dollar
            LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Fiscal month = 1. Therefore, initializing each month with zeros.");
            getJdbcTemplate().update("UPDATE CM_AST_PAYMENT_T SET AST_PRD1_DEPR1_AMT =0.0,  AST_PRD2_DEPR1_AMT =0.0,  AST_PRD3_DEPR1_AMT =0.0,  AST_PRD4_DEPR1_AMT =0.0,  AST_PRD5_DEPR1_AMT =0.0,  AST_PRD6_DEPR1_AMT =0.0,  AST_PRD7_DEPR1_AMT =0.0,  AST_PRD8_DEPR1_AMT =0.0,  AST_PRD9_DEPR1_AMT =0.0,  AST_PRD10DEPR1_AMT =0.0,  AST_PRD11DEPR1_AMT =0.0,  AST_PRD12DEPR1_AMT=0.0");
            if (LOG.isDebugEnabled()) {
                LOG.debug(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished resetPeriodValuesWhenFirstFiscalPeriod");
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#updateAssetPayments(java.util.List, java.lang.Integer)
     */
    @Override
    public void updateAssetPayments(final List<AssetPaymentInfo> assetPayments, final Integer fiscalMonth) {
        LOG.debug("updateAssetPayments() started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Batch updating [" + assetPayments.size() + "] payments");

        getJdbcTemplate().batchUpdate("UPDATE CM_AST_PAYMENT_T SET AST_ACUM_DEPR1_AMT=? , AST_PRD" + fiscalMonth + (fiscalMonth < 10 ? "_" : "") + "DEPR1_AMT = ?, ACCUM_RNDNG_ERR_MILLICENTS = ? WHERE CPTLAST_NBR = ? AND AST_PMT_SEQ_NBR = ? ", new BatchPreparedStatementSetter() {

            @Override
            public int getBatchSize() {
                return assetPayments.size();
            }

            @Override
            public void setValues(PreparedStatement pstmt, int index) throws SQLException {
                pstmt.setBigDecimal(1, assetPayments.get(index).getAccumulatedPrimaryDepreciationAmount().bigDecimalValue());
                pstmt.setBigDecimal(2, assetPayments.get(index).getTransactionAmount().bigDecimalValue());
                pstmt.setInt(3, assetPayments.get(index).getAccumulatedRoundingErrorInMillicents());
                pstmt.setLong(4, assetPayments.get(index).getCapitalAssetNumber());
                pstmt.setInt(5, assetPayments.get(index).getPaymentSequenceNumber());
            }
        });
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#savePendingGLEntries(java.util.List)
     */
    @Override
    public void savePendingGLEntries(final List<GeneralLedgerPendingEntry> glPendingEntries) {
        LOG.debug("savePendingGLEntries() started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Batch update of [" + glPendingEntries.size() + "] glpes");

        // we need batch insert for gl pending entry
        getJdbcTemplate().batchUpdate("INSERT INTO GL_PENDING_ENTRY_T " + " (FS_ORIGIN_CD,FDOC_NBR,TRN_ENTR_SEQ_NBR,OBJ_ID,VER_NBR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,UNIV_FISCAL_YR,UNIV_FISCAL_PRD_CD,TRN_LDGR_ENTR_DESC,TRN_LDGR_ENTR_AMT,TRN_DEBIT_CRDT_CD,TRANSACTION_DT,FDOC_TYP_CD,ORG_DOC_NBR,PROJECT_CD,ORG_REFERENCE_ID,FDOC_REF_TYP_CD,FS_REF_ORIGIN_CD,FDOC_REF_NBR,FDOC_REVERSAL_DT,TRN_ENCUM_UPDT_CD,FDOC_APPROVED_CD,ACCT_SF_FINOBJ_CD,TRNENTR_PROCESS_TM) VALUES " + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {

            @Override
            public int getBatchSize() {
                return glPendingEntries.size();
            }

            @Override
            public void setValues(PreparedStatement pstmt, int index) throws SQLException {
                GeneralLedgerPendingEntry generalLedgerPendingEntry = glPendingEntries.get(index);
                pstmt.setObject(1, generalLedgerPendingEntry.getFinancialSystemOriginationCode());
                pstmt.setObject(2, generalLedgerPendingEntry.getDocumentNumber());
                pstmt.setObject(3, generalLedgerPendingEntry.getTransactionLedgerEntrySequenceNumber());
                pstmt.setObject(4, java.util.UUID.randomUUID().toString());
                pstmt.setObject(5, generalLedgerPendingEntry.getVersionNumber());
                pstmt.setObject(6, generalLedgerPendingEntry.getChartOfAccountsCode());
                pstmt.setObject(7, generalLedgerPendingEntry.getAccountNumber());
                pstmt.setObject(8, generalLedgerPendingEntry.getSubAccountNumber());
                pstmt.setObject(9, generalLedgerPendingEntry.getFinancialObjectCode());
                pstmt.setObject(10, generalLedgerPendingEntry.getFinancialSubObjectCode());
                pstmt.setObject(11, generalLedgerPendingEntry.getFinancialBalanceTypeCode());
                pstmt.setObject(12, generalLedgerPendingEntry.getFinancialObjectTypeCode());
                pstmt.setObject(13, generalLedgerPendingEntry.getUniversityFiscalYear());
                pstmt.setObject(14, generalLedgerPendingEntry.getUniversityFiscalPeriodCode());
                pstmt.setObject(15, generalLedgerPendingEntry.getTransactionLedgerEntryDescription());
                pstmt.setObject(16, generalLedgerPendingEntry.getTransactionLedgerEntryAmount().bigDecimalValue());
                pstmt.setObject(17, generalLedgerPendingEntry.getTransactionDebitCreditCode());
                pstmt.setObject(18, generalLedgerPendingEntry.getTransactionDate());
                pstmt.setObject(19, generalLedgerPendingEntry.getFinancialDocumentTypeCode());
                pstmt.setObject(20, generalLedgerPendingEntry.getOrganizationDocumentNumber());
                pstmt.setObject(21, generalLedgerPendingEntry.getProjectCode());
                pstmt.setObject(22, generalLedgerPendingEntry.getOrganizationReferenceId());
                pstmt.setObject(23, generalLedgerPendingEntry.getReferenceFinancialDocumentTypeCode());
                pstmt.setObject(24, generalLedgerPendingEntry.getReferenceFinancialSystemOriginationCode());
                pstmt.setObject(25, generalLedgerPendingEntry.getReferenceFinancialDocumentNumber());
                pstmt.setObject(26, generalLedgerPendingEntry.getFinancialDocumentReversalDate());
                pstmt.setObject(27, generalLedgerPendingEntry.getTransactionEncumbranceUpdateCode());
                pstmt.setObject(28, generalLedgerPendingEntry.getFinancialDocumentApprovedCode());
                pstmt.setObject(29, generalLedgerPendingEntry.getAcctSufficientFundsFinObjCd());
                pstmt.setObject(30, generalLedgerPendingEntry.getTransactionEntryProcessedTs());
            }

        });
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getListOfDepreciableAssetPaymentInfo(java.lang.Integer,
     * java.lang.Integer, java.util.Calendar)
     */
    @Override
    public Collection<AssetPaymentInfo> getListOfDepreciableAssetPaymentInfo(Integer fiscalYear, Integer fiscalMonth, final Calendar depreciationDate) {
        LOG.debug("getListOfDepreciableAssetPaymentInfo() started");
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Starting to get the list of depreciable asset payment list.");

        final List<AssetPaymentInfo> assetPaymentDetails = new ArrayList<>();
        List<String> depreciationMethodList = new ArrayList<>();
        Collection<String> notAcceptedAssetStatus = parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSET_STATUS_CODES);
        depreciationMethodList.add(CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE);
        depreciationMethodList.add(CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
        Collection<String> federallyOwnedObjectSubTypes = getFederallyOwnedObjectSubTypes();
        String sql = "SELECT A0.CPTLAST_NBR,A0.AST_PMT_SEQ_NBR,A1.CPTL_AST_DEPR_DT,A1.AST_DEPR_MTHD1_CD,A1.CPTLAST_SALVAG_AMT,";
        sql = sql + "A2.CPTLAST_DEPRLF_LMT,A5.ORG_PLNT_COA_CD,A5.ORG_PLNT_ACCT_NBR,A5.CMP_PLNT_COA_CD,A5.CMP_PLNT_ACCT_NBR,A3.FIN_OBJ_TYP_CD, ";
        sql = sql + "A3.FIN_OBJ_SUB_TYP_CD, A0.AST_DEPR1_BASE_AMT,A0.FIN_OBJECT_CD, A0.AST_ACUM_DEPR1_AMT,A0.SUB_ACCT_NBR,A0.FIN_SUB_OBJ_CD,A0.PROJECT_CD, A0.FIN_COA_CD, A0.ACCUM_RNDNG_ERR_MILLICENTS";
        sql = sql + buildCriteria(fiscalYear, fiscalMonth, depreciationMethodList, notAcceptedAssetStatus, federallyOwnedObjectSubTypes, false, true);
        sql = sql + "ORDER BY A0.CPTLAST_NBR, A0.FS_ORIGIN_CD, A0.ACCOUNT_NBR, A0.SUB_ACCT_NBR, A0.FIN_OBJECT_CD, A0.FIN_SUB_OBJ_CD, A3.FIN_OBJ_TYP_CD, A0.PROJECT_CD";
        getJdbcTemplate().query(sql, preparedStatementSetter(depreciationDate), new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                int counter = 0;
                while (rs != null && rs.next()) {
                    counter++;
                    if (counter % 10000 == 0) {
                        LOG.info("Reading result row at " + new java.util.Date() + " -  " + counter);
                    }
                    AssetPaymentInfo assetPaymentInfo = new AssetPaymentInfo();
                    assetPaymentInfo.setCapitalAssetNumber(rs.getLong(1));
                    assetPaymentInfo.setPaymentSequenceNumber(rs.getInt(2));
                    assetPaymentInfo.setDepreciationDate(rs.getDate(3));
                    String deprMethod = rs.getString(4);
                    assetPaymentInfo.setPrimaryDepreciationMethodCode(deprMethod == null ? CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE : deprMethod);
                    BigDecimal salvage = rs.getBigDecimal(5);
                    assetPaymentInfo.setSalvageAmount(salvage == null ? KualiDecimal.ZERO : new KualiDecimal(salvage));
                    assetPaymentInfo.setDepreciableLifeLimit(rs.getInt(6));
                    assetPaymentInfo.setOrganizationPlantChartCode(rs.getString(7));
                    assetPaymentInfo.setOrganizationPlantAccountNumber(rs.getString(8));
                    assetPaymentInfo.setCampusPlantChartCode(rs.getString(9));
                    assetPaymentInfo.setCampusPlantAccountNumber(rs.getString(10));
                    assetPaymentInfo.setFinancialObjectTypeCode(rs.getString(11));
                    assetPaymentInfo.setFinancialObjectSubTypeCode(rs.getString(12));

                    BigDecimal primaryDeprAmt = rs.getBigDecimal(13);
                    assetPaymentInfo.setPrimaryDepreciationBaseAmount(primaryDeprAmt == null ? KualiDecimal.ZERO : new KualiDecimal(primaryDeprAmt));
                    assetPaymentInfo.setFinancialObjectCode(rs.getString(14));
                    BigDecimal accumDeprAmt = rs.getBigDecimal(15);
                    assetPaymentInfo.setAccumulatedPrimaryDepreciationAmount(accumDeprAmt == null ? KualiDecimal.ZERO : new KualiDecimal(accumDeprAmt));
                    assetPaymentInfo.setSubAccountNumber(rs.getString(16));
                    assetPaymentInfo.setFinancialSubObjectCode(rs.getString(17));
                    assetPaymentInfo.setProjectCode(rs.getString(18));
                    assetPaymentInfo.setChartOfAccountsCode(rs.getString(19));

                    assetPaymentInfo.setAccumulatedRoundingErrorInMillicents(rs.getInt(20));

                    assetPaymentDetails.add(assetPaymentInfo);
                }
                return assetPaymentDetails;
            }

        });
        LOG.info(CamsConstants.Depreciation.DEPRECIATION_BATCH + "Finished getting list of [" + assetPaymentDetails.size() + "] depreciable asset payment list.");

        return assetPaymentDetails;
    }

    protected PreparedStatementSetter preparedStatementSetter(final Calendar depreciationDate) {
        return new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement pstmt) throws SQLException {
                Calendar DateOf1900 = Calendar.getInstance();
                DateOf1900.set(1900, 0, 1);
                pstmt.setDate(1, new Date(depreciationDate.getTimeInMillis()));
                pstmt.setDate(2, new Date(DateOf1900.getTimeInMillis()));
            }
        };
    }

    protected String buildCriteria(Integer fiscalYear, Integer fiscalMonth, Collection<String> depreciationMethodList, Collection<String> notAcceptedAssetStatus, Collection<String> federallyOwnedObjectSubTypes, boolean includeFederal, boolean includePending) {
        String sql = "  FROM CM_AST_PAYMENT_T A0 INNER JOIN CM_CPTLAST_T A1 ON A0.CPTLAST_NBR=A1.CPTLAST_NBR INNER JOIN ";
        sql = sql + "CM_ASSET_TYPE_T A2 ON A1.CPTLAST_TYP_CD=A2.CPTLAST_TYP_CD INNER JOIN CA_OBJECT_CODE_T A3 ON " + fiscalYear + "=A3.UNIV_FISCAL_YR ";
        sql = sql + "AND A0.FIN_COA_CD=A3.FIN_COA_CD AND A0.FIN_OBJECT_CD=A3.FIN_OBJECT_CD INNER JOIN CA_ACCOUNT_T A4 ON A0.FIN_COA_CD=A4.FIN_COA_CD ";
        sql = sql + "AND A0.ACCOUNT_NBR=A4.ACCOUNT_NBR INNER JOIN CA_ORG_T A5 ON A4.FIN_COA_CD=A5.FIN_COA_CD AND A4.ORG_CD=A5.ORG_CD ";
        //sql = sql + "WHERE (A0.AST_DEPR1_BASE_AMT IS NOT NULL  AND  A0.AST_DEPR1_BASE_AMT <> 0) AND  (A0.AST_TRNFR_PMT_CD ";
        sql = sql + "WHERE  (A0.AST_TRNFR_PMT_CD ";
        sql = sql + "IN ('N','') OR  A0.AST_TRNFR_PMT_CD IS NULL ) AND ( A1.AST_DEPR_MTHD1_CD IS NULL OR A1.AST_DEPR_MTHD1_CD IN (" + buildINValues(depreciationMethodList) + ") ) ";
        sql = sql + "AND (A1.CPTL_AST_DEPR_DT IS NOT NULL AND A1.CPTL_AST_DEPR_DT <= ? AND A1.CPTL_AST_DEPR_DT <> ?) AND  ";
        sql = sql + "(A1.AST_RETIR_FSCL_YR IS NULL OR A1.AST_RETIR_PRD_CD IS NULL OR A1.AST_RETIR_FSCL_YR > " + fiscalYear + " OR (A1.AST_RETIR_FSCL_YR = " + fiscalYear + " AND A1.AST_RETIR_PRD_CD > " + fiscalMonth + ")) ";

        String fiscalPeriodColumn = "AST_PRD";
        if (fiscalMonth >= 1 && fiscalMonth <= 9) {
            fiscalPeriodColumn += fiscalMonth + "_DEPR1_AMT";
        } else if (fiscalMonth >= 10 && fiscalMonth <= 12) {
            fiscalPeriodColumn += fiscalMonth + "DEPR1_AMT";
        } else {
            throw new IllegalArgumentException("Fiscal month " + fiscalMonth + " must be between 1 and 12.");
        }
        sql = sql + "AND (" + fiscalPeriodColumn + " = 0 OR " + fiscalPeriodColumn + " IS NULL) ";

        sql = sql + "AND A1.AST_INVN_STAT_CD NOT IN (" + buildINValues(notAcceptedAssetStatus) + ")AND A2.CPTLAST_DEPRLF_LMT > 0 ";
        if (includeFederal) {
            sql = sql + "AND A3.FIN_OBJ_SUB_TYP_CD IN (" + buildINValues(federallyOwnedObjectSubTypes) + ")";
        } else {
            sql = sql + "AND A3.FIN_OBJ_SUB_TYP_CD NOT IN (" + buildINValues(federallyOwnedObjectSubTypes) + ")";
        }
        if (!includePending) {
            sql = sql + " AND NOT EXISTS (SELECT 1 FROM CM_AST_TRNFR_DOC_T TRFR, FS_DOC_HEADER_T HDR WHERE HDR.FDOC_NBR = TRFR.FDOC_NBR AND ";
            sql = sql + " HDR.FDOC_STATUS_CD = '" + KFSConstants.DocumentStatusCodes.ENROUTE + "' AND TRFR.CPTLAST_NBR = A0.CPTLAST_NBR) ";
            sql = sql + " AND NOT EXISTS (SELECT 1 FROM CM_AST_RETIRE_DTL_T DTL, FS_DOC_HEADER_T HDR WHERE HDR.FDOC_NBR = DTL.FDOC_NBR ";
            sql = sql + " AND HDR.FDOC_STATUS_CD = '" + KFSConstants.DocumentStatusCodes.ENROUTE + "' AND DTL.CPTLAST_NBR = A0.CPTLAST_NBR) ";
        }
        return sql;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getFullyDepreciatedAssetCount()
     */
    @Override
    public Integer getFullyDepreciatedAssetCount() {
        LOG.debug("getFullyDepreciatedAssetCount() started");

        return getJdbcTemplate().queryForInt("SELECT COUNT(1) FROM CM_CPTLAST_T AST, (SELECT CPTLAST_NBR, (SUM(AST_DEPR1_BASE_AMT - AST_ACUM_DEPR1_AMT) - (SELECT 0.0+CPTLAST_SALVAG_AMT FROM CM_CPTLAST_T X WHERE X.CPTLAST_NBR = Y.CPTLAST_NBR)) BAL FROM CM_AST_PAYMENT_T Y WHERE AST_DEPR1_BASE_AMT IS NOT NULL AND AST_DEPR1_BASE_AMT <> 0.0 AND AST_ACUM_DEPR1_AMT IS NOT NULL AND AST_ACUM_DEPR1_AMT <> 0.0 AND (AST_TRNFR_PMT_CD = 'N' OR AST_TRNFR_PMT_CD = '' OR AST_TRNFR_PMT_CD IS NULL) GROUP BY CPTLAST_NBR) PMT WHERE PMT.BAL = 0.0 AND AST.CPTLAST_NBR = PMT.CPTLAST_NBR");
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getAssetAndPaymentCount(java.lang.Integer,
     * java.lang.Integer, java.util.Calendar, boolean, java.util.List)
     */
    @Override
    public Object[] getAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, final Calendar depreciationDate, boolean includePending) {
        LOG.debug("getAssetAndPaymentCount() started");

        final Object[] data = new Object[2];
        List<String> depreciationMethodList = new ArrayList<String>();
        Collection<String> notAcceptedAssetStatus = parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSET_STATUS_CODES);
        depreciationMethodList.add(CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE);
        depreciationMethodList.add(CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
        Collection<String> federallyOwnedObjectSubTypes = getFederallyOwnedObjectSubTypes();
        String sql = "SELECT COUNT(DISTINCT A0.CPTLAST_NBR), COUNT(1) " + buildCriteria(fiscalYear, fiscalMonth, depreciationMethodList, notAcceptedAssetStatus, federallyOwnedObjectSubTypes, false, includePending);
        getJdbcTemplate().query(sql, preparedStatementSetter(depreciationDate), new ResultSetExtractor() {

            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs != null && rs.next()) {
                    data[0] = rs.getInt(1);
                    data[1] = rs.getInt(2);
                }
                return data;
            }

        });
        return data;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getFederallyOwnedAssetAndPaymentCount(java.lang.Integer,
     * java.lang.Integer, java.util.Calendar)
     */
    @Override
    public Object[] getFederallyOwnedAssetAndPaymentCount(Integer fiscalYear, Integer fiscalMonth, final Calendar depreciationDate) {
        LOG.debug("getFederallyOwnedAssetAndPaymentCount() started");

        final Object[] data = new Object[2];
        List<String> depreciationMethodList = new ArrayList<>();
        Collection<String> notAcceptedAssetStatus = parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.NON_DEPRECIABLE_NON_CAPITAL_ASSET_STATUS_CODES);
        depreciationMethodList.add(CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE);
        depreciationMethodList.add(CamsConstants.Asset.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
        Collection<String> federallyOwnedObjectSubTypes = getFederallyOwnedObjectSubTypes();
        String sql = "SELECT COUNT(DISTINCT A0.CPTLAST_NBR), COUNT(1) " + buildCriteria(fiscalYear, fiscalMonth, depreciationMethodList, notAcceptedAssetStatus, federallyOwnedObjectSubTypes, true, true);
        getJdbcTemplate().query(sql, preparedStatementSetter(depreciationDate), new ResultSetExtractor() {

            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs != null && rs.next()) {
                    data[0] = rs.getInt(1);
                    data[1] = rs.getInt(2);
                }
                return data;
            }

        });
        return data;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getPrimaryDepreciationBaseAmountForSV()
     */
    @Override
    public Map<Long, KualiDecimal> getPrimaryDepreciationBaseAmountForSV() {
        LOG.debug("getPrimaryDepreciationBaseAmountForSV() started");

        final Map<Long, KualiDecimal> amountMap = new HashMap<>();
        getJdbcTemplate().query("SELECT PMT.CPTLAST_NBR, SUM(PMT.AST_DEPR1_BASE_AMT) FROM CM_CPTLAST_T AST, CM_AST_PAYMENT_T PMT WHERE AST.CPTLAST_NBR = PMT.CPTLAST_NBR AND AST.AST_DEPR_MTHD1_CD = '" + CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE + "' GROUP BY PMT.CPTLAST_NBR", new ResultSetExtractor() {

            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs != null && rs.next()) {
                    amountMap.put(rs.getLong(1), new KualiDecimal(rs.getBigDecimal(2)));
                }
                return amountMap;
            }

        });
        return amountMap;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getAssetsWithNoDepreciation()
     */
    @Override
    public Set<Long> getAssetsWithNoDepreciation() {
        LOG.debug("getAssetsWithNoDepreciation() started");

        final Set<Long> assets = new HashSet<>();
        getJdbcTemplate().query("SELECT PMT.CPTLAST_NBR FROM CM_CPTLAST_T AST, CM_AST_PAYMENT_T PMT WHERE AST.CPTLAST_NBR = PMT.CPTLAST_NBR  GROUP BY PMT.CPTLAST_NBR HAVING SUM(ABS(COALESCE(PMT.AST_ACUM_DEPR1_AMT,0))) = 0", new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs != null && rs.next()) {
                    assets.add(rs.getLong(1));
                }
                return assets;
            }
        });
        return assets;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getTransferDocLockedAssetCount()
     */
    @Override
    public Integer getTransferDocLockedAssetCount() {
        LOG.debug("getTransferDocLockedAssetCount() started");

        return getJdbcTemplate().queryForInt("select count(1) from CM_AST_TRNFR_DOC_T t inner join FS_DOC_HEADER_T h on t.fdoc_nbr = h.fdoc_nbr where h.fdoc_status_cd ='" + KFSConstants.DocumentStatusCodes.ENROUTE + "'");
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getTransferDocPendingAssets()
     */
    @Override
    public Set<Long> getTransferDocPendingAssets() {
        LOG.debug("getTransferDocPendingAssets() started");

        final Set<Long> assets = new HashSet<>();
        getJdbcTemplate().query("select t.cptlast_nbr from CM_AST_TRNFR_DOC_T t inner join FS_DOC_HEADER_T h on t.fdoc_nbr = h.fdoc_nbr where h.fdoc_status_cd = '" + KFSConstants.DocumentStatusCodes.ENROUTE + "'", new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs != null && rs.next()) {
                    assets.add(rs.getLong(1));
                }
                return assets;
            }
        });
        return assets;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getRetireDocLockedAssetCount()
     */
    @Override
    public Integer getRetireDocLockedAssetCount() {
        LOG.debug("getRetireDocLockedAssetCount() started");

        return getJdbcTemplate().queryForInt("select count(1) from CM_AST_RETIRE_DTL_T t inner join FS_DOC_HEADER_T h on t.fdoc_nbr = h.fdoc_nbr where h.fdoc_status_cd  ='" + KFSConstants.DocumentStatusCodes.ENROUTE + "'");
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getLockedAssets()
     */
    @Override
    public Set<Long> getLockedAssets() {
        LOG.debug("getLockedAssets() started");

        final Set<Long> assets = new HashSet<>();
        getJdbcTemplate().query("select t.cptlast_nbr from CM_AST_RETIRE_DTL_T t inner join FS_DOC_HEADER_T h on t.fdoc_nbr = h.fdoc_nbr where h.fdoc_status_cd = '" + KFSConstants.DocumentStatusCodes.ENROUTE + "' union select t.cptlast_nbr from CM_AST_TRNFR_DOC_T t inner join FS_DOC_HEADER_T h on t.fdoc_nbr = h.fdoc_nbr where h.fdoc_status_cd = '" + KFSConstants.DocumentStatusCodes.ENROUTE + "'", new ResultSetExtractor() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                assets.add(rs.getLong(1));
                return assets;
            }
        });
        return assets;
    }

    /**
     * Utility method that will convert a list into IN string clause for SQL
     *
     * @param list values
     * @return concatenated string
     */
    protected String buildINValues(Collection<String> list) {
        if (list.isEmpty()) {
            return "''";
        }
        String returnValue = "";
        for (String string : list) {
            returnValue = returnValue + "'" + string + "',";
        }
        return returnValue.substring(0, returnValue.lastIndexOf(','));
    }

    /**
     * This method the value of the system parameter NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES
     *
     * @return
     */
    protected Collection<String> getFederallyOwnedObjectSubTypes() {
        return parameterService.parameterExists(AssetDepreciationStep.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES) ? parameterService.getParameterValuesAsString(AssetDepreciationStep.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES) : new ArrayList<>();
    }

    /**
     * Depreciation (end of year) Period 13 assets incorrect depreciation start date.
     *
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#getAssetsByDepreciationConvention(org.kuali.kfs.sys.businessobject.UniversityDate, java.util.List, java.lang.String)
     */
    @Override
    public List<Map<String, Object>> getAssetsByDepreciationConvention(Date lastFiscalYearDate, List<String> movableEquipmentObjectSubTypes, String depreciationConventionCd) {
        LOG.debug("getAssetsByDepreciationConvention() started");

        String sql = "SELECT A0.CPTLAST_NBR FROM CM_CPTLAST_T A0, CM_AST_DEPR_CNVNTN_T A1, CM_ASSET_TYPE_T A2 WHERE A0.CPTLAST_CRT_DT > ? AND A0.FIN_OBJ_SUB_TYP_CD IN (" + buildINValues(movableEquipmentObjectSubTypes) + ")" + " AND A0.FIN_OBJ_SUB_TYP_CD = A1.FIN_OBJ_SUB_TYPE_CD AND A1.CPTL_AST_DEPR_CNVNTN_CD = ? AND A2.CPTLAST_TYP_CD=A0.CPTLAST_TYP_CD AND A0.CPTLAST_TYP_CD IS NOT NULL AND A2.CPTLAST_DEPRLF_LMT IS NOT NULL AND A2.CPTLAST_DEPRLF_LMT != 0";
        return getJdbcTemplate().queryForList(sql, new Object[]{lastFiscalYearDate, depreciationConventionCd});
    }

    /**
     * @see org.kuali.kfs.module.cam.document.dataaccess.DepreciationBatchDao#updateAssetInServiceAndDepreciationDate(java.util.List, org.kuali.kfs.sys.businessobject.UniversityDate, java.sql.Date)
     */
    @Override
    public void updateAssetInServiceAndDepreciationDate(List<String> selectedAssets, final Date inServiceDate, final Date depreciationDate) {
        LOG.debug("updateAssetInServiceAndDepreciationDate() started");

        final AccountingPeriod acctPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(inServiceDate);
        getJdbcTemplate().update("UPDATE CM_CPTLAST_T SET CPTL_AST_IN_SRVC_DT=?, CPTL_AST_DEPR_DT=?, FDOC_POST_PRD_CD=? , FDOC_POST_YR=? WHERE CPTLAST_NBR IN (" + buildINValues(selectedAssets) + ")", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setDate(1, inServiceDate);
                ps.setDate(2, depreciationDate);
                ps.setString(3, acctPeriod.getUniversityFiscalPeriodCode());
                ps.setInt(4, acctPeriod.getUniversityFiscalYear());
            }
        });
    }

    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
