package com.rubiks.backendoasis.integration_test;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaperControllerIntegrationTest {
    private MockMvc mockMvc;
    // 集成测试 会直接用到service组件，不需要mock
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void clean() {

    }

    @Test
    public void testBasicSearch() throws Exception {
        mockMvc.perform(get("/search/basic/es")
                .param("keyword", "Software、")
                .param("page", "1")
                .param("sortKey", "related")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Software")))

        ;
        mockMvc.perform(get("/search/basic/es")
                .param("keyword", "\"Architecture、.\"")
                .param("page", "1")
                .param("sortKey", "related")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Architecture".toLowerCase())));

    }


    @Test
    public void testAdvancedSearch() throws Exception {
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
    public void testAffiliationBasicRanking() throws Exception {
        mockMvc.perform(get("/rank/basic/affiliation")
                .param("sortKey", "acceptanceCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testAuthorBasicRanking() throws Exception {
        mockMvc.perform(get("/rank/basic/author")
                .param("sortKey", "citationCount")
                .param("year", "2019")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void testGetResearcherInterest() throws Exception {
        mockMvc.perform(get("/researcher/interest")
                .param("authorId", "37302908800")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print()
        );

    }

    @Test
    public void testGetActivePaperAbstract() throws Exception {
        mockMvc.perform(get("/paper/abstract")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.data[0].title", is("Software Architecture"));
    }

}
