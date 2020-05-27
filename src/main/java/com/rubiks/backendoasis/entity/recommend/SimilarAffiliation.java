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
@Document(collection = Constant.SIMILAR_AFFILIATION_RECOMMEND)
public class SimilarAffiliation implements Serializable {
    @Id
    private String id;

    private List<String> affiliations;
}
