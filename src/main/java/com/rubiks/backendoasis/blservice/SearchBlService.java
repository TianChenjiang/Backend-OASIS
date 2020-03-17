package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.response.BasicResponse;

import java.util.List;

public interface SearchBlService {
    List<PaperDocument> findAll() throws Exception;
    List<PaperDocument> basicSearchByES(String keyword, int page) throws Exception;
    List<PaperDocument> advancedSearchByES(String author, String affiliation, String conferenceName, String keyword, int page) throws Exception;
    BasicResponse basicSearch(String keyword, int page, int startYear, int endYear);
    BasicResponse advancedSearch(String author, String affiliation, String publicationName, String keyword, int page, int startYear, int endYear);
}
