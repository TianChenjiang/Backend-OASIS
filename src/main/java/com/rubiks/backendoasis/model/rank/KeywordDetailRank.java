package com.rubiks.backendoasis.model.rank;

import com.rubiks.backendoasis.model.IdYearMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordDetailRank {
    private List<Integer> publicationTrend;
    private List<MostInfluentialPapers> mostInfluentialPapers;

    public static KeywordDetailRank CalAndSetPublicationTrends(String keyword,  List<IdYearMap> curRes) {
        KeywordDetailRank res = new KeywordDetailRank();
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> publicationTrends = new ArrayList<>(Collections.nCopies(10, 0));

        int low  = curYear-9;
        for (IdYearMap idYearMap : curRes) {
            if (idYearMap.getAuthorId() == null) {
                continue;  //有authorId为空的情况
            }
            else if (idYearMap.getAuthorId().equals(keyword)) {
                int index = idYearMap.getYear()-low;   //数组中应该存放的位置
                int origin = publicationTrends.get(index);
                publicationTrends.set(index, ++origin);
            }
        }

        res.setPublicationTrend(publicationTrends);
        return res;
    }
}
