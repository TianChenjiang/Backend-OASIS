package com.rubiks.backendoasis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching(proxyTargetClass = true)
public class BackendOasisApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendOasisApplication.class, args);
	}
}
