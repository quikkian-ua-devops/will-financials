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
package org.kuali.kfs.krad.app.persistence.jpa;

import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


public class RiceLocalContainerEntityManagerFactoryBean extends LocalContainerEntityManagerFactoryBean {

    public RiceLocalContainerEntityManagerFactoryBean() {
        throw new RuntimeException(getClass().getName() + " can not be constructed without a datasource");
    }

    public RiceLocalContainerEntityManagerFactoryBean(DataSource datasource) {
        this("", datasource);
    }

    public RiceLocalContainerEntityManagerFactoryBean(String prefix, DataSource datasource) {
        if (prefix.equals("")) {
            prefix = "rice";
        }
        prefix += ".jpa.";

        Config config = ConfigContext.getCurrentContextConfig();

        setPersistenceUnitManager(preparePersistentUnitManager(config, prefix, datasource));
        setPersistenceXmlLocation(determineConfigProperty(config, prefix, "PersistenceXmlLocation", "META-INF/persistence.xml"));
        setDataSource(datasource);
        setPersistenceUnitName(determineConfigProperty(config, prefix, "PersistenceUnitName", "rice"));
        setJpaDialect(new org.springframework.orm.jpa.vendor.HibernateJpaDialect());
        setJpaPropertyMap(prepareJpaProperties(config, prefix));
        //setJpaVendorAdapter(prepareJpaVendorAdapter(config, prefix));

        RicePersistenceUnitPostProcessor postProcessor = new RicePersistenceUnitPostProcessor();
        postProcessor.setJtaDataSource(datasource);
        setPersistenceUnitPostProcessors(new RicePersistenceUnitPostProcessor[]{postProcessor});
    }


    private PersistenceUnitManager preparePersistentUnitManager(Config config, String prefix, DataSource datasource) {
        DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
        persistenceUnitManager.setDefaultDataSource(datasource);
        persistenceUnitManager.setPersistenceXmlLocations(new String[]{determineConfigProperty(config, prefix, "PersistenceXmlLocation", "META-INF/persistence.xml")});
        persistenceUnitManager.setDefaultPersistenceUnitRootLocation(determineConfigProperty(config, prefix, "PersistenceUnitRootLocation", "classpath:"));
        RicePersistenceUnitPostProcessor postProcessor = new RicePersistenceUnitPostProcessor();
        postProcessor.setJtaDataSource(datasource);
        persistenceUnitManager.setPersistenceUnitPostProcessors(new RicePersistenceUnitPostProcessor[]{postProcessor});
        persistenceUnitManager.afterPropertiesSet();
        return persistenceUnitManager;
    }

	/*private JpaVendorAdapter prepareJpaVendorAdapter(Config config, String prefix) {
        DevHibernateJpaVendorAdapter jpaVendorAdapter = new DevHibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabasePlatform(determineConfigProperty(config, prefix, "DatabasePlatform", "org.hibernate.dialect.MySQLDialect"));
		jpaVendorAdapter.setGenerateDdl(new Boolean(determineConfigProperty(config, prefix, "GenerateDdl", "false")));
		jpaVendorAdapter.setSerializationFilename(determineConfigProperty(config, prefix, "SerializationFilename", "/tmp/Ejb3Configuration.out"));
		jpaVendorAdapter.setUseSerialization(new Boolean(determineConfigProperty(config, prefix, "UseSerialization", "true")));
		try {
			jpaVendorAdapter.afterPropertiesSet();
		} catch (Exception e) {}
		return jpaVendorAdapter;
	}*/

    private Map<String, String> prepareJpaProperties(Config config, String prefix) {
        Map<String, String> jpaProperties = new HashMap<String, String>();

        // Load in all user specified "JPAProperties" prefixed properties
        jpaProperties.putAll(config.getPropertiesWithPrefix(prefix + "JpaProperties.", true));

        // Load in the defaults for a Hibernate JPA Setup. Since the JPA spec states that these properties will be ignored by
        // vendors that do not understand them, we can add all of the necessary defaults per supported JPA vendor here.
        jpaProperties.put("hibernate.show_sql", determineConfigProperty(config, prefix, "JpaProperties.hibernate.show_sql", "false"));
        jpaProperties.put("hibernate.format_sql", determineConfigProperty(config, prefix, "JpaProperties.hibernate.format_sql", "false"));
        jpaProperties.put("hibernate.use_sql_comments", determineConfigProperty(config, prefix, "JpaProperties.hibernate.use_sql_comments", "false"));
        // Default now JTOM rather than Atomikos. Atomikos can be used by setting (KULRICE-1909)
        //   JpaProperties.hibernate.transaction.manager_lookup_class=org.kuali.rice.core.jta.AtomikosTransactionManagerLookup
        // in a configuration file for a JPA persistence unit.
        jpaProperties.put("hibernate.transaction.manager_lookup_class", determineConfigProperty(config, prefix, "JpaProperties.hibernate.transaction.manager_lookup_class", "org.hibernate.transaction.JOTMTransactionManagerLookup"));
        //jpaProperties.put("hibernate.transaction.manager_lookup_class", determineConfigProperty(config, prefix, "JpaProperties.hibernate.transaction.manager_lookup_class", "org.kuali.rice.core.jta.AtomikosTransactionManagerLookup"));
        jpaProperties.put("hibernate.current_session_context_class", determineConfigProperty(config, prefix, "JpaProperties.hibernate.current_session_context_class", "org.hibernate.context.JTASessionContext"));
        jpaProperties.put("hibernate.connection.release_mode", determineConfigProperty(config, prefix, "JpaProperties.hibernate.connection.release_mode", "auto"));
        jpaProperties.put("hibernate.transaction.flush_before_completion", determineConfigProperty(config, prefix, "JpaProperties.hibernate.transaction.flush_before_completion", "true"));
        jpaProperties.put("hibernate.bytecode.use_reflection_optimizer", determineConfigProperty(config, prefix, "JpaProperties.hibernate.bytecode.use_reflection_optimizer", "false"));
        jpaProperties.put("hibernate.transaction.auto_close_session", determineConfigProperty(config, prefix, "JpaProperties.hibernate.transaction.auto_close_session", "false"));
        jpaProperties.put("hibernate.hbm2ddl.auto", determineConfigProperty(config, prefix, "JpaProperties.hibernate.hbm2ddl.auto", ""));

        // TODO: Add more vendor specific defaults...

        return jpaProperties;
    }

    private String determineConfigProperty(Config config, String prefix, String key, String defaultValue) {
        String value = config.getProperty(prefix + key);
        // fallback on the defaults (non-module based properties)
        if (value == null) {
            value = config.getProperty("rice.jpa." + key);
        }
        // fallback on the default value passed in if still no value found for key
        return value == null ? defaultValue : value;
    }

}
