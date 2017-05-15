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
package org.kuali.kfs.kns.web.comparator;

import org.displaytag.model.Cell;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CellComparatorHelperTest {
    @Test
    public void testExtractFromSpan() {
        String href = "<a style=\"color: red;\" href=\"inquiry.do?businessObjectClassName=org.kuali.rice.krad.bo.Options&amp;universityFiscalYear=2004&amp;methodToCall=start\" target=\"blank\"><span class=\"actualValue\">needle</span></a>";
        Cell cell = new Cell(href);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");
    }

    @Test
    public void testExtractFromAnchor() {
        String href = "<a style=\"color: red;\" href=\"inquiry.do?businessObjectClassName=org.kuali.rice.krad.bo.Options&amp;universityFiscalYear=2004&amp;methodToCall=start\" target=\"blank\">needle</a>";
        Cell cell = new Cell(href);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");
    }

    @Test
    public void testExtractFromHrefAndRemoveNbsp() {
        String href = "<span class=\"actualValue\">needle&nbsp;</span>";
        Cell cell = new Cell(href);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");
    }

    @Test
    public void testRemoveNbsp() {
        String bad = "needle&nbsp;";
        Cell cell = new Cell(bad);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");
    }

    @Test
    public void testLeaveSimpleValueAlone() {
        String good = "needle";
        Cell cell = new Cell(good);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");
    }

    @Test
    public void testMessyHref() {
        String href = "<a onClick=\"foo();\" href=\"haystack\" class=\"my favorite class\" ><span class=\"actualValue\">needle</span></a>&nbsp;";
        Cell cell = new Cell(href);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");
    }

    @Test
    public void testExtractFromBrokenAnchor() {
        String href = "<div><a>needle</span></a>";
        Cell cell = new Cell(href);

        assertEquals(CellComparatorHelper.getSanitizedStaticValue(cell), "needle");
    }
}
