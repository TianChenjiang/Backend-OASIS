package com.rubiks.backendoasis.model.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class AffiliationInfo {
    private List<AdminAffiliation> affiliations;
    private long size;

}
