package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.blservice.PortraitBlService;
import com.rubiks.backendoasis.blservice.RankBlService;
import com.rubiks.backendoasis.blservice.SearchBlService;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortraitController {
    @Autowired PortraitBlService portraitBlService;

    public PortraitController(@Autowired PortraitBlService portraitBlService) {
        this.portraitBlService = portraitBlService;
    }

    @GetMapping("/portrait/author")
    public BasicResponse getAuthorPortraitById(@RequestParam(value = "authorId") String authorId) {
        return portraitBlService.getAuthorPortraitById(authorId);
    }

    @GetMapping("/portrait/affiliation")
    public BasicResponse getAffiliationPortrait(@RequestParam(value = "affiliation") String affiliation) {
        return portraitBlService.getAffiliationPortrait(affiliation);
    }

    @GetMapping("/portrait/keyword")
    public BasicResponse getKeywordPortrait(@RequestParam(value = "keyword") String keyword) {
        return portraitBlService.getKeywordPortrait(keyword);
    }

    @GetMapping("/portrait/conference")
    public BasicResponse getConferencePortrait(@RequestParam(value = "conference") String conference) {
        return portraitBlService.getConferencePortrait(conference);
    }

}
