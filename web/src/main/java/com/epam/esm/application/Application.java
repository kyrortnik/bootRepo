package com.epam.esm.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
		/*DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,*/
		HibernateJpaAutoConfiguration.class
},scanBasePackages = "com.epam.esm.config")
//@EnableJpaRepositories("com.epam.esm.impl")
public class Application {
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

}
