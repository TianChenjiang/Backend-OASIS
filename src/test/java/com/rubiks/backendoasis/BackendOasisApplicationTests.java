package com.rubiks.backendoasis;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
