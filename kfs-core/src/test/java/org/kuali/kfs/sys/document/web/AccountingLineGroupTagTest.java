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
package org.kuali.kfs.sys.document.web;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineComparator;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccountingLineGroupTagTest {
    private TestAccountingLineGroupTag accountingLineGroupTag;
    private KualiAccountingDocumentFormBase kualiAccountingDocumentFormBase;
    private AccountingDocument accountingDocument;
    private AccountingLineGroupDefinition accountingLineGroupDefinition;
    private DocumentHeader documentHeader;
    private WorkflowDocument workflowDocument;
    private Set<String> currentNodeNames;
    private Person user;
    private Map<String,Object> documentActions;
    private SourceAccountingLine sourceAccountingLine;
    private List<AccountingLine> lines;

    @Before
    public void setUp() {
        kualiAccountingDocumentFormBase = EasyMock.createMock(KualiAccountingDocumentFormBase.class);
        accountingDocument = EasyMock.createMock(AccountingDocument.class);
        accountingLineGroupDefinition = EasyMock.createMock(AccountingLineGroupDefinition.class);
        documentHeader = EasyMock.createMock(DocumentHeader.class);
        workflowDocument = EasyMock.createMock(WorkflowDocument.class);
        user = EasyMock.createMock(Person.class);
        sourceAccountingLine = new SourceAccountingLine();

        currentNodeNames = new HashSet<>();
        currentNodeNames.add("accounting");

        lines = new ArrayList<>();

        accountingLineGroupTag = new TestAccountingLineGroupTag();
        accountingLineGroupTag.setNewLinePropertyName("newSourceLine");
        accountingLineGroupTag.setCollectionPropertyName("document.sourceAccountingLines");
        accountingLineGroupTag.setAttributeGroupName("source");

        EasyMock.expect(accountingDocument.getDocumentHeader()).andReturn(documentHeader);
        EasyMock.expect(documentHeader.getWorkflowDocument()).andReturn(workflowDocument);
        EasyMock.expect(workflowDocument.getCurrentNodeNames()).andReturn(currentNodeNames);
        documentActions = new HashMap<>();
        documentActions.put(KRADConstants.KUALI_ACTION_CAN_EDIT,true);
        EasyMock.expect(kualiAccountingDocumentFormBase.getDocumentActions()).andReturn(documentActions);
        EasyMock.expect(accountingLineGroupDefinition.getAccountingLineComparator()).andReturn(new AccountingLineComparator());
    }

    @Test
    public void testGenerateContainersForAllLinesSourceCanEditNoLines() {
        EasyMock.expect(kualiAccountingDocumentFormBase.getNewSourceLine()).andReturn(sourceAccountingLine);
        EasyMock.replay(kualiAccountingDocumentFormBase, accountingLineGroupDefinition, accountingDocument, documentHeader, workflowDocument);

        List<RenderableAccountingLineContainer> lines = accountingLineGroupTag.callGenerateContainersForAllLines();

        Assert.assertEquals(1,lines.size());
        EasyMock.verify(kualiAccountingDocumentFormBase, accountingLineGroupDefinition, accountingDocument, documentHeader, workflowDocument);
    }

    @Test
    public void testGenerateContainersForAllLinesSourceCanEditOneLine() {
        SourceAccountingLine line1 = new SourceAccountingLine();
        line1.setChartOfAccountsCode("BL");
        lines.add(line1);

        EasyMock.expect(kualiAccountingDocumentFormBase.getNewSourceLine()).andReturn(sourceAccountingLine);
        EasyMock.replay(kualiAccountingDocumentFormBase, accountingLineGroupDefinition, accountingDocument, documentHeader, workflowDocument);

        List<RenderableAccountingLineContainer> lines = accountingLineGroupTag.callGenerateContainersForAllLines();

        Assert.assertEquals(2,lines.size());
        Assert.assertEquals("BL",lines.get(1).getAccountingLine().getChartOfAccountsCode());
        EasyMock.verify(kualiAccountingDocumentFormBase, accountingLineGroupDefinition, accountingDocument, documentHeader, workflowDocument);
    }

    @Test
    public void testGenerateContainersForAllLinesSourceCanNotEditNoLines() {
        accountingLineGroupTag.setNewLinePropertyName(null);

        EasyMock.replay(kualiAccountingDocumentFormBase, accountingLineGroupDefinition, accountingDocument, documentHeader, workflowDocument);

        List<RenderableAccountingLineContainer> lines = accountingLineGroupTag.callGenerateContainersForAllLines();

        Assert.assertEquals(0,lines.size());
        EasyMock.verify(kualiAccountingDocumentFormBase, accountingLineGroupDefinition, accountingDocument, documentHeader, workflowDocument);
    }

    @Test
    public void testGenerateContainersForAllLinesSourceCanNotEditOneLine() {
        accountingLineGroupTag.setNewLinePropertyName(null);
        SourceAccountingLine line1 = new SourceAccountingLine();
        line1.setChartOfAccountsCode("BL");
        lines.add(line1);

        EasyMock.replay(kualiAccountingDocumentFormBase, accountingLineGroupDefinition, accountingDocument, documentHeader, workflowDocument);

        List<RenderableAccountingLineContainer> lines = accountingLineGroupTag.callGenerateContainersForAllLines();

        Assert.assertEquals(1,lines.size());
        Assert.assertEquals("BL",lines.get(0).getAccountingLine().getChartOfAccountsCode());
        EasyMock.verify(kualiAccountingDocumentFormBase, accountingLineGroupDefinition, accountingDocument, documentHeader, workflowDocument);
    }

    class TestAccountingLineGroupTag extends AccountingLineGroupTag {
        // Override methods that call into spring
        @Override
        protected KualiAccountingDocumentFormBase getForm() {
            return kualiAccountingDocumentFormBase;
        }

        @Override
        protected AccountingDocument getDocument() {
            return accountingDocument;
        }

        @Override
        protected AccountingLineGroupDefinition getGroupDefinition() {
            return accountingLineGroupDefinition;
        }

        @Override
        protected List<AccountingLine> getAccountingLineCollection() {
            return lines;
        }

        @Override
        protected Person getCurrentUser() {
            return user;
        }

        @Override
        protected RenderableAccountingLineContainer buildContainerForLine(AccountingLineGroupDefinition groupDefinition, AccountingDocument accountingDocument, AccountingLine accountingLine, Person currentUser, Integer count, boolean topLine, boolean pageIsEditable, Set<String> currentNodes) {
            if (count == null) {
                return new RenderableAccountingLineContainer(getForm(),accountingLine,"add",null,count,"Add",null,null,pageIsEditable,currentNodes);
            } else {
                return new RenderableAccountingLineContainer(getForm(),accountingLine,"line",null,count,"Line",null,null,pageIsEditable,currentNodes);
            }
        }

        // Expose the protected method so it can be tested
        public List<RenderableAccountingLineContainer> callGenerateContainersForAllLines() {
            return generateContainersForAllLines();
        }
    }
}