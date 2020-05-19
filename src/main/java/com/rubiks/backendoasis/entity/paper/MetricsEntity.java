package com.rubiks.backendoasis.entity.paper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricsEntity {
    private int citationCountPaper;
    private int citationCountPatent;
    private int totalDownloads;
}
