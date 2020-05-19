package com.rubiks.backendoasis.model.paper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class NameCount implements Comparable<NameCount>, Serializable {
    String name;
    Integer count;

    @Override
    public int compareTo(NameCount o) {
        return o.getCount().compareTo(this.getCount());
    }
}
