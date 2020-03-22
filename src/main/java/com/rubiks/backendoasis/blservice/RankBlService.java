package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

public interface RankBlService {
    BasicResponse getAffiliationBasicRanking(String sortKey, int year);
    BasicResponse getAuthorBasicRanking(String sortKey, int year);
    BasicResponse getAuthorAdvancedRanking(String sortKey, int startYear, int endYear);
    BasicResponse getAffiliationAdvancedRanking(String sortKey);
    BasicResponse getAffiliationDetailRankingById(String id);
    BasicResponse getAuthorDetailRankingById(String id);
}
