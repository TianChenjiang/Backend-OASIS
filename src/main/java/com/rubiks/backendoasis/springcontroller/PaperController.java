package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.blservice.RankBlService;
import com.rubiks.backendoasis.blservice.SearchBlService;
import com.rubiks.backendoasis.esdocument.PaperDocument;
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

@RestController()
public class PaperController {
    @Autowired
    private PaperBlService paperBlService;
    @Autowired
    private RankBlService rankBlService;
    @Autowired
    private SearchBlService searchBlService;


    public PaperController(PaperBlService paperBlService, RankBlService rankBlService, SearchBlService searchBlService) {
        this.paperBlService = paperBlService;
        this.rankBlService = rankBlService;
        this.searchBlService = searchBlService;
    }

    @GetMapping("/test")
    public String test() {
        return "Success";
    }

    @GetMapping("/all")
    public List<PaperDocument> findAll() throws Exception {
        return searchBlService.findAll();
    }

    @GetMapping("/researcher/interest")
    @ApiOperation(value = "接口5 查看学者研究方向")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getResearcherInterest(@RequestParam (value = "authorId") String id) {
        return paperBlService.getResearcherInterest(id);
    }

    @GetMapping("/affiliation/interest")
    public BasicResponse getAffiliationInterest(@RequestParam (value = "affiliation") String affiliation) {
        return paperBlService.getAffiliationInterest(affiliation);
    }

    @GetMapping("/paper/abstract")
    @ApiOperation(value = "接口6 查看活跃论文摘要")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getActivePaperAbstract() {
        return paperBlService.getActivePaperAbstract();
    }

    @GetMapping("/paper/reference")
    @ApiOperation(value = "接口7 根据paper的id返回reference")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getReferenceById(@RequestParam (value = "paperId") String paperId) {
        return paperBlService.getReferenceById(paperId);
    }

    @GetMapping("/paper/author")
    public BasicResponse getAuthorPapersById(@RequestParam(value = "authorId") String authorId,
                                            @RequestParam(value = "page") int page,
                                            @RequestParam(value = "sortKey") String sortKey) {
        return paperBlService.getAuthorPapersById(authorId, page, sortKey);
    }

    @GetMapping("/paper/affiliation")
    public BasicResponse getAffiliationPapers(@RequestParam(value = "affiliation") String affiliation,
                                              @RequestParam(value = "page") int page,
                                              @RequestParam(value = "sortKey") String sortKey) {
        return paperBlService.getAffiliationPapers(affiliation, page, sortKey);
    }

    @GetMapping("/paper/keyword")
    public BasicResponse getKeywordPapers(@RequestParam(value = "keyword") String keyword,
                                          @RequestParam(value = "page") int page,
                                          @RequestParam(value = "sortKey") String sortKey) {
        return paperBlService.getKeywordPapers(keyword, page, sortKey);
    }

}
