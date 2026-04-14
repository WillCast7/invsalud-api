package com.aurealab.config.databases;

import com.aurealab.config.databases.multitenancy.SchemaMultiTenantConnectionProvider;
import com.aurealab.config.databases.multitenancy.TenantIdentifierResolver;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HibernateConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            SchemaMultiTenantConnectionProvider connectionProvider,
            TenantIdentifierResolver tenantResolver) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.aurealab.model");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());

        // CONFIGURACIÓN MULTITENANCY
        properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
        properties.put("hibernate.multi_tenant_strategy", "SCHEMA");

        // ✅ CRÍTICO: Deshabilitar caché de conexiones
        properties.put(AvailableSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, false);

        // ✅ CRÍTICO: Forzar nueva conexión en cada operación
        properties.put("hibernate.connection.provider_disables_autocommit", false);

        // Debug
        properties.put(AvailableSettings.SHOW_SQL, true);
        properties.put(AvailableSettings.FORMAT_SQL, true);
        properties.put(AvailableSettings.USE_SQL_COMMENTS, true);

        // Dialecto
        properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");

        System.out.println("🔧 EntityManagerFactory configurado con multitenancy SCHEMA");
        System.out.println("🔧 Connection Provider: " + connectionProvider.getClass().getName());
        System.out.println("🔧 Tenant Resolver: " + tenantResolver.getClass().getName());

        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public JpaTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}