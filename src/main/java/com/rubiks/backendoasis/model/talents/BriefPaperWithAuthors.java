package com.rubiks.backendoasis.model.talents;

import com.rubiks.backendoasis.entity.paper.AuthorEntity;
import com.rubiks.backendoasis.model.paper.AuthorNameId;
import com.rubiks.backendoasis.model.paper.BriefPaper;
import com.rubiks.backendoasis.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BriefPaperWithAuthors implements Serializable {
    @Field("authors")
    private List<AuthorEntity> authors;
    @Field("link")
    private String link;
    @Field("title")
    private String title;

    private String _abstract;
    @Field("publicationYear")
    private int publicationYear;

    public static List<BriefPaper> getBriefPapers(List<BriefPaperWithAuthors> briefPaperWithAuthors) {
        List<BriefPaper> res = new ArrayList<>();
        for (BriefPaperWithAuthors b : briefPaperWithAuthors) {
            List<AuthorEntity> authors = b.getAuthors();
            List<AuthorNameId> briefAuthors = new ArrayList<>();
            for (AuthorEntity authorEntity : authors) {
                briefAuthors.add(new AuthorNameId(authorEntity.getName(), authorEntity.getId()));
            }
            res.add(new BriefPaper(briefAuthors, b.getLink(), b.getTitle(), b.get_abstract(), b.getPublicationYear()));
        }
        return res;
    }
}
