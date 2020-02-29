package com.rubiks.backendoasis.util.converter;

import com.rubiks.backendoasis.entity.AuthorEntity;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;


public class AuthorReadConvertor implements Converter<Document, AuthorEntity> {

    @Override
    public AuthorEntity convert(Document source) {
        Document author = (Document) source.get("authors");
        String name = author.getString("name");
        String affiliation = author.getString("affiliation");
        String lastName = author.getString("lastName");
        String firstName = author.getString("firstName");
        return new AuthorEntity(name, affiliation, firstName, lastName);
    }
}
