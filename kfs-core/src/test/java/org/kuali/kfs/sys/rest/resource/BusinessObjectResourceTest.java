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
package org.kuali.kfs.sys.rest.resource;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.businessobject.DepositCashReceiptControl;
import org.kuali.kfs.kns.lookup.LookupUtils;
import org.kuali.kfs.kns.service.BusinessObjectAuthorizationService;
import org.kuali.kfs.krad.UserSession;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.bo.PersistableBusinessObject;
import org.kuali.kfs.krad.datadictionary.AttributeDefinition;
import org.kuali.kfs.krad.datadictionary.AttributeSecurity;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.kfs.krad.datadictionary.control.ControlDefinition;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatterLiteral;
import org.kuali.kfs.krad.datadictionary.mask.MaskFormatterSubString;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.keyvalues.KeyValuesFinder;
import org.kuali.kfs.krad.service.BusinessObjectService;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.krad.uif.UifConstants;
import org.kuali.kfs.krad.uif.view.View;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.krad.util.KRADPropertyConstants;
import org.kuali.kfs.krad.util.KRADUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sec.service.impl.AccessSecurityServiceImpl;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.UnitOfMeasure;
import org.kuali.kfs.sys.identity.TestPerson;
import org.kuali.kfs.sys.rest.BusinessObjectApiResourceTestHelper;
import org.kuali.kfs.sys.rest.ErrorMessage;
import org.kuali.kfs.sys.rest.exception.ApiRequestException;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


@RunWith(PowerMockRunner.class)
public class BusinessObjectResourceTest {

    private BusinessObjectResource apiResource;
    private KualiModuleService kualiModuleService;
    private ModuleService moduleService;
    private BusinessObjectService businessObjectService;
    private BusinessObjectAuthorizationService businessObjectAuthorizationService;
    private PersistenceStructureService persistenceStructureService;
    private ConfigurationService configurationService;
    private DataDictionaryService dataDictionaryService;
    private DataDictionary dataDictionary;
    private PermissionService permissionService;
    private AccessSecurityService accessSecurityService;
    private UserSession userSession;
    private Date now = new Date(System.currentTimeMillis());
    private Deposit deposit = BusinessObjectApiResourceTestHelper.getDeposit(now);
    private UnitOfMeasure uom = BusinessObjectApiResourceTestHelper.getUom();
    private Bank bank = BusinessObjectApiResourceTestHelper.getBank();
    private Person testPerson = new TestPerson("testPrincipalId", "testPrincipalName");

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        apiResource = new BusinessObjectResource();
        kualiModuleService = EasyMock.createMock(KualiModuleService.class);
        moduleService = EasyMock.createMock(ModuleService.class);
        businessObjectService = EasyMock.createMock(BusinessObjectService.class);
        persistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
        configurationService = EasyMock.createMock(ConfigurationService.class);
        dataDictionaryService = EasyMock.partialMockBuilder(MockDataDictionaryService.class).addMockedMethods("containsDictionaryObject", "getDictionaryObject", "getDataDictionary").createMock();
        dataDictionary = EasyMock.createMock(DataDictionary.class);
        permissionService = EasyMock.createMock(PermissionService.class);
        accessSecurityService = EasyMock.createMock(AccessSecurityService.class);
        userSession = EasyMock.createMock(UserSession.class);
        businessObjectAuthorizationService = EasyMock.createMock(BusinessObjectAuthorizationService.class);
        PowerMock.mockStatic(KRADServiceLocator.class);
        PowerMock.mockStaticPartial(org.kuali.kfs.krad.util.ObjectUtils.class, "materializeSubObjectsToDepth");
        PowerMock.mockStatic(KRADUtils.class);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testClassNotFound() throws Exception {
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(getModuleConfiguration());
        EasyMock.expect(dataDictionaryService.containsDictionaryObject("Sillyclasse")).andReturn(false);

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, dataDictionaryService);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);

        Response response = apiResource.getSingleObject("sys", "sillyclasses", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, dataDictionaryService);
        Assert.assertTrue("Should have returned 404", response.getStatus() == Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testNotAuthorized() throws Exception {
        ModuleConfiguration moduleConfig = getModuleConfiguration();
        String className = "UnitOfMeasure";
        Class clazz = UnitOfMeasure.class;
        String namespaceCode = "KFS-SYS";

        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(dataDictionaryService.containsDictionaryObject(className)).andReturn(true);
        EasyMock.expect(dataDictionaryService.getDictionaryObject(className)).andReturn(getDDEntry(clazz));
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession);
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, className));
        EasyMock.expect(userSession.getPerson()).andReturn(testPerson);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, className), Collections.<String, String>emptyMap()))
            .andReturn(false);


        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);

        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        Assert.assertTrue("Should have returned Forbidden", response.getStatus() == Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testNoAccessSecurity() throws Exception {
        ModuleConfiguration moduleConfig = getModuleConfiguration();
        String className = "UnitOfMeasure";
        Class clazz = UnitOfMeasure.class;
        String namespaceCode = "KFS-SYS";
        Collection collection = Arrays.asList(BusinessObjectApiResourceTestHelper.getUom());

        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(dataDictionaryService.containsDictionaryObject(className)).andReturn(true);
        EasyMock.expect(dataDictionaryService.getDictionaryObject(className)).andReturn(getDDEntry(clazz));
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).times(2);
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, className));
        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).times(2);
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, className), Collections.<String, String>emptyMap()))
            .andReturn(true);
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(clazz, queryCriteria)).andReturn(collection);
        EasyMock.expect(configurationService.getPropertyValueAsBoolean(SecConstants.ACCESS_SECURITY_MODULE_ENABLED_PROPERTY_NAME)).andReturn(true).anyTimes();
        EasyMock.expect(accessSecurityService.getInquiryWithFieldValueTemplate()).andReturn(null);
        EasyMock.expect(KRADUtils.getNamespaceCode(clazz)).andReturn(namespaceCode);
        accessSecurityService.applySecurityRestrictions((List) collection, testPerson, null, Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode));
        EasyMock.expectLastCall().andDelegateTo(new AccessSecurityServiceImpl() {
            @Override
            public void applySecurityRestrictions(List<? extends BusinessObject> results, Person person, Template permissionTemplate, Map<String, String> additionalPermissionDetails) {
                results.clear();
            }
        });

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession, configurationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectResource.setConfigurationService(configurationService);

        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession);
        Assert.assertTrue("Should have returned Forbidden", response.getStatus() == Status.FORBIDDEN.getStatusCode());
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSimpleBoReturned() throws Exception {
        commonSingleBusinessObjectTestPrep(UnitOfMeasure.class, "KFS-SYS", BusinessObjectApiResourceTestHelper.getUom(), getModuleConfiguration());

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession, configurationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectResource.setConfigurationService(configurationService);

        Response response = apiResource.getSingleObject("sys", "unit-of-measures", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, permissionService, accessSecurityService, userSession, configurationService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(uom);
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(),
            mapsEqualEnough(entity, beanMap, "itemUnitOfMeasureCode", "itemUnitOfMeasureDescription"));
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testComplexBoReturned() throws Exception {
        commonSingleBusinessObjectTestPrep(Deposit.class, "KFS-FP", BusinessObjectApiResourceTestHelper.getDeposit(now), getFpModuleConfiguration());
        EasyMock.expect(dataDictionaryService.getAttributeDefinition(Deposit.class.getSimpleName(), EasyMock.anyString())).andReturn(null).anyTimes();

        EasyMock.expect(dataDictionary.getBusinessObjectEntryForConcreteClass(DepositCashReceiptControl.class.getName()))
                .andReturn(getDDEntry(DepositCashReceiptControl.class)).times(2);
        EasyMock.expect(dataDictionary.getBusinessObjectEntryForConcreteClass(Bank.class.getName())).andReturn(getDDEntry(Bank.class));
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Bank.class)).andReturn(moduleService);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(DepositCashReceiptControl.class)).andReturn(moduleService).times(2);
        EasyMock.expect(configurationService.getPropertyValueAsString(KRADConstants.APPLICATION_URL_KEY)).andReturn("http://myapp").times(3);

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, configurationService, dataDictionaryService,
            dataDictionary, permissionService, accessSecurityService, userSession);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setConfigurationService(configurationService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);

        Response response = apiResource.getSingleObject("fp", "deposits", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, persistenceStructureService, dataDictionaryService, dataDictionary,
             permissionService, accessSecurityService, userSession, configurationService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(deposit);
        Assert.assertTrue("Beans should have matching values " + beanMap.toString() + " and " + entity.toString(),
            mapsEqualEnough(entity, beanMap, "depositBankCode", "depositAmount"));
        Map<String, Object> bankMap = (Map<String, Object>) entity.get("bank");
        String bankLink = (String) bankMap.get("link");
        Assert.assertEquals("Bank link incorrect: " + bankLink, "http://myapp/api/v1/business-object/fp/banks/B123", bankLink);
        List<Map<String, Object>> depositCashReceiptControl = (List<Map<String, Object>>) entity.get("depositCashReceiptControl");
        Assert.assertEquals("Should be 2 depositCashReceiptControl", 2, depositCashReceiptControl.size());
        String dcLink = (String) depositCashReceiptControl.get(0).get("link");
        Assert.assertEquals("depositCashReceiptControl link incorrect", "http://myapp/api/v1/business-object/fp/deposit-cash-receipt-controls/DC001", dcLink);
        Long depositDate = (Long) entity.get("depositDate");
        Assert.assertEquals("depositDate incorrect", now.getTime(), depositDate.longValue());
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSimpleMaskedBoReturned() throws Exception {
        commonSingleBusinessObjectTestPrep(Bank.class, "KFS-SYS", BusinessObjectApiResourceTestHelper.getBank(), getModuleConfiguration());

        EasyMock.expect(businessObjectAuthorizationService.isNonProductionEnvAndUnmaskingTurnedOff()).andReturn(false).anyTimes();
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
        EasyMock.expect(dataDictionary.getBusinessObjectEntry("Bank")).andReturn(getDDEntry(Bank.class)).anyTimes();
        EasyMock.expect(businessObjectAuthorizationService.canFullyUnmaskField(testPerson, Bank.class, "bankAccountNumber", null)).andReturn(false);
        EasyMock.expect(businessObjectAuthorizationService.canPartiallyUnmaskField(testPerson, Bank.class, "bankRoutingNumber", null)).andReturn(false);
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Chart.class)).andReturn(moduleService);
        EasyMock.expect(dataDictionary.getBusinessObjectEntryForConcreteClass("org.kuali.kfs.coa.businessobject.Chart")).andReturn(null);

        EasyMock.replay(kualiModuleService, moduleService, businessObjectService, businessObjectAuthorizationService, persistenceStructureService, dataDictionaryService, dataDictionary, permissionService, accessSecurityService, userSession, configurationService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setPermissionService(permissionService);
        BusinessObjectResource.setAccessSecurityService(accessSecurityService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectResource.setConfigurationService(configurationService);
        BusinessObjectResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);

        Response response = apiResource.getSingleObject("sys", "banks", "12345");
        EasyMock.verify(kualiModuleService, moduleService, businessObjectService, businessObjectAuthorizationService, persistenceStructureService, dataDictionaryService, dataDictionary, permissionService, accessSecurityService, userSession, configurationService);
        Assert.assertTrue("Should have returned OK", response.getStatus() == Status.OK.getStatusCode());
        Map<String, Object> entity = (Map<String, Object>) response.getEntity();
        BeanMap beanMap = new BeanMap(bank);
        Assert.assertTrue("Banks should have matching values " + beanMap.toString() + " and " + entity.toString(),
            mapsEqualEnough(entity, beanMap, "bankCode", "bankName"));
        Assert.assertNotEquals("Banks should not match routing numbers", beanMap.get(KFSPropertyConstants.BANK_ROUTING_NUMBER), entity.get(KFSPropertyConstants.BANK_ROUTING_NUMBER));
        Assert.assertNotEquals("Banks should not match account numbers", beanMap.get(KFSPropertyConstants.BANK_ACCOUNT_NUMBER), entity.get(KFSPropertyConstants.BANK_ACCOUNT_NUMBER));
        Assert.assertEquals("Bank routing number should be partially masked", "************4444", entity.get(KFSPropertyConstants.BANK_ROUTING_NUMBER));
        Assert.assertEquals("Bank account number should be fully masked", "XXXXZZZZ", entity.get(KFSPropertyConstants.BANK_ACCOUNT_NUMBER));
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSortCriteria() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountName");

        String[] sort = apiResource.getSortCriteria(Account.class, params);

        Assert.assertEquals(new String[]{"accountName"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSortCriteria_NotSpecified() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        List<String> primaryKeys = Arrays.asList("chartOfAccountsCode", "accountNumber");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        EasyMock.expect(persistenceStructureService.listPrimaryKeyFieldNames(Account.class)).andReturn(primaryKeys);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        String[] sort = apiResource.getSortCriteria(Account.class, params);

        Assert.assertEquals(new String[]{"chartOfAccountsCode","accountNumber"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSortCriteria_NotSpecified_NoPrimaryKey() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        List<String> primaryKeys = new ArrayList<>();
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        EasyMock.expect(persistenceStructureService.listPrimaryKeyFieldNames(Account.class)).andReturn(primaryKeys);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        String[] sort = apiResource.getSortCriteria(Account.class, params);

        Assert.assertEquals(new String[]{"objectId"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSortCriteria_NotSpecified_NoPrimaryKey_NoObjectId_NoNothing() {
        List<String> validFields = Arrays.asList("accountName", "accountNumber", "chartOfAccountsCode");
        List<String> primaryKeys = new ArrayList<>();
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        EasyMock.expect(persistenceStructureService.listPrimaryKeyFieldNames(Account.class)).andReturn(primaryKeys);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        String[] sort = apiResource.getSortCriteria(Account.class, params);

        Assert.assertEquals(new String[]{"accountName"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSortCriteria_Descending() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "-accountName");

        String[] sort = apiResource.getSortCriteria(Account.class, params);

        Assert.assertEquals(new String[]{"-accountName"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSortCriteria_Mutli() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountName,accountNumber");

        String[] sort = apiResource.getSortCriteria(Account.class, params);

        Assert.assertEquals(new String[]{"accountName", "accountNumber"}, sort);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSortCriteria_MutliBad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "accountname,accountnumber");

        try {
            String[] sort = apiResource.getSortCriteria(Account.class, params);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid sort field", "class"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(2, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("accountname", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(1).getMessage());
            Assert.assertEquals("accountnumber", ((List<ErrorMessage>)error.get("details")).get(1).getProperty());
        }

        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSortCriteria_Bad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("sort", "class");

        try {
            String[] sort = apiResource.getSortCriteria(Account.class, params);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid sort field", "class"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid sort field", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("class", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }

        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSearchQueryCriteria() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountName", "bob");

        Map<String, String> criteria = apiResource.getSearchQueryCriteria(Account.class, params);

        Map<String, String> validCriteria = new HashMap<>();
        validCriteria.put("accountName", "bob");
        Assert.assertEquals(validCriteria, criteria);
        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSearchQueryCriteria_Bad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountname", "bob");

        try {
            Map<String, String> criteria = apiResource.getSearchQueryCriteria(Account.class, params);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid query parameter name", "accountname"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("accountname", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }

        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetSearchQueryCriteria_MultiBad() {
        List<String> validFields = Arrays.asList("objectId", "accountName", "accountNumber", "chartOfAccountsCode");
        EasyMock.expect(persistenceStructureService.listFieldNames(Account.class)).andReturn(validFields);
        apiResource.setPersistenceStructureService(persistenceStructureService);
        EasyMock.replay(persistenceStructureService);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("accountname", "bob");
        params.add("accountnumber", "bfdsa5432adfdsaf");

        try {
            Map<String, String> criteria = apiResource.getSearchQueryCriteria(Account.class, params);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("invalid query parameter name", "accountname"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(2, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("invalid query parameter name", ((List<ErrorMessage>)error.get("details")).get(1).getMessage());
        }

        EasyMock.verify(persistenceStructureService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetIntQueryParameter_Bad() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "a");

        try {
            int limit = apiResource.getIntQueryParameter("limit", params);
        } catch (ApiRequestException are) {
            Response response = are.getResponse();

            Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

            Map<String, Object> exceptionMap = new HashedMap();
            exceptionMap.put("message", "Invalid Search Criteria");
            List<ErrorMessage> errorMessages = new ArrayList<>();
            errorMessages.add(new ErrorMessage("parameter is not a number", "limit"));
            exceptionMap.put("details", errorMessages);
            Map<String, Object> error = (Map<String, Object>)response.getEntity();
            Assert.assertEquals("Invalid Search Criteria", error.get("message"));
            Assert.assertEquals(1, ((List<ErrorMessage>)error.get("details")).size());
            Assert.assertEquals("parameter is not a number", ((List<ErrorMessage>)error.get("details")).get(0).getMessage());
            Assert.assertEquals("limit", ((List<ErrorMessage>)error.get("details")).get(0).getProperty());
        }
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testGetIntQueryParameter() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "3");

        int limit = apiResource.getIntQueryParameter("limit", params);
        Assert.assertEquals(3, limit);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSearchBusinessObjects() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("bankCode", "FW");

        commonMultipleBankBusinessObjectTestPrep(BusinessObjectApiResourceTestHelper.getBank(), queryCriteria, 1, 1, new String[] { "bankCode" });
        EasyMock.expect(kualiModuleService.getResponsibleModuleService(Chart.class)).andReturn(moduleService);
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(BusinessObjectApiResourceTestHelper.getCoaModuleConfiguration(dataDictionaryService));
        EasyMock.expect(dataDictionary.getBusinessObjectEntryForConcreteClass("org.kuali.kfs.coa.businessobject.Chart")).andReturn(null);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("bankCode", "FW");
        params.add("limit", "1");
        params.add("skip", "1");

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary, userSession, kualiModuleService, moduleService);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);

        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectResource.setKualiModuleService(kualiModuleService);

        Map<String, Object> results = apiResource.searchBusinessObjects(Bank.class, uriInfo);

        Assert.assertEquals(6, results.size());
        Assert.assertTrue("results should specify limit", results.containsKey("limit"));
        Assert.assertEquals(1, results.get("limit"));
        Assert.assertTrue("results should specify skip", results.containsKey("skip"));
        Assert.assertEquals(1, results.get("skip"));
        Assert.assertTrue("results should specify totalCount", results.containsKey("totalCount"));
        Assert.assertEquals(1, results.get("totalCount"));
        Assert.assertTrue("results should specify query", results.containsKey("query"));
        Map<String, String> query = new HashMap<>();
        query.put("bankCode", "FW");
        Assert.assertEquals(query, results.get("query"));
        Assert.assertTrue("results should specify sort", results.containsKey("sort"));
        Assert.assertEquals("bankCode", ((String[])results.get("sort"))[0]);
        Assert.assertTrue("results should specify results", results.containsKey("results"));
        Assert.assertEquals(1, ((List<Object>)results.get("results")).size());
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary, userSession, kualiModuleService, moduleService);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class})
    public void testSearchBusinessObjects_NoResults() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("bankCode", "FW");

        commonMultipleBankBusinessObjectTestPrep(null, queryCriteria, 2, 3, new String[] { "bankCode" });

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("bankCode", "FW");
        params.add("limit", "3");
        params.add("skip", "2");

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary, userSession);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);

        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);

        Map<String, Object> results = apiResource.searchBusinessObjects(Bank.class, uriInfo);

        Assert.assertEquals(6, results.size());
        Assert.assertTrue("results should specify limit", results.containsKey("limit"));
        Assert.assertEquals(3, results.get("limit"));
        Assert.assertTrue("results should specify skip", results.containsKey("skip"));
        Assert.assertEquals(2, results.get("skip"));
        Assert.assertTrue("results should specify totalCount", results.containsKey("totalCount"));
        Assert.assertEquals(0, results.get("totalCount"));
        Assert.assertTrue("results should specify query", results.containsKey("query"));
        Map<String, String> query = new HashMap<>();
        query.put("bankCode", "FW");
        Assert.assertEquals(query, results.get("query"));
        Assert.assertTrue("results should specify sort", results.containsKey("sort"));
        Assert.assertEquals("bankCode", ((String[])results.get("sort"))[0]);
        Assert.assertTrue("results should specify results", results.containsKey("results"));
        Assert.assertEquals(0, ((List<Object>)results.get("results")).size());
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary, userSession);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class, LookupUtils.class})
    public void testSearchBusinessObjects_NoLimitSpecified() {
        Map<String, String> queryCriteria = new HashMap<>();
        queryCriteria.put("bankCode", "FW");

        commonMultipleBankBusinessObjectTestPrep(null, queryCriteria, 0, 200, new String[] { "bankCode" });

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("bankCode", "FW");

        UriInfo uriInfo = EasyMock.createMock(UriInfo.class);
        EasyMock.expect(uriInfo.getQueryParameters()).andReturn(params);

        PowerMock.mockStatic(LookupUtils.class);
        EasyMock.expect(LookupUtils.getSearchResultsLimit(Bank.class)).andReturn(200);

        EasyMock.replay(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary, userSession);
        PowerMock.replay(KRADServiceLocator.class);
        PowerMock.replay(org.kuali.kfs.krad.util.ObjectUtils.class);
        PowerMock.replay(KRADUtils.class);
        PowerMock.replay(LookupUtils.class);

        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);
        BusinessObjectResource.setBusinessObjectService(businessObjectService);
        BusinessObjectResource.setBusinessObjectAuthorizationService(businessObjectAuthorizationService);
        BusinessObjectResource.setPersistenceStructureService(persistenceStructureService);
        BusinessObjectResource.setDataDictionaryService(dataDictionaryService);

        Map<String, Object> results = apiResource.searchBusinessObjects(Bank.class, uriInfo);

        Assert.assertEquals(6, results.size());
        Assert.assertTrue("results should specify limit", results.containsKey("limit"));
        Assert.assertEquals(200, results.get("limit"));
        Assert.assertTrue("results should specify skip", results.containsKey("skip"));
        Assert.assertEquals(0, results.get("skip"));
        Assert.assertTrue("results should specify totalCount", results.containsKey("totalCount"));
        Assert.assertEquals(0, results.get("totalCount"));
        Assert.assertTrue("results should specify query", results.containsKey("query"));
        Map<String, String> query = new HashMap<>();
        query.put("bankCode", "FW");
        Assert.assertEquals(query, results.get("query"));
        Assert.assertTrue("results should specify sort", results.containsKey("sort"));
        Assert.assertEquals("bankCode", ((String[])results.get("sort"))[0]);
        Assert.assertTrue("results should specify results", results.containsKey("results"));
        Assert.assertEquals(0, ((List<Object>)results.get("results")).size());
        EasyMock.verify(uriInfo, businessObjectService, persistenceStructureService, dataDictionaryService, businessObjectAuthorizationService, dataDictionary, userSession);
        PowerMock.verify(LookupUtils.class);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class, LookupUtils.class})
    public void testGetLimit() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "5");

        int limit = apiResource.getLimit(Bank.class, params);

        Assert.assertEquals(5, limit);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class, LookupUtils.class})
    public void testGetLimit_NegativeLimitSpecified() {
        PowerMock.mockStatic(LookupUtils.class);
        EasyMock.expect(LookupUtils.getSearchResultsLimit(Bank.class)).andReturn(200);
        PowerMock.replay(LookupUtils.class);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("limit", "-1");

        int limit = apiResource.getLimit(Bank.class, params);

        Assert.assertEquals(200, limit);
        PowerMock.verify(LookupUtils.class);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class, org.kuali.kfs.krad.util.ObjectUtils.class, KRADUtils.class, LookupUtils.class})
    public void testGetLimit_BusinessObjectResultsLimitNegative() {
        PowerMock.mockStatic(LookupUtils.class);
        EasyMock.expect(LookupUtils.getSearchResultsLimit(Bank.class)).andReturn(-1);
        EasyMock.expect(LookupUtils.getApplicationSearchResultsLimit()).andReturn(200);
        PowerMock.replay(LookupUtils.class);

        MultivaluedMap<String, String> params = new MultivaluedMapImpl();

        int limit = apiResource.getLimit(Bank.class, params);

        Assert.assertEquals(200, limit);
        PowerMock.verify(LookupUtils.class);
    }

    private void commonSingleBusinessObjectTestPrep(Class clazz, String namespaceCode, PersistableBusinessObject result, ModuleConfiguration moduleConfig) {
        String className = clazz.getSimpleName();
        List<? extends PersistableBusinessObject> collection = Arrays.asList(result);
        EasyMock.expect(kualiModuleService.getInstalledModuleServices()).andReturn(getInstalledModuleServices());
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(moduleConfig).anyTimes();
        EasyMock.expect(dataDictionaryService.containsDictionaryObject(className)).andReturn(true);
        EasyMock.expect(dataDictionaryService.getDictionaryObject(className)).andReturn(getDDEntry(clazz));
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).anyTimes();
        EasyMock.expect(KRADUtils.getNamespaceAndComponentSimpleName(clazz)).andReturn(makeMap(namespaceCode, className));
        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).anyTimes();
        EasyMock.expect(permissionService.isAuthorizedByTemplate("testPrincipalId", "KR-NS", KimConstants.PermissionTemplateNames.INQUIRE_INTO_RECORDS, makeMap(namespaceCode, className), Collections.<String, String>emptyMap()))
            .andReturn(true);
        Map<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(KRADPropertyConstants.OBJECT_ID, "12345");
        EasyMock.expect(businessObjectService.findMatching(clazz, queryCriteria)).andReturn(collection);
        EasyMock.expect(configurationService.getPropertyValueAsBoolean(SecConstants.ACCESS_SECURITY_MODULE_ENABLED_PROPERTY_NAME)).andReturn(true).anyTimes();
        EasyMock.expect(accessSecurityService.getInquiryWithFieldValueTemplate()).andReturn(null);
        EasyMock.expect(KRADUtils.getNamespaceCode(clazz)).andReturn(namespaceCode);
        accessSecurityService.applySecurityRestrictions(collection, testPerson, null, Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode));
        EasyMock.expectLastCall();
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        org.kuali.kfs.krad.util.ObjectUtils.materializeSubObjectsToDepth(result, 3);
        PowerMock.expectLastCall();
        EasyMock.expect(persistenceStructureService.isPersistable(clazz)).andReturn(true);
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(clazz, "extension")).andReturn(null);
    }

    private void commonMultipleBankBusinessObjectTestPrep(Bank result, Map<String, String> queryCriteria, int skip, int limit, String[] sort) {
        String className = Bank.class.getSimpleName();

        List<Bank> collection = new ArrayList<>();
        if (result != null) {
            collection.add(result);
        }
        EasyMock.expect(businessObjectService.countMatching(Bank.class, queryCriteria)).andReturn(collection.size());
        EasyMock.expect(businessObjectService.findMatching(EasyMock.eq(Bank.class), EasyMock.eq(queryCriteria), EasyMock.eq(skip), EasyMock.eq(limit), EasyMock.aryEq(sort))).andReturn(collection);

        EasyMock.expect(businessObjectAuthorizationService.isNonProductionEnvAndUnmaskingTurnedOff()).andReturn(false).anyTimes();
        EasyMock.expect(dataDictionaryService.getDataDictionary()).andReturn(dataDictionary).anyTimes();
        EasyMock.expect(dataDictionary.getBusinessObjectEntry(className)).andReturn(getDDEntry(Bank.class)).anyTimes();
        EasyMock.expect(businessObjectAuthorizationService.canFullyUnmaskField(testPerson, Bank.class, "bankAccountNumber", null)).andReturn(false).anyTimes();
        EasyMock.expect(businessObjectAuthorizationService.canPartiallyUnmaskField(testPerson, Bank.class, "bankRoutingNumber", null)).andReturn(false).anyTimes();
        EasyMock.expect(KRADUtils.getUserSessionFromRequest(null)).andReturn(userSession).anyTimes();
        EasyMock.expect(userSession.getPerson()).andReturn(testPerson).anyTimes();

        List<String> validFields = Arrays.asList("objectId", "bankCode", "bankName", "bankRountingNumber", "bankAccountNumber");
        EasyMock.expect(persistenceStructureService.listFieldNames(Bank.class)).andReturn(validFields).anyTimes();

        List<String> primaryKeyFields = Arrays.asList("bankCode");
        EasyMock.expect(persistenceStructureService.listPrimaryKeyFieldNames(Bank.class)).andReturn(primaryKeyFields).once();

        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService).anyTimes();
        org.kuali.kfs.krad.util.ObjectUtils.materializeSubObjectsToDepth(result, 3);
        PowerMock.expectLastCall();
        EasyMock.expect(persistenceStructureService.isPersistable(Bank.class)).andReturn(true).anyTimes();
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(Bank.class, "extension")).andReturn(null).anyTimes();
    }

    private Map<String, String> makeMap(String namespaceCode, String className) {
        Map<String, String> result = new HashMap<>();
        result.put(KRADConstants.NAMESPACE_CODE, namespaceCode);
        result.put(KRADConstants.COMPONENT_NAME, className);
        return result;
    }

    private boolean mapsEqualEnough(Map<String, Object> map1, Map<Object, Object> map2, String... properties) {
        for (String property : properties) {
            if (!ObjectUtils.equals(map1.get(property), map2.get(property))) {
                return false;
            }
        }

        return true;
    }

    private ModuleConfiguration getModuleConfiguration() {
        ModuleConfiguration result = new ModuleConfiguration();
        result.setNamespaceCode("KFS-SYS");
        result.setPackagePrefixes(new ArrayList<>());
        result.getPackagePrefixes().add("org.kuali.kfs.sys");
        result.setDataDictionaryService(dataDictionaryService);
        return result;
    }

    private ModuleConfiguration getFpModuleConfiguration() {
        ModuleConfiguration result = new ModuleConfiguration();
        result.setNamespaceCode("KFS-FP");
        result.setPackagePrefixes(new ArrayList<>());
        result.getPackagePrefixes().add("org.kuali.kfs.fp");
        result.setDataDictionaryService(dataDictionaryService);
        return result;
    }

    private List<ModuleService> getInstalledModuleServices() {
        List<ModuleService> result = new ArrayList<>();
        result.add(moduleService);
        return result;
    }

    private BusinessObjectEntry getDDEntry(Class clazz) {
        BusinessObjectEntry result = new BusinessObjectEntry();
        result.setBusinessObjectClass(clazz);
        return result;
    }

    class MockDataDictionaryService implements DataDictionaryService {
        @Override
        public void setBaselinePackages(List baselinePackages) throws IOException {
        }

        @Override
        public DataDictionary getDataDictionary() {
            return null;
        }

        @Override
        public void addDataDictionaryLocations(List<String> locations) throws IOException {
        }

        @Override
        public ControlDefinition getAttributeControlDefinition(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public Integer getAttributeSize(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public Integer getAttributeMaxLength(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public Pattern getAttributeValidatingExpression(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeLabel(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeShortLabel(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeErrorLabel(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public Class<? extends Formatter> getAttributeFormatter(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public Boolean getAttributeForceUppercase(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeSummary(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeDescription(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public Boolean isAttributeRequired(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public Boolean isAttributeDefined(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public Class<? extends KeyValuesFinder> getAttributeValuesFinderClass(Class dataObjectClass, String attributeName) {
            return null;
        }

        @Override
        public String getCollectionLabel(Class dataObjectClass, String collectionName) {
            return null;
        }

        @Override
        public String getCollectionShortLabel(Class dataObjectClass, String collectionName) {
            return null;
        }

        @Override
        public String getCollectionSummary(Class dataObjectClass, String collectionName) {
            return null;
        }

        @Override
        public String getCollectionDescription(Class dataObjectClass, String collectionName) {
            return null;
        }

        @Override
        public ControlDefinition getAttributeControlDefinition(String entryName, String attributeName) {
            return null;
        }

        @Override
        public Integer getAttributeSize(String entryName, String attributeName) {
            return null;
        }

        @Override
        public Integer getAttributeMinLength(String entryName, String attributeName) {
            return null;
        }

        @Override
        public Integer getAttributeMaxLength(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeExclusiveMin(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeInclusiveMax(String entryName, String attributeName) {
            return null;
        }

        @Override
        public Pattern getAttributeValidatingExpression(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeLabel(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeShortLabel(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeErrorLabel(String entryName, String attributeName) {
            return null;
        }

        @Override
        public Class<? extends Formatter> getAttributeFormatter(String entryName, String attributeName) {
            return null;
        }

        @Override
        public Boolean getAttributeForceUppercase(String entryName, String attributeName) {
            return null;
        }

        @Override
        public AttributeSecurity getAttributeSecurity(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeSummary(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeDescription(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String getAttributeValidatingErrorMessageKey(String entryName, String attributeName) {
            return null;
        }

        @Override
        public String[] getAttributeValidatingErrorMessageParameters(String entryName, String attributeName) {
            return new String[0];
        }

        @Override
        public Boolean isAttributeRequired(String entryName, String attributeName) {
            return null;
        }

        @Override
        public Boolean isAttributeDefined(String entryName, String attributeName) {
            return null;
        }

        @Override
        public Class<? extends KeyValuesFinder> getAttributeValuesFinderClass(String entryName, String attributeName) {
            return null;
        }

        @Override
        public AttributeDefinition getAttributeDefinition(String entryName, String attributeName) {
            if (StringUtils.isBlank(entryName) || StringUtils.isBlank(attributeName)) {
                return null;
            }
            AttributeDefinition attributeDefinition = new AttributeDefinition();
            attributeDefinition.setName(attributeName);
            if (StringUtils.equals(entryName, "Bank")) {
                MaskFormatterSubString partialMaskFormatter = new MaskFormatterSubString();
                partialMaskFormatter.setMaskLength(12);
                MaskFormatterLiteral fullMaskFormatter = new MaskFormatterLiteral();
                fullMaskFormatter.setLiteral("XXXXZZZZ");
                if (StringUtils.equals(attributeName, KFSPropertyConstants.BANK_ROUTING_NUMBER)) {
                    AttributeSecurity attrSec = new AttributeSecurity();
                    attrSec.setPartialMask(true);
                    attrSec.setPartialMaskFormatter(partialMaskFormatter);
                    attrSec.setMaskFormatter(fullMaskFormatter);
                    attributeDefinition.setAttributeSecurity(attrSec);
                } else if (StringUtils.equals(attributeName, KFSPropertyConstants.BANK_ACCOUNT_NUMBER)) {
                    AttributeSecurity attrSec = new AttributeSecurity();
                    attrSec.setMask(true);
                    attrSec.setPartialMaskFormatter(partialMaskFormatter);
                    attrSec.setMaskFormatter(fullMaskFormatter);
                    attributeDefinition.setAttributeSecurity(attrSec);
                }
            }
            return attributeDefinition;
        }

        @Override
        public String getCollectionLabel(String entryName, String collectionName) {
            return null;
        }

        @Override
        public String getCollectionShortLabel(String entryName, String collectionName) {
            return null;
        }

        @Override
        public String getCollectionElementLabel(String entryName, String collectionName, Class dataObjectClass) {
            return null;
        }

        @Override
        public String getCollectionSummary(String entryName, String collectionName) {
            return null;
        }

        @Override
        public String getCollectionDescription(String entryName, String collectionName) {
            return null;
        }

        @Override
        public Class<? extends BusinessObject> getRelationshipSourceClass(String entryName, String relationshipName) {
            return null;
        }

        @Override
        public Class<? extends BusinessObject> getRelationshipTargetClass(String entryName, String relationshipName) {
            return null;
        }

        @Override
        public List<String> getRelationshipSourceAttributes(String entryName, String relationshipName) {
            return null;
        }

        @Override
        public List<String> getRelationshipTargetAttributes(String entryName, String relationshipName) {
            return null;
        }

        @Override
        public Map<String, String> getRelationshipAttributeMap(String entryName, String relationshipName) {
            return null;
        }

        @Override
        public List<String> getRelationshipEntriesForSourceAttribute(String entryName, String sourceAttributeName) {
            return null;
        }

        @Override
        public List<String> getRelationshipEntriesForTargetAttribute(String entryName, String targetAttributeName) {
            return null;
        }

        @Override
        public boolean hasRelationship(String entryName, String relationshipName) {
            return false;
        }

        @Override
        public List<String> getRelationshipNames(String entryName) {
            return null;
        }

        @Override
        public String getDocumentLabelByTypeName(String documentTypeName) {
            return null;
        }

        @Override
        public String getDocumentLabelByClass(Class documentOrBusinessObjectClass) {
            return null;
        }

        @Override
        public String getDocumentTypeNameByClass(Class documentClass) {
            return null;
        }

        @Override
        public String getValidDocumentTypeNameByClass(Class documentClass) {
            return null;
        }

        @Override
        public Class<? extends Document> getDocumentClassByTypeName(String documentTypeName) {
            return null;
        }

        @Override
        public Class<? extends Document> getValidDocumentClassByTypeName(String documentTypeName) {
            return null;
        }

        @Override
        public List<String> getGroupByAttributesForEffectiveDating(Class businessObjectClass) {
            return null;
        }

        @Override
        public Set<InactivationBlockingMetadata> getAllInactivationBlockingDefinitions(Class inactivationBlockedBusinessObjectClass) {
            return null;
        }

        @Override
        public View getViewById(String viewId) {
            return null;
        }

        @Override
        public Object getDictionaryObject(String id) {
            return null;
        }

        @Override
        public boolean containsDictionaryObject(String id) {
            return false;
        }

        @Override
        public View getViewByTypeIndex(UifConstants.ViewType viewTypeName, Map<String, String> indexKey) {
            return null;
        }
    }
}