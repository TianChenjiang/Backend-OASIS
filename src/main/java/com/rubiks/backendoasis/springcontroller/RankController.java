package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.blservice.RankBlService;
import com.rubiks.backendoasis.model.ResearchInterest;
import com.rubiks.backendoasis.model.rank.AuthorRankDetail;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.response.SuccessResponse;
import com.rubiks.backendoasis.response.WrongResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RankController {
    @Autowired
    private RankBlService rankBlService;
    private PaperBlService paperBlService;

    public RankController(RankBlService rankBlService, PaperBlService paperBlService) {
        this.rankBlService = rankBlService;
        this.paperBlService = paperBlService;
    }

    @GetMapping("/rank/basic/affiliation")
    @ApiOperation(value = "接口3 查看组织简略排名", notes = "根据topic和sortkey查看排名")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getAffiliationBasicRanking(@RequestParam(value = "sortKey") String sortKey,
                                                    @RequestParam(value = "year") int year) {
        return  rankBlService.getAffiliationBasicRanking(sortKey, year);
    }

    @GetMapping("/rank/basic/author")
    @ApiOperation(value = "接口4 查看作者简略排名", notes = "根据topic和sortkey查看排名")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getAuthorBasicRanking(@RequestParam(value = "sortKey") String sortKey,
                                               @RequestParam(value = "year") int year) {
        return rankBlService.getAuthorBasicRanking(sortKey, year);
    }

    @GetMapping("/rank/advanced/author")
    public BasicResponse getAuthorAdvancedRanking(@RequestParam(value = "sortKey") String sortKey,
                                                  @RequestParam(value = "startYear") int startYear,
                                                  @RequestParam(value = "endYear") int endYear) {
        return rankBlService.getAuthorAdvancedRanking(sortKey, startYear, endYear);
    }

    @GetMapping("rank/detail/author")
    public BasicResponse getAuthorDetailRankingById(@RequestParam(value = "authorId") String authorId) {
        List<ResearchInterest> keywords = (List<ResearchInterest>) paperBlService.getResearcherInterest(authorId).getData(); //调用paperbl中的接口
        AuthorRankDetail authorRankDetail =  rankBlService.getAuthorDetailRankingById(authorId);
        authorRankDetail.setKeywords(keywords);
        return new BasicResponse(200, "Success", authorRankDetail);
    }

    @GetMapping("/rank/advanced/affiliation")
    public BasicResponse getAffiliationAdvancedRanking(@RequestParam(value = "sortKey") String sortKey,
                                                       @RequestParam(value = "startYear") int startYear,
                                                       @RequestParam(value = "endYear") int endYear) {
        return rankBlService.getAffiliationAdvancedRanking(sortKey, startYear, endYear);
    }


    @GetMapping("/rank/detail/affiliation")
    public BasicResponse getAffiliationDetailRankingById(@RequestParam(value = "affiliationId") String affiliationId) {
        return rankBlService.getAffiliationDetailRankingById(affiliationId);
    }
}