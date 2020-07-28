package com.rubiks.backendoasis.unit_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.bl.AdminBlServiceImpl;
import com.rubiks.backendoasis.blservice.AdminBlService;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.springcontroller.AdminController;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AdminBlServiceImplTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void getAffiliationInfo() throws Exception {
        mockMvc.perform(get("/info/affiliation")
                .param("name", "Tsinghua")
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.affiliations[0].name", containsString("Tsinghua")));
    }

    @Test
    void getAffiliationInfoWithEmptyName() throws Exception {
        mockMvc.perform(get("/info/affiliation")
                .param("name", "")
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size", Matchers.greaterThanOrEqualTo(100)));
    }

    @Test
    void getAuthorInfo() throws Exception{
        mockMvc.perform(get("/info/author")
                .param("name", "Liu Jia")
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.authors[0].authorName", containsString("Liu Jia")));
    }

    @Test
    void getAuthorInfoWithEmptyName() throws Exception {
        mockMvc.perform(get("/info/author")
                .param("name", "")
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size", Matchers.greaterThanOrEqualTo(100)));
    }

    @Test
    void getKeywordInfo() throws Exception{
        String keyword = "machine";
        mockMvc.perform(get("/info/keyword")
                .param("name", keyword)
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.keywords[0].name", containsString(keyword)));
    }

    @Test
    void getRecommendedSimilarAffiliation() throws Exception{
        mockMvc.perform(get("/recommend/author")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()));
    }

    @Test
    void getRecommendedSimilarAuthor() throws Exception{
        mockMvc.perform(get("/recommend/affiliation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", Matchers.notNullValue()));
    }
}