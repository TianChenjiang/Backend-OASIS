package com.rubiks.backendoasis.entity.paper;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
public class AuthorEntity {
    @Field(name = "name")
    private String name;
    @Field("affiliation")
    private String affiliation;
    @Field("firstName")
    private String firstName;
    @Field("lastName")
    private String lastName;
    @Field("id")
    private String id;

//    public AuthorEntity(String name, String affiliation, String firstName, String lastName) {
//        this.name = name;
//        this.affiliation = affiliation;
//        this.firstName = firstName;
//        this.lastName = lastName;
//    }
}
