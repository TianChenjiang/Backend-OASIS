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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
class RankBlServiceImplTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getAffiliationBasicRanking() throws Exception{
        mockMvc.perform(get("/rank/basic/affiliation")
                .param("sortKey", "acceptanceCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void getAuthorBasicRanking() throws Exception{
        mockMvc.perform(get("/rank/basic/author")
                .param("sortKey", "citationCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
    }

    @Test
    void getJournalBasicRanking() throws Exception{
    }

    @Test
    void getConferenceBasicRanking() throws Exception{
    }

    @Test
    void getKeywordBasicRanking() throws Exception{
    }

    @Test
    void getAuthorAdvancedRanking() throws Exception{
    }

    @Test
    void getAuthorDetailRankingById() throws Exception{
    }

    @Test
    void getKeywordDetailRankingById() throws Exception{
    }

    @Test
    void getAuthorDetailRankingByKeyword() throws Exception{
    }

    @Test
    void getAffiliationDetailRankingByKeyword() throws Exception{
    }

    @Test
    void getAffiliationAdvancedRanking() throws Exception{
    }

    @Test
    void getKeywordAdvancedRanking() throws Exception{
    }

    @Test
    void getAffiliationDetailRankingById() throws Exception{
    }

    @Test
    void getAuthorDetailRanking() throws Exception{
    }
}