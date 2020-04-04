package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.PictureBlService;
import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.rubiks.backendoasis.model.picture.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PictureBlServiceImpl implements PictureBlService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BasicResponse getAcademicRelationByAuthorId(String id) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("authors.id").is(id));
        Query query = new Query(criteria);

        List<PaperEntity> paperEntity = mongoTemplate.find(query, PaperEntity.class);
        List<Node> nodes = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Set<String> nodesId = new HashSet<>();
        for (PaperEntity p : paperEntity) {
            for (AuthorEntity authorEntity : p.getAuthors()) {
                nodesId.add(authorEntity.getId());
            }
        }


        return new BasicResponse(200, "Success", paperEntity);
    }

    //共同完成的paper数量
    private int getCooperatePaper(String authorId1, String authorId2) {

        return 0;
    }
}
