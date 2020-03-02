package com.rubiks.backendoasis.unit_test;

import com.rubiks.backendoasis.blservice.PaperBlService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class PaperBlServiceUnitTest {
    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private PaperBlService paperBlService;

    @Before
    public void setUp() throws Exception {
        // 初始化测试用例类中由Mockito的注解标注的所有模拟对象
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBasicSearch() {
        String keyword = "";
        int page = 1;
        String startYear = "";
        String endYear  = "";

//        List<PaperEntity> res = paperBlService.basicSearch(keyword, page, startYear, endYear);
//        List<PaperEntity> emptyList = new ArrayList<>();
//        assertEquals(res, emptyList);
    }


}
