package com.rubiks.backendoasis.model.rank;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AffiliationAdvanceRank implements Serializable {
    private String affiliationId;
    private String affiliationName;
    private int count;
    private int citation;
    private int authorNum;


    public AffiliationAdvanceRank(String affiliationName, int count, int citation, int authorNum) {
        this.affiliationName = affiliationName;
        this.affiliationId = affiliationName; //现在还没有id，暂时用name代替
        this.count = count;
        this.citation = citation;
        this.authorNum = authorNum;
    }
}
