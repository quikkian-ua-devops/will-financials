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
package org.kuali.kfs.sys.datatools.liquirelational;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LiquiRelationalTest {
    @Test
    public void testLiquiRelationalComparator() {
        LiquiRelational.LiquiRelationalFileComparator c = new LiquiRelational.LiquiRelationalFileComparator();

        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/2017-01.xml", "org/kuali/kfs/core/db/phase5/2017-01.xml") == 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/2017-01.xml", "org/kuali/kfs/core/db/phase5/2017-02.xml") < 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/2017-02.xml", "org/kuali/kfs/core/db/phase5/2017-01.xml") > 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/2017-02-05.xml", "org/kuali/kfs/core/db/phase5/2017-02-05.xml") == 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/2017-02-05.xml", "org/kuali/kfs/core/db/phase5/2017-02.xml") > 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/2017-02.xml", "org/kuali/kfs/core/db/phase5/2017-02-05.xml") < 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/next-release.xml", "org/kuali/kfs/core/db/phase5/next-release.xml") == 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/2017-02.xml", "org/kuali/kfs/core/db/phase5/next-release.xml") < 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/2017-02-05.xml", "org/kuali/kfs/core/db/phase5/next-release.xml") < 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/next-release.xml", "org/kuali/kfs/core/db/phase5/2017-02.xml") > 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/next-release.xml", "org/kuali/kfs/core/db/phase5/2017-02-05.xml") > 0);
        Assert.assertTrue(c.compare("org/kuali/kfs/core/db/phase5/next-release.xml", "org/kuali/kfs/core/db/phase5/2017-02-05.xml") > 0);

        List<String> filenames = Arrays.asList("org/kuali/kfs/core/db/phase5/next-release.xml", "org/kuali/kfs/core/db/phase5/2017-03-24.xml",
                "org/kuali/kfs/core/db/phase5/2017-02-21.xml", "org/kuali/kfs/core/db/phase5/2017-01.xml",
                "org/kuali/kfs/core/db/phase5/2017-02.xml", "org/kuali/kfs/core/db/phase5/2017-03.xml", "org/kuali/kfs/core/db/phase5/2016-01.xml");
        Collections.sort(filenames, c);

        Assert.assertEquals(0, filenames.indexOf("org/kuali/kfs/core/db/phase5/2016-01.xml"));
        Assert.assertEquals(1, filenames.indexOf("org/kuali/kfs/core/db/phase5/2017-01.xml"));
        Assert.assertEquals(2, filenames.indexOf("org/kuali/kfs/core/db/phase5/2017-02.xml"));
        Assert.assertEquals(3, filenames.indexOf("org/kuali/kfs/core/db/phase5/2017-02-21.xml"));
        Assert.assertEquals(4, filenames.indexOf("org/kuali/kfs/core/db/phase5/2017-03.xml"));
        Assert.assertEquals(5, filenames.indexOf("org/kuali/kfs/core/db/phase5/2017-03-24.xml"));
        Assert.assertEquals(6, filenames.indexOf("org/kuali/kfs/core/db/phase5/next-release.xml"));
    }
}
