package com.rubiks.backendoasis.model.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MostInfluentialPapers {
    private int publicationYear;
    private String title;
    private String publicationName;
    private String link;
}
