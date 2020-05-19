package com.rubiks.backendoasis.model.paper;

import com.rubiks.backendoasis.entity.paper.ReferenceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BriefReference implements Serializable {
    private String title;
    private String googleScholarLink;

    public BriefReference referencesToBriefRefs(ReferenceEntity referenceEntity) {
        return new BriefReference(referenceEntity.getTitle(), referenceEntity.getGoogleScholarLink());
    }

}

