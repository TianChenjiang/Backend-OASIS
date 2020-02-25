package com.rubiks.backendoasis;

import lombok.val;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
class BackendOasisApplicationTests {

//	@Autowired
//	private TodoRepository todoRepository;
//
//	@Test
//	void contextLoads() {
//		val todo0 = todoRepository.findById(1L);
//
//		assertThat(todo0).isNotEmpty();
//	}
//
//	@Autowired
//	private RestHighLevelClient client;
//
//	@Test
//	void testClient() throws IOException {
//		val getRequest = new GetRequest("se3", "papers", "JTaTcXABB1_gxo8pq0F9");
//		val response = client.get(getRequest, RequestOptions.DEFAULT);
//	}
    @Test
    void todoTest() {
        int res = 1 + 1;
        assertThat(res).isEqualTo(2);
    }
}
