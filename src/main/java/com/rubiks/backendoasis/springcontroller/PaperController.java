package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.PaperDocument;
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
    public List<PaperEntity> basicSearch(@RequestParam(value = "keyword") String keyword,
                                           @RequestParam(value = "page") int page) {
        return paperBlService.basicSearch(keyword, page);
    }

    @GetMapping("/search/advanced/mongo")
    @ApiOperation(value = "普通搜索", notes = "根据关键词获得相关论文")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public List<PaperEntity> advancedSearch(@RequestParam(value = "author", required = false) String author,
                                            @RequestParam(value = "affiliation", required = false) String affiliation,
                                            @RequestParam(value = "conferenceName", required = false) String conferenceName,
                                            @RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "page") int page) {
        if (author == null) author = "";
        if (affiliation == null) affiliation = "";
        if (conferenceName == null) conferenceName = "";
        if (keyword == null) keyword = "";
        return paperBlService.advancedSearch(author, affiliation, conferenceName, keyword, page);
    }



}
