package com.rubiks.backendoasis.entity.recommend;

import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = Constant.SIMILAR_AUTHOR_RECOMMEND)
public class SimilarAuthor implements Serializable {
    @Id
    private String id;

    private List<BriefSimilarAuthor> authors;

//    private BriefSimilarAuthor author1;
//
//    private BriefSimilarAuthor author2;
//
//    private List<String> co_author;
//
//    private int co_author_num;
}
