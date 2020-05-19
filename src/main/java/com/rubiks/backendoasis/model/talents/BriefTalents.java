package com.rubiks.backendoasis.model.talents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BriefTalents {
    @Field("author.name")
    private String name;

    @Field("author.affiliation")
    private String affiliation;

    @Field("author.id")
    private String authorId;


    private int citation;

    private int count;

    @Field("papers")
    private List<BriefPaper> papers;
}
