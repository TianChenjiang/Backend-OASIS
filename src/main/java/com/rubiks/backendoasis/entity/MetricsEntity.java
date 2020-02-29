package com.rubiks.backendoasis.entity;

import lombok.Data;

@Data
public class MetricsEntity {
    private int citationCountPaper;
    private int citationCountPatent;
    private int totalDownloads;
}
