package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.document.PaperDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
}
