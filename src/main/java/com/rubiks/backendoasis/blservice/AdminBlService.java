package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.model.admin.UpdatePaperParameter;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminBlService {
    BasicResponse importPaperData(MultipartFile file);

    BasicResponse getConferenceInfo(int page, String name);
    BasicResponse getAffiliationInfo(int page, String name);
    BasicResponse getJournalInfo(int page, String name);
    BasicResponse getAuthorInfo(int page, String name);

    BasicResponse mergeAffiliationInfo(List<String> src, String desc);
    BasicResponse updateConferenceInfo(String src, String desc);
    BasicResponse updateJournalInfo(String src, String desc);
    BasicResponse updatePaperInfo(UpdatePaperParameter parameter);
    BasicResponse mergeAuthorInfo(List<String> src, String desc);

    BasicResponse updateMainPageCache();

}
