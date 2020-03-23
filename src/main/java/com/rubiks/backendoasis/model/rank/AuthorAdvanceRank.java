package com.rubiks.backendoasis.model.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@AllArgsConstructor
public class AuthorAdvanceRank implements Serializable {
    private String authorId;
    private String authorName;
    private int count;
    private int citation;
    private List<Integer> publicationTrend;
}
