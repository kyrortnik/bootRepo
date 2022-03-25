package com.epam.esm.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.env.AbstractEnvironment;


@SpringBootApplication(exclude = {
		HibernateJpaAutoConfiguration.class
},scanBasePackages = {"com.epam.esm"})
public class Application {
	public static void main(String[] args) {

		System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "dev");

		SpringApplication.run(Application.class, args);
	}

}
