package com.rubiks.backendoasis.unit_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.bl.AdminBlServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class PortraitBlServiceImplTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void getAuthorPortraitById() throws Exception{
    }

    @Test
    void getAffiliationPortrait() throws Exception{
    }

    @Test
    void getKeywordPortrait() throws Exception{
    }

    @Test
    void getConferencePortrait() throws Exception{
    }

    @Test
    void getJournalPortrait() throws Exception{
    }
}