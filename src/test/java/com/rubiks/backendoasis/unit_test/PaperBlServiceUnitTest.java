package com.rubiks.backendoasis.unit_test;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.blservice.RankBlService;
import com.rubiks.backendoasis.blservice.SearchBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PaperBlServiceUnitTest {
    @Autowired
    private PaperBlService paperBlService;
    @Autowired
    private RankBlService rankBlService;
    @Autowired
    private SearchBlService searchBlService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testBasicSearch() {
        String keyword = "software、";
        int page = 1;
        int startYear = 2011;
        int endYear  = 2014;
        PapersWithSize res = (PapersWithSize) searchBlService.basicSearch(keyword, page, startYear, endYear, "related").getData();
        List<PaperWithoutRef> paperEntities = res.getPapers();
        for (PaperWithoutRef pa : paperEntities) {
            assertThat(pa.getPublicationYear(), lessThanOrEqualTo(endYear));
            assertThat(pa.getPublicationYear(), greaterThanOrEqualTo(startYear));
            assertThat(pa.toString().toLowerCase(), containsString("software"));
        }
    }

    @Test
    public void testSearchDateError() {

    }

    @Test
    public void testAdvancedSearch() {
        PapersWithSize res = (PapersWithSize) searchBlService.advancedSearch("ab", "ca", "ASE", "soft", 1, 2011, 2012).getData();
        List<PaperWithoutRef> paperEntities = res.getPapers();
        for (PaperWithoutRef pa : paperEntities) {
            assertThat(pa.getPublicationYear(), lessThanOrEqualTo(2012));
            assertThat(pa.getPublicationYear(), greaterThanOrEqualTo(2011));
            assertThat(pa.getPublicationName(), containsString("ASE"));
            assertThat(pa.getKeywords().toString().toLowerCase(), containsString("soft"));
        }
    }

    @Test
    public void testGetAffiliationBasicRanking() {
        List<AffiliationRank> res = (List<AffiliationRank>)rankBlService.getAffiliationBasicRanking("citationCount", 2011).getData();
        AffiliationRank former, latter;
        for (int i = 0; i < res.size() - 1; i++) {
            former = res.get(i);
            latter = res.get(i+1);
            assertThat(former.getCount(), greaterThanOrEqualTo(latter.getCount()));
        }
    }

    @Test
    public void testGetAuthorBasicRanking() {
        List<AuthorRank> res = (List<AuthorRank>) rankBlService.getAuthorBasicRanking("acceptanceCount", 2011).getData();
        AuthorRank former, latter;
        for (int i = 0; i < res.size() - 1; i++) {
            former = res.get(i);
            latter = res.get(i+1);
            assertThat(former.getCount(), greaterThanOrEqualTo(latter.getCount()));
        }

    }

    @Test
    public void testGetResearcherInterest() {
        List<ResearchInterest> res = (List<ResearchInterest>) paperBlService.getResearcherInterest("37267738200").getData();
        assertThat(res, notNullValue());
    }

    @Test
    public void testGetActivePaperAbstract() {
        List<BriefPaper> res = (List<BriefPaper>) paperBlService.getActivePaperAbstract().getData();
        BriefPaper former, latter;
        for (int i = 0; i < res.size() - 1; i++) {
            former = res.get(i);
            latter = res.get(i+1);
//            assertThat(former.getMetrics().getCitationCountPaper(), greaterThanOrEqualTo(latter.getMetrics().getCitationCountPaper()));
        }
    }

}
