package com.rubiks.backendoasis.model.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class AffiliationRankDetail implements Serializable {
    private List<Integer>  publicationTrend;
    private List<ValueCount> keywords;

    class ValueCount {
        int value;
        String name;
    }
}

