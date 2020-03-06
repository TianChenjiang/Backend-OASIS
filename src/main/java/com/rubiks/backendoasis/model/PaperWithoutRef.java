package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.MetricsEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.entity.ReferenceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class PaperWithoutRef {
    private String id;
    private String title;
    private List<AuthorEntity> author;
    private String _abstract;
    private String publicationTitle;
    private String doi;
    private String publicationYear;
    private MetricsEntity metrics;
    private List<String> keywords;
    private String conferenceName;
    private String link;

    public static List<PaperWithoutRef> PaperToPaperWithoutRef(List<PaperEntity>paperEntities) {
        List<PaperWithoutRef> res = new ArrayList<>();
        for (PaperEntity p : paperEntities) {
            res.add(new PaperWithoutRef(p.getId(),
                    p.getTitle(),
                    p.getAuthor(),
                    p.get_abstract(),
                    p.getPublicationTitle(),
                    p.getDoi(),
                    p.getPublicationYear(),
                    p.getMetrics(),
                    p.getKeywords(),
                    p.getConferenceName(),
                    p.getLink()));
        }
        return res;
    }
}
