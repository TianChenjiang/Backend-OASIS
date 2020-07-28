package com.rubiks.backendoasis.unit_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.bl.AdminBlServiceImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.hamcrest.Matchers;
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

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SearchBlServiceImplTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    void basicSearchByESWithHighLight() throws Exception{
        mockMvc.perform(get("/search/basic/es")
                .param("keyword", "Software、")
                .param("page", "1")
                .param("sortKey", "related")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Software")));

        mockMvc.perform(get("/search/basic/es")
                .param("keyword", "\"Architecture、.\"")
                .param("page", "1")
                .param("sortKey", "related")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Architecture".toLowerCase())));
    }


    @Test
    void advancedSearchByESWithHighLight() throws Exception{
        mockMvc.perform(get("/search/advanced/es")
                .param("publicationName", "ASE")
                .param("field", "software")
                .param("startYear","2010")
                .param("endYear", "2015")
                .param("page", "1")
                .param("sortKey", "related")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.papers[0].publicationName", containsString("ASE")))
                .andExpect(jsonPath("$.data.papers[0].keywords", (hasItem(containsString("software")))))
                .andExpect(jsonPath("$.data.papers[0].publicationYear", greaterThanOrEqualTo(2010)))
                .andExpect(jsonPath("$.data.papers[0].publicationYear", lessThanOrEqualTo(2015)))
        ;
    }

    @Test
    void basicFilterSearch() throws Exception{
        mockMvc.perform(get("/search/filter/es")
                .param("keyword", "software")
                .param("page", "1")
                .param("sortKey", "related")
                .param("affiliation", "nanjing university")
                .param("startYear","2010")
                .param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Software")));
    }


    @Test
    void commandSearch() throws Exception{
        mockMvc.perform(get("/search/command")
                .param("query", "software AND test NOT nanjing")
                .param("page", "1")
                .param("sortKey", "related")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("software")))
                .andExpect(content().string(containsString("test")));
    }

}