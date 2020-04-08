package com.rubiks.backendoasis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterCondition implements Serializable {
    private List<NameCount> authors;
    private List<NameCount> affiliations;
    private List<NameCount> conferences;
    private List<NameCount> journals;

    public static List<NameCount> addNameCount(List<NameCount> l, String name) {
        if (!name.isEmpty()) {
            return l; //name如果为空，直接跳过
        }
        for (NameCount n : l) {
            if (n.getName().equals(name)) {
                n.setCount(n.getCount()+1);
                return l;
            }
        }
        l.add(new NameCount(name, 1));
        return l;
    }

}

