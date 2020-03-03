package com.rubiks.backendoasis.unit_test;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.model.PapersWithSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PaperBlServiceUnitTest {
    @Autowired
    private PaperBlService paperBlService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testBasicSearch() {
        String keyword = "software、";
        int page = 1;
        String startYear = "2011";
        String endYear  = "2014";
        PapersWithSize res = paperBlService.basicSearch(keyword, page, startYear, endYear);
        List<PaperEntity> paperEntities = res.getPapers();
        for (PaperEntity pa : paperEntities) {
            assertThat(pa.getPublicationYear()).isBetween(startYear, endYear);
            assertThat(pa.toString().contains("software"));
        }
    }

    @Test
    public void testAdvancedSearch() {
        PapersWithSize res = paperBlService.advancedSearch("ab", "ca", "ASE", "x", 1, "2011", "2012");
        List<PaperEntity> paperEntities = res.getPapers();
        assertThat(paperEntities.get(0).getConferenceName().contains("ADDIDID"));
//        for (PaperEntity pa : paperEntities) {
//            assertThat(pa.getPublicationYear()).isBetween("2011", "2012");
//            assertThat(pa.getConferenceName().contains("ADDIDID"));
//            assertThat(pa.getKeywords().contains("ASE"));
//            List<AuthorEntity> authorEntities = pa.getAuthor();
//            for (AuthorEntity author : authorEntities) {
//                assertThat(author.getName().contains("ab"));
//                assertThat(author.getAffiliation().contains("ca"));
//            }
//        }
    }

    @Test
    public void testGetAffiliationBasicRanking() {

    }

    @Test
    public void testGetAuthorBasicRanking() {

    }

    @Test
    public void testGetResearcherInterest() {

    }

    @Test
    public void testGetActivePaperAbstract() {

    }



}
