package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.AdminBlService;
import com.rubiks.backendoasis.exception.FileFormatNotSupportException;
import com.rubiks.backendoasis.model.admin.MergeParm;
import com.rubiks.backendoasis.model.admin.ModifyParm;
import com.rubiks.backendoasis.model.admin.UpdatePaperParameter;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.response.Response;
import com.rubiks.backendoasis.response.SuccessResponse;
import com.rubiks.backendoasis.response.WrongResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AdminController {
    @Autowired
    private AdminBlService adminBlService;

    public AdminController(AdminBlService adminBlService) {
        this.adminBlService = adminBlService;
    }


    @PostMapping("/import/paper")
    @ApiOperation(value = "接口 6导入论文数据")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = SuccessResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized", response = WrongResponse.class),
            @ApiResponse(code = 500, message = "Failure", response = WrongResponse.class)})
    public BasicResponse importPaperData(@RequestParam("paperData") MultipartFile paperData) {
        try {
            BasicResponse basicResponse = adminBlService.importPaperData(paperData);
            return basicResponse;
        } catch (FileFormatNotSupportException e) {
            return new BasicResponse(e.getCode(), e.getMessage(), null);
        }

    }

    @GetMapping("/info/conference")
    public BasicResponse getConferenceInfo(@RequestParam(value = "page") int page,
                                           @RequestParam(value = "name", required = false) String name) {
        return adminBlService.getConferenceInfo(page, name);
    }

    @GetMapping("/info/affiliation")
    public BasicResponse getAffiliationInfo(@RequestParam(value = "page") int page,
                                           @RequestParam(value = "name", required = false) String name) {
        return adminBlService.getAffiliationInfo(page, name);
    }

    @GetMapping("/info/journal")
    public BasicResponse getJournalInfo(@RequestParam(value = "page") int page,
                                           @RequestParam(value = "name", required = false) String name) {
        return adminBlService.getJournalInfo(page, name);
    }

    @GetMapping("/info/author")
    public BasicResponse getAuthorInfo(@RequestParam(value = "page") int page,
                                           @RequestParam(value = "name", required = false) String name) {
        return adminBlService.getAuthorInfo(page, name);
    }

    @GetMapping("/info/keyword")
    public BasicResponse getKeywordInfo(@RequestParam(value = "page") int page,
                                       @RequestParam(value = "name", required = false) String name) {
        return adminBlService.getKeywordInfo(page, name);
    }

    @PutMapping("/info/conference")
    public BasicResponse updateConferenceInfo(@RequestBody ModifyParm modifyParm) {
        return adminBlService.updateConferenceInfo(modifyParm.getSrc(), modifyParm.getDest());
    }

    @PutMapping("/info/journal")
    public BasicResponse updateJournalInfo(@RequestBody ModifyParm modifyParm) {
        return adminBlService.updateJournalInfo(modifyParm.getSrc(), modifyParm.getDest());
    }

    @PutMapping("/info/affiliation")
    public BasicResponse mergeAffiliationInfo(@RequestBody MergeParm mergeParm) {
        return adminBlService.mergeAffiliationInfo(mergeParm.getSrc(), mergeParm.getDest());
    }

    @PutMapping("/info/author")
    public BasicResponse mergeAuthorInfo(@RequestBody MergeParm mergeParm) {
        return adminBlService.mergeAuthorInfo(mergeParm.getSrc(), mergeParm.getDest());
    }

    @PutMapping("/info/keyword")
    public BasicResponse mergeKeywordsInfo(@RequestBody MergeParm mergeParm) {
        return adminBlService.mergeKeywordsInfo(mergeParm.getSrc(), mergeParm.getDest());
    }

    @PutMapping("/info/paper")
    public BasicResponse updatePaperInfo(@RequestBody UpdatePaperParameter updatePaperParameter) {
        return adminBlService.updatePaperInfo(updatePaperParameter);
    }

    @GetMapping("/recommend/author")
    public BasicResponse getRecommendedSimilarAuthor() {
        return adminBlService.getRecommendedSimilarAuthor();
    }

    @GetMapping("/recommend/affiliation")
    public BasicResponse getRecommendedSimilarAffiliation() {
        return adminBlService.getRecommendedSimilarAffiliation();
    }

    @GetMapping("/cache")
    public BasicResponse updateMainPageCache() {
        return adminBlService.updateMainPageCache();
    }

}
