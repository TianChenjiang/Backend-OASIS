package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.document.PaperDocument;

import java.util.List;

public interface PaperBlService {
    List<PaperDocument> findAll() throws Exception;
}
