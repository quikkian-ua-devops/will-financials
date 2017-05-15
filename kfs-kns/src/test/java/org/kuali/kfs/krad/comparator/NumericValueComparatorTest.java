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
package org.kuali.kfs.krad.comparator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NumericValueComparatorTest {
    private NumericValueComparator numericValueComparator;

    @Before
    public void setUp() {
        numericValueComparator = NumericValueComparator.getInstance();
    }

    @Test
    public void testNulls() {
        Assert.assertTrue(numericValueComparator.compare(null,null) == 0);
        Assert.assertTrue(numericValueComparator.compare("0",null) > 0);
        Assert.assertTrue(numericValueComparator.compare(null,"0") < 0);
    }

    @Test
    public void testOddCharacters() {
        Assert.assertTrue(numericValueComparator.compare("1000","1,000") == 0);
        Assert.assertTrue(numericValueComparator.compare("1000","1000&nbsp;") == 0);
        Assert.assertTrue(numericValueComparator.compare("1000","$1000") == 0);
        Assert.assertTrue(numericValueComparator.compare("1,000","1000") == 0);
        Assert.assertTrue(numericValueComparator.compare("1000&nbsp;","1000") == 0);
        Assert.assertTrue(numericValueComparator.compare("$1000","1000") == 0);
    }

    @Test
    public void testNegatives() {
        Assert.assertTrue(numericValueComparator.compare("-1000","(1000)") == 0);
        Assert.assertTrue(numericValueComparator.compare("(1000)","-1000") == 0);
    }

    @Test
    public void testValids() {
        Assert.assertTrue(numericValueComparator.compare("100","200") < 0);
        Assert.assertTrue(numericValueComparator.compare("200","200") == 0);
        Assert.assertTrue(numericValueComparator.compare("200","100") > 0);
    }

    @Test
    public void testInvalids() {
        Assert.assertTrue(numericValueComparator.compare("abc","def") < 0);
        Assert.assertTrue(numericValueComparator.compare("def","def") == 0);
        Assert.assertTrue(numericValueComparator.compare("def","abc") > 0);
    }
}