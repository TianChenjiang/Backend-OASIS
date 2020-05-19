package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.TaskBlService;
import com.rubiks.backendoasis.model.task.TaskState;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskBlServiceImpl implements TaskBlService {
    private MongoTemplate mongoTemplate;
    @Autowired
    public TaskBlServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public BasicResponse getCrawlTask() {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.DESC, "start_time"));
        List<TaskState> res = mongoTemplate.find(query, TaskState.class, Constant.TASK_COLLECTION);
        return new BasicResponse(200, "Success", res);
    }
}
