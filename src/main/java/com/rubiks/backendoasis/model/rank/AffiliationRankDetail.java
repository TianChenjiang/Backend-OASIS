package com.rubiks.backendoasis.model.rank;

import com.rubiks.backendoasis.model.ResearchInterest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class AffiliationRankDetail implements Serializable {
    private List<Integer>  publicationTrend;
    private List<ResearchInterest> keywords;
}

