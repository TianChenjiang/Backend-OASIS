package com.rubiks.backendoasis.model.rank;

import lombok.Data;

@Data
public class MostRecentPapers {
    private int publicationYear;
    private String title;
    private String publicationName;
    private String link;
}
