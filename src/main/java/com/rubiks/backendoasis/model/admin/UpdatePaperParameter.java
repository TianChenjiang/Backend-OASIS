package com.rubiks.backendoasis.model.admin;

import com.rubiks.backendoasis.entity.paper.MetricsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePaperParameter {
    private String id;
    private String title;
    private List<String> authors;
    private String _abstract;
    private int publicationYear;
    private MetricsEntity metrics;
    private List<String> keywords;
    private String contentType;
    private String publicationName;
    private String link;

}
