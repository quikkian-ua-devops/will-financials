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
package org.kuali.kfs.sys.rest.service;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.kns.datadictionary.MaintainableCollectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.kfs.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.kfs.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.datadictionary.DataDictionary;
import org.kuali.kfs.krad.service.DataDictionaryService;
import org.kuali.kfs.krad.service.KRADServiceLocator;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.service.PersistenceStructureService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TaxRegion;
import org.kuali.kfs.sys.businessobject.TaxRegionRate;
import org.kuali.kfs.sys.rest.BusinessObjectApiResourceTestHelper;
import org.kuali.kfs.sys.rest.helper.CollectionSerializationHelper;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class SerializationServiceTest {

    private MaintenanceDocumentEntry maintenanceDocumentEntry;
    private KualiModuleService kualiModuleService;
    private ModuleService moduleService;
    private ConfigurationService configurationService;
    private DataDictionary dataDictionary;
    private DataDictionaryService dataDictionaryService;
    private PersistenceStructureService persistenceStructureService;

    @Before
    public void setup() {
        maintenanceDocumentEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
        kualiModuleService = EasyMock.createMock(KualiModuleService.class);
        moduleService = EasyMock.createMock(ModuleService.class);
        configurationService = EasyMock.createMock(ConfigurationService.class);
        dataDictionary = EasyMock.createMock(DataDictionary.class);
        dataDictionaryService = EasyMock.createMock(DataDictionaryService.class);
        persistenceStructureService = EasyMock.createMock(PersistenceStructureService.class);
        PowerMock.mockStatic(KRADServiceLocator.class);
    }

    @Test
    public void testBusinessObjectFieldsToMap() {
        List<String> fields = Arrays.asList(
            "accountName",
            "organization.responsibilityCenterCode",
            "organization.responsibilityCenterCode2",
            "organization.responsibilityCenter.responsibilityCenterName",
            "organization.responsibilityCenter.responsibilityCenterName2"
        );

        Map<String, Object> results = SerializationService.businessObjectFieldsToMap(fields);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(1, ((List<String>)results.get(SerializationService.FIELDS_KEY)).size());
        Assert.assertEquals("accountName", ((List<String>)results.get(SerializationService.FIELDS_KEY)).get(0));
        Map<String, Object> organization = (Map<String, Object>)results.get("organization");
        Assert.assertEquals(2, organization.size());
        Assert.assertEquals(2, ((List<String>)organization.get(SerializationService.FIELDS_KEY)).size());
        Assert.assertEquals("responsibilityCenterCode", ((List<String>)organization.get(SerializationService.FIELDS_KEY)).get(0));
        Map<String, Object> responsibilityCenter = (Map<String, Object>)organization.get("responsibilityCenter");
        Assert.assertEquals(1, responsibilityCenter.size());
        Assert.assertEquals(2, ((List<String>)responsibilityCenter.get(SerializationService.FIELDS_KEY)).size());
        Assert.assertEquals("responsibilityCenterName", ((List<String>)responsibilityCenter.get(SerializationService.FIELDS_KEY)).get(0));
    }

    @Test
    public void testFindBusinessObjectFields() {
        addTaxRegionMaintainbleSections();
        EasyMock.replay(maintenanceDocumentEntry);
        Map<String, Object> fields = SerializationService.findBusinessObjectFields(maintenanceDocumentEntry);
        Assert.assertEquals(2, fields.size());
        Assert.assertEquals(8, ((List<String>)fields.get(SerializationService.FIELDS_KEY)).size());
        Assert.assertEquals("taxRegionCode", ((List<String>)fields.get(SerializationService.FIELDS_KEY)).get(0));
        List<CollectionSerializationHelper> serializationHelpers = (List< CollectionSerializationHelper>)fields.get(SerializationService.COLLECTIONS_KEY);
        Assert.assertEquals(1, serializationHelpers.size());
        CollectionSerializationHelper serializationHelper = serializationHelpers.get(0);
        Assert.assertEquals("taxRegionRates", serializationHelper.getCollectionName());
        Assert.assertEquals(3, serializationHelper.getFields().size());
        Assert.assertEquals(2, serializationHelper.getTranslatedFields().size());
        List<String> collectionTopLevelFields = (List<String>)serializationHelper.getTranslatedFields().get(SerializationService.FIELDS_KEY);
        Assert.assertEquals(2, collectionTopLevelFields.size());
        Assert.assertEquals("effectiveDate", collectionTopLevelFields.get(0));
        Map<String, Object> taxRate = (Map<String, Object>)serializationHelper.getTranslatedFields().get("taxRate");
        Assert.assertEquals(1, taxRate.size());
        List<String> taxRateTopLevelFields = (List<String>)taxRate.get(SerializationService.FIELDS_KEY);
        Assert.assertEquals(1, taxRateTopLevelFields.size());
        Assert.assertEquals("name", taxRateTopLevelFields.get(0));
        EasyMock.verify(maintenanceDocumentEntry);
    }

    private void addTaxRegionMaintainbleSections() {
        List<MaintainableSectionDefinition> maintainableSections = new ArrayList<>();
        MaintainableSectionDefinition maintainableSectionDefinition = new MaintainableSectionDefinition();
        maintainableSections.add(maintainableSectionDefinition);
        List<MaintainableItemDefinition> maintainableItemDefinitions = BusinessObjectApiResourceTestHelper.createItemDefinitions("taxRegionCode",
            "taxRegionName","taxRegionTypeCode","chartOfAccountsCode", "accountNumber","financialObjectCode",
            "taxRegionUseTaxIndicator","active");

        MaintainableCollectionDefinition maintainableCollectionDefinition = new MaintainableCollectionDefinition();
        maintainableCollectionDefinition.setName("taxRegionRates");
        maintainableCollectionDefinition.setBusinessObjectClass(TaxRegionRate.class);
        List<MaintainableFieldDefinition> taxRegionRatesFieldDefinitions = BusinessObjectApiResourceTestHelper.createFieldDefinitions("effectiveDate","taxRateCode","taxRate.name");

        maintainableCollectionDefinition.setMaintainableFields(taxRegionRatesFieldDefinitions);
        maintainableItemDefinitions.add(maintainableCollectionDefinition);
        maintainableSectionDefinition.setMaintainableItems(maintainableItemDefinitions);
        EasyMock.expect(maintenanceDocumentEntry.getMaintainableSections()).andReturn(maintainableSections);
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class})
    public void testPopulateRelatedBusinessObjectFields_relatedObjects() {
        TaxRegion taxRegion = new TaxRegion();
        taxRegion.setObjectId("TR12345");

        final Chart mockChart = new Chart();
        mockChart.setObjectId("12345");
        taxRegion.setChartOfAccounts(mockChart);
        final Account mockAccount = new Account();
        mockAccount.setObjectId("54321");
        taxRegion.setAccount(mockAccount);

        Map<String, Object> serializedTaxRegion = new HashMap<>();

        EasyMock.expect(kualiModuleService.getResponsibleModuleService(EasyMock.anyObject())).andReturn(moduleService).anyTimes();
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(BusinessObjectApiResourceTestHelper.getCoaModuleConfiguration(dataDictionaryService)).times(2);
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(true).anyTimes();
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(EasyMock.anyObject(), EasyMock.matches("extension"))).andReturn(null).anyTimes();
        MaintenanceDocumentEntry chartMaintDocEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
        EasyMock.expect(chartMaintDocEntry.getDocumentTypeName()).andReturn("COAT");
        MaintenanceDocumentEntry accountMaintDocEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
        EasyMock.expect(accountMaintDocEntry.getDocumentTypeName()).andReturn("ACCT");
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(EasyMock.eq(Chart.class))).andReturn(chartMaintDocEntry);
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(EasyMock.eq(Account.class))).andReturn(accountMaintDocEntry);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)).andReturn("https://kuali.co/fin").times(2);

        EasyMock.replay(dataDictionary, configurationService, kualiModuleService, moduleService, dataDictionaryService, chartMaintDocEntry, accountMaintDocEntry);
        PowerMock.replay(KRADServiceLocator.class);

        SerializationService.populateRelatedBusinessObjectFields(taxRegion, serializedTaxRegion, kualiModuleService, configurationService, dataDictionary);

        EasyMock.verify(dataDictionary, configurationService, kualiModuleService, moduleService, dataDictionaryService, chartMaintDocEntry, accountMaintDocEntry);
        Assert.assertEquals("Serialized TaxRegion should have size of 2", 2, serializedTaxRegion.size());
        Assert.assertTrue("Serialized TaxRegion should have sub map named chartOfAccounts", serializedTaxRegion.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS));
        Assert.assertTrue("Serialized TaxRegion should have sub map named account", serializedTaxRegion.containsKey(KFSPropertyConstants.ACCOUNT));
        Assert.assertFalse("Serialized TaxRegion should NOT have sub map named objectCode", serializedTaxRegion.containsKey(KFSPropertyConstants.OBJECT_CODE));
        Map<String, Object> serializedChart = (Map<String, Object>)serializedTaxRegion.get(KFSPropertyConstants.CHART_OF_ACCOUNTS);
        Assert.assertEquals("Serialized ChartOfAccounts should have size of 1", 1, serializedChart.size());
        Assert.assertEquals("Serialized ChartOfAccounts link is incorrect", "https://kuali.co/fin/coa/api/v1/reference/coat/12345", serializedChart.get(KFSPropertyConstants.LINK));
        Map<String, Object> serializedAccount = (Map<String, Object>)serializedTaxRegion.get(KFSPropertyConstants.ACCOUNT);
        Assert.assertEquals("Serialized Account should have size of 1", 1, serializedAccount.size());
        Assert.assertEquals("Serialized Account link is incorrect", "https://kuali.co/fin/coa/api/v1/reference/acct/54321", serializedAccount.get(KFSPropertyConstants.LINK));
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class})
    public void testPopulateRelatedBusinessObjectFields_nestedObjects() {
        TaxRegion taxRegion = new TaxRegion();
        taxRegion.setObjectId("TR12345");

        final Chart mockChart = new Chart();
        mockChart.setObjectId("12345");
        taxRegion.setChartOfAccounts(mockChart);
        final Account mockAccount = new Account();
        mockAccount.setObjectId("54321");
        taxRegion.setAccount(mockAccount);

        Map<String, Object> serializedTaxRegion = new HashMap<>();
        Map<String, Object> account = new HashMap<>();
        account.put(KFSPropertyConstants.ACCOUNT_NUMBER, "3334443");
        serializedTaxRegion.put(KFSPropertyConstants.ACCOUNT, account);

        EasyMock.expect(kualiModuleService.getResponsibleModuleService(EasyMock.anyObject())).andReturn(moduleService).anyTimes();
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(BusinessObjectApiResourceTestHelper.getCoaModuleConfiguration(dataDictionaryService)).times(2);
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(true).anyTimes();
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(EasyMock.anyObject(), EasyMock.matches("extension"))).andReturn(null).anyTimes();
        MaintenanceDocumentEntry chartMaintDocEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
        EasyMock.expect(chartMaintDocEntry.getDocumentTypeName()).andReturn("COAT");
        MaintenanceDocumentEntry accountMaintDocEntry = EasyMock.createMock(MaintenanceDocumentEntry.class);
        EasyMock.expect(accountMaintDocEntry.getDocumentTypeName()).andReturn("ACCT");
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(EasyMock.eq(Chart.class))).andReturn(chartMaintDocEntry);
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(EasyMock.eq(Account.class))).andReturn(accountMaintDocEntry);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)).andReturn("https://kuali.co/fin").times(2);

        EasyMock.replay(dataDictionary, configurationService, kualiModuleService, moduleService, dataDictionaryService, chartMaintDocEntry, accountMaintDocEntry);
        PowerMock.replay(KRADServiceLocator.class);

        SerializationService.populateRelatedBusinessObjectFields(taxRegion, serializedTaxRegion, kualiModuleService, configurationService, dataDictionary);

        EasyMock.verify(dataDictionary, configurationService, kualiModuleService, moduleService, dataDictionaryService, chartMaintDocEntry, accountMaintDocEntry);
        Assert.assertEquals("Serialized TaxRegion should have size of 2", 2, serializedTaxRegion.size());
        Assert.assertTrue("Serialized TaxRegion should have sub map named chartOfAccounts", serializedTaxRegion.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS));
        Assert.assertTrue("Serialized TaxRegion should have sub map named account", serializedTaxRegion.containsKey(KFSPropertyConstants.ACCOUNT));
        Assert.assertFalse("Serialized TaxRegion should NOT have sub map named objectCode", serializedTaxRegion.containsKey(KFSPropertyConstants.OBJECT_CODE));
        Map<String, Object> serializedChart = (Map<String, Object>)serializedTaxRegion.get(KFSPropertyConstants.CHART_OF_ACCOUNTS);
        Assert.assertEquals("Serialized ChartOfAccounts should have size of 1", 1, serializedChart.size());
        Assert.assertEquals("Serialized ChartOfAccounts link is incorrect", "https://kuali.co/fin/coa/api/v1/reference/coat/12345", serializedChart.get(KFSPropertyConstants.LINK));
        Map<String, Object> serializedAccount = (Map<String, Object>)serializedTaxRegion.get(KFSPropertyConstants.ACCOUNT);
        Assert.assertEquals("Serialized Account should have size of 2", 2, serializedAccount.size());
        Assert.assertEquals("Serialized Account account number should still be there", "3334443", serializedAccount.get(KFSPropertyConstants.ACCOUNT_NUMBER));
        Assert.assertEquals("Serialized Account link is incorrect", "https://kuali.co/fin/coa/api/v1/reference/acct/54321", serializedAccount.get(KFSPropertyConstants.LINK));
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class})
    public void testPopulateRelatedBusinessObjectFields_relatedCollection() {
        TaxRegion taxRegion = new TaxRegion();
        taxRegion.setObjectId("TR12345");

        List<TaxRegionRate> taxRegionRates = new ArrayList<>();
        TaxRegionRate rate1 = new TaxRegionRate();
        rate1.setObjectId("12345");
        taxRegionRates.add(rate1);
        TaxRegionRate rate2 = new TaxRegionRate();
        rate2.setObjectId("54321");
        taxRegionRates.add(rate2);
        taxRegion.setTaxRegionRates(taxRegionRates);

        Map<String, Object> serializedTaxRegion = new HashMap<>();

        EasyMock.expect(kualiModuleService.getResponsibleModuleService(EasyMock.anyObject())).andReturn(moduleService).anyTimes();
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(BusinessObjectApiResourceTestHelper.getCoaModuleConfiguration(dataDictionaryService)).times(2);
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(true).anyTimes();
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(EasyMock.anyObject(), EasyMock.matches("extension"))).andReturn(null).anyTimes();
        MaintenanceDocumentEntry taxRegionRateMaintDocEntry = EasyMock.createMock(MaintenanceDocumentEntry.class); // no, it doesn't exist; but that's the beauty of mocking
        EasyMock.expect(taxRegionRateMaintDocEntry.getDocumentTypeName()).andReturn("TRRT").times(2);
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(EasyMock.eq(TaxRegionRate.class))).andReturn(taxRegionRateMaintDocEntry).times(2);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)).andReturn("https://kuali.co/fin").times(2);

        EasyMock.replay(dataDictionary, configurationService, kualiModuleService, moduleService, dataDictionaryService, taxRegionRateMaintDocEntry);
        PowerMock.replay(KRADServiceLocator.class);

        SerializationService.populateRelatedBusinessObjectFields(taxRegion, serializedTaxRegion, kualiModuleService, configurationService, dataDictionary);

        EasyMock.verify(dataDictionary, configurationService, kualiModuleService, moduleService, dataDictionaryService, taxRegionRateMaintDocEntry);
        Assert.assertEquals("Serialized TaxRegion should have size of 1", 1, serializedTaxRegion.size());
        Assert.assertTrue("Serialized TaxRegion should have sub map named taxRegionRates", serializedTaxRegion.containsKey(KFSConstants.TaxRegionConstants.TAX_REGION_RATES));
        Assert.assertFalse("Serialized TaxRegion should NOT have sub map named objectCode", serializedTaxRegion.containsKey(KFSPropertyConstants.OBJECT_CODE));
        List<Map<String, Object>> serializedTaxRegionRates = (List<Map<String, Object>>)serializedTaxRegion.get(KFSConstants.TaxRegionConstants.TAX_REGION_RATES);
        Assert.assertEquals("Serialized TaxRegionRates should have size of 2", 2, serializedTaxRegionRates.size());
        Assert.assertEquals("First serialized TaxRegionRate should have a size of 1", 1, serializedTaxRegionRates.get(0).size());
        Assert.assertEquals("First serialized TaxRegionRate link is incorrect", "https://kuali.co/fin/coa/api/v1/reference/trrt/12345", serializedTaxRegionRates.get(0).get(KFSPropertyConstants.LINK));
        Assert.assertEquals("Second serialized TaxRegionRate should have a size of 1", 1, serializedTaxRegionRates.get(1).size());
        Assert.assertEquals("Second serialized TaxRegionRate link is incorrect", "https://kuali.co/fin/coa/api/v1/reference/trrt/54321", serializedTaxRegionRates.get(1).get(KFSPropertyConstants.LINK));
    }

    @Test
    @PrepareForTest({KRADServiceLocator.class})
    public void testPopulateRelatedBusinessObjectFields_relatedExistingCollection() {
        TaxRegion taxRegion = new TaxRegion();
        taxRegion.setObjectId("TR12345");

        List<TaxRegionRate> taxRegionRates = new ArrayList<>();
        TaxRegionRate rate1 = new TaxRegionRate();
        rate1.setObjectId("12345");
        taxRegionRates.add(rate1);
        TaxRegionRate rate2 = new TaxRegionRate();
        rate2.setObjectId("54321");
        taxRegionRates.add(rate2);
        taxRegion.setTaxRegionRates(taxRegionRates);

        Map<String, Object> serializedTaxRegion = new HashMap<>();
        List<Map<String, Object>> serializeTaxRegionRates = new ArrayList<>();
        Map<String, Object> serializedRate1 = new HashMap<>();
        serializedRate1.put("taxRegionCode", "rateA");
        serializeTaxRegionRates.add(serializedRate1);
        Map<String, Object> serializedRate2 = new HashMap<>();
        serializedRate2.put("taxRegionCode", "rateB");
        serializeTaxRegionRates.add(serializedRate2);
        serializedTaxRegion.put(KFSConstants.TaxRegionConstants.TAX_REGION_RATES, serializeTaxRegionRates);

        EasyMock.expect(kualiModuleService.getResponsibleModuleService(EasyMock.anyObject())).andReturn(moduleService).anyTimes();
        EasyMock.expect(moduleService.getModuleConfiguration()).andReturn(BusinessObjectApiResourceTestHelper.getCoaModuleConfiguration(dataDictionaryService)).times(2);
        EasyMock.expect(KRADServiceLocator.getPersistenceStructureService()).andReturn(persistenceStructureService);
        EasyMock.expect(persistenceStructureService.isPersistable(EasyMock.anyObject())).andReturn(true).anyTimes();
        EasyMock.expect(persistenceStructureService.getBusinessObjectAttributeClass(EasyMock.anyObject(), EasyMock.matches("extension"))).andReturn(null).anyTimes();
        MaintenanceDocumentEntry taxRegionRateMaintDocEntry = EasyMock.createMock(MaintenanceDocumentEntry.class); // no, it doesn't exist; but that's the beauty of mocking
        EasyMock.expect(taxRegionRateMaintDocEntry.getDocumentTypeName()).andReturn("TRRT").times(2);
        EasyMock.expect(dataDictionary.getMaintenanceDocumentEntryForBusinessObjectClass(EasyMock.eq(TaxRegionRate.class))).andReturn(taxRegionRateMaintDocEntry).times(2);
        EasyMock.expect(configurationService.getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY)).andReturn("https://kuali.co/fin").times(2);

        EasyMock.replay(dataDictionary, configurationService, kualiModuleService, moduleService, dataDictionaryService, taxRegionRateMaintDocEntry);
        PowerMock.replay(KRADServiceLocator.class);

        SerializationService.populateRelatedBusinessObjectFields(taxRegion, serializedTaxRegion, kualiModuleService, configurationService, dataDictionary);

        EasyMock.verify(dataDictionary, configurationService, kualiModuleService, moduleService, dataDictionaryService, taxRegionRateMaintDocEntry);
        Assert.assertEquals("Serialized TaxRegion should have size of 1", 1, serializedTaxRegion.size());
        Assert.assertTrue("Serialized TaxRegion should have sub map named taxRegionRates", serializedTaxRegion.containsKey(KFSConstants.TaxRegionConstants.TAX_REGION_RATES));
        Assert.assertFalse("Serialized TaxRegion should NOT have sub map named objectCode", serializedTaxRegion.containsKey(KFSPropertyConstants.OBJECT_CODE));
        List<Map<String, Object>> serializedTaxRegionRates = (List<Map<String, Object>>)serializedTaxRegion.get(KFSConstants.TaxRegionConstants.TAX_REGION_RATES);
        Assert.assertEquals("Serialized TaxRegionRates should have size of 2", 2, serializedTaxRegionRates.size());
        Assert.assertEquals("First serialized TaxRegionRate should have a size of 2", 2, serializedTaxRegionRates.get(0).size());
        Assert.assertTrue("First serialized TaxRegionRate should still have serialized taxRegionCode", serializedTaxRegionRates.get(0).containsKey("taxRegionCode"));
        Assert.assertEquals("First serialized TaxRegionRate link is incorrect", "https://kuali.co/fin/coa/api/v1/reference/trrt/12345", serializedTaxRegionRates.get(0).get(KFSPropertyConstants.LINK));
        Assert.assertEquals("Second serialized TaxRegionRate should have a size of 2", 2, serializedTaxRegionRates.get(1).size());
        Assert.assertEquals("Second serialized TaxRegionRate link is incorrect", "https://kuali.co/fin/coa/api/v1/reference/trrt/54321", serializedTaxRegionRates.get(1).get(KFSPropertyConstants.LINK));
        Assert.assertTrue("Second serialized TaxRegionRate should still have serialized taxRegionCode", serializedTaxRegionRates.get(1).containsKey("taxRegionCode"));
    }
}