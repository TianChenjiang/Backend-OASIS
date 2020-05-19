package com.rubiks.backendoasis.model.rank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordAdvanceRank implements Serializable {
    private String keyword;
    private int count;
    private int citation;
    private int authorNum;
}
