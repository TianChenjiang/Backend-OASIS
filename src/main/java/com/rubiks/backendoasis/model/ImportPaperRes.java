package com.rubiks.backendoasis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ImportPaperRes implements Serializable {
    private int increasedCount;
}
