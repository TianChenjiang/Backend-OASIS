package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

public interface PaperBlService {
    BasicResponse getResearcherInterest(String id);
    BasicResponse getAffiliationInterest(String affiliation);
    BasicResponse getActivePaperAbstract();
    BasicResponse getReferenceById(String paperId);
    BasicResponse getAuthorPapersById(String authorId, int page, String sortKey);
    BasicResponse getAffiliationPapers(String affiliation, int page, String sortKey);
//    List<ResearchInterest> getMaxResearcherInterest();
}
