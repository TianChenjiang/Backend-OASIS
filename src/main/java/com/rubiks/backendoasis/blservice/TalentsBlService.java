package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

public interface TalentsBlService {
    BasicResponse getActiveTalentsBase();
    BasicResponse getTalentsListByTalentBase(String field, int page);
    BasicResponse getTalentsActivePapersByTalentBase(String field);
}
