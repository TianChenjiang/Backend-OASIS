package com.rubiks.backendoasis.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResearchInterest implements Serializable {
    String name;
    int value;
    public ResearchInterest(String name, int value) {
        this.name = name;
        this.value = value;
    }
}
