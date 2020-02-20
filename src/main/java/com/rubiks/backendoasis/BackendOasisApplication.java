package com.rubiks.backendoasis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendOasisApplication {
	@Bean
	public CommandLineRunner initDevDB(TodoRepository todoRepository) {
		return args -> {
			TodoEntity todo1 = new TodoEntity("buy fruits", false);
			TodoEntity todo2 = new TodoEntity("sell milk", true);

			todoRepository.save(todo1);
			todoRepository.save(todo2);

		};
	}
	public static void main(String[] args) {
		SpringApplication.run(BackendOasisApplication.class, args);
	}

}
