package com.rubiks.backendoasis.model.rank;

import lombok.Data;

import java.io.Serializable;

@Data
public class CitationCountRank implements Serializable {
    private String id;
    private int citationCount;
    private String name;

}
