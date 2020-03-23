package com.rubiks.backendoasis.model.rank;

import lombok.Data;

import java.io.Serializable;

@Data
public class AcceptanceCountRank implements Serializable {
    private String id;
    private int acceptanceCount;
    private String name;
}

