package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.AdminBlService;
import com.rubiks.backendoasis.blservice.SearchBlService;
import com.rubiks.backendoasis.model.admin.ModifyParm;
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
        return adminBlService.updateConferenceInfo(modifyParm.getSrc(), modifyParm.getDesc());
    }

    @PutMapping("/info/journal")
    public BasicResponse updateJournalInfo(@RequestBody ModifyParm modifyParm) {
        return adminBlService.updateJournalInfo(modifyParm.getSrc(), modifyParm.getDesc());
    }


}
