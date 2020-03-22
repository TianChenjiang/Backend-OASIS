package com.rubiks.backendoasis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NameCount implements Comparable<NameCount>{
    String name;
    Integer count;

    @Override
    public int compareTo(NameCount o) {
        return o.getCount().compareTo(this.getCount());
    }
}
