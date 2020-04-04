package com.rubiks.backendoasis.model.picture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private String id;
    private String name;
    private int count; // 论文数
    private int citation; // 被引量
}
