package com.rubiks.backendoasis.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.rubiks.backendoasis.blservice.RankBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.exception.NoSuchYearException;
import com.rubiks.backendoasis.model.*;
import com.rubiks.backendoasis.model.rank.*;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.Counter;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.rubiks.backendoasis.util.Constant.collectionName;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;

@Service
@CacheConfig(cacheNames = "rank")
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
    @Cacheable(value = "affiliation")
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
    @Cacheable(value = "author")
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
    public BasicResponse getAuthorAdvancedRanking(String sortKey, int startYear, int endYear) { //TODO 当数据过大，会超过group的表示范围
        if (sortKey.equals("citationCount")) {
            sortKey = "citation";
        } else if(sortKey.equals("acceptanceCount")) {
            sortKey = "count";
        }

        //选出符合条件的author
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
                limit(20)
        );
        AggregationResults<AuthorAdvanceRank> firstRes = mongoTemplate.aggregate(aggregation, collectionName, AuthorAdvanceRank.class);

        //计算author对应的十年 publicaitonTrend
//        int curYear = Calendar.getInstance().get(Calendar.YEAR);
//        for (AuthorAdvanceRank advance : firstRes) {
//            String auId = advance.getAuthorId();
//            Aggregation aggregation1 = newAggregation(
//                    match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)),  //过去十年
//                    match(Criteria.where("authors.id").is(auId)),
//                    sort(Sort.Direction.ASC, "publicationYear"),
//                    group("publicationYear").count().as("num").addToSet("publicationYear").as("publicationYear")
//            );
//            AggregationResults<PublicationTrend> curRes = mongoTemplate.aggregate(aggregation1, collectionName, PublicationTrend.class);
//            List<Integer> publicationTrends = new ArrayList<>();
//
//            List<PublicationTrend> yearNumMap = curRes.getMappedResults();
//            for (int i = curYear-9; i <= curYear; i++) {
//                publicationTrends.add(PublicationTrend.getNumOfYear(yearNumMap, i)); //得到年份对应的num
//            }
//            advance.setPublicationTrend(publicationTrends);
//        }


        // 采用先读取全部符合条件的数据，然后在服务端过滤和reduce
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        Aggregation aggregation1 = newAggregation(
                match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)), //过去十年
                unwind("authors"),
                project().and("authors.id").as("authorId").and("publicationYear").as("year")
        );
        List<IdYearMap> curRes = mongoTemplate.aggregate(aggregation1, collectionName, IdYearMap.class).getMappedResults();

        for (AuthorAdvanceRank authorAdvanceRank : firstRes.getMappedResults()) {
            String curId = authorAdvanceRank.getAuthorId();
            List<Integer> publicationTrends = new ArrayList<>(Collections.nCopies(10, 0));

            int low  = curYear-9;
            for (IdYearMap idYearMap : curRes) {
                if (idYearMap.getAuthorId() == null) {
                    continue;  //有authorId为空的情况
                }
                else if (idYearMap.getAuthorId().equals(curId)) {
                    int index = idYearMap.getYear()-low;
                    int origin = publicationTrends.get(index);
                    publicationTrends.set(index, ++origin);
                }
            }

            authorAdvanceRank.setPublicationTrend(publicationTrends);
        }

        return new BasicResponse(200, "Success", firstRes.getMappedResults());
    }


    @Override
    public AuthorRankDetail getAuthorDetailRankingById(String id) {
        MatchOperation idMatch =  match(Criteria.where("authors.id").is(id));
        Aggregation mostInfluentialAgg = newAggregation(
                idMatch,
                unwind("authors"),
                group("authors.id").
                        sum("metrics.citationCountPaper").as("citation").addToSet("publicationYear").as("publicationYear")
                        .addToSet("title").as("title").addToSet("publicationName")
                        .as("publicationName").addToSet("link").as("link"),
                sort(Sort.Direction.DESC, "citation"),
                limit(5)
        );
        AggregationResults<MostInfluentialPapers> mostInfluentialPapers = mongoTemplate.aggregate(mostInfluentialAgg, collectionName, MostInfluentialPapers.class);

        Aggregation mostRecentAgg = newAggregation(
                idMatch,
                unwind("authors"),
                group("authors.id")
                        .addToSet("publicationYear").as("publicationYear")
                        .addToSet("title").as("title").addToSet("publicationName")
                        .as("publicationName").addToSet("link").as("link"),
                sort(Sort.Direction.DESC, "publicationYear"),
                limit(5)
        );
        AggregationResults<MostRecentPapers> mostRecentPapers = mongoTemplate.aggregate(mostRecentAgg, collectionName, MostRecentPapers.class);

        return new AuthorRankDetail(null, mostInfluentialPapers.getMappedResults(), mostRecentPapers.getMappedResults());
    }

    @Override
    public BasicResponse getAffiliationAdvancedRanking(String sortKey, int startYear, int endYear) {
        if (sortKey.equals("citationCount")) {
            sortKey = "citation";
        } else if(sortKey.equals("acceptanceCount")) {
            sortKey = "count";
        }

        Aggregation aggregation = newAggregation(
                match(Criteria.where("authors.affiliation").ne("")),  //非空属性
                match(Criteria.where("publicationYear").gte(startYear).lte(endYear)),
                unwind("authors"),
                group("authors.affiliation").count().as("count")
                        .sum("metrics.citationCountPaper").as("citation")
                        .addToSet("authors.affiliation").as("affiliationName")
                        .addToSet("authors.id").as("authorId"),
                sort(Sort.Direction.DESC, sortKey),
                limit(20)
        );
        AggregationResults<BasicDBObject> res = mongoTemplate.aggregate(aggregation, collectionName, BasicDBObject.class);
        List<AffiliationAdvanceRank> affiliationAdvanceRanks = new ArrayList<>();
        for (Iterator<BasicDBObject> iterator = res.iterator(); iterator.hasNext();) {
            BasicDBObject obj = iterator.next();
            String affiliationName = obj.getString("affiliationName");
            int  count = obj.getInt("count");
            int citation = obj.getInt("citation");
            List<String> authorIds = (List<String>)(obj.get("authorId"));
            affiliationAdvanceRanks.add(new AffiliationAdvanceRank(affiliationName, count, citation, Counter.getCount(authorIds)));
        }
        return new BasicResponse(200, "Success", affiliationAdvanceRanks);
    }


    @Override
    public BasicResponse getAffiliationDetailRankingById(String id) {
        MatchOperation idMatch =  match(Criteria.where("authors.affiliation").is(id));
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        Aggregation aggregation1 = newAggregation(
                match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)),  //过去十年
                idMatch,
                sort(Sort.Direction.ASC, "publicationYear"),
                group("publicationYear").count().as("num").addToSet("publicationYear").as("publicationYear")
        );
        AggregationResults<PublicationTrend> curRes = mongoTemplate.aggregate(aggregation1, collectionName, PublicationTrend.class);
        List<Integer> publicationTrends = new ArrayList<>();

        List<PublicationTrend> yearNumMap = curRes.getMappedResults();
        for (int i = curYear-9; i <= curYear; i++) {
            publicationTrends.add(PublicationTrend.getNumOfYear(yearNumMap, i)); //得到年份对应的num
        }

        // 计算keywords
        Aggregation aggregation = newAggregation(
                idMatch,
                project( "keywords", "authors.affiliation")
        );
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, collectionName, PaperEntity.class);
        List<PaperEntity> aggregationList = aggregationRes.getMappedResults();

        return new BasicResponse(200, "Success", new AffiliationRankDetail(publicationTrends, ResearchInterest.constructNameValueMap(aggregationList)));
    }

    @Override
    public BasicResponse getAuthorDetailRanking(String affiliation) {
        MatchOperation affMatch = match(Criteria.where("authors.affiliation").is(affiliation));
        Aggregation aggregation = newAggregation(
                project("authors", "publicationYear", "metrics"),
                affMatch,
//                match(Criteria.where("authors.id").ne(null)),
                unwind("authors"),
                group("authors.id").count().as("count").
                        sum("metrics.citationCountPaper").as("citation").addToSet("authors.name").as("authorName")
                        .addToSet("authors.id").as("authorId")
        );
        AggregationResults<AuthorAdvanceRank> firstRes = mongoTemplate.aggregate(aggregation, collectionName, AuthorAdvanceRank.class);
        List<AuthorAdvanceRank> res = firstRes.getMappedResults();

        //计算author对应的十年 publicationTrend
        //换了一种实现，直接group
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        Aggregation aggregation1 = newAggregation(
                affMatch,
                match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)), //过去十年
                unwind("authors"),
//                group("authors.id", "publicationYear").count().as("num")
//                        .addToSet("publicationYear").as("publicationYear")
//                        .addToSet("authors.id").as("authorId")
//                sort(Sort.Direction.ASC, "authors.id"),
                project().and("authors.id").as("authorId").and("publicationYear").as("year")
        );
        List<IdYearMap> curRes = mongoTemplate.aggregate(aggregation1, collectionName, IdYearMap.class).getMappedResults();

        for (AuthorAdvanceRank authorAdvanceRank : res) {
            String curId = authorAdvanceRank.getAuthorId();
            List<Integer> publicationTrends = new ArrayList<>(Collections.nCopies(10, 0));

            int low  = curYear-9;
            for (IdYearMap idYearMap : curRes) {
                if (idYearMap.getAuthorId() == null) {
                    continue;  //有authorId为空的情况
                }
                else if (idYearMap.getAuthorId().equals(curId)) {
                    int index = idYearMap.getYear()-low;
                    int origin = publicationTrends.get(index);
                    publicationTrends.set(index, ++origin);
                }
            }

            authorAdvanceRank.setPublicationTrend(publicationTrends);
        }

        return new BasicResponse(200, "Success", res);
    }

}
