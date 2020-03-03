package com.rubiks.backendoasis.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class AffiliationRank {
    private String name;
    private int count;

    public AffiliationRank(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public static <T> List<AffiliationRank> transformToBasic(List<T> rank){
        List<AffiliationRank> res = new ArrayList<>();
        if (rank.get(0).getClass() == AcceptanceCountRank.class) {
            List<AcceptanceCountRank> ac = (List<AcceptanceCountRank>)rank;
            for (AcceptanceCountRank a : ac) {
                res.add(new AffiliationRank(a.getId(), a.getAcceptanceCount()));
            }
        } else {
            List<CitationCountRank> ci = (List<CitationCountRank>)rank;
            for (CitationCountRank a : ci) {
                res.add(new AffiliationRank(a.getId(), a.getCitationCount()));
            }
        }

        return res;
    }

}

