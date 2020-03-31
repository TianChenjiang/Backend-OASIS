package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.model.rank.AuthorRankDetail;
import com.rubiks.backendoasis.response.BasicResponse;

public interface RankBlService {
    BasicResponse getAffiliationBasicRanking(String sortKey, int year);
    BasicResponse getAuthorBasicRanking(String sortKey, int year);
    BasicResponse getJournalBasicRanking(String sortKey, int year);
    BasicResponse getConferenceBasicRanking(String sortKey, int year);
    BasicResponse getKeywordBasicRanking(int year);

    BasicResponse getAuthorAdvancedRanking(String sortKey, int startYear, int endYear);
    BasicResponse getAffiliationAdvancedRanking(String sortKey, int startYear, int endYear);

    BasicResponse getAffiliationDetailRankingById(String id);
    BasicResponse getAuthorDetailRanking(String affiliation);
    AuthorRankDetail getAuthorDetailRankingById(String id);
}
