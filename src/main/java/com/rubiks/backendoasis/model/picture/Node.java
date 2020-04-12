package com.rubiks.backendoasis.model.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node implements Serializable {
    private String id;
    private String name;
    private int count; // 论文数
    private int citation; // 被引量
    private double value; // 衡量影响力的指标
}
