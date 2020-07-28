package com.rubiks.backendoasis.unit_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.bl.AdminBlServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
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

import java.util.regex.Matcher;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RankBlServiceImplTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getAffiliationBasicRanking() throws Exception{
        mockMvc.perform(get("/rank/basic/affiliation")
                .param("sortKey", "acceptanceCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void getAuthorBasicRanking() throws Exception{
        mockMvc.perform(get("/rank/basic/author")
                .param("sortKey", "citationCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getJournalBasicRanking() throws Exception{
        mockMvc.perform(get("/rank/basic/journal")
                .param("sortKey", "citationCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getConferenceBasicRanking() throws Exception{
        mockMvc.perform(get("/rank/basic/conference")
                .param("sortKey", "citationCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getKeywordBasicRanking() throws Exception{
        mockMvc.perform(get("/rank/basic/keyword")
                .param("sortKey", "citationCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void getAuthorAdvancedRanking() throws Exception{
        mockMvc.perform(get("/rank/advanced/author")
                .param("sortKey", "citationCount")
                .param("startYear","2010")
                .param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAuthorDetailRankingById() throws Exception{
        mockMvc.perform(get("/rank/detail/author")
                .param("authorId", "37268675200")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()));

        mockMvc.perform(get("/rank/detail/author")
                .param("authorId", "fdsjaojfdiaosj")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("no such author")));
    }

    @Test
    public void getKeywordDetailRankingById() throws Exception{
        mockMvc.perform(get("/rank/detail/keyword")
                .param("keyword", "optimisation")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()));

        mockMvc.perform(get("/rank/detail/keyword")
                .param("keyword", "fdsjaojfdiaosj")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("no such keyword")));
    }

    @Test
    public void getAuthorDetailRankingByKeyword() throws Exception{
        mockMvc.perform(get("/rank/detail/author/keyword")
                .param("keyword", "optimisation")
                .param("sortKey", "citationCount")
                .param("startYear","2010")
                .param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()));

        mockMvc.perform(get("/rank/detail/author/keyword")
                .param("keyword", "fdsjaojfdiaosj")
                .param("sortKey", "citationCount")
                .param("startYear","2010")
                .param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("no such keyword")));
    }

    @Test
    public void getAffiliationDetailRankingByKeyword() throws Exception{
        mockMvc.perform(get("/rank/detail/affiliation/keyword")
                .param("keyword", "optimisation")
                .param("sortKey", "citationCount")
                .param("startYear","2010")
                .param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()));

        mockMvc.perform(get("/rank/detail/affiliation/keyword")
                .param("keyword", "fdsjaojfdiaosj")
                .param("sortKey", "citationCount")
                .param("startYear","2010")
                .param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("no such keyword")));
    }

    @Test
    public void getAffiliationAdvancedRanking() throws Exception{
        mockMvc.perform(get("/rank/advanced/affiliation")
                .param("sortKey", "citationCount")
                .param("startYear","2010")
                .param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getKeywordAdvancedRanking() throws Exception{
        mockMvc.perform(get("/rank/advanced/keyword")
                .param("sortKey", "citationCount")
                .param("startYear","2010")
                .param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAffiliationDetailRankingById() throws Exception{
        mockMvc.perform(get("/rank/detail/affiliation")
                .param("affiliationId", "Google")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()));

        mockMvc.perform(get("/rank/detail/affiliation")
                .param("affiliationId", "fdsjaojfdiaosj")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("no such affiliation")));
    }

    @Test
    public void getAuthorDetailRanking() throws Exception{
        mockMvc.perform(get("/rank//affiliation/author")
                .param("affiliation", "Google")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()));

        mockMvc.perform(get("/rank/affiliation/author")
                .param("affiliation", "fdsjaojfdiaosj")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("no such affiliation")));
    }
}