package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.PaperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterCondition implements Serializable {
    private List<NameCount> authors;
    private List<NameCount> affiliations;
    private List<NameCount> conferences;
    private List<NameCount> journals;

    public static List<NameCount> addNameCount(List<NameCount> l, String name) {
        if (name.isEmpty()) {
            return l; //name如果为空，直接跳过
        }
        for (NameCount n : l) {
            if (n.getName().equals(name)) {
                int curCount = n.getCount();
                n.setCount(++curCount);
                return l;
            }
        }
        l.add(new NameCount(name, 1));
        return l;
    }

    public static HashMap<String, Integer> addNameToMap(HashMap<String, Integer> map, String name) {
        if (name.isEmpty()) return map;
        int count = 1;
        if (map.containsKey(name)) {
            count = map.get(name) + 1;
        }
        map.put(name, count);
        return map;
    }

    public static List<NameCount> mapToNameCount(HashMap<String, Integer> map) {
        List<NameCount> res = new ArrayList<>();

        List<Map.Entry<String, Integer>> l  = new ArrayList<>(map.entrySet());
        l.sort((a1, a2) -> (a2.getValue() - a1.getValue()));
        int count = 0;
        for (Map.Entry<String, Integer> entry : l) {
            res.add(new NameCount(entry.getKey(), entry.getValue()));
            count++;
            if (count == 10) break;
        }
        return res;
    }
}

