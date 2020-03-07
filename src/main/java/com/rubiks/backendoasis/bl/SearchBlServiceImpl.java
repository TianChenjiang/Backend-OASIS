package com.rubiks.backendoasis.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.blservice.SearchBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.model.PaperWithoutRef;
import com.rubiks.backendoasis.model.PapersWithSize;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.StrProcesser;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.rubiks.backendoasis.util.Constant.INDEX;
import static com.rubiks.backendoasis.util.Constant.pageSize;

@Service
public class SearchBlServiceImpl implements SearchBlService {
    private RestHighLevelClient client;
    private ObjectMapper objectMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    public SearchBlServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
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
        if (!author.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("authors.name", author).operator(Operator.AND));
        if (!affiliation.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("authors.affiliation", affiliation).operator(Operator.AND));
        if (!conferenceName.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("conferenceName",conferenceName).operator(Operator.AND));
        if (!keyword.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("keywords", keyword).operator(Operator.AND));
//                .must(QueryBuilders.matchQuery("authors.name", authors).operator(Operator.AND))
//                .must(QueryBuilders.matchQuery("authors.affiliation", affiliation).operator(Operator.AND))
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
    public BasicResponse basicSearch(String keyword, int page, String startYear, String endYear) {
        Criteria criteria = new Criteria();
        Query query = new Query();
        keyword = new StrProcesser().escapeExprSpecialWord(keyword);
        Pattern pattern = getPattern(keyword);
        criteria.orOperator(
                criteria.where("title").regex(pattern), // 默认不区分大小写
                criteria.where("abstract").regex(pattern),
                criteria.where("authors.name").regex(pattern),
                criteria.where("authors.affiliation").regex(pattern),
                criteria.where("publicationTitle").regex(pattern),
                criteria.where("conferenceName").regex(pattern),
                criteria.where("keywords").regex(pattern)
        );
        criteria.andOperator(
                criteria.where("publicationYear").gte(startYear).lte(endYear)
        );
        query.addCriteria(criteria);
        long size =  mongoTemplate.count(query, PaperEntity.class);

        query.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "publicationYear")));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
    }

    @Override
    public BasicResponse advancedSearch(String author, String affiliation, String conferenceName, String keyword, int page, String startYear, String endYear) {
        Criteria criteria = new Criteria();

        StrProcesser strProcesser = new StrProcesser();
        author = strProcesser.escapeExprSpecialWord(author);
        affiliation = strProcesser.escapeExprSpecialWord(affiliation);
        conferenceName = strProcesser.escapeExprSpecialWord(conferenceName);
        keyword = strProcesser.escapeExprSpecialWord(keyword);

        criteria.andOperator(
                criteria.where("authors.name").regex(getPattern(author)),
                criteria.where("authors.affiliation").regex(getPattern(affiliation)),
                criteria.where("conferenceName").regex(getPattern(conferenceName)),
                criteria.where("keywords").regex(getPattern(keyword)),
                criteria.where("publicationYear").gte(startYear).lte(endYear)
        );
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "publicationYear")));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
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

    public List<PaperDocument> findAll() throws Exception {
        SearchRequest searchRequest = buildSearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return getSearchResult(searchResponse);
    }
}
