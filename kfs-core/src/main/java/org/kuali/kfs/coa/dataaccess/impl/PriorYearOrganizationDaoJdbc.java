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
package org.kuali.kfs.coa.dataaccess.impl;

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.PriorYearOrganization;
import org.kuali.kfs.coa.dataaccess.PriorYearOrganizationDao;
import org.kuali.rice.core.framework.persistence.jdbc.dao.PlatformAwareDaoBaseJdbc;

public class PriorYearOrganizationDaoJdbc extends PlatformAwareDaoBaseJdbc implements PriorYearOrganizationDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PriorYearOrganizationDaoJdbc.class);

    /**
     * This method purges all records in the Prior Year Organization table in the DB.
     *
     * @return Number of records that were purged.
     */
    @Override
    public int purgePriorYearOrganizations() {
        LOG.debug("purgePriorYearOrganizations() started");

        String priorYrOrgTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(PriorYearOrganization.class).getFullTableName();

        int count = getSimpleJdbcTemplate().queryForInt("SELECT COUNT(*) from " + priorYrOrgTableName);

        getSimpleJdbcTemplate().update("DELETE from " + priorYrOrgTableName);

        return count;
    }

    /**
     * This method copies all organization records from the current Org table to the Prior Year Organization table.
     *
     * @return Number of records that were copied.
     */
    public int copyCurrentOrganizationsToPriorYearTable() {
        LOG.debug("copyCurrentOrganizationsToPriorYearTable() started");

        String priorYrOrgTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(PriorYearOrganization.class).getFullTableName();
        String orgTableName = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Organization.class).getFullTableName();

        getSimpleJdbcTemplate().update("INSERT into " + priorYrOrgTableName + " (" +
            "FIN_COA_CD,ORG_CD,OBJ_ID,VER_NBR,ORG_MGR_UNVL_ID,ORG_NM,RC_CD,ORG_PHYS_CMP_CD,ORG_TYP_CD,ORG_DFLT_ACCT_NBR,ORG_CITY_NM," +
            "ORG_STATE_CD,ORG_ZIP_CD,ORG_BEGIN_DT,ORG_END_DT,RPTS_TO_FIN_COA_CD,RPTS_TO_ORG_CD,ORG_ACTIVE_CD,ORG_IN_FP_CD," +
            "ORG_PLNT_ACCT_NBR,CMP_PLNT_ACCT_NBR,ORG_PLNT_COA_CD,CMP_PLNT_COA_CD,ORG_CNTRY_CD,ORG_LN1_ADDR,ORG_LN2_ADDR,LAST_UPDT_TS" +
            ") SELECT " +
            "FIN_COA_CD,ORG_CD,OBJ_ID,VER_NBR,ORG_MGR_UNVL_ID,ORG_NM,RC_CD,ORG_PHYS_CMP_CD,ORG_TYP_CD,ORG_DFLT_ACCT_NBR,ORG_CITY_NM," +
            "ORG_STATE_CD,ORG_ZIP_CD,ORG_BEGIN_DT,ORG_END_DT,RPTS_TO_FIN_COA_CD,RPTS_TO_ORG_CD,ORG_ACTIVE_CD,ORG_IN_FP_CD," +
            "ORG_PLNT_ACCT_NBR,CMP_PLNT_ACCT_NBR,ORG_PLNT_COA_CD,CMP_PLNT_COA_CD,ORG_CNTRY_CD,ORG_LN1_ADDR,ORG_LN2_ADDR,LAST_UPDT_TS" +
            " from " + orgTableName);

        return getSimpleJdbcTemplate().queryForInt("SELECT COUNT(*) from " + priorYrOrgTableName);
    }
}
