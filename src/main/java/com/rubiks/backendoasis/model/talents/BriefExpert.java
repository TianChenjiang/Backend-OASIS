package com.rubiks.backendoasis.model.talents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BriefExpert {
    @Field("author.id")
    private String authorId;
    @Field("author.name")
    private String authorName;
}
