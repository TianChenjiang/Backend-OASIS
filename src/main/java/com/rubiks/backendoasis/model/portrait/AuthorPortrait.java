package com.rubiks.backendoasis.model.portrait;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class AuthorPortrait implements Serializable {
    private String name;
    private int count;
    private int citation;
    private String affiliation;
    private List<Integer> citationTrend;
    private List<Integer> publicationTrends;
}
