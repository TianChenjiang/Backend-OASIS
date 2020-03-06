package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.Author;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class BriefPaper {
    private List<AuthorEntity> authors;
    private String link;
    private String title;
    private String _abstract;
    private String publicationYear;

    public static List<BriefPaper> PapersToBriefPapers(List<PaperEntity> paperEntities) {
        List<BriefPaper> res = new ArrayList<>();
        for (PaperEntity p : paperEntities) {
            res.add(new BriefPaper(p.getAuthor(), p.getLink(), p.getTitle(), p.get_abstract(), p.getPublicationYear()));
        }
        return res;
    }
}
