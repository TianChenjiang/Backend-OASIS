package com.rubiks.backendoasis.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BriefAuthor {
    private String authorId;
    private String authorName;
    private int count;
    private int citation;
}
