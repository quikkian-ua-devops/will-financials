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

import org.apache.struts.taglib.html.HiddenTag;
import org.kuali.kfs.kns.web.ui.Field;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.io.IOException;

/**
 * The renderer of an override field
 */
public class OverrideFieldRenderer extends FieldRendererBase {
    private LabelRenderer overrideLabelRenderer = new LabelRenderer();
    private FieldRenderer overrideFieldRenderer;
    private HiddenTag overrideHiddenTag = new HiddenTag();
    private HiddenTag overrideNeededTag = new HiddenTag();
    private HiddenTag overridePresentTag = new HiddenTag();
    private boolean readOnly = false;
    private String overrideNeededProperty;
    private String overrideNeededValue;
    private AccountingLine accountingLine;
    private String storedFieldValue;

    /**
     * We never render quick finders on these
     *
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRenderer#renderQuickfinder()
     */
    public boolean renderQuickfinder() {
        return false;
    }

    /**
     * Cleans up the tags used to display this field
     *
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#clear()
     */
    @Override
    public void clear() {
        super.clear();
        overrideLabelRenderer.clear();
        overrideFieldRenderer = null;
        clearOverrideHiddenTag();
        clearOverrideNeededTag();
        overrideNeededProperty = null;
        overrideNeededValue = null;
        storedFieldValue = null;
    }

    /**
     * Cleans up the hidden that displays information for the override
     */
    protected void clearOverrideHiddenTag() {
        overrideHiddenTag.setPageContext(null);
        overrideHiddenTag.setParent(null);
        overrideHiddenTag.setProperty(null);
        overrideHiddenTag.setValue(null);
        overridePresentTag.setPageContext(null);
        overridePresentTag.setParent(null);
        overridePresentTag.setProperty(null);
        overridePresentTag.setValue(null);
    }

    /**
     * Cleans up the HiddenTag that renders override needed properties
     */
    protected void clearOverrideNeededTag() {
        overrideNeededTag.setPageContext(null);
        overrideNeededTag.setParent(null);
        overrideNeededTag.setProperty(null);
    }

    /**
     * Also sets the overrideNeededProperty name
     *
     * @see org.kuali.kfs.sys.document.web.renderers.FieldRendererBase#setField(org.kuali.rice.kns.web.ui.Field)
     * KRAD Conversion: setting fields
     */
    @Override
    public void setField(Field overrideField) {
        super.setField(overrideField);
        this.overrideNeededProperty = overrideField.getPropertyPrefix() + "." + overrideField.getPropertyName() + "Needed";
        storedFieldValue = overrideField.getPropertyValue();
        overrideField.setPropertyValue(null);
    }

    /**
     * Gets the readOnly attribute.
     *
     * @return Returns the readOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Sets the readOnly attribute value.
     *
     * @param readOnly The readOnly to set.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Gets the overrideNeededValue attribute.
     *
     * @return Returns the overrideNeededValue.
     */
    public String getOverrideNeededValue() {
        return overrideNeededValue;
    }

    /**
     * Sets the overrideNeededValue attribute value.
     *
     * @param overrideNeededValue The overrideNeededValue to set.
     */
    public void setOverrideNeededValue(String overrideNeededValue) {
        this.overrideNeededValue = overrideNeededValue;
    }

    /**
     * Gets the accountingLine attribute.
     *
     * @return Returns the accountingLine.
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    /**
     * Sets the accountingLine attribute value.
     *
     * @param accountingLine The accountingLine to set.
     */
    public void setAccountingLine(AccountingLine accountingLine) {
        this.accountingLine = accountingLine;
    }

    /**
     * Renders the override field and its associated override needed field
     *
     * @see org.kuali.kfs.sys.document.web.renderers.Renderer#render(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag)
     */
    public void render(PageContext pageContext, Tag parentTag) throws JspException {
        if ((readOnly && getField().getPropertyValue().equals("Yes")) || overrideNeededValue.equals("Yes")) {
            renderOverrideAsNonHidden(pageContext, parentTag);
            if (!readOnly) {
                renderOverridePresent(pageContext, parentTag);
            }
        } else {
        }
    }

    /**
     * @return the HTML for a line break
     */
    protected String buildLineBreak() {
        return "<br />";
    }

    /**
     * @return the HTML for a non-breaking space
     */
    protected String buildNonBreakingSpace() {
        return "&nbsp;";
    }

    /**
     * @return builds the opening of the span tag to go around the label
     */
    protected String buildLabelSpanOpening() {
        return "<span style=\"font-weight: normal\">";
    }

    /**
     * @return builds the closing of the span tag to go around the label
     */
    protected String buildLabelSpanClosing() {
        return "</span>";
    }

    /**
     * Renders the override field as non-hidden (probably a checkbox)
     *
     * @param pageContext the page context to render to
     * @param parentTag   the tag requesting all this rendering
     * @throws JspException thrown if rendering fails
     */
    protected void renderOverrideAsNonHidden(PageContext pageContext, Tag parentTag) throws JspException {
        JspWriter out = pageContext.getOut();
        try {
            openNoWrapSpan(pageContext, parentTag);

            overrideFieldRenderer = readOnly ? new ReadOnlyRenderer() : SpringContext.getBean(AccountingLineRenderingService.class).getFieldRendererForField(getField(), accountingLine);
            if (overrideFieldRenderer instanceof ReadOnlyRenderer) {
                ((ReadOnlyRenderer) overrideFieldRenderer).setShouldRenderInquiry(false);
                out.write(": "); // add a colon to make it prettier
                // populate the field again
                getField().setPropertyValue(storedFieldValue);
            }
            overrideFieldRenderer.setField(getField());
            overrideFieldRenderer.setArbitrarilyHighTabIndex(getQuickfinderTabIndex());
            overrideFieldRenderer.render(pageContext, parentTag);

            out.write(buildNonBreakingSpace());

            out.write(buildLabelSpanOpening());
            overrideLabelRenderer.setLabel(getField().getFieldLabel());
            overrideLabelRenderer.setRequired(true);
            overrideLabelRenderer.setReadOnly(false);
            overrideLabelRenderer.setLabelFor(getField().getPropertyPrefix() + "." + getField().getPropertyName());
            overrideLabelRenderer.render(pageContext, parentTag);

            out.write(buildLabelSpanClosing());
            closeNoWrapSpan(pageContext, parentTag);
        } catch (IOException ioe) {
            throw new JspException("Difficulty rendering override field", ioe);
        }
    }

    /**
     * Renders the override field as a hidden field
     *
     * @param pageContext the page context to render to
     * @param parentTag   the tag requesting all this rendering
     * @throws JspException thrown if rendering fails
     */
    protected void renderOverrideAsHidden(PageContext pageContext, Tag parentTag) throws JspException {
        overrideHiddenTag.setPageContext(pageContext);
        overrideHiddenTag.setParent(parentTag);
        overrideHiddenTag.setProperty(getField().getPropertyPrefix() + "." + getField().getPropertyName());
        if (!readOnly && overrideNeededValue.equals("No")) {
            overrideHiddenTag.setValue("No");
        } else {
            overrideHiddenTag.setValue(getField().getPropertyValue());
        }
        overrideHiddenTag.doStartTag();
        overrideHiddenTag.doEndTag();
    }

    /**
     * Renders the override field as a hidden field
     *
     * @param pageContext the page context to render to
     * @param parentTag   the tag requesting all this rendering
     * @throws JspException thrown if rendering fails
     */
    protected void renderOverridePresent(PageContext pageContext, Tag parentTag) throws JspException {
        overridePresentTag.setPageContext(pageContext);
        overridePresentTag.setParent(parentTag);
        overridePresentTag.setProperty(getField().getPropertyPrefix() + "." + getField().getPropertyName() + ".present");
        overridePresentTag.setValue("I'm here yo!");
        overridePresentTag.doStartTag();
        overridePresentTag.doEndTag();
    }

    /**
     * Renders the overrideNeeded field (which is always hidden)
     *
     * @param pageContext the page context to render to
     * @param parentTag   the tag requesting all this rendering
     * @throws JspException thrown if rendering fails
     */
    protected void renderOverrideNeededField(PageContext pageContext, Tag parentTag) throws JspException {
        overrideNeededTag.setPageContext(pageContext);
        overrideNeededTag.setParent(parentTag);
        overrideNeededTag.setProperty(overrideNeededProperty);
        overrideNeededTag.setValue(overrideNeededValue);
        overrideNeededTag.doStartTag();
        overrideNeededTag.doEndTag();
    }
}
