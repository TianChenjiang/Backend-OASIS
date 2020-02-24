package com.rubiks.backendoasis;

import lombok.val;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BackendOasisApplicationTests {

	@Autowired
	private TodoRepository todoRepository;

	@Test
	void contextLoads() {
		val todo0 = todoRepository.findById(1L);

		assertThat(todo0).isNotEmpty();
	}

	@Autowired
	private RestHighLevelClient client;

	@Test
	void testClient() throws IOException {
		val getRequest = new GetRequest("se3", "0");
		val response = client.get(getRequest, RequestOptions.DEFAULT);
		assertThat(response.getSource().get("conferenceName")).isEqualTo("ASE");
	}
}
