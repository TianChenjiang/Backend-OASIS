package com.rubiks.backendoasis.model.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Link implements Serializable {
    private String source; // authorId
    private String target; // authorId
    private long value; // 线段长度（距离）
}
