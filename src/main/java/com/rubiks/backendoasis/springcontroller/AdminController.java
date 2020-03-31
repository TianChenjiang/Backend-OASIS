package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.AdminBlService;
import com.rubiks.backendoasis.model.admin.MergeParm;
import com.rubiks.backendoasis.model.admin.ModifyParm;
import com.rubiks.backendoasis.model.admin.UpdatePaperParameter;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {
    @Autowired
    private AdminBlService adminBlService;

    public AdminController(AdminBlService adminBlService) {
        this.adminBlService = adminBlService;
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

    @PutMapping("/info/paper")
    public BasicResponse updatePaperInfo(@RequestBody UpdatePaperParameter updatePaperParameter) {
        return adminBlService.updatePaperInfo(updatePaperParameter);
    }

}
