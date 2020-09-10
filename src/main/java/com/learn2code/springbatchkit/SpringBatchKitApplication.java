package com.learn2code.springbatchkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:database.xml")
public class SpringBatchKitApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchKitApplication.class, args);
	}

}
