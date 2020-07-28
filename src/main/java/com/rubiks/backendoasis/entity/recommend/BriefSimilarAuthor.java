package com.rubiks.backendoasis.entity.recommend;

import com.rubiks.backendoasis.entity.paper.AuthorEntity;
import com.rubiks.backendoasis.entity.paper.PaperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BriefSimilarAuthor implements Serializable {
    @Field("author")
    private RecommendAuthor author;

    private List<RecommendPaper> papers;
}
