package com.epam.esm.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.epam.esm",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)
        })
public class PersistenceConfig {

    @Profile("prod")
    @Bean
    public DataSource dataSourcePostgres() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://localhost/epam-lab");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("admin");
        return dataSourceBuilder.build();
    }

    //TODO -- add testdata script
    @Profile("dev")
    @Bean
    public DataSource embeddedDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
//                .addScript("classpath:test-data.sql")
                .build();
    }

    @Profile("prod")
    @Bean
    @Autowired
    public LocalSessionFactoryBean sessionFactoryHibernate(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.epam.esm");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Profile("dev")
    @Bean
    public LocalSessionFactoryBean sessionFactoryH2() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(embeddedDataSource());
        sessionFactory.setPackagesToScan("com.epam.esm");
        return sessionFactory;
    }

    @Profile("prod")
    final Properties hibernateProperties() {
        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("org.hibernate.envers.audit_table_suffix", "_AUDIT_LOG");
        return properties;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManagerHibernate(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

}
