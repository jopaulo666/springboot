package com.joaopaulo.cursomc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.joaopaulo.model")
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

}
