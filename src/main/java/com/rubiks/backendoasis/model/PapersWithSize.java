package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.PaperEntity;
import lombok.Data;

import java.util.List;

@Data
public class PapersWithSize {
    private List<PaperEntity> papers;
    private int size;
    public PapersWithSize(List<PaperEntity> papers, int size) {
        this.papers = papers;
        this.size = size;
    }
}
