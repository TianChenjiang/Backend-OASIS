package com.rubiks.backendoasis.esdocument;

import lombok.Data;

import java.util.List;

@Data
public class PaperDocument {
    private String title;
    private List<Author> author;
    private String _abstract;
    private String publicationTitle;
    private String doi;
    private String publicationYear;
    private Metrics metrics;
    private List<String> keywords;
    private List<Reference> references;
    private String conferenceName;
    private String link;
}
