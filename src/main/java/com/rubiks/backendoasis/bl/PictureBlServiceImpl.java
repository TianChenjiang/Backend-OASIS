package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.PictureBlService;
import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.response.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
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
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PictureBlServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Cacheable(value = "academic_relation_pic_new")
    @Override
    public BasicResponse getAcademicRelationByAuthorId(String id) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("authors.id").is(id));
        Query query = new Query(criteria);
        query.fields().include("authors");

        List<PaperEntity> paperEntity = mongoTemplate.find(query, PaperEntity.class);
        List<Node> nodes = new ArrayList<>();
        List<Link> links = new ArrayList<>();

        Set<String> nodesId = new HashSet<>();
        String affiliation = "";
        for (PaperEntity p : paperEntity) {
            for (AuthorEntity authorEntity : p.getAuthors()) {
                if (authorEntity.getId()!= null && !authorEntity.getId().equals(id)) {
                    nodesId.add(authorEntity.getId());
                } else {
                    affiliation = authorEntity.getAffiliation();
                }
            }
        }

        nodes.add(getNode(id));  //把中心节点加入
        for (String nodeId : nodesId) {
            nodes.add(getNode(nodeId));
            links.add(new Link(id, nodeId, getValue(paperEntity, affiliation, nodeId)));
        }
        return new BasicResponse(200, "Success", new AcademicRelation(nodes, links));
    }

    private Node getNode(String nodeId) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("authors.id").is(nodeId));
        Query query = new Query(criteria);
        query.fields().include("authors").include("metrics").include("references.order");

        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        String name = "";
        double value = 0.0;
        double impactOfNodeToOthers = 0.0, impactOfOthersToNode = 0.0;
        int count = res.size(), citation = 0;
        if (res.size() > 0) {
             PaperEntity p1 = res.get(0);
            for (AuthorEntity authorEntity : p1.getAuthors()) {
                if (authorEntity.getId() != null && authorEntity.getId().equals(nodeId)) {
                    name = authorEntity.getName();
                    break;
                }
            }
            for (PaperEntity p : res) {
                impactOfNodeToOthers += p.getAuthors().size() * p.getMetrics().getCitationCountPaper();
                impactOfOthersToNode += p.getReferences().size() / p.getAuthors().size();
                citation += p.getMetrics().getCitationCountPaper();
            }
        }

        return  new Node(nodeId, name, count, citation, impactOfNodeToOthers / impactOfOthersToNode);
    }


    //共同完成的paper数量
    //多次交互所用时间比较长
    private long getCooperatePaper(String authorId1, String authorId2) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("authors.id").is(authorId1),
                Criteria.where("authors.id").is(authorId2)
        );
        Query query = new Query(criteria);
        long count = mongoTemplate.count(query, PaperEntity.class);
        return count;
    }

    private long getValue(List<PaperEntity> paperEntities, String affiliation1, String authorId2) {
        long res = 0;
        String affiliation2 = "";
        for (PaperEntity p: paperEntities) {
            for (AuthorEntity a : p.getAuthors()) {
                if (a.getId()!=null && a.getId().equals(authorId2)) {
                    affiliation2 = a.getAffiliation();
                    res++;
                    break;
                }
            }
        }
        if (affiliation2.equals(affiliation1)) {   //二者同在一个机构
            res += 1;
        }
        return res;
    }
}
