package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

public interface RankBlService {
    BasicResponse getAffiliationBasicRanking(String sortKey, int year);
    BasicResponse getAuthorBasicRanking(String sortKey, int year);
}
