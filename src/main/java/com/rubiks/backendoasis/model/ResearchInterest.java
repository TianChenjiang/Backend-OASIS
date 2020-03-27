package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.PaperEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResearchInterest implements Serializable {
    String name;
    int value;
    public ResearchInterest(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static List<ResearchInterest> constructNameValueMap(List<PaperEntity> paperEntities) {
        List<String> keywordList = new ArrayList<>();
        for (PaperEntity paperEntity : paperEntities) {
            if (paperEntity.getKeywords() != null) {    // keywords需要为非空
                List<String> curKeywordList = paperEntity.getKeywords();
                for (String curKeyword : curKeywordList) {
                    keywordList.add(curKeyword);
                }
            }
        }

        List<ResearchInterest> res = new ArrayList<>();
        for (String curKeyword: keywordList) {
            boolean keywordExist = false;
            for (int i = 0; i < res.size(); i++) {
                ResearchInterest cur = res.get(i);
                if (cur.getName().equals(curKeyword)) {
                    keywordExist = true;
                    cur.setValue(cur.getValue()+1);
                    break;
                }
            }
            if (!keywordExist) {
                res.add(new ResearchInterest(curKeyword, 1));
            }
        }
        return res;
    }
}
