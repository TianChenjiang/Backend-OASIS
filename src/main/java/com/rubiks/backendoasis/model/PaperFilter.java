package com.rubiks.backendoasis.model;

import com.rubiks.backendoasis.esdocument.Author;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaperFilter implements Serializable {
    private List<Author> authors;
    private String contentType;
    private String publicationName;
}
