package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.TaskBlService;
import com.rubiks.backendoasis.model.task.TaskState;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.Constant;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class TaskBlServiceImpl implements TaskBlService {
    private MongoTemplate mongoTemplate;
    @Autowired
    public TaskBlServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public BasicResponse getCrawlTask(String filterKey, Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,1); //把日期往后增加一天,整数  往后推,负数往前移动

        Query query = new Query(Criteria.where("start_time").gte(date).lte(calendar.getTime()));
        Criteria criteria = new Criteria();
        switch (filterKey) {
            case "recent":
                break;
            case "finished":
                criteria.where("is_finished").is(true);
                break;
            case "processing":
                criteria.where("is_finished").is(false);
                break;
        }
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "start_time"));
        List<TaskState> res = mongoTemplate.find(query, TaskState.class, Constant.TASK_COLLECTION);
        return new BasicResponse(200, "Success", res);
    }
}
