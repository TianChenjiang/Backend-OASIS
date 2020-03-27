package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.PaperEntity;

import com.rubiks.backendoasis.model.*;
import com.rubiks.backendoasis.response.BasicResponse;
import org.elasticsearch.client.RestHighLevelClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import static com.rubiks.backendoasis.util.Constant.collectionName;
import static com.rubiks.backendoasis.util.Constant.pageSize;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort.*;
import java.util.*;


@Service
@CacheConfig(cacheNames = "papers")
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
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, collectionName, PaperEntity.class);
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
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, collectionName, PaperEntity.class);
        List<PaperEntity> aggregationList = aggregationRes.getMappedResults();
        return new BasicResponse(200, "Success", ResearchInterest.constructNameValueMap(aggregationList));
    }


    @Override
    @Cacheable()
    public BasicResponse getActivePaperAbstract() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("publicationYear").is(2019)),
                sort(Direction.DESC, "metrics.citationCountPaper"),
                limit(5)
        );
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, collectionName, PaperEntity.class);
        List<PaperEntity> Top5Papers = aggregationRes.getMappedResults();
        return new BasicResponse(200, "Success", BriefPaper.PapersToBriefPapers(Top5Papers));
    }

    @Override
    public BasicResponse getReferenceById(String paperId) {
        PaperEntity res = mongoTemplate.findById(paperId, PaperEntity.class);
        return new BasicResponse(200, "Success", res.getReferences());
    }

    @Override
    public BasicResponse getAuthorPapersById(String authorId, int page, String sortKey) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("authors.id").is(authorId));
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

       if (sortKey.equals("recent")) {
            query.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "publicationYear")));
        }
        else if (sortKey.equals("early")) {
            query.with(PageRequest.of(page-1, pageSize, Sort.by(Direction.ASC, "publicationYear")));
        }
        else if (sortKey.equals("citation")) {
            query.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "metrics.citationCountPaper")));
        }

        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
    }

    @Override
    public BasicResponse getAffiliationPapers(String affiliation, int page, String sortKey) {
        Criteria criteria = new Criteria();
        criteria.andOperator(criteria.where("authors.affiliation").is(affiliation));
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        if (sortKey.equals("recent")) {
            query.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "publicationYear")));
        }
        else if (sortKey.equals("early")) {
            query.with(PageRequest.of(page-1, pageSize, Sort.by(Direction.ASC, "publicationYear")));
        }
        else if (sortKey.equals("citation")) {
            query.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "metrics.citationCountPaper")));
        }

        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
    }

    class AuthorKeywordsList {
        private String name;
        private List<String> keywords;
    }

}
