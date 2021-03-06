package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.response.BasicResponse;

import java.util.List;

public interface SearchBlService {
    List<PaperDocument> findAll() throws Exception;
    BasicResponse basicSearchByES(String keyword, int page, String sortKey) throws Exception;
    BasicResponse basicSearchByESWithHighLight(String keyword, int page, String sortKey) throws Exception;

    BasicResponse advancedSearchByES(String field, String author, String affiliation, String publicationName, String keyword, int startYear, int endYear, int page, String sortKey) throws Exception;
    BasicResponse advancedSearchByESWithHighLight(String field, String author, String affiliation, String publicationName, String keyword, int startYear, int endYear, int page, String sortKey) throws Exception;

    BasicResponse basicFilterSearch(String keyword, String author, String affiliation, String publicationName, int startYear, int endYear, int page, String sortKey) throws Exception;

    BasicResponse searchSuggestion(String keyword) throws Exception;

    BasicResponse basicSearch(String keyword, int page, String sortKey);
    BasicResponse advancedSearch(String author, String affiliation, String publicationName, String keyword, int page, int startYear, int endYear);
    BasicResponse getBasicSearchFilterCondition(String keyword) throws Exception;

    BasicResponse commandSearch(String query, int page, String sortKey) throws Exception;
}
