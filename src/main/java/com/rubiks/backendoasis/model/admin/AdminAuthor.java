package com.rubiks.backendoasis.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AdminAuthor implements Serializable {
    private String authorId;
    private String authorName;
    private int count;
    private int citation;
}
