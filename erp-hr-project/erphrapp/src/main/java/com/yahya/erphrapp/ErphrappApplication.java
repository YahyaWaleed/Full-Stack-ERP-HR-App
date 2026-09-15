package com.yahya.erphrapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ErphrappApplication {

	public static void main(String[] args) {
		System.out.println("DB_PASSWORD is: [" + System.getenv("DB_PASSWORD") + "]");
		SpringApplication.run(ErphrappApplication.class, args);
	}
}
