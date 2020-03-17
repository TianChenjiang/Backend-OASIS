package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BriefPaper {
    private List<String> authors;
    private String link;
    private String title;
    private String _abstract;
    private int publicationYear;

    public static List<BriefPaper> PapersToBriefPapers(List<PaperEntity> paperEntities) {
        List<BriefPaper> res = new ArrayList<>();
        for (PaperEntity p : paperEntities) {
            List<String> author_names = new ArrayList<>();
            for (AuthorEntity a : p.getAuthors()) {
                author_names.add(a.getName());
            }
            res.add(new BriefPaper(author_names, p.getLink(), p.getTitle(), p.get_abstract(), p.getPublicationYear()));
        }
        return res;
    }
}
