package com.rubiks.backendoasis.model.rank;

import lombok.Data;

import java.io.Serializable;

@Data
public class MostRecentPapers implements Serializable {
    private int publicationYear;
    private String title;
    private String publicationName;
    private String link;
}
