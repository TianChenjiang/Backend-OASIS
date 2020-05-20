package com.rubiks.backendoasis.blservice;

import com.rubiks.backendoasis.response.BasicResponse;

import java.util.Date;

public interface TaskBlService {
    BasicResponse getCrawlTask(String filterKey, Date date);
}
