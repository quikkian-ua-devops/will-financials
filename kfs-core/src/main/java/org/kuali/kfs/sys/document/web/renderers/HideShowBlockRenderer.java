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
package org.kuali.kfs.sys.document.web.renderers;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.html.HiddenTag;
import org.kuali.kfs.kns.web.taglib.html.KFSButtonTag;
import org.kuali.kfs.sys.document.web.HideShowBlock;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.io.IOException;

/**
 * Renders a hide show block
 */
public class HideShowBlockRenderer implements Renderer {
    private HideShowBlock hideShowBlock;
    private HiddenTag tabStateTag = new HiddenTag();
    private KFSButtonTag showHideButton = new KFSButtonTag();

    /**
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#clear()
     */
    public void clear() {
        hideShowBlock = null;
        cleanTabStateTag();
        cleanShowHideButton();
    }

    /**
     * Cleans the tab state hidden tag
     */
    protected void cleanTabStateTag() {
        tabStateTag.setPageContext(null);
        tabStateTag.setParent(null);
        tabStateTag.setProperty(null);
        tabStateTag.setValue(null);
    }

    /**
     * Cleans the show/hide button up
     */
    protected void cleanShowHideButton() {
        showHideButton.setPageContext(null);
        showHideButton.setParent(null);
        showHideButton.setAlt(null);
        showHideButton.setTitle(null);
        showHideButton.setValue(null);
        showHideButton.setInnerHTML(null);
        showHideButton.setProperty(null);
        showHideButton.setStyleClass(null);
        showHideButton.setStyleId(null);
        showHideButton.setOnclick(null);
    }

    /**
     * Renders the title row and forces the rendering of child content
     *
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();

        try {
            out.write(buildLabelButtonTableOpening());
            renderTabStateTag(pageContext, parentTag);
            if (!StringUtils.isBlank(hideShowBlock.getLabel())) {
                out.write(hideShowBlock.getLabel());
            }
            renderShowHideButton(pageContext, parentTag);
            out.write(buildLabelButtonTableClosing());
            out.write(buildInnerTableOpening());
            hideShowBlock.renderChildRows(pageContext, parentTag);
            out.write(buildInnerTableClosing());
        } catch (IOException ioe) {
            throw new JspException("Difficulty rendering Hide/Show block", ioe);
        }
    }

    /**
     * @return the HTML for the opening of the button table
     */
    protected String buildLabelButtonTableOpening() {
        return "<table class=\"datatable\" style=\"padding: 0px;\"><tr><td class=\"tab-subhead\">";
    }

    /**
     * Renders a hidden tag which holds the current tab state
     *
     * @param pageContext the pageContext to render to
     * @param parentTag   the tag requesting all this rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderTabStateTag(PageContext pageContext, Tag parentTag) throws JspException {
        tabStateTag.setPageContext(pageContext);
        tabStateTag.setParent(parentTag);
        tabStateTag.setProperty("tabStates(" + hideShowBlock.getTabKey() + ")");
        tabStateTag.setValue(hideShowBlock.getTabState());

        tabStateTag.doStartTag();
        tabStateTag.doEndTag();
    }

    /**
     * @return the HTML for the closing of the label button table
     */
    protected String buildLabelButtonTableClosing() {
        return "</td></tr></table>";
    }

    /**
     * Renders the hide/show image button
     *
     * @param pageContext the pageContext to render to
     * @param parentTag   the tag requesting all this rendering
     * @throws JspException thrown if something goes wrong
     */
    protected void renderShowHideButton(PageContext pageContext, Tag parentTag) throws JspException {
        showHideButton.setPageContext(pageContext);
        showHideButton.setParent(parentTag);
        showHideButton.setProperty("methodToCall.toggleTab.tab" + hideShowBlock.getTabKey());
        showHideButton.setStyleClass("btn btn-default small");
        showHideButton.setStyleId("tab-" + hideShowBlock.getTabKey() + "-imageToggle");
        showHideButton.setOnclick("javascript: return toggleTab(document, '" + hideShowBlock.getTabKey() + "');");

        if (hideShowBlock.isShowing()) {
            showHideButton.setAlt("Hide " + hideShowBlock.getFullLabel());
            showHideButton.setTitle("Hide " + hideShowBlock.getFullLabel());
            showHideButton.setValue("Hide");
            showHideButton.setInnerHTML("Hide");
        } else {
            showHideButton.setAlt("Show " + hideShowBlock.getFullLabel());
            showHideButton.setTitle("Show " + hideShowBlock.getFullLabel());
            showHideButton.setValue("Show");
            showHideButton.setInnerHTML("Show");
        }

        showHideButton.doStartTag();
        showHideButton.doEndTag();
    }

    /**
     * Creates the HTML for the hiding/showing div and inner table to display children in
     *
     * @return the HTML for the opening of the inner table
     */
    protected String buildInnerTableOpening() {
        StringBuilder opening = new StringBuilder();
        opening.append("<div id=\"tab-" + hideShowBlock.getTabKey() + "-div\" style=\"display: ");
        opening.append(hideShowBlock.isShowing() ? "block" : "none");
        opening.append("\">");

        opening.append("<table class=\"standard\" style=\"width: 100%;\">");

        return opening.toString();
    }

    /**
     * Creates the HTML to close the inner table and hide/show div
     *
     * @return the HTML for the closing of the inner table
     */
    protected String buildInnerTableClosing() {
        return "</table></div>";
    }


    /**
     * Gets the hideShowBlock attribute.
     *
     * @return Returns the hideShowBlock.
     */
    public HideShowBlock getHideShowBlock() {
        return hideShowBlock;
    }

    /**
     * Sets the hideShowBlock attribute value.
     *
     * @param hideShowBlock The hideShowBlock to set.
     */
    public void setHideShowBlock(HideShowBlock hideShowBlock) {
        this.hideShowBlock = hideShowBlock;
    }

}
