package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

public interface PaperBlService {
    BasicResponse getResearcherInterest(String id);
    BasicResponse getActivePaperAbstract();
    BasicResponse getReferenceById(String paperId);
//    List<ResearchInterest> getMaxResearcherInterest();
}
