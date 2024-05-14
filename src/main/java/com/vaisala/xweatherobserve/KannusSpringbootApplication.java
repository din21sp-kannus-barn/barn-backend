package com.vaisala.xweatherobserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KannusSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(KannusSpringbootApplication.class, args);
	}

}
