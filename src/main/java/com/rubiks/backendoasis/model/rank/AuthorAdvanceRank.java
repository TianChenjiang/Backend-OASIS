package com.rubiks.backendoasis.model.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class AuthorAdvanceRank {
    private String authorId;
    private String authorName;
    private int count;
    private int citation;
    private List<Integer> publicationTrend;
}
