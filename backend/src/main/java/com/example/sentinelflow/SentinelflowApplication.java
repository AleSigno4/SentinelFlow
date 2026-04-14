package com.example.sentinelflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SentinelflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(SentinelflowApplication.class, args);
	}

}
