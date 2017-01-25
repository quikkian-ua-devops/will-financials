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
package org.kuali.kfs.sys.document.web;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.util.GlobalVariables;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.ObjectUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tag that is responsible for rendering an accounting line group
 */
public class AccountingLineGroupTag extends TagSupport {
    private String collectionPropertyName;
    private String collectionItemPropertyName;
    private String newLinePropertyName;
    private String attributeGroupName;
    private JspFragment importLineOverride;
    private AccountingLineGroupDefinition groupDefinition;
    private AccountingLineGroup group;
    private KualiAccountingDocumentFormBase form;
    private AccountingDocument document;
    private Set<String> editModes;
    private PlatformTransactionManager transactionManager;

    /**
     * Gets the attributeGroupName attribute.
     *
     * @return Returns the attributeGroupName.
     */
    public String getAttributeGroupName() {
        return attributeGroupName;
    }

    /**
     * Sets the attributeGroupName attribute value.
     *
     * @param attributeGroupName The attributeGroupName to set.
     */
    public void setAttributeGroupName(String attributeGroup) {
        this.attributeGroupName = attributeGroup;
    }

    /**
     * Gets the collectionPropertyName attribute.
     *
     * @return Returns the collectionPropertyName.
     */
    public String getCollectionPropertyName() {
        return collectionPropertyName;
    }

    /**
     * Sets the collectionPropertyName attribute value.
     *
     * @param collectionPropertyName The collectionPropertyName to set.
     */
    public void setCollectionPropertyName(String collectionProperties) {
        this.collectionPropertyName = collectionProperties;
    }

    /**
     * Gets the newLinePropertyName attribute.
     *
     * @return Returns the newLinePropertyName.
     */
    public String getNewLinePropertyName() {
        return newLinePropertyName;
    }

    /**
     * Sets the newLinePropertyName attribute value.
     *
     * @param newLinePropertyName The newLinePropertyName to set.
     */
    public void setNewLinePropertyName(String newLineProperty) {
        this.newLinePropertyName = newLineProperty;
    }

    /**
     * Gets the importLineOverride attribute.
     *
     * @return Returns the importLineOverride.
     */
    public JspFragment getImportLineOverride() {
        return importLineOverride;
    }

    /**
     * Sets the importLineOverride attribute value.
     *
     * @param importLineOverride The importLineOverride to set.
     */
    public void setImportLineOverride(JspFragment importLineOverride) {
        this.importLineOverride = importLineOverride;
    }

    /**
     * Gets the collectionItemPropertyName attribute.
     *
     * @return Returns the collectionItemPropertyName.
     */
    public String getCollectionItemPropertyName() {
        return collectionItemPropertyName;
    }

    /**
     * Sets the collectionItemPropertyName attribute value.
     *
     * @param collectionItemPropertyName The collectionItemPropertyName to set.
     */
    public void setCollectionItemPropertyName(String collectionItemPropertyName) {
        if (StringUtils.isBlank(collectionItemPropertyName)) {
            collectionItemPropertyName = generateItemPropertyFromCollectionNameTheDumbWay(collectionPropertyName);
        }
        this.collectionItemPropertyName = collectionItemPropertyName;
    }

    /**
     * Automagically generates teh name of the collection item property from the collection property by taking any extra "s" off the end.  Note:
     * I never claimed this method was smart.  You'd be surprised at how often it'll work, though.
     *
     * @param collectionName the collection name to generate the collection item name from
     * @return the collection item property name
     */
    protected String generateItemPropertyFromCollectionNameTheDumbWay(String collectionName) {
        int subStringEnd = collectionName.length();
        if (collectionName.endsWith("s")) {
            subStringEnd -= 1;
        }
        return collectionName.substring(0, subStringEnd);
    }

    /**
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    @Override
    public int doStartTag() throws JspException {
        super.doStartTag();

        TransactionTemplate template = new TransactionTemplate(getTransactionManager());

        template.execute(transactionStatus -> {
            final List<RenderableAccountingLineContainer> containers = generateContainersForAllLines();
            group = groupDefinition.createAccountingLineGroup(getDocument(), containers, collectionPropertyName, collectionItemPropertyName, getForm().getDisplayedErrors(), getForm().getDisplayedWarnings(), getForm().getDisplayedInfo(), getForm().getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT));
            group.updateDeletabilityOfAllLines();
            if (getParent() instanceof AccountingLinesTag) {
                ((AccountingLinesTag) getParent()).addGroupToRender(group);
                resetTag();
            }
            return null;
        });

        return Tag.SKIP_BODY;
    }

    /**
     * If our parent isn't AccountingLinesTag, then we should render all the group
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
     */
    @Override
    public int doEndTag() throws JspException {
        super.doEndTag();
        if (!(getParent() instanceof AccountingLinesTag)) {
            group.renderEverything(pageContext, getParent(), null);
            resetTag();
        }
        return Tag.EVAL_PAGE;
    }

    /**
     * Clears out any state variables on the tag
     */
    protected void resetTag() {
        collectionPropertyName = null;
        collectionItemPropertyName = null;
        newLinePropertyName = null;
        attributeGroupName = null;
        importLineOverride = null;
        groupDefinition = null;
        group = null;
        form = null;
        document = null;
        editModes = null;
    }

    /**
     * Gets the correct accounting line group definition for this
     *
     * @return the accounting line group definition to be used by this tag
     */
    protected AccountingLineGroupDefinition getGroupDefinition() {
        if (groupDefinition == null) {
            final String documentTypeClassName = getDocument().getClass().getName();
            final FinancialSystemTransactionalDocumentEntry documentEntry = (FinancialSystemTransactionalDocumentEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry(documentTypeClassName);
            final Map<String, AccountingLineGroupDefinition> groupDefinitions = documentEntry.getAccountingLineGroups();
            groupDefinition = groupDefinitions.get(attributeGroupName);
        }
        return groupDefinition;
    }

    /**
     * Creates a List of renderable elements, with all rendering transformations accomplished, to be rendered
     *
     * @param groupDefinition the data dictionary definition of the accounting line group this line is in
     * @param accountingLine  the accounting line itself
     * @param newLine         true if the line is new and not added yet to the document, false otherwise
     * @return a List of AccountingLineTableRows to render
     */
    protected List<AccountingLineTableRow> getRenderableElementsForLine(AccountingLineGroupDefinition groupDefinition, AccountingLine accountingLine, boolean newLine, boolean topLine, String accountingLinePropertyName) {
        List<TableJoining> layoutElements = groupDefinition.getAccountingLineView().getAccountingLineLayoutElements(accountingLine.getClass());
        AccountingLineRenderingService renderingService = SpringContext.getBean(AccountingLineRenderingService.class);
        renderingService.performPreTablificationTransformations(layoutElements, groupDefinition, getDocument(), accountingLine, newLine, getForm().getUnconvertedValues(), accountingLinePropertyName);
        List<AccountingLineTableRow> renderableElements = renderingService.tablify(layoutElements);

        removeTopRowIfNecessary(groupDefinition, topLine, renderableElements);

        renderingService.performPostTablificationTransformations(renderableElements, groupDefinition, getDocument(), accountingLine, newLine);
        return renderableElements;
    }

    /**
     * If it is determined that removing the first row (presumably a header row) is necessary, removes it
     *
     * @param rows the rows to possibly remove the top row from
     */
    protected void removeTopRowIfNecessary(AccountingLineGroupDefinition definition, boolean topLine, List<AccountingLineTableRow> rows) {
        if (definition.isTopHeadersAfterFirstLineHiding() && !topLine) {
            safelyRemoveTopRow(rows);
        }
    }

    /**
     * Looks through the top row.  If there's any fields in the top row, it quietly fails; it also won't remove the top row if that's the only row there is
     *
     * @param rows the List of rows to remove the top one of if possible
     */
    protected void safelyRemoveTopRow(List<AccountingLineTableRow> rows) {
        if (rows != null && rows.size() > 1 && rows.get(0).safeToRemove()) {
            rows.remove(0);
        }
    }

    /**
     * @return the new accounting line from the form
     */
    protected AccountingLine getNewAccountingLine() {
        return (AccountingLine) ObjectUtils.getPropertyValue(getForm(), newLinePropertyName);
    }

    /**
     * @return the collection of accounting lines that this tag is supposed to render
     */
    protected List<AccountingLine> getAccountingLineCollection() {
        return (List<AccountingLine>) ObjectUtils.getPropertyValue(getForm(), collectionPropertyName);
    }

    /**
     * @return a List of accounting line table rows to be rendered for all the accounting lines available for rendering within the group
     */
    protected List<RenderableAccountingLineContainer> generateContainersForAllLines() {
        List<RenderableAccountingLineContainer> containers = new ArrayList<RenderableAccountingLineContainer>();

        final AccountingLineGroupDefinition groupDefinition = getGroupDefinition();
        final AccountingDocument document = getDocument();
        final Set<String> currentNodes = document.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        boolean addedTopLine = false;

        // add all existing lines
        int count = 0;
        boolean anyEditableLines = false;
        final boolean pageIsEditable = getForm().getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT);
        List<AccountingLine> lines = getAccountingLineCollection();
        Collections.sort(lines, getGroupDefinition().getAccountingLineComparator());
        // add the new line
        if (StringUtils.isNotBlank(newLinePropertyName) && ((getForm().getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT) && groupDefinition.getAccountingLineAuthorizer().renderNewLine(document, collectionPropertyName)) || anyEditableLines)) {
            containers.add(0, buildContainerForLine(groupDefinition, document, getNewAccountingLine(), currentUser, null, true, pageIsEditable, currentNodes));
            addedTopLine = true;
        }
        for (AccountingLine accountingLine : lines) {
            final RenderableAccountingLineContainer container = buildContainerForLine(groupDefinition, document, accountingLine, currentUser, new Integer(count), !addedTopLine, pageIsEditable, currentNodes);
            containers.add(container);
            anyEditableLines = anyEditableLines || container.isEditableLine() || isMessageMapContainingErrorsOnLine(container.getAccountingLinePropertyPath());
            count += 1;
            addedTopLine = true;
        }


        return containers;
    }

    /**
     * Determines if the error map contains any errors which exist on the currently rendered accounting line
     *
     * @param accountingLinePropertyName the property name of the accounting line
     * @return true if there are errors on the line, false otherwise
     */
    protected boolean isMessageMapContainingErrorsOnLine(String accountingLinePropertyName) {
        for (Object errorKeyAsObject : GlobalVariables.getMessageMap().getAllPropertiesWithErrors()) {
            if (((String) errorKeyAsObject).startsWith(accountingLinePropertyName)) return true;
        }
        return false;
    }

    /**
     * Builds an accounting line container for a given accounting line to render
     *
     * @param groupDefinition    the data dictionary definition of the group this line is a part of
     * @param accountingDocument the accounting document the line is or will someday live on
     * @param accountingLine     the accounting line to render
     * @param currentUser        the currently logged in user
     * @param count              the count of this line within the collection represented by the group; null if this is a new line for the group
     * @param currentNodes       the workflow nodes the document is currently at
     * @return the container created
     */
    protected RenderableAccountingLineContainer buildContainerForLine(AccountingLineGroupDefinition groupDefinition, AccountingDocument accountingDocument, AccountingLine accountingLine, Person currentUser, Integer count, boolean topLine, boolean pageIsEditable, Set<String> currentNodes) {
        final String accountingLinePropertyName = count == null ? newLinePropertyName : collectionItemPropertyName + "[" + count.toString() + "]";
        final boolean newLine = (count == null);
        final List<AccountingLineTableRow> rows = getRenderableElementsForLine(groupDefinition, accountingLine, newLine, topLine, accountingLinePropertyName);

        return new RenderableAccountingLineContainer(getForm(), accountingLine, accountingLinePropertyName, rows, count, groupDefinition.getGroupLabel(), getErrors(), groupDefinition.getAccountingLineAuthorizer(), groupDefinition.getAccountingLineAuthorizer().hasEditPermissionOnAccountingLine(getDocument(), accountingLine, collectionPropertyName, currentUser, pageIsEditable, currentNodes), currentNodes);
    }

    /**
     * Clean up state held by this tag
     *
     * @see javax.servlet.jsp.tagext.TagSupport#release()
     */
    @Override
    public void release() {
        super.release();
        resetTag();
    }

    /**
     * @return the form that this document is currently using
     */
    protected KualiAccountingDocumentFormBase getForm() {
        if (form == null) {
            if (getParent() instanceof AccountingLinesTag) {
                form = ((AccountingLinesTag) getParent()).getForm();
            } else {
                form = SpringContext.getBean(AccountingLineRenderingService.class).findForm(pageContext);
            }
        }
        return form;
    }

    /**
     * @return the ErrorPropertyList from the request
     */
    protected List getErrors() {
        return (List) pageContext.getRequest().getAttribute("ErrorPropertyList");
    }

    /**
     * @return the document this tag is helping to display
     */
    protected AccountingDocument getDocument() {
        if (document == null) {
            document = getForm().getFinancialDocument();
        }
        return document;
    }

    public PlatformTransactionManager getTransactionManager() {
        if (transactionManager == null) {
            transactionManager = KNSServiceLocator.getTransactionManager();
        }
        return this.transactionManager;
    }

}
