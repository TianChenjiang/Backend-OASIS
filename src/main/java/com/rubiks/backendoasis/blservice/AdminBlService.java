package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.model.admin.UpdateAuthorParameter;
import com.rubiks.backendoasis.response.BasicResponse;

import java.util.List;

public interface AdminBlService {
    BasicResponse getConferenceInfo(int page, String name);
    BasicResponse getAffiliationInfo(int page, String name);
    BasicResponse getJournalInfo(int page, String name);
    BasicResponse getAuthorInfo(int page, String name);

    BasicResponse mergeAffiliationInfo(String src, String desc);
    BasicResponse updateConferenceInfo(String src, String desc);
    BasicResponse updateJournalInfo(String src, String desc);
    BasicResponse updatePaperInfo(UpdateAuthorParameter parameter);
    BasicResponse mergeAuthorInfo(List<String> src, String desc);

}
