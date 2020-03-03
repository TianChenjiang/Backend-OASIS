package com.rubiks.backendoasis.unit_test;

import com.rubiks.backendoasis.blservice.PaperBlService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class PaperBlServiceUnitTest {
    @MockBean
    private MongoTemplate mongoTemplate;

    @Autowired
    private PaperBlService paperBlService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {

    }
    @Test
    public void testBasicSearch() {
        String keyword = "";
        int page = 1;
        String startYear = "";
        String endYear  = "";
    }


}
