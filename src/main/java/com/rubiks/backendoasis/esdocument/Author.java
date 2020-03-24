package com.rubiks.backendoasis.esdocument;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Author implements Serializable {
    private String name;
    private String affiliation;
    private String firstName;
    private String lastName;
    private String id;
}
