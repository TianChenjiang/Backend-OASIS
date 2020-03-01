package com.rubiks.backendoasis.model;

import lombok.Data;

@Data
public class ResearchInterest {
    String name;
    int count;
    public ResearchInterest(String name, int count) {
        this.name = name;
        this.count = count;
    }
}
