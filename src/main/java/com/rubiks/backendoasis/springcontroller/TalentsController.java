package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.TalentsBlService;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TalentsController {
    private  TalentsBlService talentsBlService;

    @Autowired
    public TalentsController(TalentsBlService talentsBlService) {
        this.talentsBlService = talentsBlService;
    }

    @GetMapping("/talents/active")
    public BasicResponse getActiveTalentsBase() {
        return talentsBlService.getActiveTalentsBase();
    }

    @GetMapping("/talents/list")
    public BasicResponse getTalentsListByTalentBase(@RequestParam(value = "field") String field,
                                                    @RequestParam(value = "page") int page) {
        return talentsBlService.getTalentsListByTalentBase(field, page);
    }

    @GetMapping("/talents/recommend")
    public BasicResponse getTalentsActivePapersByTalentBase(@RequestParam(value = "field") String field) {
        return talentsBlService.getTalentsActivePapersByTalentBase(field);
    }

}


