package com.rubiks.backendoasis.entity.recommend;

import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = Constant.SIMILAR_AFFILIATION_RECOMMEND)
public class SimilarAffiliation implements Serializable {
    @Id
    private String id;

    private int dist;

    private String s1;

    private String s2;
}
