/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kfs.sys.document.web.renderers;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.kns.web.taglib.html.KNSCheckboxTag;

/**
 * Renders a field as a checkbox control
 */
public class CheckboxRenderer extends FieldRendererBase {
    private KNSCheckboxTag checkboxTag = new KNSCheckboxTag();

    /**
     *
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#clear()
     */
    @Override
    public void clear() {
        super.clear();
        checkboxTag.setProperty(null);
        checkboxTag.setTitle(null);
        checkboxTag.setOnblur(null);
        checkboxTag.setStyleId(null);
        checkboxTag.setPageContext(null);
        checkboxTag.setParent(null);
        checkboxTag.setValue(null);
        checkboxTag.setTabindex(null);
    }

    /**
     *
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        renderCheckboxTag(pageContext, parentTag);
        if (isShowError()) {
            renderErrorIcon(pageContext);
        }
    }

    /**
     * Renders the checkbox portion of this checkbox tag
     * @param pageContext the page context to render to
     * @param parentTag the parent tag requesting all this rendering
     * @param propertyPrefix the property from the form to the business object
     */
    protected void renderCheckboxTag(PageContext pageContext, Tag parentTag) throws JspException {
        checkboxTag.setPageContext(pageContext);
        checkboxTag.setParent(parentTag);
        checkboxTag.setProperty(getFieldName());
        checkboxTag.setTitle(this.getAccessibleTitle());
        checkboxTag.setOnblur(this.buildOnBlur());
        checkboxTag.setStyleId(getFieldName());

        checkboxTag.setPageContext(pageContext);
        checkboxTag.setParent(parentTag);

        checkboxTag.doStartTag();
        checkboxTag.doEndTag();
    }

    /**
     * I'm not really into quick finders
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }

}
