package com.epam.esm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "com.epam.esm")
@EnableJpaRepositories("com.epam.esm")
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

    @Profile("dev")
    @Bean
    public DataSource dataSourceEmbedded() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:schema.sql")
                .addScript("classpath:test-data.sql")
                .build();
    }

    @Profile("prod")
    @Bean(name = "entityManagerFactory")
    @Autowired
    public LocalSessionFactoryBean entityManagerFactoryPostgres(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.epam.esm");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Profile("dev")
    @Bean(name = "entityManagerFactory")
    @Autowired
    public LocalSessionFactoryBean entityManagerFactoryEmbedded(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.epam.esm");
        return sessionFactory;
    }

    @Profile("prod")
    final Properties hibernateProperties() {
        final Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("org.hibernate.envers.audit_table_suffix", "_AUDIT_LOG");
        return properties;
    }

}
