package com.rubiks.backendoasis.integrated_test;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.MetricsEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.model.AffiliationRank;
import com.rubiks.backendoasis.springcontroller.PaperController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class PaperBlControllerTest {
    @Autowired
    private PaperController paperController;

    @Autowired
    protected WebApplicationContext wac;

    @MockBean
    PaperBlService paperBlService;

    protected MockMvc mockMvc;

    private List<PaperEntity> paperEntities;
    private List<AffiliationRank> affiliationRanks;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PaperController(paperBlService)).build();

        // 创造作者和
        AuthorEntity authorEntity1 = AuthorEntity.builder().name("lq").affiliation("NJU").build();
        AuthorEntity authorEntity2 = AuthorEntity.builder().name("mxp").affiliation("NJU gulou").build();
        List<AuthorEntity> l1 = new ArrayList<>(); l1.add(authorEntity1);
        List<AuthorEntity> l2 = new ArrayList<>(); l2.add(authorEntity2);

        MetricsEntity metricsEntity1 = MetricsEntity.builder().citationCountPatent(100).citationCountPaper(200).build();
        MetricsEntity metricsEntity2 = MetricsEntity.builder().citationCountPatent(200).citationCountPaper(50).build();

        PaperEntity paperEntity1 = PaperEntity.builder().title("Software Architecture").publicationYear("2011").conferenceName("ASE").author(l1).metrics(metricsEntity1).build();
        PaperEntity paperEntity2 = PaperEntity.builder().title("Software Design").publicationYear("2011").conferenceName("IEEE").author(l2).metrics(metricsEntity2).build();
        paperEntities = new ArrayList<>();
        paperEntities.add(paperEntity1);
        paperEntities.add(paperEntity2);
    }

    @Test
    public void testWeb() throws Exception{
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/test");
        MvcResult result =  mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Success"))
                .andReturn();
    }

    @Test
    public void testBasicSearch() throws Exception {
        when(paperBlService.basicSearch(any(String.class), any(Integer.class), any(String.class), any(String.class))).thenReturn(paperEntities);
        mockMvc.perform(get("/search/basic/mongo")
                .param("keyword", "Software").param("page", "1").param("startYear", "2002").param("endYear", "2015")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title", is("Software Architecture")))
                .andExpect(jsonPath("$.data[1].title", is("Software Design")));
    }

    @Test
    public void testAdvancedSearch() throws Exception {
        when(paperBlService.advancedSearch(any(String.class), any(String.class), any(String.class), any(String.class), any(Integer.class), any(String.class), any(String.class)))
                .thenReturn(paperEntities);
        mockMvc.perform(get("/search/advanced/mongo")
                .param("conferenceName", "ASE")
                .param("startYear","2011")
                .param("endYear", "2011")
                .param("page", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title", is("Software Architecture"))
        );
    }

    @Test
    public void testAffiliationBasicRanking() throws Exception {
        when(paperBlService.getAffiliationBasicRanking(any(String.class), any(String.class)))
                .thenReturn(affiliationRanks);
        //"acceptanceCount"|"citationCount"
        mockMvc.perform(get("/rank/basic/affiliation")
                .param("sortKey", "acceptanceCount")
                .param("year", "2011")
                .contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]", is("NJU")))
                .andExpect(jsonPath("$.data[0].count", is(200))
        );
    }
}
