package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.TalentsBlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TalentsController {
    private  TalentsBlService talentsBlService;

    @Autowired
    public TalentsController(TalentsBlService talentsBlService) {
        this.talentsBlService = talentsBlService;
    }

}
