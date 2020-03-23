package com.rubiks.backendoasis.model.rank;

import com.rubiks.backendoasis.model.ResearchInterest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthorRankDetail {
    private List<ResearchInterest> keywords;
    private List<MostInfluentialPapers> mostInfluentialPapers;
    private List<MostRecentPapers> mostRecentPapers;
}
