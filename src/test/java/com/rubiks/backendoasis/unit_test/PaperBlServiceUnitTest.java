package com.rubiks.backendoasis.unit_test;

import com.rubiks.backendoasis.blservice.AdminBlService;
import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.blservice.RankBlService;
import com.rubiks.backendoasis.blservice.SearchBlService;
import com.rubiks.backendoasis.model.paper.BriefPaper;
import com.rubiks.backendoasis.model.paper.PaperWithoutRef;
import com.rubiks.backendoasis.model.paper.PapersWithSize;
import com.rubiks.backendoasis.model.paper.ResearchInterest;
import com.rubiks.backendoasis.model.rank.BasicRank;
import com.rubiks.backendoasis.model.rank.AuthorRank;
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
    @Autowired
    private RankBlService rankBlService;
    @Autowired
    private SearchBlService searchBlService;
    @Autowired
    private AdminBlService adminBlService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        adminBlService.updateMainPageCache();
    }


    @Test
    public void testBasicSearch() throws Exception {
        String keyword = "software、";
        int page = 1;
        PapersWithSize res = (PapersWithSize) searchBlService.basicSearchByES(keyword, page, "related").getData();
        List<PaperWithoutRef> paperEntities = res.getPapers();
        for (PaperWithoutRef pa : paperEntities) {
//            assertThat(pa.getPublicationYear(), lessThanOrEqualTo(endYear));
//            assertThat(pa.getPublicationYear(), greaterThanOrEqualTo(startYear));
            assertThat(pa.toString().toLowerCase(), containsString("software"));
        }
    }


    @Test
    public void testAdvancedSearch() throws Exception {
        PapersWithSize res = (PapersWithSize) searchBlService.advancedSearchByES("software", "jia liu", "nanjing university", "", "",  2011, 2012, 1, "related").getData();
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
        List<BasicRank> res = (List<BasicRank>)rankBlService.getAffiliationBasicRanking("citationCount", 2019).getData();
        BasicRank former, latter;
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


}
