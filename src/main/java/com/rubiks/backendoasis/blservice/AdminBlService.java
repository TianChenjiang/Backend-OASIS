package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

public interface AdminBlService {
    BasicResponse getConferenceInfo(int page, String name);
    BasicResponse getAffiliationInfo(int page, String name);
    BasicResponse getJournalInfo(int page, String name);
    BasicResponse getAuthorInfo(int page, String name);

}
