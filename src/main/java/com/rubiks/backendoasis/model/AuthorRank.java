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
                res.add(new AuthorRank(a.getName(), a.getAcceptanceCount(), a.getId()));
            }
        } else {
            List<CitationCountRank> ci = (List<CitationCountRank>)rank;
            for (CitationCountRank a : ci) {
                res.add(new AuthorRank(a.getName(), a.getCitationCount(), a.getId()));
            }
        }

        return res;
    }
}
