package com.rubiks.backendoasis.document;

import lombok.Data;

import java.util.List;

@Data
public class Reference {
    private String order;
    private String text;
    private String title;
    private List<ReferenceContext> context;
    private String googleScholarLink;
    private Link links;
    private String refType;
    private String id;
}
