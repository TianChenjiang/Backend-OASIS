package com.rubiks.backendoasis.model.conference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicationList implements Serializable {
    private List<BriefPublication> list;
    private long size;
}
