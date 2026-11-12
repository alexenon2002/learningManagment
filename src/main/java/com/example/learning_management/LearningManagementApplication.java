package com.example.learning_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LearningManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningManagementApplication.class, args);
	}

}
