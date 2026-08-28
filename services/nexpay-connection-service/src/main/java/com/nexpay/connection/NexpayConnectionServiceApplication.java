package com.nexpay.connection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nexpay")
public class NexpayConnectionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexpayConnectionServiceApplication.class, args);
	}

}
