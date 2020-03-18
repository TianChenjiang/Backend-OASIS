package com.rubiks.backendoasis.bl;

import com.rubiks.backendoasis.blservice.PaperBlService;
import com.rubiks.backendoasis.entity.PaperEntity;
import com.rubiks.backendoasis.esdocument.Author;
import com.rubiks.backendoasis.esdocument.PaperDocument;
import com.rubiks.backendoasis.exception.NoSuchYearException;
import com.rubiks.backendoasis.model.*;
import com.rubiks.backendoasis.response.BasicResponse;
import com.rubiks.backendoasis.util.Constant;
import com.rubiks.backendoasis.util.MapUtil;
import com.rubiks.backendoasis.util.StrProcesser;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import static com.rubiks.backendoasis.util.Constant.collectionName;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Basic;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
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


    @Override
    public BasicResponse getResearcherInterest(String id) {
        Aggregation aggregation = newAggregation(
//                unwind("authors"),
                match(Criteria.where("authors.id").is(id)),
                project( "keywords", "authors.id")
        );
        AggregationResults<PaperEntity> aggregationRes = mongoTemplate.aggregate(aggregation, collectionName, PaperEntity.class);
        List<PaperEntity> aggregationList = aggregationRes.getMappedResults();
        List<String> keywordList = new ArrayList<>();
        for (PaperEntity paperEntity : aggregationList) {
            if (paperEntity.getKeywords() != null) {    // keywords需要为非空
                List<String> curKeywordList = paperEntity.getKeywords();
                for (String curKeyword : curKeywordList) {
                    keywordList.add(curKeyword);
                }
            }
        }

        List<ResearchInterest> res = new ArrayList<>();
        for (String curKeyword: keywordList) {
            boolean keywordExist = false;
            for (int i = 0; i < res.size(); i++) {
                ResearchInterest cur = res.get(i);
                if (cur.getName().equals(curKeyword)) {
                    keywordExist = true;
                    cur.setValue(cur.getValue()+1);
                    break;
                }
            }
            if (!keywordExist) {
                res.add(new ResearchInterest(curKeyword, 1));
            }
        }
        return new BasicResponse<>(200, "Success", res);
    }


    @Override
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

    class AuthorKeywordsList {
        private String name;
        private List<String> keywords;
    }
}
