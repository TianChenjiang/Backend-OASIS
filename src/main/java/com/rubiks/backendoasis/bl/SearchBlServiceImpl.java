package com.rubiks.backendoasis.bl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rubiks.backendoasis.blservice.SearchBlService;
import com.rubiks.backendoasis.entity.AuthorEntity;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.Author;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.model.FilterCondition;
import com.rubiks.backendoasis.model.NameCount;
import com.rubiks.backendoasis.model.PaperWithoutRef;
import com.rubiks.backendoasis.model.PapersWithSize;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.springcontroller.SearchController;
import com.rubiks.backendoasis.util.Constant;
import com.rubiks.backendoasis.util.StrProcesser;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
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

    public BasicResponse basicSearchByES(String keyword, int page, String sortKey) throws Exception{
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, "authors.name", "authors.affiliation", "abstract", "title", "publicationTitle", "doi", "keywords", "publicationName");
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.trackTotalHits(true);

        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder = sortByKey(searchSourceBuilder, sortKey); //根据sortKey排序

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperDocToPaperWithoutRef(getSearchResult(searchResponse)), searchResponse.getHits().getTotalHits().value));
    }

    @Override
    public BasicResponse advancedSearchByES(String author, String affiliation, String publicationName, String keyword, int startYear, int endYear, int page, String sortKey) throws Exception {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        QueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!author.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("authors.name", author).operator(Operator.AND));
        if (!affiliation.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("authors.affiliation", affiliation).operator(Operator.AND));
        if (!publicationName.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("publicationName",publicationName).operator(Operator.AND));
        if (!keyword.isEmpty()) ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.matchQuery("keywords", keyword).operator(Operator.AND));
        ((BoolQueryBuilder) queryBuilder).must(QueryBuilders.rangeQuery("publicationYear").gte(startYear).lte(endYear));

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.trackTotalHits(true);

        searchSourceBuilder = sortByKey(searchSourceBuilder, sortKey); //根据sortKey排序
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperDocToPaperWithoutRef(getSearchResult(searchResponse)), searchResponse.getHits().getTotalHits().value));
    }

    @Override
    public BasicResponse basicSearch(String keyword, int page, String sortKey) {
        keyword = new StrProcesser().escapeExprSpecialWord(keyword);

        TextCriteria textCriteria = TextCriteria.forLanguage("en").matching(keyword);
        Query query1 = TextQuery.queryText(textCriteria);
        long size =  mongoTemplate.count(query1, PaperEntity.class);

        if (sortKey.equals("related")) {
            ((TextQuery) query1).sortByScore().with(PageRequest.of(page-1, pageSize));
        } else if (sortKey.equals("recent")) {
            query1.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "publicationYear")));
        }
        else if (sortKey.equals("early")) {
            query1.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.ASC, "publicationYear")));
        }
        else if (sortKey.equals("citation")) {
            query1.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "metrics.citationCountPaper")));
        }

        List<PaperEntity> res = mongoTemplate.find(query1, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
    }

    @Override
    public BasicResponse advancedSearch(String author, String affiliation, String publicationName, String keyword, int page, int startYear, int endYear) {
        Criteria criteria = new Criteria();

        StrProcesser strProcesser = new StrProcesser();
        author = strProcesser.escapeExprSpecialWord(author);
        affiliation = strProcesser.escapeExprSpecialWord(affiliation);
        publicationName = strProcesser.escapeExprSpecialWord(publicationName);
        keyword = strProcesser.escapeExprSpecialWord(keyword);

        criteria.andOperator(
                criteria.where("authors.name").regex(getPattern(author)),
                criteria.where("authors.affiliation").regex(getPattern(affiliation)),
                criteria.where("publicationName").regex(getPattern(publicationName)),
                criteria.where("keywords").regex(getPattern(keyword)),
                criteria.where("publicationYear").gte(startYear).lte(endYear)
        );
        Query query = new Query(criteria);
        long size = mongoTemplate.count(query, PaperEntity.class);

        query.with(PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "publicationYear")));
        List<PaperEntity> res = mongoTemplate.find(query, PaperEntity.class);
        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperToPaperWithoutRef(res), size));
    }

    @Override
    public BasicResponse getBasicSearchFilterCondition(String keyword) throws Exception{
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, "authors.name", "authors.affiliation", "abstract", "title", "publicationTitle", "doi", "keywords", "publicationName");
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<PaperDocument> res = getSearchResult(searchResponse);

        List<NameCount> authors = new ArrayList<>();
        List<NameCount> affiliations = new ArrayList<>();
        List<NameCount> conferences = new ArrayList<>();
        List<NameCount> journals = new ArrayList<>();


        for (PaperDocument p : res) {
           for (Author authorEntity : p.getAuthors()) {
               FilterCondition.addNameCount(authors, authorEntity.getName());
               FilterCondition.addNameCount(affiliations, authorEntity.getAffiliation());
           }
           if (p.getContentType().equals("conference")) {
               FilterCondition.addNameCount(conferences, p.getPublicationName());
           } else if (p.getContentType().equals("periodicals")) {
               FilterCondition.addNameCount(journals, p.getPublicationName());
           }
        }

        Collections.sort(authors);
        Collections.sort(affiliations);
        Collections.sort(conferences);
        Collections.sort(journals);

        return new BasicResponse(200, "Success", new FilterCondition(authors, affiliations, conferences, journals));
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
            PaperDocument paperDocument = objectMapper.convertValue(hit.getSourceAsMap(), PaperDocument.class);
            paperDocument.setId(hit.getId());
            paperDocument.set_abstract((String)(hit.getSourceAsMap().get("abstract"))); //很奇怪，这里的abstract会为null，必须这样设置一下
            paperDocuments.add(paperDocument);

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

        return null;
    }

    private SearchSourceBuilder sortByKey(SearchSourceBuilder searchSourceBuilder, String sortKey) {

        if (sortKey.equals("related")) {
            searchSourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC)); // 按算分排序
        } else if (sortKey.equals("recent")) {
            searchSourceBuilder.sort(new FieldSortBuilder("publicationYear").order(SortOrder.DESC));
        }
        else if (sortKey.equals("early")) {
            searchSourceBuilder.sort(new FieldSortBuilder("publicationYear").order(SortOrder.ASC));
        }
        else if (sortKey.equals("citation")) {
            searchSourceBuilder.sort(new FieldSortBuilder("metrics.citationCountPaper").order(SortOrder.DESC));
        }
        return searchSourceBuilder;
    }

    @Cacheable("count")
    public long getSearchResSize(String keyword) throws IOException {
        CountRequest countRequest = new CountRequest();  //获得结果集总数
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, "authors", "abstract", "title", "publicationTitle", "doi", "keywords", "publicationName");
        countRequest.query(queryBuilder);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        long count = countResponse.getCount();
        return count;
    }
}
