package com.wtfcinema.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
@EnableJpaRepositories(basePackages = "com.wtfcinema.demo.repository")
@EntityScan(basePackages = "com.wtfcinema.demo.entities")
public class DemoApplication {

	public static void main(String[] args){
		SpringApplication.run(DemoApplication.class, args);

	}
}	
