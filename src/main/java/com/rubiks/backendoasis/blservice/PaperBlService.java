package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.document.PaperDocument;

import java.util.List;

public interface PaperBlService {
    List<PaperDocument> findAll() throws Exception;
    List<PaperDocument> basicSearch(String keyword, int page) throws Exception;
    List<PaperDocument> advancedSearch(String author, String affiliation, String conferenceName, String keyword, int page)
        throws Exception;
}
