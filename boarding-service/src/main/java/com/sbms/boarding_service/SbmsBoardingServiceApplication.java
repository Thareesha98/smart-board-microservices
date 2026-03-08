package com.sbms.boarding_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SbmsBoardingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbmsBoardingServiceApplication.class, args);
	}

}
