package com.rubiks.backendoasis.model.rank;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class BasicRank implements Serializable {
    private String name;
    private int count;

    public BasicRank(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public static <T> List<BasicRank> transformToBasic(List<T> rank){
        List<BasicRank> res = new ArrayList<>();
        if (rank.get(0).getClass() == AcceptanceCountRank.class) {
            List<AcceptanceCountRank> ac = (List<AcceptanceCountRank>)rank;
            for (AcceptanceCountRank a : ac) {
                res.add(new BasicRank(a.getId(), a.getAcceptanceCount()));
            }
        } else {
            List<CitationCountRank> ci = (List<CitationCountRank>)rank;
            for (CitationCountRank a : ci) {
                res.add(new BasicRank(a.getId(), a.getCitationCount()));
            }
        }

        return res;
    }

    public static <T> List<BasicRank> affTransformToBasic(List<T> rank){
        List<BasicRank> res = new ArrayList<>();
        if (rank.get(0).getClass() == AcceptanceCountRank.class) {
            List<AcceptanceCountRank> ac = (List<AcceptanceCountRank>)rank;
            for (AcceptanceCountRank a : ac) {
                res.add(new BasicRank(a.getName(), a.getAcceptanceCount()));
            }
        } else {
            List<CitationCountRank> ci = (List<CitationCountRank>)rank;
            for (CitationCountRank a : ci) {
                res.add(new BasicRank(a.getName(), a.getCitationCount()));
            }
        }

        return res;
    }

}

