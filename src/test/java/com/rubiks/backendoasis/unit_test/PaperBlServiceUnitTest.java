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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
            assertThat(pa.getPublicationYear(), lessThanOrEqualTo(endYear));
            assertThat(pa.getPublicationYear(), greaterThanOrEqualTo(startYear));
            assertThat(pa.toString().toLowerCase(), containsString("software"));
        }
    }

    @Test
    public void testAdvancedSearch() {
        PapersWithSize res = paperBlService.advancedSearch("ab", "ca", "ASE", "soft", 1, "2011", "2012");
        List<PaperEntity> paperEntities = res.getPapers();
        for (PaperEntity pa : paperEntities) {
            assertThat(pa.getPublicationYear(), lessThanOrEqualTo("2012"));
            assertThat(pa.getPublicationYear(), greaterThanOrEqualTo("2011"));
            assertThat(pa.getConferenceName(), containsString("ASE"));
            assertThat(pa.getKeywords().toString().toLowerCase(), containsString("soft"));
            assertThat(pa.getAuthor().toString().toLowerCase(), containsString("ca"));
        }
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
