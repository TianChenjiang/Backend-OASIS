package com.rubiks.backendoasis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
@AllArgsConstructor
public class IdYearNumMap implements Serializable {
    private int num;
    private int publicationYear;
    private String id;

    public static List<Integer> reduceById(List<IdYearNumMap> parm, String id) {
//        List<IdYearNumMap> filter = parm.stream()
//                .filter(p-> p.authorId.equals(authorId))
//                .collect(Collectors.toList());
        List<IdYearNumMap> filter = new ArrayList<>();
        for (IdYearNumMap i : parm) {
            if (i.getId().equals(id)) {
                filter.add(i);
            }
        }

        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> res = new ArrayList<>();
        for (int i = curYear-9; i <= curYear; i++) {
            res.add(getNumOfYear(filter, i));
        }
        return res;
    }

    private static int getNumOfYear(List<IdYearNumMap> parm, int publicationYear) {
        for (IdYearNumMap m : parm) {
            if (m.getPublicationYear() == publicationYear) {
                return m.getNum();
            }
        }
        return 0;
    }
}
