package com.rubiks.backendoasis.document;

import lombok.Data;

@Data
public class Reference {
    private String order;
    private String text;
    private String title;
    private ReferenceContext context;
    private String googleScholarLink;
    private String refType;
    private String id;
}
