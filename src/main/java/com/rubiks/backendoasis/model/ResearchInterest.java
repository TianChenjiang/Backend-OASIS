package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.PaperEntity;
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

        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry =  iterator.next();
            res.add(new ResearchInterest(entry.getKey(), entry.getValue()));
        }

        return res;
    }
}
