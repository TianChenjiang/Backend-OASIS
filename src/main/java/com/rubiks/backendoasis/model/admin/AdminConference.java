package com.rubiks.backendoasis.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class AdminConference implements Serializable {
    private String name;
}
