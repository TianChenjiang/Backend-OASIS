package com.rubiks.backendoasis.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AuthorRank {
    private String name;
    private int count;
    private String researcherId;

    public AuthorRank(String name, int count, String researcherId) {
        this.name = name;
        this.count = count;
        this.researcherId = researcherId;
    }

    public static <T> List<AuthorRank> transformToBasic(List<T> rank){
        List<AuthorRank> res = new ArrayList<>();
        if (rank.get(0).getClass() == AcceptanceCountRank.class) {
            List<AcceptanceCountRank> ac = (List<AcceptanceCountRank>)rank;
            for (AcceptanceCountRank a : ac) {
                res.add(new AuthorRank(a.getId(), a.getAcceptanceCount(), a.getResearcherId()));
            }
        } else {
            List<CitationCountRank> ci = (List<CitationCountRank>)rank;
            for (CitationCountRank a : ci) {
                res.add(new AuthorRank(a.getId(), a.getCitationCount(), a.getResearcherId()));
            }
        }

        return res;
    }
}
