package com.rubiks.backendoasis.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JournalInfo {
    private List<AdminJournal> journal;
    private long size;
}
