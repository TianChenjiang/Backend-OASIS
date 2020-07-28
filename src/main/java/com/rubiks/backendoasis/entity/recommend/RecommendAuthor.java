package com.rubiks.backendoasis.entity.recommend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendAuthor {
    private String name;
    @Field("id")
    private String id;
}
