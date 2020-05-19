package com.rubiks.backendoasis.entity.paper;

import lombok.Data;

import java.util.List;

@Data
public class ReferenceEntity {
    private String order;
    private String text;
    private String title;
    private List<ReferenceContextEntity> context;
    private String googleScholarLink;
    private LinkEntity links;
    private String refType;
    private String id;
}
