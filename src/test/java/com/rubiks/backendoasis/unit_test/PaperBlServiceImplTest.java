package com.rubiks.backendoasis.unit_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
class PaperBlServiceImplTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getResearcherInterest() throws Exception{
        mockMvc.perform(get("/researcher/interest")
                .param("authorId", "37302908800")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print()
                );

        mockMvc.perform(get("/researcher/interest")
                .param("authorId", "-13")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such author")));
    }

    @Test
    void getAffiliationInterest() throws Exception{
        mockMvc.perform(get("/affiliation/interest")
                .param("affiliation", "Nanjing University")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print()
                );

        mockMvc.perform(get("/affiliation/interest")
                .param("affiliation", "jfiojiaji")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such affiliation")));
    }

    @Test
    void getConferenceInterest() throws Exception{
        mockMvc.perform(get("/conference/interest")
                .param("conference", "EMBC")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print()
                );

        mockMvc.perform(get("/conference/interest")
                .param("conference", "fsdajofdiodsj")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such conference")));
    }

    @Test
    void getJournalInterest() throws Exception{
        mockMvc.perform(get("/journal/interest")
                .param("journal", "ACCESS")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print()
                );

        mockMvc.perform(get("/journal/interest")
                .param("journal", "jfiojiaji")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such journal")));
    }

    @Test
    void getActivePaperAbstract()throws Exception {
        mockMvc.perform(get("/paper/abstract")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(is(5)))); //返回五篇论文
    }

    @Test
    void getKeyword3DTrend() throws Exception{
        mockMvc.perform(get("/keyword/trend")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getReferenceById() throws Exception{
        mockMvc.perform(get("/journal/interest")
                .param("journal", "jfiojiaji")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such journal")));
    }

    @Test
    void getAuthorPapersById() throws Exception{
        mockMvc.perform(get("/paper/author")
                .param("authorId", "37296968900")
                .param("page", "1")
                .param("sortKey", "recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/paper/author")
                .param("authorId", "1")
                .param("page", "1")
                .param("sortKey", "recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such author")));
    }

    @Test
    void getAffiliationPapers()throws Exception {
        mockMvc.perform(get("/paper/affiliation")
                .param("affiliation", "Nanjing University")
                .param("page", "1")
                .param("sortKey", "recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/paper/affiliation")
                .param("affiliation", "fdsafdaji")
                .param("page", "1")
                .param("sortKey", "recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such affiliation")));
    }

    @Test
    void getKeywordPapers() throws Exception{
        mockMvc.perform(get("/paper/keyword")
                .param("keyword", "software")
                .param("page", "1")
                .param("sortKey", "recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/paper/keyword")
                .param("keyword", "fdsafdaji")
                .param("page", "1")
                .param("sortKey", "recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such keyword")));
    }

    @Test
    void getPaperByErrorId() throws Exception{
        mockMvc.perform(get("/paper/id")
                .param("id", "fdsajojfwe")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("No such paper")));
    }
}