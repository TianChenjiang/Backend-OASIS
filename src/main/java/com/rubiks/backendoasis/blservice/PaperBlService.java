package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.esdocument.PaperDocument;

import java.util.List;

public interface PaperBlService {
    List<PaperDocument> findAll() throws Exception;
    List<PaperDocument> basicSearchByES(String keyword, int page) throws Exception;
    List<PaperDocument> advancedSearchByES(String author, String affiliation, String conferenceName, String keyword, int page)
        throws Exception;
    List<PaperDocument> advancedSearch(String author, String affiliation, String conferenceName, String keyword, int page);
}
