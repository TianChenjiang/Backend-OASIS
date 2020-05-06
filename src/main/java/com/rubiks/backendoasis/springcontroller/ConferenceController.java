package com.rubiks.backendoasis.springcontroller;

import com.rubiks.backendoasis.blservice.ConferenceBlService;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conference")
public class ConferenceController {
    @Autowired
    private ConferenceBlService conferenceBlService;

    @GetMapping("/publication")
    public BasicResponse getConferencesAndJournalsList(@RequestParam(value = "keyword") String keyword,
                                                       @RequestParam(value = "page") int page) {
        return conferenceBlService.getConferencesAndJournalsList(keyword, page);
    }

    @GetMapping("/proceeding")
    public BasicResponse getConferencesAndJournalsProceedings(@RequestParam(value = "titleId") String titleId) {
        return conferenceBlService.getConferencesAndJournalsProceedings(titleId);
    }

}
