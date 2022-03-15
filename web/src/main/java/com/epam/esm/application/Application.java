package com.epam.esm.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {
		HibernateJpaAutoConfiguration.class
},scanBasePackages = "com.epam.esm.config")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
