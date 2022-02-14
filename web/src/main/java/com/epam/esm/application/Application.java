package com.epam.esm.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.epam.esm")
//@EnableJpaRepositories("com.epam.esm.impl")
public class Application {
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}

}
