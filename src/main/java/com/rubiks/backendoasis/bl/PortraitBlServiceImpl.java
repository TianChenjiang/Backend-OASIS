package com.rubiks.backendoasis.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.blservice.PortraitBlService;
import com.rubiks.backendoasis.entity.paper.AuthorEntity;
import com.rubiks.backendoasis.entity.paper.PaperEntity;
import com.rubiks.backendoasis.model.paper.PublicationTrend;
import com.rubiks.backendoasis.model.portrait.AffiliationPortrait;
import com.rubiks.backendoasis.model.portrait.AuthorPortrait;
import com.rubiks.backendoasis.response.BasicResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.rubiks.backendoasis.util.Constant.LARGE_COLLECTION;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class PortraitBlServiceImpl implements PortraitBlService {
    private RestHighLevelClient client;
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    public PortraitBlServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public BasicResponse getAuthorPortraitById(String id) {
        Query query = new Query(Criteria.where("authors.id").is(id));
        query.fields().include("authors").include("metrics");
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        String name = "", affiliation = "";
        int count = 0, citation = 0;
        if (res.size() > 0) {
            PaperEntity paperEntity = res.get(0);
            for (AuthorEntity authorEntity : paperEntity.getAuthors()) {
                if (authorEntity.getId().equals(id)) {
                    name = authorEntity.getName();
                    affiliation = authorEntity.getAffiliation();
                    break;
                }
            }
            for (PaperEntity p : res) {
                citation += p.getMetrics().getCitationCountPaper();
            }

            int curYear = Calendar.getInstance().get(Calendar.YEAR);
            Aggregation aggregation1 = newAggregation(
                    project("publicationYear", "authors", "metrics"),
                    match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)),  //过去十年
                    match(Criteria.where("authors.id").is(id)),
                    sort(Sort.Direction.ASC, "publicationYear"),
                    group("publicationYear").count().as("num")
                            .sum("metrics.citationCountPaper").as("citation")
                            .addToSet("publicationYear").as("publicationYear")
            );
            AggregationResults<PublicationTrend> curRes = mongoTemplate.aggregate(aggregation1, LARGE_COLLECTION, PublicationTrend.class);
            List<Integer> citationTrend = new ArrayList<>();
            List<Integer> publicationTrends = new ArrayList<>();

            List<PublicationTrend> yearNumMap = curRes.getMappedResults();
            for (int i = curYear-9; i <= curYear; i++) {
                publicationTrends.add(PublicationTrend.getNumOfYear(yearNumMap, i)); //得到年份对应的num
                citationTrend.add(PublicationTrend.getCitationOfYear(yearNumMap, i));
            }
            return new BasicResponse(200, "Success", new AuthorPortrait(name, res.size(), citation, affiliation, citationTrend, publicationTrends));
        } else {
            // 查无此人异常
            return new BasicResponse(200, "no such author", null);
        }
    }

    @Override
    public BasicResponse getAffiliationPortrait(String affiliation) {
        Query query = new Query(Criteria.where("authors.affiliation").is(affiliation));
        query.fields().include("authors").include("metrics");
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        int count = 0, citation = 0, authorNum = 0;
        if (res.size() > 0) {
            count = res.size();
            Set<String> nonRepeatedAuthors = new HashSet<>();
            for (PaperEntity p : res) {
                citation += p.getMetrics().getCitationCountPaper();
                for (AuthorEntity author : p.getAuthors()) {
                    if (author.getAffiliation() != null && author.getAffiliation().equals(affiliation)) {
                        nonRepeatedAuthors.add(author.getName());
                    }
                }
            }
            authorNum = nonRepeatedAuthors.size();

            int curYear = Calendar.getInstance().get(Calendar.YEAR);
            Aggregation aggregation1 = newAggregation(
                    project("publicationYear", "authors", "metrics"),
                    match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)),  //过去十年
                    match(Criteria.where("authors.affiliation").is(affiliation)),
                    sort(Sort.Direction.ASC, "publicationYear"),
                    group("publicationYear").count().as("num")
                            .sum("metrics.citationCountPaper").as("citation")
                            .addToSet("publicationYear").as("publicationYear")
            );
            AggregationResults<PublicationTrend> curRes = mongoTemplate.aggregate(aggregation1, LARGE_COLLECTION, PublicationTrend.class);
            List<Integer> citationTrend = new ArrayList<>();
            List<Integer> publicationTrends = new ArrayList<>();

            List<PublicationTrend> yearNumMap = curRes.getMappedResults();
            for (int i = curYear-9; i <= curYear; i++) {
                publicationTrends.add(PublicationTrend.getNumOfYear(yearNumMap, i)); //得到年份对应的num
                citationTrend.add(PublicationTrend.getCitationOfYear(yearNumMap, i));
            }
            return new BasicResponse(200, "Success", new AffiliationPortrait(count, citation, authorNum, citationTrend, publicationTrends));
        } else {
            // 查无此人异常
            return new BasicResponse(200, "no such affiliation", null);
        }
    }

    @Override
    public BasicResponse getKeywordPortrait(String keyword) {
        Query query = new Query(Criteria.where("keywords").is(keyword));
        query.fields().include("metrics").include("authors");

        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        int count = 0, citation = 0, authorNum = 0;
        if (res.size() > 0) {
            count = res.size();
            Set<String> nonRepeatedAuthors = new HashSet<>();
            for (PaperEntity p : res) {
                citation += p.getMetrics().getCitationCountPaper();
                if (p.getAuthors() != null) {
                    for (AuthorEntity author : p.getAuthors()) {
                        if (author.getName() != null) {
                            nonRepeatedAuthors.add(author.getName());
                        }
                    }
                }
            }
            authorNum = nonRepeatedAuthors.size();

            int curYear = Calendar.getInstance().get(Calendar.YEAR);
            Aggregation aggregation1 = newAggregation(
                    match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)),  //过去十年
                    match(Criteria.where("keywords").is(keyword)),
                    sort(Sort.Direction.ASC, "publicationYear"),
                    group("publicationYear").count().as("num")
                            .sum("metrics.citationCountPaper").as("citation")
                            .addToSet("publicationYear").as("publicationYear")
            );
            AggregationResults<PublicationTrend> curRes = mongoTemplate.aggregate(aggregation1, LARGE_COLLECTION, PublicationTrend.class);
            List<Integer> citationTrend = new ArrayList<>();
            List<Integer> publicationTrends = new ArrayList<>();

            List<PublicationTrend> yearNumMap = curRes.getMappedResults();
            for (int i = curYear-9; i <= curYear; i++) {
                publicationTrends.add(PublicationTrend.getNumOfYear(yearNumMap, i)); //得到年份对应的num
                citationTrend.add(PublicationTrend.getCitationOfYear(yearNumMap, i));
            }
            return new BasicResponse(200, "Success", new AffiliationPortrait(count, citation, authorNum, citationTrend, publicationTrends));
        } else {
            // 查无此人异常
            return new BasicResponse(200, "no such keyword", null);
        }
    }

    @Override
    public BasicResponse getConferencePortrait(String conference) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("publicationName").is(conference),
                Criteria.where("contentType").is("conferences")
        );
        Query query = new Query(criteria);
        query.fields().include("authors").include("metrics");
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);

        int count = 0, citation = 0, authorNum = 0;
        if (res.size() > 0) {
            count = res.size();
            Set<String> nonRepeatedAuthors = new HashSet<>();
            for (PaperEntity p : res) {
                citation += p.getMetrics().getCitationCountPaper();
                if (p.getAuthors() != null) {
                    for (AuthorEntity author : p.getAuthors()) {
                        nonRepeatedAuthors.add(author.getName());
                    }
                }
            }
            authorNum = nonRepeatedAuthors.size();

            int curYear = Calendar.getInstance().get(Calendar.YEAR);
            Aggregation aggregation1 = newAggregation(
                    project("publicationYear", "publicationName", "contentType", "metrics"),
                    match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)),  //过去十年
                    match(Criteria.where("publicationName").is(conference)),
                    match(Criteria.where("contentType").is("conferences")),
                    sort(Sort.Direction.ASC, "publicationYear"),
                    group("publicationYear").count().as("num")
                            .sum("metrics.citationCountPaper").as("citation")
                            .addToSet("publicationYear").as("publicationYear")
            );
            AggregationResults<PublicationTrend> curRes = mongoTemplate.aggregate(aggregation1, LARGE_COLLECTION, PublicationTrend.class);
            List<Integer> citationTrend = new ArrayList<>();
            List<Integer> publicationTrends = new ArrayList<>();

            List<PublicationTrend> yearNumMap = curRes.getMappedResults();
            for (int i = curYear-9; i <= curYear; i++) {
                publicationTrends.add(PublicationTrend.getNumOfYear(yearNumMap, i)); //得到年份对应的num
                citationTrend.add(PublicationTrend.getCitationOfYear(yearNumMap, i));
            }
            return new BasicResponse(200, "Success", new AffiliationPortrait(count, citation, authorNum, citationTrend, publicationTrends));
        } else {
            // 查无此人异常
            return new BasicResponse(200, "no such conference", null);
        }
    }

    @Override
    public BasicResponse getJournalPortrait(String journal) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                Criteria.where("publicationName").is(journal),
                Criteria.where("contentType").is("periodicals")
        );
        Query query = new Query(criteria);
        query.fields().include("authors").include("metrics");
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);

        int count = 0, citation = 0, authorNum = 0;
        if (res.size() > 0) {
            count = res.size();
            Set<String> nonRepeatedAuthors = new HashSet<>();
            for (PaperEntity p : res) {
                citation += p.getMetrics().getCitationCountPaper();
                if (p.getAuthors() != null) {   //TODO 为什么authors可能为null？
                    for (AuthorEntity author : p.getAuthors()) {
                        nonRepeatedAuthors.add(author.getName());
                    }
                }
            }
            authorNum = nonRepeatedAuthors.size();

            int curYear = Calendar.getInstance().get(Calendar.YEAR);
            Aggregation aggregation1 = newAggregation(
                    project("publicationYear", "publicationName", "contentType", "metrics"),
                    match(Criteria.where("publicationYear").gte(curYear-9).lte(curYear)),  //过去十年
                    match(Criteria.where("publicationName").is(journal)),
                    match(Criteria.where("contentType").is("periodicals")),
                    sort(Sort.Direction.ASC, "publicationYear"),
                    group("publicationYear").count().as("num")
                            .sum("metrics.citationCountPaper").as("citation")
                            .addToSet("publicationYear").as("publicationYear")
            );
            AggregationResults<PublicationTrend> curRes = mongoTemplate.aggregate(aggregation1, LARGE_COLLECTION, PublicationTrend.class);
            List<Integer> citationTrend = new ArrayList<>();
            List<Integer> publicationTrends = new ArrayList<>();

            List<PublicationTrend> yearNumMap = curRes.getMappedResults();
            for (int i = curYear-9; i <= curYear; i++) {
                publicationTrends.add(PublicationTrend.getNumOfYear(yearNumMap, i)); //得到年份对应的num
                citationTrend.add(PublicationTrend.getCitationOfYear(yearNumMap, i));
            }
            return new BasicResponse(200, "Success", new AffiliationPortrait(count, citation, authorNum, citationTrend, publicationTrends));
        } else {
            // 查无此人异常
            return new BasicResponse(200, "no such journal", null);
        }
    }

}
