package com.rubiks.backendoasis.model.conference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BriefPublication implements Serializable {
    private String titleId;
    private String publicationTitle;
}
