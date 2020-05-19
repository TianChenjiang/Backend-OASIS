package com.rubiks.backendoasis.model.paper;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PublicationTrend implements Serializable {
    private String authorId;
    private int publicationYear;
    private int num;
    private int citation;

    // 返回对应年份的index
    public static int getNumOfYear(List<PublicationTrend> parm, int year) {
        for (PublicationTrend p : parm) {
            if (p.getPublicationYear() == year) {
                return p.getNum();
            }
        }
        return 0;
    }

    public static  int getCitationOfYear(List<PublicationTrend> parm, int year) {
        for (PublicationTrend p : parm) {
            if (p.getPublicationYear() == year) {
                return p.getCitation();
            }
        }
        return 0;
    }
}
