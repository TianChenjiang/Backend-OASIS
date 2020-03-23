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

@RestController
public class SearchController {
    @Autowired
    private SearchBlService searchBlService;

    public SearchController(SearchBlService searchBlService) {
        this.searchBlService = searchBlService;
    }

    @GetMapping("/search/basic/es")
    @ApiOperation(value = "es的普通搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public List<PaperDocument> basicSearchByES(@RequestParam(value = "keyword") String keyword,
                                               @RequestParam(value = "page") int page) throws Exception {
        return searchBlService.basicSearchByES(keyword, page);
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

        return searchBlService.advancedSearchByES(author, affiliation, conferenceName, keyword, page);
    }

    @GetMapping("/search/basic/mongo")
    @ApiOperation(value = "接口1 普通搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse basicSearch(@RequestParam(value = "keyword") String keyword,
                                     @RequestParam(value = "page") int page,
                                     @RequestParam(value = "sortKey") String sortKey
    ) {
        return searchBlService.basicSearch(keyword, page, sortKey);
    }

    @GetMapping("/search/advanced/mongo")
    @ApiOperation(value = "接口2 进阶搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse advancedSearch(@RequestParam(value = "author", required = false) String author,
                                        @RequestParam(value = "affiliation", required = false) String affiliation,
                                        @RequestParam(value = "publicationName", required = false) String publicationName,
                                        @RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "page") int page,
                                        @RequestParam(value = "startYear") int startYear,
                                        @RequestParam(value = "endYear") int endYear) {
        if (author == null) author = "";
        if (affiliation == null) affiliation = "";
        if (publicationName == null) publicationName = "";
        if (keyword == null) keyword = "";
        return searchBlService.advancedSearch(author, affiliation, publicationName, keyword, page, startYear, endYear);
    }

    @GetMapping("/search/basic/mongo/filter")
    @ApiOperation(value = "接口8 二次筛选条件", notes = "获取普通搜索的二次筛选条件 ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse getBasicSearchFilterCondition(@RequestParam(value = "keyword") String keyword) {
        return searchBlService.getBasicSearchFilterCondition(keyword);
    }

}