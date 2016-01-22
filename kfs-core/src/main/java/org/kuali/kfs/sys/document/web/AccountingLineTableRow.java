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
package org.kuali.kfs.sys.document.web;

import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.sys.document.web.renderers.TableRowRenderer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a table row to display in an accounting view table.
 */
public class AccountingLineTableRow implements RenderableElement {
    private List<AccountingLineTableCell> cells;
    private AccountingLineRenderingContext renderingContext;
    private boolean isHeader;

    /**
     * Constructs a AccountingLineTableRow
     */
    public AccountingLineTableRow() {
        cells = new ArrayList<>();
    }

    /**
     * Gets the cells attribute. 
     * @return Returns the cells.
     */
    public List<AccountingLineTableCell> getCells() {
        return cells;
    }

    /**
     * Sets the cells attribute value.
     * @param cells The cells to set.
     */
    public void setCells(List<AccountingLineTableCell> cells) {
        this.cells = cells;
    }
    
    /**
     * Adds a new table cell to the row
     * @param cell the cell to add to the row
     */
    public void addCell(AccountingLineTableCell cell) {
        cells.add(cell);
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        for (AccountingLineTableCell cell : cells) {
            if (!cell.isHidden()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This is not an action block
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        for (AccountingLineTableCell cell : cells) {
            if (!cell.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        TableRowRenderer renderer = new TableRowRenderer();
        this.renderingContext = renderingContext;

        List<AccountingLineTableRow> rows = renderingContext.getRows();
        if (rows.size() > 2) {
            renderer.setIsMultiline(true);
        }

        if (rows.get(0).equals(this)) {
            renderer.setIsFirstLine(true);
        } else if (rows.get(rows.size() - 1).equals(this)) {
            renderer.setIsLastLine(true);
        }

        renderer.setRow(this);
        renderer.render(pageContext, parentTag);
        renderer.clear();
        this.renderingContext = null;
    }
    
    /**
     * Requests that the row renders all of its children cells
     * @param pageContext the page contex to render to
     * @param parentTag the tag requesting all this rendering
     * @throws JspException exception thrown when...something...goes, I don't know...wrong or somethin'
     */
    public void renderChildrenCells(PageContext pageContext, Tag parentTag) throws JspException {
        for (AccountingLineTableCell cell : cells) {
            cell.renderElement(pageContext, parentTag, renderingContext);
        }
    }
    
    /**
     * Returns the number of children cells this row has
     * @return the number of children cells this row has
     */
    public int getChildCellCount() {
        return cells.size();
    }
    
    /**
     * @return returns the number of cells which will actually be rendered (ie, colspans are taken into account)
     */
    public int getChildRenderableCount() {
        int count = 0;
        for (AccountingLineTableCell cell : cells) {
            count += cell.getColSpan();
        }
        return count;
    }
    
    /**
     * Dutifully appends the names of any fields it knows about to the given List of field names
     * @param fields a List of field names to append other names to
     * 
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    public void appendFields(List<Field> fields) {
        for (AccountingLineTableCell cell : cells) {
            cell.appendFields(fields);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {
        for (AccountingLineTableCell cell : cells) {
            cell.populateWithTabIndexIfRequested(reallyHighIndex);
        }
    }
    
    /**
     * Determines whether each cell is safe to remove; if so, simply removes that cell
     * @return true if the row can be safely removed; false otherwise
     */
    public boolean safeToRemove() {
        for (AccountingLineTableCell cell : cells) {
            if (!cell.safeToRemove()) return false;
        }
        return true;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setIsHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public boolean isNew() {
        return renderingContext.isNewLine();
    }
}
