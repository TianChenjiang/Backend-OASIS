package com.rubiks.backendoasis.model.talents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BriefPaper {
    @Field("title")
    private String title;
    @Field("metrics.citationCountPaper")
    private int citation;
    @Field("link")
    private String link;
}
