package com.rubiks.backendoasis;

import com.rubiks.backendoasis.util.converter.AuthorReadConvertor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@SpringBootApplication
public class BackendOasisApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendOasisApplication.class, args);
	}

//	@Bean
//	public MongoCustomConversions mongoCustomConversions() {
//		return new MongoCustomConversions(Arrays.asList(new AuthorReadConvertor()));
//	}

}
