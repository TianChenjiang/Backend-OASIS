package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.DataSourceBlService;
import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.model.AuthorRank;
import com.rubiks.backendoasis.model.PapersWithSize;
import com.rubiks.backendoasis.model.ResearchInterest;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.response.Response;
import com.rubiks.backendoasis.response.SuccessResponse;
import com.rubiks.backendoasis.response.WrongResponse;
import com.rubiks.backendoasis.model.AffiliationRank;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    @ApiOperation(value = "接口1 普通搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse basicSearch(@RequestParam(value = "keyword") String keyword,
                                           @RequestParam(value = "page") int page,
                                           @RequestParam(value = "startYear") String startYear,
                                           @RequestParam(value = "endYear") String endYear) {
        return paperBlService.basicSearch(keyword, page, startYear, endYear);
    }

    @GetMapping("/search/advanced/mongo")
    @ApiOperation(value = "接口2 进阶搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse advancedSearch(@RequestParam(value = "author", required = false) String author,
                                            @RequestParam(value = "affiliation", required = false) String affiliation,
                                            @RequestParam(value = "conferenceName", required = false) String conferenceName,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "page") int page,
                                            @RequestParam(value = "startYear") String startYear,
                                            @RequestParam(value = "endYear") String endYear) {
        if (author == null) author = "";
        if (affiliation == null) affiliation = "";
        if (conferenceName == null) conferenceName = "";
        if (keyword == null) keyword = "";
        return paperBlService.advancedSearch(author, affiliation, conferenceName, keyword, page, startYear, endYear);
    }

    @GetMapping("/rank/basic/affiliation")
    @ApiOperation(value = "接口3 查看组织简略排名", notes = "根据topic和sortkey查看排名")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getAffiliationBasicRanking(@RequestParam(value = "sortKey") String sortKey,
                                                                           @RequestParam(value = "year") String year) {
        return  paperBlService.getAffiliationBasicRanking(sortKey, year);
    }

    @GetMapping("/rank/basic/author")
    @ApiOperation(value = "接口4 查看作者简略排名", notes = "根据topic和sortkey查看排名")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getAuthorBasicRanking(@RequestParam(value = "sortKey") String sortKey,
                                                                 @RequestParam(value = "year") String year) {
        return paperBlService.getAuthorBasicRanking(sortKey, year);
    }

    @GetMapping("/researcher/interest")
    @ApiOperation(value = "接口5 查看学者研究方向")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getResearcherInterest(@RequestParam (value = "researcherId") String id) {
        return paperBlService.getResearcherInterest(id);
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



}
