package com.rubiks.backendoasis.entity.paper;


import com.rubiks.backendoasis.entity.task.ProceedingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "conferences")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ConferenceEntity {
    @Id
    private String id;

    private String publicationTitle;

    private String titleId;

    private List<String> subjects;

    private String titleUrl;

    private List<ProceedingEntity> proceedings;
}







