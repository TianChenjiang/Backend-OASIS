package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.model.AuthorRank;
import com.rubiks.backendoasis.model.ResearchInterest;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.response.SuccessResponse;
import com.rubiks.backendoasis.response.WrongResponse;
import com.rubiks.backendoasis.model.AffiliationRank;
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
    private PaperBlService paperBlService;

    @Autowired
    public PaperController(PaperBlService paperBlService) {
        this.paperBlService = paperBlService;
    }

    @GetMapping("/test")
    public String test() {
        return "Success";
    }

    @GetMapping("/all")
    public List<PaperDocument> findAll() throws Exception {
        return paperBlService.findAll();
    }

    @GetMapping("/search/basic/es")
    @ApiOperation(value = "es的普通搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public List<PaperDocument> basicSearchByES(@RequestParam(value = "keyword") String keyword,
                                           @RequestParam(value = "page") int page) throws Exception {
        return paperBlService.basicSearchByES(keyword, page);
    }

    @GetMapping("/search/advanced/es")
    @ApiOperation(value = "es的普通搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public List<PaperDocument> advancedSearchByES(@RequestParam(value = "author", required = false) String author,
                                              @RequestParam(value = "affiliation", required = false) String affiliation,
                                              @RequestParam(value = "conferenceName", required = false) String conferenceName,
                                              @RequestParam(value = "keyword", required = false) String keyword,
                                              @RequestParam(value = "page") int page)  throws Exception{
        if (author == null) author = "";
        if (affiliation == null) affiliation = "";
        if (conferenceName == null) conferenceName = "";
        if (keyword == null) keyword = "";

        return paperBlService.advancedSearchByES(author, affiliation, conferenceName, keyword, page);
    }

    @GetMapping("/search/basic/mongo")
    @ApiOperation(value = "普通搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse<List<PaperEntity>> basicSearch(@RequestParam(value = "keyword") String keyword,
                                           @RequestParam(value = "page") int page) {
        return new BasicResponse<>(200, "Success", paperBlService.basicSearch(keyword, page));
    }

    @GetMapping("/search/advanced/mongo")
    @ApiOperation(value = "进阶搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse<List<PaperEntity>> advancedSearch(@RequestParam(value = "author", required = false) String author,
                                            @RequestParam(value = "affiliation", required = false) String affiliation,
                                            @RequestParam(value = "conferenceName", required = false) String conferenceName,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "page") int page) {
        if (author == null) author = "";
        if (affiliation == null) affiliation = "";
        if (conferenceName == null) conferenceName = "";
        if (keyword == null) keyword = "";
        return new BasicResponse<>(200, "Success", paperBlService.advancedSearch(author, affiliation, conferenceName, keyword, page));
    }

    @GetMapping("/rank/basic/affiliation")
    @ApiOperation(value = "查看组织简略排名", notes = "根据topic和sortkey查看排名")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse<List<AffiliationRank>> getAffiliationBasicRanking(@RequestParam(value = "sortKey") String sortKey,
                                                                           @RequestParam(value = "year") String year) {
        return new BasicResponse<>(200, "Success", paperBlService.getAffiliationBasicRanking(sortKey, year));
    }

    @GetMapping("/rank/basic/author")
    @ApiOperation(value = "查看作者简略排名", notes = "根据topic和sortkey查看排名")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse<List<AuthorRank>> getAuthorBasicRanking(@RequestParam(value = "sortKey") String sortKey,
                                                                 @RequestParam(value = "year") String year) {
        return new BasicResponse<>(200, "Success", paperBlService.getAuthorBasicRanking(sortKey, year));
    }

    @GetMapping("/researcher/interest")
    @ApiOperation(value = "查看学者研究方向")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse<List<ResearchInterest>> getResearcherInterest(@RequestParam (value = "researcherId") String id) {
        return new BasicResponse<>(200, "Success", paperBlService.getResearcherInterest(id));
    }

    @GetMapping("/paper/abstract")
    @ApiOperation(value = "查看活跃论文摘要")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse<List<PaperEntity>> getActivePaperAbstract() {
        return new BasicResponse<>(200, "Success", paperBlService.getActivePaperAbstract());
    }


}
