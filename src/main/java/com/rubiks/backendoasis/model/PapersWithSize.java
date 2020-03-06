package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.PaperEntity;
import lombok.Data;

import java.util.List;

@Data
public class PapersWithSize {
    private List<PaperWithoutRef> papers;
    private long size;
    public PapersWithSize(List<PaperWithoutRef> papers, long size) {
        this.papers = papers;
        this.size = size;
    }
}
