package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DataSourceBlService {
    BasicResponse importPaperData(MultipartFile file);
}
