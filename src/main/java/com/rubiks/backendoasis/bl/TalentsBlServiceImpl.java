package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.TalentsBlService;
import com.rubiks.backendoasis.model.talents.ActiveTalents;
import com.rubiks.backendoasis.model.talents.BriefTalents;
import com.rubiks.backendoasis.model.talents.TalentsList;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rubiks.backendoasis.util.Constant.pageSize;

@Service
public class TalentsBlServiceImpl implements TalentsBlService {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TalentsBlServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public BasicResponse getActiveTalentsBase() {
        Criteria criteria = new Criteria();
        Query query = new Query(criteria);
        query.fields().include("field").include("count").slice("experts",0, 5)
                .include("experts.author.id").include("experts.author.name");
        query.with(Sort.by(Sort.Direction.DESC, "count"));
        query.limit(20);

        List<ActiveTalents> res = mongoTemplate.find(query, ActiveTalents.class, Constant.TALENTS_COLLECTION);
        return new BasicResponse(200, "Success", res);
    }

    @Override
    public BasicResponse getTalentsListByTalentBase(String field, int page) {
        Query query = new Query(Criteria.where("field").is(field));
//        query.fields().include("experts.author.name").include("experts.author.affiliation");
        query.fields().include("experts");
        query.with(PageRequest.of(page-1, pageSize));

        TalentsList res = mongoTemplate.findOne(query, TalentsList.class, Constant.TALENTS_COLLECTION);
        if (res == null) {
            return new BasicResponse(200, "No such field", null);
        }
        return new BasicResponse(200, "Success", res.getTalentsList());
    }
}
