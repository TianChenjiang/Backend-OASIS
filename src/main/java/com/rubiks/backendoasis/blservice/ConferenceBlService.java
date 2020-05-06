package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

public interface ConferenceBlService {
    BasicResponse getConferencesAndJournalsList(String keyword, int page);
    BasicResponse getConferencesAndJournalsProceedings(String titleId);
}
