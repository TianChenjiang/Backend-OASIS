package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.ReferenceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class BriefReference implements Serializable {
    private String title;
    private String googleScholarLink;

    public BriefReference referencesToBriefRefs(ReferenceEntity referenceEntity) {
        return new BriefReference(referenceEntity.getTitle(), referenceEntity.getGoogleScholarLink());
    }

}

