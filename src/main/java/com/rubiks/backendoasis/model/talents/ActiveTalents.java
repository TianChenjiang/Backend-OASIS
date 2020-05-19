package com.rubiks.backendoasis.model.talents;

import com.rubiks.backendoasis.entity.talents.ExpertsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveTalents implements Serializable {
    private String field;
    private int count;
    private List<BriefExpert> experts;
}











