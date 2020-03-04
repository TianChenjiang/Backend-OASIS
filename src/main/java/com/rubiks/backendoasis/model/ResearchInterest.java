package com.rubiks.backendoasis.model;

import lombok.Data;

@Data
public class ResearchInterest {
    String name;
    int value;
    public ResearchInterest(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
