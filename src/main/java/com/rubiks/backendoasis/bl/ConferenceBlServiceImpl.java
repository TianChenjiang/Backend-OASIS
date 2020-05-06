package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.ConferenceBlService;
import com.rubiks.backendoasis.entity.ConferenceEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.model.conference.BriefPublication;
import com.rubiks.backendoasis.model.conference.PublicationList;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.rubiks.backendoasis.util.Constant.pageSize;

@Service
public class ConferenceBlServiceImpl implements ConferenceBlService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BasicResponse getConferencesAndJournalsList(String keyword, int page) {
        Criteria criteria = Criteria.where("publicationTitle").regex(keyword);
        Query query = new Query(criteria);
        query.fields().include("titleId").include("publicationTitle");

        long size = mongoTemplate.count(query, PaperEntity.class);

        query.with(PageRequest.of(page-1, pageSize));
        List<ConferenceEntity> res = mongoTemplate.find(query, ConferenceEntity.class);

        List<BriefPublication> list = new ArrayList<>();
        for (ConferenceEntity conf : res) {
            list.add(new BriefPublication(conf.getTitleId(), conf.getPublicationTitle()));
        }

        return new BasicResponse(200, "Success", new PublicationList(list, size));
    }

    @Override
    public BasicResponse getConferencesAndJournalsProceedings(String titleId) {
        Query query = new Query(Criteria.where("titleId").is(titleId));
        query.fields().include("proceedings");

        ConferenceEntity res = mongoTemplate.findOne(query, ConferenceEntity.class);
        return new BasicResponse(200, "Success", res.getProceedings());
    }
}








