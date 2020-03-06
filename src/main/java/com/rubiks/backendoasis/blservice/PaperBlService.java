package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.model.AffiliationRank;
import com.rubiks.backendoasis.model.AuthorRank;
import com.rubiks.backendoasis.model.PapersWithSize;
import com.rubiks.backendoasis.model.ResearchInterest;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PaperBlService {
    List<PaperDocument> findAll() throws Exception;
    List<PaperDocument> basicSearchByES(String keyword, int page) throws Exception;
    List<PaperDocument> advancedSearchByES(String author, String affiliation, String conferenceName, String keyword, int page)
        throws Exception;
    BasicResponse basicSearch(String keyword, int page, String startYear, String endYear);
    BasicResponse advancedSearch(String author, String affiliation, String conferenceName, String keyword, int page, String startYear, String endYear);
    BasicResponse getAffiliationBasicRanking(String sortKey, String year);
    BasicResponse getAuthorBasicRanking(String sortKey, String year);
    BasicResponse getResearcherInterest(String id);
    BasicResponse getActivePaperAbstract();
    BasicResponse getReferenceById(String paperId);
//    List<ResearchInterest> getMaxResearcherInterest();
}
