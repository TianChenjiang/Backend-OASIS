package com.rubiks.backendoasis.entity.recommend;

import com.rubiks.backendoasis.entity.paper.AuthorEntity;
import com.rubiks.backendoasis.entity.paper.MetricsEntity;
import com.rubiks.backendoasis.entity.paper.ReferenceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendPaper {
    @Field("title")
    private String title;

    @Field("authors")
    private List<RecommendAuthor> authors;

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

    @Field("contentType")
    private String contentType;

    @Field("publicationName")
    private String publicationName;

    @Field("link")
    private String link;

    @Field("ieeeId")
    private String ieeeId;
}
