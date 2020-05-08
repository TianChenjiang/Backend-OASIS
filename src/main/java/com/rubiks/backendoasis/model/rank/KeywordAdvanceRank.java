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
public class KeywordAdvanceRank {
    private String keyword;
    private int count;
    private int citation;
    private int authorNum;
    private List<Integer> publicationTrend;
    private List<MostInfluentialPapers> mostInfluentialPapers;

    public static List<KeywordAdvanceRank> CalAndSetPublicationTrends(List<KeywordAdvanceRank> res, List<IdYearMap> curRes) {
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (KeywordAdvanceRank keywordAdvanceRank : res) {
            String curId = keywordAdvanceRank.getKeyword();
            List<Integer> publicationTrends = new ArrayList<>(Collections.nCopies(10, 0));

            int low  = curYear-9;
            for (IdYearMap idYearMap : curRes) {
                if (idYearMap.getAuthorId() == null) {
                    continue;  //有authorId为空的情况
                }
                else if (idYearMap.getAuthorId().equals(curId)) {
                    int index = idYearMap.getYear()-low;   //数组中应该存放的位置
                    int origin = publicationTrends.get(index);
                    publicationTrends.set(index, ++origin);
                }
            }

            keywordAdvanceRank.setPublicationTrend(publicationTrends);
        }
        return res;
    }
}
