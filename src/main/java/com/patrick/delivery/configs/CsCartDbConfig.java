//package com.swiggo.swiggodelivery.configs;
//
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.PersistenceContext;
//import javax.sql.DataSource;
//
//
///**
// * @author patrick on 7/31/20
// * @project sprintel-delivery
// */
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(entityManagerFactoryRef = "sqlServerEntityManagerFactory",
//        transactionManagerRef = "sqlServerTransactionManager",
//        basePackages = "com.swiggo.swiggodelivery.repositories.cscart")
//public class CsCartDbConfig {
//
//    @Bean
//    @ConfigurationProperties(prefix = "cscart.datasource")
//    public DataSourceProperties csCartDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    public DataSource csCartDataSource(@Qualifier("csCartDataSourceProperties") DataSourceProperties dataSourceProperties) {
//        return dataSourceProperties.initializeDataSourceBuilder().build();
//    }
//
//    @PersistenceContext(unitName = "cscart")
//    @Bean(name = "sqlServerEntityManagerFactory")
//    public LocalContainerEntityManagerFactoryBean sqlServerEntityManagerFactory(@Qualifier("csCartDataSource") DataSource sqlServerDataSource,
//                                                                                EntityManagerFactoryBuilder builder) {
//
//        return builder.dataSource(sqlServerDataSource)
//                .packages("com.swiggo.swiggodelivery.cscartEntities")
//                .persistenceUnit("cscart")
//                .build();
//
//    }
//
//    @Bean
//    public PlatformTransactionManager sqlServerTransactionManager(@Qualifier("sqlServerEntityManagerFactory")
//                                                                          EntityManagerFactory factory) {
//        return new JpaTransactionManager(factory);
//    }
//
//}
//
