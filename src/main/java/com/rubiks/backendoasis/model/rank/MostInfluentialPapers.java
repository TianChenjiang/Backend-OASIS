package com.rubiks.backendoasis.model.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class MostInfluentialPapers implements Serializable {
    private int publicationYear;
    private String title;
    private String publicationName;
    private String link;
}
