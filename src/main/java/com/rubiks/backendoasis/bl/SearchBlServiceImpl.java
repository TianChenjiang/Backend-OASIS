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
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
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
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword)
                .field("authors.name", 2f)
                .field("authors.affiliation")
                .field("abstract")
                .field("title", 2f)
                .field("keywords")
                .field("publicationName");
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.trackTotalHits(true);

        searchSourceBuilder = sortByKey(searchSourceBuilder, sortKey); //根据sortKey排序
        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperDocToPaperWithoutRef(getSearchResult(searchResponse)), searchResponse.getHits().getTotalHits().value));
    }

    @Override
    public BasicResponse basicSearchByESWithHighLight(String keyword, int page, String sortKey) throws Exception {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword)
                .field("authors.name", 2f)
                .field("authors.affiliation")
                .field("title", 2f)
                .field("abstract")
                .field("keywords")
                .field("publicationName");
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.trackTotalHits(true);

        searchSourceBuilder = sortByKey(searchSourceBuilder, sortKey); //根据sortKey排序
        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);


        String preTag = "<em>";
        String postTag = "</em>";

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("abstract").field("title").field("authors.name");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags(preTag);
        highlightBuilder.postTags(postTag);
        searchSourceBuilder.highlighter(highlightBuilder);

        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperDocToPaperWithoutRef(getSearchResultWithHighLight(searchResponse)), searchResponse.getHits().getTotalHits().value));
    }

    @Override
    public BasicResponse advancedSearchByES(String field, String author, String affiliation, String publicationName, String keyword, int startYear, int endYear, int page, String sortKey) throws Exception {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (!author.isEmpty()) queryBuilder.must(QueryBuilders.matchPhraseQuery("authors.name", author));
        if (!affiliation.isEmpty()) queryBuilder.must(QueryBuilders.matchPhraseQuery("authors.affiliation", affiliation));
        if (!publicationName.isEmpty()) queryBuilder.must(QueryBuilders.matchPhraseQuery("publicationName", publicationName));
        if (!field.isEmpty()) queryBuilder.must(QueryBuilders.matchPhraseQuery("keywords", field));
        queryBuilder.must(QueryBuilders.rangeQuery("publicationYear").gte(startYear).lte(endYear));


        if (!keyword.isEmpty()) {
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("abstract", keyword))
                    .should(QueryBuilders.matchPhraseQuery("title", keyword));
            queryBuilder.must(keywordQuery);
        }

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder = sortByKey(searchSourceBuilder, sortKey); //根据sortKey排序

        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.trackTotalHits(true);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperDocToPaperWithoutRef(getSearchResult(searchResponse)), searchResponse.getHits().getTotalHits().value));
    }

    @Override
    public BasicResponse advancedSearchByESWithHighLight(String field, String author, String affiliation, String publicationName, String keyword, int startYear, int endYear, int page, String sortKey) throws Exception {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();


        if (!author.isEmpty()) queryBuilder.must(QueryBuilders.matchPhraseQuery("authors.name", author));
        if (!affiliation.isEmpty()) queryBuilder.must(QueryBuilders.matchPhraseQuery("authors.affiliation", affiliation));
        if (!publicationName.isEmpty()) queryBuilder.must(QueryBuilders.matchPhraseQuery("publicationName", publicationName));
        if (!field.isEmpty()) queryBuilder.must(QueryBuilders.matchPhraseQuery("keywords", field));
        queryBuilder.must(QueryBuilders.rangeQuery("publicationYear").gte(startYear).lte(endYear));


        if (!keyword.isEmpty()) {
            BoolQueryBuilder keywordQuery = QueryBuilders.boolQuery().should(QueryBuilders.matchPhraseQuery("abstract", keyword))
                    .should(QueryBuilders.matchPhraseQuery("title", keyword));
            queryBuilder.must(keywordQuery);
        }

        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder = sortByKey(searchSourceBuilder, sortKey); //根据sortKey排序
        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.trackTotalHits(true);

        String preTag = "<em>";
        String postTag = "</em>";

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        if (!author.isEmpty()) highlightBuilder.field("authors.name");
        if (!keyword.isEmpty()) highlightBuilder.field("abstract").field("title");

        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags(preTag);
        highlightBuilder.postTags(postTag);
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperDocToPaperWithoutRef(getSearchResultWithHighLight(searchResponse)), searchResponse.getHits().getTotalHits().value));
    }

    @Override
    public BasicResponse basicFilterSearch(String keyword, String author, String affiliation, String publicationName, int startYear, int endYear, int page, String sortKey) throws Exception {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();

        QueryBuilder basicSearchBuilder = QueryBuilders.multiMatchQuery(keyword)
                .field("authors.name", 2f)
                .field("authors.affiliation")
                .field("title", 2f)
                .field("abstract")
                .field("keywords")
                .field("publicationName");


        BoolQueryBuilder advancedSearchBuilder = new BoolQueryBuilder();

        if (!author.isEmpty()) advancedSearchBuilder.must(QueryBuilders.matchPhraseQuery("authors.name", author));
        if (!affiliation.isEmpty()) advancedSearchBuilder.must(QueryBuilders.matchPhraseQuery("authors.affiliation", affiliation));
        if (!publicationName.isEmpty()) advancedSearchBuilder.must(QueryBuilders.matchPhraseQuery("publicationName", publicationName));
        advancedSearchBuilder.must(QueryBuilders.rangeQuery("publicationYear").gte(startYear).lte(endYear));

        filterBuilder.must(basicSearchBuilder);
        filterBuilder.must(advancedSearchBuilder);

        searchSourceBuilder.query(filterBuilder);
        searchSourceBuilder = sortByKey(searchSourceBuilder, sortKey); //根据sortKey排序
        searchSourceBuilder.from(page-1);
        searchSourceBuilder.size(pageSize);
        searchSourceBuilder.trackTotalHits(true);

        String preTag = "<em>";
        String postTag = "</em>";

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        if (!author.isEmpty()) highlightBuilder.field("authors.name");
        if (!keyword.isEmpty()) highlightBuilder.field("abstract").field("title");

        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags(preTag);
        highlightBuilder.postTags(postTag);
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        return new BasicResponse(200, "Success", new PapersWithSize(PaperWithoutRef.PaperDocToPaperWithoutRef(getSearchResultWithHighLight(searchResponse)), searchResponse.getHits().getTotalHits().value));
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
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword)
                .field("authors.name", 2f)
                .field("authors.affiliation")
                .field("authors.affiliation")
                .field("title", 2f)
                .field("keywords")
                .field("publicationName");
        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<PaperDocument> res = getSearchResult(searchResponse);

        List<NameCount> authors = new ArrayList<>();
        List<NameCount> affiliations = new ArrayList<>();
        List<NameCount> conferences = new ArrayList<>();
        List<NameCount> journals = new ArrayList<>();


        for (PaperDocument p : res) {
            if (p.getAuthors() != null) {
                for (Author authorEntity : p.getAuthors()) {
                    FilterCondition.addNameCount(authors, authorEntity.getName());
                    FilterCondition.addNameCount(affiliations, authorEntity.getAffiliation());
                }
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

    private List<PaperDocument> getSearchResultWithHighLight(SearchResponse response) {
        List<PaperDocument> paperDocuments = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            PaperDocument paperDocument = objectMapper.convertValue(hit.getSourceAsMap(), PaperDocument.class);
            paperDocument.setId(hit.getId());
            paperDocument.set_abstract((String)(hit.getSourceAsMap().get("abstract"))); //很奇怪，这里的abstract会为null，必须这样设置一下

            HighlightField authorNameField = hit.getHighlightFields().get("authors.name");
            if (authorNameField != null) {
                List<Author> authors = paperDocument.getAuthors();
                int index = 0;
                for (int i = 0; i < authors.size(); i++) {
                    Author author = authors.get(i);
                    String curName = authorNameField.fragments()[index].toString();
                    if (curName.replaceAll("<[^>]*>","").equals(author.getName())) {
                        author.setName(curName);
                        index++;
                        if (index == authorNameField.fragments().length) break;
                    }
                }
            }
            HighlightField titleField = hit.getHighlightFields().get("title");
            if (titleField != null) {
                paperDocument.setTitle(titleField.fragments()[0].toString());
            }
            HighlightField abstractField = hit.getHighlightFields().get("abstract");
            if (abstractField != null) {
                paperDocument.set_abstract(abstractField.fragments()[0].toString());
            }
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
