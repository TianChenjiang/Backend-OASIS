package com.rubiks.backendoasis.model;

import lombok.Data;

import java.util.List;

@Data
public class PublicationTrend {
    private String authorId;
    private int publicationYear;
    private int num;

    // 返回对应年份的index
    public static int getNumOfYear(List<PublicationTrend> parm, int year) {
        for (PublicationTrend p : parm) {
            if (p.getPublicationYear() == year) {
                return p.getNum();
            }
        }
        return 0;
    }
}
