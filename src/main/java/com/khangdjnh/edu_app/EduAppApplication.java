package com.khangdjnh.edu_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EduAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduAppApplication.class, args);
	}

}
