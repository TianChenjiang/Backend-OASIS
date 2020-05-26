package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.paper.PaperEntity;

import com.rubiks.backendoasis.entity.paper.ReferenceEntity;
import com.rubiks.backendoasis.entity.trend.TrendEntity;
import com.rubiks.backendoasis.model.paper.BriefPaper;
import com.rubiks.backendoasis.model.paper.PaperWithoutRef;
import com.rubiks.backendoasis.model.paper.PapersWithSize;
import com.rubiks.backendoasis.model.paper.ResearchInterest;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.Constant;
import org.elasticsearch.client.RestHighLevelClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import static com.rubiks.backendoasis.util.Constant.LARGE_COLLECTION;
import static com.rubiks.backendoasis.util.Constant.pageSize;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort.*;
import java.util.*;


@Service
public class PaperBlServiceImpl implements PaperBlService {
    private RestHighLevelClient client;
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    public PaperBlServiceImpl(){}

    @Autowired
    public PaperBlServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }


    @Override
    public BasicResponse getResearcherInterest(String id) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("authors.id").is(id)),
                project( "keywords", "authors.id")
        );
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, LARGE_COLLECTION, PaperEntity.class);
        List<PaperEntity> aggregationList = aggregationRes.getMappedResults();
        return new BasicResponse<>(200, "Success", ResearchInterest.constructNameValueMap(aggregationList));
    }

    @Override
    public BasicResponse getAffiliationInterest(String affiliation) {
        MatchOperation idMatch =  match(Criteria.where("authors.affiliation").is(affiliation));
        Aggregation aggregation = newAggregation(
                idMatch,
                project( "keywords", "authors.affiliation")
        );
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, LARGE_COLLECTION, PaperEntity.class);
        List<PaperEntity> aggregationList = aggregationRes.getMappedResults();
        return new BasicResponse(200, "Success", ResearchInterest.constructNameValueMap(aggregationList));
    }

    @Override
    public BasicResponse getConferenceInterest(String conference) {
        MatchOperation idMatch =  match(Criteria.where("publicationName").is(conference));
        Aggregation aggregation = newAggregation(
                project("contentType", "keywords", "publicationName"),
                idMatch,
                match(Criteria.where("contentType").is("conferences")),
                project( "keywords")
        );
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, LARGE_COLLECTION, PaperEntity.class);
        List<PaperEntity> aggregationList = aggregationRes.getMappedResults();
        return new BasicResponse(200, "Success", ResearchInterest.constructNameValueMap(aggregationList));
    }

    @Override
    public BasicResponse getJournalInterest(String journal) {
        Aggregation aggregation = newAggregation(
                project("contentType", "keywords", "publicationName"),
                match(Criteria.where("publicationName").is(journal)),
                match(Criteria.where("contentType").is("periodicals")),
                project( "keywords")
        );
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, LARGE_COLLECTION, PaperEntity.class);
        List<PaperEntity> aggregationList = aggregationRes.getMappedResults();
        return new BasicResponse(200, "Success", ResearchInterest.constructNameValueMap(aggregationList));
    }


    @Override
    @Cacheable(value = "active_abstract")
    public BasicResponse getActivePaperAbstract() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("publicationYear").is(2019)),
                sort(Direction.DESC, "metrics.citationCountPaper"),
                limit(5)
        );
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, LARGE_COLLECTION, PaperEntity.class);
        List<PaperEntity> Top5Papers = aggregationRes.getMappedResults();
        return new BasicResponse(200, "Success", BriefPaper.PapersToBriefPapers(Top5Papers));
    }

    @Override
    public BasicResponse getKeyword3DTrend() {
        Query query = new Query(); //直接返回
        TrendEntity res = mongoTemplate.findOne(query, TrendEntity.class, Constant.TREND_COLLECTION);
        return new BasicResponse(200, "Success", res);
    }

    @Override
    public BasicResponse getReferenceById(String paperId) {
        PaperEntity res = mongoTemplate.findById(paperId, PaperEntity.class);
        List<ReferenceEntity> refs = new ArrayList<>();
        if (res != null && res.getReferences() != null) {
            for (ReferenceEntity ref : res.getReferences()) {
                if (ref.getTitle().isEmpty() || ref.getTitle() == null) {
                    continue;
                }
                refs.add(ref);
            }
            return new BasicResponse(200, "Success", refs) ;
        }
        else {
            return new BasicResponse(200, "Success", "no such paper!") ;
        }
    }

    @Override
    public BasicResponse getAuthorPapersById(String authorId, int page, String sortKey) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("authors.id").is(authorId));
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query = getQueryAfterPaginationAndSort(query, sortKey, page);
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
    }

    @Override
    public BasicResponse getAffiliationPapers(String affiliation, int page, String sortKey) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("authors.affiliation").is(affiliation));
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query = getQueryAfterPaginationAndSort(query, sortKey, page);
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
    }

    @Override
    public BasicResponse getKeywordPapers(String keyword, int page, String sortKey) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("keywords").is(keyword));
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query = getQueryAfterPaginationAndSort(query, sortKey, page);
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
    }

    @Override
    public BasicResponse getPaperById(String id) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("id").is(id));
        Query query = new Query(criteria);

        PaperEntity paperEntity = mongoTemplate.findOne(query, PaperEntity.class);
        return new BasicResponse(200, "Success", paperEntity);
    }

    class AuthorKeywordsList {
        private String name;
        private List<String> keywords;
    }

    private Query getQueryAfterPaginationAndSort(Query query, String sortKey, int page) {
        switch (sortKey) {
            case "recent":
                query.with(PageRequest.of(page - 1, pageSize, Sort.by(Direction.DESC, "publicationYear")));
                break;
            case "early":
                query.with(PageRequest.of(page - 1, pageSize, Sort.by(Direction.ASC, "publicationYear")));
                break;
            case "citation":
                query.with(PageRequest.of(page - 1, pageSize, Sort.by(Direction.DESC, "metrics.citationCountPaper")));
                break;
        }
        return query;
    }

}
