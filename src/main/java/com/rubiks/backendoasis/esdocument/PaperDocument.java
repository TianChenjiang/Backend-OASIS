package com.rubiks.backendoasis.esdocument;

import com.rubiks.backendoasis.entity.paper.MetricsEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.List;

@Data
@Document(indexName = "oasis.large")
public class PaperDocument implements Serializable {
    @Id
    @Field(name = "_id")
    private String id;

    private String title;

    private List<Author> authors;

    @Field(name = "abstract")
    private String _abstract;

    private String publicationTitle;

    private String doi;

    private int publicationYear;

    private MetricsEntity metrics;

    private List<String> keywords;
//    private List<Reference> references;
    private String contentType;

    private String publicationName;

    private String link;
}
