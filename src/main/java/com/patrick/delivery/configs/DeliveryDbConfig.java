package com.patrick.delivery.configs;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * @author patrick on 7/31/20
 * @project sprintel-delivery
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mysqlEntityManagerFactory", transactionManagerRef = "mysqlTransactionManager",
        basePackages = {"com.patrick.delivery.repositories.api"})

public class DeliveryDbConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties deliveryDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Primary
    @Bean
    public DataSource deliveryDataSource(@Qualifier("deliveryDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }


    @Primary
    @PersistenceContext(unitName = "delivery")
    @Bean
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(@Qualifier("deliveryDataSource") DataSource hubDataSource,
                                                                            EntityManagerFactoryBuilder builder) {
        return builder.dataSource(hubDataSource).packages("com.patrick.delivery.entities")
                .persistenceUnit("delivery").build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager mysqlTransactionManager(@Qualifier("mysqlEntityManagerFactory") EntityManagerFactory factory) {
        return new JpaTransactionManager(factory);
    }

}
