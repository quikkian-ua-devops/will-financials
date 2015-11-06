/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.cg.database;

import org.junit.Test;
import org.kuali.kfs.sys.database.LiquibaseTestBase;

public class CgLiquibaseModifySqlTest extends LiquibaseTestBase {
    @Test
    public void testForDateColumn() throws Exception {
        testForDateColumn("/org/kuali/kfs/module/cg/db/phase1/kfs-cg_createTable.xml");
    }

    @Test
    public void testCg() throws Exception {
        testForMissingModifySql("/org/kuali/kfs/module/cg/db/phase1/kfs-cg_createTable.xml");
    }
}
