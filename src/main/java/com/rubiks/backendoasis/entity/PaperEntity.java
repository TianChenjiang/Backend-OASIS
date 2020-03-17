package com.rubiks.backendoasis.entity;

import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import java.util.List;

@Document(collection = Constant.collectionName)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaperEntity {
    @Id
    private String id;
    @Field("title")
    private String title;
    @Field("authors")
    private List<AuthorEntity> authors;
    @Field("abstract")
    private String _abstract;
    @Field("publicationTitle")
    private String publicationTitle;
    @Field("doi")
    private String doi;
    @Field("publicationYear")
    private int publicationYear;
    @Field("metrics")
    private MetricsEntity metrics;
    @Field("keywords")
    private List<String> keywords;
    @Field("references")
    private List<ReferenceEntity> references;
    @Field("contentType")
    private String contentType;
    @Field("publicationName")
    private String publicationName;
    @Field("link")
    private String link;
}
