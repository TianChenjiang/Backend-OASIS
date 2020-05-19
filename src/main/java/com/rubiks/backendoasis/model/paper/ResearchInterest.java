package com.rubiks.backendoasis.model.paper;

import com.rubiks.backendoasis.entity.paper.PaperEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class ResearchInterest implements Serializable {
    String name;
    int value;
    public ResearchInterest(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static List<ResearchInterest> constructNameValueMap(List<PaperEntity> paperEntities) {
        List<ResearchInterest> res = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();

        for (PaperEntity paperEntity : paperEntities) {
            if (paperEntity.getKeywords() != null) {    // keywords需要为非空
                List<String> curKeywordList = paperEntity.getKeywords();
                for (String curKeyword : curKeywordList) {
                    int count = 1;
                    if (map.containsKey(curKeyword)) {
                        count = map.get(curKeyword) + 1;
                    }
                    map.put(curKeyword, count);
                }
            }
        }
        List<Map.Entry<String, Integer>> l  = new ArrayList<>(map.entrySet());
        l.sort((a1, a2) -> (a2.getValue() - a1.getValue()));
        int count = 0;
        for (Map.Entry<String, Integer> entry : l) {
            res.add(new ResearchInterest(entry.getKey(), entry.getValue()));
            count++;
            if (count == 100) {
                break;
            }
        }
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            res.add(new ResearchInterest(entry.getKey(), entry.getValue()));
//        }
        return res;
    }
}
