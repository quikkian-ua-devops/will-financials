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
package org.kuali.kfs.module.ec.document;

import org.kuali.kfs.kns.service.DataDictionaryService;
import org.kuali.kfs.krad.bo.DocumentHeader;
import org.kuali.kfs.krad.bo.Note;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.service.NoteService;
import org.kuali.kfs.krad.service.XmlObjectSerializerService;
import org.kuali.kfs.krad.workflow.DocumentInitiator;
import org.kuali.kfs.krad.workflow.KualiDocumentXmlMaterializer;
import org.kuali.kfs.krad.workflow.KualiTransactionalDocumentInformation;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;


@ConfigureContext(session = khuntley)
public class EffortCertificationRoutingTest extends KualiTestBase {

    private Set<String> databaseNodes;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), EffortCertificationDocument.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(EffortCertificationDocument.class);

        DataSource mySource = SpringContext.getBean(DataSource.class);
        Connection dbCon = null;
        ResultSet dbAnswer = null;
        databaseNodes = new HashSet<String>();

        try {
            dbCon = mySource.getConnection();
            Statement dbAsk = dbCon.createStatement();

            String query = "select nd.nm ";
            query = query + "from KREW_RTE_NODE_T nd, KREW_DOC_TYP_T doc ";
            query = query + "where nd.rte_mthd_nm is not null ";
            query = query + "and doc.doc_typ_id = nd.doc_typ_id ";
            query = query + "and doc.doc_typ_nm = '" + documentType + "' ";
            query = query + "and doc.cur_ind = 1 ";
            query = query + "and exists (select * from KREW_RTE_NODE_LNK_T ";
            query = query + "where to_rte_node_id=nd.rte_node_id) ";
            dbAnswer = dbAsk.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbCon.close();
            } catch (SQLException sqle2) {
                sqle2.printStackTrace();
            }
        }
        while (dbAnswer.next()) {
            databaseNodes.add(dbAnswer.getString(1));
        }
    }

    private EffortCertificationDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        EffortCertificationDocument document = (EffortCertificationDocument) getDocumentParameterFixture();

        document.setEmplid("0000000060");
        document.setEffortCertificationReportNumber("A03");
        document.setUniversityFiscalYear(2009);// Data only exists for this year
        document.setEffortCertificationDocumentCode(true);// for award routing
        List<EffortCertificationDetail> effortCertificationDetailLines = new ArrayList<EffortCertificationDetail>();
        EffortCertificationDetail testDetailLine = new EffortCertificationDetail();
        testDetailLine.setAccountNumber("4831401");
        testDetailLine.setChartOfAccountsCode("BL");
        // Calculated and updated percent differ to invoke recreate routing
        testDetailLine.setEffortCertificationCalculatedOverallPercent(40);
        testDetailLine.setEffortCertificationUpdatedOverallPercent(50);
        testDetailLine.setPositionNumber("1");
        testDetailLine.setFinancialObjectCode("4000");
        testDetailLine.setSourceChartOfAccountsCode("BL");
        testDetailLine.setSourceAccountNumber("4831401");
        Integer testDate = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        testDetailLine.setUniversityFiscalYear(testDate);
        testDetailLine.setEffortCertificationOriginalPayrollAmount(new KualiDecimal(100.00));
        // testDetailLine.setEffortCertificationPayrollAmount(new KualiDecimal(100.00));
        // Adding a note because duplicate documents are not permitted otherwise
        Note testNote = new Note();
        testNote.setNoteText("This is a nice note.");
        testNote.setAuthorUniversalIdentifier(document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        SpringContext.getBean(NoteService.class).createNote(testNote, document.getDocumentHeader(), document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        effortCertificationDetailLines.add(testDetailLine);
        testDetailLine = new EffortCertificationDetail();
        testDetailLine.setAccountNumber("4631483");
        testDetailLine.setChartOfAccountsCode("BL");
        // Calculated and updated percent differ to invoke recreate routing
        testDetailLine.setEffortCertificationCalculatedOverallPercent(60);
        testDetailLine.setEffortCertificationUpdatedOverallPercent(50);
        testDetailLine.setPositionNumber("1");
        testDetailLine.setFinancialObjectCode("4000");
        testDetailLine.setSourceChartOfAccountsCode("BL");
        testDetailLine.setSourceAccountNumber("4631483");
        testDetailLine.setUniversityFiscalYear(testDate);
        testDetailLine.setEffortCertificationOriginalPayrollAmount(new KualiDecimal(100.00));
        effortCertificationDetailLines.add(testDetailLine);
        document.setEffortCertificationDetailLines(effortCertificationDetailLines);

        document.getFinancialSystemDocumentHeader().setFinancialDocumentTotalAmount(new KualiDecimal(200.00));
        SpringContext.getBean(DocumentService.class).saveDocument(document);
        return (EffortCertificationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(document.getDocumentNumber());
    }

    public String serializeDocumentToXml(Document document) {
        DocumentHeader documentHeader = document.getDocumentHeader();
        KualiTransactionalDocumentInformation transInfo = new KualiTransactionalDocumentInformation();
        DocumentInitiator initiatior = new DocumentInitiator();

        try {
            String initiatorPrincipalId = documentHeader.getWorkflowDocument().getInitiatorPrincipalId();
            Person initiatorUser = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(initiatorPrincipalId);
            initiatior.setPerson(initiatorUser);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        transInfo.setDocumentInitiator(initiatior);
        KualiDocumentXmlMaterializer xmlWrapper = new KualiDocumentXmlMaterializer();
        xmlWrapper.setDocument(document);
        xmlWrapper.setKualiTransactionalDocumentInformation(transInfo);
        String xml = SpringContext.getBean(XmlObjectSerializerService.class).toXml(xmlWrapper);
        return xml;
    }


    public final void testRouting() throws Exception {
        EffortCertificationDocument document = buildDocument();
        System.out.println("EffortCertificationDocument doc# " + document.getDocumentNumber());
        WorkflowDocument testDoc = document.getDocumentHeader().getWorkflowDocument();
        testDoc.blanketApprove("Approved by unit test");
        assertTrue("Document didn't route!", testDoc.isProcessed() || testDoc.isFinal());

//        List<ActionRequest> tempValues = SpringContext.getBean(ActionRequestService.class).findByRouteHeaderIdIgnoreCurrentInd(document.getDocumentHeader().getWorkflowDocument().getDocumentId());
//        Set<String> serviceNodes = new HashSet<String>();
//        for (ActionRequestValue tempValue : tempValues) {
//            serviceNodes.add(tempValue.getNodeInstance().getName());
//            System.out.println("serviceNodes:::: " + tempValue.getNodeInstance().getName());
//        }
//
//        boolean documentRouted = true;
//        for (String tempName : databaseNodes) {
//            System.out.println("databaseNodes::::: " + tempName);
//
//            if (serviceNodes.contains(tempName)) {
//            }
//            else {
//                documentRouted = false;
//            }
//        }

        // BIN: disable this test because the role is not setup for AccountingOrganazationHierachy in KIM
        //assertTrue("Document had routing problems", documentRouted);
        System.out.println("Document Routed");
    }
}
