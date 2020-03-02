package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.model.AffiliationRank;
import com.rubiks.backendoasis.model.AuthorRank;
import com.rubiks.backendoasis.model.ResearchInterest;

import java.util.List;

public interface PaperBlService {
    List<PaperDocument> findAll() throws Exception;
    List<PaperDocument> basicSearchByES(String keyword, int page) throws Exception;
    List<PaperDocument> advancedSearchByES(String author, String affiliation, String conferenceName, String keyword, int page)
        throws Exception;
    List<PaperEntity> basicSearch(String keyword, int page, String startYear, String endYear);
    List<PaperEntity> advancedSearch(String author, String affiliation, String conferenceName, String keyword, int page, String startYear, String endYear);
    List<AffiliationRank>  getAffiliationBasicRanking(String sortKey, String year);
    List<AuthorRank> getAuthorBasicRanking(String sortKey, String year);
    List<ResearchInterest> getResearcherInterest(String id);
    List<PaperEntity> getActivePaperAbstract();
}
