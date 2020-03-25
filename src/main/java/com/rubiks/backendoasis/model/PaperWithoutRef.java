package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.MetricsEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.Author;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperWithoutRef implements Serializable {
    private String id;
    private String title;
    private List<String> authors;
    private String _abstract;
    private String publicationTitle;
    private String doi;
    private int publicationYear;
    private MetricsEntity metrics;
    private List<String> keywords;
    private String contentType;
    private String publicationName;
    private String link;

    public static List<PaperWithoutRef> PaperToPaperWithoutRef(List<PaperEntity>paperEntities) {
        List<PaperWithoutRef> res = new ArrayList<>();
        for (PaperEntity p : paperEntities) {
            List<String> author_names = new ArrayList<>();
            for (AuthorEntity a : p.getAuthors()) {
                author_names.add(a.getName());
            }
            res.add(new PaperWithoutRef(p.getId(),
                    p.getTitle(),
                    author_names,
                    p.get_abstract(),
                    p.getPublicationTitle(),
                    p.getDoi(),
                    p.getPublicationYear(),

                    p.getMetrics(),
                    p.getKeywords(),
                    p.getContentType(),
                    p.getPublicationName(),
                    p.getLink()));
        }
        return res;
    }

    public static List<PaperWithoutRef> PaperDocToPaperWithoutRef(List<PaperDocument> paperdocs) {
        List<PaperWithoutRef> res = new ArrayList<>();
        for (PaperDocument p : paperdocs) {
            List<String> author_names = new ArrayList<>();
            if (p.getAuthors()!=null) {
                for (Author a : p.getAuthors()) {
                    author_names.add(a.getName());
                }
            }
            res.add(new PaperWithoutRef(p.getId(),
                    p.getTitle(),
                    author_names,
                    p.get_abstract(),
                    p.getPublicationTitle(),
                    p.getDoi(),
                    p.getPublicationYear(),
                    p.getMetrics(),
                    p.getKeywords(),
                    p.getContentType(),
                    p.getPublicationName(),
                    p.getLink()));
        }
        return res;
    }
}
