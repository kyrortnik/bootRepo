package com.epam.esm.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.epam.esm",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = EnableWebMvc.class)
        })
@EnableTransactionManagement
public class RootConfig {

//    @Profile("prod")
//    @Bean
//    public DataSource dataSource() {
//        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName();
//        ds.setUrl();
//        ds.setUsername();
//        ds.setPassword("admin");
//        ds.setInitialSize(5);
//        ds.setMaxActive(10);
//        return ds;
//    }

//    @Profile("prod")
    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://localhost/epam-lab");
        dataSourceBuilder.username("postgres");
        dataSourceBuilder.password("admin");
        return dataSourceBuilder.build();
    }


//    @Profile("dev")
//    @Bean
//    public DataSource embeddedDataSource() {
//        return new EmbeddedDatabaseBuilder()
//        .setType(EmbeddedDatabaseType.H2)
//        .addScript("classpath:schema.sql")
//        .addScript("classpath:test-data.sql")
//        .build();
//    }

//Entity manager factory
    @Bean
    public LocalSessionFactoryBean sessionFactory(){
        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.epam.esm");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }
// Transactions manager
    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    final Properties hibernateProperties(){
        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return properties;
    }
//    @Bean
//    public NamedParameterJdbcTemplate namedParameterjdbcTemplate(DataSource dataSource) {
//        return new NamedParameterJdbcTemplate(dataSource);
//    }
//
//    @Bean
//    @Scope("prototype")
//    public SimpleJdbcInsert simpleJdbcInsert(DataSource dataSource){
//        return new SimpleJdbcInsert(dataSource);
//    }
}
