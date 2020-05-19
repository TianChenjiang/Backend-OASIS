package com.rubiks.backendoasis.model.paper;

import com.rubiks.backendoasis.entity.paper.AuthorEntity;
import com.rubiks.backendoasis.entity.paper.PaperEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BriefPaper implements Serializable {
    private List<AuthorNameId> authors;
    private String link;
    private String title;
    private String _abstract;
    private int publicationYear;

    public static List<BriefPaper> PapersToBriefPapers(List<PaperEntity> paperEntities) {
        List<BriefPaper> res = new ArrayList<>();
        for (PaperEntity p : paperEntities) {
            List<AuthorNameId> author_names = new ArrayList<>();
            for (AuthorEntity a : p.getAuthors()) {
                author_names.add(new AuthorNameId(a.getName(), a.getId()));
            }
            res.add(new BriefPaper(author_names, p.getLink(), p.getTitle(), p.get_abstract(), p.getPublicationYear()));
        }
        return res;
    }
}
