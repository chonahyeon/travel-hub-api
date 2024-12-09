//package com.travelhub.travelhub_api.common.configuration.data;
//
//import jakarta.persistence.EntityManagerFactory;
//
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableTransactionManagement(proxyTargetClass = true)
//@EnableAutoConfiguration
//@EntityScan(basePackages = "com.travelhub.travelhub_api.data.mysql.entity")
//@EnableJpaRepositories(value = "com.travelhub.travelhub_api.data.mysql.repository", entityManagerFactoryRef = "travelJpaSqlSessionFactory", transactionManagerRef = "travelTransactionManager")
//public class TravelDBConfiguration {
//
//    @Bean(name = "travelDataSource")
//    @ConfigurationProperties(prefix = "spring.datasource.travel-hub")
//    public DataSource travelDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    /******************************* JPA *******************************/
//    @Bean(name = "travelJpaSqlSessionFactory")
//    public EntityManagerFactory travelJpaSqlSessionFactory(DataSource dataSource) {
//        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan("com.travelhub.travelhub_api.data.mysql.entity");
//
//        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//        em.afterPropertiesSet();
//
//        return em.getObject();
//    }
//
//    @Bean(name = "travelTransactionManager")
//    public PlatformTransactionManager travelTransactionManager(EntityManagerFactory entityManagerFactory) {
//        JpaTransactionManager tm = new JpaTransactionManager();
//        tm.setEntityManagerFactory(entityManagerFactory);
//        return tm;
//    }
//}
