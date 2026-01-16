package com.aplusbinary.binarypixor.doc.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.aplusbinary.binarypixor.doc.dao.am",
    entityManagerFactoryRef = "accountManagementEntityManagerFactory",
    transactionManagerRef = "accountManagementTransactionManager"
)
@ConditionalOnProperty(name = "spring.datasource.account-management.url")
public class AccountManagementDatabaseConfig {

    @Bean(name = "accountManagementDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.account-management")
    public DataSource accountManagementDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:mysql://localhost:3306/accountmanagement?useSSL=false&serverTimezone=UTC")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .username("root")
                .password("qweqwe123")
                .build();
    }

    @Bean(name = "accountManagementEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean accountManagementEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("accountManagementDataSource") DataSource dataSource) {
        
        // Configure JPA properties for the account management database
        java.util.Map<String, Object> jpaProperties = new java.util.HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.show_sql", "false");
        jpaProperties.put("hibernate.format_sql", "false");
        
        return builder
                .dataSource(dataSource)
                .packages("com.aplusbinary.binarypixor.doc.model.am")
                .persistenceUnit("accountManagement")
                .properties(jpaProperties)
                .build();
    }

    @Bean(name = "accountManagementTransactionManager")
    public PlatformTransactionManager accountManagementTransactionManager(
            @Qualifier("accountManagementEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
