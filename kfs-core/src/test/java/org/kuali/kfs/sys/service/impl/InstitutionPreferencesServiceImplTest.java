package org.kuali.kfs.sys.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal;
import org.kuali.kfs.fp.businessobject.CreditCardType;
import org.kuali.kfs.fp.document.ServiceBillingDocument;
import org.kuali.kfs.krad.bo.ModuleConfiguration;
import org.kuali.kfs.krad.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.krad.datadictionary.DocumentEntry;
import org.kuali.kfs.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.DocumentAuthorizer;
import org.kuali.kfs.krad.document.DocumentPresentationController;
import org.kuali.kfs.krad.maintenance.Maintainable;
import org.kuali.kfs.krad.maintenance.MaintenanceDocument;
import org.kuali.kfs.krad.rules.rule.BusinessRule;
import org.kuali.kfs.krad.service.DocumentDictionaryService;
import org.kuali.kfs.krad.service.KualiModuleService;
import org.kuali.kfs.krad.service.ModuleService;
import org.kuali.kfs.krad.util.KRADConstants;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.dataaccess.PreferencesDao;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.identity.PersonImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class InstitutionPreferencesServiceImplTest {
    abstract class PreferencesDaoInstitutionPreferences implements PreferencesDao {
        public Integer cacheLength = null;

        @Override
        public Map<String, Object> findInstitutionPreferences() {
            return null;
        }

        @Override
        public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {
        }

        @Override
        public Map<String, Object> findInstitutionPreferencesCache(String principalName) {
            return null;
        }

        @Override
        public void setInstitutionPreferencesCacheLength(int seconds) {
            cacheLength = seconds;
        }

        @Override
        public int getInstitutionPreferencesCacheLength() {
            return cacheLength;
        }

        @Override
        public void cacheInstitutionPreferences(String principalName, Map<String, Object> institutionPreferences) {
        }

        @Override
        public Map<String, Object> getUserPreferences(String principalName) {
            return null;
        }

        @Override
        public void saveUserPreferences(String principalName, String preferences) {
        }
    }

    private PreferencesDaoInstitutionPreferences createFakePreferencesDaoInstitutionPreferences() {
        return new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId","123413535");
                ip.put("logoUrl", "static/images/out-of-the-box-logo-rtna.png");
                ip.put("institutionName", "Kuali");
                return ip;
            }

            @Override
            public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {

            }
        };
    }

    private PreferencesDaoInstitutionPreferences createFakePreferencesDaoInstitutionPreferencesWithMenu() {
        return new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "static/images/out-of-the-box-logo-rtna.png");
                ip.put("institutionName", "Kuali");

                List<Map<String, String>> menu = new ArrayList<>();
                Map<String, String> menuItem = new ConcurrentHashMap<>();
                menuItem.put("label", "Feedback");
                menuItem.put("link", "kr/kualiFeedbackReport.do");
                menu.add(menuItem);
                menuItem = new ConcurrentHashMap<>();
                menuItem.put("label", "Help");
                menuItem.put("link", "static/help/default.htm");
                menu.add(menuItem);

                ip.put("menu", menu);
                return ip;
            }

            @Override
            public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {

            }
        };
    }

    private PreferencesDaoInstitutionPreferences createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(List<Map<String, Object>> linkGroups) {
        return new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                Map<String, Object> ip = new ConcurrentHashMap<>();
                ip.put("institutionId", "123413535");
                ip.put("logoUrl", "static/images/out-of-the-box-logo-rtna.png");
                ip.put("institutionName", "Kuali");

                ip.put("linkGroups", linkGroups);

                return ip;
            }

            @Override
            public void saveInstitutionPreferences(String institutionId, Map<String, Object> preferences) {

            }
        };
    }

    private Map<String,Object> jsonToMap(String json) {
        try {
            return new ObjectMapper().readValue(json, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid json: " + json);
        }
    }

    @Test
    public void testFindInstitutionPreferencesLinks_NoLinkGroups() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNull("Link Groups should not exist", preferences.get("linkGroups"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasActionList() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include an action list url", preferences.containsKey("actionListUrl"));
        Assert.assertEquals("We should know what action list is", "http://tst.kfs.kuali.org/kfs-tst/kew/ActionList.do", preferences.get("actionListUrl"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasSignoutUrl() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a signoutUrl", preferences.containsKey("signoutUrl"));
        Assert.assertEquals("We should know what the signoutUrl is", "http://tst.kfs.kuali.org/kfs-tst/logout.do", preferences.get("signoutUrl"));
    }

    @Test
    public void testFindInstitutionPreferences_HasDocSearch() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferences());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a doc search url", preferences.containsKey("actionListUrl"));
        Assert.assertEquals("We should know what doc search is", "http://tst.kfs.kuali.org/kfs-tst/kew/DocumentSearch.do?docFormKey=88888888&hideReturnLink=true&returnLocation=http://tst.kfs.kuali.org/kfs-tst/index.jsp", preferences.get("docSearchUrl"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasHelpLinksNotTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithMenu());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a help url", StringUtils.isNotBlank(getMenuLinkUrl(preferences, "Help")));
        Assert.assertEquals("We should know what help url is", "static/help/default.htm", getMenuLinkUrl(preferences, "Help"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasHelpTransformedLinks() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithMenu());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new PersonImpl(), false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a help url", StringUtils.isNotBlank(getMenuLinkUrl(preferences, "Help")));
        Assert.assertEquals("We should know what help url is", "http://tst.kfs.kuali.org/kfs-tst/static/help/default.htm", getMenuLinkUrl(preferences, "Help"));
    }

    private String getMenuLinkUrl(Map<String, Object> preferences, String key) {
        List<Map<String, String>> menuItems = (List<Map<String, String>>)preferences.get("menu");
        if (CollectionUtils.isNotEmpty(menuItems)) {
            for (Map<String, String> menuItem: menuItems) {
                String label = menuItem.get("label");
                if (StringUtils.equals(label, key)) {
                    return menuItem.get("link");
                }
            }
        }
        return StringUtils.EMPTY;
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HasFeedback() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithMenu());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertTrue("Preferences should always include a feedback url", StringUtils.isNotBlank(getMenuLinkUrl(preferences, "Feedback")));
        Assert.assertEquals("We should know what feedback url is", "http://tst.kfs.kuali.org/kfs-tst/kr/kualiFeedbackReport.do", getMenuLinkUrl(preferences, "Feedback"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_HealthyLinkGroup() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "SB");
        link.put("type", "activities");
        link.put("linkType", "kfs");

        List<Map<String, String>> links = new ArrayList<>();
        links.add(link);

        Map<String, Object> linkGroup = new ConcurrentHashMap<>();
        linkGroup.put("label", "Test Menu");
        linkGroup.put("links", links);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(linkGroup);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_TransactionalDocumentTypeLinkIsTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "SB");
        link.put("type", "activities");
        link.put("linkType", "kfs");

        List<Map<String, String>> links = new ArrayList<>();
        links.add(link);

        Map<String, Object> linkGroup = new ConcurrentHashMap<>();
        linkGroup.put("label", "Test Menu");
        linkGroup.put("links", links);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(linkGroup);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));
        Assert.assertTrue("Link should have a type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("type")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("linkType")));

        String groupLink = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));
        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/financialServiceBilling.do?methodToCall=docHandler&command=initiate&docTypeName=SB", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_MissingDocumentTypeReturnsNoLink() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "SB");
        link.put("type", "activities");
        link.put("linkType", "kfs");

        Map<String, String> link2 = new ConcurrentHashMap<>();
        link2.put("documentTypeCode", "ZZZZ");
        link2.put("type", "reference");
        link2.put("linkType", "kfs");

        Map<String, String> link3 = new ConcurrentHashMap<>();
        link3.put("documentTypeCode", "CCR");
        link3.put("type", "activities");
        link3.put("linkType", "kfs");

        List<Map<String, String>> links = new ArrayList<>();
        links.add(link);
        links.add(link2);
        links.add(link3);

        Map<String, Object> linkGroup = new ConcurrentHashMap<>();
        linkGroup.put("label", "Test Menu");
        linkGroup.put("links", links);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(linkGroup);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));
        Assert.assertEquals("Link group should only have one link", 1, ((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).size());
        Assert.assertEquals("The one link should be for Service Billing", "Service Billing", ((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_MaintenanceDocumentTypeLinkIsTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "CCTY");
        link.put("type", "administration");
        link.put("linkType", "kfs");

        List<Map<String, String>> links = new ArrayList<>();
        links.add(link);

        Map<String, Object> linkGroup = new ConcurrentHashMap<>();
        linkGroup.put("label", "Test Menu");
        linkGroup.put("links", links);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(linkGroup);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));
        Assert.assertTrue("Link should have a type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("type")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("linkType")));

        String groupLink = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.fp.businessobject.CreditCardType&docFormKey=88888888&returnLocation=http://tst.kfs.kuali.org/kfs-tst/index.jsp&hideReturnLink=true", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_GlobalMaintenanceDocumentTypeLinkIsTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("documentTypeCode", "GORV");
        link.put("type", "administration");
        link.put("linkType", "kfs");

        List<Map<String, String>> links = new ArrayList<>();
        links.add(link);

        Map<String, Object> linkGroup = new ConcurrentHashMap<>();
        linkGroup.put("label", "Test Menu");
        linkGroup.put("links", links);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(linkGroup);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));
        Assert.assertTrue("Link should have a type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("type")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("linkType")));

        String groupLink = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/maintenance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.coa.businessobject.OrganizationReversionGlobal&hideReturnLink=true", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_RelativeLinkIsTransformed() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new InstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("link", "electronicFundTransfer.do?methodToCall=start");
        link.put("label", "Electronic Payment Claim");
        link.put("type", "activities");
        link.put("linkType", "kfs");

        List<Map<String, String>> links = new ArrayList<>();
        links.add(link);

        Map<String, Object> linkGroup = new ConcurrentHashMap<>();
        linkGroup.put("label", "Test Menu");
        linkGroup.put("links", links);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(linkGroup);

        institutionPreferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));
        Assert.assertTrue("Link should have a type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("type")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("linkType")));

        String groupLink = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/electronicFundTransfer.do?methodToCall=start", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));
    }

    @Test
    public void testFindInstitutionPreferencesNoLinks_hasNoLinks() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return jsonToMap("{ " +
                        "\"linkGroups\": [" +
                        "    { " +
                        "      \"label\": \"Test Menu\", " +
                        "      \"links\": [" +
                        "          { \"link\": \"electronicFundTransfer.do?methodToCall=start\",\"label\": \"Electronic Payment Claim\"," +
                        "            \"type\": \"activities\",\"linkType\": \"kfs\" }" +
                        "        ] " +
                        "    } " +
                        "] " +
                        "}");
            }
        });
        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesNoLinks();

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNull("Link Groups should not exist", preferences.get("linkGroups"));
    }

    @Test
    public void testFindInstitutionPreferencesLinks_canViewLinkPermission() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl() {
            @Override
            protected boolean canViewLink(Map<String, Object> permission, Person person) {
                return false;
            }
        };
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return jsonToMap("{ " +
                        "\"linkGroups\": [" +
                        "    { " +
                        "      \"label\": \"Test Menu\", " +
                        "      \"links\": [" +
                        "          { \"link\": \"electronicFundTransfer.do?methodToCall=start\",\"label\": \"Electronic Payment Claim\"," +
                        "            \"type\": \"activities\",\"linkType\": \"kfs\",\"permission\": {" +
                        "                \"templateNamespace\": \"KR-SYS\"," +
                        "                \"templateName\": \"Initiate Document\"," +
                        "                \"details\": { \"documentTypeCode\": \"ETB\" }" +
                        "              } }" +
                        "        ] " +
                        "    } " +
                        "] " +
                        "}");
            }
        });

        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertEquals("Link Group should be empty", 0, ((List) preferences.get("linkGroups")).size());
    }

    @Test
    public void testFindInstitutionPreferencesLinks_canInitiateDocumentPermission() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl() {
            @Override
            protected boolean canInitiateDocument(String documentTypeName, Person person) {
                return false;
            }
        };
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return jsonToMap("{ " +
                        "\"linkGroups\": [" +
                        "    { " +
                        "      \"label\": \"Test Menu\", " +
                        "      \"links\": [" +
                        "          { \"documentTypeCode\": \"SB\",\"type\": \"activities\",\"linkType\": \"kfs\" }" +
                        "        ] " +
                        "    } " +
                        "] " +
                        "}");
            }
        });

        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertEquals("Link Group should be empty", 0, ((List) preferences.get("linkGroups")).size());
    }

    @Test
    public void testFindInstitutionPreferencesLinks_canViewMaintableBusinessObjectLookupPermission() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl() {
            @Override
            protected boolean canViewBusinessObjectLookup(Class<?> businessObjectClass, Person person) {
                return false;
            }
        };
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return jsonToMap("{ " +
                        "\"linkGroups\": [" +
                        "    { " +
                        "      \"label\": \"Test Menu\", " +
                        "      \"links\": [" +
                        "          { \"documentTypeCode\": \"CCTY\",\"type\": \"administration\",\"linkType\": \"kfs\" }" +
                        "        ] " +
                        "    } " +
                        "] " +
                        "}");
            }
        });

        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertEquals("Link Group should be empty", 0, ((List) preferences.get("linkGroups")).size());
    }

    @Test
    public void testInstitutionPreferencesCacheSet() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        PreferencesDaoInstitutionPreferences dao = new PreferencesDaoInstitutionPreferences() {
        };
        institutionPreferencesServiceImpl.setPreferencesDao(dao);

        institutionPreferencesServiceImpl.setInstitutionPreferencesCacheLength(1000);
        Assert.assertEquals("Cache Length should be set",1000,(int)dao.cacheLength);
    }

    @Test
    public void testInstitutionPreferencesCacheGet() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        PreferencesDaoInstitutionPreferences dao = new PreferencesDaoInstitutionPreferences() {
        };
        institutionPreferencesServiceImpl.setPreferencesDao(dao);
        dao.cacheLength = 100;

        Assert.assertEquals("Cache Length should be retrieved",100,(int)institutionPreferencesServiceImpl.getInstitutionPreferencesCacheLength());
    }

    @Test
    public void testFindInstitutionPreferencesLinksCache() {
        InstitutionPreferencesServiceImpl institutionPreferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();
        institutionPreferencesServiceImpl.setPreferencesDao(new PreferencesDaoInstitutionPreferences() {
            private Map<String,Object> getData() {
                return jsonToMap("{ " +
                        "\"linkGroups\": [" +
                        "    { " +
                        "      \"label\": \"Test Menu\", " +
                        "      \"links\": [" +
                        "          { \"documentTypeCode\": \"CCTY\",\"type\": \"administration\",\"linkType\": \"kfs\" }" +
                        "        ] " +
                        "    } " +
                        "] " +
                        "}");
            }

            @Override
            public Map<String, Object> findInstitutionPreferencesCache(String principalName) {
                Map<String,Object> data = getData();
                data.put("cache","cached");
                return data;
            }

            @Override
            public Map<String, Object> findInstitutionPreferences() {
                return getData();
            }
        });

        institutionPreferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        institutionPreferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        institutionPreferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = institutionPreferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),true);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertEquals("Should have retrieved cached version","cached",preferences.get("cache"));
    }

    @Test
    public void testFindInstitutionPreferences_LookupLinkHasReturnLocation() {
        InstitutionPreferencesServiceImpl preferencesServiceImpl = new NoPermissionsInstitutionPreferencesServiceImpl();

        Map<String, String> link = new ConcurrentHashMap<>();
        link.put("link", "kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchJobStatus&docFormKey=88888888&hideReturnLink=true&conversionFields=name:name,group:group");
        link.put("label", "Batch Schedule");
        link.put("type", "administration");
        link.put("linkType", "kfs");

        Map<String, String> link2 = new ConcurrentHashMap<>();
        link2.put("link", "kr/lookup.do");
        link2.put("label", "Batch Schedule");
        link2.put("type", "administration");
        link2.put("linkType", "kfs");

        List<Map<String, String>> links = new ArrayList<>();
        links.add(link);
        links.add(link2);

        Map<String, Object> linkGroup = new ConcurrentHashMap<>();
        linkGroup.put("label", "Test Menu");
        linkGroup.put("links", links);

        List<Map<String, Object>> linkGroups = new ArrayList<>();
        linkGroups.add(linkGroup);

        preferencesServiceImpl.setPreferencesDao(createFakePreferencesDaoInstitutionPreferencesWithLinkGroups(linkGroups));
        preferencesServiceImpl.setDocumentDictionaryService(new StubDocumentDictionaryService());
        preferencesServiceImpl.setConfigurationService(new StubConfigurationService());
        preferencesServiceImpl.setKualiModuleService(new StubKualiModuleService());

        Map<String, Object> preferences = preferencesServiceImpl.findInstitutionPreferencesLinks(new TestPerson(),false);

        Assert.assertNotNull("Preferences should really really exist", preferences);
        Assert.assertNotNull("Link Groups should exist", preferences.get("linkGroups"));
        Assert.assertTrue("Link Groups should be a List", (preferences.get("linkGroups") instanceof List));
        Assert.assertTrue("Link Groups should not be empty", !CollectionUtils.isEmpty((List) preferences.get("linkGroups")));
        Assert.assertTrue("Link Groups should have a label", !StringUtils.isBlank((String) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("label")));
        Assert.assertTrue("Link groups should have links", !CollectionUtils.isEmpty((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")));

        Assert.assertTrue("Link should have a label", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("label")));
        Assert.assertTrue("Link should have a type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("type")));
        Assert.assertTrue("Link should have a link type", !StringUtils.isBlank(((List<Map<String, String>>) ((List<Map<String, Object>>) preferences.get("linkGroups")).get(0).get("links")).get(0).get("linkType")));

        String groupLink = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.sys.batch.BatchJobStatus&docFormKey=88888888&hideReturnLink=true&conversionFields=name:name,group:group&returnLocation=http://tst.kfs.kuali.org/kfs-tst/portal.do", groupLink);
        Assert.assertTrue("Link should NOT have a document type", StringUtils.isBlank(((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(0).get("documentTypeCode")));

        String groupLink2 = ((List<Map<String, String>>)((List<Map<String, Object>>)preferences.get("linkGroups")).get(0).get("links")).get(1).get("link");
        Assert.assertTrue("Link should have a link", !StringUtils.isBlank(groupLink2));

        Assert.assertEquals("Link should be generated correctly", "http://tst.kfs.kuali.org/kfs-tst/kr/lookup.do?returnLocation=http://tst.kfs.kuali.org/kfs-tst/portal.do", groupLink2);
    }

    protected class StubDocumentDictionaryService implements DocumentDictionaryService {
        @Override
        public String getLabel(String documentTypeName) {
            if (StringUtils.equals(documentTypeName, "SB")) {
                return "Service Billing";
            } else if (StringUtils.equals(documentTypeName, "CCTY")) {
                return "Credit Card Type";
            } else if (StringUtils.equals(documentTypeName, "GORV")) {
                return "Organization Reversion Global";
            }
            return null;
        }

        @Override
        public String getMaintenanceDocumentTypeName(Class dataObjectClass) {
            return null;
        }

        @Override
        public String getDescription(String documentTypeName) {
            return null;
        }

        @Override
        public Collection getDefaultExistenceChecks(Class dataObjectClass) {
            return null;
        }

        @Override
        public Collection getDefaultExistenceChecks(Document document) {
            return null;
        }

        @Override
        public Collection getDefaultExistenceChecks(String docTypeName) {
            return null;
        }

        @Override
        public Class<?> getMaintenanceDataObjectClass(String docTypeName) {
            if (StringUtils.equals(docTypeName, "CCTY")) {
                return CreditCardType.class;
            } else if (StringUtils.equals(docTypeName, "GORV")) {
                return OrganizationReversionGlobal.class;
            }
            return null;
        }

        @Override
        public Class<? extends Maintainable> getMaintainableClass(String docTypeName) {
            return null;
        }

        @Override
        public Class<? extends BusinessRule> getBusinessRulesClass(Document document) {
            return null;
        }

        @Override
        public Boolean getAllowsCopy(Document document) {
            return null;
        }

        @Override
        public Boolean getAllowsNewOrCopy(String docTypeName) {
            return null;
        }

        @Override
        public DocumentEntry getDocumentEntry(String docTypeName) {
            return null;
        }

        @Override
        public DocumentEntry getDocumentEntryByClass(Class<? extends Document> documentClass) {
            return null;
        }

        @Override
        public MaintenanceDocumentEntry getMaintenanceDocumentEntry(String docTypeName) {
            return null;
        }

        @Override
        public Class<?> getDocumentClassByName(String documentTypeName) {
            if (StringUtils.equals(documentTypeName, "SB")) {
                return ServiceBillingDocument.class;
            } else if (StringUtils.equals(documentTypeName, "CCTY") || StringUtils.equals(documentTypeName, "GORV")) {
                return FinancialSystemMaintenanceDocument.class;
            }
            return null;
        }

        @Override
        public String getDocumentTypeByClass(Class<? extends Document> documentClass) {
            return null;
        }

        @Override
        public Boolean getAllowsRecordDeletion(Class dataObjectClass) {
            return null;
        }

        @Override
        public Boolean getAllowsRecordDeletion(MaintenanceDocument document) {
            return null;
        }

        @Override
        public List<String> getLockingKeys(String docTypeName) {
            return null;
        }

        @Override
        public boolean getPreserveLockingKeysOnCopy(Class dataObjectClass) {
            return false;
        }

        @Override
        public DocumentAuthorizer getDocumentAuthorizer(String documentType) {
            return null;
        }

        @Override
        public DocumentAuthorizer getDocumentAuthorizer(Document document) {
            return null;
        }

        @Override
        public DocumentPresentationController getDocumentPresentationController(String documentType) {
            return null;
        }

        @Override
        public DocumentPresentationController getDocumentPresentationController(Document document) {
            return null;
        }
    }

    protected class TestPerson implements Person {
        @Override
        public String getPrincipalId() {
            return "1234567890";
        }

        @Override
        public String getPrincipalName() {
            return "Test";
        }

        @Override
        public String getEntityId() {
            return null;
        }

        @Override
        public String getEntityTypeCode() {
            return null;
        }

        @Override
        public String getFirstName() {
            return null;
        }

        @Override
        public String getFirstNameUnmasked() {
            return null;
        }

        @Override
        public String getMiddleName() {
            return null;
        }

        @Override
        public String getMiddleNameUnmasked() {
            return null;
        }

        @Override
        public String getLastName() {
            return null;
        }

        @Override
        public String getLastNameUnmasked() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getNameUnmasked() {
            return null;
        }

        @Override
        public String getEmailAddress() {
            return null;
        }

        @Override
        public String getEmailAddressUnmasked() {
            return null;
        }

        @Override
        public String getAddressLine1() {
            return null;
        }

        @Override
        public String getAddressLine1Unmasked() {
            return null;
        }

        @Override
        public String getAddressLine2() {
            return null;
        }

        @Override
        public String getAddressLine2Unmasked() {
            return null;
        }

        @Override
        public String getAddressLine3() {
            return null;
        }

        @Override
        public String getAddressLine3Unmasked() {
            return null;
        }

        @Override
        public String getAddressCity() {
            return null;
        }

        @Override
        public String getAddressCityUnmasked() {
            return null;
        }

        @Override
        public String getAddressStateProvinceCode() {
            return null;
        }

        @Override
        public String getAddressStateProvinceCodeUnmasked() {
            return null;
        }

        @Override
        public String getAddressPostalCode() {
            return null;
        }

        @Override
        public String getAddressPostalCodeUnmasked() {
            return null;
        }

        @Override
        public String getAddressCountryCode() {
            return null;
        }

        @Override
        public String getAddressCountryCodeUnmasked() {
            return null;
        }

        @Override
        public String getPhoneNumber() {
            return null;
        }

        @Override
        public String getPhoneNumberUnmasked() {
            return null;
        }

        @Override
        public String getCampusCode() {
            return null;
        }

        @Override
        public Map<String, String> getExternalIdentifiers() {
            return null;
        }

        @Override
        public boolean hasAffiliationOfType(String s) {
            return false;
        }

        @Override
        public List<String> getCampusCodesForAffiliationOfType(String s) {
            return null;
        }

        @Override
        public String getEmployeeStatusCode() {
            return null;
        }

        @Override
        public String getEmployeeTypeCode() {
            return null;
        }

        @Override
        public KualiDecimal getBaseSalaryAmount() {
            return null;
        }

        @Override
        public String getExternalId(String s) {
            return null;
        }

        @Override
        public String getPrimaryDepartmentCode() {
            return null;
        }

        @Override
        public String getEmployeeId() {
            return null;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public void refresh() {

        }
    }

    protected class StubConfigurationService implements ConfigurationService {
        @Override
        public String getPropertyValueAsString(String s) {
            if (StringUtils.equals(s, KFSConstants.APPLICATION_URL_KEY)) {
                return "http://tst.kfs.kuali.org/kfs-tst";
            } else if (StringUtils.equals(s, KRADConstants.WORKFLOW_URL_KEY)) {
                return "http://tst.kfs.kuali.org/kfs-tst/kew";
            }
            return null;
        }

        @Override
        public boolean getPropertyValueAsBoolean(String s) {
            return false;
        }

        @Override
        public Map<String, String> getAllProperties() {
            return null;
        }
    }

    protected class StubKualiModuleService implements KualiModuleService {
        @Override
        public List<ModuleService> getInstalledModuleServices() {
            return null;
        }

        @Override
        public ModuleService getModuleService(String moduleId) {
            return null;
        }

        @Override
        public ModuleService getModuleServiceByNamespaceCode(String namespaceCode) {
            return null;
        }

        @Override
        public boolean isModuleServiceInstalled(String namespaceCode) {
            return false;
        }

        @Override
        public ModuleService getResponsibleModuleService(Class boClass) {
            if (boClass.equals(ServiceBillingDocument.class) || boClass.equals(CreditCardType.class)) {
                return new ModuleService() {
                    @Override
                    public ModuleConfiguration getModuleConfiguration() {
                        FinancialSystemModuleConfiguration moduleConfig = new FinancialSystemModuleConfiguration();
                        moduleConfig.setNamespaceCode("KFS-FP");
                        return moduleConfig;
                    }

                    @Override
                    public boolean isResponsibleFor(Class businessObjectClass) {
                        return false;
                    }

                    @Override
                    public boolean isResponsibleForJob(String jobName) {
                        return false;
                    }

                    @Override
                    public List listPrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
                        return null;
                    }

                    @Override
                    public List<List<String>> listAlternatePrimaryKeyFieldNames(Class businessObjectInterfaceClass) {
                        return null;
                    }

                    @Override
                    public BusinessObjectEntry getExternalizableBusinessObjectDictionaryEntry(Class businessObjectInterfaceClass) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> T getExternalizableBusinessObject(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsList(Class<T> businessObjectClass, Map<String, Object> fieldValues) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> List<T> getExternalizableBusinessObjectsListForLookup(Class<T> businessObjectClass, Map<String, Object> fieldValues, boolean unbounded) {
                        return null;
                    }

                    @Override
                    public String getExternalizableDataObjectInquiryUrl(Class<?> inquiryDataObjectClass, Properties parameters) {
                        return null;
                    }

                    @Override
                    public String getExternalizableDataObjectLookupUrl(Class<?> inquiryDataObjectClass, Properties parameters) {
                        return null;
                    }

                    @Override
                    public String getExternalizableBusinessObjectInquiryUrl(Class inquiryBusinessObjectClass, Map<String, String[]> parameters) {
                        return null;
                    }

                    @Override
                    public String getExternalizableBusinessObjectLookupUrl(Class inquiryBusinessObjectClass, Map<String, String> parameters) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> T retrieveExternalizableBusinessObjectIfNecessary(BusinessObject businessObject, T currentInstanceExternalizableBO, String externalizableRelationshipName) {
                        return null;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> List<T> retrieveExternalizableBusinessObjectsList(BusinessObject businessObject, String externalizableRelationshipName, Class<T> externalizableClazz) {
                        return null;
                    }

                    @Override
                    public boolean isExternalizable(Class boClass) {
                        return false;
                    }

                    @Override
                    public boolean isExternalizableBusinessObjectLookupable(Class boClass) {
                        return false;
                    }

                    @Override
                    public boolean isExternalizableBusinessObjectInquirable(Class boClass) {
                        return false;
                    }

                    @Override
                    public <T extends ExternalizableBusinessObject> T createNewObjectFromExternalizableClass(Class<T> boClass) {
                        return null;
                    }

                    @Override
                    public <E extends ExternalizableBusinessObject> Class<E> getExternalizableBusinessObjectImplementation(Class<E> externalizableBusinessObjectInterface) {
                        return null;
                    }

                    @Override
                    public boolean isLocked() {
                        return false;
                    }

                    @Override
                    public boolean goToCentralRiceForInquiry() {
                        return false;
                    }

                    @Override
                    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

                    }

                    @Override
                    public void afterPropertiesSet() throws Exception {

                    }
                };
            }
            return null;
        }

        @Override
        public ModuleService getResponsibleModuleServiceForJob(String jobName) {
            return null;
        }

        @Override
        public void setInstalledModuleServices(List<ModuleService> moduleServices) {

        }

        @Override
        public List<String> getDataDictionaryPackages() {
            return null;
        }

        @Override
        public String getNamespaceName(String namespaceCode) {
            return null;
        }

        @Override
        public String getNamespaceCode(Class<?> documentOrStepClass) {
            return null;
        }

        @Override
        public String getComponentCode(Class<?> documentOrStepClass) {
            return null;
        }
    }

    class NoPermissionsInstitutionPreferencesServiceImpl extends InstitutionPreferencesServiceImpl {
        @Override
        protected boolean canViewLink(Map<String, Object> permission, Person person) {
            return true;
        }

        @Override
        protected boolean canInitiateDocument(String documentTypeName, Person person) {
            return true;
        }

        @Override
        protected boolean canViewBusinessObjectLookup(Class<?> businessObjectClass, Person person) {
            return true;
        }
    }
}
