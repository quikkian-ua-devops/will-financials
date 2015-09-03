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
package org.kuali.kfs.module.ar.businessobject.lookup;

import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.GenerateDunningLettersLookupResult;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.DunningLetterService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.DunningCampaignFixture;
import org.kuali.kfs.module.ar.fixture.DunningLetterDistributionFixture;
import org.kuali.kfs.module.ar.fixture.DunningLetterTemplateFixture;
import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.web.struts.GenerateDunningLettersLookupForm;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DocumentService;
import org.kuali.kfs.krad.util.ErrorMessage;
import org.kuali.kfs.krad.util.GlobalVariables;

/**
 * This class tests the Generate Dunning Letters lookup.
 */
@ConfigureContext(session = wklykins)
public class DunningLetterDistributionLookupableHelperServiceImplTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterDistributionLookupableHelperServiceImplTest.class);

    private GenerateDunningLettersLookupableHelperServiceImpl generateDunningLettersLookupableHelperServiceImpl;
    private GenerateDunningLettersLookupForm generateDunningLettersLookupForm;
    private Map fieldValues;

    private static final String invoiceDocumentNumber = null;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        generateDunningLettersLookupableHelperServiceImpl = new GenerateDunningLettersLookupableHelperServiceImpl();
        generateDunningLettersLookupableHelperServiceImpl.setContractsGrantsInvoiceDocumentService(SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class));
        generateDunningLettersLookupableHelperServiceImpl.setContractsGrantsReportHelperService(SpringContext.getBean(ContractsGrantsReportHelperService.class));
        generateDunningLettersLookupableHelperServiceImpl.setBusinessObjectClass(GenerateDunningLettersLookupResult.class);
        generateDunningLettersLookupableHelperServiceImpl.setBusinessObjectService(SpringContext.getBean(BusinessObjectService.class));
        generateDunningLettersLookupableHelperServiceImpl.setAccountService(SpringContext.getBean(AccountService.class));
        generateDunningLettersLookupableHelperServiceImpl.setParameterService(SpringContext.getBean(ParameterService.class));
        generateDunningLettersLookupableHelperServiceImpl.setDunningLetterService(SpringContext.getBean(DunningLetterService.class));
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        generateDunningLettersLookupForm = new GenerateDunningLettersLookupForm();
        // To create a basic invoice with test data

        String coaCode = "BL";
        String orgCode = "SRS";
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);
        // To add data for OrganizationOptions as fixture.


        OrganizationOptions organizationOptions = new OrganizationOptions();

        organizationOptions.setChartOfAccountsCode(coaCode);
        organizationOptions.setOrganizationCode(orgCode);
        organizationOptions.setProcessingChartOfAccountCode(coaCode);
        organizationOptions.setProcessingOrganizationCode(orgCode);
        SpringContext.getBean(BusinessObjectService.class).save(organizationOptions);

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode, errorMessages, null, null);
        cgInvoice.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);

        // To create Dunning Campaign and Dunning LEtter Distribtuions and templates.

        DunningCampaign dunningCampaign = DunningCampaignFixture.AR_DUNC1.createDunningCampaign();
        DunningLetterDistribution dunningLetterDistribution = DunningLetterDistributionFixture.AR_DLD1.createDunningLetterDistribution();
        dunningLetterDistribution.setActiveIndicator(true);
        dunningLetterDistribution.setSendDunningLetterIndicator(true);
        DunningLetterTemplate dunningLetterTemplate = DunningLetterTemplateFixture.CG_DLTS1.createDunningLetterTemplate();
        SpringContext.getBean(BusinessObjectService.class).save(dunningLetterTemplate);
        dunningLetterDistribution.setDunningLetterTemplate(dunningLetterTemplate.getDunningLetterTemplateCode());
        dunningCampaign.getDunningLetterDistributions().add(dunningLetterDistribution);
        SpringContext.getBean(BusinessObjectService.class).save(dunningCampaign);

        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setDunningCampaignFromFixture((Award) award);
        cgInvoice.getInvoiceGeneralDetail().setAward(award);
        cgInvoice.setAge(10);
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        Date today = new Date(ts.getTime());

        cgInvoice.setBillingDate(today);
        for (InvoiceAddressDetail invoiceAddressDetail : cgInvoice.getInvoiceAddressDetails()) {
            invoiceAddressDetail.setCustomerInvoiceTemplateCode("STD");
            invoiceAddressDetail.setInvoiceTransmissionMethodCode("MAIL");
        }
        documentService.saveDocument(cgInvoice);
        fieldValues = new LinkedHashMap();
        fieldValues.put("invoiceDocumentNumber", cgInvoice.getDocumentNumber());

        Map<String, String> qualification = new HashMap<String, String>(3);
        qualification.put(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, cgInvoice.getBillByChartOfAccountCode());
        qualification.put(ArKimAttributes.BILLING_ORGANIZATION_CODE, cgInvoice.getBilledByOrganizationCode());

        org.kuali.rice.kim.api.role.RoleService roleService = KimApiServiceLocator.getRoleService();
        Person user = GlobalVariables.getUserSession().getPerson();
        roleService.assignPrincipalToRole(user.getPrincipalId(), ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualification);

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        generateDunningLettersLookupableHelperServiceImpl = null;
        generateDunningLettersLookupForm = null;
        fieldValues = null;
    }

    /**
     * This method tests the performLookup method of GenerateDunningLettersLookupableHelperServiceImpl.
     */
    public void testPerformLookup() {
        Collection resultTable = new ArrayList<String>();
        generateDunningLettersLookupForm.setFieldsForLookup(fieldValues);

        assertTrue(generateDunningLettersLookupableHelperServiceImpl.performLookup(generateDunningLettersLookupForm, resultTable, true).size() > 0);
    }
}
