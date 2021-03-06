package com.rubiks.backendoasis.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthorInfo {
    private List<AdminAuthor> authors;
    private long size;
}
