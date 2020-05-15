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
import org.springframework.data.mongodb.core.query.Query;
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
    @Cacheable(value = "affiliation_rank")
    public BasicResponse getAffiliationBasicRanking(String sortKey, int year) {
        // "acceptanceCount"|"citationCount"
        Aggregation aggregation = newAggregation(
                project("authors", "publicationYear", "metrics", "_id"),
                match(Criteria.where("publicationYear").is(year)),
                unwind("authors"),
                match(Criteria.where("authors.affiliation").ne("").ne(null)),  //非空属性
                group( "authors.affiliation").count().as("acceptanceCount").
                        sum("metrics.citationCountPaper").as("citationCount"),
//                        .addToSet("authors.affiliation").as("id"),
                sort(Sort.Direction.DESC, sortKey),
                limit(10)
        );

        return getSortRes(sortKey, aggregation);

    }

    @Override
    @Cacheable(value = "author_rank")
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
                throw new NoSuchYearException();
            }
            return new BasicResponse<>(200, "Success", AuthorRank.transformToBasic(res.getMappedResults()));
        }
        return null;
    }

    @Override
    @Cacheable(value = "journal_rank")
    public BasicResponse getJournalBasicRanking(String sortKey, int year) {
        Aggregation aggregation = newAggregation(
                project("publicationName", "publicationYear", "metrics", "contentType"),
                match(Criteria.where("publicationYear").is(year)),
                match(Criteria.where("contentType").is("periodicals")),
                group("publicationName").count().as("acceptanceCount").
                        sum("metrics.citationCountPaper").as("citationCount"),
                sort(Sort.Direction.DESC, sortKey),
                limit(10)
        );
        return getSortRes(sortKey, aggregation);
    }

    @Override
    @Cacheable(value = "conference_rank")
    public BasicResponse getConferenceBasicRanking(String sortKey, int year) {
        Aggregation aggregation = newAggregation(
                project("publicationName", "publicationYear", "metrics", "contentType"),
                match(Criteria.where("publicationYear").is(year)),
                match(Criteria.where("contentType").is("conferences")),
                group("publicationName").count().as("acceptanceCount").
                        sum("metrics.citationCountPaper").as("citationCount"),
                sort(Sort.Direction.DESC, sortKey),
                limit(10)
        );
        return getSortRes(sortKey, aggregation);
    }

    @Override
    @Cacheable(value = "keyword_rank")
    public BasicResponse getKeywordBasicRanking(int year) {
        Aggregation aggregation = newAggregation(
                project("keywords", "publicationYear", "metrics"),
                match(Criteria.where("publicationYear").is(year)),
                unwind("keywords"),
                group("keywords").count().as("acceptanceCount"),
                sort(Sort.Direction.DESC, "acceptanceCount"),
                limit(10)
        );
        AggregationResults<AcceptanceCountRank> res = mongoTemplate.aggregate(aggregation, collectionName, AcceptanceCountRank.class);
        if (res.getMappedResults().size() == 0) {
            throw new NoSuchYearException();
        }
        return new BasicResponse<>(200, "Success", BasicRank.transformToBasic(res.getMappedResults()));
    }


    private <T> BasicResponse getSortRes(String sortKey, Aggregation aggregation) {
        if (sortKey.equals("acceptanceCount")) {
            AggregationResults<AcceptanceCountRank> res = mongoTemplate.aggregate(aggregation, collectionName, AcceptanceCountRank.class);
            if (res.getMappedResults().size() == 0) {
                throw new NoSuchYearException();
            }
            return new BasicResponse<>(200, "Success", BasicRank.transformToBasic(res.getMappedResults()));

        } else if (sortKey.equals("citationCount")) {
            AggregationResults<CitationCountRank> res = mongoTemplate.aggregate(aggregation, collectionName, CitationCountRank.class);
            if (res.getMappedResults().size() == 0) {
                throw  new NoSuchYearException();
            }
            return new BasicResponse<>(200, "Success", BasicRank.transformToBasic(res.getMappedResults()));
        }
        else return null;
    }


    @Override
    @Cacheable(value = "author_advance_rank")
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
                project("authorId", "citation", "authorName", "count"),
                sort(Sort.Direction.DESC, sortKey),
                limit(20)
        );
        List<AuthorAdvanceRank> res = mongoTemplate.aggregate(aggregation, collectionName, AuthorAdvanceRank.class).getMappedResults();

        List<String> ids = new ArrayList<>();
        for (AuthorAdvanceRank authorAdvanceRank : res) {
            ids.add(authorAdvanceRank.getAuthorId());
        }

        // 采用先读取全部符合条件的数据，然后在服务端过滤和reduce
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        MatchOperation idMatch = match(Criteria.where("authors.id").in(ids));
        Aggregation aggregation1 = newAggregation(
                project("publicationYear", "authors"),
                idMatch,
                match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)), //过去十年
                unwind("authors"),
                idMatch,
                project().and("authors.id").as("authorId").and("publicationYear").as("year")
        );
        List<IdYearMap> curRes = mongoTemplate.aggregate(aggregation1, collectionName, IdYearMap.class).getMappedResults();

        res = CalAndSetPublicationTrends(res, curRes);

        return new BasicResponse(200, "Success", res);
    }


    @Override
    public AuthorRankDetail getAuthorDetailRankingById(String id) {
        MatchOperation idMatch =  match(Criteria.where("authors.id").is(id));
        Aggregation mostInfluentialAgg = newAggregation(
                idMatch,
                sort(Sort.Direction.DESC, "metrics.citationCountPaper"),
                limit(5)
        );
        AggregationResults<MostInfluentialPapers> mostInfluentialPapers = mongoTemplate.aggregate(mostInfluentialAgg, collectionName, MostInfluentialPapers.class);

        Aggregation mostRecentAgg = newAggregation(
                idMatch,
                sort(Sort.Direction.DESC, "publicationYear"),
                limit(5)
        );
        AggregationResults<MostRecentPapers> mostRecentPapers = mongoTemplate.aggregate(mostRecentAgg, collectionName, MostRecentPapers.class);

        return new AuthorRankDetail(null, mostInfluentialPapers.getMappedResults(), mostRecentPapers.getMappedResults());
    }

    @Override
    public BasicResponse getKeywordDetailRankingById(String keyword) {
        // 采用先读取全部符合条件的数据，然后在服务端过滤和reduce
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        MatchOperation idMatch = match(Criteria.where("keywords").is(keyword));
        Aggregation aggregation1 = newAggregation(
                project("publicationYear", "authors", "keywords"),
                idMatch,
                match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)), //过去十年
                unwind("keywords"),
                idMatch,
                project().and("keywords").as("authorId").and("publicationYear").as("year")  //这里为了复用IdYearMap，把keyword起名为authorId，它们都是group的对象
        );
        List<IdYearMap> curRes = mongoTemplate.aggregate(aggregation1, collectionName, IdYearMap.class).getMappedResults();

        KeywordDetailRank res = KeywordDetailRank.CalAndSetPublicationTrends(keyword, curRes);
        Aggregation mostInfluentialAgg = newAggregation(
                match(Criteria.where("keywords").is(keyword)),
                sort(Sort.Direction.DESC, "metrics.citationCountPaper"),
                limit(5)
        );
        AggregationResults<MostInfluentialPapers> mostInfluentialPapers = mongoTemplate.aggregate(mostInfluentialAgg, collectionName, MostInfluentialPapers.class);
        res.setMostInfluentialPapers(mostInfluentialPapers.getMappedResults());

        return new BasicResponse(200, "Success", res);
    }

    @Override
    public BasicResponse getAuthorDetailRankingByKeyword(String keyword, String sortKey, int startYear, int endYear) {
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
                project("authorId", "citation", "authorName", "count"),
                sort(Sort.Direction.DESC, sortKey),
                limit(20)
        );
        List<AuthorAdvanceRank> res = mongoTemplate.aggregate(aggregation, collectionName, AuthorAdvanceRank.class).getMappedResults();

        List<String> ids = new ArrayList<>();
        for (AuthorAdvanceRank authorAdvanceRank : res) {
            ids.add(authorAdvanceRank.getAuthorId());
        }

        // 采用先读取全部符合条件的数据，然后在服务端过滤和reduce
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        MatchOperation idMatch = match(Criteria.where("authors.id").in(ids));
        Aggregation aggregation1 = newAggregation(
                project("publicationYear", "authors"),
                idMatch,
                match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)), //过去十年
                unwind("authors"),
                idMatch,
                project().and("authors.id").as("authorId").and("publicationYear").as("year")
        );
        List<IdYearMap> curRes = mongoTemplate.aggregate(aggregation1, collectionName, IdYearMap.class).getMappedResults();

        res = CalAndSetPublicationTrends(res, curRes);

        return new BasicResponse(200, "Success", res);
    }

    @Override
    public BasicResponse getAffiliationDetailRankingByKeyword(String keyword, String sortKey, int startYear, int endYear) {
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
            affiliationName = affiliationName.substring(1, affiliationName.length()-1);
            int  count = obj.getInt("count");
            int citation = obj.getInt("citation");
            List<String> authorIds = (List<String>)(obj.get("authorId"));
            affiliationAdvanceRanks.add(new AffiliationAdvanceRank(affiliationName, count, citation, Counter.getCount(authorIds)));
        }
        return new BasicResponse(200, "Success", affiliationAdvanceRanks);
    }

    @Override
    @Cacheable(value = "affiliation_advance_rank")
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
                match(Criteria.where("authors.affiliation").ne("").ne(null)),
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
            affiliationName = affiliationName.substring(1, affiliationName.length()-1);
            int  count = obj.getInt("count");
            int citation = obj.getInt("citation");
            List<String> authorIds = (List<String>)(obj.get("authorId"));
            affiliationAdvanceRanks.add(new AffiliationAdvanceRank(affiliationName, count, citation, Counter.getCount(authorIds)));
        }
        return new BasicResponse(200, "Success", affiliationAdvanceRanks);
    }

    @Override
    public BasicResponse getKeywordAdvancedRanking(String sortKey, int startYear, int endYear) {
        if (sortKey.equals("citationCount")) {
            sortKey = "citation";
        } else if(sortKey.equals("acceptanceCount")) {
            sortKey = "count";
        }

        Aggregation aggregation = newAggregation(
                project("keywords", "metrics", "publicationYear").and(ArrayOperators.Size.lengthOfArray(ConditionalOperators.ifNull("authors").then(Collections.emptyList()))).as("authorNum"),
                match(Criteria.where("publicationYear").gte(startYear).lte(endYear)),
                unwind("keywords"),
                group("keywords").count().as("count")
                        .sum("metrics.citationCountPaper").as("citation")
                        .addToSet("keywords").as("keyword")
                        .sum("authorNum").as("authorNum"),
                sort(Sort.Direction.DESC, sortKey),
                limit(20)
        );

        List<KeywordAdvanceRank> res = mongoTemplate.aggregate(aggregation, collectionName, KeywordAdvanceRank.class).getMappedResults();

        List<String> ids = new ArrayList<>();
        for (KeywordAdvanceRank keywordAdvanceRank : res) {
            ids.add(keywordAdvanceRank.getKeyword());
        }

        return new BasicResponse(200, "Success", res);
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
                project("authors", "publicationYear", "metrics"), //投影，减少网络传输时间
                affMatch,  //提前match，减少进入管道的数据量
                unwind("authors"),
                affMatch,
                group("authors.id").count().as("count").
                        sum("metrics.citationCountPaper").as("citation").addToSet("authors.name").as("authorName")
                        .addToSet("authors.id").as("authorId")
        );
        AggregationResults<AuthorAdvanceRank> firstRes = mongoTemplate.aggregate(aggregation, collectionName, AuthorAdvanceRank.class);
        List<AuthorAdvanceRank> res = firstRes.getMappedResults();

        List<String> ids = new ArrayList<>();
        for (AuthorAdvanceRank authorAdvanceRank : res) {
            ids.add(authorAdvanceRank.getAuthorId());
        }

        //计算author对应的十年 publicationTrend
        //换了一种实现，直接group
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        MatchOperation idMatch = match(Criteria.where("authors.id").in(ids));
        Aggregation aggregation1 = newAggregation(
//                idMatch,
                affMatch,
                match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)), //过去十年
                unwind("authors"),
                idMatch,
                affMatch,
                project().and("authors.id").as("authorId").and("publicationYear").as("year")
        );
        List<IdYearMap> curRes = mongoTemplate.aggregate(aggregation1, collectionName, IdYearMap.class).getMappedResults();

        res = CalAndSetPublicationTrends(res, curRes);

        return new BasicResponse(200, "Success", res);
    }

    private List<AuthorAdvanceRank> CalAndSetPublicationTrends(List<AuthorAdvanceRank> res, List<IdYearMap> curRes) {
        int curYear = Calendar.getInstance().get(Calendar.YEAR);
        for (AuthorAdvanceRank authorAdvanceRank : res) {
            String curId = authorAdvanceRank.getAuthorId();
            List<Integer> publicationTrends = new ArrayList<>(Collections.nCopies(10, 0));

            int low  = curYear-9;
            for (IdYearMap idYearMap : curRes) {
                if (idYearMap.getAuthorId() == null) {
                    continue;  //有authorId为空的情况
                }
                else if (idYearMap.getAuthorId().equals(curId)) {
                    int index = idYearMap.getYear()-low;   //数组中应该存放的位置
                    int origin = publicationTrends.get(index);
                    publicationTrends.set(index, ++origin);
                }
            }

            authorAdvanceRank.setPublicationTrend(publicationTrends);
        }
        return res;
    }

}
