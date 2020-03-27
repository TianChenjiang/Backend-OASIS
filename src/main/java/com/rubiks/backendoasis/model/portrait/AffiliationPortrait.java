package com.rubiks.backendoasis.model.portrait;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class AffiliationPortrait implements Serializable {
    private int count;
    private int citation;
    private int authorNum;
    private List<Integer> citationTrend;
    private List<Integer> publicationTrends;
}

