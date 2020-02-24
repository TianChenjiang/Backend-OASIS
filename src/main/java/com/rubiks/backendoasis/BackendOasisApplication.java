package com.rubiks.backendoasis;

import lombok.val;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendOasisApplication {
	@Autowired
	private RestHighLevelClient client;
	@Bean
	public CommandLineRunner initDevDB(TodoRepository todoRepository) {
		return args -> {
			TodoEntity todo1 = new TodoEntity("buy fruits", false);
			TodoEntity todo2 = new TodoEntity("sell milk", true);

			todoRepository.save(todo1);
			todoRepository.save(todo2);

			val getRequest = new GetRequest("se3", "0");
			val response = client.get(getRequest, RequestOptions.DEFAULT);
			System.out.println(response);
		};
	}
	public static void main(String[] args) {
		SpringApplication.run(BackendOasisApplication.class, args);
	}

}
