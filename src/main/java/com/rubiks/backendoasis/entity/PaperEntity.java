package com.rubiks.backendoasis.entity;

import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.TextScore;


import java.util.List;

@Document(collection = Constant.collectionName)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaperEntity {
    @Id
    private String id;

//    @TextIndexed
    @Field("title")
    private String title;

//    @TextIndexed
    @Field("authors")
    private List<AuthorEntity> authors;

//    @TextIndexed
    @Field("abstract")
    private String _abstract;

//    @TextIndexed
    @Field("publicationTitle")
    private String publicationTitle;

//    @TextIndexed
    @Field("doi")
    private String doi;

    @Field("publicationYear")
    private int publicationYear;

    @Field("metrics")
    private MetricsEntity metrics;

//    @TextIndexed
    @Field("keywords")
    private List<String> keywords;

    @Field("references")
    private List<ReferenceEntity> references;

//    @TextIndexed
    @Field("contentType")
    private String contentType;

//    @TextIndexed
    @Field("publicationName")
    private String publicationName;

    @Field("link")
    private String link;

    @Field("ieeeId")
    private String ieeeId;

}
