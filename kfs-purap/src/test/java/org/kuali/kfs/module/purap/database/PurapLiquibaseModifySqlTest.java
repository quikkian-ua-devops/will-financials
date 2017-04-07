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
package org.kuali.kfs.module.purap.database;

import org.junit.Test;
import org.kuali.kfs.sys.database.LiquibaseTestBase;

import java.util.List;

public class PurapLiquibaseModifySqlTest extends LiquibaseTestBase {
    public static final String PACKAGE_PHASE5 = "org/kuali/kfs/module/purap/db/phase5/";

    @Test
    public void testForDateColumn() throws Exception {
        testForDateColumn("/org/kuali/kfs/module/purap/db/phase1/kfs-purap_createTable.xml");
    }

    @Test
    public void testForMissingModifySql() throws Exception {
        testForMissingModifySql("/org/kuali/kfs/module/purap/db/phase1/kfs-purap_createTable.xml");
    }

    @Test
    public void testPhase5_modifySql() throws Exception {
        List<String> phase5Files = findLiquibaseFiles(PACKAGE_PHASE5);
        for (String fileName : phase5Files) {
            System.out.println(fileName);
            testForMissingModifySql("/" + fileName);
        }
    }

    @Test
    public void testPhase5_dateColumn() throws Exception {
        List<String> phase5Files = findLiquibaseFiles(PACKAGE_PHASE5);
        for (String fileName : phase5Files) {
            System.out.println(fileName);
            testForDateColumn("/" + fileName);
        }
    }

    @Test
    public void testPhase5_filename() throws Exception {
        for (String fileName : findLiquibaseFiles(PACKAGE_PHASE5)) {
            System.out.println(fileName);
            testPhase5FileName("/" + fileName);
        }
    }

    @Test
    public void testPhase5_logicalFilePath() throws Exception {
        for (String fileName : findLiquibaseFiles(PACKAGE_PHASE5)) {
            System.out.println(fileName);
            testLogicalFilePath("/" + fileName);
        }
    }
}
