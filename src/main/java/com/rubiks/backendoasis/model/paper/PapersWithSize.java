package com.rubiks.backendoasis.model.paper;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PapersWithSize implements Serializable {
    private List<PaperWithoutRef> papers;
    private long size;
    public PapersWithSize(List<PaperWithoutRef> papers, long size) {
        this.papers = papers;
        this.size = size;
    }
}
