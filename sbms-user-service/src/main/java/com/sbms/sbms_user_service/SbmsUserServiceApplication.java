package com.sbms.sbms_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.sbms.sbms_user_service")
@EnableJpaRepositories(basePackages = "com.sbms.sbms_user_service")
public class SbmsUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbmsUserServiceApplication.class, args);
	}

}
