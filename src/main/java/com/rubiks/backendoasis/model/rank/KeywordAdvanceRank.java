package com.rubiks.backendoasis.model.rank;

import com.rubiks.backendoasis.model.IdYearMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeywordAdvanceRank implements Serializable {
    private String keyword;
    private int count;
    private int citation;
    private int authorNum;
}
