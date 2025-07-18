package com.example.BillUp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BillUpApplication {
	public static void main(String[] args) {
		SpringApplication.run(BillUpApplication.class, args);
	}
}