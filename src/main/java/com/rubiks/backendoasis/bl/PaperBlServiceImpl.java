package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.util.AcceptanceCountRank;
import com.rubiks.backendoasis.util.BasicRank;
import com.rubiks.backendoasis.util.BasicRank.*;
import com.rubiks.backendoasis.util.CitationCountRank;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.rubiks.backendoasis.util.Constant.INDEX;
import static com.rubiks.backendoasis.util.Constant.pageSize;

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

    public List<PaperDocument> findAll() throws Exception {
        SearchRequest searchRequest = buildSearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    public List<PaperDocument> basicSearchByES(String keyword, int page) throws Exception{
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword));
        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC)); // 按算分排序
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    @Override
    public List<PaperDocument> advancedSearchByES(String author, String affiliation, String conferenceName, String keyword, int page) throws Exception {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!author.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("author.name", author).operator(Operator.AND));
        if (!affiliation.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("author.affiliation", affiliation).operator(Operator.AND));
        if (!conferenceName.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("conferenceName",conferenceName).operator(Operator.AND));
        if (!keyword.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("keywords", keyword).operator(Operator.AND));
//                .must(QueryBuilders.matchQuery("author.name", author).operator(Operator.AND))
//                .must(QueryBuilders.matchQuery("author.affiliation", affiliation).operator(Operator.AND))
//                .must(QueryBuilders.matchQuery("conferenceName",conferenceName).operator(Operator.AND))
//                .must(QueryBuilders.matchQuery("keywords", keyword).operator(Operator.AND));
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }

    @Override
    public List<PaperEntity> basicSearch(String keyword, int page) {
        Pattern pattern = getPattern(keyword);
        Criteria criteria = new Criteria();
        Query query = new Query();
        criteria.orOperator(
                criteria.where("title").regex(pattern),
                criteria.where("abstract").regex(pattern),
                criteria.where("author.name").regex(pattern),
                criteria.where("author..affiliation").regex(pattern),
                criteria.where("publicationTitle").regex(pattern),
                criteria.where("conferenceName").regex(pattern),
                criteria.where("keywords").regex(pattern)
        );
        query.addCriteria(criteria);
        query.with(PageRequest.of(page-1, pageSize));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return res;
    }

    @Override
    public List<PaperEntity> advancedSearch(String author, String affiliation, String conferenceName, String keyword, int page) {
        Criteria criteria = new Criteria();
        criteria.andOperator(
                criteria.where("author.name").regex(getPattern(author)),
                criteria.where("author.affiliation").regex(getPattern(affiliation)),
                criteria.where("conferenceName").regex(getPattern(conferenceName)),
                criteria.where("keywords").regex(getPattern(keyword))
        );
        Query query = new Query(criteria);
        query.with(PageRequest.of(page-1, pageSize));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return res;
    }

    @Override
    public <T> List<BasicRank> getBasicRanking(String topic, String sortkey, String year) {
        // "affiliation"|"author
        // "acceptanceCount"|"citationCount"
        String key = "";
        if (topic.equals("affiliation")) {
            key = "authors.affiliation";
        } else if (topic.equals("author")){
            key = "authors.name";
        }
        Aggregation aggregation = newAggregation(
                project("authors", "publicationYear", "metrics"),
                match(Criteria.where("authors.affiliation").ne("").ne(null)),  //非空属性
                match(Criteria.where("publicationYear").is(year)),
                group(key).sum("metrics.citationCountPaper").as("acceptanceCount").
                        sum("metrics.citationCountPatent").as("citationCount"),
                sort(Direction.DESC, sortkey)
        );

        if (sortkey.equals("acceptanceCount")) {
            AggregationResults<AcceptanceCountRank> res = mongoTemplate.aggregate(aggregation,"papers", AcceptanceCountRank.class);
            return BasicRank.transformToBasic(res.getMappedResults());

        } else if (sortkey.equals("citationCount")) {
            AggregationResults<CitationCountRank> res = mongoTemplate.aggregate(aggregation,"papers", CitationCountRank.class);
            return BasicRank.transformToBasic(res.getMappedResults());
        }
        return null;
    }

    // 建立基本请求的接口
    private SearchRequest buildSearchRequest(String index) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        return searchRequest;
    }

    private List<PaperDocument> getSearchResult(SearchResponse response) {
        SearchHit[] searchHit = response.getHits().getHits();
        List<PaperDocument> paperDocuments = new ArrayList<>();
        for (SearchHit hit : searchHit){
            paperDocuments
                    .add(objectMapper
                            .convertValue(hit
                                    .getSourceAsMap(), PaperDocument.class));
        }
        return paperDocuments;
    }

    private Pattern getPattern(String keyword) {
        Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
        return pattern;
    }
}
