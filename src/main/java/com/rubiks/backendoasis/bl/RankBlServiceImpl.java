package com.rubiks.backendoasis.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.blservice.RankBlService;
import com.rubiks.backendoasis.exception.NoSuchYearException;
import com.rubiks.backendoasis.model.AcceptanceCountRank;
import com.rubiks.backendoasis.model.AffiliationRank;
import com.rubiks.backendoasis.model.AuthorRank;
import com.rubiks.backendoasis.model.CitationCountRank;
import com.rubiks.backendoasis.response.BasicResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import static com.rubiks.backendoasis.util.Constant.collectionName;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;

@Service
public class RankBlServiceImpl implements RankBlService {
    private RestHighLevelClient client;
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    public RankBlServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public BasicResponse getAffiliationBasicRanking(String sortKey, int year) {
        // "acceptanceCount"|"citationCount"
        Aggregation aggregation = newAggregation(
                project("authors", "publicationYear", "metrics"),
                match(Criteria.where("authors.affiliation").ne("").ne(null)),  //非空属性
                match(Criteria.where("publicationYear").is(year)),
                unwind("authors"),
                group("authors.affiliation").count().as("acceptanceCount").
                        sum("metrics.citationCountPaper").as("citationCount"),
                sort(Sort.Direction.DESC, sortKey),
                limit(10)
        );

        if (sortKey.equals("acceptanceCount")) {
            AggregationResults<AcceptanceCountRank> res = mongoTemplate.aggregate(aggregation, collectionName, AcceptanceCountRank.class);
            if (res.getMappedResults().size() == 0) {
                throw new NoSuchYearException();
            }
            return new BasicResponse<>(200, "Success", AffiliationRank.transformToBasic(res.getMappedResults()));

        } else if (sortKey.equals("citationCount")) {
            AggregationResults<CitationCountRank> res = mongoTemplate.aggregate(aggregation,collectionName, CitationCountRank.class);
            if (res.getMappedResults().size() == 0) {
                throw  new NoSuchYearException();
            }
            return new BasicResponse<>(200, "Success", AffiliationRank.transformToBasic(res.getMappedResults()));
        } else {

        }
        return null;
    }

    public BasicResponse getAuthorBasicRanking(String sortKey, int year) {
        Aggregation aggregation = newAggregation(
                project("authors", "publicationYear", "metrics"),
                match(Criteria.where("publicationYear").is(year)),
                unwind("authors"),
                group("authors.id").count().as("acceptanceCount").
                        sum("metrics.citationCountPaper").as("citationCount").addToSet("authors.name").as("name"),
                sort(Sort.Direction.DESC, sortKey),
                limit(10)
        );

        if (sortKey.equals("acceptanceCount")) {
            AggregationResults<AcceptanceCountRank> res = mongoTemplate.aggregate(aggregation, collectionName, AcceptanceCountRank.class);
            if (res.getMappedResults().size() == 0) {
                throw new NoSuchYearException();
            }
            return new BasicResponse<>(200, "Success", AuthorRank.transformToBasic(res.getMappedResults()));

        } else if (sortKey.equals("citationCount")) {
            AggregationResults<CitationCountRank> res = mongoTemplate.aggregate(aggregation, collectionName, CitationCountRank.class);
            if (res.getMappedResults().size() == 0) {
                throw  new NoSuchYearException();
            }
            return new BasicResponse<>(200, "Success", AuthorRank.transformToBasic(res.getMappedResults()));
        }
        return null;
    }
}
