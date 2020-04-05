package com.rubiks.backendoasis.model.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link {
    private String source; // authorId
    private String target; // authorId
    private long value; // 线段长度（距离）
}
