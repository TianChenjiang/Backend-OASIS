package com.rubiks.backendoasis.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.rubiks.backendoasis.blservice.RankBlService;
import com.rubiks.backendoasis.esdocument.Author;
import com.rubiks.backendoasis.exception.NoSuchYearException;
import com.rubiks.backendoasis.model.*;
import com.rubiks.backendoasis.response.BasicResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
                match(Criteria.where("authors.affiliation").ne("")),  //非空属性
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

    @Override
    public BasicResponse getAuthorBasicRanking(String sortKey, int year) {
        Aggregation aggregation = newAggregation(
                project("authors", "publicationYear", "metrics"),
                match(Criteria.where("publicationYear").is(year)),
                match(Criteria.where("authors.id").ne(null)),
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

    @Override
    public BasicResponse getAuthorAdvancedRanking(String sortKey, int startYear, int endYear) {
        if (sortKey.equals("citationCount")) {
            sortKey = "citation";
        } else if(sortKey.equals("acceptanceCount")) {
            sortKey = "count";
        }

        MatchOperation yearOperation = match(Criteria.where("publicationYear").gte(startYear).lte(endYear));
        Aggregation aggregation = newAggregation(
                project("authors", "publicationYear", "metrics"),
                yearOperation,
                match(Criteria.where("authors.id").ne(null)),
                unwind("authors"),
                group("authors.id").count().as("count").
                        sum("metrics.citationCountPaper").as("citation").addToSet("authors.name").as("authorName")
                        .addToSet("authors.id").as("authorId"),
                sort(Sort.Direction.DESC, sortKey),
                limit(10)
        );
        AggregationResults<AuthorAdvanceRank> firstRes = mongoTemplate.aggregate(aggregation, collectionName, AuthorAdvanceRank.class);


        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (AuthorAdvanceRank advance : firstRes) {
            String auId = advance.getAuthorId();
            Aggregation aggregation1 = newAggregation(
                    match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)),  //过去十年
                    match(Criteria.where("authors.id").is(auId)),
                    sort(Sort.Direction.ASC, "publicationYear"),
                    group("publicationYear").count().as("num").addToSet("publicationYear").as("publicationYear")
            );
            AggregationResults<PublicationTrend> curRes = mongoTemplate.aggregate(aggregation1, collectionName, PublicationTrend.class);
            List<Integer> publicationTrends = new ArrayList<>();

            List<PublicationTrend> yearNumMap = curRes.getMappedResults();
            for (int i = curYear-9; i <= curYear; i++) {
                publicationTrends.add(PublicationTrend.getNumOfYear(yearNumMap, i)); //得到年份对应的num
            }
            advance.setPublicationTrend(publicationTrends);
        }

        return new BasicResponse(200, "Success", firstRes.getMappedResults());
    }

    @Override
    public BasicResponse getAffiliationAdvancedRanking(String sortKey) {
        return null;
    }


    @Override
    public BasicResponse getAffiliationDetailRankingById(String id) {
        return null;
    }

    @Override
    public BasicResponse getAuthorDetailRankingById(String id) {
        return null;
    }


}
