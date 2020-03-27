package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

public interface PortraitBlService {
    BasicResponse getAuthorPortraitById(String id);
    BasicResponse getAffiliationPortrait(String affiliation);
    BasicResponse getKeywordPortrait(String keyword);
    BasicResponse getConferencePortrait(String conference);
}
